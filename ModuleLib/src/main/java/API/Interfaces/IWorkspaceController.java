package API.Interfaces;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface IWorkspaceController extends IBaseController{
    void initContextMenu();

    ObservableList<MenuItem> getContextMenu();

    @FXML
    void initialize();

    void checkTabs();

    void handleActionButton(ActionEvent event);

    void setContent(Node content);

    HBox getLister();

    IWorkspaceModel getModel();
}
