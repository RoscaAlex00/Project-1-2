package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;

public class Ball {
    private final float mass = 0.0f;
    private final double diameter = 0.0;
    public boolean colliding = false;
    public boolean stopped = false;
    private Vector3 velocity = new Vector3();
    private Vector3 position;

    public float getMass() {
        return mass;
    }

    public double getDiameter() {
        return diameter;
    }


}
