package com.tdtech.wheeledmadness.world.physics;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.entity.shape.IShape;
import org.andengine.util.math.MathUtils;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

class PhysicsConnector {
    
    public static final float METER_TO_PIXEL_RATIO = 32.0f;
    public static final float PIXEL_TO_METER_RATIO = 1 / METER_TO_PIXEL_RATIO;
    
    private final IShape mShape;
    private final Body mBody;
    
    private final float mShapeHalfBaseWidth;
    private final float mShapeHalfBaseHeight;
    
    PhysicsConnector(final IAreaShape shape, final Body body) {
        mShape = shape;
        mBody = body;
        
        mShapeHalfBaseWidth = shape.getWidth() * 0.5f;
        mShapeHalfBaseHeight = shape.getHeight() * 0.5f;
    }
    
    IShape getShape() {
        return mShape;
    }
    
    Body getBody() {
        return mBody;
    }
    
    void onUpdate() {
        Vec2 position = mBody.getPosition();
        mShape.setPosition(position.x * METER_TO_PIXEL_RATIO - mShapeHalfBaseWidth,
                position.y * METER_TO_PIXEL_RATIO - mShapeHalfBaseHeight);
        
        mShape.setRotation(MathUtils.radToDeg(mBody.getAngle()));
    }
}