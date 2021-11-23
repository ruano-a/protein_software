package Manager.Workspace;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;

import java.io.File;

/**
 * Created by paull on 27/03/2016.
 */
public class WorkspaceTab {
    protected TabTitle      tabTitle;
    protected String        title;
    protected boolean       visible;
    protected boolean       closable;
    protected boolean       modified;
    protected boolean       isEditor;
    protected Node          content;
    protected TabLister     tabLister;
    protected File          file;

    public WorkspaceTab(){
        this.title = null;
        this.visible = true;
        this.closable = true;
        this.isEditor = false;
        this.modified = false;
        this.content = null;
        this.tabLister = null;
        this.tabTitle = new TabTitle(this);
        file = null;
    }

    public WorkspaceTab(File f){
        if (f != null){
            this.title = f.getName();
        } else{
            this.title = null;
        }
        this.visible = true;
        this.closable = true;
        this.isEditor = false;
        this.modified = false;
        this.content = null;
        this.tabLister = null;
        this.tabTitle = new TabTitle(this);
        this.file = f;
    }

    public WorkspaceTab(String title, Node content, TabLister t){
        this.title = title;
        this.visible = true;
        this.closable = true;
        this.isEditor = false;
        this.modified = false;
        this.setContent(content);
        this.tabLister = t;
        this.tabTitle = new TabTitle(this);
        file = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String s) {
        this.title = s;
        if (this.tabTitle != null){
            this.tabTitle.setText(s);
        }
    }

    public TabLister getTabLister() {
        return tabLister;
    }

    public void setTabLister(TabLister e) {
        this.tabLister = e;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean v) {
        this.visible = v;
        if (v){
            tabLister.add(tabTitle);
        }
        else {
            tabLister.remove(tabTitle);
        }
    }

    public Node getContent() {
        return content;
    }

    public void setContent(Node c) {
        WorkspaceTab self = this;
        if (c != null){
            c.setOnMouseClicked(event -> {
                tabLister.getWorkspace().setActiveTab(self);
                WorkspaceManager.setActiveWorkspace(tabLister.getWorkspace());
            });

        }
        this.content = c;
    }

    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean c) {
        this.closable = c;
        this.tabTitle.showCloseButton(c);
    }

    public TabTitle getTabTitle(){
        return this.tabTitle;
    }

    public void setModified(boolean v){
        this.modified = v;
        this.tabTitle.showIsModified(v);
    }

    public boolean isModified() {
        return this.modified;
    }

    public boolean isEditor(){
        return this.isEditor;
    }

    public File getFile(){
        return this.file;
    }

    public void setFile(File f){
        this.file = f;
        if (this.file != null){
            this.title = this.file.getName();
            this.tabTitle.setText(this.title);
        }
    }
}
