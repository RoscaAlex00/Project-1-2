package com.crazyputting.player;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.function.Function;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;

import java.util.Map;

public class AI implements Player {

    private Terrain terrain;
    private float maximumVelocity;
    private float frictionCoefficient;
    private Hole hole;
    private Ball ball;
    private Function terrainFunction;

    public AI(float maximumVelocity){
        this.maximumVelocity = maximumVelocity;
    }

    @Override
    public Vector3 shot_velocity(Vector3 ball_position, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain){
        this.terrain = terrain;
        this.frictionCoefficient = terrain.getMU();
        this.hole = terrain.getHole();
        this.ball = terrain.getBall();
        this.terrainFunction = terrain.getFunction();

        return new Vector3(2,2,2);
    }

    private float getFitness(){
        Vector3 holePos = hole.getPosition().cpy();
        Vector3 ballPos = ball.getPosition().cpy();
        Vector3 ballToHoleDistance = holePos.sub(ballPos);
        ballToHoleDistance.z = 0;
        return ballToHoleDistance.len();
    }
}

