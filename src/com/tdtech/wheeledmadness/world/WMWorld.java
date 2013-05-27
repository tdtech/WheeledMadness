package com.tdtech.wheeledmadness.world;

import java.util.ArrayList;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.entity.scene.Scene;
import org.jbox2d.common.Vec2;

import com.tdtech.wheeledmadness.world.physics.PhysicsEntity;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntityDef;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntity.PhysicsEntityType;
import com.tdtech.wheeledmadness.world.physics.PhysicsWorld;

public class WMWorld implements IUpdateHandler, PhysicsWorld.IContactListener {

    // Entity groups
    public static final int ENTITY_GROUP_WHEEL  = 0x0001;
    public static final int ENTITY_GROUP_BULLET = 0x0002;
    public static final int ENTITY_GROUP_BONUS  = 0x0004;
    public static final int ENTITY_GROUP_DEBRIS = 0x0008;
    
    private static final float DEFAULT_DECELERATION = 2.5f;
    private static final float DEFAULT_ANGULAR_DECELERATION = 3.0f;
    
    private static final int ENTITIES_INITIAL_CAPACITY = 256;
    
    private static Vec2 GRAVITY = new Vec2(0, 9.8f);
    
    private static WMWorld mInstance = null;
    
    // TEMP PHYSICS ENTITY DEF
    private PhysicsEntityDef TEMP_PHYSICS_ENTITY_DEF = new PhysicsEntityDef();
    
    private Engine mEngine;
    private PhysicsWorld mPhysicsWorld;
    private Scene mScene;
    private ArrayList<WMWorldEntity> mEntities;
    
    private WMWorld(Engine engine) {
        mEngine = engine;
        mPhysicsWorld = new PhysicsWorld(GRAVITY);
        mScene = new Scene();
        mEntities = new ArrayList<WMWorldEntity>(ENTITIES_INITIAL_CAPACITY);
    }
    
    public static void init(Engine engine) {
        mInstance = new WMWorld(engine);
    }
    
    public static void release() {
        mInstance = null;
    }
    
    public static WMWorld getInstance() {
        return mInstance;
    }
    
    public Engine getEngine() {
        return mEngine;
    }
    
    public PhysicsWorld getWorldPhysics() {
        return mPhysicsWorld;
    }
    
    public Scene getWorldScene() {
        return mScene;
    }
    
    public void registerEntity(WMWorldEntity entity, WMWorldEntityDef entityDef) {
        int group = entity.mEntityGroup;
        
        TEMP_PHYSICS_ENTITY_DEF.mPosition.set(entityDef.mPosition);
        TEMP_PHYSICS_ENTITY_DEF.mAngle = entityDef.mAngle;
        TEMP_PHYSICS_ENTITY_DEF.mFrameRadius = entityDef.mFrameRadius;
        TEMP_PHYSICS_ENTITY_DEF.mType = PhysicsEntityType.DYNAMIC;
        TEMP_PHYSICS_ENTITY_DEF.mIsActive = false;
        TEMP_PHYSICS_ENTITY_DEF.mIsBullet = (group & ENTITY_GROUP_BULLET) > 0;
        TEMP_PHYSICS_ENTITY_DEF.mIsGravitated = entityDef.mIsGravitated;
        TEMP_PHYSICS_ENTITY_DEF.mIsFixedRotation = false;
        TEMP_PHYSICS_ENTITY_DEF.mMass = entity.mMass;
        TEMP_PHYSICS_ENTITY_DEF.mFriction = entityDef.mFriction;
        TEMP_PHYSICS_ENTITY_DEF.mRestitution = entityDef.mRestitution;
        TEMP_PHYSICS_ENTITY_DEF.mDeceleration = DEFAULT_DECELERATION;
        TEMP_PHYSICS_ENTITY_DEF.mAngularDeceleration = DEFAULT_ANGULAR_DECELERATION;
        TEMP_PHYSICS_ENTITY_DEF.mCategoryBits = group;
        TEMP_PHYSICS_ENTITY_DEF.mFilterBits = entityDef.mEntityGroupFilter;
        TEMP_PHYSICS_ENTITY_DEF.mUserData = entity;
        
        entity.mPhysicsEntity = entityDef.mWorldShape.buildPhysicsEntity(TEMP_PHYSICS_ENTITY_DEF, mPhysicsWorld);
        entity.mIsActive = false;
        
        if (entityDef.mAttachedShape != null) {
            mPhysicsWorld.registerConnector(entity.mPhysicsEntity, entityDef.mAttachedShape);
        }
        
        mEntities.add(entity);
    }
    
    public void clear() {
        mPhysicsWorld.destroyAllEntities();
        mEntities.clear();
    }
    
    @Override
    public void onUpdate(float pSecondsElapsed) {
        
    }

    @Override
    public void reset() {
        
    }

    @Override
    public int onContact(PhysicsEntity entityA, PhysicsEntity entityB) {
        WMWorldEntity worldEntityA = (WMWorldEntity)entityA.getUserData();
        WMWorldEntity worldEntityB = (WMWorldEntity)entityB.getUserData();
        
        int solution = PhysicsWorld.CONTACT_SOLUTION_CONTINUE;
        
        if (worldEntityA != null && worldEntityB != null) {
            if (worldEntityA.onContact(worldEntityB)) {
                solution |= PhysicsWorld.CONTACT_SOLUTION_IGNORE | PhysicsWorld.CONTACT_SOLUTION_STOP_A;
            }
        
            if (worldEntityB.onContact(worldEntityA)) {
                solution |= PhysicsWorld.CONTACT_SOLUTION_IGNORE | PhysicsWorld.CONTACT_SOLUTION_STOP_B;
            }
        }
        
        return solution;
    }

    @Override
    public void onSensorTouched(PhysicsEntity sensor, PhysicsEntity entity) {
        // TODO: implement
    }
    
}