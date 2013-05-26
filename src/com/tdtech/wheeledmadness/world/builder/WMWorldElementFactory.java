package com.tdtech.wheeledmadness.world.builder;

import java.util.HashMap;

import com.tdtech.wheeledmadness.world.builder.IWMWorldElement.WMWorldElementType;

import android.util.SparseArray;

class WMWorldElementFactory {
    
    private SparseArray<HashMap<String, IWMWorldElement>> mDeepCache;
    private WMWorldStubElement mStubElement;
    
    WMWorldElementFactory() {
        mDeepCache = new SparseArray<HashMap<String, IWMWorldElement>>();
        mStubElement = new WMWorldStubElement();
    }
    
    IWMWorldElement getElementByName(String localName, int deep) {
        HashMap<String, IWMWorldElement> elementsCache = mDeepCache.get(deep);
        if (elementsCache == null) {
            elementsCache = new HashMap<String, IWMWorldElement>();
            mDeepCache.put(deep, elementsCache);
        }
        
        IWMWorldElement element = elementsCache.get(localName);
        
        if (element == null) {
            if (localName.equals(WMWorldElementType.VERTEX.toString())) {
                element = new WMWorldVertexElement();
            } else if (localName.equals(WMWorldElementType.LINE.toString())) {
                element = new WMWorldLineElement();
            } else if (localName.equals(WMWorldElementType.CHAIN.toString())) {
                element = new WMWorldChainElement();
            } else {
                element = mStubElement;
            }
            
            elementsCache.put(localName, element);
        }
        
        return element;
    }
    
    IWMWorldElement getStubElement() {
        return mStubElement;
    }
    
    void clearCache() {
        for (int i = 0, size = mDeepCache.size(); i < size; i++) {
            mDeepCache.valueAt(i).clear();
        }
        
        mDeepCache.clear();
    }
}