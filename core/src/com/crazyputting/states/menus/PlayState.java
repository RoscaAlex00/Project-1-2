package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.crazyputting.engine.Engine;
import com.crazyputting.engine.solver.Euler;
import com.crazyputting.managers.GameInputProcessor;
import com.crazyputting.managers.GameKeys;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.PuttingCourse;
import com.crazyputting.objects.Terrain;

import java.awt.*;
import java.util.HashMap;

public class PlayState extends ThreeDimensional {
    private Engine engine;
    private Ball ball;
    private Hole hole;
    private Stage hud;
    private boolean paused = false;
    private boolean isPushed = false;
    private boolean moving = false;
    protected Terrain terrain;

    private GameStateManager manager;

    public HashMap<String, Label> labels;

    private PuttingCourse course;
    private int hole_number;

    public PlayState(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }

    public PlayState(GameStateManager manager, PuttingCourse course) {
        super(manager, course.getTerrain(0));
        this.course = course;
        hole_number = 1;

        ball = new Ball(terrain.getStartPos().cpy());
        instances.add(ball.getModel());

        this.manager = manager;
    }

    /**
     * Method responsible for creating the menu, this is a part of the requirements of libGDX.
     */
    public void init() {
        super.init();
        this.terrain = super.terrain;
        this.hole = terrain.getHole();
        this.ball = terrain.getBall();

        ball.update(terrain.getFunction().evaluateF(ball.getPosition().x, ball.getPosition().y));
        controller.initFocus(ball.getPosition());

        engine = new Engine(ball, terrain, hole);
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
        handleInput();
        super.update(dt);
        if(isPushed){
            paused = false;
            isPushed = false;
            setProcessors();
        }
        if (!ball.isStopped()) {
            if(!moving){
                moving = true;
            }
            ball.updateInstance(terrain.getFunction().evaluateF(ball.getPosition().x, ball.getPosition().y),engine.updateBall(dt));
        }
        else {
            if(moving){
                moving = false;
                setProcessors();
            }
        }
        if(engine.isGoal()) {
            System.out.println("You WON!");
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            ball.hit(new Vector3(10,10, 0));
        }
    }


    //Setting inputProcessor that processes the key-events and stuff like that.
    public void setProcessors() {
        Gdx.input.setInputProcessor(controller);
    }

    public Ball getBall() {
        return ball;
    }

    public PuttingCourse getCourse() {
        return course;
    }

    @Override
    public void draw() {
            super.draw();
            hud.act();
            hud.draw();
        }

    @Override
    public void handleInput(){
        if (GameKeys.isPressed(GameKeys.UP)){
            hit(1); //TODO: implement hit charge
            System.out.println("HELLO?");
        }
    }

    private void hit(float percentage){
        //TODO: find vector of direction camera looks to
        //TODO: move the ball
        System.out.println(terrain + "Hey");
        ball.setVelocity(new Vector3(15,15,0));
    }


    @Override
    public void dispose() {

    }
}
