package com.crazyputting.models;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.crazyputting.objects.Terrain;

import java.util.ArrayList;


	public class TerrainModel {
		
	    private ArrayList<ModelInstance> edges;
	    private final int DIV_SIZE = 10;
	    private final int CHUNK_SIZE = 5;

	    private Terrain terrain;

	    private int attr;

	    public Array<HeightField> map;


	    public TerrainModel(Terrain terrain){
	    	
	        this.terrain = terrain;
	        
	        edges = new ArrayList<>();
	        map = new Array<>();

	        attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;

	        createWorld();
	    }
	   
	    
	    private void createWorld(){
	    	
	        for (int i = 0; i < terrain.getWidth(); i+=CHUNK_SIZE) {
	            for (int j = 0; j < terrain.getHeight(); j+=CHUNK_SIZE) {
	                map.add(createField(i,j));
	            }
	        }

	    }
	    
	    //TODO: Textures for edges

	    
	    
	    private HeightField createField(int x, int y){
	    	
	        float[] heights = createHeights(x,y,CHUNK_SIZE,CHUNK_SIZE);
	        HeightField field = new HeightField(true,heights,(CHUNK_SIZE*DIV_SIZE)+1,(CHUNK_SIZE*DIV_SIZE)+1,true,attr);
	        field.corner00.set(x, y, 0);
	        field.corner01.set(x+CHUNK_SIZE, y, 0);
	        field.corner10.set(x, y+CHUNK_SIZE, 0);
	        field.corner11.set(x+CHUNK_SIZE,y+CHUNK_SIZE,0);
	        field.color00.set(0, 0, 1, 1);
	        field.color01.set(0, 1, 1, 1);
	        field.color10.set(1, 0, 1, 1);
	        field.color11.set(1, 1, 1, 1);
	        field.magnitude.set(0,0,1f);
	        field.update();

	        return field;
	    }

	    
	    private float[] createHeights(int x0, int y0, int width, int height){
	    	
	        float[] heights = new float[((width*DIV_SIZE)+1)*((height*DIV_SIZE)+1)];
	        int ih = 0;
	        float hole_rad = terrain.getHoleDiameter()/2;
	        final float hole_depth = 3;
	        for (float i = 0; i <= width*DIV_SIZE ; i++) {
	            for (float j = 0; j <= height*DIV_SIZE ; j++) {
	                if(terrain.getEndPos().dst(new Vector3(x0+(i/DIV_SIZE),y0+(j/DIV_SIZE),0)) <= hole_rad){
	                    heights[ih] = terrain.getFunction().evaluateF(x0+(i/DIV_SIZE) ,y0+(j/DIV_SIZE))-hole_depth;
	                }
	                else heights[ih] =  terrain.getFunction().evaluateF(x0+(i/DIV_SIZE) ,y0+(j/DIV_SIZE));
	                ih++;
	            }
	        }
	        return heights;
	    }


	    public ArrayList<ModelInstance> getEdges() {
	        return edges;
	    }	    
}
