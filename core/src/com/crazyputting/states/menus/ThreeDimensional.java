package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.crazyputting.camera.GameCamera;
import com.crazyputting.managers.GameInputProcessor;
import com.crazyputting.managers.GameKeys;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.models.HeightField;
import com.crazyputting.models.TerrainModel;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;

public abstract class ThreeDimensional extends GameState {
    protected PerspectiveCamera camera;
    protected ArrayList<ModelInstance> instances = new ArrayList<>();
    protected Terrain terrain;
    protected Ball ball;
    public GameCamera controller;
    private ModelBatch batch;
    private Color bgColor = new Color(.8f, .8f, .8f, 1f);
    private Environment environment;
    private TerrainModel terrainModel;
    private Array<Renderable> fields;
    private ArrayList<ModelInstance> skeleton;


    private boolean hideWalls = false;
    private boolean showSkeleton = false;
    private boolean current = false;

    public ThreeDimensional(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }

    /**
     * Creates a model batch with the environment's properties and camera properties.
     */
    public void init() {
        batch = new ModelBatch();

        //camera setup
        camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 0f, 10f);
        camera.lookAt(10, 10, 0);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

        Gdx.input.setInputProcessor(controller = new GameCamera(camera));

        //environment setup
        DirectionalLight light = new DirectionalLight();
        light.set(.8f, .8f, .8f, -1f, -1f, -1f);

        environment = new Environment();
        environment.clear();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.3f, 0.3f, 0.3f, 1.f));
        environment.add(light);

        createTerrain();
    }

    public abstract void pause();

    public abstract void resume();

    /**
     * Shows on the screen all the different instances of the game
     *
     * @param instances instances of the game
     */
    public void render(final ArrayList<ModelInstance> instances) {
        batch.begin(camera);
        if (instances != null) batch.render(instances, environment);
        if (current) batch.render((RenderableProvider) environment);
        else for (Renderable r : fields) batch.render(r);
        batch.render(terrainModel.getEdges(), environment);
        batch.render(terrainModel.getBallModel(), environment);
        batch.end();
    }

    public void draw() {
        controller.update();

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glClearColor(bgColor.r, bgColor.g, bgColor.b, bgColor.a);

        update(Gdx.graphics.getDeltaTime());
        render(instances);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    /**
     * Creates the game's field.
     */
    public void createTerrain() {
        this.terrain = super.terrain;
        terrainModel = new TerrainModel(terrain);
        Array<HeightField> hf = terrainModel.map;

        fields = new Array<>();
        for (int i = 0; i < hf.size; i++) {
            Renderable field = new Renderable();
            field.environment = environment;
            field.meshPart.mesh = hf.get(i).mesh;
            field.meshPart.primitiveType = GL20.GL_TRIANGLES;
            field.meshPart.offset = 0;
            field.meshPart.size = hf.get(i).mesh.getNumIndices();
            field.meshPart.update();
            field.material = new Material(TextureAttribute.createDiffuse(new Texture("grass.jpg")));
            fields.add(field);
        }
    }


        public void update ( float dt){
            camera.update();
        }

        public PerspectiveCamera getCamera () {
            return camera;
        }

        public Terrain getTerrain () {
            return terrain;
        }

        protected void toggleSkeleton () {
            if (!showSkeleton) skeleton = terrainModel.generateSkeleton();
            showSkeleton = !showSkeleton;
        }


    }

