package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.function.Function;


public class Terrain {
    private float height;
    private float width;
    private Vector3 startPos;
    private Hole hole;
    private String name;
    private Function yourFunction;
    private final float HOLE_DIAMETER = 1.10f;
    private float frictionCoefficient;
    private float maximumVelocity;

    public Terrain(float ourHeight, float ourWidth, Vector3 startingPoint, Hole endHole,
                   Function function, float MU, float maxSpeed, String name) {
        this.height = ourHeight;
        this.width = ourWidth;
        this.startPos = startingPoint; //TODO: edit the z value, using the function
        this.hole = endHole;
        this.name = name;
        this.yourFunction = function;
        this.frictionCoefficient = MU;
        this.maximumVelocity = maxSpeed;
    }


    public float getHeight() {
        return height;
    }
    public Function getFunction(){
        return yourFunction;
    }

    public float getWidth() {
        return width;
    }

    public Vector3 getStartPos() {
        return startPos;
    }

    public Hole getHole() {
        return hole;
    }

    public float getMU() {
        return frictionCoefficient;
    }

    public float getMaximumVelocity(){ return maximumVelocity; }

    public String getName() {
        return name;
    }

    public float getHoleDiameter() {
        return HOLE_DIAMETER;
    }

}
