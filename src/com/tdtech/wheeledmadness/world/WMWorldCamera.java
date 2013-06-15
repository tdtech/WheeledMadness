package com.tdtech.wheeledmadness.world;

import org.andengine.engine.camera.ZoomCamera;

public class WMWorldCamera extends ZoomCamera {

    public static final int WIDTH = 720;
    public static final int HEIGHT = 480;
    
    private float mShakeMagnitude = 0;
    
    public WMWorldCamera() {
        super(0, 0, WIDTH, HEIGHT);
    }
    
    public void applyShake(float magnitude) {
        mShakeMagnitude = Math.max(mShakeMagnitude, magnitude);
    }
    
    @Override
    public void onUpdate(final float pSecondsElapsed) {
        super.onUpdate(pSecondsElapsed);
        
        // TODO: do shake
    }
}