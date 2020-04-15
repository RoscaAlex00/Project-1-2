package com.crazyputting.engine;

import com.badlogic.gdx.math.Vector3;

public interface PhysicsSolver {
    void set_step_size(double h);

    void setPhysics(Physics physics);

    Vector3 getPosition(Vector3 position, Vector3 velocity);

    Vector3 getSpeed(Vector3 position, Vector3 velocity);
}