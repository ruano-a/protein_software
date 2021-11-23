package Protein;

import API.Interfaces.*;

import Manager.Config.ConfigManager;
import Manager.File.*;
import Manager.Site.SiteManager;
import Manager.Workspacetmp.WorkspaceManager2;
import Manager.Module.*;
import Manager.Notification.*;
import Manager.Popup.*;
import Manager.Database.*;
import Manager.ProteinMenuBar.*;
import Modules.FileTree.FileTreeModule;
import Modules.Site.Site;
import Tab.Editor.CodeTab;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.json.JSONObject;

import java.io.File;
import java.net.URLDecoder;

/**
 * Core est la classe qui contient toute les classes vitales au logiciel.
 *
 * @author paull
 * @version 0.1
 */
public class Core {
    private static Stage         window;
    private BorderPane           rootPane;
    private SplitPane            content;
    private String               progName;
    private String               version;
    private PopupManager         popupManager;
    private NotificationManager  notificationManager;
    private MenuBarManager       barManager;
    private static String        baseUrl;
    private double               divider;
    private static DatabaseManager      databaseManager;
    private static WorkspaceManager2    workspaceManager2;
    private static ModuleManager        moduleManager;
    private static FileManager          fileManager;
    private static ConfigManager        configManager;
    private static Translater           translater;
    private static SiteManager          siteManager;

    /**
     * Constructeur de la classe.
     * @param primaryStage Fenêtre du programme.
     */
    public Core(Stage primaryStage){
        initBaseUrl();
        fileManager = new FileManager();
        configManager = new ConfigManager();
        databaseManager = new DatabaseManager();
        translater = new Translater();
        workspaceManager2 = new WorkspaceManager2();
        siteManager = new SiteManager();
        window = primaryStage;
        this.rootPane = new BorderPane();
        this.content = new SplitPane();
        this.progName = "Lantern";
        this.version = "";
        this.popupManager = new PopupManager();
        this.notificationManager = new NotificationManager();

        this.translater = new Translater();

        this.barManager = new MenuBarManager();
        moduleManager = new ModuleManager();
        initEditor();
    }

    /**
     * Méthode qui initialise le logiciel.
     */
    public void init() {
        initMenu();

        window.getIcons().add(new Image("/Images/protein_ico.png"));

        this.content.getStyleClass().add("root-split-pane");
        this.content.getItems().add(workspaceManager2.getLayout());

        this.rootPane.setTop(this.barManager.getBar());
        this.rootPane.setLeft(moduleManager.getLayout());
        this.rootPane.setCenter(this.content);

        this.content.getStyleClass().addAll("protein-content", "no-padding-insets");

        window.addEventHandler(ModuleEvent.MODULE_ANY, event -> {
            if (event.getEventType() == ModuleEvent.MODULE_SHOW){
                if (content.getItems().size() > 1){
                    saveLastDivider(content.getDividerPositions()[0]);
                    content.getItems().remove(0);
                }
                content.getItems().add(0, event.getModule().getLayout());
                if (divider != 0.f){
                    content.setDividerPosition(0, divider);
                }
            }
            else if (event.getEventType() == ModuleEvent.MODULE_HIDE){
                if (content.getItems().size() > 1){
                    saveLastDivider(content.getDividerPositions()[0]);
                    content.getItems().remove(0);
                }
            }
        });

        moduleManager.attachModule(new FileTreeModule());
        moduleManager.attachModule(new Modules.FindReplace.FindReplaceModule());
//        ModuleManager.attach(new NotificationModule());
        moduleManager.attachModule(new Site());

        moduleManager.initialize(null);
        /* Jusque ici */

        Scene scene = new Scene(rootPane);
        scene.getStylesheets().add("/Styles/protein.css");
        scene.getStylesheets().add("/Styles/md-design.css");
        scene.getStylesheets().add("/Styles/button.css");
        scene.getStylesheets().add("/Styles/notification.css");
        scene.getStylesheets().add("/Styles/menu.css");
        scene.getStylesheets().add("/Styles/workspace.css");

        this.window.setTitle(this.progName + " " + this.version);
        this.window.setScene(scene);
        this.window.setOnCloseRequest(this::close);
    }

    private void initBaseUrl(){
        File baseFolder = new File(getClass().getProtectionDomain().getCodeSource().getLocation().getFile()).getParentFile();
        baseUrl = baseFolder.getPath();
        try{
            String path = baseFolder.getPath();
            baseUrl = URLDecoder.decode(path, "utf-8");
        }  catch (Exception e){
            e.printStackTrace();
        }
        baseUrl += "/";
    }

    private void saveLastDivider(double last){
        divider = last;
    }

    public static String getBaseUrl(){
        return baseUrl;
    }

    private void initEditor(){
        double x, y, w, h;
        boolean maximized;
        w = 800;
        h = 600;
        x = -1;
        y = -1;
        maximized = false;
        if (configManager.getParams().has("window")){
            JSONObject windowObject = configManager.getParams().getJSONObject("window");
            w = ((windowObject.has("width")) ? windowObject.getDouble("width") : w);
            h = ((windowObject.has("height")) ? windowObject.getDouble("height") : h);
            x = ((windowObject.has("x")) ? windowObject.getDouble("x") : x);
            y = ((windowObject.has("y")) ? windowObject.getDouble("y") : y);
            maximized = ((windowObject.has("maximized")) ? windowObject.getBoolean("maximized") : maximized);
        }

        if (x >= 0 && y >= 0){
            window.setX(x);
            window.setY(y);
        }
        window.setWidth(w);
        window.setHeight(h);
        window.setMaximized(maximized);
    }

    public void saveEditor(){
        try {
            JSONObject windowObject = new JSONObject();
            windowObject.put("maximized", window.isMaximized());
            windowObject.put("width", window.getWidth());
            windowObject.put("height", window.getHeight());
            windowObject.put("x", window.getX());
            windowObject.put("y", window.getY());
            configManager.getParams().put("window", windowObject);
            configManager.saveParams();
        } catch (NoClassDefFoundError e){
            e.printStackTrace();
        }
    }

    public void initMenu(){
        Menu file = new Menu();
        translater.setText("file", file.textProperty());
        Menu workspace = new Menu();
        translater.setText("workspace", workspace.textProperty());
        Menu sync = new Menu("Synchronisation");

        sync.getItems().addAll(siteManager.getItems());
        file.getItems().addAll(fileManager.getMenuItems());
        workspace.getItems().addAll(workspaceManager2.getMenuItems());
        MenuBarManager.addMenu(file);
        MenuBarManager.addMenu(workspace);
        MenuBarManager.addMenu(sync);
    }

    public static Stage getWindow(){
        return window;
    }

    public static IDatabaseManager getDatabaseManager() { return databaseManager; }
    public static ModuleManager getModuleManager() { return moduleManager; }
    public static IWorkspaceManager getWorkspaceManager(){
        return workspaceManager2;
    }
    public static IFileManager getFileManager(){
        return fileManager;
    }
    public static IConfigManager getConfigManager() { return configManager; }
    public static ISiteManager getSiteManager() { return siteManager; }
    public static ITranslater getTranslater() {
        return (translater);
    }

    /**
     * Méthode qui affiche la fenêtre du logiciel.
     */
    public void start(){
        window.show();
    }

    public void close(WindowEvent event){
        databaseManager.disconnect();
        CodeTab.stop();
        saveEditor();
        window.close();
        event.consume();
    }
}
