package Modules.Site;

import Common.Loadable;
import Modules.Site.Controller.ModuleController;
import Modules.Site.Model.ModuleModel;
import org.json.JSONObject;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Module extends Loadable {

    public Module(JSONObject module){
        super(Site.class.getResource("module-item.fxml"));
        this.getModel().setJson(module);
    }

    @Override
    public ModuleController getController() {
        return (ModuleController) controller;
    }

    @Override
    public ModuleModel getModel() {
        return (ModuleModel) model;
    }

}
