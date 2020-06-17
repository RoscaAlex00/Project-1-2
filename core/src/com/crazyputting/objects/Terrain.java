package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.player.Player;
import com.crazyputting.physicsengine.PhysicsSolver;
import com.crazyputting.function.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class Terrain {
    private final float height;
    private final float width;
    private final Vector3 startPos;
    private final Hole hole;
    private final String name;
    private final Function yourFunction;
    private final float maximumVelocity;
    private final PhysicsSolver solver;
    private final Player player;
    private float frictionCoefficient;
    private Ball ball;
    private List<Vector3> sandCoordinates;
    private List<Vector3> treeCoordinates;
    //**********
    private List<Vector3> rockCoordinates;


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
        this.sandCoordinates = new ArrayList<>();
        this.solver = solver;
        this.player = player;
        this.ball = new Ball(teeVector);
        treeCoordinates = new ArrayList<>();
        rockCoordinates = new ArrayList<>(); //****************
    }

    public float getHeight() {
        return height;
    }

    public Function getFunction() {
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

    public float getMaximumVelocity() {
        return maximumVelocity;
    }

    public String getName() {
        return name;
    }

    public float getHoleDiameter() {
        return 1.10f;
    }

    public PhysicsSolver getSolver() {
        return solver;
    }

    public Player getPlayer() {
        return player;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public Ball setBall() {
        this.ball = new Ball(startPos.cpy());
        return ball;
    }

    public float getFrictionCoefficient() {
        return frictionCoefficient;
    }
    public void setTreeCoordinates(List<Vector3> newCoords){
        this.treeCoordinates = newCoords;
    }

    public List<Vector3> getTreeCoordinates(){
        return treeCoordinates;
    }

    //***********************
    public void setRockCoordinates(List<Vector3> newCoordi){
        this.rockCoordinates = newCoordi;
    }

    public List<Vector3> getRockCoordinates(){
        return rockCoordinates;
    }


    public void setSandCoordinates(List<Vector3> newCoordinates){
        this.sandCoordinates = newCoordinates;
    }

    public List<Vector3> getSandCoordinates(){
        return sandCoordinates;
    }

    public void setFrictionCoefficient(float frictionCoefficient) {
        this.frictionCoefficient = frictionCoefficient;
    }
}
