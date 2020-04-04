package com.crazyputting.engine;

import com.badlogic.gdx.math.Vector3;

public class RungeKutta implements PhysicsSolver {
    private Physics physics;
    @Override
    public void set_step_size(double h) {
    }

    @Override
    public void setPhysics(Physics physics) {
        this.physics = physics;
    }

    @Override
    public Vector3 getPosition(Vector3 position, Vector3 velocity) {
        return null;
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        return null;
    }
}
