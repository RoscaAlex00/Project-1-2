package com.crazyputting.player.astar;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Player;

import java.util.*;

public class AStar implements Player {

    private Hole hole;
    private Ball ball;
    private Terrain terrain;
    private List<Node> openList;
    private List<Node> closedList;

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) {
        this.ball = terrain.getBall();
        this.hole = terrain.getHole();
        this.terrain = terrain;
        
        ArrayList<Node> path = getPath();
        
        ArrayList<Node> turningNodes = new ArrayList<>();
        turningNodes.add(path.get(0));      
        for (int i = 2; i < path.size(); i++) {
        	Node node = path.get(i);
        	if (i == path.size() - 1)
        		turningNodes.add(node);
        	else if (node.getOrientation() != node.getParent().getOrientation())
        		turningNodes.add(node.getParent());
        }
        
        // Used for testing
        System.out.println(path);
        System.out.println("Hole position: " + hole.getPosition());
        System.out.println(turningNodes);
        System.exit(0);
        
        //TODO: Shoot the ball following the given path
        /*
        Vector3 shot = new Vector3();
        if (path == null) return null;
        for (Vector3 vector3 : path) {
            shot.add(vector3);
        }
        ball.hit(shot);
        */     
        
        return null;
    }

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
            int currentIndex = 0;

            for (int i = 0; i < openList.size(); i++) {
            	Node node = openList.get(i);         	
                if (node.getTotalCost() < currentNode.getTotalCost()) {
                    currentNode = node;
                    currentIndex = i; 
                }
            }

            openList.remove(currentIndex);
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
                
                if (outOfBounds(newPosition)) {
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
            	for (Node closedChild : closedList) {
            		if (child == closedChild)
            			continue;
            	}
            	
            	// Create the cost and the heuristic values
            	child.setCost(currentNode.getCost() + 1);
            	child.setHeuristic(endNode.getPosition().cpy().sub(child.getPosition()).len());
            	
            	for (Node openNode : openList) {
            		if (child == openNode && child.getCost() > openNode.getCost())
            			continue;
            	}
            	
                openList.add(child);
            }
        }

        return null;
    }

    private boolean outOfBounds(Vector3 vector3) {
        return vector3.x < 0 || vector3.y < 0 || vector3.x > terrain.getWidth() || vector3.y > terrain.getHeight();
    }

    @Override
    public void runLoop() {
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
