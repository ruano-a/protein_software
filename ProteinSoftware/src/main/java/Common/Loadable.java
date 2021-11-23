package Common;

import API.Interfaces.IBaseController;
import API.Interfaces.IBaseModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URL;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Loadable {
    protected IBaseController controller;
    protected IBaseModel model;
    protected Node view;

    public Loadable(URL url){
        this.load(url);
    }

    public Loadable(URL... urls){
        for (URL url : urls){
            this.load(url);
        }
    }

    private void load(URL url){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(url);
            this.view = loader.load();
            this.controller = loader.getController();
            this.controller.setView(view);
            this.model = this.controller.getModel();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public IBaseController getController() {
        return controller;
    }

    public IBaseModel getModel() {
        return model;
    }

    public Node getView() {
        return view;
    }
}
