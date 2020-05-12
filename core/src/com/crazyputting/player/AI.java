package com.crazyputting.player;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.function.Function;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Individual;
import com.crazyputting.player.Player;
import com.crazyputting.player.Population;

import java.util.Map;
import java.util.Random;

public class AI implements Player {

    int generationCount = 0;
    private Terrain terrain;
    private float maximumVelocity;
    private float frictionCoefficient;
    private Hole hole;
    private Ball ball;
    private Function terrainFunction;
    private HeapSort heapSort = new HeapSort();
    private Population population;

    public AI(float maximumVelocity) {
        this.maximumVelocity = maximumVelocity;
    }


    @Override
    public Vector3 shot_velocity(Vector3 ball_position, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) {
        this.terrain = terrain;
        this.frictionCoefficient = terrain.getMU();
        this.hole = terrain.getHole();
        this.ball = terrain.getBall();
        this.terrainFunction = terrain.getFunction();

        return new Vector3(1, 1, 1);
    }

    public void runLoop() {


        //Initialize population
        population = new Population(terrain);
        shot_velocity(terrain);
        population.initializePopulation(100);

        //Calculate fitness of each individual
        for(int i = 0;i<population.getIndividuals().length;i++){
            ball.hit(population.getIndividuals()[i].getShotVelocity());
            population.getIndividuals()[i].calcFitness(ball.getPosition().cpy());
        }
        heapSort.sort(population.getIndividuals());


        for(int i = 0;i<population.getIndividuals().length;i++){
            System.out.println(population.getIndividuals()[i].getFitness());
        }

        //While population gets an individual with maximum fitness (the best fitness is the smallest one)
      /*  while (population.individuals[0].getFitness() >= 1) {
            ++generationCount;
            Individual[] newIndividuals = crossover(population);
            population.setIndividuals(newIndividuals);
            mutation(population);

            //Calculate new fitness value
            population.calculateFitness();
            heapSort.sort(population.getIndividuals());

            System.out.println("Generation: " + generationCount + " Fittest: " + population.getIndividuals()[0].getFitness());
        }

        System.out.println("\nSolution found in generation " + generationCount);
        System.out.println("Fitness: " + population.individuals[0].getFitness());*/

    }


    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }


    //Selection
    Individual[] tournamentSelection(Population population) {
        Individual[] tournamentPopulation = new Individual[4];
        for (int i = 0; i < 4; i++) {
            tournamentPopulation[i] = population.getIndividuals()[(int) (Math.random() * population.getIndividuals().length)];
        }
        return tournamentPopulation;
    }

    //TODO: Crossover
    Individual[] crossover(Population population) {
        int length = population.getIndividuals().length;
        Individual[] offsprings = new Individual[length];

        for (int i = 0; i < length; i++) {
            Individual parent1 = tournamentSelection(population)[0];
            Individual parent2 = tournamentSelection(population)[0];

            offsprings[i] = parentCrossover(parent1, parent2);
        }

        return offsprings;

    }

    private Individual parentCrossover(Individual individual, Individual individual1) {
        Individual offSpring = new Individual(population.getTerrain());
        float offSpringX = (individual.getShotVelocity().x * individual1.getShotVelocity().x) / 2;
        float offSpringY = (individual.getShotVelocity().y * individual1.getShotVelocity().y) / 2;
        offSpring.setShotVelocity(new Vector3(offSpringX, offSpringY, 0));
        return offSpring;
    }


    //TODO: Mutation
    void mutation(Population population) {
        double chanceOfMutation = 0.07;
        double xOrYOrZ = Math.random();
        double plusOrMinus = Math.random();

        for (int i = 0; i < population.getIndividuals().length; i++) {
            double check = Math.random();
            if (check <= chanceOfMutation) {
                if (xOrYOrZ <= 0.50) {
                    Vector3 mutatedX = population.getIndividuals()[i].getShotVelocity();
                    if (plusOrMinus >= 0.50) {
                        mutatedX.x = mutatedX.x - 0.1f;
                    } else {
                        mutatedX.x = mutatedX.x + 0.1f;
                    }
                    population.getIndividuals()[i].setShotVelocity(mutatedX);
                } else {
                    Vector3 mutatedY = population.getIndividuals()[i].getShotVelocity();
                    if (plusOrMinus >= 0.50) {
                        mutatedY.y = mutatedY.y - 0.1f;
                    } else {
                        mutatedY.y = mutatedY.y + 0.1f;
                    }
                    population.getIndividuals()[i].setShotVelocity(mutatedY);
                }
            }
        }

    }

}

