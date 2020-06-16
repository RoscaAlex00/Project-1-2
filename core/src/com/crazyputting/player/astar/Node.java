package com.crazyputting.player.astar;

import com.badlogic.gdx.math.Vector3;

import java.util.*;

/**
 * Node for a graph
 */
public class Node {
    private final Vector3 position;
    private final Map<Node, Vector3> children = new HashMap<>();
    private final String id;
    private Vector3 accumulatedValues;
    private Vector3 heuristicValue;
    private Node parent;

    public Node(String id, Vector3 position) {
        this.id = id;
        this.position = position;
    }

    public Node(String id, Vector3 position, Node parent) {
        this.id = id;
        this.position = position;
        this.parent = parent;
        this.accumulatedValues = parent.getAccumulatedValues().cpy();
    }

    public Node(String id, Vector3 position, Vector3 heuristicValue) {
        this.id = id;
        this.position = position;
        this.heuristicValue = heuristicValue;
    }

    public Node(String id, Vector3 position, Vector3 heuristicValue, Vector3 value) {
        this.id = id;
        this.accumulatedValues = value;
        this.heuristicValue = heuristicValue;
        this.position = position;
    }

    public Node(String id, Vector3 position, Vector3 heuristicValue, Vector3 value, Node parent) {
        this.id = id;
        parent.addChild(this, value);
        this.parent = parent;
        this.accumulatedValues = parent.accumulatedValues.cpy().add(value);
        this.heuristicValue = heuristicValue;
        this.position = position;
    }

    public Node root() {
        if (parent != null) {
            return parent.root();
        } else {
            return this;
        }
    }

    public boolean isParent(Node node) {
        return parent.equals(node);
    }

    public Node getParent() {
        return parent;
    }

    public boolean isChild(Node node) {
        return children.containsKey(node);
    }

    private void addChild(Node childKey, Vector3 childValue) {
        children.put(childKey, childValue);
    }

    public String getId() {
        return id;
    }

    public Vector3 getAccumulatedValues() {
        return accumulatedValues;
    }

    public void setAccumulatedValues(Vector3 accumulatedValues) {
        this.accumulatedValues = accumulatedValues;
    }

    public Vector3 getHeuristicValue() {
        return heuristicValue;
    }

    public void setHeuristicValue(Vector3 heuristicValue) {
        this.heuristicValue = heuristicValue;
    }

    public Vector3 getTotalNodeValue() {
        return accumulatedValues.cpy().add(heuristicValue);
    }

    @Override
    public String toString() {
        StringBuilder nodeToString = new StringBuilder("Node ");
        nodeToString.append(id).append(": \n");
        nodeToString.append("Parent: ").append("node ").append(parent.getId()).append(": \n").append("Childnodes:");
        for (Node node : children.keySet()) {
            nodeToString.append(" ").append(node.getId());
        }
        return nodeToString.toString();
    }

    public Vector3 getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return this.position.x == node.position.x &&
                this.position.y == node.position.y;
    }
}
