package com.crazyputting.physicsengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;
import java.util.List;


public class Physics {
    protected final double SPVELOCITY = 0.20;
    protected final double SPACCELERATION = 0.9;
    protected final float GRAVITY = 9.81f;
    private final float GOAL_TOLERANCE = 2f;
    private final float WALL_HIT_FRICTION = -0.95f;
    private final float TREE_HIT_FRICTION = -0.80f;
    private final float TREE_RADIUS = 0.66f;

    private final float WALL_POWER_LOSS = -0.80f;
    private final float TREE_POWER_LOSS = -0.65f;
    float dt = Gdx.graphics.getDeltaTime();
    private Ball ball;
    private Terrain terrain;
    private PhysicsSolver solver;
    private Hole hole;
    private float mass;
    private float radius;
    private ArrayList<Vector3> treeCoordinates;
    private int wallHitCounter;
    private int treeHitCounter;


    public Physics(Ball yourBall, Terrain yourTerrain, Hole newHole, PhysicsSolver solver) {
        this.ball = yourBall;
        this.terrain = yourTerrain;
        this.solver = solver;
        solver.setPhysics(this);
        this.hole = newHole;
        radius = hole.getHoleRadius() - 0.5f;
        mass = yourBall.getMass();
    }

    public float getGRAVITY() {
        return GRAVITY;
    }

    public float getDt() {
        return dt;
    }

    protected void setDt(float dt) {
        this.dt = dt;
    }

    private Vector3 calcGravity(Vector3 position) {
        Vector3 grav = new Vector3();
        grav.x = (-mass * GRAVITY * terrain.getFunction().calcXDeriv(position.x, position.y));
        grav.y = (-mass * GRAVITY * terrain.getFunction().calcYDeriv(position.x, position.y));
        return grav;
    }

    private Vector3 calcFriction(Vector3 velocity) {
        Vector3 v = new Vector3(velocity);
        if (v.len() != 0.0) v.scl(1 / v.len());
        v.scl(-terrain.getMU() * mass * GRAVITY);
        return v;
    }

    public Vector3 getAcceleration(Vector3 position, Vector3 velocity) {
        Vector3 acc = calcGravity(position);
        acc.add(calcFriction(velocity));
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

    public float updateBall(float dt) {
        this.dt = dt;

        solver.setHit(ball.isHit());

        Vector3 position = ball.getPosition();
        Vector3 velocity = ball.getVelocity();

        treeCoordinates = terrain.getTreeCoordinates();

        Vector3 newVel = solver.getSpeed(position.cpy(), velocity.cpy());
        ball.getVelocity().set(newVel.cpy());
        Vector3 newPos = solver.getPosition(position.cpy(), velocity.cpy());
        ball.getPosition().set(newPos);

        if (terrain.getFunction().evaluateF(newPos.x, newPos.y) < -0.10f) {
            terrain.setFrictionCoefficient(4.5f);
        } else {
            terrain.setFrictionCoefficient(1.5f);
        }
        treeCoordinates = terrain.getTreeCoordinates();
        updateBall(newPos, newVel);
        return position.dst(newPos);
    }

    protected void updateBall(Vector3 position, Vector3 velocity) {
        /*
        Check all trees to see if the ball hits the trees. The ball bounces off the tree in a realistic angle losing some speed.
         */
        for (Vector3 treeCoordinate : treeCoordinates) {
            //The ball collides with the tree if the next position of the ball is within the bounds of the tree.
            if (treeCoordinate.x - TREE_RADIUS <= position.x && position.x <= treeCoordinate.x + TREE_RADIUS &&
                    treeCoordinate.y - TREE_RADIUS <= position.y && position.y <= treeCoordinate.y + TREE_RADIUS) {
                setTreeHitCounter(getTreeHitCounter() + 1);
                if (getTreeHitCounter() == 1) {
                    ball.setVelocity(findReflection(ball, treeCoordinate));
                }
            }
        }

        /*
        If the ball goes out of bounds (outside of the playing field),
        inverse and scale the velocity on the axis of the side where it gets out of bounds.
        This way it appears as if the ball bounces off the wall and losing a bit of speed.
         */
        if (position.x <= 0.2f || position.x >= terrain.getWidth() - 0.3f) {
            setWallHitCounter(getWallHitCounter() + 1);
            if (getWallHitCounter() == 1) {
                Vector3 storage = new Vector3(ball.getVelocity().x * WALL_POWER_LOSS,
                        ball.getVelocity().y, 0);
                ball.setStopped();
                ball.hit(storage);
            }
            ball.getVelocity().x *= WALL_HIT_FRICTION;
        }
        if (position.y <= 0.2f || position.y >= terrain.getHeight() - 0.3f) {
            setWallHitCounter(getWallHitCounter() + 1);
            if (getWallHitCounter() == 1) {
                Vector3 storage = new Vector3(ball.getVelocity().x,
                        ball.getVelocity().y * WALL_POWER_LOSS, 0);
                ball.setStopped();
                ball.hit(storage);
            }
            ball.getVelocity().y *= WALL_HIT_FRICTION;
        }
        if (velocity.len() < SPVELOCITY && calcGravity(position).len() < SPACCELERATION) {
            ball.setStopped();
        }
        if (getWallHitCounter() >= 1){
            setWallHitCounter(getWallHitCounter() + 1);
        }
        if(getWallHitCounter() == 4){
            resetWallHitCounter();
        }
        if (getTreeHitCounter() >= 1) {
            setTreeHitCounter(getTreeHitCounter() + 1);
        }
        if (getTreeHitCounter() == 15) {
            resetTreeHitCounter();
        }
        ball.getPosition().z = terrain.getFunction().evaluateF(position.x, position.y);
    }

    /**
     * The reflection at a point can be calculated with the equation r = d - 2(d.n)*n. Where n is the normalized normal vector
     * and d.n is the dot-product between the incoming vector d and n.
     *
     * This equation can be found with the following:
     * d = (d.n)*n + (d - (d.n)*n), d.n is the angle between d and n, thus is a scaled n.
     * r = -(d.n)*n + (d - (d.n)*n) = d - 2(d.n)*n
     *
     * A more detailed explanation can be found on https://math.stackexchange.com/questions/13261/how-to-get-a-reflection-vector
     * and https://www.fabrizioduroni.it/2017/08/25/how-to-calculate-reflection-vector.html
     */
    private Vector3 findReflection(Ball ball, Vector3 tree){
        Vector3 normal = findNormalOfCircleCollision(ball, tree);
        Vector3 ballVelocity = ball.getVelocity().cpy();
        return ballVelocity.sub(normal.scl(2 * normal.dot(ballVelocity))).scl(-TREE_HIT_FRICTION);
    }

    private Vector3 findNormalOfCircleCollision(Ball ball, Vector3 tree){
        return ball.getPosition().cpy().sub(tree.cpy()).nor();
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

    public int getWallHitCounter() {
        return wallHitCounter;
    }

    public void setWallHitCounter(int counter) {
        this.wallHitCounter = counter;
    }

    public void resetWallHitCounter() {
        this.wallHitCounter = 0;
    }
}
