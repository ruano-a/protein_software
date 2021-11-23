package Manager.Workspacetmp;

import API.Interfaces.ITab;
import API.Interfaces.IWorkspace;
import javafx.scene.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Tab implements ITab {

    protected IWorkspace workspace;
    protected Node content;
    protected Title title;
    protected File file;
    private static List<Tab> openTabs;

    public Tab(){
        this.title = new Title(this);
        this.content = null;
        if (openTabs == null){
            openTabs = new ArrayList<>();
        }
    }

    public Tab(String title, Node content){
        this.title = new Title(this, title);
        this.content = content;
        if (openTabs == null){
            openTabs = new ArrayList<>();
        }
    }

    public Tab(IWorkspace workspace){
        this.content = null;
        this.workspace = workspace;
        this.title = new Title(this);
        if (openTabs == null){
            openTabs = new ArrayList<>();
        }
    }

    public Tab(IWorkspace workspace, String title, Node content){
        this.workspace = workspace;
        this.content = content;
        this.title = new Title(this, title);
        if (openTabs == null){
            openTabs = new ArrayList<>();
        }
    }

    public Tab(IWorkspace workspace, File file, Node content){
        this.workspace = workspace;
        this.content = content;
        if (openTabs == null){
            openTabs = new ArrayList<>();
        }
        if (file != null){
            this.title = new Title(this, file.getName());
        } else {
            this.title = new Title(this, "untitled");
        }
    }

    public static Tab create(String text, Node content){
        return new Tab(text, content);
    }

    public static Tab create(Workspace2 workspace){
        return new Tab(workspace);
    }

    public static Tab create(Workspace2 workspace, String text, Node content){
        return new Tab(workspace, text, content);
    }

    public static List<Tab> getOpenTabs(){
        return openTabs;
    }

    public static Tab create(){
        return new Tab();
    }

    public static boolean isNotOpen(Tab tab){
        if (tab.getFile() != null){
            for (Tab tmp : openTabs) {
                if (tab.getFile().getAbsolutePath().equals(tmp.getFile().getAbsolutePath())){
                    tmp.getWorkspace().getModel().setActiveTab(tmp);
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public Title getTitle() {
        return title;
    }

    @Override
    public Node getContent() {
        return content;
    }

    @Override
    public void setContent(Node content) {
        this.content = content;
    }

    @Override
    public IWorkspace getWorkspace(){
        return workspace;
    }

    @Override
    public void setWorkspace(IWorkspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
        this.file = file;
        this.title.getModel().setText(file.getName());
    }
}
