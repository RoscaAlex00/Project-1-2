package com.crazyputting.threedimensional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;


public class ThreeDimensionalModel {

    private final int DIV_SIZE = 10;
    private final int CHUNK_SIZE = 5;
    private final ModelLoader loader;
    public Array<HeightField> map;
    private Ball ball;
    private ArrayList<ModelInstance> edges;
    private Terrain terrain;
    private int attr;
    private ModelInstance water;
    private ArrayList<ModelInstance> tree;
    private ArrayList<ModelInstance> rock;
    private ArrayList<ModelInstance> maze;
    private ArrayList<Vector3> treeCoordinates;
    private ArrayList<Vector3> rockCoordinates;
    private ArrayList<Vector3> mazeWallsCoordinates;


    public ThreeDimensionalModel(Terrain terrain) {

        this.terrain = terrain;
        this.ball = terrain.getBall();

        edges = new ArrayList<>();
        map = new Array<>();
        loader = new ObjLoader();
        tree = new ArrayList<>();
        treeCoordinates = new ArrayList<>();
        rock = new ArrayList<>();
        rockCoordinates = new ArrayList<>();
        maze = new ArrayList<>();
        mazeWallsCoordinates = new ArrayList<>();

        attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

        createWorld();
    }


    private void createWorld() {
        for (int i = 0; i < terrain.getWidth(); i += CHUNK_SIZE) {
            for (int j = 0; j < terrain.getHeight(); j += CHUNK_SIZE) {
                map.add(createField(i, j));
            }
        }
        ModelBuilder modelBuilder = new ModelBuilder();
        float height_border = 20f;
        float width_border = 1f;

        Model water = modelBuilder.createRect(0, 0, 0, terrain.getWidth(), 0, 0, terrain.getWidth(), terrain.getHeight(), 0,
                0, terrain.getHeight(), 0, 0, 0, 1,
                new Material(TextureAttribute.createDiffuse(new Texture("water.png"))), attr);
        this.water = new ModelInstance(water, 0, 0, -0.12f);

        Texture wood = new Texture("wood.jpg");
        Model border_w = modelBuilder.createBox(terrain.getWidth() + (2 * width_border), width_border, height_border,
                new Material(TextureAttribute.createDiffuse(wood)), attr);
        edges.add(new ModelInstance(border_w, terrain.getWidth() / 2, -width_border / 2, (-height_border / 2) + 5.5f));
        edges.add(new ModelInstance(border_w, terrain.getWidth() / 2, terrain.getHeight() + width_border / 2, (-height_border / 2) + 5.5f));

        Model border_d = modelBuilder.createBox(width_border, terrain.getHeight(), height_border,
                new Material(TextureAttribute.createDiffuse(wood)), attr);
        edges.add(new ModelInstance(border_d, -(width_border / 2), terrain.getHeight() / 2, (-height_border / 2) + 5.5f));
        edges.add(new ModelInstance(border_d, terrain.getWidth() + (width_border / 2), terrain.getHeight() / 2, (-height_border / 2) + 5.5f));

        if (!terrain.getMazeEnabled()) {
            Model treeModel = loader.loadModel(Gdx.files.internal("cartontree.obj"));
            for (int i = 0; i < Math.random() * 10; i++) {
                this.tree.add(new ModelInstance(treeModel));
            }
            for (int i = 0; i < tree.size(); i++) {
                float x = (float) Math.random() * (terrain.getWidth() - 2);
                float y = (float) Math.random() * (terrain.getHeight() - 2);

                if (terrain.getFunction().evaluateHeight(x, y) >= 0) {
                    treeCoordinates.add(new Vector3(x, y, 0));
                    tree.get(i).transform = new Matrix4(new Vector3(x, y, terrain.getFunction().evaluateHeight(x, y)), new Quaternion(new Vector3(1, 1, 1), 120),
                            new Vector3(1.65f, 1.65f, 1.65f));
                } else {
                    tree.remove(i);
                    i--;
                }
            }
            terrain.setTreeCoordinates(treeCoordinates);

            Model rockModel = loader.loadModel(Gdx.files.internal("Rock_1.obj"));
            for (int i = 0; i < Math.random() * 15; i++) {
                this.rock.add(new ModelInstance(rockModel));
            }
            for (int i = 0; i < rock.size(); i++) {
                float x = (float) Math.random() * (terrain.getWidth() - 2);
                float y = (float) Math.random() * (terrain.getHeight() - 2);

                if (terrain.getFunction().evaluateHeight(x, y) >= 0) {
                    rockCoordinates.add(new Vector3(x, y, 0));
                    rock.get(i).transform = new Matrix4(new Vector3(x, y, terrain.getFunction().evaluateHeight(x, y)),
                            new Quaternion(new Vector3(0, 0, 1), (int) (Math.random() * 180)),
                            new Vector3(0.5f, 0.5f, 0.5f));
                } else {
                    rock.remove(i);
                    i--;
                }
            }
            terrain.setRockCoordinates(rockCoordinates);
        }
        //Create a maze
        else {
            Texture brick = new Texture("brick.jpg");
            Model wall1 = modelBuilder.createBox(1.4f, 48, 10,
                    new Material(TextureAttribute.createDiffuse(brick)), attr);
            maze.add(new ModelInstance(wall1, 7, 24, 0));
            mazeWallsCoordinates.add(new Vector3(7,24,0));
            maze.add(new ModelInstance(wall1, 14, 26, 0));
            mazeWallsCoordinates.add(new Vector3(14,26,0));
            maze.add(new ModelInstance(wall1, 21, 26, 0));
            mazeWallsCoordinates.add(new Vector3(21,26,0));
            maze.add(new ModelInstance(wall1, 28, 24, 0));
            mazeWallsCoordinates.add(new Vector3(28,24,0));
            maze.add(new ModelInstance(wall1, 35, 26, 0));
            mazeWallsCoordinates.add(new Vector3(35,26,0));
            maze.add(new ModelInstance(wall1, 42, 24, 0));
            mazeWallsCoordinates.add(new Vector3(42,24,0));
            terrain.setMazeWallCoordinates(mazeWallsCoordinates);
        }

    }


    private HeightField createField(int x, int y) {

        float[] heights = createHeights(x, y);
        HeightField field = new HeightField(true, heights, (CHUNK_SIZE * DIV_SIZE) + 1, (CHUNK_SIZE * DIV_SIZE) + 1, true, attr);
        field.corner00.set(x, y, 0);
        field.corner01.set(x + CHUNK_SIZE, y, 0);
        field.corner10.set(x, y + CHUNK_SIZE, 0);
        field.corner11.set(x + CHUNK_SIZE, y + CHUNK_SIZE, 0);
        field.magnitude.set(0, 0, 1f);
        field.update();

        return field;
    }


    private float[] createHeights(int x0, int y0) {

        float[] heights = new float[((5 * DIV_SIZE) + 1) * ((5 * DIV_SIZE) + 1)];
        int ih = 0;
        float hole_rad = terrain.getHoleDiameter() / 2;
        final float hole_depth = 3;
        for (float i = 0; i <= 5 * DIV_SIZE; i++) {
            for (float j = 0; j <= 5 * DIV_SIZE; j++) {
                if (terrain.getHole().getPosition().dst(new Vector3(x0 + (i / DIV_SIZE), y0 + (j / DIV_SIZE), 0)) <= hole_rad) {
                    heights[ih] = terrain.getFunction().evaluateHeight(x0 + (i / DIV_SIZE), y0 + (j / DIV_SIZE)) - hole_depth;
                } else heights[ih] = terrain.getFunction().evaluateHeight(x0 + (i / DIV_SIZE), y0 + (j / DIV_SIZE));
                ih++;
            }
        }
        return heights;
    }

    public ModelInstance getBallModel() {
        return ball.getModel();
    }

    public ArrayList<ModelInstance> getEdges() {
        return edges;
    }

    public ModelInstance getWater() {
        return water;
    }

    public ArrayList<ModelInstance> getTree() {
        return tree;
    }

    public ArrayList<ModelInstance> getRock() {
        return rock;
    }

    public ArrayList<ModelInstance> getMaze() {
        return maze;
    }

}

