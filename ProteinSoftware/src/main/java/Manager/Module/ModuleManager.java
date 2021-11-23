package Manager.Module;

import Common.MaterialDesignComponents.*;
import IModule.IModule;
import Items.Module;
import ModuleLib.ModuleLoader;
import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import Protein.Core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ModuleManager {
    private VBox            moduleList;
    private List<Module>    modules;
    private Module          activeModule;

    public ModuleManager(){
        moduleList = new VBox();
        modules = new ArrayList<>();

        moduleList.getStyleClass().add("modules-list");
    }

    public void initialize(File f){
        List<IModule> modules = ModuleLoader.loadModules("ModuleMenu-Class", f);
        for (IModule module : modules){
            this.attachModule(module.plug());
        }
    }

    public void attachModule(Module m){
        for (Module mod : modules){
            if (mod.getName().equals(m.getName())){
                System.err.println("Module already attach.");
                return;
            }
        }
        MDButton b = new MDButton(m.getName());
        if (m.getIcon() != null){
            ImageView iv = new ImageView(m.getIcon());
            iv.getStyleClass().add("no-padding-insets");
            b.setGraphic(new ImageView(m.getIcon()));
            b.setText("");
        }
        b.getStyleClass().add("module-button");
        if (m.getLayout() != null){
            m.getLayout().getStyleClass().add("module-menu");
            b.setOnAction(event ->  {
                ModuleEvent me;
                if (activeModule == m){
                    activeModule = null;
                    m.getButton().getStyleClass().remove("active");
                    me = new ModuleEvent(ModuleEvent.MODULE_HIDE, m);
                }
                else{
                    if (activeModule != null){
                        activeModule.getButton().getStyleClass().remove("active");
                    }
                    activeModule = m;
                    m.getButton().getStyleClass().add("active");
                    me = new ModuleEvent(ModuleEvent.MODULE_SHOW, m);
                }
                Core.getWindow().fireEvent(me);
            });
            MenuItem detach = new MenuItem("Détacher le module");
            detach.setOnAction(event -> detachModule(m));
            b.setContextMenu(new ContextMenu(detach));
        }
        m.setButton(b);
        modules.add(m);
        moduleList.getChildren().add(b);
    }

    public void detachModule(Module m){
        modules.remove(m);
        moduleList.getChildren().remove(m.getButton());
    }

    public void fireEventToAllModule(Event event){
        for (Module m : modules){
            if (m.getLayout() != null){
                m.getLayout().fireEvent(event);
            }
        }
    }

    public VBox getLayout(){
        return moduleList;
    }
}
