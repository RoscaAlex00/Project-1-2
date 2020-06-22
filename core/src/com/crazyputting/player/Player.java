package com.crazyputting.player;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Terrain;

public interface Player {

    /**
     * Only used for human player
     */
    Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException;

    /**
     * Only used for AI's
     */
    void shot_velocity(Terrain terrain) throws IllegalAccessException;

    void setTerrain(Terrain terrain);

}
