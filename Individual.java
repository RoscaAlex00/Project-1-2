package com.crazyputting.Bot;

import java.util.Random;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;

public class Individual {

	private Terrain terrain;
	private Hole hole;
    private Ball ball;    

    private float fitness;
    private Vector3 shotVelocity;
    
    public Individual(Terrain terrain) {
        Random rn = new Random();
        
        this.terrain = terrain;
        this.hole = terrain.getHole();
        this.ball = terrain.getBall();
        shotVelocity = new Vector3(rn.nextFloat(), rn.nextFloat(), rn.nextFloat()); // Needs to be changed (only reflects the idea)
        ball.hit(shotVelocity);

        fitness = Float.MAX_VALUE;
    }

    //Calculate fitness
    public void calcFitness() {
    	Vector3 holePos = hole.getPosition().cpy();
        Vector3 ballPos = ball.getPosition().cpy();
        Vector3 ballToHoleDistance = holePos.sub(ballPos);
        ballToHoleDistance.z = 0;
        fitness = ballToHoleDistance.len();
    }

	public float getFitness() {
		calcFitness();
		return fitness;
	}
}
