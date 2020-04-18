package com.crazyputting.Bot;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Terrain;

public interface Player {

    Vector3 shot_velocity(Vector3 ball_position, float charge);

}
