package API.Interfaces;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface ITitleModel extends IBaseModel{
    ITab getTab();

    SimpleObjectProperty<ITab> tabProperty();

    void setTab(ITab tab);

    String getText();

    SimpleStringProperty textProperty();

    void setText(String text);

    boolean isActive();

    SimpleBooleanProperty activeProperty();

    void setActive(boolean active);
}
