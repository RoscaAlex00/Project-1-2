package com.crazyputting;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.crazyputting.managers.GameInputProcessor;
import com.crazyputting.managers.GameKeys;
import com.crazyputting.managers.GameStateManager;

import java.io.File;

public class CrazyPutting extends ApplicationAdapter {
    public static int width;
    public static int height;
    public static OrthographicCamera cam;
    private GameStateManager gsm;
    private Music music;
    private float volume = 0.25f;


    @Override
    public void create() {
        music = Gdx.audio.newMusic(Gdx.files.internal("golfmusic.ogg"));
        music.setLooping(true);
        music.setVolume(volume);
        music.play();
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();
        cam = new OrthographicCamera(width, height);
        cam.translate(width / 2f, height / 2f);
        cam.update();
        gsm = new GameStateManager();
        Gdx.input.setInputProcessor(new GameInputProcessor());

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        try {
            gsm.update(Gdx.graphics.getDeltaTime());
            gsm.draw();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        if (GameKeys.isDown(GameKeys.SPACE)) {
            System.out.println("down");
        }
        GameKeys.update();
    }

    @Override
    public void dispose() {
    }
}
