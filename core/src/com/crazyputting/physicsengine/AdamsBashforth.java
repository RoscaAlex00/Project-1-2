package com.crazyputting.physicsengine;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class AdamsBashforth implements PhysicsSolver {
    private final int ORDER = 4;
    private final int[] ab4Coefficients = new int[]{-9, 37, -59, 55};
    private boolean isHit = false;
    private Physics physics;
    private PhysicsSolver bootstrap = new RungeKutta();
    private List<Vector3> positions = new ArrayList<>();
    private List<Vector3> velocities = new ArrayList<>();

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
        //Doesn't add new positions to the list if the ball is not moving.
        if (isHit) {
            //Makes a list of positions, to use those in the adams-bashforth steps
            if (positions.size() < ORDER) {
                Vector3 newPos = bootstrap.getPosition(position.cpy(), velocity.cpy());
                positions.add(newPos.cpy());
                return newPos.cpy();
            }

            for (int i = 0; i < (positions.size() - 1); i++) {
                positions.set(i, positions.get(i + 1).cpy());
            }
            positions.set(positions.size() - 1, position.cpy());

            Vector3[] velocityFunctions = new Vector3[4];
            for (int i = 0; i < velocityFunctions.length; i++) {
                velocityFunctions[i] = velocities.get(i).cpy();
            }

            Vector3 funSum = new Vector3(0, 0, 0);
            for (int i = velocityFunctions.length - 1; i >= 0; i--) {
                funSum.add(velocityFunctions[i].scl(ab4Coefficients[i]));
            }
            return positions.get(3).add(funSum.scl(physics.getDt() / 24f));
        }
        //Clears the positions list so the function doesn't use wrong values.
        positions.clear();
        return bootstrap.getPosition(position, velocity);
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        //Doesn't add new velocities to the list if the ball is not moving.
        if (isHit) {
            //Makes a list of velocities, to use those in the adams-bashforth steps
            if (velocities.size() < ORDER) {
                Vector3 newSpeed = bootstrap.getSpeed(position.cpy(), velocity.cpy());
                velocities.add(newSpeed.cpy());
                return newSpeed.cpy();
            }

            for (int i = 0; i < (velocities.size() - 1); i++) {
                velocities.set(i, velocities.get(i + 1).cpy());
            }
            velocities.set(ORDER - 1, velocity.cpy());

            Vector3[] accelerationFunctions = new Vector3[4];
            for (int i = 0; i < accelerationFunctions.length; i++) {
                accelerationFunctions[i] = physics.getAcceleration(positions.get(i), velocities.get(i)).cpy();
            }

            Vector3 funSum = new Vector3(0, 0, 0);
            for (int i = accelerationFunctions.length - 1; i >= 0; i--) {
                funSum.add(accelerationFunctions[i].scl(ab4Coefficients[i]));
            }
            return velocities.get(3).add(funSum.scl(physics.getDt() / 24f));
        }
        //Clears the velocities list so the function doesn't use wrong values.
        velocities.clear();
        return bootstrap.getSpeed(position, velocity);
    }

    @Override
    public void setHit(boolean isHit) {
        this.isHit = isHit;
    }
}
