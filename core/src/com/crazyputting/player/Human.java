package com.crazyputting.player;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Terrain;

public class Human implements Player {

    private final float maximumVelocity;

    public Human(float maximumVelocity) {
        this.maximumVelocity = maximumVelocity;
    }

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) {
        //Fixes a bug that the ball shoots backwards when the camera is topdown
        float reverse = 1;
        if (camera_direction.z < -0.999849) {
            reverse = -1;
        }
        double hypotenuse = Math.sqrt(Math.pow(camera_direction.x, 2) + Math.pow(camera_direction.y, 2));
        float scalingFactor = maximumVelocity * (float) (1 / hypotenuse);
        return camera_direction.scl(reverse * scalingFactor * charge);
    }

    @Override
    public void shot_velocity(Terrain terrain) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }
    @Override
    public void setTerrain(Terrain terrain) {

    }
}
