package com.crazyputting.Bot;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Terrain;

public class Human implements Player {

    private float maximumVelocity;

    public Human(float maximumVelocity){
        this.maximumVelocity = maximumVelocity;
    }

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) {
        float reverse = 1;
        if (camera_direction.z < -0.999849)
            reverse = -1;
        double hypotenuse = Math.sqrt(Math.pow(camera_direction.x, 2) + Math.pow(camera_direction.y, 2));
        float scalingFactor = maximumVelocity * (float) (1 / hypotenuse);
        return camera_direction.scl(reverse * scalingFactor * charge);
    }

    /**
     * Only used for the AI classes
     * @return null
     */
    @Override
    public Vector3 shot_velocity(Terrain terrain) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }
}
