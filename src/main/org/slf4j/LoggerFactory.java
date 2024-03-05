package org.slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class LoggerFactory {

    private static Logger logger = new LoggerImpl();

    static void reset() {
    }

    private static boolean messageContainsOrgSlf4jImplStaticLoggerBinder(String msg) {
        if (msg == null) {
            return false;
        }
        if (msg.indexOf("org/slf4j/impl/StaticLoggerBinder") != -1) {
            return true;
        }
        if (msg.indexOf("org.slf4j.impl.StaticLoggerBinder") != -1) {
            return true;
        }
        return false;
    }


    static void failedBinding(Throwable t) {
    }


    public static Logger getLogger(String name) {
        return logger;
    }

    public static Logger getLogger(Class clazz) {
        return logger;
    }

    public static ILoggerFactory getILoggerFactory() {
        return null;
    }
}
