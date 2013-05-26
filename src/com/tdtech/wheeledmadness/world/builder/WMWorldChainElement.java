package com.tdtech.wheeledmadness.world.builder;

import java.util.ArrayList;
import java.util.Stack;

import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.log.WMLog;
import com.tdtech.wheeledmadness.world.WMWorld;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntityDef;
import com.tdtech.wheeledmadness.world.physics.PhysicsWorld;

class WMWorldChainElement implements IWMWorldElement {

    int mColor;
    
    float mFriction;
    float mRestitution;
    boolean mLoop;
    
    ArrayList<Vec2> mChain;
    
    private static Stack<Vec2> CHAIN_BUFFER = new Stack<Vec2>();
    private static PhysicsEntityDef TEMP_ENTITY_DEF = new PhysicsEntityDef();
    
    WMWorldChainElement() {
        mChain = new ArrayList<Vec2>();
    }
    
    @Override
    public WMWorldElementType getElementType() {
        return WMWorldElementType.CHAIN;
    }

    @Override
    public void parseAttributes(Attributes attrs) {
        try {
            mFriction = Float.parseFloat(attrs.getValue(WMWorldAttributes.FRICTION));
            mRestitution = Float.parseFloat(attrs.getValue(WMWorldAttributes.RESTITUTION));
            
            mLoop = Boolean.parseBoolean(attrs.getValue(WMWorldAttributes.LOOP));
        } catch (NumberFormatException e) {
            WMLog.e("Error while parsing attributes for chain element: " + e.toString());
        }
    }

    @Override
    public void parseInnerElement(IWMWorldElement element) {
        if (element.getElementType() == WMWorldElementType.VERTEX) {
            WMWorldVertexElement vertex = (WMWorldVertexElement)element;
            
            if (CHAIN_BUFFER.isEmpty()) {
                increaseChainBuffer(16);
            }
            
            Vec2 vec = CHAIN_BUFFER.pop();
            vec.set(vertex.mOrigin);
            mChain.add(vec);
        }
    }

    @Override
    public void postToWorld(WMWorld world) {
        PhysicsWorld physics = world.getPhysics();
        
        TEMP_ENTITY_DEF.mFriction = mFriction;
        TEMP_ENTITY_DEF.mRestitution = mRestitution;
        physics.createChainEntity(TEMP_ENTITY_DEF, (Vec2[])mChain.toArray(), mLoop);
        
        // TODO: put chain to scene
        
        for (Vec2 vec : mChain) {
            CHAIN_BUFFER.push(vec);
        }
        mChain.clear();
    }
    
    private static void increaseChainBuffer(int size) {
        for (int i = 0; i < size; i++) {
            CHAIN_BUFFER.push(new Vec2());
        }
    }
    
}