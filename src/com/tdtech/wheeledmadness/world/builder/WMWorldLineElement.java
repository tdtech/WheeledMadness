package com.tdtech.wheeledmadness.world.builder;

import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.log.WMLog;
import com.tdtech.wheeledmadness.world.WMWorld;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntityDef;
import com.tdtech.wheeledmadness.world.physics.PhysicsWorld;

class WMWorldLineElement implements WMWorldElement {

    private static PhysicsEntityDef TEMP_ENTITY_DEF = new PhysicsEntityDef();
    
    Vec2 mStart;
    Vec2 mEnd;
    
    int mColor;
    
    float mFriction;
    float mRestitution;
    
    WMWorldLineElement() {
        mStart = new Vec2();
        mEnd = new Vec2();
    }
    
    @Override
    public WMWorldElementType getElementType() {
        return WMWorldElementType.LINE;
    }

    @Override
    public void parseAttributes(Attributes attrs) {
        try {
            mStart.x = Float.parseFloat(attrs.getValue(WMWorldAttributes.START_X));
            mStart.y = Float.parseFloat(attrs.getValue(WMWorldAttributes.START_Y));
            
            mEnd.x = Float.parseFloat(attrs.getValue(WMWorldAttributes.END_X));
            mEnd.y = Float.parseFloat(attrs.getValue(WMWorldAttributes.END_Y));
            
            mColor = Integer.parseInt(attrs.getValue(WMWorldAttributes.COLOR));
            
            mFriction = Float.parseFloat(attrs.getValue(WMWorldAttributes.FRICTION));
            mRestitution = Float.parseFloat(attrs.getValue(WMWorldAttributes.RESTITUTION));
        } catch (NumberFormatException e) {
            WMLog.e("Error while parsing attributes for line element: " + e.toString());
        }
    }

    @Override
    public void parseInnerElement(WMWorldElement element) {
        // line has no inner elements
    }

    @Override
    public void postToWorld(WMWorld world) {
        PhysicsWorld physics = world.getPhysics();
        
        TEMP_ENTITY_DEF.mFriction = mFriction;
        TEMP_ENTITY_DEF.mRestitution = mRestitution;
        physics.createEdgeEntity(TEMP_ENTITY_DEF, mStart, mEnd, null, null);
        
        // TODO: put line to scene
    }
    
}