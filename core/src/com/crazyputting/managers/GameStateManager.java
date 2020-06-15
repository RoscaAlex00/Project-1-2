package com.crazyputting.managers;

import com.crazyputting.objects.Terrain;
import com.crazyputting.states.menus.CreatorMenu;
import com.crazyputting.states.menus.MainMenu;
import com.crazyputting.states.menus.SettingsMenu;
import com.crazyputting.states.gamestates.EndState;
import com.crazyputting.states.gamestates.GameState;
import com.crazyputting.states.gamestates.PlayState;

public class GameStateManager {
    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int SETTINGS = 2;
    public static final int COURSE_CREATOR = 3;
    public static final int END = 4;
    private GameState gameState;
    private Terrain terrain;

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
            gameState = new PlayState(this, terrain);
            //gameState = new PlayState(this, course);
        }
        if (state == SETTINGS) {
            gameState = new SettingsMenu(this);
        }
        if (state == COURSE_CREATOR) {
            gameState = new CreatorMenu(this);
        }
        if (state == END) {
            gameState = new EndState(this);
        }
    }

    public void update(float dt) throws IllegalAccessException {
        gameState.update(dt);
    }

    public void draw() throws IllegalAccessException {
        gameState.draw();
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain newTerrain) {
        this.terrain = newTerrain;
    }

}
