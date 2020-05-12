package com.crazyputting.player;

import com.badlogic.gdx.math.Vector;
import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;

public class AlexAI implements Player {
    private Hole hole;
    private Ball ball;
    private Terrain terrain;
    private Vector3 velocity;

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        return null;
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) throws IllegalAccessException {
        Vector3 diff = new Vector3();
        this.ball = terrain.getBall();
        Vector3 ballPos = ball.getPosition();
        Vector3 threshold = new Vector3(3.5f, 3.5f, 0);
        this.hole = terrain.getHole();
        velocity = new Vector3();

        float subX = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy()).x;
        float subY = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy()).y;

        if (subX < threshold.x && subY < threshold.y && subX > -3.5f && subY > -3.5f) {
            velocity = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy());
        } else if (velocity.len() == 0) {
            velocity = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy());
            velocity.scl(0.385f);
        } else {
            diff.x = terrain.getHole().getPosition().cpy().x - ballPos.x;
            diff.y = terrain.getHole().getPosition().cpy().y - ballPos.y;
            velocity.add(diff);
        }
        ball.hit(velocity);
        return null;
    }

    @Override
    public void runLoop() {
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
