package com.tdtech.wheeledmadness.world.physics;

import java.util.ArrayList;

import org.andengine.entity.shape.IAreaShape;
import org.andengine.util.math.MathUtils;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.callbacks.RayCastCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Distance.SimplexCache;
import org.jbox2d.collision.DistanceInput;
import org.jbox2d.collision.DistanceOutput;
import org.jbox2d.collision.Manifold;
import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import com.tdtech.wheeledmadness.WMGameActivity;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntity.PhysicsEntityType;

public class PhysicsWorld implements ContactListener,
                                     RayCastCallback,
                                     QueryCallback {
    
    // Contact solutions
    public static final int CONTACT_SOLUTION_CONTINUE = 0x0000;
    public static final int CONTACT_SOLUTION_IGNORE   = 0x0001;
    public static final int CONTACT_SOLUTION_STOP_A   = 0x0002;
    public static final int CONTACT_SOLUTION_STOP_B   = 0x0004;
    
    public interface IContactListener {
        int onContact(PhysicsEntity entityA, PhysicsEntity entityB);
        void onSensorTouched(PhysicsEntity sensor, PhysicsEntity entity);
    }
    
    public static class EntityDistance {
        public Vec2 mClosestPoint;
        public float mDistance;
        
        EntityDistance() {
            mClosestPoint = new Vec2();
            mDistance = 0;
        }
        
        @Override
        public EntityDistance clone() {
            EntityDistance distance = new EntityDistance();
            
            distance.mClosestPoint = new Vec2(mClosestPoint);
            distance.mDistance = mDistance;
            
            return distance;
        }
    }
    
    public static class RayCastResults {
        public PhysicsEntity mPhysicsEntity;
        public Vec2 mIntersection;
        
        RayCastResults() {
            mPhysicsEntity = null;
            mIntersection = new Vec2();
        }
        
        @Override
        public RayCastResults clone() {
            RayCastResults results = new RayCastResults();
            
            results.mPhysicsEntity = mPhysicsEntity;
            results.mIntersection = new Vec2(mIntersection);
            
            return results;
        }
    }

    public static class SearchResults {
        public PhysicsEntity[] mEntities;
        public int mCount;

        SearchResults(int capacity) {
            mEntities = new PhysicsEntity[capacity];
            mCount = 0;
        }

        @Override
        public SearchResults clone() {
            SearchResults results = new SearchResults(mEntities.length);

            for (int i = 0; i < mEntities.length; i++) {
                results.mEntities[i] = mEntities[i];
            }
            results.mCount = mCount;

            return results;
        }
    }

    private static IContactListener DEFAULT_CONTACT_LISTENER = new IContactListener() {
    
        @Override
        public int onContact(PhysicsEntity entityA, PhysicsEntity entityB) {
            return CONTACT_SOLUTION_CONTINUE;
        }
        
        @Override
        public void onSensorTouched(PhysicsEntity sensor, PhysicsEntity entity) {
            // do nothing
        }
    };

    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 4;
    
    private static final float STEP_TIME = 1.0f / WMGameActivity.FPS;
    
    private static final int CONNECTORS_INITIAL_CAPACITY = 64;
    
    private static final Vec2 ZERO_VECTOR = new Vec2();
    
    // TEMP VECTORS
    private static Vec2[] TEMP_VECTORS = new Vec2[2];
    
    static {
        for (int i = 0; i < TEMP_VECTORS.length; i++) {
            TEMP_VECTORS[i] = new Vec2();
        }
    }
    
    // TEMP SHAPES
    private PolygonShape TEMP_POLYGON_SHAPE = new PolygonShape();
    private CircleShape TEMP_CIRCLE_SHAPE = new CircleShape();
    private EdgeShape TEMP_EDGE_SHAPE = new EdgeShape();
    private ChainShape TEMP_CHAIN_SHAPE = new ChainShape();
    
    // TEMP BODY DEF
    private BodyDef TEMP_BODY_DEF = new BodyDef();
    
    // TEMP FIXTURE DEF
    private FixtureDef TEMP_FIXTURE_DEF = new FixtureDef();
    
    // TEMP ENTITY DISTANCE
    private EntityDistance TEMP_ENTITY_DISTANCE = new EntityDistance();
    
    // TEMP RAYCAST RESULTS
    private RayCastResults TEMP_RAYCAST_RESULTS = new RayCastResults();
    
    // TEMP SEARCH RESULTS
    private static final int SEARCH_RESULTS_INITIAL_CAPACITY = 32;
    private SearchResults TEMP_SEARCH_RESULTS = new SearchResults(SEARCH_RESULTS_INITIAL_CAPACITY);
    private AABB TEMP_AABB = new AABB();
    
    private World mWorld;
    private ArrayList<PhysicsConnector> mPhysicsConnectors;
    private IContactListener mContactListener;
    
    // for distance
    private SimplexCache mSimplexCache;
    private DistanceInput mDistanceInput;
    private DistanceOutput mDistanceOutput;
    private CircleShape mEpsilonShape;
    
    // for raycast
    private Fixture mReportedFixture;
    private int mFilterBits;
    private Vec2 mInitialIntersection;
    
    // for search
    private Vec2 mSearchCenter;
    private float mSearchRadius;
    
    public PhysicsWorld(Vec2 gravity) {
        mWorld = new World(gravity);
        mPhysicsConnectors = new ArrayList<PhysicsConnector>(CONNECTORS_INITIAL_CAPACITY);
        mContactListener = DEFAULT_CONTACT_LISTENER;
        mWorld.setContactListener(this);
        
        mSimplexCache = new SimplexCache();
        mDistanceInput = new DistanceInput();
        mDistanceOutput = new DistanceOutput();
        mEpsilonShape = new CircleShape();
        mEpsilonShape.m_radius = Settings.EPSILON;
        mDistanceInput.proxyA.set(mEpsilonShape, 0);
        mDistanceInput.transformA.setIdentity();
        
        mInitialIntersection = new Vec2();
    }
    
    public boolean isLocked() {
        return mWorld.isLocked();
    }
    
    public void setContactListener(IContactListener listener) {
        mContactListener = (listener == null ? DEFAULT_CONTACT_LISTENER : listener);
    }
    
    public EntityDistance getDistance(PhysicsEntity entity, Vec2 point) {
        mSimplexCache.count = 0;
        
        mEpsilonShape.m_p.set(point);
        mEpsilonShape.m_p.mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        
        Body body = entity.mBody;
        mDistanceInput.transformB.set(body.getPosition(), body.getAngle());
        mDistanceInput.proxyB.set(body.getFixtureList().getShape(), 0);
        mDistanceInput.useRadii = (body.getFixtureList().getType() == ShapeType.CIRCLE);
        
        mWorld.getPool().getDistance().distance(mDistanceOutput, mSimplexCache, mDistanceInput);
        
        TEMP_ENTITY_DISTANCE.mClosestPoint.set(mDistanceOutput.pointB);
        TEMP_ENTITY_DISTANCE.mClosestPoint.mulLocal(PhysicsConnector.METER_TO_PIXEL_RATIO);
        TEMP_ENTITY_DISTANCE.mDistance = mDistanceOutput.distance * PhysicsConnector.METER_TO_PIXEL_RATIO;
        
        return TEMP_ENTITY_DISTANCE;
    }
    
    public RayCastResults rayCast(Vec2 from, Vec2 to, int filterBits) {
        TEMP_VECTORS[0].set(from);
        TEMP_VECTORS[0].mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        
        TEMP_VECTORS[1].set(to);
        TEMP_VECTORS[1].mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        
        mReportedFixture = null;
        mFilterBits = filterBits;
        mWorld.raycast(this, TEMP_VECTORS[0], TEMP_VECTORS[1]);
        
        if (mReportedFixture != null) {
            TEMP_RAYCAST_RESULTS.mPhysicsEntity = (PhysicsEntity)mReportedFixture.getUserData();
            
            TEMP_RAYCAST_RESULTS.mIntersection.set(mInitialIntersection);
            TEMP_RAYCAST_RESULTS.mIntersection.mulLocal(PhysicsConnector.METER_TO_PIXEL_RATIO);
        } else {
            TEMP_RAYCAST_RESULTS.mPhysicsEntity = null;
        }
        
        return TEMP_RAYCAST_RESULTS;
    }
    
    public SearchResults searchEntities(Vec2 position, float radius, int filterBits) {
        mSearchCenter.set(position);
        mSearchCenter.mulLocal(PhysicsConnector.PIXEL_TO_METER_RATIO);
        
        mSearchRadius = radius * PhysicsConnector.PIXEL_TO_METER_RATIO;
        mFilterBits = filterBits;
        
        TEMP_AABB.lowerBound.set(mSearchCenter.x - mSearchRadius, mSearchCenter.y - mSearchRadius);
        TEMP_AABB.upperBound.set(mSearchCenter.x + mSearchRadius, mSearchCenter.y + mSearchRadius);
        
        TEMP_SEARCH_RESULTS.mCount = 0;
        mWorld.queryAABB(this, TEMP_AABB);
        
        return TEMP_SEARCH_RESULTS;
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
    
    public PhysicsEntity createChainEntity(PhysicsEntityDef entityDef, Vec2[] vertices, boolean loop, Vec2 nextGhostVertex, Vec2 prevGhostVertex) {
        Body body = createBody(entityDef);
        
        PhysicsEntity entity = new PhysicsEntity(body, entityDef.mUserData);
        
        Vec2[] chain = new Vec2[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            chain[i] = vertices[i].mul(PhysicsConnector.PIXEL_TO_METER_RATIO);
        }
        
        // Attaching chain shape
        if (loop) {
            TEMP_CHAIN_SHAPE.createLoop(chain, chain.length);
        } else {
            TEMP_CHAIN_SHAPE.createChain(chain, chain.length);
            
            TEMP_CHAIN_SHAPE.m_hasPrevVertex = (prevGhostVertex != null);
            TEMP_CHAIN_SHAPE.m_hasNextVertex = (nextGhostVertex != null);
            
            if (TEMP_CHAIN_SHAPE.m_hasPrevVertex) {
                TEMP_CHAIN_SHAPE.m_prevVertex.set(prevGhostVertex);
            }
            if (TEMP_CHAIN_SHAPE.m_hasNextVertex) {
                TEMP_CHAIN_SHAPE.m_nextVertex.set(nextGhostVertex);
            }
        }
        TEMP_FIXTURE_DEF.shape = TEMP_CHAIN_SHAPE;
        
        fillTempFixtureDef(entityDef);
        TEMP_FIXTURE_DEF.userData = entity;
        TEMP_FIXTURE_DEF.density = 0; // chain always has zero mass
        body.createFixture(TEMP_FIXTURE_DEF);
        
        return entity;
    }
    
    public void destroyEntity(PhysicsEntity entity) {
        unregisterConnector(entity);
        mWorld.destroyBody(entity.mBody);
    }
    
    public void destroyAllEntities() {
        Body body;
        
        while ((body = mWorld.getBodyList()) != null) {
            mWorld.destroyBody(body);
        }
        
        mPhysicsConnectors.clear();
    }
    
    public void registerConnector(PhysicsEntity entity, IAreaShape shape) {
        // unregister previous connector
        unregisterConnector(entity);
        
        entity.mConnector = new PhysicsConnector(shape, entity.mBody);
        mPhysicsConnectors.add(entity.mConnector);
    }
    
    public void unregisterConnector(PhysicsEntity entity) {
        if (entity.mConnector != null) {
            mPhysicsConnectors.remove(entity.mConnector);
            entity.mConnector = null;
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
        
        Body body = mWorld.createBody(TEMP_BODY_DEF);
        
        if (!entityDef.mIsGravitated) {
            body.setGravityScale(0);
        }
        
        if (TEMP_SEARCH_RESULTS.mEntities.length < mWorld.getBodyCount()) {
            TEMP_SEARCH_RESULTS.mEntities = new PhysicsEntity[(mWorld.getBodyCount() >> 1) + 1];
        }
        
        return body;
    }
    
    private void fillTempFixtureDef(PhysicsEntityDef entityDef) {
        TEMP_FIXTURE_DEF.friction = entityDef.mFriction;
        TEMP_FIXTURE_DEF.restitution = entityDef.mRestitution;
        TEMP_FIXTURE_DEF.isSensor = entityDef.mIsSensor;
        TEMP_FIXTURE_DEF.filter.categoryBits = entityDef.mCategoryBits;
        TEMP_FIXTURE_DEF.filter.maskBits = entityDef.mFilterBits;
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        boolean isSensorA = fixtureA.isSensor();
        boolean isSensorB = fixtureB.isSensor();
        
        if (isSensorA && !isSensorB) {
            mContactListener.onSensorTouched((PhysicsEntity)fixtureA.getUserData(), (PhysicsEntity)fixtureB.getUserData());
        } else if (!isSensorA && isSensorB) {
            mContactListener.onSensorTouched((PhysicsEntity)fixtureB.getUserData(), (PhysicsEntity)fixtureA.getUserData());
        }
    }

    @Override
    public void endContact(Contact contact) {
        // not needed for now
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        
        int solution = mContactListener.onContact((PhysicsEntity)fixtureA.getUserData(), (PhysicsEntity)fixtureB.getUserData());
        
        if ((solution & CONTACT_SOLUTION_IGNORE) > 0) {
            contact.setEnabled(false);
        }
        
        if ((solution & CONTACT_SOLUTION_STOP_A) > 0) {
            Body body = fixtureA.getBody();
            
            body.setLinearVelocity(ZERO_VECTOR);
            body.setAngularVelocity(0);
        }

        if ((solution & CONTACT_SOLUTION_STOP_B) > 0) {
            Body body = fixtureB.getBody();
            
            body.setLinearVelocity(ZERO_VECTOR);
            body.setAngularVelocity(0);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // not needed for now
    }

    @Override
    public float reportFixture(Fixture fixture, Vec2 point, Vec2 normal, float fraction) {
        if (fixture.isSensor() || (mFilterBits & fixture.getFilterData().categoryBits) == 0) {
            return (-1); // ignore sensors and fixtures not matching filter
        }
        
        mReportedFixture = fixture;
        mInitialIntersection.set(point);
        
        return fraction;
    }

    @Override
    public boolean reportFixture(Fixture fixture) {
        if (fixture.isSensor() || (mFilterBits & fixture.getFilterData().categoryBits) == 0) {
            return true; // ignore sensors and fixtures not matching filter
        }
        
        Body body = fixture.getBody();
        
        TEMP_VECTORS[0].set(body.getPosition());
        TEMP_VECTORS[0].subLocal(mSearchCenter);
        
        float radius = ((Float)body.getUserData()) + mSearchRadius;
        
        if (TEMP_VECTORS[0].lengthSquared() < (radius * radius)) {
            TEMP_SEARCH_RESULTS.mEntities[TEMP_SEARCH_RESULTS.mCount++] = (PhysicsEntity)fixture.getUserData();
        }
        
        return true;
    }
}