package com.tdtech.wheeledmadness.world.physics;

import org.andengine.util.math.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

import com.tdtech.wheeledmadness.utils.XMath;

public final class PhysicsEntity {
    
    public enum PhysicsEntityType {
        STATIC,
        DYNAMIC
    }
    
    Body mBody;
    
    private Vec2 mCenter;
    private Object mUserData;
    
    PhysicsEntity(Body body, Object userData) {
        mBody = body;
        mUserData = userData;
    }
    
    public final PhysicsEntityType getType() {
        return mBody.getType() == BodyType.DYNAMIC ? PhysicsEntityType.DYNAMIC : PhysicsEntityType.STATIC;
    }
    
    public final Vec2 getCenter() {
        Vec2 pos = mBody.getPosition();
        
        mCenter.x = pos.x * PhysicsConnector.METER_TO_PIXEL_RATIO;
        mCenter.y = pos.y * PhysicsConnector.METER_TO_PIXEL_RATIO;
        
        return mCenter;
    }
    
    public final float getAngle() {
        return XMath.wrapAngle(MathUtils.radToDeg(mBody.getAngle()));
    }
    
    public final Object getUserData() {
        return mUserData;
    }
}