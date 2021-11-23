package API.Interfaces;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface IWorkspaceModel extends IBaseModel{
    ObservableList<ITab> getTabs();

    ObservableList<ITab> getVisibleTabs();

    ObservableList<ITab> getHiddenTabs();

    ITab getActiveTab();

    SimpleObjectProperty<ITab> activeTabProperty();

    IWorkspace getWorkspace();

    void setWorkspace(IWorkspace workspace);

    ISplitter getSplitter();

    void setSplitter(ISplitter splitter);

    void addTab(ITab tab);

    void addTab(ITab tab, int i);

    void addTabOffsetFromTab(ITab toAdd, int offset, ITab tab);

    void removeTab(ITab tab);

    void removeAllTab();

    void removeAllTabBut(ITab tab);

    void selectNextTab();

    void selectPreviousTab();

    void setActiveTab(ITab tab);

    void addButtonClicked();

    boolean isAddMode();

    SimpleBooleanProperty addModeProperty();

    void setAddMode(boolean addMode);

    void showTab();

    void hideTab();
}
