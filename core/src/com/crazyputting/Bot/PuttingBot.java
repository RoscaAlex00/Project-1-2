package com.crazyputting.Bot;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.PuttingCourse;

interface PuttingBot {
    public Vector3 shot_velocity(PuttingCourse course,
                                 Vector3 ball_position);

    // For future phases
}
