package com.crazyputting.player.AI;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Point;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Player;

import java.util.Random;

public class FrunzAI implements Player {
    private Hole hole;
    private Ball ball;
    private Terrain terrain;
    private Vector3 start;
    private Vector3 velocity;
    private Vector3 vec;

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) {
        this.ball = terrain.getBall();
        this.hole = terrain.getHole();
        this.start = terrain.getStartPos();

        float subX = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy()).x;
        float subY = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy()).y;

        Point[] pointCollection = new Point[10];
        for (int i = 0; i < pointCollection.length; i++) {
            float generated1 = randFloat();
            float generated2 = randFloat();
            pointCollection[i] = new Point(generated1, generated2);
            float holeDis = pointCollection[i].holeDisCalc(pointCollection[i], hole);
            float startDis = pointCollection[i].startDisCalc(pointCollection[i], start);
            pointCollection[i].setDisHole(holeDis);
            pointCollection[i].setDisStart(startDis);
            pointCollection[i].setCumDis(holeDis + 4 * startDis);
            System.out.println(pointCollection[i].getCumDis());
        }

        float minDis = pointCollection[0].getCumDis();
        for (int i = 1; i < pointCollection.length; i++) {
            if (pointCollection[i].getCumDis() < minDis) {
                minDis = pointCollection[i].getCumDis();
                System.out.println(minDis);
                System.out.println("best");
            }
        }

        for (Point point : pointCollection) {
            if (point.getCumDis() == minDis) {
                vec = point.getPointPosition();
            }
        }
        if (subX < 2f && subY < 2f && subX > -2f && subY > -2) {
            velocity = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy());
            ball.hit(velocity);
        }
        if (vec.dst(hole.getPosition()) < (terrain.getBall().getPosition().dst(hole.getPosition()))) {
            velocity = vec.sub(terrain.getBall().getPosition().cpy());
            velocity.scl(0.8f);
            ball.hit(velocity);
        }
        return null;
    }

    public float randFloat() {
        Random rand = new Random();
        return (rand.nextFloat() * (35 - 0f) + 0f);

    }

    public float getMin(float[] inputArray) {
        float minVal = inputArray[0];
        for (int i = 1; i < inputArray.length; i++) {
            if (inputArray[i] > minVal) {
                minVal = inputArray[i];
            }
        }
        return minVal;
    }

    @Override
    public void runLoop() {
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
