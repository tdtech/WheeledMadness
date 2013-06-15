package com.tdtech.wheeledmadness.world.builder;

import java.util.ArrayList;
import java.util.Stack;

import org.andengine.util.color.Color;
import org.andengine.util.color.ColorUtils;
import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.utils.batch.LineBatch;
import com.tdtech.wheeledmadness.world.WMWorld;
import com.tdtech.wheeledmadness.world.physics.PhysicsEntityDef;
import com.tdtech.wheeledmadness.world.physics.PhysicsWorld;

class WMWorldChainElement implements IWMWorldElement {

    static class ChainHolder {
        Vec2 mVertex;
        int mColor;
        
        ChainHolder() {
            mVertex = new Vec2();
        }
    }
    
    float mLineWidth;
    
    float mFriction;
    float mRestitution;
    
    boolean mLoop;
    
    ArrayList<ChainHolder> mChain;
    
    private Vec2 mLeftGhostVertex;
    private Vec2 mRightGhostVertex;
    
    private boolean mHasLeftGhostVertex;
    private boolean mHasRightGhostVertex;
    
    private boolean mHasLineBatchParent;
    
    private static Stack<ChainHolder> CHAIN_BUFFER = new Stack<ChainHolder>();
    private static PhysicsEntityDef TEMP_ENTITY_DEF = new PhysicsEntityDef();
    
    WMWorldChainElement() {
        mChain = new ArrayList<ChainHolder>();
        
        mLeftGhostVertex = new Vec2();
        mRightGhostVertex = new Vec2();
    }
    
    @Override
    public WMWorldElementType getElementType() {
        return WMWorldElementType.CHAIN;
    }

    @Override
    public void parse(Attributes attrs, IWMWorldElement parent) {
        mLineWidth = WMWorldAttributes.parseFloat(WMWorldAttributes.LINE_WIDTH, attrs, 1);
        
        mFriction = WMWorldAttributes.parseFloat(WMWorldAttributes.FRICTION, attrs, 0);
        mRestitution = WMWorldAttributes.parseFloat(WMWorldAttributes.RESTITUTION, attrs, 0);
        
        mLoop = WMWorldAttributes.parseBoolean(WMWorldAttributes.LOOP, attrs, false);
        
        mHasLeftGhostVertex = false;
        mHasRightGhostVertex = false;
        
        mHasLineBatchParent = (parent.getElementType() == WMWorldElementType.LINE_BATCH);
    }

    @Override
    public void parseInnerElement(IWMWorldElement element) {
        WMWorldElementType elementType = element.getElementType();
        
        switch (elementType) {
            case VERTEX:
                parseVertexElement((WMWorldVertexElement)element);
                break;
            case GHOST_VERTEX:
                parseGhostVertexElement((WMWorldGhostVertexElement)element);
                break;
            default:
                break;
        }
    }

    @Override
    public void postToWorld(WMWorld world) {
        int chainSize = mChain.size();
        
        if (chainSize > 1) {
            // post only valid chain
            PhysicsWorld physics = world.getWorldPhysics();
            boolean hasLoop = mLoop && (chainSize > 2);
        
            Vec2[] vertices = new Vec2[chainSize];
            for (int i = 0; i < chainSize; i++) {
            	vertices[i] = mChain.get(i).mVertex;
            }
            
            TEMP_ENTITY_DEF.mFriction = mFriction;
            TEMP_ENTITY_DEF.mRestitution = mRestitution;
            physics.createChainEntity(TEMP_ENTITY_DEF, vertices, hasLoop,
                    mHasLeftGhostVertex ? mLeftGhostVertex : null,
                    mHasRightGhostVertex ? mRightGhostVertex : null);
            
            if (!mHasLineBatchParent) {
                LineBatch lineBatch = new LineBatch(chainSize - (hasLoop ? 0 : 1), mLineWidth, world.getEngine().getVertexBufferObjectManager());
                
                ChainHolder holder = mChain.get(0);
                
                Vec2 start = holder.mVertex;
                int argb = holder.mColor;
                
                Color color = new Color(0, 0, 0, 1);
                float a, r, g, b;
                
                for (int i = 1; i < chainSize; i++) {
                    holder = mChain.get(i);
                    
                    Vec2 end = holder.mVertex;
                    
                    a = ColorUtils.extractAlphaFromARGBPackedInt(argb);
                    r = ColorUtils.extractRedFromARGBPackedInt(argb);
                    g = ColorUtils.extractGreenFromARGBPackedInt(argb);
                    b = ColorUtils.extractBlueFromARGBPackedInt(argb);
                    
                    color.set(r, g, b, a);
                    
                    lineBatch.addLine(start.x, start.y, end.x, end.y, color);
                    
                    start = end;
                    argb = holder.mColor;
                }
                
                if (hasLoop) {
                    Vec2 end = mChain.get(0).mVertex;
                    
                    a = ColorUtils.extractAlphaFromARGBPackedInt(argb);
                    r = ColorUtils.extractRedFromARGBPackedInt(argb);
                    g = ColorUtils.extractGreenFromARGBPackedInt(argb);
                    b = ColorUtils.extractBlueFromARGBPackedInt(argb);
                    
                    color.set(r, g, b, a);
                    
                    lineBatch.addLine(start.x, start.y, end.x, end.y, color);
                }
                
                lineBatch.submit();
                
                world.getWorldScene().attachChild(lineBatch);
            }
        }
        
        for (ChainHolder holder : mChain) {
            CHAIN_BUFFER.push(holder);
        }
        mChain.clear();
    }
    
    private void parseVertexElement(WMWorldVertexElement vertex) {
        if (CHAIN_BUFFER.isEmpty()) {
            increaseChainBuffer(16);
        }
        
        ChainHolder holder = CHAIN_BUFFER.pop();
        holder.mVertex.set(vertex.mOrigin);
        holder.mColor = vertex.mColor;
        mChain.add(holder);
    }
    
    private void parseGhostVertexElement(WMWorldGhostVertexElement ghostVertex) {
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
    
    private static void increaseChainBuffer(int size) {
        for (int i = 0; i < size; i++) {
            CHAIN_BUFFER.push(new ChainHolder());
        }
    }
    
}