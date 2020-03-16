package com.crazyputting.engine.solver;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.engine.Engine;

public class Euler implements Solver {
    private Engine engine;

    @Override
    public void set_step_size(double h) {
    }

    @Override
    public Vector3 getPosition(Vector3 position, Vector3 velocity) {
        float newX = (float) (position.x + (engine.getDt() * velocity.x));
        float newY = (float) (position.y + (engine.getDt() * velocity.y));

        return new Vector3(newX, newY, 0);
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        float newVelX = (float) (velocity.x + engine.getDt() * engine.getAcceleration(position, velocity).x);
        float newVelY = (float) (velocity.y + engine.getDt() * engine.getAcceleration(position, velocity).y);

        return new Vector3(newVelX, newVelY, 0);
    }
}
