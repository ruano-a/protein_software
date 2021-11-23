package Manager.Workspacetmp.Model;

import API.Interfaces.ISplitter;
import API.Interfaces.ISplitterModel;
import API.Interfaces.IWorkspace;
import Common.BaseModel;
import Manager.Workspacetmp.Workspace2;
import Manager.Workspacetmp.Splitter;
import Protein.Core;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class SplitterModel extends BaseModel implements ISplitterModel {

    private ISplitter splitter;

    private ISplitter parentSplitter;

    private ObservableList<Node> views;

    private IWorkspace workspace;

    private List<ISplitter> child;

    private SimpleObjectProperty<Orientation> orientation;

    public SplitterModel(){
        this.splitter = null;
        this.parentSplitter = null;
        this.views = FXCollections.observableArrayList();
        this.workspace = null;
        this.child = new ArrayList<>();
        this.orientation = new SimpleObjectProperty<>(Orientation.VERTICAL);
    }

    @Override
    public ISplitter getSplitter() {
        return splitter;
    }

    @Override
    public void setSplitter(ISplitter splitter) {
        this.splitter = splitter;
    }

    @Override
    public ISplitter getParentSplitter() {
        return parentSplitter;
    }

    @Override
    public void setParentSplitter(ISplitter parentSplitter) {
        this.parentSplitter = parentSplitter;
    }

    @Override
    public ObservableList<Node> getViews() {
        return views;
    }

    @Override
    public void setViews(ObservableList<Node> views) {
        this.views = views;
    }

    @Override
    public IWorkspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(IWorkspace workspace) {
        this.workspace = workspace;
        if (workspace != null){
            this.workspace.getModel().setSplitter(this.splitter);
            this.views.clear();
            this.views.add(this.workspace.getView());
        }
    }

    @Override
    public List<ISplitter> getChild() {
        return child;
    }

    @Override
    public void setChild(List<ISplitter> child) {
        this.child.clear();
        if (child != null && child.size() > 0){
            for (ISplitter s : child){
                this.child.add(s);
                s.getModel().setParentSplitter(this.splitter);
            }
            if (this.child.size() > 1){
                this.views.clear();
                this.views.addAll(this.child.get(0).getView(), this.child.get(1).getView());
            }
        }
    }

    @Override
    public Orientation getOrientation() {
        return orientation.get();
    }

    @Override
    public SimpleObjectProperty<Orientation> orientationProperty() {
        return orientation;
    }

    @Override
    public void setOrientation(Orientation orientation) {
        this.orientation.set(orientation);
    }

    @Override
    public void changeOrientation() {
        if (this.getOrientation() == Orientation.HORIZONTAL){
            this.setOrientation(Orientation.VERTICAL);
        } else {
            this.setOrientation(Orientation.HORIZONTAL);
        }
    }

    @Override
    public IWorkspace splitDown(){
        return this.split(Orientation.VERTICAL, Workspace2.create(), false);
    }

    @Override
    public IWorkspace splitUp(){
        return this.split(Orientation.VERTICAL, Workspace2.create(), true);
    }

    @Override
    public IWorkspace splitRight(){
        return this.split(Orientation.HORIZONTAL, Workspace2.create(), false);
    }

    @Override
    public IWorkspace splitLeft(){
        return this.split(Orientation.HORIZONTAL, Workspace2.create(), true);
    }

    @Override
    public IWorkspace split(Orientation or, IWorkspace toAdd, boolean toAddFirst){
        this.orientation.set(or);
        ISplitter s1 = new Splitter((Splitter) this.splitter);
        ISplitter s2 = new Splitter((Splitter) this.splitter);

        if(toAddFirst){
            s1.getModel().setWorkspace(toAdd);
            s2.getModel().setWorkspace(this.workspace);
        } else {
            s1.getModel().setWorkspace(this.workspace);
            s2.getModel().setWorkspace(toAdd);
        }

        this.child.clear();
        this.child.add(s1);
        this.child.add(s2);
        this.workspace = null;

        this.views.setAll(s1.getView(), s2.getView());

        Core.getWorkspaceManager().setActiveWorkspace(toAdd);

        return toAdd;
    }

    @Override
    public void removeSplitter(ISplitter splitter){
        this.child.remove(splitter);
        ISplitter s = this.child.get(0);

        if (s.getModel().getChild().size() > 0){
            this.setChild(s.getModel().getChild());
        } else {
            this.setWorkspace(s.getModel().getWorkspace());
        }
        this.orientation.set(s.getModel().getOrientation());
    }

    @Override
    public void close(){
        if (this.parentSplitter != null){
            this.parentSplitter.getModel().removeSplitter(this.splitter);
        }
    }
}
