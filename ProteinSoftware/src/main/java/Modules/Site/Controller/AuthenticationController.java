package Modules.Site.Controller;

import Common.BaseController;
import Modules.Site.Model.AuthenticationModel;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Created by DeepSpaceI on 01/10/2016.
 */
public class AuthenticationController extends BaseController {

    @FXML
    private TextField emailForCoInput;

    @FXML
    private PasswordField passwordForCoInput;

    @FXML
    private Button connectButton;

    @FXML
    private TextField idInput;

    @FXML
    private TextField emailForRegInput;

    @FXML
    private PasswordField passwordForRegInput;

    @FXML
    private Button regButton;

    @FXML
    private TextArea resultsText;

    @FXML
    private Button moduleButton;

    private AuthenticationModel model;

    @FXML
    private void initialize() {

        this.model = new AuthenticationModel();
        this.emailForCoInput.textProperty().bindBidirectional(this.model.emailCoProperty());
        this.passwordForCoInput.textProperty().bindBidirectional(this.model.passwordCoProperty());
        this.idInput.textProperty().bindBidirectional(this.model.idRegProperty());
        this.emailForRegInput.textProperty().bindBidirectional(this.model.emailRegProperty());
        this.passwordForRegInput.textProperty().bindBidirectional(this.model.passwordRegProperty());
        this.resultsText.textProperty().bind(this.model.resultProperty());

        this.connectButton.setOnAction(event -> this.model.connect());
        this.regButton.setOnAction(event -> this.model.register());
        this.moduleButton.setOnAction(event -> this.model.testModule());
    }
}