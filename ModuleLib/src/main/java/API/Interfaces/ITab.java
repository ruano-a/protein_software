package API.Interfaces;

import javafx.scene.Node;

import java.io.File;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface ITab {

    ITitle getTitle();

    Node getContent();

    void setContent(Node content);

    IWorkspace getWorkspace();

    void setWorkspace(IWorkspace workspace);

    File getFile();

    void setFile(File file);
}
