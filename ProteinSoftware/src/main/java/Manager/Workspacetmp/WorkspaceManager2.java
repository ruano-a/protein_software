package Manager.Workspacetmp;


import API.Interfaces.ITab;
import API.Interfaces.IWorkspace;
import API.Interfaces.IWorkspaceManager;
import Protein.Core;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class WorkspaceManager2 implements IWorkspaceManager {
    private SimpleObjectProperty<IWorkspace> activeWorkspace;
    private StackPane layout;
    private ITab dragTab;

    public WorkspaceManager2(){
        layout = new StackPane();
        activeWorkspace = new SimpleObjectProperty<>(new Workspace2());
        dragTab = null;
        Splitter splitter = new Splitter(null);

        splitter.getModel().setWorkspace(activeWorkspace.get());
        layout.setStyle("-fx-background-insets: 0; -fx-padding: 0;-fx-border-width: 0px;");
        layout.getChildren().add(splitter.getView());
    }

    @Override
    public IWorkspace getActiveWorkspace() {
        return activeWorkspace.get();
    }

    public SimpleObjectProperty<IWorkspace> activeWorkspaceProperty() {
        return activeWorkspace;
    }

    public void setActiveWorkspace(IWorkspace activeWorkspace) {
        this.activeWorkspace.set(activeWorkspace);
    }

    @Override
    public ITab getDragTab() {
        return dragTab;
    }

    @Override
    public void setDragTab(ITab dragTab) {
        this.dragTab = dragTab;
    }

    @Override
    public List<MenuItem> getMenuItems(){
        List<MenuItem> items = new ArrayList<>();

        MenuItem miSD = new MenuItem();
        Core.getTranslater().setText("split-down", miSD.textProperty());
        MenuItem miSU = new MenuItem();
        Core.getTranslater().setText("split-up", miSU.textProperty());
        MenuItem miSR = new MenuItem();
        Core.getTranslater().setText("split-right", miSR.textProperty());
        MenuItem miSL = new MenuItem();
        Core.getTranslater().setText("split-left", miSL.textProperty());
        MenuItem miCD = new MenuItem();
        Core.getTranslater().setText("change-direction", miCD.textProperty());
        MenuItem miC = new MenuItem();
        Core.getTranslater().setText("close", miC.textProperty());
        MenuItem miCT = new MenuItem();
        Core.getTranslater().setText("close-tab", miCT.textProperty());
        MenuItem miCO = new MenuItem();
        Core.getTranslater().setText("close-other", miCO.textProperty());
        MenuItem miCA = new MenuItem();
        Core.getTranslater().setText("close-all", miCA.textProperty());
        MenuItem miSN = new MenuItem();
        Core.getTranslater().setText("select-next-tab", miSN.textProperty());
        MenuItem miSP = new MenuItem();
        Core.getTranslater().setText("select-prev-tab", miSP.textProperty());
        MenuItem miNT = new MenuItem();
        Core.getTranslater().setText("new-tab", miNT.textProperty());

        miSD.setOnAction(event -> this.activeWorkspace.get().getModel().getSplitter().getModel().splitDown());
        miSU.setOnAction(event -> this.activeWorkspace.get().getModel().getSplitter().getModel().splitUp());
        miSR.setOnAction(event -> this.activeWorkspace.get().getModel().getSplitter().getModel().splitRight());
        miSL.setOnAction(event -> this.activeWorkspace.get().getModel().getSplitter().getModel().splitLeft());
        miCD.setOnAction(event -> {
            if (this.activeWorkspace.get().getModel().getSplitter().getModel().getParentSplitter() != null){
                this.activeWorkspace.get().getModel().getSplitter().getModel().getParentSplitter().getModel().changeOrientation();
            }
        });
        miC.setOnAction(event -> this.activeWorkspace.get().getModel().getSplitter().getModel().close());

        miCT.setOnAction(event -> this.activeWorkspace.get().getModel().removeTab(this.activeWorkspace.get().getModel().getActiveTab()));
        miCT.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));

        miCO.setOnAction(event -> this.activeWorkspace.get().getModel().removeAllTabBut(this.activeWorkspace.get().getModel().getActiveTab()));
        miCA.setOnAction(event -> this.activeWorkspace.get().getModel().removeAllTab());

        miSN.setOnAction(event -> this.activeWorkspace.get().getModel().selectNextTab());
        miSN.setAccelerator(new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN));

        miSP.setOnAction(event -> this.activeWorkspace.get().getModel().selectPreviousTab());
        miSP.setAccelerator(new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));

        miNT.setOnAction(event -> this.activeWorkspace.get().getModel().addButtonClicked());
        miNT.setAccelerator(new KeyCodeCombination(KeyCode.T, KeyCombination.CONTROL_DOWN));

        items.add(miNT);
        items.add(miCT);
        items.add(miCO);
        items.add(miCA);
        items.add(miSN);
        items.add(miSP);
        items.add(new SeparatorMenuItem());
        items.add(miSD);
        items.add(miSU);
        items.add(miSR);
        items.add(miSL);
        items.add(miCD);
        items.add(miC);
        return items;
    }

    @Override
    public StackPane getLayout() {
        return layout;
    }

}
