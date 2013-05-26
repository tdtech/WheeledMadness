package com.tdtech.wheeledmadness.world.builder;

import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.world.WMWorld;

interface WMWorldElement {
    
    enum WMWorldElementType {
        VERTEX("Vertex"),
        LINE("Line"),
        CHAIN("Chain");
        
        private String mLocalName;
        
        private WMWorldElementType(String localName) {
            mLocalName = localName;
        }
        
        @Override
        public String toString() {
            return mLocalName;
        }
    }
    
    WMWorldElementType getElementType();
    
    void parseAttributes(Attributes attrs);
    void parseInnerElement(WMWorldElement element);
    
    void postToWorld(WMWorld world);
}