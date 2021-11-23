package Common.MaterialDesignComponents;

import com.sun.javafx.scene.control.skin.ComboBoxBaseSkin;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Skin;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class MDComboBox<T> extends ComboBox {

    public MDComboBox(){
        super();
        getStyleClass().addAll("md-combo-box");
    }

    public MDComboBox(ObservableList<T> items){
        super(items);
        getStyleClass().addAll("md-combo-box");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        final ComboBoxBaseSkin comboBoxSkin = new ComboBoxListViewSkin<>(this);

        // Adding circleRipple as fist node of button nodes to be on the bottom
        return comboBoxSkin;
    }

}
