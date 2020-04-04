package com.crazyputting.engine;

import com.badlogic.gdx.math.Vector3;

public class Verlet implements PhysicsSolver {
    private Physics physics;

    public Verlet(Physics engine) {
        this.physics = engine;
    }

    @Override
    public void set_step_size(double h) {
    }

    @Override
    public Vector3 getPosition(Vector3 position, Vector3 velocity) {
        float newX = 0f;
        float newY = 0f;

        return new Vector3(newX, newY, 0);
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        float newVelX = 0f;
        float newVelY = 0f;

        return new Vector3(newVelX, newVelY, 0);
    }
}
