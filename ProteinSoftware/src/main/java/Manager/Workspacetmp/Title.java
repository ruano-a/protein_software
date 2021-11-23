package Manager.Workspacetmp;

import API.Interfaces.ITitle;
import Common.Loadable;
import Manager.Workspace.WorkspaceManager;
import Manager.Workspacetmp.Controller.TitleController;
import Manager.Workspacetmp.Model.TitleModel;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Title extends Loadable implements ITitle {

    public Title(Tab tab){
        super(WorkspaceManager.class.getResource("title.fxml"));
        this.getModel().setTab(tab);
        this.getController().initContextMenu();
    }

    public Title(Tab tab, String text){
        super(WorkspaceManager.class.getResource("title.fxml"));
        this.getModel().setText(text);
        this.getModel().setTab(tab);
        this.getController().initContextMenu();
    }

    @Override
    public TitleController getController() {
        return (TitleController)controller;
    }

    @Override
    public TitleModel getModel() {
        return (TitleModel)model;
    }
}
