package Items;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Module {
    protected String      name;
    protected String      version;
    protected Button      button;
    protected StackPane   layout;
    protected boolean     isShowing;
    protected Image       icon;

    public Module(String n){
        this.name = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Button getButton(){
        return button;
    }

    public void setButton(Button b){
        this.button = b;
    }

    public Node getLayout(){
        return null;
    }

    public void setIsShowing(boolean value){
        this.isShowing = value;
    }

    public boolean isShowing(){
        return this.isShowing;
    }

    public Image getIcon() {
        return icon;
    }

    public void setIcon(Image icon) {
        this.icon = icon;
    }
}
