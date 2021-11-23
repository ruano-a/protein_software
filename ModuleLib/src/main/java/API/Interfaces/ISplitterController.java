package API.Interfaces;

import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface ISplitterController extends IBaseController{
    ObservableList<MenuItem> getContextMenu();

    ISplitterModel getModel();
}
