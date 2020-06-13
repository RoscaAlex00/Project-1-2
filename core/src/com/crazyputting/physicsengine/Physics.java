package com.crazyputting.physicsengine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;


public class Physics {
    protected final double SPVELOCITY = 0.25;
    protected final double SPACCELERATION = 0.9;
    protected final float GRAVITY = 9.81f;
    private final float GOAL_TOLERANCE = 2f;
    private final float WALL_HIT_FRICTION = -0.95f;
    float dt = Gdx.graphics.getDeltaTime();
    private Ball ball;
    private Terrain terrain;
    private PhysicsSolver solver;
    private Hole hole;
    private float mass;
    private float radius;
    private ArrayList<ArrayList<Float>> treeCoordinates;


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

        Vector3 newVel = solver.getSpeed(position.cpy(), velocity.cpy());
        ball.getVelocity().set(newVel.cpy());
        Vector3 newPos = solver.getPosition(position.cpy(), velocity.cpy());
        ball.getPosition().set(newPos);

        if (newPos.z < -0.04f) {
            terrain.setFrictionCoefficient(3.5f);
        } else {
            terrain.setFrictionCoefficient(1.5f);
        }
        updateBall(newPos, newVel);
        return position.dst(newPos);
    }

    protected void updateBall(Vector3 position, Vector3 velocity) {
        treeCoordinates = terrain.getTreeCoordinates();
        for (ArrayList<Float> treeCoordinate : treeCoordinates) {
            if (treeCoordinate.get(0) - 0.7f <= position.x && position.x <= treeCoordinate.get(0) + 0.7f &&
                    treeCoordinate.get(1) - 0.8f <= position.y && position.y <= treeCoordinate.get(1) + 0.8f) {
                System.out.println(position.x);
                System.out.println(position.y);
                ball.getVelocity().x *= -0.60f;
                ball.getVelocity().y *= -0.60f;
            } else if (treeCoordinate.get(0) - 0.7f <= position.x && position.x <= treeCoordinate.get(0) + 0.7f) {
                if (treeCoordinate.get(1) - 1f <= position.y && position.y <= treeCoordinate.get(1) + 1f) {
                    ball.getVelocity().x *= -0.60f;
                    ball.getVelocity().y *= -0.60f;
                } else if (treeCoordinate.get(1) - 0.7f <= position.y && position.y <= treeCoordinate.get(1) + 0.7f) {
                    if (treeCoordinate.get(0) - 1f <= position.x && position.x <= treeCoordinate.get(0) + 1f) {
                        ball.getVelocity().x *= -0.60f;
                        ball.getVelocity().y *= -0.60f;
                    }
                }
            }
        }
        if (position.x <= 0.2f || position.x >= terrain.getWidth() - 0.3f) {
            ball.getVelocity().x *= WALL_HIT_FRICTION;
        }
        if (position.y <= 0.2f || position.y >= terrain.getHeight() - 0.3f) {
            ball.getVelocity().y *= WALL_HIT_FRICTION;
        }
        if (velocity.len() < SPVELOCITY && calcGravity(position).len() < SPACCELERATION) {
            ball.setStopped();
        }
        ball.getPosition().z = terrain.getFunction().evaluateF(position.x, position.y);
    }

}
