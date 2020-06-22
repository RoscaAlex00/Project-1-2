package com.crazyputting.player.AI;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Player;

public class AI implements Player {

    private Terrain terrain;
    private Ball ball;

    @Override
    public Vector3 shot_velocity(Vector3 ball_position, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public void shot_velocity(Terrain terrain) {
        this.terrain = terrain;
        this.ball = terrain.getBall();
        runLoop();

        new Vector3(1, 1, 1);
    }

    private void runLoop() {
        //Initialize population
        Population population = new Population(terrain);
        shot_velocity(terrain);
        population.initializePopulation(100);

        //Calculate fitness of each individual
        for (int i = 0; i < population.getIndividuals().length; i++) {
            ball.hit(population.getIndividuals()[i].getShotVelocity());
            population.getIndividuals()[i].calcFitness(ball.getPosition().cpy());
        }
        for (int i = 0; i < population.getIndividuals().length; i++) {
            System.out.println(population.getIndividuals()[i].getFitness());
        }
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}

