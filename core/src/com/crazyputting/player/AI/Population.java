package com.crazyputting.player.AI;

import com.crazyputting.objects.Terrain;

public class Population {

    public Individual[] individuals = new Individual[100];
    private final Terrain terrain;

    public Population(Terrain terrain) {
        this.terrain = terrain;
    }

    //Initialize population
    public void initializePopulation(int size) {
        for (int i = 0; i < individuals.length; i++) {
            individuals[i] = new Individual(terrain);
        }
    }

    public Individual[] getIndividuals() {
        return individuals;
    }

    public Terrain getTerrain() {
        return terrain;
    }
}


