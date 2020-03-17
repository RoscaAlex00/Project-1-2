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
        parameter.size = 40;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.BLACK);
        stage = new Stage(viewport, spriteBatch);
        atlas = new TextureAtlas("comic/skin/comic-ui.atlas");
        skin = new Skin(Gdx.files.internal("comic/skin/comic-ui.json"));
        CrazyPutting.cam.update();
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        final Table goal = new Table();
        HorizontalGroup goalRadius = new HorizontalGroup();
        HorizontalGroup fieldSize = new HorizontalGroup();

        Label labelGoalX = new Label("Goal  X = ", skin);
        final TextField fieldGoalX = new TextField("5", skin);
        Label labelGoalY = new Label("Goal Y = ", skin);
        final TextField fieldGoalY = new TextField("0", skin);

        Label labelGoalRadius = new Label("Goal Radius: ", skin);
        final TextField fieldGoalRadius = new TextField("0.5", skin);

        Label labelCourseWidth = new Label("Width of field: ", skin);
        final TextField fieldCourseWidth = new TextField("50", skin);
        Label labelCourseDepth = new Label("Depth of field: ", skin);
        final TextField fieldCourseDepth = new TextField("50", skin);
        final TextField functionField = new TextField("50", skin);
        Label labelFunction = new Label("Function : ", skin);

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float goalX = 0, goalY = 0, goalRadius = 0;
                int depth = 0, width = 0;
                boolean error;
                error = fieldGoalX.toString().isEmpty() || fieldGoalY.toString().isEmpty() ||
                        functionField.toString().isEmpty() || fieldCourseWidth.toString().isEmpty() ||
                        fieldCourseDepth.toString().isEmpty();
                try {
                    goalX = Float.parseFloat(fieldGoalX.getText().replaceAll(" ", ""));
                    goalY = Float.parseFloat(fieldGoalY.getText().replaceAll(" ", ""));
                    goalRadius = Float.parseFloat(fieldGoalRadius.getText().replaceAll(" ", ""));
                    width = Integer.parseInt(fieldCourseWidth.getText().replaceAll(" ", ""));
                    depth = Integer.parseInt(fieldCourseDepth.getText().replaceAll(" ", ""));
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
                    Vector3 holeVector = new Vector3(goalX, goalY, 0);
                    Terrain newTerrain = new Terrain(depth, width, new Vector3(0, 0, 0),
                            new Hole(goalRadius, holeVector), new Derivatives(functionField.getText())
                            , "newTerrain");
                    gsm.setTerrain(newTerrain);
                    gsm.setState(GameStateManager.PLAY);
                }
            }
        };

        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(listener);

        goal.add(labelGoalX);
        goal.add(fieldGoalX);
        goal.add(labelGoalY);
        goal.add(fieldGoalY);
        goal.row().pad(10, 0, 10, 0);
        goal.add(labelFunction);
        goal.add(functionField);

        fieldSize.addActor(labelCourseDepth);
        fieldSize.addActor(fieldCourseDepth);
        fieldSize.addActor(labelCourseWidth);
        fieldSize.addActor(fieldCourseWidth);

        playButton.setX(playButton.getX() + 50);

        table.add(goal);
        table.row().pad(10, 0, 10, 0);
        table.add(fieldSize);
        table.row();
        table.add(playButton);
        table.setY(table.getY() + 80);

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
        comicFont.draw(spriteBatch, "Creator Menu", 265, 560);
        spriteBatch.end();
    }

    @Override
    public void handleInput() {

    }

    @Override
    public void dispose() {

    }
}
