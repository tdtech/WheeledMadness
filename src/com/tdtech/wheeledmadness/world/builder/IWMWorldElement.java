package com.tdtech.wheeledmadness.world.builder;

import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.world.WMWorld;

interface IWMWorldElement {
    
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
    void parseInnerElement(IWMWorldElement element);
    
    void postToWorld(WMWorld world);
}