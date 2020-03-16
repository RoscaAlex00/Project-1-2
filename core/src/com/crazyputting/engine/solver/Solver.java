package com.crazyputting.engine.solver;

import com.badlogic.gdx.math.Vector3;

public interface Solver {
    void set_step_size(double h);
    Vector3 getPosition(Vector3 position,Vector3 velocity);
    Vector3 getSpeed(Vector3 position,Vector3 velocity);
}
