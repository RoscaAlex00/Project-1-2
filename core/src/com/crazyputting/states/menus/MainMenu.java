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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyputting.CrazyPutting;
import com.crazyputting.managers.GameKeys;
import com.crazyputting.managers.GameStateManager;

public class MainMenu extends GameState {
    private final String title = "Crazy Putting";
    public int currentItem = 0;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private Viewport viewport;
    private Texture img;
    private Image background;
    private BitmapFont comicFont;
    private FreeTypeFontGenerator gen;

    public MainMenu(GameStateManager gsm) {
        super(gsm);
    }

    @Override
    public void init() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam);
        viewport.apply();
        gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/SF_Arch_Rival.ttf"));
        img = new Texture("mainMenu.jpg");
        background = new Image(img);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 55;
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
        Table table = new Table();
        TextButton newGame = new TextButton("Play", skin);
        TextButton preferences = new TextButton("Settings", skin);
        TextButton exit = new TextButton("Exit", skin);
        table.add(newGame).fillX().uniformX();
        table.row().pad(50, 0, 50, 0);
        table.add(preferences).fillX().uniformX();
        table.row();
        table.add(exit).fillX().uniformX();

        table.setFillParent(true);
        stage.addActor(background);

        stage.addActor(table);
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (int i = 0; i < 3; i++) {
            if (currentItem == 0) {
                newGame.setColor(Color.LIME);
            } else {
                newGame.setColor(Color.WHITE);
            }
            if (currentItem == 1) {
                preferences.setColor(Color.LIME);
            } else {
                preferences.setColor(Color.WHITE);
            }
            if (currentItem == 2) {
                exit.setColor(Color.LIME);
            } else {
                exit.setColor(Color.WHITE);
            }

        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        spriteBatch.begin();
        comicFont.draw(spriteBatch, title, 225, 540);
        spriteBatch.end();


    }

    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.UP)) {
            if (currentItem > 0)
                currentItem--;
        }
        if (GameKeys.isPressed(GameKeys.DOWN)) {
            if (currentItem < 2)
                currentItem++;

        }
        if (GameKeys.isPressed(GameKeys.ENTER)) {
            select();
        }
    }

    public void select() {
        if (currentItem == 0) {
            gsm.setState(GameStateManager.PLAY);
        }
        if (currentItem == 1) {
            gsm.setState(GameStateManager.SETTINGS);
        }
        if (currentItem == 2) {
            Gdx.app.exit();
        }
    }

    @Override
    public void dispose() {

    }
}
