package com.crazyputting.player.astar;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.physicsengine.Physics;
import com.crazyputting.player.Player;

import java.util.*;

public class AStar implements Player {

    private Hole hole;
    private Ball ball;
    private Terrain terrain;
    private List<Node> openList;
    private List<Node> closedList;
    private List<Node> turningNodes;
    private Node endNode;
    private int index = 1;
    private int idCounter;
    private Vector3 prevShot;

    /* Map with positions of objects as keys, the values correspond to the radius.
    *  It is meant for objects such as trees and rocks. */
    private Map<Vector3, Float> circularObstacles;

    /* Map where the keys are lists which contain the beginning and end coordinate of the rectangular obstacle,
    *  the values correspond to the width and length of the obstacle. It is meant for objects such as walls. */
    private Map<Vector3, List<Float>> rectangularObstacles;

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public void shot_velocity(Terrain terrain) {
        this.ball = terrain.getBall();
        this.hole = terrain.getHole();
        this.terrain = terrain;

        //Get the path once
        if (turningNodes == null) {
        	circularObstacles = new HashMap<>();
        	rectangularObstacles = new HashMap<>();
        	
        	// Add the trees to the circularObstacles map
            for (Vector3 obstaclePos : terrain.getTreeCoordinates()) {
                circularObstacles.put(obstaclePos, Physics.TREE_RADIUS);
            }
            // Add the rocks to the circularObstacles map
            for (Vector3 obstaclePos : terrain.getRockCoordinates()) {
                circularObstacles.put(obstaclePos, Physics.ROCK_RADIUS);
            }
            addMazeWalls();
            
        	// Get the path found by A* search
        	ArrayList<Node> path = getPath();
        
        	// Construct a simplified path only containing the start and the end, as well as any turning node(s)
            assert path != null;
            createTurningNodes(path);
        }
        
        // Shoot the ball following the given path
        if (index < turningNodes.size()) {
        	Node node = turningNodes.get(index);
            if (!equals(ball.getPosition().cpy(), node.getPosition().cpy())) {
                Vector3 velocity = node.getPosition().cpy().sub(ball.getPosition().cpy());

                if (!terrain.getMazeEnabled()) {
                    //Scale the velocity based on the current friction.
                    float frictionCoefficient = estimateFriction();
                    /* Here the formula for the stopping distance is used: s = v^2 / (2 * Mu * g). Rewriting gives:
                     * v = sqrt(a * Mu) where a = s * 2 * g. Since a is a constant, and the variable is Mu in this case,
                     * v depends on Mu. So the velocity needs to be scaled by: v2 / v1 = sqrt(Mu2) / sqrt(Mu1) = sqrt(Mu2 / Mu1). */
                    velocity.scl((float) Math.sqrt(frictionCoefficient / Physics.GRASS_FRICTION_COEFFICIENT));
                }

                scaleVelocity(velocity);

                //used to estimate the friction
                prevShot = velocity.cpy();
                ball.hit(velocity);
            } else {
                index++;
            }
        }
    }

    // Perform A* search and return the shortest path
    private ArrayList<Node> getPath() {
    	idCounter = 0;
        Vector3 holePos = hole.getPosition().cpy();
        Vector3 startPos = terrain.getStartPos().cpy();

        Node startNode = new Node(idCounter, startPos);
        idCounter++;
        endNode = new Node(idCounter, holePos);
        idCounter++;

        openList = new ArrayList<>();
        closedList = new ArrayList<>();
        
        openList.add(startNode);

        while (openList.size() > 0) {
            Node currentNode = openList.get(0);

            for (Node node : openList) {
                if (node.getTotalCost() < currentNode.getTotalCost()) {
                    currentNode = node;
                }
            }

            openList.remove(currentNode);
            closedList.add(currentNode);

            // Found a path
            if (currentNode.equals(endNode)) {
                return makePath(currentNode);
            }

            List<Node> children = createChildren(currentNode);

            // Add correct children to the open list
            addToOpenList(children, currentNode);
        }
        return null;
    }

    /**
     * Correctly makes the path, which the algorithm has found
     * @param node initially the end node
     * @return the path from the end to the beginning reversed
     */
    private ArrayList<Node> makePath(Node node){
        ArrayList<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Creates a list of children by going in 8 directions around the considered node (the 4 straight and diagonal directions)
     */
    private List<Node> createChildren(Node node){
        List<Node> children = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Vector3 newPosition = node.getPosition().cpy();
            switch (i) {
                case 0:
                    newPosition.x++;
                    break;
                case 1:
                    newPosition.y++;
                    break;
                case 2:
                    newPosition.x--;
                    break;
                case 3:
                    newPosition.y--;
                    break;
                case 4:
                    newPosition.x++;
                    newPosition.y++;
                    break;
                case 5:
                    newPosition.x++;
                    newPosition.y--;
                    break;
                case 6:
                    newPosition.x--;
                    newPosition.y--;
                    break;
                case 7:
                    newPosition.x--;
                    newPosition.y++;
            }

            if (outOfBounds(newPosition) || !walkable(newPosition)) {
                continue;
            }

            Node child = new Node(idCounter, newPosition, node);
            child.setOrientation(i);
            idCounter++;

            if (listHasSimilarElement(openList, child) && listHasSimilarElement(closedList, child)) {
                children.add(child);
            }
        }
        return children;
    }

    /**
     * Goes over the list of children which was just created and add it to the open list if applicable
     */
    private void addToOpenList(List<Node> children, Node currentNode){
        for (Node child : children) {
            // Create the cost and the heuristic values
            child.setCost(currentNode.getCost() + 1);
            child.setHeuristic(endNode.getPosition().cpy().sub(child.getPosition()).len());

            boolean skip = false;
            for (Node openNode : openList) {
                if (child.equals(openNode) && child.getCost() > openNode.getCost()){
                    skip = true;
                    break;
                }
            }
            if (skip) {
                continue;
            }

            openList.add(child);
        }
    }

    /**
     * Goes over the path and finds the nodes where the orientation changes.
     */
    private void createTurningNodes(List<Node> path){
        turningNodes = new ArrayList<>();
        assert path != null;
        turningNodes.add(path.get(0));
        for (int i = 1; i < path.size(); i++) {
            Node node = path.get(i);
            if (i == path.size() - 1){
                turningNodes.add(node);
            } else if (i != 1 && node.getOrientation() != node.getParent().getOrientation()){
                turningNodes.add(node.getParent());
            }
        }
    }

    /**
     * Adds the maze walls to the rectangularObstacles list
     */
    private void addMazeWalls(){
        for (Vector3 obstaclePosCenter : terrain.getMazeWallCoordinates()) {
            List<Float> wallInfo = new ArrayList<>();
            wallInfo.add(Physics.WALL_LENGTH);
            wallInfo.add(Physics.WALL_WIDTH);
            rectangularObstacles.put(obstaclePosCenter, wallInfo);
        }
    }

    private boolean outOfBounds(Vector3 vector3) {
        return vector3.x < 0 || vector3.y < 0 || vector3.x > terrain.getWidth() || vector3.y > terrain.getHeight();
    }

    /**
     * Checks if the ball can be on the given point.
     * I.e. whether the ball would not be colliding with obstacles or in the water.
     * For rectangular objects it only works for straight rectangles. So rotated in 0 or 90 degrees.
     * Also, it is not checked whether the ball passes through an object for circular obstacles.
     */
    private boolean walkable(Vector3 pos){
        for (Map.Entry<Vector3, Float> circularObstacle : circularObstacles.entrySet()){
            float radiiSum = circularObstacle.getValue() + Ball.DIAMETER / 2f;
            // Check if the given position is in an obstacle
            if (pos.dst(circularObstacle.getKey()) <= radiiSum){
                return false;
            }
        }

        for (Map.Entry<Vector3, List<Float>> rectangularObstacle : rectangularObstacles.entrySet()){
            Vector3 rectangleCenter = rectangularObstacle.getKey().cpy();
            float length = rectangularObstacle.getValue().get(0);
            float width = rectangularObstacle.getValue().get(1);

            // Check if the given position is inside of the rectangular object
            if (rectangleCenter.x - width/2f <= pos.x && pos.x <= rectangleCenter.x + width/2f
                    && rectangleCenter.y - length/2f <= pos.y && pos.y <= rectangleCenter.y + length/2f){
                return false;
            }
        }
        //checks whether the position is in the water
        return (terrain.getFunction().evaluateHeight(pos.x, pos.y) >= 0);
    }

    private boolean listHasSimilarElement(List<Node> nodeList, Node node){
        for (Node nodeInList: nodeList) {
            if (equals(nodeInList.getPosition(), node.getPosition())){
                return false;
            }
        }
        return true;
    }

    /**
     * Tests whether the two points are close
     */
    private boolean equals(Vector3 a, Vector3 b) {
        float THRESHOLD = 0.3f;
        return (Math.abs(a.x - b.x) < THRESHOLD) && (Math.abs(a.y - b.y) < THRESHOLD);
    }

    /**
     * Here the formula for the stopping distance is used: s = v^2 / (2 * Mu * g). Rewriting gives:
     * v = sqrt(a * Mu) where a = s * 2 * g. Since a is a constant, and the variable is Mu in this case,
     * v depends on Mu. So the velocity needs to be scaled by: v2 / v1 = sqrt(Mu2) / sqrt(Mu1) = sqrt(Mu2 / Mu1).
     */
    private float estimateFriction(){
        float friction = Physics.GRASS_FRICTION_COEFFICIENT;
        if (prevShot != null) {
            float velocity = prevShot.len();
            float dst = ball.getHitPosition().dst(ball.getPosition());
            float SCALING_FACTOR = 4.155436f;
            friction = (velocity * velocity) / (2 * dst * Physics.GRAVITY) * SCALING_FACTOR;
        }
        return Math.min(friction, Physics.SAND_FRICTION_COEFFICIENT);
    }

    /**
     * Scale the velocity in different ways depending on the distance to the next coordinate
     */
    private void scaleVelocity(Vector3 velocity){
        int THRESHOLD1 = 6;
        int THRESHOLD2 = 15;
        float subX = velocity.x;
        float subY = velocity.y;
        if (subX < THRESHOLD1 && subY < THRESHOLD1 && subX > -THRESHOLD1 && subY > -THRESHOLD1) {
            velocity.scl(1.07f);
        } else if (subX < THRESHOLD2 && subY < THRESHOLD2 && subX > -THRESHOLD2 && subY > -THRESHOLD2) {
            velocity.scl(0.59f);
        } else {
            velocity.scl(0.325f);
        }
    }


    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
