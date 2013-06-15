package com.tdtech.wheeledmadness.world.builder;

import java.util.ArrayList;
import java.util.Stack;

import org.andengine.util.color.Color;
import org.andengine.util.color.ColorUtils;
import org.jbox2d.common.Vec2;
import org.xml.sax.Attributes;

import com.tdtech.wheeledmadness.utils.batch.LineBatch;
import com.tdtech.wheeledmadness.world.WMWorld;
import com.tdtech.wheeledmadness.world.builder.WMWorldChainElement.ChainHolder;

class WMWorldLineBatchElement implements IWMWorldElement {

    static class LineHolder {
        Vec2 mStart;
        Vec2 mEnd;
        
        int mColor;
        
        LineHolder() {
            mStart = new Vec2();
            mEnd = new Vec2();
        }
    }
    
    float mLineWidth;
    
    ArrayList<LineHolder> mLines;
    
    private static Stack<LineHolder> LINE_BUFFER = new Stack<LineHolder>();
    
    WMWorldLineBatchElement() {
        mLines = new ArrayList<LineHolder>();
    }
    
    @Override
    public WMWorldElementType getElementType() {
        return WMWorldElementType.LINE_BATCH;
    }

    @Override
    public void parse(Attributes attrs, IWMWorldElement parent) {
        mLineWidth = WMWorldAttributes.parseFloat(WMWorldAttributes.LINE_WIDTH, attrs, 1);
    }

    @Override
    public void parseInnerElement(IWMWorldElement element) {
        WMWorldElementType elementType = element.getElementType();
        
        switch (elementType) {
            case LINE:
                parseLineElement((WMWorldLineElement)element);
                break;
            case CHAIN:
                parseChainElement((WMWorldChainElement)element);
                break;
            default:
                break;
        }
    }

    @Override
    public void postToWorld(WMWorld world) {
        int linesCount = mLines.size();
        
        if (linesCount > 0) {
            LineBatch lineBatch = new LineBatch(linesCount, mLineWidth, world.getEngine().getVertexBufferObjectManager());
            
            Color color = new Color(0, 0, 0, 1);
            float a, r, g, b;
            
            for (LineHolder holder : mLines) {
            	a = ColorUtils.extractAlphaFromARGBPackedInt(holder.mColor);
                r = ColorUtils.extractRedFromARGBPackedInt(holder.mColor);
                g = ColorUtils.extractGreenFromARGBPackedInt(holder.mColor);
                b = ColorUtils.extractBlueFromARGBPackedInt(holder.mColor);
                
                color.set(r, g, b, a);
                
                lineBatch.addLine(holder.mStart.x, holder.mStart.y,
                        holder.mEnd.x, holder.mEnd.y, color);
                
                LINE_BUFFER.push(holder);
            }
            lineBatch.submit();
            
            world.getWorldScene().attachChild(lineBatch);
            
            mLines.clear();
        }
    }
    
    private void parseLineElement(WMWorldLineElement line) {
        addLineHolder(line.mStart, line.mEnd, line.mColor);
    }
    
    private void parseChainElement(WMWorldChainElement chain) {
        int chainSize = chain.mChain.size();
        
        if (chainSize > 1) {
            for (int i = 1; i < chainSize; i++) {
                ChainHolder holder = chain.mChain.get(i - 1);
                addLineHolder(holder.mVertex, chain.mChain.get(i).mVertex, holder.mColor);
            }
            
            if (chain.mLoop && chainSize > 2) {
                ChainHolder holder = chain.mChain.get(chainSize - 1);
                addLineHolder(holder.mVertex, chain.mChain.get(0).mVertex, holder.mColor);
            }
        }
    }
    
    private void addLineHolder(Vec2 start, Vec2 end, int color) {
        if (LINE_BUFFER.isEmpty()) {
            increaseLineBuffer(32);
        }
        
        LineHolder holder = LINE_BUFFER.pop();
        holder.mStart.set(start);
        holder.mEnd.set(end);
        holder.mColor = color;
        mLines.add(holder);
    }
    
    private static void increaseLineBuffer(int size) {
        for (int i = 0; i < size; i++) {
            LINE_BUFFER.push(new LineHolder());
        }
    }
}