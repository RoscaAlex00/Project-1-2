package com.crazyputting.physicsengine;

import com.badlogic.gdx.math.Vector3;

public class Verlet implements PhysicsSolver {
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
        Vector3 pos1 = position.cpy().add(velocity.cpy().scl(physics.getDt()));
        return pos1.cpy().add(physics.getAcceleration(position, velocity).cpy().scl((float) Math.pow(physics.getDt(), 2) / 2f));
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        Vector3 velNext = velocity.cpy().add(physics.getAcceleration(position, velocity).cpy().scl(physics.getDt()));
        Vector3 posNext = position.cpy().add(velocity.cpy().scl(physics.getDt()));
        Vector3 accNext = physics.getAcceleration(posNext, velNext).cpy();
        Vector3 accSum = accNext.add(physics.getAcceleration(position, velocity).cpy());

        return velocity.cpy().add(accSum.scl(physics.getDt() / 2f));
    }

    @Override
    public void setHit(boolean isHit) {

    }
}
