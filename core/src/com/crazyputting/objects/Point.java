package com.crazyputting.objects;

import com.badlogic.gdx.math.Vector3;

public class Point {
    private Vector3 pointPosition;
    private float disHole;
    private float disStart;
    private float cumDis;
    private float x;
    private float y;


    public Point(Vector3 pointPosition){
        this.disHole=0;
        this.disStart=0;
        this.x=pointPosition.x;
        this.y=pointPosition.y;
    }


    public Point(float x,float y){
        this.disHole=0;
        this.disStart=0;
        this.x=x;
        this.y=y;
        this.pointPosition=new Vector3(x,y,0);
    }



    public void setDisStart(float disStart){
        this.disStart=disStart;
    }

    public float getDisStart(){
        return disStart;
    }


    public float getDisHole(){
        return  disHole;
    }

    public void setDisHole(float disHole){
        this.disHole=disHole;
    }

    public float getCumDis(){
        return cumDis;
    }

    public void setCumDis(float cumDis){
        this.cumDis=cumDis;
    }


    public Vector3 getPointPosition(){
        return pointPosition;
    }

    public void setPointPosition(Vector3 pos){
        this.pointPosition=pos;
    }

    public float holeDisCalc(Point p,Hole hole){

        double holeDisxd=Math.pow((hole.getPosition().x-p.x),2);
        double holeDisyd=Math.pow((hole.getPosition().y-p.y),2);
        double holeDisd=holeDisxd+holeDisyd;
        return (float)holeDisd;
    }

    public float startDisCalc(Point p,Vector3 start){
        double startDisxd=Math.pow((start.x-p.x),2);
        double startDisyd=Math.pow((start.y-p.y),2);
        double startDisd=startDisxd+startDisyd;
        return (float)startDisd;
    }


}
