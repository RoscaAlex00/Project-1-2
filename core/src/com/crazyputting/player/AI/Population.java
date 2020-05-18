package com.crazyputting.player.AI;

import com.crazyputting.objects.Terrain;

public class Population {

	private Terrain terrain;
    public Individual[] individuals = new Individual[100];
    public Population(Terrain terrain) {
    	this.terrain = terrain;
    }
    
    //Initialize population
    public void initializePopulation(int size) {
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(terrain);
        }


    }
    //Calculate fitness of each individual
   /* public void calculateFitness() {
        for (int i = 0; i < individuals.length; i++) {
            individuals[i].calcFitness();
        }
    }*/
    public Individual[] getIndividuals(){
        return individuals;
    }
    public Terrain getTerrain(){
        return terrain;
    }
    public void setIndividuals(Individual[] newIndividuals){
        this.individuals = newIndividuals;
    }
}


