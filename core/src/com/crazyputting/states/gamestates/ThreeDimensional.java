package com.crazyputting.states.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.crazyputting.CrazyPutting;
import com.crazyputting.camera.GameCamera;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.threedimensional.HeightField;
import com.crazyputting.threedimensional.ThreeDimensionalModel;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class ThreeDimensional extends GameState {
    public GameCamera controller;
    protected PerspectiveCamera camera;
    protected ArrayList<ModelInstance> instances = new ArrayList<>();
    protected Terrain terrain;
    protected Ball ball;
    private ModelBatch batch;
    private final Color bgColor = new Color(.8f, .8f, .8f, 1f);
    private Environment environment;
    private ThreeDimensionalModel threeDimensionalModel;
    private Array<Renderable> fields;
    private Stage stage;
    private Image background;
    private List<Vector3> sandCoords;
    private List<Vector3> dirtCoords;
    private List<Vector3> darkGrassCoords;

    public ThreeDimensional(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }

    public void init() {
        SpriteBatch back = new SpriteBatch();
        stage = new Stage(new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam), back);
        Texture img = new Texture("newGame.png");
        background = new Image(img);
        sandCoords = new ArrayList<>();
        dirtCoords = new ArrayList<>();
        darkGrassCoords = new ArrayList<>();

        batch = new ModelBatch();

        camera = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 0f, 12f);
        camera.lookAt(10, 10, 0);
        camera.near = 0.1f;
        camera.far = 100f;
        camera.update();

        Gdx.input.setInputProcessor(controller = new GameCamera(camera));

        DirectionalLight light = new DirectionalLight();
        light.set(.95f, .95f, .95f, -1f, -1f, -1f);

        environment = new Environment();
        environment.clear();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
        environment.add(light);

        createTerrain();
    }

    public void render(final ArrayList<ModelInstance> instances) {
        batch.begin(camera);
        if (instances != null) {
            batch.render(instances, environment);
        }
        for (Renderable r : fields) {
            batch.render(r);
        }
        batch.render(threeDimensionalModel.getEdges(), environment);
        batch.render(threeDimensionalModel.getWater(), environment);
        batch.render(threeDimensionalModel.getBallModel(), environment);
        if (!terrain.getMazeEnabled()) {
            batch.render(threeDimensionalModel.getTree(), environment);
            batch.render(threeDimensionalModel.getRock(), environment);
        } else {
            batch.render(threeDimensionalModel.getMaze(), environment);
        }
        batch.end();
    }

    public void draw() throws IllegalAccessException {
        controller.update();
        stage.addActor(background);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);
        stage.act();
        stage.draw();

        update(Gdx.graphics.getDeltaTime());
        render(instances);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void createTerrain() {
        this.terrain = super.terrain;
        threeDimensionalModel = new ThreeDimensionalModel(terrain);
        Array<HeightField> hf = threeDimensionalModel.map;

        fields = new Array<>();
        for (int i = 0; i < hf.size; i++) {
            Renderable field = new Renderable();
            field.environment = environment;
            field.meshPart.mesh = hf.get(i).mesh;
            field.meshPart.primitiveType = GL20.GL_TRIANGLES;
            field.meshPart.offset = 0;
            field.meshPart.size = hf.get(i).mesh.getNumIndices();
            field.meshPart.update();

            Vector3 tileCenter = new Vector3(field.meshPart.center.x, field.meshPart.center.y, 0);
            if (!terrain.getMazeEnabled() && !terrain.getSeasonsEnabled()) {
                if (Math.random() <= 0.85 || teeOnTile(tileCenter)) {
                    field.material = new Material(TextureAttribute.createDiffuse(new Texture("grass.jpg")));
                } else {
                    sandCoords.add(tileCenter.cpy());
                    field.material = new Material(TextureAttribute.createDiffuse(new Texture("sand.jpg")));
                    terrain.setSandCoordinates(sandCoords);
                }
            } else if (!terrain.getMazeEnabled() && terrain.getSeasonsEnabled()) {
                int BOUND = 4;
                Random random = new Random();
                int check = random.nextInt(BOUND);

                //if the tee is on the current tile, it has to be grass.
                if (teeOnTile(tileCenter)) {
                    check = BOUND;
                }
                switch (check){
                    case 0:
                        sandCoords.add(tileCenter);
                        field.material = new Material(TextureAttribute.createDiffuse(new Texture("sand.jpg")));
                        terrain.setSandCoordinates(sandCoords);
                        break;
                    case 1:
                        dirtCoords.add(tileCenter);
                        field.material = new Material(TextureAttribute.createDiffuse(new Texture("dirt.jpg")));
                        terrain.setDirtCoordinates(dirtCoords);
                        break;
                    case 2:
                        darkGrassCoords.add(tileCenter);
                        field.material = new Material(TextureAttribute.createDiffuse(new Texture("darkGrass.png")));
                        terrain.setDarkGrassCoordinates(darkGrassCoords);
                        break;
                    default:
                        field.material = new Material(TextureAttribute.createDiffuse(new Texture("grass.jpg")));
                }
            } else {
                field.material = new Material(TextureAttribute.createDiffuse(new Texture("grass.jpg")));
            }
            fields.add(field);
        }
    }

    private boolean teeOnTile(Vector3 tileCenter){
        Vector3 teePos = terrain.getStartPos();
        return tileCenter.x - ThreeDimensionalModel.CHUNK_SIZE/2f <= teePos.x &&
                teePos.x <= tileCenter.x + ThreeDimensionalModel.CHUNK_SIZE/2f &&
                tileCenter.y - ThreeDimensionalModel.CHUNK_SIZE/2f <= teePos.y &&
                teePos.y <= tileCenter.y + ThreeDimensionalModel.CHUNK_SIZE/2f;
    }

    public void update(float dt) throws IllegalAccessException {
        camera.update();
    }

    public Terrain getTerrain() {
        return terrain;
    }

}

