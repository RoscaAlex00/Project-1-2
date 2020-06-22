package com.crazyputting.states.gamestates;

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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.crazyputting.player.*;
import com.crazyputting.physicsengine.Physics;
import com.crazyputting.physicsengine.PhysicsSolver;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.AI.AI;
import com.crazyputting.player.AI.WindAI;

public class PlayState extends ThreeDimensional {
    protected Terrain terrain;
    private Physics physics;
    private Ball ball;
    private Player player;
    private SpriteBatch spriteBatch;
    private Stage hud;
    private BitmapFont comicFont;
    private Image chargeBar;
    private Image chargeMeter;
    private boolean isSpacePressed = false;
    private long startChargeTime;
    private int hitCounter = 0;

    public PlayState(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }

    public void init() {
        super.init();
        this.terrain = super.terrain;
        Hole hole = terrain.getHole();
        this.ball = terrain.getBall();
        this.player = terrain.getPlayer();
        PhysicsSolver solver = terrain.getSolver();

        spriteBatch = new SpriteBatch();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/SF_Arch_Rival.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.WHITE);

        ball.update(terrain.getFunction().evaluateHeight(ball.getPosition().x, ball.getPosition().y));
        controller.initFocus(ball.getPosition());

        physics = new Physics(ball, terrain, hole, solver);
        createHUD();

        //Only if the player is human, create the charge meter
        if (player instanceof Human) {
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
        } else {
            player.setTerrain(terrain);
        }
        //If the player is the windAI, declare the wind
        if (player instanceof WindAI) {
            ((WindAI) player).setWind(physics.getWindForce());
        }

        setProcessors();
    }

    private void createHUD() {
        hud = new Stage(new ScalingViewport(Scaling.fit, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        Table table = new Table();
        table.setFillParent(true);
    }

    @Override
    public void update(float dt) throws IllegalAccessException {
        handleInput();
        super.update(dt);

        // update the ball's model when it's moved
        if (!ball.isStopped()) {
            ball.updateInstance(terrain.getFunction().evaluateHeight(ball.getPosition().x, ball.getPosition().y),
                    physics.update(dt));
        } else {
            // The player shoots the ball here
            if (player instanceof Human) {
                /* The human player can determine the shot-strength by pressing space twice within a certain interval.
                 * They can also determine the direction with the camera angle */
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    isSpacePressed = true;
                    startChargeTime = System.currentTimeMillis();
                }
                if (!Gdx.input.isKeyPressed(Input.Keys.SPACE) && isSpacePressed) {
                    float charge = calcMeterPercentage();
                    isSpacePressed = false;

                    Vector3 cameraDirection = camera.direction.cpy();

                    ball.hit(player.shot_velocity(cameraDirection, charge));
                    hitCounter++;
                }
            } else if (player instanceof AI) {
                ball.setPosition(new Vector3(10, 10, 0));
                hitCounter++;
            } else {
                player.shot_velocity(terrain);
                hitCounter++;
            }
            if (physics.isGoal()) {
                gsm.setState(GameStateManager.END);
            }
        }
    }

    public void setProcessors() {
        Gdx.input.setInputProcessor(controller);
    }

    public Ball getBall() {
        return ball;
    }

    /**
     * The shot-strength goes back and forth from 0 to 100% and back in 2 seconds
     */
    public float calcMeterPercentage() {
        float chargeTime = (System.currentTimeMillis() - startChargeTime) / 1000.0f; //chargeTime in seconds
        float remainder = chargeTime % 2;
        if ((((int) chargeTime) % 2) == 0) {
            return remainder;
        } else {
            return (2.0f - remainder);
        }
    }

    @Override
    public void draw() throws IllegalAccessException {
        //shows the shotstrength in- and decreasing
        if (isSpacePressed && player instanceof Human) {
            float barIndex = calcMeterPercentage();
            chargeBar.setX(7 + (barIndex * (chargeMeter.getWidth() - chargeBar.getWidth() * 0.055f)));
        }
        super.draw();
        hud.act();
        hud.draw();

        spriteBatch.begin();
        comicFont.draw(spriteBatch, "Hit Counter : " + hitCounter, 872, 760);
        //Only displays the shotcharge text if the player is human.
        if (player instanceof Human) {
            comicFont.draw(spriteBatch, "Shot Charge :", 15, 50);
        }
        spriteBatch.end();
    }

    /**
     * Not used yet for this state
     */
    @Override
    public void handleInput() {
    }

    /**
     * Not used yet for this state
     */
    @Override
    public void dispose() {
    }
}
