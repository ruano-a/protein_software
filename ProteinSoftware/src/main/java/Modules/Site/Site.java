package Modules.Site;

import Items.Module;
import Modules.Site.Model.SiteModel;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Created by DeepSpaceI on 04/10/2016.
 */

public class Site extends Module {

    private AnchorPane layout;
    private SiteModel siteModel;

    public Site() {
        super("Site");
//        this.layout = new AnchorPane();
        this.siteModel = new SiteModel();
        this.layout = (AnchorPane)this.siteModel.getLayout();
//        this.layout.getChildren().add(this.siteModel.getLayout());
//        this.siteModel.layoutProperty().addListener(new ChangeListener<Node>() {
//            @Override
//            public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
//                AnchorPane.setBottomAnchor(newValue, 0.0);
//                AnchorPane.setTopAnchor(newValue, 0.0);
//                AnchorPane.setRightAnchor(newValue, 0.0);
//                AnchorPane.setLeftAnchor(newValue, 0.0);
//                layout.getChildren().add(siteModel.getLayout());
//            }
//        });
    }

    @Override
    public Node getLayout() {
        return this.layout;
    }
}