package com.crazyputting.player.astar;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.objects.Ball;
import com.crazyputting.objects.Hole;
import com.crazyputting.objects.Terrain;
import com.crazyputting.player.Player;

public class AStar implements Player {

	private Hole hole;
    private Ball ball;
    private Terrain terrain;
    private Vector3 velocity;

    @Override
    public Vector3 shot_velocity(Vector3 camera_direction, float charge) throws IllegalAccessException {
        throw new IllegalAccessException("This is the wrong class");
    }

    @Override
    public Vector3 shot_velocity(Terrain terrain) {
    	this.ball = terrain.getBall();
        this.hole = terrain.getHole();
        Vector3 holePos = hole.getPosition().cpy();
        Vector3 startPos = terrain.getStartPos();
        Vector3 startToHoleDistance = holePos.sub(startPos);
        startToHoleDistance.z = 0;
        
    	Node startNode = new Node("", startToHoleDistance);
    	Node endNode = new Node("", new Vector3(0, 0, 0));
    	return null;
    }
	
	@Override
    public void runLoop() {
    }

    @Override
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}
