package com.crazyputting.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.crazyputting.engine.solver.Euler;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;


public class Engine {
    private final float GOAL_TOLERANCE = 15.0f;
    protected final double STOP_TOLERANCE_VELOCITY = 0.02;
    protected final double STOP_TOLERANCE_ACCELERATION = 0.9;
    private Ball ball;
    private Terrain terrain;
    private Euler solver;
    private Hole hole;
    private float mass;
    private float radius;
    protected final float GRAVITY = 9.81f;
    float dt = Gdx.graphics.getDeltaTime();


    public Engine(Ball yourBall, Terrain yourTerrain, Hole newHole) {
        this.ball = yourBall;
        this.terrain = yourTerrain;
        this.solver = new Euler(this);
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
        grav.x = (-mass*GRAVITY*terrain.getFunction().calcXDeriv(position.x,position.y));
        grav.y = (-mass*GRAVITY*terrain.getFunction().calcYDeriv(position.x,position.y));
        return grav;
    }
    private Vector3 calcFriction(Vector3 velocity) {
        Vector3 v = new Vector3(velocity);
        if(v.len() != 0.0) v.scl(1/v.len());
        v.scl(-terrain.getMU()*mass*GRAVITY);
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
    public float updateBall(float dt){
        this.dt = dt;

        Vector3 position = ball.getPosition();
        Vector3 velocity = ball.getVelocity();

        Vector3 temp1 = new Vector3(position);
        Vector3 temp2 = new Vector3(velocity);

        Vector3 newVel = solver.getSpeed(temp1,temp2);
        ball.getVelocity().set(newVel.cpy());
        Vector3 newPos = solver.getPosition(new Vector3(position),new Vector3(velocity));
        ball.getPosition().set(newPos);

        updateBall(newPos,newVel);

        return position.dst(newPos);
    }
    protected void updateBall(Vector3 position, Vector3 velocity){
        //stop the ball
        if(velocity.len() < STOP_TOLERANCE_VELOCITY && calcGravity(position).len() < STOP_TOLERANCE_ACCELERATION){
            ball.setStopped();
        }

        //check for collisions
        if(position.x <= 0){
            Vector3 n = new Vector3(1,0,0);
            bounce(velocity, n);
            ball.getPosition().x = 0;
        }
        if(position.x >= terrain.getWidth()){
            Vector3 n = new Vector3(-1,0,0);
            bounce(velocity, n);
            ball.getPosition().x =  terrain.getWidth();
        }
        if(position.y <= 0){
            Vector3 n = new Vector3(0,1,0);
            bounce(velocity, n);
            ball.getPosition().y = 0;
        }
        if(position.y >= terrain.getHeight()){
            Vector3 n = new Vector3(0,-1,0);
            bounce(velocity,n);
            ball.getPosition().y = terrain.getHeight();
        }

        //collide();

        ball.getPosition().z = terrain.getFunction().evaluateF(position.x,position.y);
    }

    private void bounce(Vector3 velocity, Vector3 n){
        Vector3 v = new Vector3(velocity);
        float dot = v.dot(n) * 2;
        n.scl(dot);
        n.add(v.scl(-1));
        n.scl(-1);
        ball.getVelocity().set(n);
    }
}
