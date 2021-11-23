package Modules.Site;

import Common.Loadable;
import Modules.Site.Controller.ModuleListController;
import Modules.Site.Model.ModuleListModel;
import Modules.Site.Model.SiteModel;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ModuleList extends Loadable {

    public ModuleList(SiteModel siteModel){
        super(Site.class.getResource("modules-list.fxml"));
        this.getModel().setSiteModel(siteModel);
    }

    @Override
    public ModuleListController getController() {
        return (ModuleListController) controller;
    }

    @Override
    public ModuleListModel getModel() {
        return (ModuleListModel) model;
    }
}
