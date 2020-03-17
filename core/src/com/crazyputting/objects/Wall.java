package com.crazyputting.objects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelCache;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

public class Wall {

    private final float thickness = 0.4f;
    private float height = 0.5f;
    private Vector3 v0, v1;
    private ModelInstance wall;

    /**
     * Constructor that uses the default height
     * @param point0
     * @param point1
     */
    public Wall(Vector3 point0, Vector3 point1){
        this.v0 = point0;
        this.v1 = point1;
    }

    public Wall(Vector3 point0, Vector3 point1, float height){
        this.v0 = point0;
        this.v1 = point1;
        this.height = height;
    }

    private void createWall(){
        ModelBuilder builder = new ModelBuilder();
        float width = (float) Math.sqrt((Math.pow((v0.x - v1.x), 2) + Math.pow((v0.y - v1.y), 2)));
        Model box = builder.createBox(width, height*2, thickness,
                new Material(ColorAttribute.createDiffuse(Color.BROWN)), VertexAttributes.Usage.Position
                | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
        wall = new ModelInstance(box, (v0.x + v1.x)/2, (v0.y + v1.y)/2, (v0.z + v1.z)/2);

        double angleRad = Math.asin((v0.x - v1.x)/width);
        float angle = (float) (angleRad * (180/Math.PI));
        wall.transform.rotate(new Vector3(0,0,1), angle);
    }

    public ModelInstance getWall(){ return wall; }

    public float getThickness() { return thickness; }
}
