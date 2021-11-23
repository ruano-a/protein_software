package Modules.Site.Model;

import Common.BaseModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by DeepSpaceI on 03/10/2016.
 */
public class AuthenticationModel extends BaseModel{

    private StringProperty emailCo;
    private StringProperty passwordCo;
    private StringProperty emailReg;
    private StringProperty idReg;
    private StringProperty passwordReg;
    private StringProperty token;
    private StringProperty result;

    public AuthenticationModel(){        this.emailCo = new SimpleStringProperty();
        this.passwordCo = new SimpleStringProperty();
        this.emailReg = new SimpleStringProperty();
        this.idReg = new SimpleStringProperty();
        this.passwordReg = new SimpleStringProperty();
        this.result = new SimpleStringProperty();
        this.token = new SimpleStringProperty();
    }

    public String getResult() {
        return result.get();
    }

    public StringProperty resultProperty() {
        return result;
    }

    public void setResult(String result) {
        this.result.set(result);
    }

    public String getEmailCo() {
        return emailCo.get();
    }

    public StringProperty emailCoProperty() {
        return emailCo;
    }

    public void setEmailCo(String emailCo) {
        this.emailCo.set(emailCo);
    }

    public String getPasswordCo() {
        return passwordCo.get();
    }

    public StringProperty passwordCoProperty() {
        return passwordCo;
    }    public void setPasswordCo(String passwordCo) {
        this.passwordCo.set(passwordCo);
    }

    public String getEmailReg() {
        return emailReg.get();
    }

    public StringProperty emailRegProperty() {
        return emailReg;
    }

    public void setEmailReg(String emailReg) {
        this.emailReg.set(emailReg);
    }

    public String getIdReg() {
        return idReg.get();
    }

    public StringProperty idRegProperty() {
        return idReg;
    }

    public void setIdReg(String idReg) {
        this.idReg.set(idReg);
    }

    public String getPasswordReg() {
        return passwordReg.get();
    }

    public StringProperty passwordRegProperty() {
        return passwordReg;
    }

    public void setPasswordReg(String passwordReg) {
        this.passwordReg.set(passwordReg);
    }

    public String getToken() {
        return token.get();
    }

    public StringProperty tokenProperty() {
        return token;
    }

    public void setToken(String token) {
        this.token.set(token);
    }

    public JSONObject request(String type, String route, Map<String, Object> params) {
        String response = null;
        if (type.equals("POST")){
            response = executePost("http://www.lantern-editor.tech/api/"+route, params);
        } else if (type.equals("GET")){
            response = executeGet("http://www.lantern-editor.tech/api/"+route, params);
        }
        if (response != null){
            return new JSONObject(response);
        }
        return null;
    }

    public void connect(){
        if (emailCo.get().length()> 0 && passwordCo.get().length() > 0) {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("email", emailCo.get());
            params.put("password", passwordCo.get());

            JSONObject result = request("POST", "auth/login", params);
            if (result != null) {
                Object errors = result.get("errors");
                if (errors instanceof Boolean && !result.getBoolean("errors")) {
                    this.token.set(result.getJSONObject("data").getString("token"));
                }
                this.result.set(result.toString() + "\ntoken: " + this.token.get());
            }
        }
    }

    public void register(){
        if (emailReg.get().length() > 0 && idReg.get().length() > 0 && passwordReg.get().length() > 0){
            Map<String,Object> params = new LinkedHashMap<>();
            params.put("email", emailReg.get());
            params.put("password", passwordReg.get());
            params.put("username", idReg.get());

            JSONObject result = request("POST", "register", params);
            if (result != null) {
                this.result.set(result.toString());
            }

        }
    }

    private String executePost(String request, Map<String,Object> params){
        try {

            URL url = new URL(request);

            StringBuilder postData = new StringBuilder();
            if (params != null) {
                for (Map.Entry<String, Object> param : params.entrySet()) {
                    if (postData.length() != 0) postData.append('&');
                    postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                    postData.append('=');
                    postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
                }
            }
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
            if (this.token.get() != null && this.token.get().length() > 0) {
                conn.setRequestProperty("X-XSRF-TOKEN", this.token.get());
            }
            conn.setDoOutput(true);
            conn.getOutputStream().write(postDataBytes);
            Reader in;
            if (conn.getResponseCode() == 200){
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);

            return sb.toString();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private String executeGet(String request, Map<String,Object> params){
        try {
            if (params != null && params.size() > 0) {
                request += "?";
                for (Map.Entry<String, Object> parameter : params.entrySet()) {
                    String key = parameter.getKey();
                    String value = parameter.getValue().toString();
                    request += key + "=" + value;
                }
            }
            URL url = new URL(request);

            System.out.println("GET URL = " + request);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (this.token.get() != null && this.token.get().length() > 0) {
                conn.setRequestProperty("X-XSRF-TOKEN", this.token.get());
            }
            conn.setDoOutput(true);
            Reader in;
            if (conn.getResponseCode() == 200){
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                in = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            StringBuilder sb = new StringBuilder();
            for (int c; (c = in.read()) >= 0; )
                sb.append((char) c);

            return sb.toString();
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    public void testModule() {
        Map<String,Object> params = new LinkedHashMap<>();
        JSONObject result = request("GET", "modules", params);
        if (result != null) {
            this.result.set(result.toString());
        }
    }
}