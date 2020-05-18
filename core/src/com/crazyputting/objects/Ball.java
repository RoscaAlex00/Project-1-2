package com.crazyputting.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;


public class Ball {
    public final float DIAMETER = 0.62f; //in meters
    private Vector3 position;
    private boolean colliding = false;
    private boolean stopped = false;
    private boolean isHit = false;

    private final Texture TEXTURE = new Texture("ball.jpg");
    private Vector3 velocity;
    private ModelInstance ball;
    private Vector3 initialPosition;

    public Ball(Vector3 initPosition) {
        position = initPosition.cpy();
        this.initialPosition = initPosition.cpy();
        velocity = new Vector3(0, 0, 0);
        ballCreator();
    }

    public void hit(Vector3 initialHit) {
        this.stopped = false;
        this.colliding = true;
        this.isHit = true;
        this.velocity = initialHit.cpy();
    }

    public void update(float z) {
        position.z = z - (DIAMETER/2);
        ball.transform.setTranslation(position);
    }


    private void ballCreator() {
        ModelBuilder builder = new ModelBuilder();
        Model sphere = builder.createSphere(DIAMETER, DIAMETER, DIAMETER,
                50, 50,
                new Material(TextureAttribute.createDiffuse(TEXTURE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );

        ball = new ModelInstance(sphere, position.x, position.y, position.z + (DIAMETER / 2));
    }

    public void setStopped() {
        velocity.set(0,0,0);
        this.isHit = false;
        this.stopped = true;
    }

    public void updateInstance(float z, float displacement) {
        if (displacement != 0) colliding = false;
        position.z = z + (DIAMETER/2);
        ball.transform.setTranslation(position);
    }

    public float getMass() {
        //in kilograms
        return 0.15f;
    }

    public boolean isColliding() {
        return colliding;
    }

    public boolean isStopped() {
        return stopped;
    }

    public boolean isHit(){
        return isHit;
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
    public void setPosition(Vector3 pos){
        this.position = pos;
    }

    public void setPositionToInitial(){
        this.position = initialPosition;
    }
}
