package API.Interfaces;

import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface ITitleController extends IBaseController {
    void initContextMenu();

    @FXML
    void initialize();

    ITitleModel getModel();

    MenuItem getMenuItem();
}
