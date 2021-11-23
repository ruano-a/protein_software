package Manager.Workspacetmp;

import API.Interfaces.ISplitter;
import Common.Loadable;
import Manager.Workspace.WorkspaceManager;
import Manager.Workspacetmp.Controller.SplitterController;
import Manager.Workspacetmp.Model.SplitterModel;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Splitter extends Loadable implements ISplitter {

    public Splitter(Splitter parent){
        super(WorkspaceManager.class.getResource("splitter.fxml"));
        this.getModel().setSplitter(this);
        this.getModel().setParentSplitter(parent);
    }

    @Override
    public SplitterController getController() {
        return (SplitterController)super.getController();
    }

    @Override
    public SplitterModel getModel() {
        return (SplitterModel)super.getModel();
    }
}
