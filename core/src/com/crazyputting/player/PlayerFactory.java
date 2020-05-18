package com.crazyputting.player;

import com.crazyputting.engine.*;

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
            default:
                return new Human(maximumVelocity);
        }
    }
}
