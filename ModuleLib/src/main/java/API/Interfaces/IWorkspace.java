package API.Interfaces;

import javafx.scene.Node;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface IWorkspace {

    IWorkspaceController getController();

    IWorkspaceModel getModel();

    Node getView();
}
