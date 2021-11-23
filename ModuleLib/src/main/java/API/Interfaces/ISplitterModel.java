package API.Interfaces;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;

import java.util.List;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface ISplitterModel extends IBaseModel{
    ISplitter getSplitter();

    void setSplitter(ISplitter splitter);

    ISplitter getParentSplitter();

    void setParentSplitter(ISplitter parentSplitter);

    ObservableList<Node> getViews();

    void setViews(ObservableList<Node> views);

    IWorkspace getWorkspace();

    void setWorkspace(IWorkspace workspace);

    List<ISplitter> getChild();

    void setChild(List<ISplitter> child);

    Orientation getOrientation();

    SimpleObjectProperty<Orientation> orientationProperty();

    void setOrientation(Orientation orientation);

    void changeOrientation();

    IWorkspace splitDown();

    IWorkspace splitUp();

    IWorkspace splitRight();

    IWorkspace splitLeft();

    IWorkspace split(Orientation or, IWorkspace toAdd, boolean toAddFirst);

    void removeSplitter(ISplitter splitter);

    void close();
}
