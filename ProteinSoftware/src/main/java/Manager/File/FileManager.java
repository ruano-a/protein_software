package Manager.File;

import API.Interfaces.IFileManager;
import API.Interfaces.ITab;
import Tab.Editor.CodeTab;
import Manager.Notification.NotificationManager;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import Protein.Core;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class FileManager implements IFileManager{
    private File    lastDirectory;

    public File chooseFile(){
        FileChooser fc = new FileChooser();
        File file;
        fc.setTitle(Core.getTranslater().get("open-file"));
        if (lastDirectory != null){
            fc.setInitialDirectory(lastDirectory);
        }
        file = fc.showOpenDialog(Core.getWindow());
        if (file.getParentFile() != null){
            lastDirectory = file.getParentFile();
        }
        return file;
    }

    public File chooseSaveFile(){
        FileChooser fc = new FileChooser();
        File file;
        fc.setTitle(Core.getTranslater().get("save-file-as"));
        if (lastDirectory != null){
            fc.setInitialDirectory(lastDirectory);
        }
        file = fc.showSaveDialog(Core.getWindow());
        if (file != null){
            lastDirectory = file.getParentFile();
        }
        return file;
    }

    public List<File> chooseMultiFile(){
        FileChooser fc = new FileChooser();
        List<File> files;
        fc.setTitle(Core.getTranslater().get("open-file"));
        if (lastDirectory != null){
            fc.setInitialDirectory(lastDirectory);
        }
        files = fc.showOpenMultipleDialog(Core.getWindow());
        if (files != null && files.size() > 0){
            if (files.get(0).getParentFile() != null){
                lastDirectory = files.get(0).getParentFile();
            }
        }
        return files;
    }

    public File chooseDirectory(){
        DirectoryChooser dc = new DirectoryChooser();
        File dir;
        dc.setTitle(Core.getTranslater().get("open-directory"));
        if (lastDirectory != null){
            dc.setInitialDirectory(lastDirectory);
        }
        dir = dc.showDialog(Core.getWindow());
        if (dir != null){
            lastDirectory = dir;
        }
        return dir;
    }

    public void newFile(){
        FileEvent fe = new FileEvent(FileEvent.FILE_NEW, null);
        Core.getModuleManager().fireEventToAllModule(fe);
//        WorkspaceManager.getActiveWorkspace().fireEvent(fe);
        Core.getWorkspaceManager().getActiveWorkspace().getView().fireEvent(fe);
    }

    public void openFile(File file){
        if (file != null){
            FileEvent fe = new FileEvent(FileEvent.FILE_OPEN, file);
//                WorkspaceManager.getActiveWorkspace().openFile(file);
            Core.getWorkspaceManager().getActiveWorkspace().getView().fireEvent(fe);
            Core.getModuleManager().fireEventToAllModule(fe);
        }
    }

    public void openFolder(File file){
        if (file != null){
            FileEvent fe = new FileEvent(FileEvent.DIR_OPEN, file);
            Core.getModuleManager().fireEventToAllModule(fe);
        }
    }

    public void openFiles(List<File> files){
        if (files != null){
            for (File f : files){
                openFile(f);
            }
        }
    }

    public String getFileContent(File file){
        String tmp = "";
        if (file != null){
            StringBuilder sb = new StringBuilder();
            try (FileInputStream fis = new FileInputStream(file);
                 BufferedInputStream bis = new BufferedInputStream(fis) ) {
                while ( bis.available() > 0 ) {
                    sb.append((char)bis.read());
                }
                tmp = sb.toString();
            }
            catch ( Exception e ) {
                System.err.println("Le fichier " + file.getPath() + "n'existe pas.");
                //e.printStackTrace();
            }
        }
        return tmp;
    }

    public boolean saveFile(File f, String content, boolean silent){
        boolean success = false;
        try (FileOutputStream fos = new FileOutputStream(f); BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(content.getBytes());
            bos.flush();
            success = true;
        }
        catch ( Exception e ) {
            success = false;
            System.out.println("Manager.File save failed (error: " + e.getLocalizedMessage() + ")");
            NotificationManager.pop("Save error", "Manager.File save failed (error: " + e.getLocalizedMessage() + ")", "error");
            e.printStackTrace();
        }
        finally {
            if ( success ) {
                FileEvent fe = new FileEvent(FileEvent.FILE_SAVED, f);
//                    WorkspaceManager.getActiveWorkspace().fireEvent(fe);
                if (!silent){
                    Core.getWorkspaceManager().getActiveWorkspace().getView().fireEvent(fe);
                }
            }
        }
        return success;
    }

    public boolean saveFileAs(File as, String content, boolean silent){
        boolean success = false;
        if (as != null){
            success = this.saveFile(as, content, silent);
        }
        return success;
    }

    public List<MenuItem> getMenuItems(){
        List<MenuItem> items = new ArrayList<>();

        MenuItem newFile = new MenuItem();
        Core.getTranslater().setText("new-file", newFile.textProperty());
        MenuItem openFile = new MenuItem();
        Core.getTranslater().setText("open-file", openFile.textProperty());
        MenuItem openDir = new MenuItem();
        Core.getTranslater().setText("open-directory", openDir.textProperty());
        MenuItem saveFile = new MenuItem();
        Core.getTranslater().setText("save-file", saveFile.textProperty());
        MenuItem saveFileAs = new MenuItem();
        Core.getTranslater().setText("save-file-as", saveFileAs.textProperty());

        newFile.setOnAction(event -> newFile());
        newFile.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.CONTROL_DOWN));
        openFile.setOnAction(event -> openFiles(chooseMultiFile()));
        openFile.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.CONTROL_DOWN));
        openDir.setOnAction(event -> openFolder(chooseDirectory()));
        saveFile.setOnAction(event -> {
//            WorkspaceTab tab = WorkspaceManager.getActiveWorkspace().getActiveTab();
            ITab tab = Core.getWorkspaceManager().getActiveWorkspace().getModel().getActiveTab();
            if (tab != null){
                if (tab.getFile() == null){
                    tab.setFile(chooseSaveFile());
                }
                saveFile(tab.getFile(), ((CodeTab)tab).getText(), false);
            }
        });
        saveFile.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        saveFileAs.setOnAction(event -> {
//            WorkspaceTab tab = WorkspaceManager.getActiveWorkspace().getActiveTab();
            ITab tab = Core.getWorkspaceManager().getActiveWorkspace().getModel().getActiveTab();
            if (tab != null){
                tab.setFile(chooseSaveFile());
                saveFileAs(tab.getFile(), ((CodeTab)tab).getText(), false);
            }
        });
        saveFileAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, KeyCombination.CONTROL_DOWN));

        items.add(newFile);
        items.add(openFile);
        items.add(openDir);
        items.add(saveFile);
        items.add(saveFileAs);
        return items;
    }
}
