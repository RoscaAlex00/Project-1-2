package com.crazyputting.player.AI;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Point;
import com.crazyputting.objects.Terrain;
import com.crazyputting.physicsengine.Physics;
import com.crazyputting.player.Player;

import java.util.Random;

public class WindAI implements Player {
    public int numberOfHits = 0;
    private Terrain terrain;
    private Vector3 vec;
    private Vector3 wind;

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public void shot_velocity(Terrain terrain) {
        Ball ball = terrain.getBall();
        Hole hole = terrain.getHole();
        Vector3 start = terrain.getStartPos();

        Vector3 sub = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy());

        float generated1;
        float generated2;

        Point[] pointCollection = new Point[10];
        for (int i = 0; i < pointCollection.length; i++) {
            if (start.dst(hole.getPosition()) < 25f) {
                generated1 = randFloatSmall();
                generated2 = randFloatSmall();
            } else {
                generated1 = randFloatBig();
                generated2 = randFloatBig();
            }
            pointCollection[i] = new Point(generated1, generated2);
            float holeDis = pointCollection[i].holeDisCalc(pointCollection[i], hole);
            float startDis = pointCollection[i].startDisCalc(pointCollection[i], start);
            pointCollection[i].setCumulativeDistance(holeDis + 4 * startDis);
        }

        float minDis = pointCollection[0].getCumulativeDistance();
        for (int i = 1; i < pointCollection.length; i++) {
            if (pointCollection[i].getCumulativeDistance() < minDis) {
                minDis = pointCollection[i].getCumulativeDistance();
            }
        }

        for (Point point : pointCollection) {
            if (point.getCumulativeDistance() == minDis) {
                vec = point.getPointPosition();
                if (terrain.getWindEnabled()) {
                    vec.add(handleWind(estimateWind()));
                }
            }
        }

        Vector3 velocity;
        float THRESHOLD = 2f;
        if (-THRESHOLD < sub.x && sub.x < THRESHOLD && -THRESHOLD < sub.y && sub.y < THRESHOLD) {
            velocity = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy());
            ball.hit(velocity);
            numberOfHits++;
        }
        if (vec.dst(hole.getPosition()) < (terrain.getBall().getPosition().dst(hole.getPosition()))) {
            velocity = vec.sub(terrain.getBall().getPosition().cpy());
            velocity.scl(0.8f);
            ball.hit(velocity);
            numberOfHits++;
        }
    }

    public float randFloatSmall() {
        Random rand = new Random();
        return (rand.nextFloat() * (35 - 0f) + 0f);
    }

    public float randFloatBig() {
        Random rand = new Random();
        return (rand.nextFloat() * (70 - 0f) + 0f);
    }

    public Vector3 estimateWind() {
        float windForceX = wind.x * 0.5f * Physics.DRAG_COEFFICIENT
                * matPower(Ball.DIAMETER / 2.0f, 2);
        float windForceY = wind.y * 0.5f * Physics.DRAG_COEFFICIENT
                * matPower(Ball.DIAMETER / 2.0f, 2);
        Vector3 windForce = new Vector3(windForceX, windForceY, 0f);
        windForce.scl((float) (-1f * terrain.getBall().getVelocity().cpy().len() * Math.PI) * 3);
        return windForce;
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public Vector3 handleWind(Vector3 wind) {
        return new Vector3(-1 * wind.x, -1 * wind.y, 0);
    }

    public void setWind(Vector3 wind) {
        this.wind = wind;
    }

    public float matPower(float base, int power) {
        float matPower = 1f;
        for (int i = 0; i < power; i++) {
            matPower *= base;
        }
        return matPower;
    }
}