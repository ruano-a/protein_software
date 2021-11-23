package Modules.FileTree;

import Items.Module;
import Manager.File.*;
import Protein.Core;
import Tab.Editor.CodeTab;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

import java.io.File;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class FileTreeModule extends Module {

    private FileTree ft;

    public FileTreeModule() {
        super("Arborescence");
        ft = new FileTree();
        ft.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue != oldValue){
                File selected = ((TreeItem<File>)newValue).getValue();
                System.out.println(selected.getName());
                if (!selected.isDirectory()){
                    Core.getWorkspaceManager().getActiveWorkspace().getModel().addTab(new CodeTab(Core.getWorkspaceManager().getActiveWorkspace(), selected));
                }
            }
        });

        ft.addEventHandler(FileEvent.FILE_ANY, event -> {
            if (event.getFile() != null){
                if (event.getEventType() == FileEvent.DIR_OPEN || event.getEventType() == FileEvent.FILE_OPEN){
                    ft.addFile(event.getFile());
                }
                else if (event.getEventType() == FileEvent.FILE_DELETED){
                    ft.removeFile(event.getFile());
                }
            }
        });

        ft.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            event.consume();
        });

        ft.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.getFiles() != null){
                for (File f : db.getFiles()){
                    ft.addFile(f);
                }
                success = true;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    @Override
    public Node getLayout() {
        return ft;
    }
}
