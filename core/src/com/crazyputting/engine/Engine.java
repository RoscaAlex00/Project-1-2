package com.crazyputting.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.crazyputting.engine.solver.Euler;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;


public class Engine {
    private Ball ball;
    private Terrain terrain;
    private Euler solver;
    private Hole hole;
    private float mass;
    private float radius;
    protected final float GRAVITY = 9.81f;
    float dt = Gdx.graphics.getDeltaTime();


    public Engine(Ball yourBall, Terrain yourTerrain, Euler yourSolver) {
        this.ball = yourBall;
        this.terrain = yourTerrain;
        this.solver = yourSolver;
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
}
