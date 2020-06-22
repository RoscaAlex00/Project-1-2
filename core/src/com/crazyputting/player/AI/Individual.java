package com.crazyputting.player.AI;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;

public class Individual {

    private final Hole hole;

    private float fitness;
    private Vector3 shotVelocity;

    public Individual(Terrain terrain) {
        this.hole = terrain.getHole();
        shotVelocity = new Vector3((float) Math.random() * 10, (float) Math.random() * 10, 0);
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

    public Vector3 getShotVelocity() {
        return shotVelocity;
    }

    public void setShotVelocity(Vector3 newVelocity) {
        this.shotVelocity = newVelocity;
    }
}
