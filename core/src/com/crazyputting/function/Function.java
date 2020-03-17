package com.crazyputting.function;

import com.badlogic.gdx.math.Vector3;

public interface Function {

        float calcXDeriv(float x,float y);
        float calcYDeriv(float x,float y);
        double evaluate(Vector3 pos);
        float evaluateF(float f, float g);
}
