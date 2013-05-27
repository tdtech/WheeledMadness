package com.tdtech.wheeledmadness.world.builder;

import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.world.WMWorld;

class WMWorldGhostVertexElement implements IWMWorldElement {

    enum GhostVertexType {
        LEFT,
        RIGHT,
        
        UNKNOWN;
        
        static GhostVertexType fromString(String str) {
            GhostVertexType type = UNKNOWN;
            
            if (str.equalsIgnoreCase("left")) {
                type = LEFT;
            } else if (str.equalsIgnoreCase("right")) {
                type = RIGHT;
            }
            
            return type;
        }
    }
    
    GhostVertexType mType;
    Vec2 mVertex;
    
    WMWorldGhostVertexElement() {
        mType = GhostVertexType.UNKNOWN;
        mVertex = new Vec2();
    }
    
    @Override
    public WMWorldElementType getElementType() {
        return WMWorldElementType.GHOST_VERTEX;
    }

    @Override
    public void parse(Attributes attrs, IWMWorldElement parent) {
        mType = GhostVertexType.fromString(WMWorldAttributes.parseString(WMWorldAttributes.GHOST_VERTEX_TYPE, attrs, ""));
        
        mVertex.x = WMWorldAttributes.parseFloat(WMWorldAttributes.ORIGIN_X, attrs, 0);
        mVertex.y = WMWorldAttributes.parseFloat(WMWorldAttributes.ORIGIN_Y, attrs, 0);
    }

    @Override
    public void parseInnerElement(IWMWorldElement element) {
        // ghost vertex has no inner elements
    }

    @Override
    public void postToWorld(WMWorld world) {
        // do nothing
    }
    
}