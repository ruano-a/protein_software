package Manager.Workspace;

import javafx.geometry.Orientation;
import javafx.scene.control.SplitPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paull on 27/03/2016.
 */
public class WorkspaceSplitter extends SplitPane {

    private WorkspaceSplitter           parentSplitter;
    private List<WorkspaceSplitter>     child;
    private Workspace                   workspace;

    public WorkspaceSplitter(){
        this.parentSplitter = null;
        this.child = new ArrayList<>();
        setOrientation(Orientation.VERTICAL);
        this.getStyleClass().addAll("workspace-splitter", "no-padding-insets");
    }

    public void init(WorkspaceSplitter parent, Workspace ws){
        this.parentSplitter = parent;
        ws.setWorkspaceSplitter(this);
        this.setWorkspace(ws);
    }

    public void splitDown(Workspace toSplit){
        Workspace toAdd = new Workspace();
        if (workspace != null){
            split(Orientation.VERTICAL, toSplit, toAdd);
        }
        WorkspaceManager.setActiveWorkspace(toAdd);
    }

    public void splitUp(Workspace toSplit){
        Workspace toAdd = new Workspace();
        if (workspace != null){
            split(Orientation.VERTICAL, toAdd, toSplit);
        }
        WorkspaceManager.setActiveWorkspace(toAdd);
    }

    public void splitLeft(Workspace toSplit){
        Workspace toAdd = new Workspace();
        if (workspace != null){
            split(Orientation.HORIZONTAL, toAdd, toSplit);
        }
        WorkspaceManager.setActiveWorkspace(toAdd);
    }

    public void splitRight(Workspace toSplit){
        Workspace toAdd = new Workspace();
        if (workspace != null){
            split(Orientation.HORIZONTAL, toSplit, toAdd);
        }
        WorkspaceManager.setActiveWorkspace(toAdd);
    }

    public void changeDirection(){
        if (this.parentSplitter != null){
            if (this.parentSplitter.getOrientation() == Orientation.HORIZONTAL){
                this.parentSplitter.setOrientation(Orientation.VERTICAL);
            }
            else {
                this.parentSplitter.setOrientation(Orientation.HORIZONTAL);
            }
        }
    }

    public void close(Workspace ws){
        WorkspaceSplitter parent = ws.getWorkspaceSplitter().getParentSplitter();
        if (parent != null){
            if (this.workspace != null){
                parent.getChild().remove(this);
                WorkspaceSplitter lastChild = parent.getChild().get(0);
                parent.setWorkspace(lastChild.getWorkspace());
                parent.setChild(lastChild.getChild());
                WorkspaceManager.setActiveWorkspace(parent.getFirstWorkspace());
            }
        }
    }

    private void split(Orientation or, Workspace ws1, Workspace ws2){
        WorkspaceSplitter splitter1 = new WorkspaceSplitter();
        WorkspaceSplitter splitter2 = new WorkspaceSplitter();

        this.setOrientation(or);
        splitter1.init(this, ws1);
        splitter2.init(this, ws2);
        this.workspace = null;
        this.child.add(splitter1);
        this.child.add(splitter2);
        this.getItems().clear();
        this.getItems().addAll(this.child);
    }

    public WorkspaceSplitter getParentSplitter() {
        return parentSplitter;
    }

    public void setParentSplitter(WorkspaceSplitter parent) {
        this.parentSplitter = parent;
    }

    public List<WorkspaceSplitter> getChild() {
        return child;
    }

    public void setChild(List<WorkspaceSplitter> list) {
        this.child = list;
        if (this.child.size() > 0){
            this.getItems().clear();
            for(WorkspaceSplitter splitter : this.child){
                splitter.setParentSplitter(this);
            }
            this.getItems().addAll(child);
        }
    }

    public Workspace getFirstWorkspace(){
        if (workspace != null){
            return workspace;
        }
        return this.child.get(0).getFirstWorkspace();
    }

    public Workspace getWorkspace() {
        return workspace;

    }

    public void setWorkspace(Workspace ws) {
        this.workspace = ws;
        if (this.workspace != null){
            this.getItems().clear();
            this.workspace.setWorkspaceSplitter(this);
            this.getItems().add(this.workspace);
        }
    }
}
