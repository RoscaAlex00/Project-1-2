package com.crazyputting.player;

import com.crazyputting.player.AI.GeneticAlgorithm;
import com.crazyputting.player.AI.AlexAI;
import com.crazyputting.player.AI.FrunzAI;
import com.crazyputting.player.AI.WindAI;
import com.crazyputting.player.astar.AStar;

public class PlayerFactory {

    private static PlayerFactory factory = null;

    public static PlayerFactory get() {
        if (factory == null) {
            factory = new PlayerFactory();
        }
        return factory;
    }

    public Player makePlayer(String solverString, float maximumVelocity) {
        switch (solverString) {
            case "GA":
                return new GeneticAlgorithm();
            case "AlexAI":
                return new AlexAI();
            case "FrunzAI":
                return new FrunzAI();
            case "AStar":
                return new AStar();
            case "WindAi":
                return new WindAI();
            default:
                return new Human(maximumVelocity);
        }
    }
}
