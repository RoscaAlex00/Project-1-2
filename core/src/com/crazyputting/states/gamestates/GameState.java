package com.crazyputting.states.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
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

    public abstract void init();

    public abstract void update(float dt) throws IllegalAccessException;

    public abstract void draw() throws IllegalAccessException;

    public abstract void handleInput();

    public abstract void dispose();

}
