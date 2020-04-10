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
        float k1 = physics.getDt() * rungeFunction(velocity.x, velocity.y);
        float k2 = physics.getDt() * rungeFunction(velocity.x + (physics.getDt() / 2), velocity.y + (k1 / 2));
        float k3 = physics.getDt() * rungeFunction(velocity.x + (physics.getDt() / 2), velocity.y + (k2 / 2));
        float k4 = physics.getDt() * rungeFunction(velocity.x + physics.getDt(), velocity.y + k3);
        float newX = position.x + physics.getDt();
        float newY = (position.y + (1 / 6) * (k1 + 2 * k2 + 2 * k3 + k4));
        return new Vector3(newX, newY, 0);
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        float newVelX = (velocity.x + physics.getDt() * physics.getAcceleration(position, velocity).x);
        float newVelY = (velocity.y + physics.getDt() * physics.getAcceleration(position, velocity).y);
        return new Vector3(newVelX, newVelY, 0);
    }

    public float rungeFunction(float x, float y) {
        return (x - y) / 2;
    }
}
