package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyputting.CrazyPutting;
import com.crazyputting.function.Derivatives;
import com.crazyputting.function.Function;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;

public class CreatorMenu extends GameState {

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private TextureAtlas atlas;
    private Viewport viewport;
    private Texture img;
    private Image background;
    private BitmapFont comicFont;
    private FreeTypeFontGenerator gen;

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
        parameter.size = 30;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.BLACK);
        stage = new Stage(viewport, spriteBatch);
        atlas = new TextureAtlas("comic/skin/comic-ui.atlas");
        skin = new Skin(Gdx.files.internal("comic/skin/comic-ui.json"));
        CrazyPutting.cam.update();
        Gdx.input.setInputProcessor(stage);

        Table main = new Table();
        HorizontalGroup start = new HorizontalGroup();
        HorizontalGroup goal = new HorizontalGroup();
        HorizontalGroup fieldSize = new HorizontalGroup();
        HorizontalGroup constants = new HorizontalGroup();

        Label startXLabel = new Label("                 Start X = ", skin);
        final TextField startXField = new TextField("10", skin);
        Label startYLabel = new Label("Start Y = ", skin);
        final TextField startYField = new TextField("10", skin);

        Label goalXLabel = new Label("                  Goal X = ", skin);
        final TextField goalXField = new TextField("20", skin);
        Label goalYLabel = new Label("Goal Y = ", skin);
        final TextField goalYField = new TextField("20", skin);
        Label goalRadiusLabel = new Label("Goal Radius: ", skin);
        final TextField goalRadiusField = new TextField("2", skin);

        Label functionLabel = new Label("Function of Terrain: ", skin);
        final TextField functionField = new TextField("0", skin);

        Label courseLengthLabel = new Label("            Fieldlength: ", skin);
        final TextField courseLengthField = new TextField("50", skin);
        Label courseWidthLabel = new Label("Fieldwidth: ", skin);
        final TextField courseWidthField = new TextField("50", skin);

        Label frictionLabel = new Label("Friction coefficient: ", skin);
        final TextField frictionField = new TextField("0.15", skin);
        Label speedLabel = new Label("Maximum speed (in m/s): ", skin);
        final TextField speedField = new TextField("15", skin);

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                /*TODO: add sth that checks if the function has been correctly formatted
                    Or alternatively, sth like a scientific calculator, so the user always puts in a
                    correct value*/
                float goalX = 0, goalY = 0, goalRadius = 0, startX = 0, startY = 0, MU = 0, vMax = 0;
                Function function = new Derivatives(functionField.getText());
                int length = 0, width = 0;
                boolean error;
                error = goalXField.toString().isEmpty() || goalYField.toString().isEmpty() ||
                        functionField.toString().isEmpty() || courseWidthField.toString().isEmpty() ||
                        courseLengthField.toString().isEmpty();
                try {
                    startX = Float.parseFloat(startXField.getText().replaceAll(" ", ""));
                    startY = Float.parseFloat(startYField.getText().replaceAll(" ", ""));
                    goalX = Float.parseFloat(goalXField.getText().replaceAll(" ", ""));
                    goalY = Float.parseFloat(goalYField.getText().replaceAll(" ", ""));
                    goalRadius = Float.parseFloat(goalRadiusField.getText().replaceAll(" ", ""));
                    width = Integer.parseInt(courseWidthField.getText().replaceAll(" ", ""));
                    length = Integer.parseInt(courseLengthField.getText().replaceAll(" ", ""));
                    MU = Float.parseFloat(frictionField.getText().replaceAll(" ", ""));
                    vMax = Float.parseFloat(speedField.getText().replaceAll(" ", ""));
                }
                catch (Exception e) {
                    TextButton buttonOK = new TextButton("Ok", skin);
                    Label labelError0 = new Label("Not all fields contain real values.", skin);
                    labelError0.setColor(Color.RED);

                    final Dialog parseError = new Dialog("", skin);
                    parseError.setWidth(300);
                    parseError.setHeight(200);
                    parseError.setModal(true);

                    buttonOK.addListener(new InputListener() {
                        @Override
                        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                            parseError.hide();
                            parseError.cancel();
                            parseError.remove();
                            return true;
                        }
                    });

                    parseError.getContentTable().add(labelError0).pad(20, 20, 20, 20);
                    parseError.getButtonTable().add(buttonOK).width(50).height(50).pad(20, 20,
                            20, 20);
                    parseError.show(stage).setPosition(200, 200);
                    error = true;
                }
                if (!error) {
                    Vector3 ballVector = new Vector3(startX, startY, 1);
                    Vector3 holeVector = new Vector3(goalX, goalY, 0);
                    Hole hole = new Hole(goalRadius, holeVector);
                    Terrain newTerrain = new Terrain(length, width, ballVector, hole, function, MU, vMax,"newTerrain");
                    /*PuttingCourse course = new PuttingCourse(newTerrain);
                    gsm.setCourse(course);*/
                    gsm.setTerrain(newTerrain);
                    gsm.setState(GameStateManager.PLAY);
                }
            }
        };

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(listener);

        start.addActor(startXLabel);
        start.addActor(startXField);
        start.addActor(startYLabel);
        start.addActor(startYField);

        goal.addActor(goalXLabel);
        goal.addActor(goalXField);
        goal.addActor(goalYLabel);
        goal.addActor(goalYField);
        goal.addActor(goalRadiusLabel);
        goal.addActor(goalRadiusField);

        fieldSize.addActor(courseLengthLabel);
        fieldSize.addActor(courseLengthField);
        fieldSize.addActor(courseWidthLabel);
        fieldSize.addActor(courseWidthField);

        constants.addActor(frictionLabel);
        constants.addActor(frictionField);
        constants.addActor(speedLabel);
        constants.addActor(speedField);

        main.add(start).fillY().align(Align.left);
        main.row().pad(5, 0, 5, 0);
        main.add(goal).fillY().align(Align.left);
        main.row().pad(5, 0, 5, 0);
        main.add(functionLabel).fillY().align(Align.center);
        main.row().pad(5, 0, 5, 0);
        main.add(functionField).fillY().align(Align.center).width(300);
        main.row().pad(5, 0, 5, 0);
        main.add(fieldSize).fillY().align(Align.left);
        main.row().pad(5, 0, 5, 0);
        main.add(constants).fillY().align(Align.left);
        main.row().pad(5, 0, 5, 0);
        main.add(playButton);
        main.setY(main.getY());

        main.setFillParent(true);
        stage.addActor(background);
        stage.addActor(main);
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

        comicFont.draw(spriteBatch, "Creator Menu", 265, 570);
        spriteBatch.end();
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {

    }
}
