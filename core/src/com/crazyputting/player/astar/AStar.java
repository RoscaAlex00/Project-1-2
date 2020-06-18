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
    private int index = 1;

    /* Map with positions of objects as keys, the values correspond to the radius.
    *  It is meant for objects such as trees and rocks. */
    private Map<Vector3, Float> circularObstacles = new HashMap<>();

    /* Map where the keys are lists which contain the beginning and end coordinate of the rectangular obstacle,
    *  the values correspond to the width of the obstacle. It is meant for objects such as walls. */
    private Map<List<Vector3>, Float> rectangularObstacles = new HashMap<>();

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) {
        this.ball = terrain.getBall();
        this.hole = terrain.getHole();
        this.terrain = terrain;

        //Adding the trees to the circularObstacles map
        for (Vector3 obstaclePos : terrain.getTreeCoordinates()) {
            circularObstacles.put(obstaclePos, Physics.TREE_RADIUS);
        }
        //Adding the rocks to the circularObstacles map
        for (Vector3 obstaclePos : terrain.getRockCoordinates()) {
            circularObstacles.put(obstaclePos, Physics.ROCK_RADIUS);
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
        System.out.println(path);
        System.out.println("Hole position: " + hole.getPosition());
        System.out.println(turningNodes);
        
        // Shoot the ball following the given path
        if (index < turningNodes.size()) {
        	Node node = turningNodes.get(index);
        	if (!equals(ball.getPosition().x, node.getPosition().x) || !equals(ball.getPosition().y, node.getPosition().y)) {
        		int THRESHOLD_X = 6;
        		int THRESHOLD_Y = 6;
        		Vector3 velocity = node.getPosition().cpy().sub(ball.getPosition().cpy());
        		float subX = velocity.x;
                float subY = velocity.y;

                //Scale the velocity in different ways depending on the conditions
                if (subX < THRESHOLD_X && subY < THRESHOLD_Y && subX > -THRESHOLD_X && subY > -THRESHOLD_Y) {
                    //System.out.println("threshold: " + subX + " y: " + subY);
                    velocity.scl(1.07f);
                    System.out.println("1");
                } else if (subX < 15f && subY < 15f && subX > -15f && subY > -15f) {
                    // System.out.println("regular: " + subX + " y: " + subY);
                    velocity.scl(0.59f);
                    System.out.println("2");
                } else {
                    //System.out.println("regula22: " + subX + " y: " + subY);
                    velocity.scl(0.325f);
                    System.out.println("3");
                }
                ball.hit(velocity);
        		System.out.println("Ball hit!");
        	} else {
        		index++;
        	}
        }
        
        return null;
    }

    // Perform A* search and return the shortest path
    private ArrayList<Node> getPath() {
    	int idCounter = 0;
        Vector3 holePos = hole.getPosition().cpy();
        Vector3 startPos = terrain.getStartPos().cpy();

        Node startNode = new Node(idCounter, startPos);
        idCounter++;
        Node endNode = new Node(idCounter, holePos);
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
                ArrayList<Node> path = new ArrayList<>();
                while (currentNode != null) {
                    path.add(currentNode);
                    currentNode = currentNode.getParent();
                }
                Collections.reverse(path);
                return path;
            }

            // Add new children
            List<Node> children = new ArrayList<>();
            for (int i = 0; i < 8; i++) {
                Vector3 newPosition = currentNode.getPosition().cpy();
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
                
                //TODO: Make sure the terrain is walkable (how to recognize obstacles?)
                
                Node child = new Node(idCounter, newPosition, currentNode);
                child.setOrientation(i);
                idCounter++;
                children.add(child);
            }

            // Add correct children to the open list
            for (Node child : children) {
                boolean skip = false;
            	for (Node closedChild : closedList) {
                    if (child.equals(closedChild)) {
                        skip = true;
                        break;
                    }
            	}
            	if (skip) continue;

            	// Create the cost and the heuristic values
            	child.setCost(currentNode.getCost() + 1);
            	child.setHeuristic(endNode.getPosition().cpy().sub(child.getPosition()).len());
            	
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

        return null;
    }

    private boolean outOfBounds(Vector3 vector3) {
        return vector3.x < 0 || vector3.y < 0 || vector3.x > terrain.getWidth() || vector3.y > terrain.getHeight();
    }

    /**
     * Checks if the ball can be on the given point.
     * I.e. whether the ball would not be colliding with obstacles or in the water.
     * For rectangular objects it only works for straight rectangles. So rotated in 0 or 90 degrees.
     * Also, it is not checked whether the ball passes through an object
     */
    private boolean walkable(Vector3 pos){
        for (Map.Entry<Vector3, Float> circularObstacle : circularObstacles.entrySet()){
            float radiiSum = circularObstacle.getValue() + Ball.DIAMETER /2f;

            //checks if the given position is in an obstacle
            if (pos.dst(circularObstacle.getKey()) <= radiiSum){
                return false;
            }
        }

        for (Map.Entry<List<Vector3>, Float> rectangularObstacle : rectangularObstacles.entrySet()){
            Vector3 firstPos = rectangularObstacle.getKey().get(0);
            Vector3 secondPos = rectangularObstacle.getKey().get(1);
            float width = rectangularObstacle.getValue();
            float radiiSum = width + Ball.DIAMETER/2f;
            boolean rotated90 = (firstPos.x == secondPos.x);

            //checks if the given position is inside of the rectangular object.
            if (rotated90){
                float xDst = Math.abs(pos.x - firstPos.x);
                if (xDst >= radiiSum && firstPos.y <= pos.y && pos.y <= secondPos.y){
                    return false;
                }
            }
            else {
                float yDst = Math.abs(pos.y - firstPos.y);
                if (yDst >= radiiSum && firstPos.x <= pos.x && pos.x <= secondPos.x){
                    return false;
                }
            }

        }

        return !(ball.getPosition().z < 0);
    }

    private boolean equals(float a, float b) {
        return Math.abs(a - b) < 0.3f;
    }

    @Override
    public void runLoop() {
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
