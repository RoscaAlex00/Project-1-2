package com.crazyputting.function;

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
    public float evaluateHeight(float f, float g) {
        return 0;
    }
}
