package com.tdtech.wheeledmadness.world.shapes;

import com.tdtech.wheeledmadness.world.physics.PhysicsEntity;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntityDef;
import com.tdtech.wheeledmadness.world.physics.PhysicsWorld;

class WMWorldCircleShape implements IWMWorldShape {
    
    private float mRadius;
    
    WMWorldCircleShape(float radius) {
        mRadius = radius;
    }

    @Override
    public PhysicsEntity buildPhysicsEntity(PhysicsEntityDef entityDef,
            PhysicsWorld physicsWorld) {
        return physicsWorld.createCircleEntity(entityDef, mRadius);
    }
}