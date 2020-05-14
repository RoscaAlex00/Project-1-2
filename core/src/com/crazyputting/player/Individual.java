package com.crazyputting.player;

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

        this.terrain = terrain;
        this.hole = terrain.getHole();
        this.ball = terrain.getBall();
        shotVelocity = new Vector3((float)Math.random() * 10, (float)Math.random() * 10,0);
    }

    //Calculate fitness
    public void calcFitness(Vector3 ballpos) {
    	Vector3 holePos = hole.getPosition().cpy();
        Vector3 ballPos = ballpos.cpy();
        Vector3 ballToHoleDistance = holePos.sub(ballPos);
        ballToHoleDistance.z = 0;
        fitness = ballToHoleDistance.len();
    }

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float newFitness){
        this.fitness = newFitness;
    }
    public void setShotVelocity(Vector3 newVelocity){
        this.shotVelocity = newVelocity;
    }
    public Vector3 getShotVelocity(){
        return shotVelocity;
    }
}
