package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.player.Human;
import com.crazyputting.player.Player;
import com.crazyputting.engine.PhysicsSolver;
import com.crazyputting.function.Function;

import java.util.List;


public class Terrain {
    private final float height;
    private final float width;
    private final Vector3 startPos;
    private final Hole hole;
    private final String name;
    private final Function yourFunction;
    private final float frictionCoefficient;
    private final float maximumVelocity;
    private final PhysicsSolver solver;
    private final Player player;
    private Ball ball;

    public Terrain(float ourHeight, float ourWidth, Vector3 teeVector, Hole endHole, Function function, float MU,
                   float maxSpeed, String name, PhysicsSolver solver, Player player) {
        this.height = ourHeight;
        this.width = ourWidth;
        this.startPos = teeVector;
        this.hole = endHole;
        this.name = name;
        this.yourFunction = function;
        this.frictionCoefficient = MU;
        this.maximumVelocity = maxSpeed;
        this.solver = solver;
        this.player = player;
        this.ball = new Ball(teeVector);
    }

    public float getHeight() {
        return height;
    }

    public Function getFunction(){
        return yourFunction;
    }

    public float getWidth() {
        return width;
    }

    public Vector3 getStartPos() {
        return startPos;
    }

    public Hole getHole() {
        return hole;
    }

    public float getMU() {
        return frictionCoefficient;
    }

    public float getMaximumVelocity(){
        return maximumVelocity;
    }

    public String getName() {
        return name;
    }

    public float getHoleDiameter() {
        return 1.10f;
    }

    public PhysicsSolver getSolver(){
        return solver;
    }

    public Player getPlayer(){
        return player;
    }

    public Ball getBall(){
        return ball;
    }

    public Ball setBall(){
        this.ball = new Ball(startPos.cpy());
        return ball;
    }

    public void setBall(Ball ball){
        this.ball = ball;
    }
}
