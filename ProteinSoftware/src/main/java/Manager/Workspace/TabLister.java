package Manager.Workspace;

import Common.MaterialDesignComponents.*;
import Extractor.SVGExtractor;
import Manager.File.FileManager;
import Protein.Core;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.WindowEvent;

import java.util.List;

/**
 * Created by paull on 27/03/2016.
 */
public class TabLister extends HBox {
    private Button                      tabButton;
    private boolean                     addState;
    private HBox                        tabTitles;
    private Workspace                   workspace;
    private boolean                     change;
    private SVGPath                     addIcon;
    private SVGPath                     downIcon;


    public TabLister(Workspace w){
        this.tabTitles = new HBox();
        this.tabButton = new MDButton();
        this.workspace = w;
        this.addIcon = SVGExtractor.GetSVGPath("/Icons/add.svg");
        this.downIcon = SVGExtractor.GetSVGPath("/Icons/down.svg");

        this.setAddState(true);
        this.tabTitles.setSpacing(2);

        change = false;

        this.tabTitles.setPrefWidth(Integer.MAX_VALUE);
        this.getChildren().addAll(tabTitles, tabButton);

        this.tabTitles.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                check_width();
                check_children();
                check_button();
            }
        });
        this.tabTitles.getChildren().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                check_children();
                check_button();
            }
        });

        this.getStyleClass().add("tab-list");
        tabTitles.getStyleClass().add("list");
        tabButton.getStyleClass().add("button");
        tabButton.getStyleClass().remove("md-button");
    }

    private void handleAddTab(MouseEvent event){
        if (event.getButton() == MouseButton.PRIMARY){
            WorkspaceManager.setActiveWorkspace(this.workspace);
            Core.getFileManager().newFile();
        }
        event.consume();
    }

    private void handleShowHidden(MouseEvent event){
        if (event.getButton() == MouseButton.PRIMARY){
            ContextMenu ctmenu = workspace.getContextMenu();
            ctmenu.setOnShown(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    double width = ctmenu.getWidth();
                    double maxXBtnOnWindow = tabButton.localToScreen(tabButton.getBoundsInLocal()).getMaxX();
                    ctmenu.setX(maxXBtnOnWindow - width);
                }
            });
            List<WorkspaceTab> tabs = workspace.getTabs();
            for (WorkspaceTab t : tabs){
                if (!t.isVisible()){
                    MenuItem m = new MenuItem(t.getTitle());
                    m.setOnAction(selectEvent -> workspace.setActiveTab(t));
                    ctmenu.getItems().add(m);
                }
            }
            ctmenu.show(tabButton, Side.BOTTOM, 0, 0);
        }
        event.consume();
    }

    private void check_width(){
        WorkspaceTab tmp = null;
        if (!change && computeNTabWidth(tabTitles.getChildren().size()) > tabTitles.getWidth()){
            change = true;
            tmp = workspace.getLastVisible();
            if (tmp != null){
                tmp.setVisible(false);
                check_width();
            }
            change = false;
        }
        else if (!change && computeNTabWidth(tabTitles.getChildren().size() + 1) <= tabTitles.getWidth()){
            change = true;
            tmp = workspace.getFirstHidden();
            if (tmp != null){
                tmp.setVisible(true);
                check_width();
            }
            change = false;
        }
    }

    private void check_children(){
        if (!tabTitles.getStyleClass().contains("only-one") &&
                tabTitles.getChildren().size() == 1 &&
                tabTitles.getWidth() < computeNTabWidth(2)){
            tabTitles.getStyleClass().add("only-one");
        }
        else if (tabTitles.getStyleClass().contains("only-one") &&
                tabTitles.getWidth() >= computeNTabWidth(2)){
            tabTitles.getStyleClass().remove("only-one");
        }
    }

    private void check_button(){
        if (tabTitles.getChildren().size() == workspace.getTabs().size()){
            setAddState(true);
        }
        else{
            setAddState(false);
        }
    }

    public void add(TabTitle e){
        if (!tabTitles.getChildren().contains(e)){
            if (computeNTabWidth(tabTitles.getChildren().size() + 1) > tabTitles.getWidth()){
                WorkspaceTab tmp = workspace.getLastVisible();
                if (tmp != null){
                    tmp.setVisible(false);
                    setAddState(false);
                }
            }
            if (tabTitles.getChildren().size() > workspace.getTabs().indexOf(e.getTab())){
                tabTitles.getChildren().add(workspace.getTabs().indexOf(e.getTab()), e);
            }
            else {
                tabTitles.getChildren().add(e);
            }
        }
    }

    public void remove(TabTitle e) {
        tabTitles.getChildren().remove(e);
    }

    public boolean isAddState() {
        return addState;
    }

    public void setAddState(boolean state) {
        if (this.addState != state){
            this.addState = state;
            if (this.addState){
                this.tabButton.setGraphic(addIcon);
                this.tabButton.setOnMouseClicked(this::handleAddTab);
            }
            else {
                this.tabButton.setGraphic(downIcon);
                this.tabButton.setOnMouseClicked(this::handleShowHidden);
            }
        }
    }

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace ws) {
        this.workspace = ws;
    }

    private float computeNTabWidth(int n){
        float result = 0;
        for (int i = 0; i < n; i++){
            result += WorkspaceManager.getTabWidth();
            if (i + 1 < n){
                result += tabTitles.getSpacing();
            }
        }
        return result;
    }
}
