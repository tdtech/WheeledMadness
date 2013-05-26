package com.tdtech.wheeledmadness.world.builder;

import java.util.HashMap;

import com.tdtech.wheeledmadness.world.builder.WMWorldElement.WMWorldElementType;

import android.util.SparseArray;

class WMWorldElementFactory {
    
    private SparseArray<HashMap<String, WMWorldElement>> mDeepCache;
    private WMWorldStubElement mStubElement;
    
    WMWorldElementFactory() {
        mDeepCache = new SparseArray<HashMap<String, WMWorldElement>>();
        mStubElement = new WMWorldStubElement();
    }
    
    WMWorldElement getElementByName(String localName, int deep) {
        HashMap<String, WMWorldElement> elementsCache = mDeepCache.get(deep);
        if (elementsCache == null) {
            elementsCache = new HashMap<String, WMWorldElement>();
            mDeepCache.put(deep, elementsCache);
        }
        
        WMWorldElement element = elementsCache.get(localName);
        
        if (element == null) {
            if (localName.equals(WMWorldElementType.VERTEX)) {
                element = new WMWorldVertexElement();
            } else if (localName.equals(WMWorldElementType.LINE)) {
                element = new WMWorldLineElement();
            } else if (localName.equals(WMWorldElementType.CHAIN)) {
                element = new WMWorldChainElement();
            } else {
                element = mStubElement;
            }
            
            elementsCache.put(localName, element);
        }
        
        return element;
    }
    
    WMWorldElement getStubElement() {
        return mStubElement;
    }
    
    void clearCache() {
        for (int i = 0, size = mDeepCache.size(); i < size; i++) {
            mDeepCache.valueAt(i).clear();
        }
        
        mDeepCache.clear();
    }
}