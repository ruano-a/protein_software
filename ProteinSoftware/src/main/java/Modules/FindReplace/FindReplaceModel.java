package Modules.FindReplace;

import API.Interfaces.ITab;
import API.Interfaces.IWorkspace;
import Common.BaseModel;
import Protein.Core;
import Tab.Editor.MyCodeArea;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class FindReplaceModel extends BaseModel {
    
    private int index;
    private StringProperty toFind;
    private StringProperty toReplace;
    private ObservableList<MyCodeArea.Word> wordsFinded;
    private ChangeListener<? super ITab> tabListener;

    public FindReplaceModel(StringProperty f, StringProperty r){
        this.toFind = f;
        this.toReplace = r;
        this.tabListener = new ChangeListener<ITab>() {
            @Override
            public void changed(ObservableValue<? extends ITab> observable, ITab oldValue, ITab newValue) {
                System.out.println("TAB CHANGE");
                if (oldValue != null){
                    endFind(oldValue);
                }
                if (newValue != null){
                    startFind(newValue);
                } else {
                    if (wordsFinded != null){
                        wordsFinded.clear();
                    }
                }
            }
        };
        this.wordsFinded = FXCollections.observableArrayList();
        Core.getWorkspaceManager().activeWorkspaceProperty().addListener(new ChangeListener<IWorkspace>() {
            @Override
            public void changed(ObservableValue<? extends IWorkspace> observable, IWorkspace oldValue, IWorkspace newValue) {
                System.out.println("WORSKAPCE CHANGE");
                if (oldValue != null){
                    oldValue.getModel().activeTabProperty().removeListener(tabListener);
                    endFind(oldValue.getModel().getActiveTab());
                }
                if (newValue != null){
                    newValue.getModel().activeTabProperty().addListener(tabListener);
                    startFind(newValue.getModel().getActiveTab());
                }
            }
        });
        Core.getWorkspaceManager().getActiveWorkspace().getModel().activeTabProperty().addListener(tabListener);
    }

    private void endFind(ITab tab){
        if (tab != null){
            MyCodeArea editor = (MyCodeArea) tab.getContent();
            editor.find(null, null);
        }
    }

    private void startFind(ITab tab){
        if (tab != null){
            MyCodeArea editor = (MyCodeArea) tab.getContent();
            editor.find(toFind, wordsFinded);
        }
    }
    
    public void replace(){
        int tmplength = this.wordsFinded.size();
        if (toFind.get().length() > 0 && toReplace.get().length() > 0 && wordsFinded.size() > 0
                && this.wordsFinded.size() > 0 && !this.toFind.get().equals(this.toReplace.get())){
            MyCodeArea editor = (MyCodeArea) Core.getWorkspaceManager().getActiveWorkspace().getModel().getActiveTab().getContent();
            editor.replaceWord(this.wordsFinded.get(this.index), toReplace.get());
            if (tmplength == this.wordsFinded.size()){
                this.moveIndex(this.index + 1);
            }
            else {
                this.moveIndex(this.index);
            }
        }
    }

    private void moveIndex(int i){
        if (i < this.wordsFinded.size()){
            this.index = i;
        }
        else {
            this.index = 0;
        }
    }
    
    public void replaceAll(){
        List<MyCodeArea.Word> words = new ArrayList<>(this.wordsFinded);
        if (toFind.get().length() > 0 && words.size() > 0){
            MyCodeArea editor = (MyCodeArea) Core.getWorkspaceManager().getActiveWorkspace().getModel().getActiveTab().getContent();
            while (words.size() > 0) {
                editor.replaceWord(words.get(words.size()-1), toReplace.get());
                words.remove(words.size() - 1);
            }
        }
        this.moveIndex(0);
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getToFind() {
        return toFind.get();
    }

    public StringProperty toFindProperty() {
        return toFind;
    }

    public void setToFind(String toFind) {
        this.toFind.set(toFind);
    }

    public String getToReplace() {
        return toReplace.get();
    }

    public StringProperty toReplaceProperty() {
        return toReplace;
    }

    public void setToReplace(String toReplace) {
        this.toReplace.set(toReplace);
    }

    public ObservableList<MyCodeArea.Word> getWordsFinded() {
        return wordsFinded;
    }

    public void setWordsFinded(ObservableList<MyCodeArea.Word> wordsFinded) {
        this.wordsFinded = wordsFinded;
    }
}
