package com.tdtech.wheeledmadness.world.builder;

import org.andengine.entity.primitive.Line;
import org.andengine.util.color.ColorUtils;
import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;

import android.graphics.Color;

import com.tdtech.wheeledmadness.world.WMWorld;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntityDef;
import com.tdtech.wheeledmadness.world.physics.PhysicsWorld;

class WMWorldLineElement implements IWMWorldElement {

    private static PhysicsEntityDef TEMP_ENTITY_DEF = new PhysicsEntityDef();
    
    Vec2 mStart;
    Vec2 mEnd;
    
    int mColor;
    float mLineWidth;
    
    float mFriction;
    float mRestitution;
    
    private Vec2 mLeftGhostVertex;
    private Vec2 mRightGhostVertex;
    
    private boolean mHasLeftGhostVertex;
    private boolean mHasRightGhostVertex;
    
    private boolean mHasLineBatchParent;
    
    WMWorldLineElement() {
        mStart = new Vec2();
        mEnd = new Vec2();
        
        mLeftGhostVertex = new Vec2();
        mRightGhostVertex = new Vec2();
    }
    
    @Override
    public WMWorldElementType getElementType() {
        return WMWorldElementType.LINE;
    }

    @Override
    public void parse(Attributes attrs, IWMWorldElement parent) {
        mStart.x = WMWorldAttributes.parseFloat(WMWorldAttributes.START_X, attrs, 0);
        mStart.y = WMWorldAttributes.parseFloat(WMWorldAttributes.START_Y, attrs, 0);
        
        mEnd.x = WMWorldAttributes.parseFloat(WMWorldAttributes.END_X, attrs, 0);
        mEnd.y = WMWorldAttributes.parseFloat(WMWorldAttributes.END_Y, attrs, 0);
        
        mColor = WMWorldAttributes.parseColor(WMWorldAttributes.COLOR, attrs, Color.BLACK);
        mLineWidth = WMWorldAttributes.parseFloat(WMWorldAttributes.LINE_WIDTH, attrs, 1);
        
        mFriction = WMWorldAttributes.parseFloat(WMWorldAttributes.FRICTION, attrs, 0);
        mRestitution = WMWorldAttributes.parseFloat(WMWorldAttributes.RESTITUTION, attrs, 0);
        
        mHasLeftGhostVertex = false;
        mHasRightGhostVertex = false;
        
        mHasLineBatchParent = (parent.getElementType() == WMWorldElementType.LINE_BATCH);
    }

    @Override
    public void parseInnerElement(IWMWorldElement element) {
        if (element.getElementType() == WMWorldElementType.GHOST_VERTEX) {
            WMWorldGhostVertexElement ghostVertex = (WMWorldGhostVertexElement)element;
            
            switch (ghostVertex.mType) {
                case LEFT:
                    mLeftGhostVertex.set(ghostVertex.mVertex);
                    mHasLeftGhostVertex = true;
                    break;
                case RIGHT:
                    mRightGhostVertex.set(ghostVertex.mVertex);
                    mHasRightGhostVertex = true;
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void postToWorld(WMWorld world) {
        PhysicsWorld physics = world.getWorldPhysics();
        
        TEMP_ENTITY_DEF.mFriction = mFriction;
        TEMP_ENTITY_DEF.mRestitution = mRestitution;
        physics.createEdgeEntity(TEMP_ENTITY_DEF, mStart, mEnd,
                mHasLeftGhostVertex ? mLeftGhostVertex : null,
                mHasRightGhostVertex ? mRightGhostVertex : null);
        
        if (!mHasLineBatchParent) {
            Line line = new Line(mStart.x, mStart.y, mEnd.x, mEnd.y, mLineWidth, world.getEngine().getVertexBufferObjectManager());
            line.setColor(ColorUtils.convertARGBPackedIntToColor(mColor));
            world.getWorldScene().attachChild(line);
        }
    }
    
}