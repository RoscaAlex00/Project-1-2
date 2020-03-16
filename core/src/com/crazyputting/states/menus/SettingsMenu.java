package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyputting.CrazyPutting;
import com.crazyputting.managers.GameStateManager;

public class SettingsMenu extends GameState {
    SpriteBatch myBatch;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private Viewport viewport;
    private Texture img;
    private Image background;
    private BitmapFont comicFont;
    private FreeTypeFontGenerator gen;

    public SettingsMenu(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam);
        viewport.apply();
        gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/SF_Arch_Rival.ttf"));
        img = new Texture("lime.jpg");
        background = new Image(img);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 65;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.BLACK);
        stage = new Stage(viewport, spriteBatch);
        atlas = new TextureAtlas("comic/skin/comic-ui.atlas");
        skin = new Skin(Gdx.files.internal("comic/skin/comic-ui.json"));
        CrazyPutting.cam.update();
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {
        stage.addActor(background);

        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        spriteBatch.begin();
        comicFont.draw(spriteBatch, "Settings", 265, 540);
        spriteBatch.end();

    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {

    }
}
