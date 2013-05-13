package com.tdtech.wheeledmadness.world.physics;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.andengine.util.math.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;

import com.tdtech.wheeledmadness.network.INetworkObject;
import com.tdtech.wheeledmadness.utils.XMath;

public final class PhysicsEntity implements INetworkObject {
    
    public enum PhysicsEntityType {
        STATIC,
        DYNAMIC
    }
    
    private static Vec2[] TEMP_VECTORS = new Vec2[2];
    
    static {
        for (int i = 0; i < TEMP_VECTORS.length; i++) {
            TEMP_VECTORS[i] = new Vec2();
        }
    }
    
    Body mBody;
    
    private Vec2 mCenter;
    private Object mUserData;
    
    PhysicsEntity(Body body, Object userData) {
        mBody = body;
        mUserData = userData;
    }
    
    public final void setActive(boolean active) {
        mBody.setActive(active);
    }
    
    public final void applyLinearImpulse(Vec2 point, Vec2 impulse) {
        TEMP_VECTORS[0].set(point);
        TEMP_VECTORS[0].mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        
        TEMP_VECTORS[1].set(impulse);
        
        mBody.applyLinearImpulse(TEMP_VECTORS[1], TEMP_VECTORS[0]);
    }
    
    public final void setTransform(Vec2 position, float angle) {
        TEMP_VECTORS[0].set(position);
        TEMP_VECTORS[0].mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        
        mBody.setTransform(TEMP_VECTORS[0], MathUtils.degToRad(angle));
    }
    
    public final PhysicsEntityType getType() {
        return mBody.getType() == BodyType.DYNAMIC ? PhysicsEntityType.DYNAMIC : PhysicsEntityType.STATIC;
    }
    
    public final Vec2 getCenter() {
        mCenter.set(mBody.getPosition());
        mCenter.mulLocal(PhysicsConnector.METER_TO_PIXEL_RATIO);
        
        return mCenter;
    }
    
    public final float getAngle() {
        return XMath.wrapAngle(MathUtils.radToDeg(mBody.getAngle()));
    }
    
    public final Object getUserData() {
        return mUserData;
    }

    @Override
    public void readFromStream(DataInputStream stream) {
        // TODO: implement
    }

    @Override
    public void writeToStream(DataOutputStream stream) {
        // TODO: implement
    }
}