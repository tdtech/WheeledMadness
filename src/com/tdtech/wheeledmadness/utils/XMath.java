package com.tdtech.wheeledmadness.utils;

import org.jbox2d.common.MathUtils;
import org.jbox2d.common.Settings;
import org.jbox2d.common.Vec2;

public class XMath {
    
    private static Vec2[] mUnitVectors = new Vec2[361];
    
    public static void initUnitVectors() {
        float radians = 0;
        for (int angle = 0; angle < 361; angle++) {
            mUnitVectors[angle] = new Vec2((float)Math.cos(radians), -(float)Math.sin(radians));
            radians += MathUtils.DEG2RAD;
        }
    }
    
    public static Vec2 getUnitVector(int angle) {
        return mUnitVectors[angle];
    }
    
    public static Vec2 getUnitVector(float angle) {
        return mUnitVectors[MathUtils.round(angle)];
    }
    
    public static float wrapAngleLower(float angle) {
        return (angle < 0 ? (angle + 360) : angle);
    }
    
    public static float wrapAngleUpper(float angle) {
        return (angle > 360 ? (angle - 360) : angle);
    }
    
    public static float wrapAngle(float angle) {
        angle -= 360 * ((int)(angle / 360.0f));
        if (angle < 0) angle += 360.0f;
        return angle;
    }
}