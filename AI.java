package com.crazyputting.Bot;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.function.Function;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;

import java.util.Map;
import java.util.Random;

public class AI implements Player {

    private Terrain terrain;
    private float maximumVelocity;
    private float frictionCoefficient;
    private Hole hole;
    private Ball ball;
    private Function terrainFunction;
    
    private Population population;
    private Individual fittest;
    private Individual secondFittest;
    int generationCount = 0;

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

        return new Vector3(1,1,1);
    }
    
    private void update() {
    	
    	Random rn = new Random();
    	
    	//Initialize population
    	population = new Population(terrain);
    	population.initializePopulation(10);
    	
    	//Calculate fitness of each individual
        population.calculateFitness();
        
        System.out.println("Generation: " + generationCount + " Fittest: " + population.getFittestValue());
        
        //While population gets an individual with maximum fitness (the best fitness is the smallest one)
        while (population.getFittestValue() >= 1) {
            ++generationCount;

            //Do selection
            selection();

            //Do crossover
            crossover();

            //Do mutation under a random probability
            if (rn.nextInt()%7 < 5) {
                mutation();
            }

            //Add fittest offspring to population
            addFittestOffspring();

            //Calculate new fitness value
            population.calculateFitness();

            System.out.println("Generation: " + generationCount + " Fittest: " + population.getFittestValue());
        }

        System.out.println("\nSolution found in generation " + generationCount);
        System.out.println("Fitness: "+ population.getFittest().getFitness());
       
    }
    
    //Selection
    void selection() {

        //Select the most fittest individual
        fittest = population.getFittest();

        //Select the second most fittest individual
        secondFittest = population.getSecondFittest();
    }

    //TODO: Crossover
    void crossover() {
        Random rn = new Random();
        
        //Select a random crossover point
        
        //Swap values among parents 
    }

    //TODO: Mutation
    void mutation() {
        Random rn = new Random();

        //Select a random mutation point
        
        //Flip values at the mutation point
    }

    //Get fittest offspring
    Individual getFittestOffspring() {
        if (fittest.getFitness() > secondFittest.getFitness()) {
            return fittest;
        }
        return secondFittest;
    }


    //Replace least fittest individual from most fittest offspring
    void addFittestOffspring() {

        //Update fitness values of offspring
        fittest.calcFitness();
        secondFittest.calcFitness();

        //Get index of least fit individual
        int leastFittestIndex = population.getLeastFittestIndex();

        //Replace least fittest individual from most fittest offspring
        population.individuals[leastFittestIndex] = getFittestOffspring();
    }
}
