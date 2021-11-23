package Manager.Workspace;

import Protein.Core;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by paull on 27/03/2016.
 */
public class WorkspaceManager {
    private class Manager {
        private StackPane           rootPane;
        private WorkspaceSplitter   rootSplitter;
        private Workspace           activeWorkspace;
        private float               tabWidth;

        public Manager(){
            rootPane = new StackPane();
            activeWorkspace = null;
            tabWidth = 200;

            initWorkspace();
            this.rootPane.getStyleClass().addAll("workspace-manager", "no-padding-insets");
        }

        private void initWorkspace(){
            rootSplitter = new WorkspaceSplitter();
            Workspace ws = new Workspace();
            rootSplitter.init(null, ws);
            setActiveWorkspace(ws);
            rootPane.getChildren().add(rootSplitter);
        }

        public void splitUp(Workspace ws){
            if (ws != null){
                ws.getWorkspaceSplitter().splitUp(ws);
            }
        }

        public void splitDown(Workspace ws){
            if (ws != null){
                ws.getWorkspaceSplitter().splitDown(ws);
            }
        }

        public void splitLeft(Workspace ws){
            if (ws != null){
                ws.getWorkspaceSplitter().splitLeft(ws);
            }
        }

        public void splitRight(Workspace ws){
            if (ws != null){
                ws.getWorkspaceSplitter().splitRight(ws);
            }
        }

        public void changeDirection(Workspace ws){
            ws.getWorkspaceSplitter().changeDirection();
        }

        public void close(Workspace ws){
            ws.getWorkspaceSplitter().close(ws);
        }

        public StackPane getLayout() {
            return rootPane;
        }

        public Workspace getActiveWorkspace() {
            return activeWorkspace;
        }

        public void setActiveWorkspace(Workspace ws) {
            if (activeWorkspace != ws){
                System.out.println("Manager.Workspace set");
                activeWorkspace = ws;
            }
        }

        public WorkspaceTab getActiveTab(){
            if (activeWorkspace != null){
                return activeWorkspace.getActiveTab();
            }
            return null;
        }

        public void addTab(WorkspaceTab tab){ activeWorkspace.addTab(tab); }

        public float getTabWidth() {
            return tabWidth;
        }

        public void test(){
            this.activeWorkspace.getWorkspaceSplitter().getItems().add(Core.getWorkspaceManager().getLayout());

        }
    }
    private static Manager manager;

    public WorkspaceManager(){
        if (manager == null){
            manager = new Manager();
        }
    }

    public StackPane getLayout() {
        return manager.getLayout();
    }

    public static void splitUp(Workspace ws){
        manager.splitUp(ws);
    }

    public static void splitDown(Workspace ws){
        manager.splitDown(ws);
    }

    public static void splitRight(Workspace ws){
        manager.splitRight(ws);
    }

    public static void splitLeft(Workspace ws){
        manager.splitLeft(ws);
    }

    public static void changeDirection(Workspace ws){
        manager.changeDirection(ws);
    }

    public static void close(Workspace ws){
        manager.close(ws);
    }

    public static Workspace getActiveWorkspace() {
        return manager.getActiveWorkspace();
    }

    public static void setActiveWorkspace(Workspace ws) {
        manager.setActiveWorkspace(ws);
    }

    public static WorkspaceTab getActiveTab() { return manager.getActiveTab();}

    public static void addTab(WorkspaceTab tab){ manager.addTab(tab); }

    public static float getTabWidth() {
        return manager.getTabWidth();
    }

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

        MenuItem miTest = new MenuItem("test");
        miTest.setOnAction(event -> {
            manager.test();
        });

        miSD.setOnAction(event -> WorkspaceManager.splitDown(manager.getActiveWorkspace()));
        miSU.setOnAction(event -> WorkspaceManager.splitUp(manager.getActiveWorkspace()));
        miSR.setOnAction(event -> WorkspaceManager.splitRight(manager.getActiveWorkspace()));
        miSL.setOnAction(event -> WorkspaceManager.splitLeft(manager.getActiveWorkspace()));
        miCD.setOnAction(event -> WorkspaceManager.changeDirection(manager.getActiveWorkspace()));
        miC.setOnAction(event -> WorkspaceManager.close(manager.getActiveWorkspace()));

        miCT.setOnAction(event -> manager.getActiveWorkspace().closeTab());
        miCT.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyCombination.CONTROL_DOWN));

        miCO.setOnAction(event -> manager.getActiveWorkspace().closeOthersTab());
        miCA.setOnAction(event -> manager.getActiveWorkspace().closeAllTab());

        miSN.setOnAction(event -> manager.getActiveWorkspace().selectNextTab());
        miSN.setAccelerator(new KeyCodeCombination(KeyCode.TAB, KeyCombination.CONTROL_DOWN));

        miSP.setOnAction(event -> manager.getActiveWorkspace().selectPreviousTab());
        miSP.setAccelerator(new KeyCodeCombination(KeyCode.TAB, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));

        miNT.setOnAction(event -> manager.getActiveWorkspace().createTab());
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
        items.add(miTest);
        return items;
    }
}
