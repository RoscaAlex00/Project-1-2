package com.crazyputting.camera;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

public class GameCamera extends InputAdapter {
    private final int leftArrow = Input.Keys.DPAD_LEFT;
    private final int rightArrow = Input.Keys.DPAD_RIGHT;
    private final int upArrow = Input.Keys.DPAD_UP;
    private final int downArrow = Input.Keys.DPAD_DOWN;

    private Camera ourCamera;
    private Vector3 position;

    public GameCamera(Camera yourCamera) {
        this.ourCamera = yourCamera;
        position = ourCamera.position;
    }

    //Not needed atm


}
