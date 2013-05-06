package com.tdtech.wheeledmadness.content;

public enum TextureId {
    HUD("hud.png"),
    
    COUNT(null); // always should be the last
    
    private String mAssetFileName;
    
    private TextureId(String assetFileName) {
        mAssetFileName = assetFileName;
    }
    
    public String getAssetFileName() {
        return mAssetFileName;
    }
}