package com.tdtech.wheeledmadness.world.builder;

import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.world.WMWorld;

class WMWorldStubElement implements WMWorldElement {

    @Override
    public WMWorldElementType getElementType() {
        return null;
    }

    @Override
    public void parseAttributes(Attributes attrs) {
        // stub
    }

    @Override
    public void parseInnerElement(WMWorldElement element) {
        // stub
    }

    @Override
    public void postToWorld(WMWorld world) {
        // stub
    }
    
}