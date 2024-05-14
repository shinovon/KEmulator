package emulator;

import java.net.URL;
import java.net.URLClassLoader;

public class Java9ClassLoader extends URLClassLoader {

    public Java9ClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public void addURL(URL url) {
        super.addURL(url);
    }
}
