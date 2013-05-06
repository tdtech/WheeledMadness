package com.tdtech.wheeledmadness.world.physics;

import java.util.ArrayList;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.util.math.MathUtils;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.tdtech.wheeledmadness.WheeledMadnessGameActivity;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntity.PhysicsEntityType;

public class PhysicsWorld {

    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 4;
    
    private static final float STEP_TIME = 1.0f / WheeledMadnessGameActivity.FPS;
    
    private static final int DEFAULT_CONNECTORS_CAPACITY = 64;
    
    // TEMP SHAPES
    private static PolygonShape TEMP_POLYGON_SHAPE = new PolygonShape();
    private static CircleShape TEMP_CIRCLE_SHAPE = new CircleShape();
    private static EdgeShape TEMP_EDGE_SHAPE = new EdgeShape();
    private static ChainShape TEMP_CHAIN_SHAPE = new ChainShape();
    
    // TEMP BODY DEF
    private static BodyDef TEMP_BODY_DEF = new BodyDef();
    
    // TEMP FIXTURE DEF
    private static FixtureDef TEMP_FIXTURE_DEF = new FixtureDef();
    
    private World mWorld;
    private ArrayList<PhysicsConnector> mPhysicsConnectors;
    
    public PhysicsWorld(Vec2 gravity) {
        mWorld = new World(gravity);
        mPhysicsConnectors = new ArrayList<PhysicsConnector>(DEFAULT_CONNECTORS_CAPACITY);
    }
    
    public PhysicsEntity createRectangleEntity(PhysicsEntityDef entityDef, float width, float height) {
        Body body = createBody(entityDef);
        
        PhysicsEntity entity = new PhysicsEntity(body, entityDef.mUserData);
        
        float bodyWidth = width * PhysicsConnector.PIXEL_TO_METER_RATIO;
        float bodyHeight = height * PhysicsConnector.PIXEL_TO_METER_RATIO;
        
        // Attaching rectangle shape
        TEMP_POLYGON_SHAPE.setAsBox(bodyWidth * 0.5f, bodyHeight * 0.5f);
        TEMP_FIXTURE_DEF.shape = TEMP_POLYGON_SHAPE;
        
        fillTempFixtureDef(entityDef);
        TEMP_FIXTURE_DEF.userData = entity;
        TEMP_FIXTURE_DEF.density = entityDef.mMass / (bodyWidth * bodyHeight);
        body.createFixture(TEMP_FIXTURE_DEF);
        
        return entity;
    }
    
    public PhysicsEntity createCircleEntity(PhysicsEntityDef entityDef, float radius) {
        Body body = createBody(entityDef);
        
        PhysicsEntity entity = new PhysicsEntity(body, entityDef.mUserData);
        
        float bodyRadius = radius * PhysicsConnector.PIXEL_TO_METER_RATIO;
        
        // Attaching circle shape
        TEMP_CIRCLE_SHAPE.m_radius = bodyRadius;
        TEMP_FIXTURE_DEF.shape = TEMP_CIRCLE_SHAPE;
        
        fillTempFixtureDef(entityDef);
        TEMP_FIXTURE_DEF.userData = entity;
        TEMP_FIXTURE_DEF.density = entityDef.mMass / (Settings.PI * bodyRadius * bodyRadius);
        body.createFixture(TEMP_FIXTURE_DEF);
        
        return entity;
    }
    
    public PhysicsEntity createEdgeEntity(PhysicsEntityDef entityDef, Vec2 vertex1, Vec2 vertex2, Vec2 ghostVertex1, Vec2 ghostVertex2) {
        Body body = createBody(entityDef);
        
        PhysicsEntity entity = new PhysicsEntity(body, entityDef.mUserData);
        
        // Attaching edge shape
        TEMP_EDGE_SHAPE.m_vertex1.set(vertex1);
        TEMP_EDGE_SHAPE.m_vertex1.mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        TEMP_EDGE_SHAPE.m_vertex2.set(vertex2);
        TEMP_EDGE_SHAPE.m_vertex2.mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        if (ghostVertex1 != null) {
            TEMP_EDGE_SHAPE.m_vertex0.set(ghostVertex1);
            TEMP_EDGE_SHAPE.m_vertex0.mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
            TEMP_EDGE_SHAPE.m_hasVertex0 = true;
        }
        if (ghostVertex2 != null) {
            TEMP_EDGE_SHAPE.m_vertex3.set(ghostVertex2);
            TEMP_EDGE_SHAPE.m_vertex3.mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
            TEMP_EDGE_SHAPE.m_hasVertex3 = true;
        }
        TEMP_FIXTURE_DEF.shape = TEMP_EDGE_SHAPE;
        
        fillTempFixtureDef(entityDef);
        TEMP_FIXTURE_DEF.userData = entity;
        TEMP_FIXTURE_DEF.density = 0; // edge always has zero mass
        body.createFixture(TEMP_FIXTURE_DEF);
        
        return entity;
    }
    
    public PhysicsEntity createChainEntity(PhysicsEntityDef entityDef, Vec2[] vertices, boolean loop) {
        Body body = createBody(entityDef);
        
        PhysicsEntity entity = new PhysicsEntity(body, entityDef.mUserData);
        
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        }
        
        // Attaching chain shape
        if (loop) {
            TEMP_CHAIN_SHAPE.createLoop(vertices, vertices.length);
        } else {
            TEMP_CHAIN_SHAPE.createChain(vertices, vertices.length);
        }
        TEMP_FIXTURE_DEF.shape = TEMP_CHAIN_SHAPE;
        
        fillTempFixtureDef(entityDef);
        TEMP_FIXTURE_DEF.userData = entity;
        TEMP_FIXTURE_DEF.density = 0; // chain always has zero mass
        body.createFixture(TEMP_FIXTURE_DEF);
        
        return entity;
    }
    
    public void registerConnector(PhysicsEntity entity, IAreaShape shape) {
        mPhysicsConnectors.add(new PhysicsConnector(shape, entity.mBody));
    }
    
    public void unregisterConnector(PhysicsEntity entity) {
        for (PhysicsConnector connector : mPhysicsConnectors) {
            if (connector.getBody() == entity.mBody) {
                mPhysicsConnectors.remove(connector);
                break;
            }
        }
    }
    
    public void onUpdate() {
        mWorld.step(STEP_TIME, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
        
        for (PhysicsConnector connector : mPhysicsConnectors) {
            connector.onUpdate();
        }
    }
    
    private Body createBody(PhysicsEntityDef entityDef) {
        TEMP_BODY_DEF.type = entityDef.mType == PhysicsEntityType.DYNAMIC ? BodyType.DYNAMIC : BodyType.STATIC;
        TEMP_BODY_DEF.position.x = entityDef.mPosition.x * PhysicsConnector.PIXEL_TO_METER_RATIO;
        TEMP_BODY_DEF.position.y = entityDef.mPosition.y * PhysicsConnector.PIXEL_TO_METER_RATIO;
        TEMP_BODY_DEF.angle = MathUtils.degToRad(entityDef.mAngle);
        TEMP_BODY_DEF.linearDamping = entityDef.mDeceleration;
        TEMP_BODY_DEF.angularDamping = entityDef.mAngularDeceleration;
        TEMP_BODY_DEF.fixedRotation = entityDef.mIsFixedRotation;
        TEMP_BODY_DEF.bullet = entityDef.mIsBullet;
        TEMP_BODY_DEF.active = entityDef.mIsActive;
        TEMP_BODY_DEF.userData = Float.valueOf(entityDef.mFrameRadius);
        
        return mWorld.createBody(TEMP_BODY_DEF);
    }
    
    private static void fillTempFixtureDef(PhysicsEntityDef entityDef) {
        TEMP_FIXTURE_DEF.friction = entityDef.mFriction;
        TEMP_FIXTURE_DEF.restitution = entityDef.mRestitution;
        TEMP_FIXTURE_DEF.isSensor = entityDef.mIsSensor;
        TEMP_FIXTURE_DEF.filter.categoryBits = entityDef.mCategoryBits;
        TEMP_FIXTURE_DEF.filter.maskBits = entityDef.mFilterBits;
    }
}