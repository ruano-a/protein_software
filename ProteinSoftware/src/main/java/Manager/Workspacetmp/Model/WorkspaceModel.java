package Manager.Workspacetmp.Model;

import API.Interfaces.ISplitter;
import API.Interfaces.ITab;
import API.Interfaces.IWorkspace;
import API.Interfaces.IWorkspaceModel;
import Common.BaseModel;
import Manager.Workspacetmp.Tab;
import Protein.Core;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class WorkspaceModel extends BaseModel implements IWorkspaceModel {
    private ObservableList<ITab> tabs;
    private ObservableList<ITab> visibleTabs;
    private ObservableList<ITab> hiddenTabs;
    private SimpleObjectProperty<ITab> activeTab;
    private SimpleBooleanProperty addMode;
    private IWorkspace workspace;
    private ISplitter splitter;

    private int test = 0;

    public WorkspaceModel(){
        this.tabs = FXCollections.observableArrayList();
        this.visibleTabs = FXCollections.observableArrayList();
        this.hiddenTabs = FXCollections.observableArrayList();

        this.activeTab = new SimpleObjectProperty<ITab>();

        this.addMode = new SimpleBooleanProperty(true);

        this.tabs.addListener(new ListChangeListener<ITab>() {
            @Override
            public void onChanged(Change<? extends ITab> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutate
                        }
                    } else if (c.wasUpdated()) {
                        //update item
                    } else {
                        for (ITab remitem : c.getRemoved()) {
                            Tab.getOpenTabs().remove(remitem);
                        }
                        for (ITab additem : c.getAddedSubList()) {
                            Tab.getOpenTabs().add((Tab) additem);
                        }
                    }
                }
            }

        });

        this.hiddenTabs.addListener(new ListChangeListener<ITab>() {
            @Override
            public void onChanged(Change<? extends ITab> c) {
                if (hiddenTabs.size() > 0){
                    setAddMode(false);
                } else {
                    setAddMode(true);
                }
            }
        });
    }

    @Override
    public ObservableList<ITab> getTabs() {
        return tabs;
    }

    @Override
    public ObservableList<ITab> getVisibleTabs() {
        return visibleTabs;
    }

    @Override
    public ObservableList<ITab> getHiddenTabs() {
        return hiddenTabs;
    }

    @Override
    public ITab getActiveTab() {
        return activeTab.get();
    }

    @Override
    public SimpleObjectProperty<ITab> activeTabProperty() {
        return activeTab;
    }

    @Override
    public IWorkspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(IWorkspace workspace) {
        this.workspace = workspace;
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
    public void addTab(ITab tab){
        if (Tab.isNotOpen((Tab) tab)){
            tab.setWorkspace(this.workspace);
            this.setActiveTab(tab);
            this.tabs.add(tab);
        }
    }

    @Override
    public void addTab(ITab tab, int i){
        if (Tab.isNotOpen((Tab) tab)){
            tab.setWorkspace(this.workspace);
            this.setActiveTab(tab);
            this.tabs.add(i, tab);
        }
    }

    @Override
    public void addTabOffsetFromTab(ITab toAdd, int offset, ITab tab){
        if (Tab.isNotOpen((Tab) tab)){
            int i = this.visibleTabs.indexOf(tab) + offset;
            toAdd.setWorkspace(this.workspace);
            this.tabs.add(toAdd);
            this.setActiveTab(i, toAdd);
        }
    }

    @Override
    public void removeTab(ITab tab){
        int index = this.tabs.indexOf(tab);
        this.tabs.remove(tab);
        this.visibleTabs.remove(tab);
        this.hiddenTabs.remove(tab);
        if (this.tabs.size() > 0){
            if (this.tabs.size() > index){
                this.setActiveTab(this.tabs.get(index));
            } else {
                this.setActiveTab(this.tabs.get(this.tabs.size() - 1));
            }
        }
        else {
            this.setActiveTab(null);
        }
    }

    @Override
    public void removeAllTab(){
        this.tabs.clear();
        this.hiddenTabs.clear();
        this.visibleTabs.clear();
        this.setActiveTab(null);
    }

    @Override
    public void removeAllTabBut(ITab tab){
        if (this.tabs.contains(tab)) {
            for (int i = 0; i < this.tabs.size(); i++) {
               if (this.tabs.get(i) != tab) {
                   this.removeTab(this.tabs.get(i));
                   i--;
               }
            }
        }
    }

    @Override
    public void selectNextTab(){
        if (this.tabs.size() > 0){
            int i = this.visibleTabs.indexOf(this.activeTab.get());
            if (i == this.visibleTabs.size() - 1) {
                if (this.hiddenTabs.size() > 0){
                    this.setActiveTab(this.hiddenTabs.get(0));
                } else {
                    this.setActiveTab(this.visibleTabs.get(0));
                }
            } else {
                this.setActiveTab(this.visibleTabs.get(i+1));
            }
        }
    }

    @Override
    public void selectPreviousTab(){
        if (this.tabs.size() > 0) {
            int i = this.visibleTabs.indexOf(this.activeTab.get());
            if (i == 0) {
                if (this.hiddenTabs.size() > 0){
                    this.setActiveTab(this.hiddenTabs.get(this.hiddenTabs.size() - 1));
                } else {
                    this.setActiveTab(this.visibleTabs.get(this.visibleTabs.size() - 1));
                }
            } else {
                this.setActiveTab(this.visibleTabs.get(i-1));
            }
        }
    }

    @Override
    public void setActiveTab(ITab tab){
        if (this.activeTab.get() != tab){
            if (this.activeTab.get() != null){
                this.activeTab.get().getTitle().getModel().setActive(false);
            }

            this.activeTab.set(tab);
            Core.getWorkspaceManager().setActiveWorkspace(this.workspace);

            if (tab != null){
                tab.getTitle().getModel().setActive(true);

                if (this.hiddenTabs.contains(tab)){
                    this.hiddenTabs.remove(tab);
                }
                if (!this.visibleTabs.contains(tab)) {
                    this.visibleTabs.add(tab);
                }
            }
        }
    }

    private void setActiveTab(int i, ITab tab){
        if (this.activeTab.get() != tab){
            if (this.activeTab.get() != null){
                this.activeTab.get().getTitle().getModel().setActive(false);
            }

            this.activeTab.set(tab);
            Core.getWorkspaceManager().setActiveWorkspace(this.workspace);

            if (tab != null){
                tab.getTitle().getModel().setActive(true);
                if (tab.getContent() != null){
                    tab.getContent().requestFocus();
                }
                if (this.hiddenTabs.contains(tab)){
                    this.hiddenTabs.remove(tab);
                }
                if (!this.visibleTabs.contains(tab)) {
                    this.visibleTabs.add(i, tab);
                }
            }
        }
    }

    @Override
    public void addButtonClicked(){
        Core.getFileManager().newFile();
//        this.addTab(CodeTab.create(this.workspace,));
    }

    @Override
    public boolean isAddMode() {
        return addMode.get();
    }

    @Override
    public SimpleBooleanProperty addModeProperty() {
        return addMode;
    }

    @Override
    public void setAddMode(boolean addMode) {
        this.addMode.set(addMode);
    }

    @Override
    public void showTab(){
        ITab tmp = this.hiddenTabs.get(0);
        this.hiddenTabs.remove(tmp);

        if (this.tabs.indexOf(tmp) > this.visibleTabs.size()){
            this.visibleTabs.add(tmp);
        } else {
            this.visibleTabs.add(this.tabs.indexOf(tmp), tmp);
        }
    }

    @Override
    public void hideTab(){
        ITab tmp = this.visibleTabs.get(this.visibleTabs.size() - 1);
        if (tmp == this.activeTab.getValue()){
            tmp = this.visibleTabs.get(this.visibleTabs.indexOf(tmp) - 1);
        }
        this.visibleTabs.remove(tmp);
        this.hiddenTabs.add(0, tmp);
    }
}
