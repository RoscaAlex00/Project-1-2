package com.crazyputting.managers;

import com.crazyputting.states.menus.GameState;
import com.crazyputting.states.menus.MainMenu;
import com.crazyputting.states.menus.PlayState;
import com.crazyputting.states.menus.SettingsMenu;

public class GameStateManager {
    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int SETTINGS = 2;
    private GameState gameState;

    public GameStateManager() {
        setState(MENU);
    }

    public void setState(int state) {
        if (gameState != null) {
            gameState.dispose();
        }
        if (state == MENU) {
            gameState = new MainMenu(this);
        }
        if (state == PLAY) {
            gameState = new PlayState(this);
        }
        if (state == SETTINGS) {
            gameState = new SettingsMenu(this);
        }

    }

    public void update(float dt) {
        gameState.update(dt);
    }

    public void draw() {
        gameState.draw();
    }
}
