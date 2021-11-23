package Modules.Site.Controller;

import Common.BaseController;
import Modules.Site.Model.ModuleListModel;
import Modules.Site.Module;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.layout.FlowPane;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ModuleListController extends BaseController {

    @FXML
    private FlowPane modulesLister;

    public ModuleListController() {
        this.model = new ModuleListModel();
    }

    @FXML
    public void initialize(){
        this.getModel().getModules().addListener(new ListChangeListener<Module>() {
            @Override
            public void onChanged(Change<? extends Module> c) {
                while (c.next()) {
                    if (c.wasPermutated()) {
                        for (int i = c.getFrom(); i < c.getTo(); ++i) {
                            //permutate
                        }
                    } else if (c.wasUpdated()) {
                        //update item
                    } else {
                        for (Module remitem : c.getRemoved()) {
                            modulesLister.getChildren().remove(remitem.getView());
                        }
                        for (Module additem : c.getAddedSubList()) {
                            modulesLister.getChildren().add(additem.getView());
                        }
                    }
                }
            }
        });
    }

    @Override
    public ModuleListModel getModel() {
        return (ModuleListModel)this.model;
    }
}
