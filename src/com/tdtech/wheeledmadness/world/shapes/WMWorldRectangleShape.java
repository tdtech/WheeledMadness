package com.tdtech.wheeledmadness.world.shapes;

import com.tdtech.wheeledmadness.world.physics.PhysicsEntity;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntityDef;
import com.tdtech.wheeledmadness.world.physics.PhysicsWorld;

class WMWorldRectangleShape implements IWMWorldShape {
    
    private float mWidth;
    private float mHeight;
    
    WMWorldRectangleShape(float width, float height) {
        mWidth = width;
        mHeight = height;
    }

    @Override
    public PhysicsEntity buildPhysicsEntity(PhysicsEntityDef entityDef,
            PhysicsWorld physicsWorld) {
        return physicsWorld.createRectangleEntity(entityDef, mWidth, mHeight);
    }
}