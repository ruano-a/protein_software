package API.Interfaces;
import javafx.beans.property.StringProperty;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface ITranslater {
    String get(String code);

    void setText(String code, StringProperty sp);

    void changeLang(String l);

    void setLang(String l);

    String getLang();
}
