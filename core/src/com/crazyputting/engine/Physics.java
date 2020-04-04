package com.crazyputting.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;


public class Physics {
    protected final double SPVELOCITY = 0.02;
    protected final double SPACCELERATION = 0.9;
    protected final float GRAVITY = 9.81f;
    private final float GOAL_TOLERANCE = 2f;
    float dt = Gdx.graphics.getDeltaTime();
    private Ball ball;
    private Terrain terrain;
    private PhysicsSolver solver;
    private Hole hole;
    private float mass;
    private float radius;


    public Physics(Ball yourBall, Terrain yourTerrain, Hole newHole, PhysicsSolver solver) {
        this.ball = yourBall;
        this.terrain = yourTerrain;
        this.solver = solver;
        solver.setPhysics(this);
        this.hole = newHole;
        radius = hole.getHoleRadius();
        mass = yourBall.getMass();
    }

    public float getGRAVITY() {
        return GRAVITY;
    }

    public float getDt() {
        return dt;
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
        Vector3 hol = hole.getPos().cpy();
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

        Vector3 position = ball.getPosition();
        Vector3 velocity = ball.getVelocity();

        Vector3 temp1 = new Vector3(position);
        Vector3 temp2 = new Vector3(velocity);

        Vector3 newVel = solver.getSpeed(temp1, temp2);
        ball.getVelocity().set(newVel.cpy());
        Vector3 newPos = solver.getPosition(new Vector3(position), new Vector3(velocity));
        ball.getPosition().set(newPos);

        updateBall(newPos, newVel);

        return position.dst(newPos);
    }

    protected void updateBall(Vector3 position, Vector3 velocity) {
        if (velocity.len() < SPVELOCITY && calcGravity(position).len() < SPACCELERATION) {
            ball.setStopped();
        }
        ball.getPosition().z = terrain.getFunction().evaluateF(position.x, position.y);
    }

}
