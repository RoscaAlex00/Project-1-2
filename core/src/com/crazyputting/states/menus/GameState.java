package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.crazyputting.managers.GameStateManager;

public abstract class GameState {
    protected GameStateManager gsm;

    protected GameState(GameStateManager ourGsm) {
        this.gsm = ourGsm;
        init();
    }

    public abstract void init();

    public abstract void update(float dt);

    public abstract void draw();

    public abstract void handleInput();

    public abstract void dispose();

}
