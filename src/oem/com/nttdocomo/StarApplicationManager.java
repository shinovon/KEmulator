package com.nttdocomo;

import com.nttdocomo.ui.IApplication;

public class StarApplicationManager {

    public static String[] getArgs() {
        return IApplication.getCurrentApp().getArgs();
    }

}
