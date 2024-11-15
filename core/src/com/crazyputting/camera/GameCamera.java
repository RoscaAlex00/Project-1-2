package com.crazyputting.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class GameCamera extends InputAdapter {
    private final int ctrlKey = Input.Keys.CONTROL_LEFT;
    private final float rotateUnit = 2f;
    private final float translateUnits = 0.5f;
    private int button = -1;
    private final ArrayList<Integer> keyPressed = new ArrayList<>();
    private final Camera camera;
    private Vector3 focus;
    private boolean focused;
    private final Vector3 tmpV1 = new Vector3();
    private final Vector3 tmpV2 = new Vector3();
    private float startX, startY;
    private final Vector3 distanceCamFocus = new Vector3();
    private final int bottom = 1;

    public GameCamera(Camera camera) {
        this.camera = camera;
        focus = camera.position;
        updateDistanceCamFocus();
    }

    public void initFocus(Vector3 focus) {
        camera.position.set(focus.cpy().add(new Vector3(0, -1, 1).scl(10)));
        camera.lookAt(focus);
        focused = true;
        this.focus = focus;
        updateDistanceCamFocus();
    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == ctrlKey) {
            focused = !focused;
            return false;
        }
        keyPressed.add(keycode);
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        for (int i = 0; i < keyPressed.size(); i++) {
            if (keycode == keyPressed.get(i)) {
                keyPressed.remove(i);
            }
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        startX = screenX;
        startY = screenY;
        this.button = button;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (this.button == button) {
            this.button = -1;
        }
        return false;
    }

    private boolean process(float deltaX, float deltaY) {
        int rotateButton = Input.Buttons.RIGHT;
        if (button == rotateButton && !focused) {
            camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-deltaX * 40 * translateUnits));
            if (deltaY < 0 || camera.position.z > bottom){
                camera.translate(tmpV2.set(camera.up).scl(-deltaY * 40 * translateUnits));
            }

        } else if (button == rotateButton) {
            camera.rotateAround(focus, new Vector3(0, 0, 1), -(90 * rotateUnit * deltaX));
            Vector3 normal = new Vector3(camera.up).rotate(camera.direction, 90);
            if ((camera.up.z > 0 || deltaY > 0) && (camera.position.z > bottom || deltaY < 0)){
                camera.rotateAround(focus, normal, (90 * rotateUnit * deltaY));
            }
        }
        updateDistanceCamFocus();
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        final float deltaX = (screenX - startX) / Gdx.graphics.getWidth();
        final float deltaY = (startY - screenY) / Gdx.graphics.getHeight();
        startX = screenX;
        startY = screenY;
        return process(deltaX, deltaY);
    }

    @Override
    public boolean scrolled(int amount) {
        if (amount > 0 || camera.position.z > bottom){
            camera.translate(tmpV1.set(camera.direction).scl(amount * (-0.8f)));
        }
        updateDistanceCamFocus();
        return false;
    }

    public void update() {
        if (focused && !Gdx.input.isKeyPressed(ctrlKey)) {
            camera.position.set(distanceCamFocus).add(focus);
        }
        int lArrow = Input.Keys.DPAD_LEFT;
        if (keyPressed.contains(lArrow)) {
            if (!focused) {
                camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(-translateUnits));
            } else {
                camera.rotateAround(focus, new Vector3(0, 0, 1), -rotateUnit);
            }
        }
        int rArrow = Input.Keys.DPAD_RIGHT;
        if (keyPressed.contains(rArrow)) {
            if (!focused) {
                camera.translate(tmpV1.set(camera.direction).crs(camera.up).nor().scl(translateUnits));
            } else {
                camera.rotateAround(focus, new Vector3(0, 0, 1), rotateUnit);
            }
        }
        int uArrow = Input.Keys.DPAD_UP;
        if (keyPressed.contains(uArrow)) {
            if (!focused) {
                camera.translate(tmpV2.set(camera.up).scl(translateUnits));
            } else if (camera.up.z >= 0) {
                Vector3 normal = new Vector3(camera.up).rotate(camera.direction, 90);
                camera.rotateAround(focus, normal, -rotateUnit);
            }
        }
        int dArrow = Input.Keys.DPAD_DOWN;
        if (keyPressed.contains(dArrow) && camera.position.z > bottom) {
            if (!focused) {
                camera.translate(tmpV2.set(camera.up).scl(-translateUnits));
            } else {
                Vector3 normal = new Vector3(camera.up).rotate(camera.direction, 90);
                camera.rotateAround(focus, normal, rotateUnit);
            }
        }
        updateDistanceCamFocus();
        camera.update();
    }

    private void updateDistanceCamFocus() {
        distanceCamFocus.set(camera.position).sub(focus);
    }
}

