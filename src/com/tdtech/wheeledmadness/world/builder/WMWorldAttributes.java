package com.tdtech.wheeledmadness.world.builder;

import org.xml.sax.Attributes;

import android.graphics.Color;

final class WMWorldAttributes {
    
    static final String ORIGIN_X = "originX";
    static final String ORIGIN_Y = "originY";
    
    static final String START_X = "startX";
    static final String START_Y = "startY";
    static final String END_X = "endX";
    static final String END_Y = "endY";
    
    static final String COLOR = "color";
    static final String LINE_WIDTH = "line_width";
    
    static final String FRICTION = "friction";
    static final String RESTITUTION = "restitution";
    
    static final String LOOP = "loop";
    
    static final String GHOST_VERTEX_TYPE = "type";
    
    static boolean hasAttribute(String name, Attributes attrs) {
        return (attrs.getValue(name) != null);
    }
    
    static String parseString(String name, Attributes attrs, String defaultValue) {
        String value = attrs.getValue(name);
        return (value == null ? defaultValue : value);
    }
    
    static int parseInt(String name, Attributes attrs, int defaultValue) {
        int value;
        
        try {
            value = Integer.parseInt(attrs.getValue(name));
        } catch (Exception e) {
            value = defaultValue;
        }
        
        return value;
    }
    
    static float parseFloat(String name, Attributes attrs, float defaultValue) {
        float value;
        
        try {
            value = Float.parseFloat(attrs.getValue(name));
        } catch (Exception e) {
            value = defaultValue;
        }
        
        return value;
    }
    
    static boolean parseBoolean(String name, Attributes attrs, boolean defaultValue) {
        boolean value;
        
        try {
            value = Boolean.parseBoolean(attrs.getValue(name));
        } catch (Exception e) {
            value = defaultValue;
        }
        
        return value;
    }
    
    static int parseColor(String name, Attributes attrs, int defaultValue) {
        int value;
        
        try {
            value = Color.parseColor(attrs.getValue(name));
        } catch (Exception e) {
            value = defaultValue;
        }
        
        return value;
    }
    
}