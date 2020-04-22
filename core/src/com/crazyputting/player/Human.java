package com.crazyputting.player;

import com.badlogic.gdx.math.Vector3;

public class Human implements Player {

    private float maximumVelocity;

    public Human(float maximumVelocity){
        this.maximumVelocity = maximumVelocity;
    }

    @Override
    public Vector3 shot_velocity(Vector3 ball_position, float charge) {
        float reverse = 1;
        if (ball_position.z < -0.999849)
            reverse = -1;
        double hypotenuse = Math.sqrt(Math.pow(ball_position.x, 2) + Math.pow(ball_position.y, 2));
        float scalingFactor = maximumVelocity * (float) (1 / hypotenuse);
        return ball_position.scl(reverse * scalingFactor * charge);
    }
}
