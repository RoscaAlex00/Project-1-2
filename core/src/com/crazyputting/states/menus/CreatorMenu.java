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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CreatorMenu extends GameState {

    private SpriteBatch spriteBatch;
    private Stage stage;
    private Skin skin;
    private BitmapFont comicFont;
    private boolean error;

    public static float FRICTION_COEFFICIENT = 1.5f;
    public static float MAXIMUM_VELOCITY = 15;

    public CreatorMenu(GameStateManager gsm) {
        super(gsm);
    }

    /**
     * Declares all the buttons, textfields etc. only once
     */
    @Override
    public void init() {
        //for debugging reasons these have to be placed inside the method
        final String[] SOLVER_STRING = new String[]{"Euler", "Verlet", "Runge-Kutta", "Adams-Bashforth"};
        final Array<String> SOLVERS = new Array<>(SOLVER_STRING);
        final String[] PLAYER_STRING = new String[]{"Human", "AI", "AlexAI", "FrunzAI", "AStar", "WindAi"};
        final Array<String> PLAYERS = new Array<>(PLAYER_STRING);

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
        final HorizontalGroup start = new HorizontalGroup();
        final HorizontalGroup goal = new HorizontalGroup();
        final HorizontalGroup fieldSize = new HorizontalGroup();
        final HorizontalGroup solverAndPlayer = new HorizontalGroup();
        final HorizontalGroup checkBoxes = new HorizontalGroup();

        LinkedList<HorizontalGroup> horizontalGroupList = new LinkedList<>();
        horizontalGroupList.add(start);
        horizontalGroupList.add(goal);
        horizontalGroupList.add(fieldSize);
        horizontalGroupList.add(solverAndPlayer);
        horizontalGroupList.add(checkBoxes);

        HashMap<String, String> labelsAndValuesStart = new HashMap<>();
        labelsAndValuesStart.put("                       Start X = ", "10");
        labelsAndValuesStart.put("      Start Y = ", "10");
        addToHorizontalGroupWithField(start, labelsAndValuesStart);

        HashMap<String, String> labelsAndValuesGoal = new HashMap<>();
        labelsAndValuesGoal.put("    Goal X = ", "20");
        labelsAndValuesGoal.put("     Goal Y = ", "20");
        labelsAndValuesGoal.put("     Goal Radius: ", "1");
        addToHorizontalGroupWithField(goal, labelsAndValuesGoal);

        Label functionLabel = new Label("Function of Terrain: ", skin);
        final TextField functionField = new TextField("0", skin);

        HashMap<String, String> labelsAndValuesField = new HashMap<>();
        labelsAndValuesField.put("                  Field Length: ", "50");
        labelsAndValuesField.put("    Field Width: ", "50");
        addToHorizontalGroupWithField(fieldSize, labelsAndValuesField);

        HashMap<String, Array<String>> labelsAndValuesSolverAndPlayer = new HashMap<>();
        labelsAndValuesSolverAndPlayer.put("        Solver:  ", SOLVERS);
        labelsAndValuesSolverAndPlayer.put("                    Player: ", PLAYERS);
        addToHorizontalGroupWithSelectBox(solverAndPlayer, labelsAndValuesSolverAndPlayer);

        final LinkedList<String> labelsCheckboxes = new LinkedList<>();
        labelsCheckboxes.add("          Wind Enabled: ");
        labelsCheckboxes.add("          Maze Enabled: ");
        labelsCheckboxes.add("          Seasons Enabled: ");
        addToHorizontalGroupWithCheckBox(checkBoxes, labelsCheckboxes);

        ChangeListener listener = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float goalX = 0, goalY = 0, goalRadius = 0, teeX = 0, teeY = 0;
                int length = 0, width = 0;

                String goalXString = goal.getChild(2).toString();
                String goalYString = goal.getChild(4).toString();
                String goalRadiusString = goal.getChild(6).toString();

                String teeXString = start.getChild(2).toString();
                String teeYString = start.getChild(4).toString();

                String courseLengthString = fieldSize.getChild(2).toString();
                String courseWidthString = fieldSize.getChild(4).toString();

                String functionFieldString = functionField.toString();

                String selectedSolver = ((SelectBox<String>) solverAndPlayer.getChild(2)).getSelected();
                String selectedPlayer = ((SelectBox<String>) solverAndPlayer.getChild(4)).getSelected();

                boolean windEnabled = ((CheckBox) checkBoxes.getChild(2)).isChecked();
                boolean mazeEnabled = ((CheckBox) checkBoxes.getChild(4)).isChecked();
                boolean seasonsEnabled = ((CheckBox) checkBoxes.getChild(6)).isChecked();

                //Makes sure that fields have been filled in
                error = goalXString.isEmpty() || goalYString.isEmpty() || goalRadiusString.isEmpty() ||
                        functionFieldString.isEmpty() || courseWidthString.isEmpty() ||
                        courseLengthString.isEmpty() || teeXString.isEmpty() || teeYString.isEmpty();

                //Converts text to a value, otherwise the user needs to enter the values again
                try {
                    teeX = Float.parseFloat(teeXString.replaceAll(" ", ""));
                    teeY = Float.parseFloat(teeYString.replaceAll(" ", ""));
                    goalX = Float.parseFloat(goalXString.replaceAll(" ", ""));
                    goalY = Float.parseFloat(goalYString.replaceAll(" ", ""));
                    goalRadius = Float.parseFloat(goalRadiusString.replaceAll(" ", ""));
                    width = Integer.parseInt(courseWidthString.replaceAll(" ", ""));
                    length = Integer.parseInt(courseLengthString.replaceAll(" ", ""));
                }
                catch (Exception e) {
                    errorScreen("Not all fields contain proper values (they all need to be real, and the fieldsize needs to be an integer)");
                }
                Function function = new Derivatives(functionFieldString);
                if(function.evaluateHeight(teeX, teeY) < -0.10f || function.evaluateHeight(goalX,goalY) < -0.10f ){
                    errorScreen("Ball or Hole in WATER!");
                }
                if (!error) {
                    PhysicsSolver solver = SolverFactory.get().makeSolver(selectedSolver);
                    Player player = PlayerFactory.get().makePlayer(selectedPlayer, MAXIMUM_VELOCITY);
                    Vector3 teeVector = new Vector3(teeX, teeY, 0);
                    Vector3 holeVector = new Vector3(goalX, goalY, 0);
                    Hole hole = new Hole(goalRadius, holeVector);

                    Terrain newTerrain = new Terrain(length, width, teeVector, hole, function, FRICTION_COEFFICIENT,
                            MAXIMUM_VELOCITY, "newTerrain", solver, player,windEnabled,mazeEnabled,seasonsEnabled);
                    gsm.setTerrain(newTerrain);
                    gsm.setState(GameStateManager.PLAY);
                }
            }
        };
        TextButton playButton = new TextButton("Play", skin);
        playButton.addListener(listener);

        //Add all groups to the main table
        int SPACE_BETWEEN_BUTTONS = 20;
        int alignment = Align.left;
        main.row();
        for (int i = 0; i < horizontalGroupList.size(); i++) {
            if (i == 3){
                alignment = Align.center;
            }
            main.add(horizontalGroupList.get(i)).fillY().align(alignment);
            main.row().pad(SPACE_BETWEEN_BUTTONS, 0, SPACE_BETWEEN_BUTTONS, 0);
            if (i == 1){
                main.add(functionLabel).fillY().align(Align.center);
                main.row().pad(SPACE_BETWEEN_BUTTONS, 0, SPACE_BETWEEN_BUTTONS, 0);
                main.add(functionField).fillY().align(Align.center).width(300);
                main.row().pad(SPACE_BETWEEN_BUTTONS, 0, SPACE_BETWEEN_BUTTONS, 0);
            }
        }
        main.add(playButton).align(Align.center);
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

    private void addToHorizontalGroupWithField(HorizontalGroup group, Map<String, String> labelStringAndInitValue){
        for (Map.Entry<String, String> entry : labelStringAndInitValue.entrySet()) {
            Label label = new Label(entry.getKey(), skin);
            TextField textField = new TextField(entry.getValue(), skin);
            group.addActor(label);
            group.addActor(textField);
        }
    }

    private void addToHorizontalGroupWithSelectBox(HorizontalGroup group, Map<String, Array<String>> labelStringsAndSelectBoxes){
        for (Map.Entry<String, Array<String>> entry : labelStringsAndSelectBoxes.entrySet()){
            Label label = new Label(entry.getKey(), skin);
            SelectBox<String> selectBox = new SelectBox<>(skin);
            selectBox.setItems(entry.getValue());
            group.addActor(label);
            group.addActor(selectBox);
        }
    }

    private void addToHorizontalGroupWithCheckBox(HorizontalGroup group, LinkedList<String> labels){
        for (String element : labels) {
            Label label = new Label(element,skin);
            CheckBox checkBox = new CheckBox("",skin);
            group.addActor(label);
            group.addActor(checkBox);
        }
    }
}
