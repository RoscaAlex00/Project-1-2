package com.crazyputting;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.crazyputting.managers.GameStateManager;

public class CrazyPutting extends ApplicationAdapter {
	Texture img;
	public static int width ;
	public static int height;
	public static OrthographicCamera cam;
	private GameStateManager gsm;
	
	@Override
	public void create () {
		width = Gdx.graphics.getWidth();
		height = Gdx.graphics.getHeight();
		img = new Texture("golf.jpg");
		cam = new OrthographicCamera(width,height);
		cam.translate(width/2,height/2);
		cam.update();
		gsm = new GameStateManager();
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.draw();
	}
	
	@Override
	public void dispose () {
		img.dispose();
	}
}
