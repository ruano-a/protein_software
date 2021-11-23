package Tab.Test;

import Common.MaterialDesignComponents.*;
import Manager.Database.DatabaseManager;
import Manager.Workspace.WorkspaceTab;
import Protein.Core;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.awt.*;
import java.sql.ResultSet;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class TestTab extends WorkspaceTab {

    public static class Word{
        private final SimpleStringProperty word;
        private final SimpleStringProperty type;
        private final SimpleStringProperty lang;

        private Word(String w, String t, String l){
            this.word = new SimpleStringProperty(w);
            this.type = new SimpleStringProperty(t);
            this.lang = new SimpleStringProperty(l);
        }

        public String getWord() {
            return word.get();
        }

        public SimpleStringProperty wordProperty() {
            return word;
        }

        public void setWord(String word) {
            this.word.set(word);
        }

        public String getType() {
            return type.get();
        }

        public SimpleStringProperty typeProperty() {
            return type;
        }

        public void setType(String type) {
            this.type.set(type);
        }

        public String getLang() {
            return lang.get();
        }

        public SimpleStringProperty langProperty() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang.set(lang);
        }
    }

    private TableView<Word> tv;
    private ObservableList<Word> data;

    public TestTab() {
        super();
        AnchorPane root = new AnchorPane();

        tv = new TableView<>();
        TableColumn wordC = new TableColumn<>("word");
        TableColumn typeC = new TableColumn<>("type");
        TableColumn langC = new TableColumn<>("lang");
        wordC.setCellValueFactory(new PropertyValueFactory<Word, String>("word"));
        typeC.setCellValueFactory(new PropertyValueFactory<Word, String>("type"));
        langC.setCellValueFactory(new PropertyValueFactory<Word, String>("lang"));
        tv.getColumns().addAll(wordC, typeC, langC);
        tv.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        data = FXCollections.observableArrayList();

        tv.setItems(data);

        loadData();

        MDButton reloadButton = new MDButton("Reload");
        reloadButton.setOnAction(this::reloadData);

        root.getChildren().addAll(tv, reloadButton);

        AnchorPane.setBottomAnchor(tv, 0.0);
        AnchorPane.setTopAnchor(tv, 0.0);
        AnchorPane.setRightAnchor(tv, 0.0);
        AnchorPane.setLeftAnchor(tv, 0.0);

        AnchorPane.setBottomAnchor(reloadButton, 10.0);
        AnchorPane.setRightAnchor(reloadButton, 10.0);

        root.setPrefSize(Integer.MAX_VALUE, Integer.MAX_VALUE);

        setContent(root);
        setTitle("TestTab");
    }

    private void reloadData(ActionEvent event){
        loadData();
    }

    private void loadData(){
        ResultSet rs = Core.getDatabaseManager().get("SELECT word, type, lang FROM keyword;");
        try {
            while (rs.next()){
                String word, lang, type;
                word = rs.getString("word");
                lang = rs.getString("lang");
                type = rs.getString("type");
                Word tmp = new Word(word, type, lang);
                if (!data.contains(tmp)){
                    data.add(tmp);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
