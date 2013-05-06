package com.tdtech.wheeledmadness.world.hud;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.tdtech.wheeledmadness.world.WorldCamera;

public class WorldHud extends HUD {
    
    private WheelFireController mFireController;
    private WheelMoveController mMoveController;
    
    public WorldHud(WorldCamera camera, VertexBufferObjectManager vboManager) {
        super();
        
        this.setTouchAreaBindingOnActionDownEnabled(true);
        this.setCamera(camera);
        
        mFireController = new WheelFireController(camera, vboManager);
        mMoveController = new WheelMoveController(camera, vboManager);
        
        mFireController.setChildScene(mMoveController);
        this.setChildScene(mFireController);
    }
    
    public WheelFireController getFireController() {
        return mFireController;
    }
    
    public WheelMoveController getMoveController() {
        return mMoveController;
    }

}