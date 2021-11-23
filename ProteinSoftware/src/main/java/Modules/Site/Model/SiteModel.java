package Modules.Site.Model;

import Modules.Site.Connexion;
import Modules.Site.ModuleList;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class SiteModel {

    private Connexion connexion;
    private ModuleList moduleList;
    private AnchorPane layout;

    public SiteModel(){
        this.connexion = new Connexion(this);
        this.moduleList = new ModuleList(this);
        AnchorPane.setTopAnchor(this.connexion.getView(), 0.0);
        AnchorPane.setTopAnchor(this.moduleList.getView(), 0.0);
        AnchorPane.setBottomAnchor(this.connexion.getView(), 0.0);
        AnchorPane.setBottomAnchor(this.moduleList.getView(), 0.0);
        AnchorPane.setLeftAnchor(this.connexion.getView(), 0.0);
        AnchorPane.setLeftAnchor(this.moduleList.getView(), 0.0);
        AnchorPane.setRightAnchor(this.connexion.getView(), 0.0);
        AnchorPane.setRightAnchor(this.moduleList.getView(), 0.0);

        this.layout = new AnchorPane(this.connexion.getView());
        this.connexion.getModel().connectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    moduleList.getModel().getModulesList();
                    layout.getChildren().setAll(moduleList.getView());
                } else {
                    layout.getChildren().setAll(connexion.getView());
                }
            }
        });
    }

    public Node getLayout() {
        return layout;
    }

}
