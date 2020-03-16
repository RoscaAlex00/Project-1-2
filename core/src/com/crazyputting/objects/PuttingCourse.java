package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;
import com.crazyputting.function.Function;

public class PuttingCourse {

    private String name;
    private Function height;
    private Vector3 flag;
    private Vector3 start;
    private double frictionCoefficient;
    private double holeTolerancee;
    final private double maximumVelocity = 3.0; // in meters per second

    public PuttingCourse(Function height,
                         Vector3 flag, Vector3 start, double mu,double holeTolerance,String name) {
        this.height = height;
        this.flag = flag;
        this.start = start;
        this.name=name;
        this.holeTolerancee =holeTolerance;
        this.frictionCoefficient = mu;
    }

    public double get_height(Vector3 pos) {
        return height.evaluate(pos);
    }

    public Vector3 get_flag_position() {
        return flag;
    }

    public Vector3 get_start_position() {
        return start;
    }

    public double get_friction_coefficient() {
        return frictionCoefficient;
    }

    public double get_maximum_velocity() {
        return maximumVelocity;
    }

    public double get_hole_tolerance() {
        return holeTolerancee;
    }

    public String get_Name() {
        return name;
    }
}
