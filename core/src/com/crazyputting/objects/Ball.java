package com.crazyputting.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.awt.*;


public class Ball {
    private final float mass = 0.045f; //in kilograms
    private final float diameter = 0.65f; //in meters
    public Vector3 position;
    private boolean colliding = false;
    private boolean stopped = false;
    private Texture ballTexture = new Texture("ball.jpg");
    private Vector3 velocity;
    private ModelInstance ball;

    public Ball(Vector3 initPosition) {
        position = initPosition;
        velocity = new Vector3(0, 0, 0);
        ballCreator();
    }

    public void hit(Vector3 initialHit) {
        stopped = false;
        colliding = true;
        velocity = initialHit.cpy();
    }

    public void update(float z) {
        position.z = z;
        ball.transform.setTranslation(position.x, position.y, position.z);
    }


    private void ballCreator() {
        ModelBuilder builder = new ModelBuilder();
        Model sphere = builder.createSphere(diameter, diameter, diameter,
                50, 50,
                new Material(TextureAttribute.createDiffuse(ballTexture)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );

        ball = new ModelInstance(sphere, position.x, position.y, position.z + (diameter / 2));
    }

    public void setStopped() {
        velocity = new Vector3(0, 0, 0);
        this.stopped = true;
    }

    public void updateInstance(float z, float displacement) {
        if (displacement != 0) colliding = false;
        position.z = z;
        ball.transform.setTranslation(position.x, position.y, position.z + (diameter));
    }

    public float getMass() {
        return mass;
    }

    public double getDiameter() {
        return diameter;
    }

    public boolean isColliding() {
        return colliding;
    }

    public boolean isStopped() {
        return stopped;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3 newVelocity) {
        this.velocity = newVelocity;
    }

    public Vector3 getPosition() {
        return position;
    }

    public ModelInstance getModel() {
        return ball;
    }
}
