package com.tdtech.wheeledmadness.world;

import org.jbox2d.common.Vec2;

import com.tdtech.wheeledmadness.network.INetworkObject;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntity;

public abstract class WMWorldEntity implements INetworkObject {
    
    protected PhysicsEntity mPhysicsEntity; // protected for inherited classes to have access
    
    boolean mIsActive;
    float mMass;
    int mEntityGroup;
    
    public WMWorldEntity(float mass, int entityGroup) {
        mMass = mass;
        mEntityGroup = entityGroup;
    }
    
    public abstract void update();
    public abstract void applyDamage(Vec2 position, WMDamageType damageType, int damageValue);
    public abstract boolean onContact(WMWorldEntity entity); // true if contact has been consumed
    
    public void setTransform(Vec2 position, float angle) {
        mPhysicsEntity.setTransform(position, angle);
    }
    
    public void setActive(boolean active) {
        mPhysicsEntity.setActive(active);
        mIsActive = active;
    }
    
    public Vec2 getPosition() {
        return mPhysicsEntity.getCenter();
    }
    
    public float getAngle() {
        return mPhysicsEntity.getAngle();
    }
    
    public final float getMass() {
        return mMass;
    }
    
    public final int getEntityGroup() {
        return mEntityGroup;
    }
}