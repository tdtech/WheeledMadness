package com.tdtech.wheeledmadness.world.builder;

import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.world.WMWorld;

interface IWMWorldElement {
    
    enum WMWorldElementType {
        VERTEX("Vertex"),
        GHOST_VERTEX("GhostVertex"),
        LINE("Line"),
        CHAIN("Chain"),
        LINE_BATCH("LineBatch");
        
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
    
    void parse(Attributes attrs, IWMWorldElement parent);
    void parseInnerElement(IWMWorldElement element);
    
    void postToWorld(WMWorld world);
}