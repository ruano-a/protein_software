package Manager.Workspacetmp;

import API.Interfaces.IWorkspace;
import Common.Loadable;
import Manager.Workspace.WorkspaceManager;
import Manager.Workspacetmp.Controller.WorkspaceController;
import Manager.Workspacetmp.Model.WorkspaceModel;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Workspace2 extends Loadable implements IWorkspace {
    public Workspace2(){
        super(WorkspaceManager.class.getResource("workspace.fxml"));
        this.getModel().setWorkspace(this);
        this.getController().initContextMenu();
    }

    public Workspace2(Splitter splitter){
        super(WorkspaceManager.class.getResource("workspace.fxml"));
        this.getModel().setWorkspace(this);
        this.getModel().setSplitter(splitter);
        this.getController().initContextMenu();
    }

    public static Workspace2 create(){
        return new Workspace2();
    }

    public static Workspace2 create(Splitter splitter){
        return new Workspace2(splitter);
    }

    @Override
    public WorkspaceController getController() {
        return (WorkspaceController)controller;
    }

    @Override
    public WorkspaceModel getModel() {
        return (WorkspaceModel)model;
    }

}
