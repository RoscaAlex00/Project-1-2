package com.crazyputting.engine;

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
        float newX = (position.x + (physics.getDt() * velocity.x) +
                (physics.getDt() * physics.getAcceleration(position, velocity).x * physics.getDt() / 2));
        float newY = (position.y + (physics.getDt() * velocity.y) +
                (physics.getDt() * physics.getAcceleration(position, velocity).y * physics.getDt() / 2));

        return new Vector3(newX, newY, 0);
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        float newVelX = (velocity.x + physics.getDt() * physics.getAcceleration(position, velocity).x);
        float newVelY = (velocity.y + physics.getDt() * physics.getAcceleration(position, velocity).y);

        return new Vector3(newVelX, newVelY, 0);
    }
}
