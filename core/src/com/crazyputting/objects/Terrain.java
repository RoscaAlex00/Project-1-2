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

    public Terrain(float ourHeight, float ourWidth, Vector3 startingPoint, Hole endHole,
                   Function function, String name) {
        this.height = ourHeight;
        this.width = ourWidth;
        this.startPos = startingPoint;
        this.hole = endHole;
        this.name = name;
        this.yourFunction = function;
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
        return 0;
    }

    public String getName() {
        return name;
    }

    public float getHoleDiameter() {
        return HOLE_DIAMETER;
    }

}
