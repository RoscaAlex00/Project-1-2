package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.player.Player;
import com.crazyputting.physicsengine.PhysicsSolver;
import com.crazyputting.function.Function;
import java.util.ArrayList;
import java.util.List;

public class Terrain {
    private final float height;
    private final float width;
    private final Vector3 startPos;
    private final Hole hole;
    private final Function yourFunction;
    private final PhysicsSolver solver;
    private final Player player;
    private float frictionCoefficient;
    private Ball ball;
    private List<Vector3> sandCoordinates;
    private List<Vector3> treeCoordinates;
    private List<Vector3> rockCoordinates;
    private List<Vector3> mazeWallCoordinates;
    private List<Vector3> dirtCoordinates;
    private List<Vector3> darkGrassCoordinates;
    private final boolean windEnabled;
    private final boolean mazeEnabled;
    private final boolean seasonsEnabled;


    public Terrain(float ourHeight, float ourWidth, Vector3 teeVector, Hole endHole, Function function, float MU,
                   PhysicsSolver solver, Player player, boolean windEnabled, boolean mazeEnabled, boolean seasonsEnabled) {
        this.height = ourHeight;
        this.width = ourWidth;
        this.startPos = teeVector;
        this.hole = endHole;
        this.yourFunction = function;
        this.frictionCoefficient = MU;
        this.sandCoordinates = new ArrayList<>();
        this.solver = solver;
        this.player = player;
        this.ball = new Ball(teeVector);
        this.treeCoordinates = new ArrayList<>();
        this.rockCoordinates = new ArrayList<>();
        this.mazeWallCoordinates = new ArrayList<>();
        this.dirtCoordinates = new ArrayList<>();
        this.darkGrassCoordinates = new ArrayList<>();
        this.windEnabled = windEnabled;
        this.mazeEnabled = mazeEnabled;
        this.seasonsEnabled = seasonsEnabled;
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

    public List<Vector3> getTreeCoordinates() {
        return treeCoordinates;
    }

    public void setTreeCoordinates(List<Vector3> newCoords) {
        this.treeCoordinates = newCoords;
    }

    public float getFrictionCoefficient() {
        return frictionCoefficient;
    }

    public void setFrictionCoefficient(float frictionCoefficient) {
        this.frictionCoefficient = frictionCoefficient;
    }

    public List<Vector3> getSandCoordinates() {
        return sandCoordinates;
    }

    public void setSandCoordinates(List<Vector3> newCoordinates) {
        this.sandCoordinates = newCoordinates;
    }

    public List<Vector3> getRockCoordinates() {
        return rockCoordinates;
    }

    public void setRockCoordinates(List<Vector3> newCoords) {
        this.rockCoordinates = newCoords;
    }

    public List<Vector3> getDirtCoordinates() {
        return dirtCoordinates;
    }

    public void setDirtCoordinates(List<Vector3> newCoordinates) {
        this.dirtCoordinates = newCoordinates;
    }

    public List<Vector3> getDarkGrassCoordinates() {
        return darkGrassCoordinates;
    }

    public void setDarkGrassCoordinates(List<Vector3> newCoordinates) {
        this.darkGrassCoordinates = newCoordinates;
    }

    public List<Vector3> getMazeWallCoordinates() {
        return mazeWallCoordinates;
    }

    public void setMazeWallCoordinates(List<Vector3> newCoords) {
        this.mazeWallCoordinates = newCoords;
    }

    public boolean getWindEnabled() {
        return windEnabled;
    }

    public boolean getMazeEnabled() {
        return mazeEnabled;
    }

    public boolean getSeasonsEnabled() {
        return seasonsEnabled;
    }
}
