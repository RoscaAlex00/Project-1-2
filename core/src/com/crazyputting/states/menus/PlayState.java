package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.crazyputting.engine.Engine;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.PuttingCourse;
import com.crazyputting.objects.Terrain;

public class PlayState extends ThreeDimensional {
    private Engine engine;
    private Ball ball;
    private Hole hole;

    private SpriteBatch spriteBatch;
    private Stage hud;
    private Skin skin;
    private BitmapFont comicFont;
    private Image chargeBar;
    private Image chargeMeter;

    private boolean paused = false;
    private boolean isPushed = false;
    private boolean moving = false;
    protected Terrain terrain;

    private boolean isSpacePressed = false;
    private long startChargeTime;

    private GameStateManager manager;

    private PuttingCourse course;
    private int hole_number;

    public PlayState(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }

    public PlayState(GameStateManager manager, PuttingCourse course) {
        super(manager, course.getTerrain(0));
        this.course = course;
        hole_number = 1;

        ball = new Ball(terrain.getBall().getPosition().cpy());
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

        spriteBatch = new SpriteBatch();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/SF_Arch_Rival.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.WHITE);
        skin = new Skin(Gdx.files.internal("comic/skin/comic-ui.json"));

        ball.update(terrain.getFunction().evaluateF(ball.getPosition().x, ball.getPosition().y));
        controller.initFocus(ball.getPosition());

        engine = new Engine(ball, terrain, hole);
        createHUD();

        Texture meterImg = new Texture("chargeMeter.jpg");
        chargeMeter = new Image(meterImg);
        chargeMeter.setScaleY(0.2f);
        chargeMeter.setPosition(7, 10);

        Texture barImg = new Texture("chargeBar.jpg");
        chargeBar = new Image(barImg);
        chargeBar.setScale(0.055f);
        chargeBar.setPosition(7, 10);

        hud.addActor(chargeMeter);
        hud.addActor(chargeBar);
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
            ball.updateInstance(terrain.getFunction().evaluateF(ball.getPosition().x, ball.getPosition().y),
                    engine.updateBall(dt));
        }
        else {
            if(moving){
                moving = false;
                setProcessors();
            }
        }
        if (ball.isStopped()) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                isSpacePressed = true;
                startChargeTime = System.currentTimeMillis();
            }
            if (!Gdx.input.isKeyPressed(Input.Keys.SPACE) && isSpacePressed){
                float charge = calcMeterPercentage();
                isSpacePressed = false;
                float reverse = 1;

                Vector3 cameraDirection = camera.direction.cpy();
                if (cameraDirection.z < -0.999849)
                    reverse = -1;
                double hypotenuse = Math.sqrt(Math.pow(cameraDirection.x, 2) + Math.pow(cameraDirection.y, 2));
                float scalingFactor = terrain.getMaximumVelocity() * (float) (1 / hypotenuse);
                ball.hit(cameraDirection.scl(reverse * scalingFactor * charge));
            }
            if(engine.isGoal()) {
                gsm.setState(GameStateManager.END);
            }
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

    public float calcMeterPercentage(){
        float chargeTime = (System.currentTimeMillis() - startChargeTime)/1000.0f; //chargeTime in seconds
        float remainder = chargeTime % 2;
        if ((((int) chargeTime) % 2) == 0) {
            return remainder;
        }
        else {
            return (2.0f - remainder);
        }
    }

    @Override
    public void draw() {
        if (isSpacePressed){
            float barIndex = calcMeterPercentage();
            chargeBar.setX(7 + (barIndex * (chargeMeter.getWidth() - chargeBar.getWidth()*0.055f)));
        }
        super.draw();
        hud.act();
        hud.draw();

        spriteBatch.begin();
        comicFont.draw(spriteBatch, "Shot Charge :", 20, 50);
        spriteBatch.end();
    }

    @Override
    public void handleInput(){
    }


    @Override
    public void dispose() {
    }
}
