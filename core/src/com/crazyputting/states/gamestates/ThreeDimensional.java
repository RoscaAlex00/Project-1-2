package com.crazyputting.states.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.crazyputting.CrazyPutting;
import com.crazyputting.camera.GameCamera;
import com.crazyputting.managers.GameStateManager;
import com.crazyputting.states.menus.MainMenu;
import com.crazyputting.threedimensional.HeightField;
import com.crazyputting.threedimensional.ThreeDimensionalModel;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class ThreeDimensional extends GameState {
    public GameCamera controller;
    protected PerspectiveCamera camera;
    protected ArrayList<ModelInstance> instances = new ArrayList<>();
    protected Terrain terrain;
    protected Ball ball;
    private ModelBatch batch;
    private Color bgColor = new Color(.8f, .8f, .8f, 1f);
    private Environment environment;
    private ThreeDimensionalModel threeDimensionalModel;
    private Array<Renderable> fields;
    private SpriteBatch back;
    private Stage stage;
    private Texture img;
    private Image background;
    private List<Vector3> sandCoords;
    private List<Vector3> dirtCoords;
    private List<Vector3> darkGrassCoords;

    private boolean current = false;

    public ThreeDimensional(GameStateManager manager, Terrain terrain) {
        super(manager, terrain);
    }

    public void init() {
        back = new SpriteBatch();
        stage = new Stage(new FitViewport(CrazyPutting.width, CrazyPutting.height, CrazyPutting.cam), back);
        img = new Texture("newGame.png");
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
        if (instances != null) batch.render(instances, environment);
        if (current) batch.render((RenderableProvider) environment);
        else for (Renderable r : fields) batch.render(r);
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
            if (!terrain.getMazeEnabled() && !terrain.getSeasonsEnabled()) {
                Vector3 ballStartPos = terrain.getStartPos();
                if (Math.random() <= 0.85 || field.meshPart.center.x - 2.5f <= ballStartPos.x && ballStartPos.x <= field.meshPart.center.x
                        + 2.5f && field.meshPart.center.y - 2.5f <= ballStartPos.y && ballStartPos.y <= field.meshPart.center.y + 2.5f) {
                    field.material = new Material(TextureAttribute.createDiffuse(new Texture("grass.jpg")));
                } else {
                    float x = field.meshPart.center.x;
                    float y = field.meshPart.center.y;
                    sandCoords.add(new Vector3(x, y, 0));
                    field.material = new Material(TextureAttribute.createDiffuse(new Texture("sand.jpg")));
                    terrain.setSandCoordinates(sandCoords);
                }
            } else if (!terrain.getMazeEnabled() && terrain.getSeasonsEnabled()) {
                double check = Math.random();
                if (check <= 0.25) {
                    float x = field.meshPart.center.x;
                    float y = field.meshPart.center.y;
                    sandCoords.add(new Vector3(x, y, 0));
                    field.material = new Material(TextureAttribute.createDiffuse(new Texture("sand.jpg")));
                    terrain.setSandCoordinates(sandCoords);
                } else if (check <= 0.50) {
                    float x = field.meshPart.center.x;
                    float y = field.meshPart.center.y;
                    dirtCoords.add(new Vector3(x, y, 0));
                    field.material = new Material(TextureAttribute.createDiffuse(new Texture("dirt.jpg")));
                    terrain.setDirtCoordinates(dirtCoords);
                } else if (check <= 0.75) {
                    float x = field.meshPart.center.x;
                    float y = field.meshPart.center.y;
                    darkGrassCoords.add(new Vector3(x, y, 0));
                    field.material = new Material(TextureAttribute.createDiffuse(new Texture("darkGrass.png")));
                    terrain.setDarkGrassCoordinates(darkGrassCoords);
                } else {
                    field.material = new Material(TextureAttribute.createDiffuse(new Texture("grass.jpg")));
                }
            } else {
                field.material = new Material(TextureAttribute.createDiffuse(new Texture("grass.jpg")));
            }
            fields.add(field);
        }
    }

    public void update(float dt) throws IllegalAccessException {
        camera.update();
    }

    public PerspectiveCamera getCamera() {
        return camera;
    }

    public Terrain getTerrain() {
        return terrain;
    }

}

