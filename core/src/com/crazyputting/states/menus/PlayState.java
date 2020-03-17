package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.crazyputting.engine.Engine;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.PuttingCourse;
import com.crazyputting.objects.Terrain;

import java.awt.*;
import java.util.HashMap;

public class PlayState extends ThreeDimensional {
    private Engine engine;
    private Ball ball;
    private Stage hud;
    private boolean paused = false;
    private boolean isPushed = false;
    private boolean isSettings = false;
    private boolean moving = false;

    private GameStateManager manager;

    public HashMap<String, Label> labels;

    private PuttingCourse course;
    private int hole_number;

    public PlayState(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }
}

    /*
    public PlayState(GameStateManager manager, PlayState course, Player player) {
        super(manager, course.getTerrain(0));

        this.course = course;
        hole_number = 1;

        ball = new Ball(terrain.getStart().cpy());
        instances.add(ball.getModel());

        this.player = player;
        player.setState(this);
        this.manager = manager;
    }

    /**
     * Method responsible for creating the menu, this is a part of the requirements of libGDX.
     */
   /* @Override
    public void create() {
        super.create();

        // ball.update(terrain.getFunction().evaluate(ball.getPosition().x, ball.getPosition().y));

        // engine = new Engine(terrain, ball, StateManager.settings.getSolver());
        createHUD();

        setProcessors();
    }

    //Method which creates the HUD for the game.
    private void createHUD() {
        hud = new Stage(new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));

        //used to organise the different input elements
        Table table = new Table();
        table.setFillParent(true);
    }

    /*

    @Override
    public void render() {
    }


    @Override
    public void pause() {
    }

    @Override
    public void resume() {
        paused = !paused;
        setProcessors();
    }

    @Override
    public void update(float dt) {
        super.update(dt);
        if(isPushed){
            paused = false;
            isPushed = false;
            setProcessors();
        }
        if(isSettings){
            isSettings = false;
            Gdx.input.setInputProcessor(new InputMultiplexer(pause,this));
        }
        if (paused) return;
        if (!ball.isStopped()) {
            if(!moving){
                moving = true;
                Gdx.input.setInputProcessor(new InputMultiplexer(this,hud));
            }
            ball.updateInstance(terrain.getFunction().evaluateF(ball.getPosition().x, ball.getPosition().y),engine.updateBall(dt));
        }
        else {
            if(moving){
                moving = false;
                setProcessors();
            }
            player.handleInput(this);
        }
        if(engine.isGoal()) {

        }
        if(player.getHitCount() >= 17) endState(new LoseState(manager,this));
        updateLabels();
    }

    private void updateLabels(){
        labels.get("score").setText("Score: "+String.valueOf(player.getHitCount()));
        if (controller.isFocused()) labels.get("focus").setText("Ball focus ON");
        else labels.get("focus").setText("");
        String dist = "Distance to hole: " + String.valueOf(ball.getPosition().dst(terrain.getHole()) / 10) + "m";
        labels.get("distance").setText(dist);
        labels.get("speed").setText("Speed: " + String.valueOf(ball.getVelocity().len()/10) + "m/s");
    }

    //Setting inputProcessor that processes the key-events and stuff like that.
    public void setProcessors() {
        Gdx.input.setInputProcessor(new InputMultiplexer(hud, player.getInputProcessor(), this, controller));
    }

    public Ball getBall() {
        return ball;
    }

    public PuttingCourse getCourse() {
        return course;
    }


    public void restart(){
        ball.getPosition().set(terrain.getStart().cpy());
        ball.setStopped();
        ball.updateInstance(terrain.getFunction().evaluateF(ball.getPosition().x,ball.getPosition().y),0);
        player.resetCount();
    }

    @Override
    public void init() {

    }


    @Override
    public void draw() {

    }

    @Override
    public void handleInput() {

    }


    @Override
    public void dispose() {

    }
}
