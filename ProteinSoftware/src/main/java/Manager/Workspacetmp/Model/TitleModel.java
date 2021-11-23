package Manager.Workspacetmp.Model;

import API.Interfaces.ITab;
import API.Interfaces.ITitleModel;
import Common.BaseModel;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class TitleModel extends BaseModel implements ITitleModel {
    private SimpleStringProperty text;
    private SimpleBooleanProperty active;
    private SimpleObjectProperty<ITab> tab;

    public TitleModel(){
        this.text = new SimpleStringProperty();
        this.active = new SimpleBooleanProperty(false);
        this.tab = new SimpleObjectProperty<>();
    }

    public TitleModel(String text){
        this.text = new SimpleStringProperty(text);
        this.active = new SimpleBooleanProperty(false);
        this.tab = new SimpleObjectProperty<>();
    }

    @Override
    public ITab getTab() {
        return tab.get();
    }

    @Override
    public SimpleObjectProperty<ITab> tabProperty() {
        return tab;
    }

    @Override
    public void setTab(ITab tab) {
        this.tab.set(tab);
    }

    @Override
    public String getText() {
        return text.get();
    }

    @Override
    public SimpleStringProperty textProperty() {
        return text;
    }

    @Override
    public void setText(String text) {
        this.text.set(text);
    }

    @Override
    public boolean isActive() {
        return active.get();
    }

    @Override
    public SimpleBooleanProperty activeProperty() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active.set(active);
    }
}
