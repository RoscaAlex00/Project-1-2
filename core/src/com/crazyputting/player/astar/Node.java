package com.crazyputting.player.astar;

import com.badlogic.gdx.math.Vector3;

/**
 * Node for a graph (A* path finding)
 */
public class Node {
	
	private final int id;
    private final Vector3 position;
    
    private float cost;
    private float heuristic;
    private Node parent;
    private int orientation;

    public Node(int id, Vector3 position) {
    	this.id = id;
        this.position = position;
    }

    public Node(int id, Vector3 position, Node parent) {
    	this.id = id;
        this.position = position;
        this.parent = parent;
    }
    
    @Override
    public String toString() {
        StringBuilder nodeToString = new StringBuilder("Node ");
        nodeToString.append(id);
        if (parent != null)
        	nodeToString.append(", Parent: ").append(parent.getId());
        nodeToString.append(", Position: ").append(position).append("\n");
        return nodeToString.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return this.position.x == node.position.x &&
                this.position.y == node.position.y;
    }

    public int getId() {
    	return id;
    }
    
    public Vector3 getPosition() {
        return position;
    }
    
    public float getCost() {
    	return cost;
    }
    public void setCost(float cost) {
    	this.cost = cost;
    }
    
    public float getHeurisitc() {
    	return heuristic;
    }
    public void setHeuristic(float heuristic) {
    	this.heuristic = heuristic;
    }
    
    public float getTotalCost() {
        return cost + heuristic;
    }
    
    public Node getParent() {
        return parent;
    }

	public int getOrientation() {
		return orientation;
	}
	public void setOrientation(int orientation) {
		this.orientation = orientation;	
	}

}
