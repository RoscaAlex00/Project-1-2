package com.crazyputting.physicsengine;

import com.badlogic.gdx.math.Vector3;

/**
 * Runge-Kutta 4th order Solver
 */

public class RungeKutta implements PhysicsSolver {
    private Physics physics;


    @Override
    public void setPhysics(Physics physics) {
        this.physics = physics;
    }

    @Override
    public Vector3 getPosition(Vector3 position, Vector3 velocity) {
        Vector3 funK1 = getSpeed(position, velocity).cpy();
        Vector3 k1 = funK1.scl(physics.getDt());

        //Used to get the time-differences in the function.
        float sclFactor = 1 / 3f;
        Vector3 velK2 = setDtGetSpeed(position, velocity, sclFactor);

        sclFactor = 2 / 3f;
        Vector3 velK3 = setDtGetSpeed(position, velocity, sclFactor);

        Vector3 velK4 = getSpeed(position, velocity).cpy();


        Vector3 posK2 = position.cpy().add(k1.cpy().scl(1 / 3f));
        Vector3 funK2 = getSpeed(posK2, velK2).cpy();
        Vector3 k2 = funK2.scl(physics.getDt());

        Vector3 posK3 = position.cpy().sub(k1.cpy().scl(1 / 3f)).add(k2.cpy());
        Vector3 funK3 = getSpeed(posK3, velK3).cpy();
        Vector3 k3 = funK3.scl(physics.getDt());

        Vector3 posK4 = position.cpy().add(k1.cpy()).sub(k2.cpy()).add(k3.cpy());
        Vector3 funK4 = getSpeed(posK4, velK4).cpy();
        Vector3 k4 = funK4.scl(physics.getDt());

        Vector3 kSum = k1.add(k2.scl(3f)).add(k3.scl(3f)).add(k4);
        Vector3 newPos = position.cpy().add(kSum.scl(1 / 8f));
        newPos.z = 0;
        return newPos;
    }

    @Override
    public Vector3 getSpeed(Vector3 position, Vector3 velocity) {
        Vector3 funK1 = physics.getAcceleration(position, velocity).cpy();
        Vector3 k1 = funK1.scl(physics.getDt());

        Vector3 posK2 = position.cpy().add(velocity.cpy().scl(physics.getDt() / 3f));
        Vector3 velK2 = velocity.cpy().add(k1.cpy().scl(1 / 3f));
        Vector3 funK2 = physics.getAcceleration(posK2, velK2).cpy();
        Vector3 k2 = funK2.scl(physics.getDt());

        Vector3 posK3 = position.cpy().add(velocity.cpy().scl((2 * physics.getDt()) / 3f));
        Vector3 velK3 = velocity.cpy().sub(k1.cpy().scl(1 / 3f)).add(k2.cpy());
        Vector3 funK3 = physics.getAcceleration(posK3, velK3).cpy();
        Vector3 k3 = funK3.scl(physics.getDt());

        Vector3 posK4 = position.cpy().add(velocity.cpy().scl(physics.getDt()));
        Vector3 velK4 = velocity.cpy().add(k1.cpy()).sub(k2.cpy()).add(k3.cpy());
        Vector3 funK4 = physics.getAcceleration(posK4, velK4).cpy();
        Vector3 k4 = funK4.scl(physics.getDt());

        Vector3 kSum = k1.add(k2.scl(3f)).add(k3.scl(3f)).add(k4);

        return velocity.cpy().add(kSum.scl(1 / 8f));
    }

    @Override
    public void setHit(boolean isHit) {

    }

    private Vector3 setDtGetSpeed(Vector3 pos, Vector3 vel, float sclFactor) {

        physics.setDt(physics.getDt() * (sclFactor));
        Vector3 sclSpeed = getSpeed(pos, vel);
        physics.setDt(physics.getDt() / (sclFactor));

        return sclSpeed;
    }
}
