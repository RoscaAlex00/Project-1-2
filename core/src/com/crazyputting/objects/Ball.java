package com.crazyputting.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import java.awt.*;


public class Ball {
    private final float mass = 0.045f; //in kilograms
    private final float diameter = 0.043f; //in meters

    private boolean colliding = false;
    private boolean stopped = false;
    private Texture ballTexture = new Texture("ball.jpeg");

    private Vector3 velocity;
    public Vector3 position;

    private ModelInstance ball;

    /**
     * Gives the initial position of the ball.
     */
    public Ball (Vector3 initPosition){
        position = initPosition;
        ballCreator();
    }

    /**
     * Gets invoked when the ball is hit.
     */
    public void hit(Vector3 initialHit){
        stopped = false;
        velocity = initialHit.cpy();
    }

    public void update(float z){
        position.z = z;
        ball.transform.setTranslation(position.x, position.y, position.z);
    }

    /**
     * Creates a model for the ball
     */
    private void ballCreator(){
        ModelBuilder builder = new ModelBuilder();
        Model sphere = builder.createSphere(diameter, diameter, diameter, 50, 50,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
                | VertexAttributes.Usage.TextureCoordinates);
        ball = new ModelInstance(sphere, position.x, position.y, position.z);
    }

    public void setStopped() { stopped = true; }

    public float getMass() { return mass; }

    public double getDiameter() { return diameter; }

    public boolean isColliding() { return colliding; }

    public boolean isStopped() { return stopped; }

    public Vector3 getVelocity(){ return velocity; }

    public ModelInstance getModel(){ return ball; }
}
