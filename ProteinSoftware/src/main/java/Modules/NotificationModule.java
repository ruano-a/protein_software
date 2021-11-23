package Modules;

import Common.MaterialDesignComponents.*;
import Manager.Database.DatabaseManager;
import Manager.Database.Translater;
import Items.Module;
import Manager.Notification.NotificationManager;
import Protein.Core;
import Tab.Test.TestTab;
import Manager.Workspace.WorkspaceManager;
import com.sun.javafx.collections.ObservableListWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class NotificationModule extends Module {
    private VBox    test;

    public NotificationModule(){
        super("Notification");
        Button a = new MDButton("Display Succes notification");
        Button b = new MDButton("Display Info notification");
        Button c = new MDButton("Display Error notification");
        Button d = new MDButton("Display Long notification");
        Button e = new MDButton("Open test tab");
        Button f = new MDButton("test");

        TextField url = new TextField();

        List<String> langList = new ArrayList<>();
        langList.add("fr");
        langList.add("en");
        ComboBox<String> langCBB = new MDComboBox<>(new ObservableListWrapper<>(langList));

        TextField dictTF = new TextField();
        MDButton loadDict = new MDButton("Load data");
        HBox DictHB = new HBox(dictTF, loadDict);
        DictHB.setSpacing(5);

        a.setOnAction(event -> NotificationManager.pop("Notifcation Succès", "Vous avez cliqué sur le bouton vert, donc on affiche cette popup comme test", "success"));
        b.setOnAction(event -> NotificationManager.pop("Notifcation Info", "Vous avez cliqué sur le bouton bleu, donc on affiche cette popup comme test", "info"));
        c.setOnAction(event -> NotificationManager.pop("Notifcation Erreur", "Vous avez cliqué sur le bouton rouge, donc on affiche cette popup comme test", "error"));
        d.setOnAction(event -> NotificationManager.pop(
                "Tab.Test longue notification avec beaucoup de texte",
                "Vous avez cliqué sur le bouton rouge, donc on affiche cette popup comme test et ceci est un teste de longueur donc on y insère beaucoup de texte !Vous avez cliqué sur le bouton rouge, donc on affiche cette popup comme test et ceci est un teste de longueur donc on y insère beaucoup de texte !",
                "info"));
        e.setOnAction(event -> {
            TestTab tab = new TestTab();
            WorkspaceManager.addTab(tab);
        });

        langCBB.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Core.getTranslater().changeLang(newValue);
            }
        });

        loadDict.setOnAction(event -> {
            Core.getDatabaseManager().loadDictionary(dictTF.getText());
        });
        dictTF.setOnAction(loadDict.getOnAction());
        DictHB.setAlignment(Pos.CENTER);

        a.getStyleClass().addAll("success");
        b.getStyleClass().addAll("info");
        c.getStyleClass().addAll("error");
        test = new VBox(a,b,c, d, e, url, f, langCBB, DictHB);
        test.setSpacing(5);
        test.setAlignment(Pos.CENTER);
    }

    @Override
    public Node getLayout() {
        return test;
    }
}
