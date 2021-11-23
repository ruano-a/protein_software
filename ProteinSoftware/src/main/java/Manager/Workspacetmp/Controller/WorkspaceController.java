package Manager.Workspacetmp.Controller;

import API.Interfaces.ITab;
import API.Interfaces.IWorkspaceController;
import API.Interfaces.IWorkspaceModel;
import Common.BaseController;
import Extractor.SVGExtractor;
import Manager.File.FileEvent;
import Manager.Workspacetmp.Model.WorkspaceModel;
import Manager.Workspacetmp.Workspace2;
import Protein.Core;
import Tab.Editor.CodeTab;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.WindowEvent;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class WorkspaceController extends BaseController implements IWorkspaceController {
    protected WorkspaceModel model;

    @FXML
    private VBox workspaceLayout;
    @FXML
    private AnchorPane tabContentLayout;
    @FXML
    private HBox lister;
    @FXML
    private Button actionButton;

    private ContextMenu workspaceContextMenu;

    private ContextMenu hiddenTabCM;

    private SVGPath addIcon;
    private SVGPath downIcon;

    private boolean change;

    public WorkspaceController(){
        this.model = new WorkspaceModel();
        this.addIcon = SVGExtractor.GetSVGPath("/Icons/add.svg");
        this.downIcon = SVGExtractor.GetSVGPath("/Icons/down.svg");
        this.hiddenTabCM = new ContextMenu();
        this.workspaceContextMenu = new ContextMenu();
        this.change = false;
        this.hiddenTabCM.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                double width = hiddenTabCM.getWidth();
                double maxXBtnOnWindow = actionButton.localToScreen(actionButton.getLayoutBounds()).getMaxX();
                hiddenTabCM.setX(maxXBtnOnWindow - width);
            }
        });

    }

    @Override
    public void initContextMenu(){
        this.workspaceContextMenu.getItems().setAll(this.getContextMenu());
    }

    @Override
    public ObservableList<MenuItem> getContextMenu(){
        MenuItem splitUp = new MenuItem("Split up");
        MenuItem splitRight = new MenuItem("Split right");
        MenuItem splitDown = new MenuItem("Split down");
        MenuItem splitLeft = new MenuItem("Split left");
        MenuItem close = new MenuItem("Close workspace");

        splitUp.setOnAction(event -> {
            model.getSplitter().getModel().splitUp();
        });
        splitDown.setOnAction(event -> {
            model.getSplitter().getModel().splitDown();
        });
        splitLeft.setOnAction(event -> {
            model.getSplitter().getModel().splitLeft();
        });
        splitRight.setOnAction(event -> {
            model.getSplitter().getModel().splitRight();
        });
        close.setOnAction(event -> {
            model.getSplitter().getModel().close();
        });

        return FXCollections.observableArrayList(splitUp, splitDown, splitRight, splitLeft, close);
    }

    @Override
    @FXML
    public void initialize(){
        this.actionButton.setGraphic(this.addIcon);
        this.actionButton.setOnAction(this::handleActionButton);
        workspaceLayout.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY){
                    workspaceContextMenu.hide();
                } else {
                    workspaceContextMenu.hide();
                    workspaceContextMenu.show(workspaceLayout, event.getScreenX(), event.getScreenY());
                }
                event.consume();
            }
        });
        this.model.addModeProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    actionButton.setGraphic(addIcon);
                } else {
                    actionButton.setGraphic(downIcon);
                }
            }
        });
        this.model.getHiddenTabs().addListener(new ListChangeListener<ITab>() {
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
                            hiddenTabCM.getItems().remove(remitem.getTitle().getController().getMenuItem());
                        }
                        for (ITab additem : c.getAddedSubList()) {
                            hiddenTabCM.getItems().add(model.getHiddenTabs().indexOf(additem), additem.getTitle().getController().getMenuItem());
                        }
                    }
                }
            }
        });

        this.model.getVisibleTabs().addListener(new ListChangeListener<ITab>() {
            @Override
            public void onChanged(Change<? extends ITab> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            System.out.println("PERMUTE");
                        }
                    } else if (c.wasUpdated()) {
                        System.out.println("UPDATE");
                    } else {
                        for (ITab remitem : c.getRemoved()) {
                            lister.getChildren().remove(remitem.getTitle().getView());
                        }
                        for (ITab additem : c.getAddedSubList()) {
                            lister.getChildren().add(model.getVisibleTabs().indexOf(additem), additem.getTitle().getView());
                        }
                    }
                }
                if (!change){
                    change = true;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            checkTabs();
                        }
                    });
                    change = false;
                }
            }
        });
        this.lister.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (!change){
                    change = true;
                    checkTabs();
                    change = false;
                }
            }
        });
        this.model.activeTabProperty().addListener(new ChangeListener<ITab>() {
            @Override
            public void changed(ObservableValue<? extends ITab> observable, ITab oldValue, ITab newValue) {
                if (newValue != null){
                    setContent(newValue.getContent());
                } else {
                    setContent(null);
                }
            }
        });
        this.lister.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (event.getGestureSource() != lister &&
                        event.getDragboard().hasString()) {
                    event.acceptTransferModes(TransferMode.ANY);
                }
                event.consume();
            }
        });
        this.lister.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                final Dragboard db = event.getDragboard();
                boolean success = false;

                if (db.hasString() && db.getString().equals("TAB MOVED") && Core.getWorkspaceManager().getDragTab() != null){
                    ITab dragTab = Core.getWorkspaceManager().getDragTab();
                    dragTab.getWorkspace().getModel().removeTab(dragTab);
                    getModel().addTab(dragTab);
                    Core.getWorkspaceManager().setDragTab(null);

                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });
        this.workspaceLayout.addEventHandler(FileEvent.FILE_OPEN, event -> {
            if (event.getFile() != null){
//                openFile(event.getFile());
                CodeTab tab = CodeTab.create((Workspace2) getModel().getWorkspace(), event.getFile());
                this.getModel().addTab(tab);
            }
        });

        this.workspaceLayout.addEventHandler(FileEvent.FILE_NEW, event -> {
//            EditorTab tab = new EditorTab(event.getFile());
            CodeTab tab = CodeTab.create(getModel().getWorkspace(), event.getFile());
            if (event.getContent() != null){
//                tab.setText(event.getContent());
            }
            getModel().addTab(tab);
        });

    }


    private boolean canAddVisible(){
        if ((this.model.getVisibleTabs().size() + 1) * 150 < this.getLister().getWidth()) {
            return true;
        }
        return false;
    }

    @Override
    public void checkTabs(){
        if (this.model.getVisibleTabs().size() > 1 && this.model.getVisibleTabs().size() * 150 > this.getLister().getWidth()){
            while (this.model.getVisibleTabs().size() > 1 && this.model.getVisibleTabs().size() * 150 > this.getLister().getWidth()){
                this.model.hideTab();
            }
        }
        else if (this.model.getHiddenTabs().size() > 0 && (this.model.getVisibleTabs().size() + 1) * 150 < this.getLister().getWidth()){
            while (this.model.getHiddenTabs().size() > 0 && (this.model.getVisibleTabs().size() + 1) * 150 < this.getLister().getWidth()){
                this.model.showTab();
            }
        }
    }

    @Override
    public void handleActionButton(ActionEvent event){
        Core.getWorkspaceManager().setActiveWorkspace(this.model.getWorkspace());
        if (this.model.isAddMode()){
            this.model.addButtonClicked();
        } else {
            if (!this.hiddenTabCM.isShowing()){
                this.hiddenTabCM.show(this.actionButton, Side.BOTTOM, 0, 0);
            } else {
                this.hiddenTabCM.hide();
            }

        }
        event.consume();
    }

    @Override
    public void setContent(Node content){
        if (content == null){
            this.tabContentLayout.getChildren().clear();
        }
        else {
            AnchorPane.setTopAnchor(content, 0.0);
            AnchorPane.setBottomAnchor(content, 0.0);
            AnchorPane.setRightAnchor(content, 0.0);
            AnchorPane.setLeftAnchor(content, 0.0);
            content.getStyleClass().add("content");
            content.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                Core.getWorkspaceManager().setActiveWorkspace(this.model.getWorkspace());
            });
            if (this.tabContentLayout.getChildren().size() > 0){
                this.tabContentLayout.getChildren().set(0, content);
            }
            else {
                this.tabContentLayout.getChildren().add(content);
            }
        }
    }

    @Override
    public HBox getLister(){
        return lister;
    }

    @Override
    public IWorkspaceModel getModel() {
        return model;
    }
}
