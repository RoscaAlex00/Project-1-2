package com.crazyputting.physicsengine;

import com.badlogic.gdx.math.Vector3;
/**
 * Euler Solver
 */

public class Euler implements PhysicsSolver {
    private Physics physics;

    public void setPhysics(Physics physics) {
        this.physics = physics;
    }


    @Override
    public Vector3 getPosition(Vector3 position, Vector3 velocity) {
        return position.cpy().add(velocity.cpy().scl(physics.getDt()));
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        return velocity.cpy().add(physics.getAcceleration(position, velocity).cpy().scl(physics.getDt()));
    }

    @Override
    public void setHit(boolean isHit) {

    }
}
