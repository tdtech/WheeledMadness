package com.tdtech.wheeledmadness.content;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureManager;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.bitmap.AssetBitmapTexture;
import org.andengine.util.progress.IProgressListener;

import com.tdtech.wheeledmadness.log.WMLog;

import android.content.res.AssetManager;

public class ContentManager {
    
    private Engine mEngine;
    private AssetManager mAssetManager;
    
    private ITexture[] mTextures;
    
    private static ContentManager mInstance = null;
    
    private ContentManager(Engine engine, AssetManager assetManager) {
        mEngine = engine;
        mAssetManager = assetManager;
        mTextures = new ITexture[TextureId.COUNT.ordinal()];
    }
    
    public static void init(Engine engine, AssetManager assetManager) {
        mInstance = new ContentManager(engine, assetManager);
    }
    
    public static ContentManager getInstance() {
        return mInstance;
    }
    
    public static void release() {
        mInstance = null;
    }
    
    public void loadResources(IProgressListener progressCallback) {
        loadTextures(progressCallback);
        loadSounds(progressCallback);
    }
    
    public ITexture getTextureById(TextureId id) {
        return mTextures[id.ordinal()];
    }
    
    private void loadTextures(IProgressListener progressCallback) {
        TextureId[] textureIds = TextureId.values();
        TextureManager textureManager = mEngine.getTextureManager();
        
        ITexture texture;
        for (int i = 0; i < textureIds.length; i++) {
            texture = null;

            try {
                // TODO: attach TextureOptions to TextureId
                texture = new AssetBitmapTexture(textureManager, mAssetManager,
                        textureIds[i].getAssetFileName(), TextureOptions.BILINEAR);
                texture.load();
            } catch (IOException e) {
                WMLog.e(String.format("Error while reading '%s' file: %s", textureIds[i].getAssetFileName(), e.toString()));
            }
            
            mTextures[textureIds[i].ordinal()] = texture;
        }
    }
    
    private void loadSounds(IProgressListener progressCallback) {
        
    }
}