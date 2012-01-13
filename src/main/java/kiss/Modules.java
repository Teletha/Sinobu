/*
 * Copyright (C) 2012 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package kiss;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import kiss.model.ClassUtil;
import kiss.model.Model;

/**
 * @version 2011/11/19 19:05:38
 */
@Manageable(lifestyle = Singleton.class)
class Modules implements ClassListener {

    /** The module list. */
    final List<Module> modules = new CopyOnWriteArrayList();

    /**
     * The two length class array for class load listener. (0 : ClassLoadListener class, 1 : Target
     * class to listen)
     */
    final List<Object[]> types = new CopyOnWriteArrayList();

    /**
     * Avoid construction
     */
    Modules() {
        // built-in ClassLoadListener
        types.add(new Object[] {this, ClassListener.class});
    }

    /**
     * <p>
     * Find a service provider class associated with the service provider interface.
     * </p>
     * 
     * @param <S> A type of service provider interface.
     * @param spi A service provider interface to find. An abstract class is only accepted.
     * @return A finded service provider class.
     */
    public <S> Class<S> find(Class<S> spi) {
        for (Module module : modules) {
            List<Class<S>> list = module.find(spi, true);

            if (list.size() != 0) {
                return list.get(0);
            }
        }
        return spi;
    }

    /**
     * @see kiss.ClassListener#load(java.lang.Class)
     */
    public void load(Class clazz) {
        if (clazz != Modules.class) {
            Object[] types = {I.make(clazz), Object.class};
            Class[] params = ClassUtil.getParameter(clazz, ClassListener.class);

            if (params.length != 0) {
                types[1] = params[0];
            }

            // The new ClassLoadListener is introduced by some module. For all existing modules,
            // that is unknown. So we must notify this event to all modules.
            for (Module module : modules) {
                for (Class provider : module.find((Class<?>) types[1], false)) {
                    ((ClassListener) types[0]).load(provider);
                }
            }

            // register
            this.types.add(types);
        }
    }

    /**
     * @see kiss.ClassListener#unload(java.lang.Class)
     */
    public void unload(Class clazz) {
        for (Object[] types : this.types) {
            if (Model.load(types[0].getClass()).type == clazz) {
                this.types.remove(types);
                return;
            }
        }
    }

    /**
     * <p>
     * Load the path as an additional classpath into JVM. If the file indicates the classpath which
     * is already loaded, that will be reloaded. The classpath can accept directory or archive (like
     * Jar). If it is <code>null</code> or a file, this method does nothing.
     * </p>
     * 
     * @param path A module path to load. Directory or archive path (like Jar) can be accepted.
     */
    ClassLoader load(Path path) {
        // check module file
        if (path != null && Files.exists(path)) {
            // If the given module file has been already loaded, we must unload it on ahead.
            unload(path);

            // build module
            try {
                Module module = new Module(path);

                // Load module for the specified directory. The new module has high priority than
                // previous.
                modules.add(0, module);

                // fire event
                for (Object[] types : this.types) {
                    for (Class provider : module.find((Class<?>) types[1], false)) {
                        ((ClassListener) types[0]).load(provider);
                    }
                }
                return module;
            } catch (MalformedURLException e) {
                throw I.quiet(e);
            }
        } else {
            return null;
        }
    }

    /**
     * <p>
     * Unload the path which is an additional classpath in JVM. If the file indicates the classpath
     * which is not loaded yet, that will be ignored. The classpath can accept directory or archive
     * (like Jar). If it is <code>null</code> or a file, this method does nothing.
     * </p>
     * 
     * @param path A module path to unload. Directory or archive path (like Jar) can be accepted.
     */
    void unload(Path path) {
        // check module file
        if (path != null && Files.exists(path)) {
            for (Module module : modules) {
                try {
                    if (Files.isSameFile(path, module.path)) {
                        // fire event
                        for (Object[] types : this.types) {
                            for (Class provider : module.find((Class<?>) types[1], false)) {
                                ((ClassListener) types[0]).unload(provider);
                            }
                        }

                        // unload class key from module aware map
                        for (WeakReference<Map> reference : I.awares) {
                            Map aware = reference.get();

                            if (aware == null) {
                                I.awares.remove(reference);
                            } else {
                                Iterator<Class> iterator = aware.keySet().iterator();

                                while (iterator.hasNext()) {
                                    if (iterator.next().getClassLoader() == module) {
                                        iterator.remove();
                                    }
                                }
                            }
                        }

                        // unload
                        modules.remove(module);

                        // close classloader
                        I.quiet(module);
                        break;
                    }
                } catch (Exception e) {
                    throw I.quiet(e);
                }
            }
        }
    }
}