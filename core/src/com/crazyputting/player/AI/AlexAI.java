package com.crazyputting.player.AI;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Player;

public class AlexAI implements Player {
    private Hole hole;
    private Ball ball;
    private Terrain terrain;
    private Vector3 velocity;

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) {
        this.ball = terrain.getBall();
        Vector3 threshold = new Vector3(5f, 5f, 0);
        this.hole = terrain.getHole();
        velocity = new Vector3();

        float subX = hole.getPosition().cpy().sub(ball.getPosition().cpy()).x;
        float subY = hole.getPosition().cpy().sub(ball.getPosition().cpy()).y;

        if (subX < threshold.x && subY < threshold.y && subX > -threshold.x && subY > -threshold.y) {
            //System.out.println("threshold: " + subX + " y: " + subY);
            velocity = hole.getPosition().cpy().sub(ball.getPosition().cpy());
            velocity.scl(1.07f);
        } else if (subX < 15f && subY < 15f && subX > -15f && subY > -15f) {
            // System.out.println("regular: " + subX + " y: " + subY);
            velocity = hole.getPosition().cpy().sub(ball.getPosition().cpy());
            velocity.scl(0.65f);
        } else {
            //System.out.println("regula22: " + subX + " y: " + subY);
            velocity = hole.getPosition().cpy().sub(ball.getPosition().cpy());
            velocity.scl(0.325f);
        }
        ball.hit(velocity);
        return null;
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
