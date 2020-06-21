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
import com.crazyputting.physicsengine.Physics;

import java.util.ArrayList;


public class ThreeDimensionalModel {

    private final int DIV_SIZE = 10;
    public static final int CHUNK_SIZE = 5;
    private final ModelLoader loader;
    public Array<HeightField> map;
    private final Ball ball;
    private final ArrayList<ModelInstance> edges;
    private final Terrain terrain;
    private final int attr;
    private ModelInstance water;
    private final ArrayList<ModelInstance> tree;
    private final ArrayList<ModelInstance> rock;
    private final ArrayList<ModelInstance> maze;
    private final ArrayList<Vector3> treeCoordinates;
    private final ArrayList<Vector3> rockCoordinates;
    private final ArrayList<Vector3> mazeWallsCoordinates;


    public ThreeDimensionalModel(Terrain terrain) {
        this.terrain = terrain;
        this.ball = terrain.getBall();
        this.edges = new ArrayList<>();
        this.map = new Array<>();
        this.loader = new ObjLoader();
        this.tree = new ArrayList<>();
        this.treeCoordinates = new ArrayList<>();
        this.rock = new ArrayList<>();
        this.rockCoordinates = new ArrayList<>();
        this.maze = new ArrayList<>();
        this.mazeWallsCoordinates = new ArrayList<>();
        this.attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
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
            //Adds trees at random positions
            Model treeModel = loader.loadModel(Gdx.files.internal("cartontree.obj"));
            for (int i = 0; i < Math.random() * 10; i++) {
                this.tree.add(new ModelInstance(treeModel));
            }
            for (int i = 0; i < tree.size(); i++) {
                float x = (float) Math.random() * (terrain.getWidth() - 2);
                float y = (float) Math.random() * (terrain.getHeight() - 2);
                Vector3 treeCoord = new Vector3(x, y, terrain.getFunction().evaluateHeight(x, y));
                if (!obstacleIsInHoleOrBall(treeCoord, Physics.TREE_RADIUS)) {
                    //Does not spawn tree in water
                    if (terrain.getFunction().evaluateHeight(x, y) >= 0) {
                        treeCoordinates.add(treeCoord.cpy());
                        tree.get(i).transform = new Matrix4(treeCoord.cpy(),
                                new Quaternion(new Vector3(1, 1, 1), 120),
                                new Vector3(1.65f, 1.65f, 1.65f));
                    } else {
                        tree.remove(i);
                        i--;
                    }
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
                Vector3 rockCoord = new Vector3(x, y, terrain.getFunction().evaluateHeight(x, y));
                if (!obstacleIsInHoleOrBall(rockCoord, Physics.ROCK_RADIUS)) {
                    //Rock can't spawn in water
                    if (terrain.getFunction().evaluateHeight(x, y) >= 0) {
                        rockCoordinates.add(rockCoord);
                        rock.get(i).transform = new Matrix4(rockCoord, new Quaternion(new Vector3(0, 0, 1),
                                (int) (Math.random() * 180)), new Vector3(0.5f, 0.5f, 0.5f));
                    } else {
                        rock.remove(i);
                        i--;
                    }
                }
            }
            terrain.setRockCoordinates(rockCoordinates);
        }
        //Create a maze
        else {
            Texture brick = new Texture("brick.jpg");
            Model wall1 = modelBuilder.createBox(1.4f, 48, 10,
                    new Material(TextureAttribute.createDiffuse(brick)), attr);

            addMazeWall(wall1, new Vector3(7, 24, 0));
            addMazeWall(wall1, new Vector3(14,26,0));
            addMazeWall(wall1, new Vector3(21,26,0));
            addMazeWall(wall1, new Vector3(28,24,0));
            addMazeWall(wall1, new Vector3(35,26,0));
            addMazeWall(wall1, new Vector3(42,24,0));
            terrain.setMazeWallCoordinates(mazeWallsCoordinates);
        }
    }

    /**
     * @return Whether the obstacle is in the hole or starting ball
     */
    private boolean obstacleIsInHoleOrBall(Vector3 obstaclePos, float radius){
        Vector3 teeToObstacle = obstaclePos.cpy().sub(terrain.getStartPos());
        teeToObstacle.z = 0;
        Vector3 holeToObstacle = obstaclePos.cpy().sub(terrain.getHole().getPosition());
        holeToObstacle.z = 0;
        return teeToObstacle.len() <= (Ball.DIAMETER/2 + radius) ||
                holeToObstacle.len() <= (terrain.getHole().getHoleRadius() + radius);
    }

    /**
     * Adds the coordinate of the maze wall to the maze list and the mazeWallsCoordinates list
     * @param wallModel model used for maze list
     * @param wallCoord coord tied to the maze wall
     */
    private void addMazeWall(Model wallModel, Vector3 wallCoord){
        maze.add(new ModelInstance(wallModel, wallCoord.cpy()));
        mazeWallsCoordinates.add(wallCoord.cpy());
    }

    /**
     * Creates a tile of the map
     * @param x close-left x-coordinate of tile
     * @param y close-left y-coordinate of tile
     */
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
                if (terrain.getHole().getPosition().dst(new Vector3(x0 + (i / DIV_SIZE),
                        y0 + (j / DIV_SIZE), 0)) <= hole_rad) {
                    heights[ih] = terrain.getFunction().evaluateHeight(x0 + (i / DIV_SIZE),
                            y0 + (j / DIV_SIZE)) - hole_depth;
                }
                else heights[ih] = terrain.getFunction().evaluateHeight(x0 + (i / DIV_SIZE), y0 + (j / DIV_SIZE));
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

