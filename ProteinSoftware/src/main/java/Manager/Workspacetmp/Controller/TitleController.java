package Manager.Workspacetmp.Controller;

import API.Interfaces.ITab;
import API.Interfaces.ITitleController;
import API.Interfaces.ITitleModel;
import Common.BaseController;
import Manager.Workspacetmp.Model.TitleModel;
import Protein.Core;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class TitleController extends BaseController implements ITitleController {
    protected ITitleModel model;

    @FXML
    private HBox layout;

    @FXML
    private Button titleButton;

    @FXML
    private Button closeButton;

    private MenuItem menuItem;

    private ContextMenu titleContextMenu;

    public TitleController(){
        this.model = new TitleModel("untitled");
        this.menuItem = new MenuItem();
        this.menuItem.textProperty().bind(this.model.textProperty());
        this.titleContextMenu = new ContextMenu();
    }

    @Override
    public void initContextMenu(){
        MenuItem close = new MenuItem("Close");
        MenuItem closeOthers = new MenuItem("Close others");
        MenuItem closeAll = new MenuItem("Close all");

        close.setOnAction(event -> {
            model.getTab().getWorkspace().getModel().removeTab(model.getTab());
        });

        ObservableList<MenuItem> items = FXCollections.observableArrayList(close, closeOthers, closeAll);
        ObservableList<MenuItem> workspaceItems = this.model.getTab().getWorkspace().getController().getContextMenu();
        if (workspaceItems != null){
            items.add(new SeparatorMenuItem());
            items.addAll(workspaceItems);
        }
        this.titleContextMenu.getItems().setAll(items);
    }

    @Override
    @FXML
    public void initialize(){
        this.titleButton.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.MIDDLE){
                model.getTab().getWorkspace().getModel().removeTab(model.getTab());
            } else if (event.getButton() == MouseButton.PRIMARY){
                model.getTab().getWorkspace().getModel().setActiveTab(model.getTab());
            }
        });

        this.closeButton.setOnAction(event -> {
            model.getTab().getWorkspace().getModel().removeTab(model.getTab());
        });
        this.menuItem.setOnAction(event -> {
            model.getTab().getWorkspace().getModel().setActiveTab(model.getTab());
        });
        this.titleButton.textProperty().bind(this.model.textProperty());
        this.titleButton.setContextMenu(this.titleContextMenu);
        this.model.activeProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    layout.getStyleClass().add("active");
                } else {
                    layout.getStyleClass().remove("active");
                }
            }
        });
        this.titleButton.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = layout.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString("TAB MOVED");
                Core.getWorkspaceManager().setDragTab(getModel().getTab());
                db.setContent(content);
                event.consume();
            }
        });
        titleButton.setOnDragExited(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                layout.getStyleClass().remove("left-over");
                layout.getStyleClass().remove("right-over");
                event.consume();
            }
        });
        this.titleButton.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != titleButton &&
                        getModel().getTab() != Core.getWorkspaceManager().getDragTab() &&
                        event.getDragboard().hasString()) {
                    if (event.getX() < titleButton.getWidth() / 2) {
                        if (!layout.getStyleClass().contains("left-over")){
                            layout.getStyleClass().remove("right-over");
                            layout.getStyleClass().add("left-over");
                        }
                    } else {
                        if (!layout.getStyleClass().contains("right-over")){
                            layout.getStyleClass().remove("left-over");
                            layout.getStyleClass().add("right-over");
                        }
                    }
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });
        this.titleButton.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                final Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString() && db.getString().equals("TAB MOVED") && Core.getWorkspaceManager().getDragTab() != null){
                    double x = event.getX();
                    int offset = 1;

                    ITab dragTab = Core.getWorkspaceManager().getDragTab();
                    ITab tab = getModel().getTab();
                    dragTab.getWorkspace().getModel().removeTab(dragTab);

                    if (x < titleButton.getWidth() / 2){
                        offset = 0;
                    }
                    tab.getWorkspace().getModel().addTabOffsetFromTab(dragTab, offset, tab);
                    Core.getWorkspaceManager().setDragTab(null);

                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
    }

    @Override
    public ITitleModel getModel() {
        return this.model;
    }

    @Override
    public MenuItem getMenuItem() {
        return menuItem;
    }
}
