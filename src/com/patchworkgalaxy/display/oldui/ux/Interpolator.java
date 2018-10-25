package com.patchworkgalaxy.display.oldui.ux;

interface Interpolator<T> {
    
    float distanceBetween(T a, T b);
    
    T getInterpolation(T a, T b, float progress, InterpolationFunction ifunc);
    
    T getValuesForNullInput();
    
}
