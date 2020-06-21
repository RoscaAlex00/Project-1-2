package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.physicsengine.PhysicsSolver;
import com.crazyputting.physicsengine.SolverFactory;
import com.crazyputting.player.*;
import com.crazyputting.CrazyPutting;
import com.crazyputting.function.Derivatives;
import com.crazyputting.function.Function;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.states.gamestates.GameState;

import java.util.LinkedList;

public class CreatorMenu extends GameState {

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private BitmapFont comicFont;
    private boolean error = true;

    public CreatorMenu(GameStateManager gsm) {
        super(gsm);
    }

    /**
     * Declares all the buttons, textfields etc. only once
     */
    @Override
    public void init() {
        final Array<String> SOLVERS = new Array<>(new String[]{"Euler", "Verlet", "Runge-Kutta", "Adams-Bashforth"});
        final Array<String> PLAYERS = new Array<>(new String[]{"Human", "AI", "AlexAI", "FrunzAI", "AStar", "WindAi"});

        spriteBatch = new SpriteBatch();
        Viewport viewport = new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam);
        viewport.apply();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/PAC-FONT.ttf"));
        Texture img = new Texture("creatormenu.jpg");
        Image background = new Image(img);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 45;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.WHITE);
        stage = new Stage(viewport, spriteBatch);
        skin = new Skin(Gdx.files.internal("cloud-form/skin/cloud-form-ui.json"));
        CrazyPutting.cam.update();
        Gdx.input.setInputProcessor(stage);

        Table main = new Table();
        Table start = new Table();
        final Table goal = new Table();
        Table function = new Table();
        Table fieldSize = new Table();
        Table solverAndPlayer = new Table();
        Table checkBoxes = new Table();

        LinkedList<Actor> actors = new LinkedList<>();

        Label startXLabel = new Label("Start X = ", skin);
        final TextField startXField = new TextField("10", skin);
        Label startYLabel = new Label("Start Y = ", skin);
        final TextField startYField = new TextField("10", skin);

        start.add(startXLabel);
        int HORIZONTAL_ACTORS_DISTANCE = 20;
        start.add(startXField).padRight(HORIZONTAL_ACTORS_DISTANCE);
        start.add(startYLabel);
        start.add(startYField);
        actors.add(start);

        Label goalXLabel = new Label("Goal X = ", skin);
        final TextField goalXField = new TextField("20", skin);
        Label goalYLabel = new Label("Goal Y = ", skin);
        final TextField goalYField = new TextField("20", skin);
        Label goalRadiusLabel = new Label("Goal Radius: ", skin);
        final TextField goalRadiusField = new TextField("1", skin);

        goal.add(goalXLabel);
        goal.add(goalXField).padRight(HORIZONTAL_ACTORS_DISTANCE);
        goal.add(goalYLabel);
        goal.add(goalYField).padRight(HORIZONTAL_ACTORS_DISTANCE);
        goal.add(goalRadiusLabel);
        goal.add(goalRadiusField);
        actors.add(goal);

        Label functionLabel = new Label("Function of Terrain: ", skin);
        final TextField functionField = new TextField("0", skin);

        function.add(functionLabel);
        int VERTICAL_ACTORS_DISTANCE = 20;
        function.row().padTop(VERTICAL_ACTORS_DISTANCE);
        int FUNCTION_FIELD_SIZE = 300;
        function.add(functionField).width(FUNCTION_FIELD_SIZE);
        actors.add(function);

        Label courseLengthLabel = new Label("Field Length: ", skin);
        final TextField courseLengthField = new TextField("50", skin);
        Label courseWidthLabel = new Label("Field Width: ", skin);
        final TextField courseWidthField = new TextField("50", skin);

        fieldSize.add(courseLengthLabel);
        fieldSize.add(courseLengthField).padRight(HORIZONTAL_ACTORS_DISTANCE);
        fieldSize.add(courseWidthLabel);
        fieldSize.add(courseWidthField);
        actors.add(fieldSize);

        Label solverLabel = new Label("Solver:  ", skin);
        final SelectBox<String> solverSelect = new SelectBox<>(skin);
        solverSelect.setItems(SOLVERS);
        Label playerLabel = new Label("Player: ", skin);
        final SelectBox<String> playerSelect = new SelectBox<>(skin);
        playerSelect.setItems(PLAYERS);

        solverAndPlayer.add(playerLabel);
        solverAndPlayer.add(playerSelect).padRight(HORIZONTAL_ACTORS_DISTANCE);
        solverAndPlayer.add(solverLabel);
        solverAndPlayer.add(solverSelect);
        actors.add(solverAndPlayer);

        Label windLabel = new Label("Wind Enabled: ",skin);
        final CheckBox windCheck = new CheckBox("",skin);
        Label mazeLabel = new Label("Maze Enabled: ",skin);
        final CheckBox mazeCheck = new CheckBox("",skin);
        Label seasonsLabel = new Label("Seasons Enabled: ",skin);
        final CheckBox seasonsCheck = new CheckBox("",skin);

        checkBoxes.add(mazeLabel);
        checkBoxes.add(mazeCheck).padRight(HORIZONTAL_ACTORS_DISTANCE);
        checkBoxes.add(windLabel);
        checkBoxes.add(windCheck).padRight(HORIZONTAL_ACTORS_DISTANCE);
        checkBoxes.add(seasonsLabel);
        checkBoxes.add(seasonsCheck);
        actors.add(checkBoxes);

        TextButton playButton = new TextButton("Play", skin);
        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float goalX = 0, goalY = 0, goalRadius = 0, startX = 0, startY = 0;
                Function function = new Derivatives(functionField.getText());
                int length = 0, width = 0;
                boolean windEnabled = windCheck.isChecked();
                boolean mazeEnabled = mazeCheck.isChecked();
                boolean seasonsEnabled = seasonsCheck.isChecked();
                String selectedSolver = solverSelect.getSelected();
                String selectedPlayer = playerSelect.getSelected();
                float FRICTION_COEFFICIENT = 1.5f;
                float MAXIMUM_VELOCITY = 15;

                //Makes sure that fields have been correctly filled in
                error = goalXField.toString().isEmpty() || goalYField.toString().isEmpty() ||
                        functionField.toString().isEmpty() || courseWidthField.toString().isEmpty() ||
                        courseLengthField.toString().isEmpty();
                //Converts text to a value, otherwise the user needs to enter the values again
                try {
                    startX = Float.parseFloat(startXField.getText().replaceAll(" ", ""));
                    startY = Float.parseFloat(startYField.getText().replaceAll(" ", ""));
                    goalX = Float.parseFloat(goalXField.getText().replaceAll(" ", ""));
                    goalY = Float.parseFloat(goalYField.getText().replaceAll(" ", ""));
                    goalRadius = Float.parseFloat(goalRadiusField.getText().replaceAll(" ", ""));
                    width = Integer.parseInt(courseWidthField.getText().replaceAll(" ", ""));
                    length = Integer.parseInt(courseLengthField.getText().replaceAll(" ", ""));
                } catch (Exception e) {
                    errorScreen("Not all fields contain proper values (they all need to be real, and the field size needs to be an integer)");
                }
                if(function.evaluateHeight(startX,startY) < -0.10f || function.evaluateHeight(goalX,goalY) < -0.10f ){
                    errorScreen("Ball or Hole in water! (in a coordinate lower than 0)");
                }
                if (!error) {
                    PhysicsSolver solver = SolverFactory.get().makeSolver(selectedSolver);
                    Player player = PlayerFactory.get().makePlayer(selectedPlayer, MAXIMUM_VELOCITY);
                    Vector3 teeVector = new Vector3(startX, startY, 0);
                    Vector3 holeVector = new Vector3(goalX, goalY, 0);
                    Hole hole = new Hole(goalRadius, holeVector);

                    Terrain newTerrain = new Terrain(length, width, teeVector, hole, function, FRICTION_COEFFICIENT,
                            MAXIMUM_VELOCITY, "newTerrain", solver, player, windEnabled, mazeEnabled, seasonsEnabled);
                    gsm.setTerrain(newTerrain);
                    gsm.setState(GameStateManager.PLAY);
                }
            }
        };
        playButton.addListener(listener);
        actors.add(playButton);

        for (Actor actor : actors) {
            main.row().pad(VERTICAL_ACTORS_DISTANCE, 0, VERTICAL_ACTORS_DISTANCE, 0);
            main.add(actor).fillY().align(Align.center);
        }
        main.setY(main.getY());

        main.setFillParent(true);
        stage.addActor(background);
        stage.addActor(main);
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
        Gdx.gl.glClearColor(.1f, .12f, .16f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
        spriteBatch.begin();

        comicFont.draw(spriteBatch, "Creator Menu", 245, 720);
        spriteBatch.end();
    }

    /**
     * Not used yet for this menu
     */
    @Override
    public void handleInput() {

    }

    /**
     * Not used yet for this menu
     */
    @Override
    public void dispose() {

    }

    /**
     * Screen that shows when a field is incorrectly accessed
     * @param errorText the text that is required
     */
    private void errorScreen(String errorText){
        TextButton buttonOK = new TextButton("Ok", skin);
        Label errorLabel = new Label(errorText, skin);
        errorLabel.setColor(Color.RED);

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

        parseError.getContentTable().add(errorLabel).pad(20, 20, 20, 20);
        parseError.getButtonTable().add(buttonOK).width(50).height(50).pad(20, 20,
                20, 20);
        parseError.show(stage).setPosition(200, 200);
        error = true;
    }
}
