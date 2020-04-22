package com.crazyputting.player;

import com.badlogic.gdx.math.Vector3;

public interface Player {

    Vector3 shot_velocity(Vector3 ball_position, float charge);

}
