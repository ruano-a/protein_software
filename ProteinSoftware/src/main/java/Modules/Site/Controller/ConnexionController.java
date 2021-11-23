package Modules.Site.Controller;

import Common.BaseController;
import Common.BaseModel;
import Modules.Site.Model.ConnexionModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ConnexionController extends BaseController {

    @FXML
    private TextField emailInput;

    @FXML
    private TextField passwordInput;

    @FXML
    private TextField regEmailInput;

    @FXML
    private TextField idInput;

    @FXML
    private TextField regPasswordInput;

    @FXML
    private Button connectButton;

    @FXML
    private Button registerButton;


    public ConnexionController() {
        this.model = new ConnexionModel();
    }

    @FXML
    public void initialize(){
        this.emailInput.textProperty().bindBidirectional(this.getModel().emailProperty());
        this.passwordInput.textProperty().bindBidirectional(this.getModel().passwordProperty());
        this.regEmailInput.textProperty().bindBidirectional(this.getModel().regEmailProperty());
        this.passwordInput.textProperty().bindBidirectional(this.getModel().regPasswordProperty());
        this.idInput.textProperty().bindBidirectional(this.getModel().identifiantProperty());

        this.connectButton.setOnAction(event -> this.getModel().connect());
        this.registerButton.setOnAction(event -> this.getModel().register());
    }

    @Override
    public ConnexionModel getModel() {
        return (ConnexionModel)this.model;
    }
}
