package com.crazyputting.physicsengine;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.physicsengine.Physics;

public interface PhysicsSolver {
    void set_step_size(double h);

    void setPhysics(Physics physics);

    Vector3 getPosition(Vector3 position, Vector3 velocity);

    Vector3 getSpeed(Vector3 position, Vector3 velocity);

    //Only used for AdamsBashforth
    void setHit(boolean isHit);
}
