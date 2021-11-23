package Manager.Workspacetmp.Controller;

import API.Interfaces.ISplitterController;
import API.Interfaces.ISplitterModel;
import Common.BaseController;
import Manager.Workspacetmp.Model.SplitterModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class SplitterController extends BaseController implements ISplitterController {
    protected ISplitterModel model;

    @FXML
    private SplitPane layout;

    public SplitterController(){
        this.model = new SplitterModel();
    }

    @Override
    public ObservableList<MenuItem> getContextMenu(){
        return null;
    }

    @FXML
    private void initialize(){
        this.model.getViews().addListener(new ListChangeListener<Node>() {
            @Override
            public void onChanged(Change<? extends Node> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutate
                        }
                    } else if (c.wasUpdated()) {
                        //update item
                    } else {
                        for (Node remitem : c.getRemoved()) {
                            layout.getItems().remove(remitem);
                        }
                        for (Node additem : c.getAddedSubList()) {
                            layout.getItems().add(additem);
                        }
                    }
                }
            }
        });
        this.layout.orientationProperty().bind(this.model.orientationProperty());
    }

    @Override
    public ISplitterModel getModel() {
        return model;
    }
}
