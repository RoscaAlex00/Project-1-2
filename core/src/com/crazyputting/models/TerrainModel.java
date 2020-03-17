package com.crazyputting.models;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.crazyputting.function.Function;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;


public class TerrainModel {

    private final int DIV_SIZE = 10;
    private final int CHUNK_SIZE = 5;
    public Array<HeightField> map;
    private ArrayList<ModelInstance> edges;
    private Terrain terrain;
    private int attr;


    public TerrainModel(Terrain terrain) {

        this.terrain = terrain;

        edges = new ArrayList<>();
        map = new Array<>();

        attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

        createWorld();
    }


    private void createWorld() {

        for (int i = 0; i < terrain.getWidth(); i += CHUNK_SIZE) {
            for (int j = 0; j < terrain.getHeight(); j += CHUNK_SIZE) {
                map.add(createField(i, j));
            }
        }

    }

    //TODO: Textures for edges


    private HeightField createField(int x, int y) {

        float[] heights = createHeights(x, y, CHUNK_SIZE, CHUNK_SIZE);
        HeightField field = new HeightField(true, heights, (CHUNK_SIZE * DIV_SIZE) + 1, (CHUNK_SIZE * DIV_SIZE) + 1, true, attr);
        field.corner00.set(x, y, 0);
        field.corner01.set(x + CHUNK_SIZE, y, 0);
        field.corner10.set(x, y + CHUNK_SIZE, 0);
        field.corner11.set(x + CHUNK_SIZE, y + CHUNK_SIZE, 0);
        field.color00.set(0, 0, 1, 1);
        field.color01.set(0, 1, 1, 1);
        field.color10.set(1, 0, 1, 1);
        field.color11.set(1, 1, 1, 1);
        field.magnitude.set(0, 0, 1f);
        field.update();

        return field;
    }


    private float[] createHeights(int x0, int y0, int width, int height) {

        float[] heights = new float[((width * DIV_SIZE) + 1) * ((height * DIV_SIZE) + 1)];
        int ih = 0;
        float hole_rad = terrain.getHoleDiameter() / 2;
        final float hole_depth = 3;
        for (float i = 0; i <= width * DIV_SIZE; i++) {
            for (float j = 0; j <= height * DIV_SIZE; j++) {
                if (terrain.getHole().getPos().dst(new Vector3(x0 + (i / DIV_SIZE), y0 + (j / DIV_SIZE), 0)) <= hole_rad) {
                    heights[ih] = terrain.getFunction().evaluateF(x0 + (i / DIV_SIZE), y0 + (j / DIV_SIZE)) - hole_depth;
                } else heights[ih] = terrain.getFunction().evaluateF(x0 + (i / DIV_SIZE), y0 + (j / DIV_SIZE));
                ih++;
            }
        }
        return heights;
    }

    public ArrayList<ModelInstance> generateSkeleton() {
        ArrayList<ModelInstance> skeletons = new ArrayList<>();

        ArrayList<Function> functions = new ArrayList<>();
        functions.add(terrain.getFunction());

        ModelBuilder modelBuilder = new ModelBuilder();
        MeshPartBuilder builder;

        for (Function f : functions) {
            for (int i = 0; i < terrain.getWidth(); i++) {
                for (int j = 0; j < terrain.getHeight(); j++) {
                    for (float k = 0; k < 1 - (1f / DIV_SIZE); k += (1f / DIV_SIZE)) {
                        modelBuilder.begin();
                        builder = modelBuilder.part("line", 1, 3, new Material());
                        builder.setColor(Color.BLACK);
                        builder.line(i + k, j, terrain.getFunction().evaluateF(i + k, j), i + k + (1f / DIV_SIZE), j, f.evaluateF(i + k + (1f / DIV_SIZE), j));
                        skeletons.add(new ModelInstance(modelBuilder.end()));

                        modelBuilder.begin();
                        builder = modelBuilder.part("line", 1, 3, new Material());
                        builder.setColor(Color.BLACK);
                        builder.line(i, j + k, terrain.getFunction().evaluateF(i, j + k), i, j + k + (1f / DIV_SIZE), f.evaluateF(i, j + k + (1f / DIV_SIZE)));
                        skeletons.add(new ModelInstance(modelBuilder.end()));
                    }
                }
            }
        }
        return skeletons;
    }


    public ArrayList<ModelInstance> getEdges() {
        return edges;
    }
}
