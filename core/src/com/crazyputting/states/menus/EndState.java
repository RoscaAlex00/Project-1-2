package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyputting.CrazyPutting;
import com.crazyputting.managers.GameStateManager;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EndState extends GameState {
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

    public EndState(GameStateManager ourGsm) { super(ourGsm); }

    @Override
    public void init() {
        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam);
        viewport.apply();
        gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/SF_Arch_Rival.ttf"));
        img = new Texture("winScreen.jpg");
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

        Table main = new Table();
        /*HorizontalGroup buttons = new HorizontalGroup();

        Label quitLabel = new Label("Would you like to coninue?", skin);
        final TextButton quitButton = new TextButton("Quit", skin);
        final TextButton continueButton = new TextButton("New Game", skin);

        ChangeListener listener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (e.getSource().equals(continueButton)){
                    gsm.setState(GameStateManager.COURSE_CREATOR);
                }
                else if (e.getSource().equals(quitButton)){
                    Gdx.app.exit();
                }
            }
        };

        quitButton.addListener(listener);
        continueButton.addListener(listener);

        buttons.addActor(quitButton);
        buttons.addActor(continueButton);

        main.row().pad(300,0,0,0);
        main.add(quitLabel);
        main.row().pad(20,0,20,0);
        main.add(buttons);*/
        main.setBackground(new TextureRegionDrawable(new TextureRegion(img)));
        main.setFillParent(true);

        stage.addActor(main);
        //TODO: draw label and buttons
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void draw() {
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {

    }
}
