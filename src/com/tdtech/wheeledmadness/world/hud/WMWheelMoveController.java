package com.tdtech.wheeledmadness.world.hud;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.tdtech.wheeledmadness.content.ContentManager;
import com.tdtech.wheeledmadness.content.TextureId;
import com.tdtech.wheeledmadness.world.WMWorldCamera;

public class WMWheelMoveController extends AnalogOnScreenControl {
    
    private static final float TIME_BETWEEN_UPDATES = 0.1f;
    private static final long CLICK_TIMEOUT = 150;
    
    private AnalogControllerState mControllerState;
    
    private boolean mJump;
    
    public float getControlValueX() {
        return mControllerState.mControlValueX;
    }
    
    public float getControlValueY() {
        return mControllerState.mControlValueY;
    }
    
    public boolean isJumping() {
        return mJump;
    }
    
    public WMWheelMoveController(WMWorldCamera camera, VertexBufferObjectManager vboManager) {
        super(0, 0, camera, getBaseControl(), getKnobControl(), TIME_BETWEEN_UPDATES, CLICK_TIMEOUT, vboManager, new AnalogControllerState());
        
        mControllerState = (AnalogControllerState)this.getOnScreenControlListener();
        // TODO: compute size and position
    }
    
    private static ITextureRegion getBaseControl() {
        ITexture texture = ContentManager.getInstance().getTextureById(TextureId.HUD);
        ITextureRegion region = TextureRegionFactory.extractFromTexture(texture, 0, 64, 64, 64);
        
        return region;
    }
    
    private static ITextureRegion getKnobControl() {
        ITexture texture = ContentManager.getInstance().getTextureById(TextureId.HUD);
        ITextureRegion region = TextureRegionFactory.extractFromTexture(texture, 64, 16, 16, 16);
        
        return region;
    }
    
    @Override
    protected void onManagedUpdate(final float pSecondsElapsed) {
        super.onManagedUpdate(pSecondsElapsed);
        
        mJump = mControllerState.mControlClicked;
        mControllerState.mControlClicked = false;
    }
}