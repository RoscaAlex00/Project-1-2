package com.crazyputting.engine;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;

public class AdamsBashforth implements PhysicsSolver {
    private final int ORDER = 4;

    private boolean isHit = false;

    private Physics physics;
    private PhysicsSolver bootstrap = new RungeKutta();

    private ArrayList<Vector3> positions = new ArrayList<>();
    private ArrayList<Vector3> velocities = new ArrayList<>();
    private Vector3[] accelerationFunctions = new Vector3[4];
    private Vector3[] velocityFunctions = new Vector3[4];
    private final int[] ab4Coefficients = new int[]{-9,37,-59,55};

    @Override
    public void set_step_size(double h) {

    }

    @Override
    public void setPhysics(Physics physics) {
        this.physics = physics;
        bootstrap.setPhysics(physics);
    }

    @Override
    public Vector3 getPosition(Vector3 position, Vector3 velocity) {
        if (isHit) {
            if (positions.size() < ORDER) {
                Vector3 newPos = bootstrap.getPosition(position.cpy(), velocity.cpy());
                positions.add(newPos.cpy());
                return newPos.cpy();
            }

            for (int i = 0; i < (positions.size() - 1); i++) {
                positions.set(i, positions.get(i + 1).cpy());
            }
            positions.set(positions.size() - 1, position.cpy());

            //error here (need to use getSpeed())?
            for (int i = 0; i < velocityFunctions.length; i++) {
                velocityFunctions[i] = velocities.get(i).cpy();
            }

            Vector3 funSum = new Vector3(0,0,0);
            for (int i = velocityFunctions.length - 1; i >= 0; i--){
                funSum.add(velocityFunctions[i].scl(ab4Coefficients[i]));
            }
            return positions.get(3).add(funSum.scl(physics.getDt() / 24f));
        }
        positions.clear();
        return bootstrap.getPosition(position, velocity);
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        if (isHit) {
            if (velocities.size() < ORDER) {
                Vector3 newSpeed = bootstrap.getSpeed(position.cpy(), velocity.cpy());
                velocities.add(newSpeed.cpy());
                return newSpeed.cpy();
            }

            for (int i = 0; i < (velocities.size() - 1); i++) {
                velocities.set(i, velocities.get(i + 1).cpy());
            }
            velocities.set(ORDER - 1, velocity.cpy());

            for (int i = 0; i < accelerationFunctions.length; i++) {
                accelerationFunctions[i] = physics.getAcceleration(positions.get(i), velocities.get(i)).cpy();
            }

            Vector3 funSum = new Vector3(0,0,0);
            for (int i = accelerationFunctions.length - 1; i >= 0; i--){
                funSum.add(accelerationFunctions[i].scl(ab4Coefficients[i]));
            }
            return velocities.get(3).add(funSum.scl(physics.getDt() / 24f));
        }
        velocities.clear();
        return bootstrap.getSpeed(position,velocity);
    }

    @Override
    public void setHit(boolean isHit) {
        this.isHit = isHit;
    }
}
