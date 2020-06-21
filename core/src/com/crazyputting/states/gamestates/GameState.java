package com.crazyputting.states.gamestates;

import com.crazyputting.managers.GameStateManager;
import com.crazyputting.objects.Terrain;

public abstract class GameState {
    protected GameStateManager gsm;
    protected Terrain terrain;

    protected GameState(GameStateManager ourGsm) {
        this.gsm = ourGsm;
        init();
    }

    protected GameState(GameStateManager ourGsm, Terrain ourTerrain) {
        this.gsm = ourGsm;
        this.terrain = ourTerrain;
        init();
    }

    /**
     * Initial method. Declares font, backgrounds etc. only once
     */
    public abstract void init();

    /**
     * Makes sure that the states can constantly update
     * @param dt step-size
     */
    public abstract void update(float dt) throws IllegalAccessException;

    /**
     * Makes sure that the menu visually updates its contents, once they're changed
     */
    public abstract void draw() throws IllegalAccessException;

    public abstract void handleInput();

    public abstract void dispose();

}
