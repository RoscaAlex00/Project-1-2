package com.crazyputting.player;

import com.crazyputting.player.AI.AI;
import com.crazyputting.player.AI.AlexAI;
import com.crazyputting.player.AI.FrunzAI;

public class PlayerFactory {

    private static PlayerFactory factory = null;

    public static PlayerFactory get(){
        if (factory == null){
            factory = new PlayerFactory();
        }
        return factory;
    }

    public Player makePlayer(String solverString, float maximumVelocity){
        switch (solverString){
            case "AI":
                return new AI(maximumVelocity);
            case "AlexAI":
                return new AlexAI();
            /*case "AIStefan":
                return new AIStefan();*/
            case "FrunzAI":
                return new FrunzAI();
            default:
                return new Human(maximumVelocity);
        }
    }
}
