package com.tdtech.wheeledmadness.world.hud;

import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;

class AnalogControllerState implements IAnalogOnScreenControlListener {

    float mControlValueX;
    float mControlValueY;
    
    boolean mControlClicked;
    
    @Override
    public void onControlChange(BaseOnScreenControl pBaseOnScreenControl,
            float pValueX, float pValueY) {
        mControlValueX = pValueX;
        mControlValueY = pValueY;
    }

    @Override
    public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
        mControlClicked = true;
    }
    
}