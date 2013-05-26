package com.tdtech.wheeledmadness.world.shapes;

import com.tdtech.wheeledmadness.world.physics.PhysicsEntity;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntityDef;
import com.tdtech.wheeledmadness.world.physics.PhysicsWorld;

public interface IWMWorldShape {
    
    PhysicsEntity buildPhysicsEntity(PhysicsEntityDef entityDef, PhysicsWorld physicsWorld);
}