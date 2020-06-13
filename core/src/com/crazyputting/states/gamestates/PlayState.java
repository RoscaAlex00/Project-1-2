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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
import com.crazyputting.player.AI.AlexAI;
import com.crazyputting.player.AI.Population;
import com.crazyputting.player.astar.AStar;

public class PlayState extends ThreeDimensional {
    protected Terrain terrain;
    private Physics physics;
    private Ball ball;
    private Hole hole;
    private Player player;
    private SpriteBatch spriteBatch;
    private Stage hud;
    private Skin skin;
    private BitmapFont comicFont;
    private Image chargeBar;
    private Image chargeMeter;
    private boolean isPushed = false;
    private boolean moving = false;
    private boolean playerIsHuman = false;

    private boolean isSpacePressed = false;
    private long startChargeTime;
    private int hitCounter = 0;

    private GameStateManager manager;

    public PlayState(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }

    public void init() {
        super.init();
        this.terrain = super.terrain;
        this.hole = terrain.getHole();
        this.ball = terrain.getBall();
        this.player = terrain.getPlayer();
        PhysicsSolver solver = terrain.getSolver();

        spriteBatch = new SpriteBatch();
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("comic/raw/SF_Arch_Rival.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        comicFont = gen.generateFont(parameter);
        comicFont.setColor(Color.WHITE);
        skin = new Skin(Gdx.files.internal("comic/skin/comic-ui.json"));

        ball.update(terrain.getFunction().evaluateF(ball.getPosition().x, ball.getPosition().y));
        controller.initFocus(ball.getPosition());

        physics = new Physics(ball, terrain, hole, solver);
        createHUD();

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
        }
        if (player instanceof AI) {
            player.setTerrain(terrain);
        }
        if (player instanceof AlexAI) {
            player.setTerrain(terrain);
        }
        if (player instanceof AStar) {
            player.setTerrain(terrain);
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
        if (isPushed) {
            isPushed = false;
            setProcessors();
        }
        if (!ball.isStopped()) {
            if (!moving) {
                moving = true;
            }
            ball.updateInstance(terrain.getFunction().evaluateF(ball.getPosition().x, ball.getPosition().y),
                    physics.updateBall(dt));
        } else {
            if (moving) {
                moving = false;
                setProcessors();
            }
        }
        if (ball.isStopped()) {
            if (player instanceof Human) {
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
                player.runLoop();
                ball.setPosition(new Vector3(10, 10, 0));
            } else {
                player.shot_velocity(terrain);
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
        if (isSpacePressed && player instanceof Human) {
            float barIndex = calcMeterPercentage();
            chargeBar.setX(7 + (barIndex * (chargeMeter.getWidth() - chargeBar.getWidth() * 0.055f)));
        }
        super.draw();
        hud.act();
        hud.draw();

        if (player instanceof Human) {
            spriteBatch.begin();
            comicFont.draw(spriteBatch, "Hit Counter : " + hitCounter, 872, 760);
            comicFont.draw(spriteBatch, "Shot Charge :", 15, 50);
            spriteBatch.end();
        }
    }

    @Override
    public void handleInput() {
    }

    @Override
    public void dispose() {
    }
}
