package com.tdtech.wheeledmadness.world.builder;

import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;

import android.graphics.Color;

import com.tdtech.wheeledmadness.world.WMWorld;

class WMWorldVertexElement implements IWMWorldElement {

    Vec2 mOrigin;
    int mColor;
    
    WMWorldVertexElement() {
        mOrigin = new Vec2();
    }
    
    @Override
    public WMWorldElementType getElementType() {
        return WMWorldElementType.VERTEX;
    }
    
    @Override
    public void parse(Attributes attrs, IWMWorldElement parent) {
        mOrigin.x = WMWorldAttributes.parseFloat(WMWorldAttributes.ORIGIN_X, attrs, 0);
        mOrigin.y = WMWorldAttributes.parseFloat(WMWorldAttributes.ORIGIN_Y, attrs, 0);
        
        mColor = WMWorldAttributes.parseColor(WMWorldAttributes.COLOR, attrs, Color.BLACK);
    }

    @Override
    public void parseInnerElement(IWMWorldElement element) {
        // vertex has no inner elements
    }

    @Override
    public void postToWorld(WMWorld world) {
        // do nothing
    }
    
}