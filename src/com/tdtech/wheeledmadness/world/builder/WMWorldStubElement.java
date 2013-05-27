package com.tdtech.wheeledmadness.world.builder;

import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.world.WMWorld;

class WMWorldStubElement implements IWMWorldElement {

    @Override
    public WMWorldElementType getElementType() {
        return null;
    }

    @Override
    public void parse(Attributes attrs, IWMWorldElement parent) {
        // stub
    }

    @Override
    public void parseInnerElement(IWMWorldElement element) {
        // stub
    }

    @Override
    public void postToWorld(WMWorld world) {
        // stub
    }
    
}