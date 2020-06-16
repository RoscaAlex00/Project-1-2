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
    private Vector3 velocity;
    private float maximumVelocity;

    private List<Node> openList;
    private List<Node> closedList;

    public AStar(float maximumVelocity) {
        this.maximumVelocity = maximumVelocity;
    }

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) {
        this.ball = terrain.getBall();
        this.hole = terrain.getHole();

        //TODO: Remove for test, otherwise Nullpointer
        //this.terrain = terrain;

        List<Vector3> path = getPath();
        Vector3 shot = new Vector3();
        if (path == null) return null;
        for (Vector3 vector3 : path) {
            shot.add(vector3);
        }
        ball.hit(shot);

        return null;
    }

    private List<Vector3> getPath() {
        Vector3 holePos = hole.getPosition().cpy();
        Vector3 startPos = ball.getPosition().cpy();
        Vector3 startToHoleDistance = holePos.sub(startPos);
        startToHoleDistance.z = 0;

        Node startNode = new Node("", startPos, startToHoleDistance, new Vector3(0, 0, 0));
        Node endNode = new Node("", holePos, new Vector3(0, 0, 0));

        openList = new ArrayList<>();
        closedList = new ArrayList<>();
        openList.add(startNode);

        //First while loop has a first odd shot
        //while (openList.size() > 0) {
        while (openList.get(0) != null){
            System.out.println("List size: " + openList.size());
            Node currentNode = openList.get(0);

            for (Node node : openList) {
                if (node.getTotalNodeValue().len() < currentNode.getTotalNodeValue().len()) {
                    currentNode = node;
                    System.out.println("mee");
                }
            }

            openList.remove(currentNode);
            closedList.add(currentNode);
            System.out.println("mees");

            //found a path
            if (currentNode.equals(endNode)) {
                List<Vector3> path = new ArrayList<>();
                Node current = currentNode;
                while (current != null) {
                    path.add(current.getPosition().cpy());
                    current = current.getParent();
                    System.out.println("meeee");
                }
                Collections.reverse(path);
                return path;
            }

            //add new children
            List<Node> children = new LinkedList<>();
            for (int i = 0; i < 4; i++) {
                Vector3 newNodeVector = new Vector3(currentNode.getPosition().cpy());
                switch (i) {
                    case 0:
                        newNodeVector.x++;
                        break;
                    case 1:
                        newNodeVector.y++;
                        break;
                    case 2:
                        newNodeVector.x--;
                        break;
                    case 3:
                        newNodeVector.y--;
                        break;
                }
                if (outOfBounds(newNodeVector) || positionIsVisited(newNodeVector)) {
                    continue;
                }
                children.add(new Node("", newNodeVector, currentNode));
            }

            //add correct children to the open list
            for (Node child : children) {
                child.getAccumulatedValues().add(child.getPosition().cpy().sub(currentNode.getPosition().cpy()));
                child.setHeuristicValue(endNode.getPosition().cpy().sub(child.getPosition().cpy()));
                openList.add(child);
            }
        }

        return null;
    }

    private boolean outOfBounds(Vector3 vector3) {
        return vector3.x < 0 || vector3.y < 0 || vector3.x > terrain.getWidth() || vector3.y > terrain.getHeight();
    }

    private boolean positionIsVisited(Vector3 nodeVector){
        List<Node> visitedList = new LinkedList<>();
        visitedList.addAll(openList);
        visitedList.addAll(closedList);
        for (Node visitedNode : visitedList) {
            if (visitedNode.getPosition().x == nodeVector.x && visitedNode.getPosition().y == nodeVector.y){
                return false;
            }
        }
        return true;
    }

    @Override
    public void runLoop() {
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
