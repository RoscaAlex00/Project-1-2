package com.crazyputting.function;

import com.badlogic.gdx.math.Vector3;

public class BiCubicSplines implements Function {
    private float[][] splineInfo;
    private float[][] xDerivative;
    private float[][] yDerivative;

    @Override
    public float calcXDeriv(float x, float y) {
        return 0;
    }

    @Override
    public float calcYDeriv(float x, float y) {
        return 0;
    }

    @Override
    public double evaluate(Vector3 pos) {
        return 0;
    }

    @Override
    public float evaluateF(float f, float g) {
        return 0;
    }
}
