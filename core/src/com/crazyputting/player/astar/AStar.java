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
    private Node endNode;
    private int index = 1;
    private int idCounter;

    private final boolean PRINT_PATH = true;
    private final boolean PRINT_SHOT = true;

    /* Map with positions of objects as keys, the values correspond to the radius.
    *  It is meant for objects such as trees and rocks. */
    private Map<Vector3, Float> circularObstacles = new HashMap<>();

    /* Map where the keys are lists which contain the beginning and end coordinate of the rectangular obstacle,
    *  the values correspond to the width and length of the obstacle. It is meant for objects such as walls. */
    private Map<Vector3, List<Float>> rectangularObstacles = new HashMap<>();

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) {
        //TODO: fix error with sand, where ball goes out of bounds, happens when ball's center starting position is in sand
        this.ball = terrain.getBall();
        this.hole = terrain.getHole();
        this.terrain = terrain;

        if (!(circularObstacles.size() > 0)) {
            circularObstacles = new HashMap<>();
            //Adding the trees to the circularObstacles map
            for (Vector3 obstaclePos : terrain.getTreeCoordinates()) {
                circularObstacles.put(obstaclePos, Physics.TREE_RADIUS);
            }

            //Adding the rocks to the circularObstacles map
            for (Vector3 obstaclePos : terrain.getRockCoordinates()) {
                circularObstacles.put(obstaclePos, Physics.ROCK_RADIUS);
            }
        }

        if (!(rectangularObstacles.size() > 0)) {
            rectangularObstacles = new HashMap<>();
            //Adding the maze walls
            for (Vector3 obstaclePosCenter : terrain.getMazeWallCoordinates()) {
                List<Float> wallInfo = new ArrayList<>();
                wallInfo.add(Physics.WALL_LENGTH);
                wallInfo.add(Physics.WALL_WIDTH);
                rectangularObstacles.put(obstaclePosCenter, wallInfo);
            }
        }

        // Get the path found by A*
        ArrayList<Node> path = getPath();
        
        // Construct a simplified path only containing the start and the end, as well as any turning node(s)
        ArrayList<Node> turningNodes = new ArrayList<>();
        assert path != null;
        turningNodes.add(path.get(0));
        for (int i = 1; i < path.size(); i++) {
        	Node node = path.get(i);
        	if (i == path.size() - 1)
        		turningNodes.add(node);
        	else if (i != 1 && node.getOrientation() != node.getParent().getOrientation())
        		turningNodes.add(node.getParent());
        }
        
        // Use for testing
        if (PRINT_PATH) {
            System.out.println(path);
            System.out.println("Hole position: " + hole.getPosition());
            System.out.println(turningNodes);
        }
        
        // Shoot the ball following the given path
        if (index < turningNodes.size()) {
        	Node node = turningNodes.get(index);
        	if (!equals(ball.getPosition().cpy(), node.getPosition().cpy())) {
        		int THRESHOLD_X = 6;
        		int THRESHOLD_Y = 6;
        		Vector3 velocity = node.getPosition().cpy().sub(ball.getPosition().cpy());
        		float subX = velocity.x;
                float subY = velocity.y;

                //Scale the velocity in different ways depending on the conditions
                int shotType;
                if (subX < THRESHOLD_X && subY < THRESHOLD_Y && subX > -THRESHOLD_X && subY > -THRESHOLD_Y) {
                    //System.out.println("threshold: " + subX + " y: " + subY);
                    velocity.scl(1.07f);
                    shotType = 1;
                } else if (subX < 15f && subY < 15f && subX > -15f && subY > -15f) {
                    // System.out.println("regular: " + subX + " y: " + subY);
                    velocity.scl(0.59f);
                    shotType = 2;
                } else {
                    //System.out.println("regula22: " + subX + " y: " + subY);
                    velocity.scl(0.325f);
                    shotType = 3;
                }
                ball.hit(velocity);

                //print shot info
                if (PRINT_SHOT) {
                    System.out.println("Shottype: " + shotType);
                    System.out.println("Ball hit! " + "velocity: " + velocity);
                }
        	} else {
        		index++;
        	}
        }
        
        return null;
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
            System.out.println(openList.size());
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

    private ArrayList<Node> makePath(Node node){
        ArrayList<Node> path = new ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.getParent();
        }
        Collections.reverse(path);
        return path;
    }

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

            if (!listContains(openList, child) && !listContains(closedList, child)) {
                children.add(child);
            }
        }
        return children;
    }

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
            if (skip) continue;

            openList.add(child);
        }
    }

    private boolean outOfBounds(Vector3 vector3) {
        return vector3.x < 0 || vector3.y < 0 || vector3.x > terrain.getWidth() || vector3.y > terrain.getHeight();
    }

    /**
     * Checks if the ball can be on the given point.
     * I.e. whether the ball would not be colliding with obstacles or in the water.
     * For rectangular objects it only works for straight rectangles. So rotated in 0 or 90 degrees.
     * Also, it is not checked whether the ball passes through an object for circular obstacles
     */
    private boolean walkable(Vector3 pos){
        for (Map.Entry<Vector3, Float> circularObstacle : circularObstacles.entrySet()){
            float radiiSum = circularObstacle.getValue() + Ball.DIAMETER /2f;

            //checks if the given position is in an obstacle
            if (pos.dst(circularObstacle.getKey()) <= radiiSum){
                return false;
            }
        }

        for (Map.Entry<Vector3, List<Float>> rectangularObstacle : rectangularObstacles.entrySet()){
            Vector3 rectangleCenter = rectangularObstacle.getKey().cpy();
            float length = rectangularObstacle.getValue().get(0);
            float width = rectangularObstacle.getValue().get(1);

            //checks if the given position is inside of the rectangular object.
            if (rectangleCenter.x - width/2f <= pos.x && pos.x <= rectangleCenter.x + width/2f
                    && rectangleCenter.y - length/2f <= pos.y && pos.y <= rectangleCenter.y + length/2f){
                return false;
            }
        }

        return !(terrain.getFunction().evaluateHeight(pos.x, pos.y) < 0);
    }

    private boolean listContains(List<Node> nodeList, Node node){
        for (Node nodeInList: nodeList) {
            if (equals(nodeInList.getPosition(), node.getPosition())){
                return true;
            }
        }
        return false;
    }

    private boolean equals(Vector3 a, Vector3 b) {
        float THRESHOLD = 0.3f;
        return (Math.abs(a.x - b.x) < THRESHOLD) && (Math.abs(a.y - b.y) < THRESHOLD);
    }

    @Override
    public void runLoop() {
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
