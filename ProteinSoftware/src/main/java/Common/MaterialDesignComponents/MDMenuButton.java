package Common.MaterialDesignComponents;

import com.sun.javafx.scene.control.skin.MenuButtonSkin;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Skin;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */

public class MDMenuButton extends MenuButton {

    private MDBase mdBase;
    public MDMenuButton() {
        super();
        getStyleClass().addAll("md-menu-button");
        mdBase = new MDBase(this);
    }

    public MDMenuButton(String text) {
        super(text);
        getStyleClass().addAll("md-menu-button");
        mdBase = new MDBase(this);
    }

    public MDMenuButton(String text, Node graphic) {
        super(text, graphic);
        getStyleClass().addAll("md-menu-button");
        mdBase = new MDBase(this);
    }

    public MDMenuButton(String text, Node graphic, MenuItem... items) {
        super(text, graphic, items);
        getStyleClass().addAll("md-menu-button");
        mdBase = new MDBase(this);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        final MenuButtonSkin buttonSkin = new MenuButtonSkin(this);
        // Adding circleRipple as fist node of button nodes to be on the bottom
        this.getChildren().add(0, mdBase.getCircleRipple());
        return buttonSkin;
    }

}