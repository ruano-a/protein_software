package API;

import API.Interfaces.*;
import Manager.Notification.NotificationManager;
import Protein.Core;
import javafx.stage.Stage;

/**
 * Created by Aurélien on 28/11/2016.
 */
public class ApiImplementer extends ApiInterface {
    public Stage getMainWindow(){
        return (Protein.Core.getWindow());
    }
    public void notify(String title, String message, String state){
        NotificationManager.pop(title, message, state);
    }
    public IFileManager getFileManager(){ return ( Core.getFileManager());}
    public IDatabaseManager getDatabaseManager(){
        return  Core.getDatabaseManager();
    }
    public API.Interfaces.IConfigManager getConfigManager(){
        return Core.getConfigManager();
    }
    public ITranslater getTranslater(){
        return Core.getTranslater();
    }
    public IWorkspaceManager getWorkspaceManager(){
        return Core.getWorkspaceManager();
    }
    public ISiteManager getSiteManager() { return Core.getSiteManager(); }
}
