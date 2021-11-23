package API;

import API.Interfaces.*;
import javafx.stage.Stage;

/**
 * Created by Aurélien on 28/11/2016.
 */
public abstract class ApiInterface {
    public abstract Stage getMainWindow();
    public abstract void notify(String title, String message, String state);
    public abstract IFileManager getFileManager();
    public abstract IDatabaseManager getDatabaseManager();
    public abstract IConfigManager getConfigManager();
    public abstract ITranslater getTranslater();
    public abstract IWorkspaceManager getWorkspaceManager();
    public abstract ISiteManager getSiteManager();

    public static ApiInterface getInterface(){
        try {
            System.out.println("get 2");
            ClassLoader classLoader = Class.forName("Protein.Main").getClassLoader();
            Class aClass = classLoader.loadClass("API.ApiImplementer");
            System.out.println("aClass.getName() = " + aClass.getName());
            return ((ApiInterface) aClass.newInstance());
        } catch (ClassNotFoundException|InstantiationException|IllegalAccessException e) {
            e.printStackTrace();
        }
        return (null);
    }
}
