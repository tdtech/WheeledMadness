package com.tdtech.wheeledmadness.utils.batch;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.primitive.Line;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import android.opengl.GLES20;

import com.tdtech.wheeledmadness.utils.batch.vbo.HighPerformanceLineBatchVertexBufferObject;

public class LineBatch extends Line {
    
    private final HighPerformanceLineBatchVertexBufferObject mLineBatchVBO;
    private int mCapacity;
    private int mIndex;
    private int mVertices;
    
    public LineBatch(final int pCapacity, final float pLineWidth, final VertexBufferObjectManager pVertextBufferObjectManager) {
        super(0, 0, 0, 0, pLineWidth, new HighPerformanceLineBatchVertexBufferObject(pVertextBufferObjectManager, pCapacity * Line.LINE_SIZE, DrawType.STATIC, true, VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT));
        mLineBatchVBO = (HighPerformanceLineBatchVertexBufferObject)mLineVertexBufferObject;
    }
    
    public boolean addLine(final float pX1, final float pY1, final float pX2, final float pY2, final Color pColor) {
        if (mIndex < mCapacity) {
            mLineBatchVBO.addLine(pX1, pY1, pX2, pY2, pColor);
            mIndex++;
            return true;
        }
        
        return false;
    }
    
    public boolean addLine(final Line pLine) {
        if (mIndex < mCapacity) {
            mLineBatchVBO.addLine(pLine.getX1(), pLine.getY1(), pLine.getX2(), pLine.getY2(), pLine.getColor());
            mIndex++;
            return true;
        }
        
        return false;
    }
    
    public void submit() {
        mVertices = mIndex * Line.VERTICES_PER_LINE;
        mLineBatchVBO.setDirtyOnHardware();
    }
    
    @Override
    protected void draw(final GLState pGLState, final Camera pCamera) {
        mLineBatchVBO.draw(GLES20.GL_LINES, mVertices);
    }
    
    @Override
    public boolean isCulled(final Camera pCamera) {
        return true;
    }
    
}