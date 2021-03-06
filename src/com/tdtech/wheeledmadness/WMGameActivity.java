package com.tdtech.wheeledmadness;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.ui.activity.SimpleAsyncGameActivity;
import org.andengine.util.progress.IProgressListener;

import com.tdtech.wheeledmadness.world.WMWorld;
import com.tdtech.wheeledmadness.world.WMWorldCamera;
import com.tdtech.wheeledmadness.world.builder.WMWorldBuilder;

public class WMGameActivity extends SimpleAsyncGameActivity {

    public static final int FPS = 30;
    
    @Override
    public EngineOptions onCreateEngineOptions() {
        EngineOptions options = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR,
                new RatioResolutionPolicy(WMWorldCamera.WIDTH, WMWorldCamera.HEIGHT), new WMWorldCamera());
        
        options.getTouchOptions().setNeedsMultiTouch(true);
        
        return options;
    }
    
    @Override
    public Engine onCreateEngine(final EngineOptions pEngineOptions) {
        return new LimitedFPSEngine(pEngineOptions, FPS);
    }

    @Override
    public void onCreateResourcesAsync(IProgressListener pProgressListener)
            throws Exception {

    }

    @Override
    public Scene onCreateSceneAsync(IProgressListener pProgressListener)
            throws Exception {
        WMWorld.init(getEngine());
        
        WMWorld world = WMWorld.getInstance();
        
        WMWorldBuilder builder = new WMWorldBuilder(world);
        builder.buildFromXML(getAssets().open("level.xml"));
        
        return world.getWorldScene();
    }

    @Override
    public void onPopulateSceneAsync(Scene pScene,
            IProgressListener pProgressListener) throws Exception {

    }

}