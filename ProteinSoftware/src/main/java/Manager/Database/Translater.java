package Manager.Database;

import API.Interfaces.ITranslater;
import Manager.Notification.NotificationManager;
import Protein.Core;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.util.*;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class Translater implements ITranslater {
    private static String lang;
    private static List<TranlaterAssoc> cache;

    public Translater(){
        lang = "fr";
        cache = new ArrayList<>();
        Core.getDatabaseManager().loadDictionary("traduction");
    }

    public void setLang(String l){
        lang = l;
    }

    public String getLang(){
        return lang;
    }

    public String get(String code){
        String res = code;
        ResultSet rs = Core.getDatabaseManager().get("SELECT sentence FROM lang_utils WHERE code == '" + code + "' AND lang == '" + lang + "';");
        try {
            while (rs.next()){
                res = rs.getString("sentence");
            }
        } catch (Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            NotificationManager.pop("SQLite", e.getMessage(), "error");
        }
        return res;
    }

    public void setText(String code, StringProperty sp){
        TranlaterAssoc newTa = null;
        for (int i = 0; i < cache.size() && newTa == null; i++){
            if (cache.get(i).stringProperty == sp){
                newTa = cache.get(i);
            }
        }
        if (newTa == null){
            newTa = new TranlaterAssoc();
            newTa.stringProperty = sp;
            cache.add(newTa);
        }
        newTa.code = code;
        sp.setValue(get(code));
    }

    public void changeLang(String l){
        if (l.equals(lang)){
            return;
        }
        lang = l;
        for (TranlaterAssoc ta : cache){
            setText(ta.code, ta.stringProperty);
            System.out.println("New value for " + ta.code + " = " + ta.stringProperty.getValue());
        }
    }

}