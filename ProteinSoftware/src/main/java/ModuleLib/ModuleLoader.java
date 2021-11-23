package ModuleLib;

import IModule.IModule;
import Protein.Core;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * Created by ellie_e on 22/09/2015.
 *
 * Permet de charger les classes qui implémente
 * l'interface IModule
 */
public class                        ModuleLoader {

    private  static final String    MODULE_REPO = "modules";

    private static List<URL>        urls = new ArrayList<URL>();
    private static ClassLoader      classLoader;

    /**
     * Méthode qui retourne la liste des classes à charger
     * dans un point JAR
     * @return La liste des classes à charger
     */
    private static List<String>     getModuleClasses(String manifest_attribute) {
        List<String>                classes;
        File[]                      files;

        classes = new ArrayList<String>();
        files = new File(Core.getBaseUrl()+File.separator+MODULE_REPO).listFiles(new ModuleFilter());

        if (files == null) {
            new File(Core.getBaseUrl()+File.separator+MODULE_REPO).mkdir();
            return classes;
        }

        for (File f : files) {
            JarFile                 jarFile = null;
            Manifest                manifest = null;
            String                  className = null;

            try {
                jarFile = new JarFile(f);
                manifest = jarFile.getManifest();
                className = manifest.getMainAttributes().getValue(manifest_attribute);
                classes.add(className);
                urls.add(f.toURI().toURL());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return (classes);
    }

    private static List<String>     getModuleClasse(String manifest_attribute, File file) {
        List<String>                classes;

        classes = new ArrayList<String>();

        if (file == null) {
            return classes;
        }

        JarFile                 jarFile = null;
        Manifest                manifest = null;
        String                  className = null;

        try {
            jarFile = new JarFile(file);
            manifest = jarFile.getManifest();
            className = manifest.getMainAttributes().getValue(manifest_attribute);
            classes.add(className);
            urls.add(file.toURI().toURL());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return (classes);
    }

    /**
     * Instancie les modules
     *
     */
    public static List<IModule>     loadModules(String manifest_attribute, File file) {
        List<IModule>               modules;
        List<String>                classes;

        modules = new ArrayList<IModule>();
        if (file != null){
            classes = getModuleClasse(manifest_attribute, file);
        } else {
            classes = getModuleClasses(manifest_attribute);
        }

        System.out.println(classes);

        AccessController.doPrivileged(new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                classLoader = new URLClassLoader(
                        urls.toArray(new URL[urls.size()]),
                        ModuleLoader.class.getClassLoader());
                return (null);
            }
        });
        for (String c : classes) {
            try {
                Class<?>            moduleClass = Class.forName(c, true, classLoader);

                if (IModule.class.isAssignableFrom(moduleClass)) {
                    Class<IModule>  castedClass = (Class<IModule>)moduleClass;
                    System.out.println(castedClass);
                    IModule         module = castedClass.newInstance();
                    modules.add(module);
                }
            } catch (ClassNotFoundException e1) {
                e1.printStackTrace();
            } catch (InstantiationException e2) {
                e2.printStackTrace();
            } catch (IllegalAccessException e3) {
                e3.printStackTrace();
            }
        }
        return (modules);
    }

    /**
     * Classe qui hérite de FileFilter afin de surcharger l'acceptation
     * pour verifier si le fichier est un .jar
     */
    private static class    ModuleFilter implements FileFilter {
        @Override
        public boolean      accept(File file) {
            return (file.isFile() && file.getName().toLowerCase().endsWith(".jar"));
        }
    }

}