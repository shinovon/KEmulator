package com.nttdocomo;

import com.nttdocomo.util.EventListener;

public abstract interface StarEventListener
        extends EventListener {
    public abstract void updateStarApplication(StarEventObject paramStarEventObject);
}
