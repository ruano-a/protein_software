package Modules.Site;

import Common.Loadable;
import Modules.Site.Controller.ConnexionController;
import Modules.Site.Model.ConnexionModel;
import Modules.Site.Model.SiteModel;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Connexion extends Loadable {

    public Connexion(SiteModel siteModel){
        super(Site.class.getResource("connexion.fxml"));
        this.getModel().setSiteModel(siteModel);
    }

    @Override
    public ConnexionController getController() {
        return (ConnexionController) controller;
    }

    @Override
    public ConnexionModel getModel() {
        return (ConnexionModel) model;
    }

}
