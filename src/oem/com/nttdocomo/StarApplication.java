package com.nttdocomo;

import com.nttdocomo.ui.MApplication;

public abstract class StarApplication extends MApplication {

    private static StarApplication instance;
    private StarApplicationManager mgr;

    public StarApplication() {
        instance = this;
    }

    public abstract void started(int n);

    public final void addEventListener(int paramInt, StarEventListener paramStarEventListener) {
    }

    public static StarApplication getThisStarApplication() {
        return instance;
    }

    public StarApplicationManager getStarApplicationManager() {
        if (mgr == null) {
            mgr = new StarApplicationManager();
        }
        return mgr;
    }

    public void start() {
        started(0);
    }
}
