package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyputting.CrazyPutting;
import com.crazyputting.managers.GameStateManager;

public class CreatorMenu extends GameState{

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private Viewport viewport;
    private Texture img;
    private Image background;
    private BitmapFont comicFont;
    private FreeTypeFontGenerator gen;

    private Label labelGoalX;
    private TextField fieldGoalX;
    private Label labelGoalY;
    private TextField fieldGoalY;
    private Label labelGoalRadius;
    private TextField fieldGoalRadius;

    public CreatorMenu(GameStateManager gsm) { super(gsm); }

    @Override
    public void init() {

        spriteBatch = new SpriteBatch();
        viewport = new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam);
        viewport.apply();
        gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/SF_Arch_Rival.ttf"));
        img = new Texture("lime.jpg");
        background = new Image(img);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.BLACK);
        stage = new Stage(viewport, spriteBatch);
        atlas = new TextureAtlas("comic/skin/comic-ui.atlas");
        skin = new Skin(Gdx.files.internal("comic/skin/comic-ui.json"));
        CrazyPutting.cam.update();
        Gdx.input.setInputProcessor(stage);
        Table table = new Table();

        labelGoalX = new Label("Goal  X = ", skin);
        fieldGoalX = new TextField("5", skin);
        labelGoalY = new Label("Goal  Y = ", skin);
        fieldGoalY = new TextField("0", skin);
        labelGoalRadius = new Label("Goal Radius: ", skin);
        fieldGoalRadius = new TextField("0.5", skin);

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                launch();
            }
        });

        table.add(labelGoalX);
        table.add(fieldGoalX);
        table.row().pad(20,0,20,0);
        table.add(labelGoalY);
        table.add(fieldGoalY);
        table.row().pad(20,0,20,0);
        table.add(labelGoalRadius);
        table.add(fieldGoalRadius);
        table.row();
        table.add(playButton);

        table.setFillParent(true);
        stage.addActor(background);
        stage.addActor(table);

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
        spriteBatch.begin();
        comicFont.draw(spriteBatch, "Creator Menu", 265, 540);
        spriteBatch.end();
    }

    @Override
    public void handleInput() {

    }

    public void launch(){
        System.out.println("hello");
        float goalX, goalY, goalRadius;
        boolean error;
        error = fieldGoalX.toString().isEmpty() || fieldGoalY.toString().isEmpty() ||
                fieldGoalRadius.toString().isEmpty();
        if (!error){
            try {
                goalX = Float.parseFloat(fieldGoalX.getText().replaceAll(" ",""));
                goalY = Float.parseFloat(fieldGoalY.getText().replaceAll(" ",""));
                goalRadius = Float.parseFloat(fieldGoalRadius.getText().replaceAll(" ",""));
            }
            catch (Exception e){
                //TODO: add alert for illegal parsing
                error = true;
            }
            if (!error){
                gsm.setState(GameStateManager.PLAY);
            }
        }
        else{
            //TODO: add Alert for having an empty field
        }
    }

    @Override
    public void dispose() {

    }
}
