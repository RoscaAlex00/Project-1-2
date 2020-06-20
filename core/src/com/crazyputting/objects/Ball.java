package com.crazyputting.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;


public class Ball {
    public static final float DIAMETER = 0.62f; //in meters
    public static final float MASS = 0.15f;

    private final Texture TEXTURE = new Texture("ball.jpg");
    private Vector3 position;
    private boolean stopped = false;
    private boolean isHit = false;
    private Vector3 velocity;
    private ModelInstance ball;
    private Vector3 hitPosition;

    public Ball(Vector3 initPosition) {
        position = initPosition.cpy();
        this.hitPosition = initPosition.cpy();
        velocity = new Vector3(0, 0, 0);
        ballModelCreator();
    }

    public void hit(Vector3 initialHit) {
        this.hitPosition = position.cpy();
        this.stopped = false;
        this.isHit = true;
        this.velocity = initialHit.cpy();
    }

    public void update(float z) {
        position.z = z - (DIAMETER / 2);
        ball.transform.setTranslation(position);
    }

    private void ballModelCreator() {
        ModelBuilder builder = new ModelBuilder();
        Model sphere = builder.createSphere(DIAMETER, DIAMETER, DIAMETER, 50, 50,
                new Material(TextureAttribute.createDiffuse(TEXTURE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates
        );
        ball = new ModelInstance(sphere, position.x, position.y, position.z + (DIAMETER / 2));
    }

    public void setStopped() {
        velocity.set(0, 0, 0);
        this.isHit = false;
        this.stopped = true;
    }

    public void updateInstance(float z, float displacement) {
        position.z = z + (DIAMETER / 2);
        ball.transform.setTranslation(position);
        Vector3 temp = new Vector3();
        final float speed = velocity.len();
        final float angle = speed * displacement * MathUtils.radiansToDegrees;
        Vector3 axis = temp.set(velocity).scl(-1f / speed).scl(displacement).crs(Vector3.Z);
        if (speed > 0.2f) {
            ball.transform.rotate(new Quaternion(axis, angle/50));
        }
    }

    public float getMass() {
        return MASS;
    }


    public boolean isStopped() {
        return stopped;
    }

    public boolean isHit() {
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
    public void setPosition(Vector3 pos) {
        this.position = pos;
    }

    public ModelInstance getModel() {
        return ball;
    }

    public void setPositionToHit() {
        this.position.set(hitPosition);
    }

    public Vector3 getHitPosition(){
        return this.hitPosition;
    }
}
