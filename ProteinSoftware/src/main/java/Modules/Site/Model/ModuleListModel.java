package Modules.Site.Model;

import Common.BaseModel;
import Modules.Site.Module;
import Protein.Core;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ModuleListModel extends BaseModel {

    private SiteModel siteModel;
    private ObservableList<Module> modules;

    public ModuleListModel() {
        this.modules = FXCollections.observableArrayList();
    }

    public SiteModel getSiteModel() {
        return siteModel;
    }

    public void setSiteModel(SiteModel siteModel) {
        this.siteModel = siteModel;
    }

    public ObservableList<Module> getModules() {
        return modules;
    }

    public void setModules(ObservableList<Module> modules) {
        this.modules = modules;
    }

    public void getModulesList(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                JSONObject result = Core.getSiteManager().request("GET", "modules", null);
                if (result != null) {
                    Object errors = result.get("errors");
                    if (errors instanceof Boolean && !(Boolean)errors) {
                        modules.clear();
                        System.out.println(result);
                        JSONArray modulesList = result.getJSONObject("data").getJSONArray("modules");
                        int size = modulesList.length();
                        for (int i = 0; i < size; i++){
                            JSONObject mod = modulesList.getJSONObject(i);
                            modules.add(new Module(mod));
                        }
                    } else {
                        //TODO gestion connect erreur
                    }
                }
            }
        });
    }
}
