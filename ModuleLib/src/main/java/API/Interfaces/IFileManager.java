package API.Interfaces;

import javafx.scene.control.MenuItem;

import java.io.File;
import java.util.List;

/**
 * Created by Aurélien on 02/12/2016.
 */
public interface IFileManager {
    public File chooseFile();

    public File chooseSaveFile();

    public List<File> chooseMultiFile();

    public File chooseDirectory();

    public void newFile();

    public void openFile(File file);

    public void openFolder(File file);

    public void openFiles(List<File> files);

    public String getFileContent(File file);

    public boolean saveFile(File f, String content, boolean silent);

    public boolean saveFileAs(File as, String content, boolean silent);

    public List<MenuItem> getMenuItems();
}
