package Manager.Popup;

import javafx.stage.Popup;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ProteinPopup extends Popup {
    private boolean wasShowing;

    public ProteinPopup(){
        this.wasShowing = false;
    }

    @Override
    protected void show() {
        super.show();
        this.wasShowing = true;
    }

    @Override
    public void hide() {
        super.hide();
        this.wasShowing = false;
    }

    public boolean wasShowing(){
        return this.wasShowing;
    }
}
