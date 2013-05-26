package com.tdtech.wheeledmadness.world.builder;

import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.log.WMLog;
import com.tdtech.wheeledmadness.world.WMWorld;

class WMWorldVertexElement implements WMWorldElement {

    Vec2 mOrigin;
    
    WMWorldVertexElement() {
        mOrigin = new Vec2();
    }
    
    @Override
    public WMWorldElementType getElementType() {
        return WMWorldElementType.VERTEX;
    }
    
    @Override
    public void parseAttributes(Attributes attrs) {
        try {
            mOrigin.x = Float.parseFloat(attrs.getValue(WMWorldAttributes.ORIGIN_X));
            mOrigin.y = Float.parseFloat(attrs.getValue(WMWorldAttributes.ORIGIN_Y));
        } catch (NumberFormatException e) {
            WMLog.e("Error while parsing attributes for vertex element: " + e.toString());
        }
    }

    @Override
    public void parseInnerElement(WMWorldElement element) {
        // vertex has no inner elements
    }

    @Override
    public void postToWorld(WMWorld world) {
        // do nothing
    }
    
}