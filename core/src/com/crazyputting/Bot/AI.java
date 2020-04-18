package com.crazyputting.Bot;

import com.badlogic.gdx.math.Vector3;

public class AI implements Player {

    float maximumVelocity;

    public AI(float maximumVelocity){
        this.maximumVelocity = maximumVelocity;
    }

    @Override
    public Vector3 shot_velocity(Vector3 ball_position, float charge) {

        return new Vector3(1,1,0);
    }
}

