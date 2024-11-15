package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyputting.CrazyPutting;
import com.crazyputting.managers.GameKeys;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.states.gamestates.GameState;

public class MainMenu extends GameState {
    public int currentItem = 0;
    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private Image background;
    private BitmapFont comicFont;
    private final Color selectColor = new Color(178f / 255f, 223f / 255f, 182f / 255f, 1f);
    private final Color unselectedColor = Color.WHITE;
    private TextButton[] textButtons;

    public MainMenu(GameStateManager gsm) {
        super(gsm);
    }

    /**
     * Initial method. Declares font, backgrounds etc. only once
     */
    @Override
    public void init() {
        spriteBatch = new SpriteBatch();
        Viewport viewport = new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam);
        viewport.apply();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/PAC-FONT.ttf"));
        Texture img = new Texture("golfGAME.jpg");
        background = new Image(img);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 72;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.BLACK);
        stage = new Stage(viewport, spriteBatch);
        skin = new Skin(Gdx.files.internal("cloud-form/skin/cloud-form-ui.json"));
        CrazyPutting.cam.update();
        Gdx.input.setInputProcessor(stage);
    }

    /**
     * Not used yet for this menu
     */
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
        textButtons = new TextButton[]{newGame, preferences, exit};

        table.add(textButtons[0]).width(250);
        table.row().pad(20, 0, 20, 0);
        table.add(textButtons[1]).fillX().uniformX();
        table.row();
        table.add(textButtons[2]).fillX().uniformX();

        table.setFillParent(true);
        table.setTransform(true);
        stage.addActor(background);

        stage.addActor(table);
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Sets the colours of the buttons depending on whether they are selected.
        for (int i = 0; i < textButtons.length; i++) {
            if (currentItem == i){
                textButtons[i].setColor(selectColor);
            } else {
                textButtons[i].setColor(unselectedColor);
            }
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        spriteBatch.begin();
        String title = "Crazy Putting";
        comicFont.draw(spriteBatch, title, 100, 700);
        spriteBatch.end();
    }

    /**
     * Handles keyboard inputs to select the correct state.
     */
    @Override
    public void handleInput() {
        if (GameKeys.isPressed(GameKeys.UP)) {
            //Prevents the currentItem from going out of bounds
            if (currentItem > 0) {
                currentItem--;
            }
        }
        if (GameKeys.isPressed(GameKeys.DOWN)) {
            //Prevents the currentItem from going out of bounds
            if (currentItem < (textButtons.length - 1)) {
                currentItem++;
            }
        }
        if (GameKeys.isPressed(GameKeys.ENTER)) {
            select();
        }
    }

    /**
     * Enters the selected state
     */
    private void select() {
        int state = 0;
        switch (currentItem){
            case 0:
                state = GameStateManager.COURSE_CREATOR;
                break;
            case 1:
                state = GameStateManager.SETTINGS;
                break;
            case 2:
                Gdx.app.exit();
                break;
            default:
                break;
        }
        gsm.setState(state);
    }

    /**
     * Not used yet for this menu
     */
    @Override
    public void dispose() {

    }
}
