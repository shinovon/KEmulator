package com.nokia.mid.ui.gestures;

public interface GestureEvent {
    float getFlickDirection();
    int getStartX();
    int getStartY();
    int getType();
}
