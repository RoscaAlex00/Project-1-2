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
    private int treeHitCounter = 0;
    private int wallHitCounter = 0;


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
        for (Vector3 treeCoordinate : treeCoordinates) {
            if (treeCoordinate.x - 0.66f <= position.x && position.x <= treeCoordinate.x + 0.66f &&
                    treeCoordinate.y - 0.66f <= position.y && position.y <= treeCoordinate.y + 0.66f) {
                setTreeHitCounter(getTreeHitCounter() + 1);
                if (getTreeHitCounter() == 1) {
                    Vector3 storage = new Vector3(ball.getVelocity().x * TREE_POWER_LOSS,
                            ball.getVelocity().y * TREE_POWER_LOSS, 0);
                    ball.setStopped();
                    ball.hit(storage);
                }
            }
        }
        if (position.x <= 0.2f || position.x >= terrain.getWidth() - 0.3f) {
            setWallHitCounter(getWallHitCounter() + 1);
            if (getWallHitCounter() == 1) {
                Vector3 storage = new Vector3(ball.getVelocity().x * WALL_POWER_LOSS,
                        ball.getVelocity().y, 0);
                ball.setStopped();
                ball.hit(storage);
            }
        }
        if (position.y <= 0.2f || position.y >= terrain.getHeight() - 0.3f) {
            setWallHitCounter(getWallHitCounter() + 1);
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
