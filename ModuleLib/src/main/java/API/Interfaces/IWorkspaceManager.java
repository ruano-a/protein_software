package API.Interfaces;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;

import java.util.List;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface IWorkspaceManager {

    SimpleObjectProperty<IWorkspace> activeWorkspaceProperty();

    IWorkspace getActiveWorkspace();

    void setActiveWorkspace(IWorkspace workspace);

    ITab getDragTab();

    void setDragTab(ITab dragTab);

    List<MenuItem> getMenuItems();

    StackPane getLayout();
}
