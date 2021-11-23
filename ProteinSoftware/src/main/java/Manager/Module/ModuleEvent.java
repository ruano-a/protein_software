package Manager.Module;

import Items.Module;
import javafx.event.Event;
import javafx.event.EventType;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ModuleEvent extends Event {
    public static EventType<ModuleEvent> MODULE_ANY = new EventType<>(ANY, "MODULE_ANY");
    public static EventType<ModuleEvent> MODULE_SHOW = new EventType<>(MODULE_ANY, "MODULE_SHOW");
    public static EventType<ModuleEvent> MODULE_HIDE = new EventType<>(MODULE_ANY, "MODULE_HIDE");

    private Module module;

    public ModuleEvent(EventType<ModuleEvent> evtType, Module m){
        super(evtType);
        module = m;
    }

    public Module getModule(){
        return module;
    }
}
