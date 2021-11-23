package Modules.FindReplace;

import Common.BaseController;
import Manager.Workspace.WorkspaceManager;
import Protein.Core;
import Tab.Editor.MyCodeArea;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class FindReplaceController extends BaseController{
    @FXML
    private TextField findTextField;

    @FXML
    private TextField replaceTextField;

    @FXML
    private Button findButton;

    @FXML
    private Button replaceButton;

    @FXML
    private Button replaceAllButton;

    @FXML
    private ListView<MyCodeArea.Word> wordsFindedListView;


    private FindReplaceModel model;
    private int _index;
    private String _lastWord;

    public FindReplaceController(){
        this._index = 0;
    }

    @FXML
    private void initialize(){
        this.replaceTextField.setOnAction(this::handleReplace);
        this.replaceButton.setOnAction(this::handleReplace);
        this.replaceAllButton.setOnAction(this::handleReplaceAll);
        this.model = new FindReplaceModel(this.findTextField.textProperty(), this.replaceTextField.textProperty());
        this.wordsFindedListView.setItems(this.model.getWordsFinded());
        this.wordsFindedListView.setCellFactory(new Callback<ListView<MyCodeArea.Word>, ListCell<MyCodeArea.Word>>() {
            @Override
            public ListCell<MyCodeArea.Word> call(ListView<MyCodeArea.Word> param) {
                ListCell<MyCodeArea.Word> cell = new ListCell<MyCodeArea.Word>(){
                    @Override
                    protected void updateItem(MyCodeArea.Word item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null){
                            setText("");
                        }
                        if (item != null){
                            setText(findTextField.getText() + ", line: " + (item.pos.getMajor() + 1)  +  " pos: " + item.pos.getMinor());
                        }
                    }
                };
                return cell;
            }
        });
        this.wordsFindedListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        this.wordsFindedListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<MyCodeArea.Word>() {
            @Override
            public void changed(ObservableValue<? extends MyCodeArea.Word> observable, MyCodeArea.Word oldValue, MyCodeArea.Word newValue) {
                if (newValue != null && Core.getWorkspaceManager().getActiveWorkspace().getModel().getActiveTab() != null){
                    MyCodeArea editor = (MyCodeArea)Core.getWorkspaceManager().getActiveWorkspace().getModel().getActiveTab().getContent();
                    _index = wordsFindedListView.getItems().indexOf(newValue);
                    editor.setCaretOnPos(newValue.start);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            editor.requestFocus();
                        }
                    });
                }
            }
        });
    }

    private void handleReplace(ActionEvent event){
        this.model.replace();
        this.wordsFindedListView.getSelectionModel().select(this.model.getIndex());
        event.consume();
    }

    private void handleReplaceAll(ActionEvent event){
        this.model.replaceAll();
        event.consume();
    }

}
