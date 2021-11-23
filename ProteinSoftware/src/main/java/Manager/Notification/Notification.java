package Manager.Notification;

import Manager.Popup.ProteinPopup;
import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Notification {
    private Label           title_label;
    private Button          close_button;
    private Label           message_label;
    private StackPane       state_layout;
    private HBox            layout;
    private String          stateClass;
    private ProteinPopup    popup;
    private PauseTransition delay;

    public Notification(String title, String message, String state){
        Notification self = this;
        try {
            stateClass = state;
            layout = FXMLLoader.load(getClass().getResource("/Manager/Notification/notification.fxml"));
            title_label = (Label)((HBox)((VBox)layout.getChildren().get(1)).getChildren().get(0)).getChildren().get(0);
            close_button = (Button)((HBox)((VBox)layout.getChildren().get(1)).getChildren().get(0)).getChildren().get(1);
            message_label = (Label)((VBox)layout.getChildren().get(1)).getChildren().get(1);
            state_layout = (StackPane) layout.getChildren().get(0);
            title_label.setText(title);
            message_label.setText(message);
            state_layout.getStyleClass().add(state);
            close_button.setOnAction(event -> {
                NotificationManager.remove(self);
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getTitle(){
        return title_label.getText();
    }

    public String getMessage(){
        return message_label.getText();
    }

    public String getState(){
        return stateClass;
    }

    public void setDelay(PauseTransition d){
        delay = d;
    }

    public PauseTransition getDelay(){
        return delay;
    }

    public void setPopup(ProteinPopup p){
        popup = p;
    }

    public ProteinPopup getPopup(){
        return popup;
    }

    public HBox getLayout(){
        return this.layout;
    }
}
