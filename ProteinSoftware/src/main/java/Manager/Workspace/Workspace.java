package Manager.Workspace;

import Protein.Core;
import Tab.Editor.EditorTab;
import Manager.File.*;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by paull on 27/03/2016.
 */
public class Workspace extends BorderPane{
    private TabLister                   tabLister;
    private StackPane                   tabContent;
    private WorkspaceSplitter           workspaceSplitter;
    private List<WorkspaceTab>          tabs;
    private WorkspaceTab                activeTab;
    private List<MenuItem>              items;
    private static ContextMenu          ctMenu;

    public Workspace(){
        Workspace self = this;
        this.tabContent = new StackPane();
        this.tabLister = new TabLister(this);

        this.addEventHandler(FileEvent.FILE_OPEN, event -> {
            if (event.getFile() != null){
                openFile(event.getFile());
            }
        });

        this.addEventHandler(FileEvent.FILE_NEW, event -> {
            EditorTab tab = new EditorTab(event.getFile());
            if (event.getContent() != null){
                tab.setText(event.getContent());
            }
            addTab(tab);
        });

        this.tabs = new ArrayList<WorkspaceTab>(){
            @Override
            public boolean add(WorkspaceTab t){
                super.add(t);
                tabLister.add(t.getTabTitle());
                return true;
            }

            @Override
            public boolean remove(Object o) {
                boolean result = super.remove(o);
                tabLister.remove(((WorkspaceTab)o).getTabTitle());
                WorkspaceTab tmp = getFirstHidden();
                if (tmp != null){
                    tmp.setVisible(true);
                }
                return result;
            }
        };
        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                WorkspaceManager.setActiveWorkspace(self);
            }
        });

        this.setOnDragOver(this::handleDragOver);

        this.setOnDragDropped(this::handleDragDropped);

        initContextMenu();

        this.setOnMouseClicked(event -> WorkspaceManager.setActiveWorkspace(self));
        this.tabContent.setOnMouseClicked(event -> WorkspaceManager.setActiveWorkspace(self));

        this.setTop(tabLister);
        this.setCenter(tabContent);
        this.getStyleClass().addAll("workspace", "no-padding-insets");
    }

    public WorkspaceSplitter getWorkspaceSplitter() {
        return workspaceSplitter;
    }

    public void setWorkspaceSplitter(WorkspaceSplitter splitter) {
        this.workspaceSplitter = splitter;
    }

    public void addTab(WorkspaceTab tab){
        tab.setTabLister(tabLister);
        setActiveTab(tab);
        this.tabs.add(tab);
    }

    public void removeTab(WorkspaceTab tab){
        int idx = this.tabs.indexOf(tab);
        this.tabs.remove(tab);
        this.tabContent.getChildren().clear();
        if (tabs.size() > 0){
            if (idx < tabs.size()){
                setActiveTab(tabs.get(idx));
            }
            else{
                setActiveTab(tabs.get(tabs.size() - 1));
            }
        }
        else{
            activeTab = null;
        }
    }

    public WorkspaceTab getLastVisible(){
        WorkspaceTab tmp = null;
        for (WorkspaceTab t : tabs){
            if (t != activeTab && t.isVisible())
                tmp = t;
        }
        return tmp;
    }

    public WorkspaceTab getFirstHidden(){
        for (WorkspaceTab t : tabs){
            if (!t.isVisible()){
                return t;
            }
        }
        return null;
    }

    public List<WorkspaceTab> getTabs() {
        return tabs;
    }

    public WorkspaceTab getActiveTab() {
        return activeTab;
    }

    public void setActiveTab(WorkspaceTab tab) {
        if (tab != null && tab != this.activeTab){
            if (this.activeTab != null){
                this.activeTab.getTabTitle().getStyleClass().remove("active");
            }
            System.out.println("Active tab set");

            this.activeTab = tab;
            this.activeTab.getTabTitle().getStyleClass().add("active");
            if (!this.activeTab.isVisible()) {
                this.activeTab.setVisible(true);
            }
            tabContent.getChildren().clear();
            if (tab.getContent() != null){
                tabContent.getChildren().add(tab.getContent());
                tab.getContent().requestFocus();
            }
        }
        else if (tab == null){
            tabContent.getChildren().clear();
        }
    }

    public void createTab(){
        EditorTab t = new EditorTab(null);
        addTab(t);
    }

    public void selectNextTab(){
        if (activeTab != null){
            int idx = tabs.indexOf(activeTab);
            idx++;
            if (idx >= tabs.size()) {
                idx = 0;
            }
            setActiveTab(tabs.get(idx));
        }
    }

    public void selectPreviousTab(){
        if (activeTab != null){
            int idx = tabs.indexOf(activeTab);
            idx--;
            if (idx < 0){
                idx = tabs.size() - 1;
            }
            setActiveTab(tabs.get(idx));
        }
    }

    public void closeOthersTab(){
        if (activeTab != null){
            while (tabs.size() > 1){
                if (tabs.get(0) == activeTab){
                    removeTab(tabs.get(1));
                }
                else {
                    removeTab(tabs.get(0));
                }
            }
        }
    }

    public void closeAllTab(){
        tabs.clear();
    }

    public void closeTab(){
        if (activeTab != null){
            removeTab(activeTab);
        }
    }

    public ContextMenu getContextMenu(){
        if (ctMenu == null){
            ctMenu = new ContextMenu();
            ctMenu.setAutoHide(true);
            ctMenu.setHideOnEscape(true);
        }
        if (ctMenu.isShowing()){
            ctMenu.hide();
        }
        ctMenu.setOnShown(null);
        ctMenu.getItems().clear();
        return ctMenu;
    }

    public void openFile(File file){
        WorkspaceTab tab = getTabFile(file);
        if (file != null && tab == null && !file.isDirectory()){
            EditorTab tmp = new EditorTab(file);
            tmp.setText(Core.getFileManager().getFileContent(file));
            addTab(tmp);
        }
        else if (tab != null){
            this.setActiveTab(tab);
        }
    }

    public boolean containsFile(File file){
        if (file != null){
            for (WorkspaceTab tab : tabs){
                if (file.equals(tab.getFile())){
                    return true;
                }
            }
        }
        return false;
    }

    private WorkspaceTab getTabFile(File file){
        if (file != null){
            for (WorkspaceTab tab : tabs){
                if (file.equals(tab.getFile())){
                    return tab;
                }
            }
        }
        return null;
    }

    private void handleDragOver(DragEvent event){
        /* data is dragged over the target */
        /* accept it only if it is  not dragged from the same node
         * and if it has a string data */
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        event.consume();
    }

    private void handleDragDropped(DragEvent event){
        /* data dropped */
        System.out.println("onDragDropped");
        /* if there is a string data on dragboard, read it and use it */
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (db.hasFiles()) {
            Core.getFileManager().openFiles(db.getFiles());
            success = true;
        }
        else if (db.hasString()){
            if (activeTab != null && activeTab.isEditor()){
                EditorTab tab = (EditorTab)this.activeTab;
                tab.appendText(db.getString());
            } else{
                FileEvent fe = new FileEvent(FileEvent.FILE_NEW, null);
                fe.setContent(db.getString());
                this.fireEvent(fe);
            }
        }
        /* let the source know whether the string was successfully
         * transferred and used */
        event.setDropCompleted(success);
        event.consume();
    }

    private void initContextMenu(){
        Workspace self = this;
        items = new ArrayList<>();
        MenuItem splitDown = new MenuItem();
        Core.getTranslater().setText("split-down", splitDown.textProperty());
        MenuItem splitUp = new MenuItem();
        Core.getTranslater().setText("split-up", splitUp.textProperty());
        MenuItem splitRight = new MenuItem();
        Core.getTranslater().setText("split-right", splitRight.textProperty());
        MenuItem splitLeft = new MenuItem();
        Core.getTranslater().setText("split-left", splitLeft.textProperty());
        MenuItem changeDir = new MenuItem();
        Core.getTranslater().setText("change-direction", changeDir.textProperty());
        MenuItem close = new MenuItem();
        Core.getTranslater().setText("close", close.textProperty());

        splitDown.setOnAction(event -> WorkspaceManager.splitDown(self));
        splitUp.setOnAction(event -> WorkspaceManager.splitUp(self));
        splitRight.setOnAction(event -> WorkspaceManager.splitRight(self));
        splitLeft.setOnAction(event -> WorkspaceManager.splitLeft(self));
        changeDir.setOnAction(event -> WorkspaceManager.changeDirection(self));
        close.setOnAction(event -> WorkspaceManager.close(self));

        items.add(splitDown);
        items.add(splitUp);
        items.add(splitRight);
        items.add(splitLeft);
        items.add(changeDir);
        items.add(close);

        tabContent.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                ContextMenu ctmenu = getContextMenu();
                ctmenu.getItems().addAll(items);
                ctmenu.show(self.getScene().getWindow(), event.getScreenX(), event.getScreenY());
            }
        });
    }
}
