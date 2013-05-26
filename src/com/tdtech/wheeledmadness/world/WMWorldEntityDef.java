package com.tdtech.wheeledmadness.world;

import org.andengine.entity.shape.IAreaShape;
import org.jbox2d.common.Vec2;

import com.tdtech.wheeledmadness.world.shapes.IWMWorldShape;

public class WMWorldEntityDef {
    
    private static final float DEFAULT_FRICTION = 0.3f;
    
    public Vec2 mPosition;
    public float mAngle;
    public float mFrameRadius; // for searching entities
    public IAreaShape mAttachedShape;
    public IWMWorldShape mWorldShape;
    public float mFriction;
    public float mRestitution;
    public boolean mIsGravitated;
    public int mEntityGroupFilter;
    
    public WMWorldEntityDef() {
        mPosition = new Vec2();
        mAngle = 0;
        mFrameRadius = 0;
        mFriction = DEFAULT_FRICTION;
        mRestitution = 0;
        mIsGravitated = true;
        mEntityGroupFilter = 0xFFFF;
    }
}