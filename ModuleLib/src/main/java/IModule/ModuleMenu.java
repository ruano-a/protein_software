package IModule;

import Items.Module;
import javafx.scene.Node;
import javafx.scene.control.Button;

/**
 * Created by ellie_e on 03/02/2016.
 */
public class ModuleMenu implements IModule {
    protected Module module;

    public ModuleMenu(Module module) {
        this.module = module;
    }

    @Override
    public Module plug() {
        return (this.module);
    }

    @Override
    public void unplug() {

    }

    @Override
    public String getName() {
        return null;
    }
}
