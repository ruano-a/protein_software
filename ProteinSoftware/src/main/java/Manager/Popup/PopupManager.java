package Manager.Popup;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.Popup;
import javafx.stage.WindowEvent;
import Protein.Core;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class PopupManager {
    private class Manager{
        private List<ProteinPopup> popups;
        private List<EventHandler<WindowEvent>> _focusHandler;

        public Manager(){
            popups = new ArrayList<>();
            Core.getWindow().focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean oldFocus, Boolean newFocus) {
                    if (newFocus){
                        displayPopup();
                    }
                    else{
                        hidePopup();
                    }
                }
            });
        }

        public ProteinPopup getPopup(){
            ProteinPopup p = new ProteinPopup();
            popups.add(p);
            return p;
        }

        public void removePopup(Popup p){
            p.hide();
            popups.remove(p);
        }

        private void displayPopup(){
            for (ProteinPopup p : popups){
                if (p.wasShowing()){
                    p.setOnShown(null);
                    p.show(Core.getWindow());
                }
            }
        }

        private void hidePopup(){
            for (Popup p : popups){
                p.hide();
            }
        }

        public void show(Popup p){
            if (Core.getWindow().isFocused()){
                p.show(Core.getWindow());
            }
        }
    }

    private static Manager _manager;

    public PopupManager(){
        _manager = new Manager();
    }

    public static ProteinPopup getPopup(){
        return _manager.getPopup();
    }

    public static void removePopup(ProteinPopup p){
        _manager.removePopup(p);
    }

    public static void show(ProteinPopup p){
        _manager.show(p);
    }
}
