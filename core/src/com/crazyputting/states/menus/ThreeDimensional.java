package com.crazyputting.states.menus;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
import com.crazyputting.managers.GameInputProcessor;
import com.crazyputting.managers.GameKeys;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.models.HeightField;
import com.crazyputting.models.TerrainModel;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;

public abstract class ThreeDimensional extends GameState {
    public GameCamera controller;
    protected PerspectiveCamera camera;
    protected ArrayList<ModelInstance> instances = new ArrayList<>();
    protected Terrain terrain;
    protected Ball ball;
    private ModelBatch batch;
    private Color bgColor = new Color(.8f, .8f, .8f, 1f);
    private Environment environment;
    private TerrainModel terrainModel;
    private Array<Renderable> fields;
    private SpriteBatch back;
    private Stage stage;
    private Texture img;
    private Image background;

    private boolean current = false;

    public ThreeDimensional(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }

    public void init() {
        back = new SpriteBatch();
        stage = new Stage(new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam),back);
        img = new Texture("newGame.png");
        background = new Image(img);

        batch = new ModelBatch();

        camera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(10f, 0f, 10f);
        camera.lookAt(10, 10, 0);
        camera.near = 0.1f;
        camera.far = 1000f;
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
        if (instances != null) batch.render(instances, environment);
        if (current) batch.render((RenderableProvider) environment);
        else for (Renderable r : fields) batch.render(r);
        batch.render(terrainModel.getEdges(), environment);
        batch.render(terrainModel.getBallModel(), environment);
        batch.end();
    }

    public void draw() {
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


    public void update(float dt) {
        camera.update();
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Terrain getTerrain() {
        return terrain;
    }

}

