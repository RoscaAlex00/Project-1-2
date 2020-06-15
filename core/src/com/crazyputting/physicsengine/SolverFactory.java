package com.crazyputting.physicsengine;

public class SolverFactory {

    private static SolverFactory factory = null;

    public static SolverFactory get() {
        if (factory == null) {
            factory = new SolverFactory();
        }
        return factory;
    }

    public PhysicsSolver makeSolver(String solverString) {
        switch (solverString) {
            case "Verlet":
                return new Verlet();
            case "Runge-Kutta":
                return new RungeKutta();
            case "Adams-Bashforth":
                return new AdamsBashforth();
            default:
                return new Euler();
        }
    }
}
