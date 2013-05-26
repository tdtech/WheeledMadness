package com.tdtech.wheeledmadness.world.physics;

import org.jbox2d.common.Vec2;

import com.tdtech.wheeledmadness.world.physics.PhysicsEntity.PhysicsEntityType;

public class PhysicsEntityDef {
    
    public Vec2 mPosition;
    public float mAngle;
    public float mFrameRadius; // used when searching
    
    public PhysicsEntityType mType;
    
    public boolean mIsActive;
    public boolean mIsBullet;
    public boolean mIsGravitated;
    public boolean mIsFixedRotation; // Should this body be prevented from rotating? Useful for characters
    public boolean mIsSensor;
    
    public float mMass;
    public float mFriction;
    public float mRestitution;
    public float mDeceleration;
    public float mAngularDeceleration;
    
    public int mCategoryBits;
    public int mFilterBits;
    
    public Object mUserData;
    
    public PhysicsEntityDef() {
        mPosition = new Vec2();
        mAngle = 0;
        mFrameRadius = 0;
        mType = PhysicsEntityType.STATIC;
        mIsActive = true;
        mIsBullet = false;
        mIsGravitated = true;
        mIsFixedRotation = false;
        mIsSensor = false;
        mMass = 1;
        mFriction = 0;
        mRestitution = 0;
        mDeceleration = 0;
        mAngularDeceleration = 0;
        mCategoryBits = 0xFFFF;
        mFilterBits = 0xFFFF;
        mUserData = null;
    }
}