package com.tdtech.wheeledmadness.world.shapes;

public class WMWorldShapeFactory {
    
    public static IWMWorldShape createCircleShape(float radius) {
        return new WMWorldCircleShape(radius);
    }
    
    public static IWMWorldShape createRectangleShape(float width, float height) {
        return new WMWorldRectangleShape(width, height);
    }
}