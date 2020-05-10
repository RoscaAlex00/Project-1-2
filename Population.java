package com.crazyputting.Bot;

import com.crazyputting.objects.Terrain;

public class Population {

	private Terrain terrain;
	int popSize = 10;
    Individual[] individuals = new Individual[10];
    private float fittest = Float.MAX_VALUE;

    public Population(Terrain terrain) {
    	this.terrain = terrain;
    }
    
    //Initialize population
    public void initializePopulation(int size) {
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(terrain);
        }
    }

    //Get the fittest individual
    public Individual getFittest() {
        float maxFit = Float.MAX_VALUE;
        int maxFitIndex = 0;
        for (int i = 0; i < individuals.length; i++) {
            if (maxFit >= individuals[i].getFitness()) {
                maxFit = individuals[i].getFitness();
                maxFitIndex = i;
            }
        }
        fittest = individuals[maxFitIndex].getFitness();
        return individuals[maxFitIndex];
    }
    
    public float getFittestValue() {
    	return fittest;
    }

    //Get the second most fittest individual
    public Individual getSecondFittest() {
        int maxFit1 = 0;
        int maxFit2 = 0;
        for (int i = 0; i < individuals.length; i++) {
            if (individuals[i].getFitness() < individuals[maxFit1].getFitness()) {
                maxFit2 = maxFit1;
                maxFit1 = i;
            } else if (individuals[i].getFitness() < individuals[maxFit2].getFitness()) {
                maxFit2 = i;
            }
        }
        return individuals[maxFit2];
    }

    //Get index of least fittest individual
    public int getLeastFittestIndex() {
        float minFitVal = Float.MIN_VALUE;
        int minFitIndex = 0;
        for (int i = 0; i < individuals.length; i++) {
            if (minFitVal <= individuals[i].getFitness()) {
                minFitVal = individuals[i].getFitness();
                minFitIndex = i;
            }
        }
        return minFitIndex;
    }

    //Calculate fitness of each individual
    public void calculateFitness() {

        for (int i = 0; i < individuals.length; i++) {
            individuals[i].calcFitness();
        }
        getFittest();
    }
}

