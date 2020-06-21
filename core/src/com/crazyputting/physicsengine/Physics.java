package com.crazyputting.physicsengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Human;

import java.util.List;
import java.util.Random;


public class Physics {
    public static final float TREE_RADIUS = 0.5f;
    public static final float ROCK_RADIUS = 0.35f;
    public static final float FIELD_SQUARE_WIDTH = 5;
    public static final float WALL_LENGTH = 48.4f;
    public static final float WALL_WIDTH = 2f;

    public static final float SAND_FRICTION_COEFFICIENT = 10;
    public static final float DIRT_FRICTION_COEFFICIENT = 6;
    public static final float DARK_GRASS_FRICTION_COEFFICIENT = 2.5f;
    public static final float GRASS_FRICTION_COEFFICIENT = 1.5f;
    public static final float GRAVITY = 9.81f;
    public static final float DRAG_COEFFICIENT = 0.50f;

    protected final double SPVELOCITY = 0.20;
    protected final double SPACCELERATION = 0.9;

    private final float GOAL_TOLERANCE = 2f;
    private final float WALL_POWER_LOSS = -0.80f;
    private final float TREE_POWER_LOSS = -0.65f;
    private final float ROCK_POWER_LOSS = -0.50f;

    float dt = Gdx.graphics.getDeltaTime();

    private Ball ball;
    private Terrain terrain;
    private PhysicsSolver solver;
    private Hole hole;
    private float radius;
    private int wallHitCounter;
    private int treeHitCounter;
    private int rockHitCounter;
    private int mazeWallHitCounter;
    private Vector3 windForce;


    public Physics(Ball yourBall, Terrain yourTerrain, Hole newHole, PhysicsSolver solver) {
        this.ball = yourBall;
        this.terrain = yourTerrain;
        this.solver = solver;
        solver.setPhysics(this);
        this.hole = newHole;
        this.radius = hole.getHoleRadius() - 0.5f;
        if (terrain.getWindEnabled()) {
            float random1 = randSmallFloat();
            float random2 = randSmallFloat();
            float x = (float) (Math.cos(random1 * Math.PI));
            float y = (float) (Math.sin(random2 * Math.PI));
            this.windForce = new Vector3(x, y, 0);
        }
    }

    public float getDt() {
        return dt;
    }

    protected void setDt(float dt) {
        this.dt = dt;
    }

    private Vector3 calcGravity(Vector3 position) {
        Vector3 grav = new Vector3();
        grav.x = (-Ball.MASS * GRAVITY * terrain.getFunction().calcXDeriv(position.x, position.y));
        grav.y = (-Ball.MASS * GRAVITY * terrain.getFunction().calcYDeriv(position.x, position.y));
        return grav;
    }

    private Vector3 calcFriction(Vector3 velocity) {
        Vector3 v = velocity.cpy();
        if (v.len() != 0.0) {
            v.scl(1 / v.len());
        }
        v.scl(-terrain.getFrictionCoefficient() * Ball.MASS * GRAVITY);
        return v;
    }

    private Vector3 calcWind(Vector3 velocity) {
        float windForceX = windForce.x * 0.5f * DRAG_COEFFICIENT
                * matPower(Ball.DIAMETER / 2.0f, 2);
        float windForceY = windForce.y * 0.5f * DRAG_COEFFICIENT
                * matPower(Ball.DIAMETER / 2.0f, 2);
        Vector3 windForceNew = new Vector3(windForceX, windForceY, 0f);

            windForceNew.scl((float) (-1f * velocity.len() * Math.PI) * 2.75f);
        return windForceNew;
    }

    public Vector3 getAcceleration(Vector3 position, Vector3 velocity) {
        Vector3 acc = calcGravity(position);
        acc.add(calcFriction(velocity));
        if (terrain.getWindEnabled()) {
            acc.add(calcWind(velocity));
        }
        return acc;
    }

    public boolean isGoal() {
        Vector3 pos = ball.getPosition().cpy();
        Vector3 vel = ball.getVelocity().cpy();
        Vector3 hol = hole.getPosition().cpy();
        pos.z = 0f;
        vel.z = 0f;
        hol.z = 0f;
        boolean goal = false;
        if ((pos.dst(hol) < radius) && vel.len() < GOAL_TOLERANCE) {
            goal = true;
        }
        return goal;
    }

    public float update(float dt) {
        this.dt = dt;

        solver.setHit(ball.isHit());

        Vector3 position = ball.getPosition();
        Vector3 velocity = ball.getVelocity();

        Vector3 newVel = solver.getSpeed(position.cpy(), velocity.cpy());
        ball.getVelocity().set(newVel.cpy());
        Vector3 newPos = solver.getPosition(position.cpy(), velocity.cpy());
        ball.getPosition().set(newPos.cpy());

        //Alter the friction coefficients according to the terrain
        if (!terrain.getSeasonsEnabled()) {
            if (checkInGroundType(terrain.getSandCoordinates(), newPos)) {
                terrain.setFrictionCoefficient(SAND_FRICTION_COEFFICIENT);
            } else {
                terrain.setFrictionCoefficient(GRASS_FRICTION_COEFFICIENT);
            }
        } else {
            if (checkInGroundType(terrain.getSandCoordinates(), newPos)) {
                terrain.setFrictionCoefficient(SAND_FRICTION_COEFFICIENT);
            } else if (checkInGroundType(terrain.getDirtCoordinates(), newPos)) {
                terrain.setFrictionCoefficient(DIRT_FRICTION_COEFFICIENT);
            } else if (checkInGroundType(terrain.getDarkGrassCoordinates(), newPos)) {
                terrain.setFrictionCoefficient(DARK_GRASS_FRICTION_COEFFICIENT);
            } else {
                terrain.setFrictionCoefficient(GRASS_FRICTION_COEFFICIENT);
            }
        }

        //Do something when the ball gets in the water
        if (terrain.getFunction().evaluateHeight(newPos.x, newPos.y) <= -0.1f) {
            //Option 1
            if (terrain.getPlayer() instanceof Human) {
                ball.setStopped();
                ball.getPosition().set(ball.getHitPosition());
            }
            //option 2
            else {
                ball.setStopped();
                //set the ball back a bit out of the water
                Vector3 outOfWaterPos = newPos.cpy().add(position.cpy().sub(newPos.cpy()).nor().scl(3));
                ball.getPosition().set(outOfWaterPos);
            }
            //update ball for debugging reasons
            ball.hit(new Vector3(0, 0, 0.001f));
        }

        update(newPos, newVel);
        return position.dst(newPos);
    }

    protected void update(Vector3 position, Vector3 velocity) {
        //Collisions for trees, which are considered to have circular trunks
        for (Vector3 treeCoordinate : terrain.getTreeCoordinates()) {
            //The ball collides with the tree if the next position of the ball is within the bounds of the tree.
            if (ballIsCollidingWithCircle(ball, treeCoordinate, TREE_RADIUS)) {
                treeHitCounter++;
                if (getTreeHitCounter() == 1) {
                    ball.setVelocity(findReflection(ball, TREE_POWER_LOSS, findNormalOfCircleCollision(ball, treeCoordinate)));
                }
            }
        }

        //Collisions for rocks, which are considered to have circular bases
        for (Vector3 rockCoordinate : terrain.getRockCoordinates()) {
            if (ballIsCollidingWithCircle(ball, rockCoordinate, ROCK_RADIUS)) {
                rockHitCounter++;
                if (getRockHitCounter() == 1) {
                    ball.setVelocity(findReflection(ball, ROCK_POWER_LOSS, findNormalOfCircleCollision(ball, rockCoordinate)));
                }
            }
        }

        //Collisions for maze walls
        if (terrain.getMazeEnabled()) {
            for (Vector3 mazeWallCoordinate : terrain.getMazeWallCoordinates()) {
                if (mazeWallCoordinate.x - WALL_WIDTH / 2f <= position.x && position.x <= mazeWallCoordinate.x + WALL_WIDTH / 2f
                        && mazeWallCoordinate.y - WALL_LENGTH / 2f <= position.y && position.y <= mazeWallCoordinate.y + WALL_LENGTH / 2f) {
                    mazeWallHitCounter++;
                    if (getMazeWallHitCounter() == 1) {
                        // Create the four coordinates that make up the rectangle
                        Vector3 a = new Vector3(mazeWallCoordinate.x - WALL_WIDTH / 2f, mazeWallCoordinate.y - WALL_LENGTH / 2f, mazeWallCoordinate.z);
                        Vector3 b = new Vector3(mazeWallCoordinate.x - WALL_WIDTH / 2f, mazeWallCoordinate.y + WALL_LENGTH / 2f, mazeWallCoordinate.z);
                        Vector3 c = new Vector3(mazeWallCoordinate.x + WALL_WIDTH / 2f, mazeWallCoordinate.y - WALL_LENGTH / 2f, mazeWallCoordinate.z);
                        Vector3 d = new Vector3(mazeWallCoordinate.x + WALL_WIDTH / 2f, mazeWallCoordinate.y + WALL_LENGTH / 2f, mazeWallCoordinate.z);

                        //Collisions with correct side
                        if (ballCollidesWithLine(position, a, b) || ballCollidesWithLine(position, c, d)) {
                            ball.setVelocity(findReflection(ball, WALL_POWER_LOSS, normalOfLine(a, b)));
                        }
                        if (ballCollidesWithLine(position, a, c) || ballCollidesWithLine(position, b, d)) {
                            ball.setVelocity(findReflection(ball, WALL_POWER_LOSS, normalOfLine(a, c)));
                        }
                    }
                }
            }
        }

        //Collisions for boundary walls
        if (position.x <= 0.2f || position.x >= terrain.getWidth() - 0.3f) {
            wallHitCounter++;
            if (getWallHitCounter() == 1) {
                Vector3 storage = new Vector3(ball.getVelocity().x * WALL_POWER_LOSS,
                        ball.getVelocity().y, 0);
                ball.setStopped();
                ball.hit(storage);
            }
        }
        if (position.y <= 0.2f || position.y >= terrain.getHeight() - 0.3f) {
            wallHitCounter++;
            if (getWallHitCounter() == 1) {
                Vector3 storage = new Vector3(ball.getVelocity().x,
                        ball.getVelocity().y * WALL_POWER_LOSS, 0);
                ball.setStopped();
                ball.hit(storage);
            }
        }

        if (velocity.len() < SPVELOCITY && calcGravity(position).len() < SPACCELERATION) {
            ball.setStopped();
        }

        if (getWallHitCounter() >= 1) {
            setWallHitCounter(getWallHitCounter() + 1);
        }
        if (getWallHitCounter() == 4) {
            resetWallHitCounter();
        }

        if (getMazeWallHitCounter() >= 1) {
            setMazeWallHitCounter(getMazeWallHitCounter() + 1);
        }
        if (getMazeWallHitCounter() == 7) {
            resetMazeWallHitCounter();
        }

        if (getTreeHitCounter() >= 1) {
            setTreeHitCounter(getTreeHitCounter() + 1);
        }
        if (getTreeHitCounter() == 15) {
            resetTreeHitCounter();
        }

        if (getRockHitCounter() >= 1) {
            setRockHitCounter(getRockHitCounter() + 1);
        }
        if (getRockHitCounter() == 15) {
            resetRockHitCounter();
        }
        ball.getPosition().z = terrain.getFunction().evaluateHeight(position.x, position.y);
    }

    /**
     * The reflection at a point can be calculated with the equation r = d - 2(d.n)*n. Where n is the normalized normal vector
     * and d.n is the dot-product between the incoming vector d and n.
     * <p>
     * This equation can be found with the following:
     * d = (d.n)*n + (d - (d.n)*n), d.n is the angle between d and n, thus is a scaled n.
     * r = -(d.n)*n + (d - (d.n)*n) = d - 2(d.n)*n
     * <p>
     * A more detailed explanation can be found on https://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
     * and https://www.fabrizioduroni.it/2017/08/25/how-to-calculate-reflection-vector.html
     */
    private Vector3 findReflection(Ball ball, float powerLoss, Vector3 normal) {
        Vector3 ballVelocity = ball.getVelocity().cpy();
        return ballVelocity.sub(normal.scl(2 * normal.dot(ballVelocity))).scl(-powerLoss);
    }

    private Vector3 findNormalOfCircleCollision(Ball ball, Vector3 obstacle) {
        return ball.getPosition().cpy().sub(obstacle.cpy()).nor();
    }

    private boolean ballIsCollidingWithCircle(Ball ball, Vector3 tree, float radius) {
        float distance = ball.getPosition().dst(tree);
        float ballRadius = Ball.DIAMETER / 2f;
        return distance <= (ballRadius + radius);
    }

    private boolean ballIsInGroundType(Vector3 ball, Vector3 ground) {
        return ground.x - FIELD_SQUARE_WIDTH / 2 <= ball.x && ball.x <= ground.x + FIELD_SQUARE_WIDTH / 2 &&
                ground.y - FIELD_SQUARE_WIDTH / 2 <= ball.y && ball.y <= ground.y + FIELD_SQUARE_WIDTH / 2;
    }

    private boolean checkInGroundType(List<Vector3> groundCoordinates, Vector3 ballPos) {
        for (Vector3 groundCoordinate : groundCoordinates) {
            if (ballIsInGroundType(ballPos, groundCoordinate)) {
                return true;
            }
        }
        return false;
    }

    private boolean ballCollidesWithLine(Vector3 ballPos, Vector3 lineStart, Vector3 lineEnd) {
        float ballRadius = Ball.DIAMETER / 2f;
        Vector3 line = lineEnd.cpy().sub(lineStart.cpy());
        float dstBallCenterToLine = Math.abs(line.y * ballPos.x - line.x * ballPos.y + lineEnd.x * lineStart.y - lineEnd.y * lineStart.x) / line.len();
        return dstBallCenterToLine <= ballRadius;
    }

    private Vector3 normalOfLine(Vector3 lineStart, Vector3 lineEnd) {
        Vector3 line = lineEnd.cpy().sub(lineStart.cpy());
        return line.rotate(new Vector3(0, 0, 1), 90).nor();
    }

    public int getTreeHitCounter() {
        return treeHitCounter;
    }

    public void setTreeHitCounter(int counter) {
        this.treeHitCounter = counter;
    }

    public void resetTreeHitCounter() {
        this.treeHitCounter = 0;
    }

    public int getRockHitCounter() {
        return rockHitCounter;
    }

    public void setRockHitCounter(int counter) {
        this.rockHitCounter = counter;
    }

    public void resetRockHitCounter() {
        this.rockHitCounter = 0;
    }

    public int getMazeWallHitCounter() {
        return mazeWallHitCounter;
    }

    public void setMazeWallHitCounter(int counter) {
        this.mazeWallHitCounter = counter;
    }

    public void resetMazeWallHitCounter() {
        this.mazeWallHitCounter = 0;
    }

    public int getWallHitCounter() {
        return wallHitCounter;
    }

    public void setWallHitCounter(int counter) {
        this.wallHitCounter = counter;
    }

    public void resetWallHitCounter() {
        this.wallHitCounter = 0;
    }

    public float randSmallFloat() {
        Random rand = new Random();
        return (rand.nextFloat() * (1.5f - 0.5f) + 0.5f);
    }

    public float matPower(float base, int power) {
        float matPower = 1f;
        for (int i = 0; i < power; i++) {
            matPower *= base;
        }
        return matPower;
    }

    public Vector3 getWindForce() {
        return windForce;
    }
}
