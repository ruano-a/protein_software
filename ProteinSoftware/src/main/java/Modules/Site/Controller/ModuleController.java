package Modules.Site.Controller;

import Common.BaseController;
import Modules.Site.Model.ModuleModel;
import Protein.Core;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ModuleController extends BaseController {

    @FXML
    ImageView moduleImage;

    @FXML
    Label moduleLabel;

    @FXML
    Hyperlink moduleLink;

    public ModuleController() {
        this.model = new ModuleModel();
    }

    @FXML
    public void initialize(){
        this.moduleLabel.textProperty().bindBidirectional(this.getModel().nameProperty());
        this.moduleLink.textProperty().bindBidirectional(this.getModel().linkProperty());
        this.moduleImage.imageProperty().bindBidirectional(this.getModel().imageProperty());
        this.moduleLink.setOnAction(event -> this.getModel().download());
    }

    @Override
    public ModuleModel getModel() {
        return (ModuleModel) this.model;
    }}
