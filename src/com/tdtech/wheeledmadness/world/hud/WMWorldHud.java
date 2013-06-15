package com.tdtech.wheeledmadness.world.hud;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.tdtech.wheeledmadness.world.WMWorldCamera;

public class WMWorldHud extends HUD {
    
    private WMWheelFireController mFireController;
    private WMWheelMoveController mMoveController;
    
    public WMWorldHud(WMWorldCamera camera, VertexBufferObjectManager vboManager) {
        super();
        
        this.setTouchAreaBindingOnActionDownEnabled(true);
        camera.setHUD(this);
        
        mFireController = new WMWheelFireController(camera, vboManager);
        mMoveController = new WMWheelMoveController(camera, vboManager);
        
        mFireController.setChildScene(mMoveController);
        this.setChildScene(mFireController);
    }
    
    public WMWheelFireController getFireController() {
        return mFireController;
    }
    
    public WMWheelMoveController getMoveController() {
        return mMoveController;
    }

}