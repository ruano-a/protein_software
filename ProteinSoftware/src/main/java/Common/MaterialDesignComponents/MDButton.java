package Common.MaterialDesignComponents;

import com.sun.javafx.scene.control.skin.ButtonSkin;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class MDButton extends Button {

    private MDBase mdBase;

    public MDButton() {
        super();
        getStyleClass().addAll("md-button");
        mdBase = new MDBase(this);
    }

    public MDButton(String text) {
        super(text);
        getStyleClass().addAll("md-button");
        mdBase = new MDBase(this);
    }

    public MDButton(String text, Node graphic) {
        super(text, graphic);
        getStyleClass().addAll("md-button");
        mdBase = new MDBase(this);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        final ButtonSkin buttonSkin = new ButtonSkin(this);
        // Adding circleRipple as fist node of button nodes to be on the bottom
        this.getChildren().add(0, mdBase.getCircleRipple());
        return buttonSkin;
    }
}
