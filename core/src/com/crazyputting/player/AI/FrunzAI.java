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
    public int numberOfHits=0;

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

        float generated1;
        float generated2;

        Point[] pointCollection = new Point[10];
        for (int i = 0; i < pointCollection.length; i++) {
            if(start.dst(hole.getPosition())<25f) {
                generated1 = randFloatSmall();
                generated2 = randFloatSmall();
            }
            else{
                generated1 = randFloatBig();
                generated2 = randFloatBig();

            }
            pointCollection[i] = new Point(generated1, generated2);
            float holeDis = pointCollection[i].holeDisCalc(pointCollection[i], hole);
            float startDis = pointCollection[i].startDisCalc(pointCollection[i], start);
            pointCollection[i].setDisHole(holeDis);
            pointCollection[i].setDisStart(startDis);
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
            }
        }
        if (subX < 2f && subY < 2f && subX > -2f && subY > -2f) {
            velocity = hole.getPosition().cpy().sub(terrain.getBall().getPosition().cpy());
            ball.hit(velocity);
            numberOfHits++;
            System.out.println(numberOfHits);
        }
        if (vec.dst(hole.getPosition()) < (terrain.getBall().getPosition().dst(hole.getPosition()))) {
            velocity = vec.sub(terrain.getBall().getPosition().cpy());
            velocity.scl(0.8f);
            ball.hit(velocity);
            numberOfHits++;
            System.out.println(numberOfHits);
        }
        return null;
    }

    public float randFloatSmall() {
        Random rand = new Random();
        return (rand.nextFloat() * (35 - 0f) + 0f);

    }
    public float randFloatBig() {
        Random rand = new Random();
        return (rand.nextFloat() * (70 - 0f) + 0f);

    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
