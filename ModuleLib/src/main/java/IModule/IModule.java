package IModule;

import Items.Module;
import javafx.scene.Node;

/**
 * Created by ellie_e on 21/10/2015.
 *
 * Représente l'interface de base que tout module
 * doit implémenter.
 */
public interface IModule {
    public Module plug();
    public void unplug();
    public String getName();
}
