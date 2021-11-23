package Modules.FileTree;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class FileTree extends TreeView {

    List<File> files;

    public FileTree(){
        this.files = new ArrayList<>();
        TreeItem<File> root = new TreeItem<>();
        this.getStyleClass().add("filetree");
        initTreeView();
        this.setRoot(root);
    }

    public FileTree(File f)
    {
        this.files = new ArrayList<>();
        this.files.add(f);
        initTreeView();
        addFile(f);
    }

    private void initTreeView(){
        setShowRoot(false);
        this.setContextMenu(this.createContextMenu());


        this.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            @Override
            public TreeCell<File> call(TreeView<File> param) {
                return new TreeCell<File>(){
                    @Override
                    protected void updateItem(File item, boolean empty) {
                        if (empty){
                            setGraphic(null);
                            setText(null);
                        } else{
                            if (item.isDirectory()){
                                setText(item.getName());
                            } else {
                                setText(item.getName());
                            }
                        }
                        super.updateItem(item, empty);
                    }
                };
            }
        });
        getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private ContextMenu createContextMenu(){
        ContextMenu contextMenu = new ContextMenu();
        MenuItem newFile = new MenuItem("New file");
        MenuItem newFolder = new MenuItem("New folder");
        MenuItem copy = new MenuItem("Copy");
        MenuItem cut = new MenuItem("Cut");
        MenuItem paste = new MenuItem("Paste");
        MenuItem rename = new MenuItem("Rename");
        MenuItem remove = new MenuItem("Remove");
        MenuItem delete = new MenuItem("Delete");
        MenuItem showInExplorer = new MenuItem("Show in explorer");

        copy.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            ArrayList<File> array = new ArrayList<File>();
            array.add(((TreeItem<File>)getSelectionModel().getSelectedItem()).getValue());
            content.putFiles(array);
            clipboard.setContent(content);
        });

        cut.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            ArrayList<File> array = new ArrayList<File>();
            array.add(((TreeItem<File>)getSelectionModel().getSelectedItem()).getValue());
            getChildren().remove(getSelectionModel().getSelectedItem());
            content.putFiles(array);
            clipboard.setContent(content);
        });

        paste.setOnAction(event -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            List<File> array = clipboard.getFiles();
            for (File f : array){
                this.addFile(f);
            }

        });

        showInExplorer.setOnAction(event -> {
            try {
                Runtime.getRuntime().exec("explorer.exe /select," + ((TreeItem<File>)getSelectionModel().getSelectedItem()).getValue().getPath());
            } catch (IOException e){
                e.printStackTrace();
            }
        });
        remove.setOnAction(event -> {
//            removeFile(((TreeItem<File>)getSelectionModel().getSelectedItem()).getValue());
            getChildren().remove(getSelectionModel().getSelectedItem());
        });

        delete.setOnAction(event -> {
            ((TreeItem<File>)getSelectionModel().getSelectedItem()).getValue().delete();
            ((TreeItem<File>)getSelectionModel().getSelectedItem()).getParent().getChildren().remove((TreeItem<File>)getSelectionModel().getSelectedItem());
        });

        contextMenu.getItems().addAll(newFile, newFolder, new SeparatorMenuItem(), copy, cut, paste, delete, rename, new SeparatorMenuItem(), remove, showInExplorer);
        return contextMenu;
    }

    public void addFile(File f){
        if (!this.files.contains(f)){
            this.getRoot().getChildren().add(createNode(f));
            this.files.add(f);
        }
    }

    public void removeFile(File f){

    }

    private TreeItem<File> createNode(final File f) {
        return new TreeItem<File>(f) {
            private boolean isLeaf;
            private boolean isFirstTimeChildren = true;
            private boolean isFirstTimeLeaf = true;

            @Override public ObservableList<TreeItem<File>> getChildren() {
                if (isFirstTimeChildren) {
                    isFirstTimeChildren = false;
                    super.getChildren().setAll(buildChildren(this));
                }
                return super.getChildren();
            }

            @Override public boolean isLeaf() {
                if (isFirstTimeLeaf) {
                    isFirstTimeLeaf = false;
                    File f = (File) getValue();
                    isLeaf = f.isFile();
                }

                return isLeaf;
            }

            private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
                File f = TreeItem.getValue();
                if (f != null && f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

                        for (File childFile : files) {
                            children.add(createNode(childFile));
                        }

                        return children;
                    }
                }

                return FXCollections.emptyObservableList();
            }
        };
    }

}
