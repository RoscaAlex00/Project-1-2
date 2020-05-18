package com.crazyputting.player.astar;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Node for a graph
 */
public class Node {
    private Vector3 accumulatedValues;
    private Vector3 heuristicValue;
    private Node parent;
    private final Map<Node, Vector3> children = new HashMap<>();
    private final String id;

    public Node(String id, Vector3 heuristicValue){
        this.id = id;
        this.accumulatedValues = new Vector3(0,0,0);
        this.heuristicValue = heuristicValue;
    }

    public Node(String id, Node parent, Vector3 value, Vector3 heuristicValue){
        this.id = id;
        parent.addChild(this, value);
        this.parent = parent;
        this.accumulatedValues = parent.accumulatedValues.cpy().add(value);
        this.heuristicValue = heuristicValue;
    }

    public Node root(){
        if (parent != null){
            return parent.root();
        }
        else{
            return this;
        }
    }

    public boolean isParent(Node node){
        return parent.equals(node);
    }

    public boolean isChild(Node node){
        return children.containsKey(node);
    }

    private void addChild(Node childKey, Vector3 childValue){
        children.put(childKey, childValue);
    }

    public String getId() {
        return id;
    }

    public Vector3 getAccumulatedValues(){
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

    public Vector3 getTotalNodeValue(){
        return accumulatedValues.cpy().add(heuristicValue);
    }

    @Override
    public String toString() {
        StringBuilder nodeToString = new StringBuilder("Node ");
        nodeToString.append(id).append(": \n");
        nodeToString.append("Parent: ").append("node ").append(parent.getId()).append(": \n").append("Childnodes:");
        for (Node node : children.keySet()){
            nodeToString.append(" ").append(node.getId());
        }
        return nodeToString.toString();
    }
}
