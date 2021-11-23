package Modules.Site.Model;

import Common.BaseModel;
import Protein.Core;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ConnexionModel extends BaseModel {

    private SimpleBooleanProperty connected;
    private SimpleStringProperty email;
    private SimpleStringProperty password;
    private SimpleStringProperty identifiant;
    private SimpleStringProperty regEmail;
    private SimpleStringProperty regPassword;
    private SiteModel siteModel;

    public ConnexionModel() {
        JSONObject object = Core.getConfigManager().getModule("Site");
        this.connected = new SimpleBooleanProperty(false);
        this.email = new SimpleStringProperty(object.has("email") ? object.getString("email") : "");
        this.password = new SimpleStringProperty(object.has("password") ? object.getString("password") : "");
        this.identifiant = new SimpleStringProperty("");
        this.regEmail = new SimpleStringProperty("");
        this.regPassword= new SimpleStringProperty("");
    }

    public boolean isConnected() {
        return connected.get();
    }

    public SimpleBooleanProperty connectedProperty() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected.set(connected);
    }

    public String getEmail() {
        return email.get();
    }

    public SimpleStringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPassword() {
        return password.get();
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getIdentifiant() {
        return identifiant.get();
    }

    public SimpleStringProperty identifiantProperty() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant.set(identifiant);
    }

    public String getRegEmail() {
        return regEmail.get();
    }

    public SimpleStringProperty regEmailProperty() {
        return regEmail;
    }

    public void setRegEmail(String regEmail) {
        this.regEmail.set(regEmail);
    }

    public String getRegPassword() {
        return regPassword.get();
    }

    public SimpleStringProperty regPasswordProperty() {
        return regPassword;
    }

    public void setRegPassword(String regPassword) {
        this.regPassword.set(regPassword);
    }

    public SiteModel getSiteModel() {
        return siteModel;
    }

    public void setSiteModel(SiteModel siteModel) {
        this.siteModel = siteModel;
    }

    public void connect(){
        if (email.get().length()> 0 && password.get().length() > 0) {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("email", email.get());
            params.put("password", password.get());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    JSONObject result = Core.getSiteManager().request("POST", "auth/login", params);
                    if (result != null) {
                        Object errors = result.get("errors");
                        if (errors instanceof Boolean && !(Boolean)errors) {
                            Core.getSiteManager().setToken(result.getJSONObject("data").getString("token"));
                            connected.set(true);
                            JSONObject object = Core.getConfigManager().getModule("Site");
                            object.put("email", email.get());
                            object.put("password", password.get());
                            Core.getConfigManager().setModule("Site", object);
                            Core.getSiteManager().setUserid(result.getJSONObject("data").getJSONObject("user").getInt("id"));
                        } else {
                            //TODO gestion connect erreur
                        }
                    }
                }
            });
        }
    }

    public void disconnect(){

    }

    public void register(){
        if (this.regEmail.get().length() > 0 && identifiant.get().length() > 0 && regPassword.get().length() > 0){
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("email", regEmail.get());
            params.put("password", regPassword.get());
            params.put("username", identifiant.get());

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    JSONObject result = Core.getSiteManager().request("POST", "register", params);
                    if (result != null) {
                        Object errors = result.get("errors");
                        if (errors instanceof Boolean && !(Boolean)errors)  {
                            Core.getSiteManager().setToken(result.getJSONObject("data").getString("token"));
                            connected.set(true);
                        } else {
                            //TODO gestion register erreur
                        }
                    }
                }
            });
        }
    }

}
