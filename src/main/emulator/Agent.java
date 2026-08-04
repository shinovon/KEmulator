/*
Copyright (c) 2024 Arman Jussupgaliyev
*/
package emulator;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

public class Agent {
	private static Instrumentation inst;

	public static void premain(String a, Instrumentation inst) {
		Agent.inst = inst;
	}

	public static void agentmain(String a, Instrumentation inst) {
		Agent.inst = inst;
	}

	public static void addClassPath(File f) throws Exception {
		ClassLoader cl = ClassLoader.getSystemClassLoader();

		if (!(cl instanceof URLClassLoader)) {
			inst.appendToSystemClassLoaderSearch(new JarFile(f));
			return;
		}

		Method m = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		m.setAccessible(true);
		m.invoke(cl, f.toURI().toURL());
	}

}
