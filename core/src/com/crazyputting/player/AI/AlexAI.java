package com.crazyputting.player.AI;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Player;

public class AlexAI implements Player {

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    /**
     * Scale the velocity in different ways depending on the distance to the next coordinate
     */
    @Override
    public void shot_velocity(Terrain terrain) {
        Ball ball = terrain.getBall();
        Hole hole = terrain.getHole();
        Vector3 velocity = hole.getPosition().cpy().sub(ball.getPosition().cpy());
        int THRESHOLD1 = 5;
        int THRESHOLD2 = 15;
        float subX = velocity.x;
        float subY = velocity.y;
        if (subX < THRESHOLD1 && subY < THRESHOLD1 && subX > -THRESHOLD1 && subY > -THRESHOLD1) {
            velocity.scl(1.07f);
        } else if (subX < THRESHOLD2 && subY < THRESHOLD2 && subX > -THRESHOLD2 && subY > -THRESHOLD2) {
            velocity.scl(0.65f);
        } else {
            velocity.scl(0.325f);
        }
        ball.hit(velocity);
    }

    /**
     * Not used in this AI
     */
    @Override
    public void setTerrain(Terrain terrain) {
    }
}
