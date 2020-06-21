package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;

public class Hole {
    private float holeRadius;
    private Vector3 pos;

    public Hole(float rad, Vector3 yourPos) {
        this.holeRadius = rad;
        this.pos = yourPos;
    }

    public Vector3 getPosition() {
        return pos;
    }

    public float getHoleRadius() {
        return holeRadius;
    }
}
