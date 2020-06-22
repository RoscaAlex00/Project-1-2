package com.crazyputting.physicsengine;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.physicsengine.Physics;

public interface PhysicsSolver {

    void setPhysics(Physics physics);

    Vector3 getPosition(Vector3 position, Vector3 velocity);

    Vector3 getSpeed(Vector3 position, Vector3 velocity);

    /**
     * Only used for Adams Bashforth solver
     */
    void setHit(boolean isHit);
}
