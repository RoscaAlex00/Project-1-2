package com.crazyputting.player.astar;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

public class AStar implements Player {

	private Hole hole;
    private Ball ball;
    private Terrain terrain;
    private Vector3 velocity;
    private float maximumVelocity;

    public AStar(float maximumVelocity){
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
        for (Vector3 vector3 : path){
            shot.add(vector3);
        }

        return shot;
    }

    private List<Vector3> getPath(){
        Vector3 holePos = hole.getPosition().cpy();
        Vector3 startPos = terrain.getStartPos();
        Vector3 startToHoleDistance = holePos.sub(startPos);
        startToHoleDistance.z = 0;

        Node startNode = new Node("", startPos, startToHoleDistance, new Vector3(0,0,0));
        Node endNode = new Node("", holePos, new Vector3(0, 0, 0));

        List<Node> openList = new ArrayList<>();
        List<Node> closedList = new ArrayList<>();
        openList.add(startNode);

        while (openList.size() > 0) {
            boolean skip = false;
            Node currentNode = openList.get(0);
            int currentIndex = 0;

            for (int i = 0; i < openList.size(); i++) {
                if (openList.get(i).getTotalNodeValue().len() < currentNode.getTotalNodeValue().len()) {
                    currentNode = openList.get(i);
                    currentIndex = i;
                }
            }

            openList.remove(currentIndex);
            closedList.add(currentNode);

            if (currentNode.equals(endNode)) {
                List<Vector3> path = new ArrayList<>();
                Node current = currentNode;
                while (current != null) {
                    path.add(current.getPosition());
                    current = current.getParent();
                }
                Collections.reverse(path);
                return path;
            }

            List<Node> children = new ArrayList<>();
            for (int i = 0; i < 4; i++){
                Vector3 newNodeVector = new Vector3(currentNode.getPosition().cpy());
                switch (i){
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
                if (outOfBounds(newNodeVector)){
                    skip = true;
                    break;
                }
                children.add(new Node("", newNodeVector, currentNode));
            }
            if (skip) continue;

            for (Node child : children) {
                for (Node closedNode : closedList) {
                    if (child.equals(closedNode)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) continue;
                child.getAccumulatedValues().add(child.getPosition().cpy().sub(currentNode.getPosition()));
                child.setHeuristicValue(endNode.getPosition().cpy().sub(child.getPosition()));

                for (Node openNode : openList){
                    if (child.equals(openNode) && child.getAccumulatedValues().len() > openNode.getAccumulatedValues().len()){
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

    private boolean outOfBounds(Vector3 vector3){
        if (vector3.x < 0 || vector3.y < 0 || vector3.x > terrain.getWidth() || vector3.y > terrain.getHeight()){
            return true;
        }
        else {
            return false;
        }
    }

	@Override
    public void runLoop() {
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
