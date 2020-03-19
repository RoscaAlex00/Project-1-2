package com.crazyputting.managers;

import com.crazyputting.objects.PuttingCourse;
import com.crazyputting.objects.Terrain;
import com.crazyputting.states.menus.*;

public class GameStateManager {
    public static final int MENU = 0;
    public static final int PLAY = 1;
    public static final int SETTINGS = 2;
    public static final int COURSE_CREATOR = 3;
    private GameState gameState;
    private Terrain terrain;
    private PuttingCourse course;

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

    }

    public void update(float dt) {
        gameState.update(dt);
    }

    public void draw() {
        gameState.draw();
    }

    public void setTerrain(Terrain newTerrain){
        this.terrain = newTerrain;
    }

    public Terrain getTerrain(){ return terrain; }

    public void setCourse(PuttingCourse newCourse){ this.course = newCourse; }

    public PuttingCourse getCourse(){ return course; }
}
