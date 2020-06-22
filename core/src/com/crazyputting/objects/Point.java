package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;

public class Point {
    private final Vector3 pointPosition;
    private float cumulativeDistance;
    private final float x;
    private final float y;

    public Point(float x, float y) {
        this.x = x;
        this.y = y;
        this.pointPosition = new Vector3(x, y, 0);
    }

    public float getCumulativeDistance() {
        return cumulativeDistance;
    }

    public void setCumulativeDistance(float cumulativeDistance) {
        this.cumulativeDistance = cumulativeDistance;
    }

    public Vector3 getPointPosition() {
        return pointPosition;
    }

    public float holeDisCalc(Point p, Hole hole) {
        double holeDisxd = Math.pow((hole.getPosition().x - p.x), 2);
        double holeDisyd = Math.pow((hole.getPosition().y - p.y), 2);
        double holeDisd = holeDisxd + holeDisyd;
        return (float) holeDisd;
    }

    public float startDisCalc(Point p, Vector3 start) {
        double startDisxd = Math.pow((start.x - p.x), 2);
        double startDisyd = Math.pow((start.y - p.y), 2);
        double startDisd = startDisxd + startDisyd;
        return (float) startDisd;
    }
}
