package org.slf4j;

public final class LoggerFactory {

    private static Logger logger = new LoggerImpl();

    static void reset() {
    }

    private static boolean messageContainsOrgSlf4jImplStaticLoggerBinder(String msg) {
        if (msg == null) {
            return false;
        }
        if (msg.contains("org/slf4j/impl/StaticLoggerBinder")) {
            return true;
        }
        if (msg.contains("org.slf4j.impl.StaticLoggerBinder")) {
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
