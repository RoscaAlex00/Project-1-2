package com.crazyputting.managers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;

public class GameInputProcessor extends InputAdapter {
    public boolean keyDown(int k) {
        keyChanged(k, true);
        return true;
    }

    public boolean keyUp(int k) {
        keyChanged(k, false);
        return true;
    }

    private void keyChanged(int k, boolean pressed){
        if (k == Keys.UP) {
            GameKeys.setKey(GameKeys.UP, pressed);
        }
        if (k == Keys.DOWN) {
            GameKeys.setKey(GameKeys.DOWN, pressed);
        }
        if (k == Keys.LEFT) {
            GameKeys.setKey(GameKeys.LEFT, pressed);
        }
        if (k == Keys.RIGHT) {
            GameKeys.setKey(GameKeys.RIGHT, pressed);
        }
        if (k == Keys.SPACE) {
            GameKeys.setKey(GameKeys.SPACE, pressed);
        }
        if (k == Keys.ESCAPE) {
            GameKeys.setKey(GameKeys.ESCAPE, pressed);
        }
        if (k == Keys.ENTER) {
            GameKeys.setKey(GameKeys.ENTER, pressed);
        }
    }
}
