package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.function.Function;

import java.util.ArrayList;

public class PuttingCourse {

    private ArrayList<Terrain> course;
    private String name;
    private int[] scores;

    /**
     * Takes properties from the course class
     * @param course
     */
    public PuttingCourse(ArrayList<Terrain> course, String name) {
        this.course = course;
        this.name = name;
        scores = new int[course.size()];
    }

    public PuttingCourse(Terrain terrain){
        course = new ArrayList<>();
        course.add(terrain);
        this.name = terrain.getName();
        scores = new int[1];
    }

    /**
     *Takes properties from the terrain class
     * @param i
     * @return a new course with the desired properties
     */
    public Terrain getTerrain(int i){
        return course.get(i);
    }

    public String getName(){
        return name;
    }

    public int getSize(){
        return course.size();
    }

}
