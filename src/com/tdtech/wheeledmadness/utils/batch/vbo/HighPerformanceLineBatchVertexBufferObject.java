package com.tdtech.wheeledmadness.utils.batch.vbo;

import org.andengine.entity.primitive.Line;
import org.andengine.entity.primitive.vbo.ILineVertexBufferObject;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.HighPerformanceVertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.util.color.Color;

public class HighPerformanceLineBatchVertexBufferObject extends HighPerformanceVertexBufferObject implements ILineVertexBufferObject {
    
    private int mBufferDataOffset;
    
    public HighPerformanceLineBatchVertexBufferObject(final VertexBufferObjectManager pVertexBufferObjectManager, final int pCapacity, final DrawType pDrawType, final boolean pAutoDispose, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
        super(pVertexBufferObjectManager, pCapacity, pDrawType, pAutoDispose, pVertexBufferObjectAttributes);
    }
    
    public void addLine(final float pX1, final float pY1, final float pX2, final float pY2, final Color pColor) {
        final float[] bufferData = this.mBufferData;
        final float packedColor = pColor.getABGRPackedFloat();
        
        bufferData[mBufferDataOffset + 0 * Line.VERTEX_SIZE + Line.VERTEX_INDEX_X] = pX1;
        bufferData[mBufferDataOffset + 0 * Line.VERTEX_SIZE + Line.VERTEX_INDEX_Y] = pY1;
        
        bufferData[mBufferDataOffset + 1 * Line.VERTEX_SIZE + Line.VERTEX_INDEX_X] = pX2;
        bufferData[mBufferDataOffset + 1 * Line.VERTEX_SIZE + Line.VERTEX_INDEX_Y] = pY2;
        
        bufferData[mBufferDataOffset + 0 * Line.VERTEX_SIZE + Line.COLOR_INDEX] = packedColor;
        bufferData[mBufferDataOffset + 1 * Line.VERTEX_SIZE + Line.COLOR_INDEX] = packedColor;
        
        mBufferDataOffset += Line.LINE_SIZE;
    }

    @Override
    public void onUpdateColor(Line pLine) {
        // do nothing
    }

    @Override
    public void onUpdateVertices(Line pLine) {
        // do nothing
    }
}