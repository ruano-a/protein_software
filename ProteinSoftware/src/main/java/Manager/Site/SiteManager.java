package Manager.Site;

import API.Interfaces.ISiteManager;
import Manager.Notification.NotificationManager;
import Protein.Core;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.MenuItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class SiteManager implements ISiteManager {
    private String url;
    private String awsUrl;
    private StringProperty token;
    private IntegerProperty userid;

    public SiteManager(){
        this.token = new SimpleStringProperty();
        this.userid = new SimpleIntegerProperty();
        this.url = "http://www.lantern-editor.tech/api/";
        this.url = "https://protein-eip-dev.herokuapp.com/api/";
        this.awsUrl = "http://lantern-eip-dev.s3-eu-west-1.amazonaws.com/";
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getAwsUrl() {
        return awsUrl;
    }

    @Override
    public void setAwsUrl(String awsUrl) {
        this.awsUrl = awsUrl;
    }

    @Override
    public String getToken() {
        return token.get();
    }

    @Override
    public StringProperty tokenProperty() {
        return token;
    }

    @Override
    public void setToken(String token) {
        this.token.set(token);
    }

    public int getUserid() {
        return userid.get();
    }

    public IntegerProperty useridProperty() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid.set(userid);
    }

    @Override
    public JSONObject request(String type, String route, Map<String, Object> params) {
        String response = null;
        if (type.equals("POST")){
            response = this.executePost(this.url+route, params);
        } else if (type.equals("GET")){
            response = this.executeGet(this.url+route, params);
        }
        if (response != null){
            System.out.println(response);
            return new JSONObject(response);
        }
        return null;
    }

    @Override
    public JSONObject requestAws(String type, String route) {
        String response = null;
        if (type.equals("POST")){
            response = this.executePost(this.awsUrl+route, null);
        } else if (type.equals("GET")){
            response = this.executeGet(this.awsUrl+route, null);
        }
        if (response != null){
            return new JSONObject(response);
        }
        return null;
    }

    private String executePost(String request, Map<String,Object> params){
        try {

            URL url = new URL(request);

            System.out.println("POST URL = " + request);

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

            return this.fetch("POST", conn);
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

            return this.fetch("GET", conn);
        } catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

    private String fetch(String type, HttpURLConnection conn){
        Reader in;
        try {
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
        }
        return null;
    }

    public List<MenuItem> getItems(){
        ArrayList<MenuItem> list = new ArrayList<>();
        MenuItem sync = new MenuItem("Récupérer");
        MenuItem syncSave = new MenuItem("Sauvegarder");

        syncSave.setOnAction(event -> {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("user", userid.get());
            params.put("file", Core.getConfigManager().getGlobal().toString());
            JSONObject result = request("POST", "config", params);
            System.out.println("SYNC SAVE: "+result.toString());
            NotificationManager.pop("Synchornisation", "Synchronisation réussie!", "success");
        });
        sync.setOnAction(event -> {
            Map<String, Object> params = new LinkedHashMap<>();
            params.put("user", userid.get());
            JSONObject result = request("GET", "config", params);
            System.out.println("Sync: " + result.toString());
            JSONArray configs = result.getJSONObject("data").getJSONArray("config");
            result = requestAws("GET", configs.getJSONObject(configs.length() - 1).getString("path"));
            Core.getConfigManager().setGlobal(result);
            System.out.println(result.toString());
            Core.getConfigManager().setModule("git", Core.getConfigManager().getModule("git").put("credentials", new JSONObject("{'nickname': 'lanternTest', 'password': 'lanternTest1'}")));

            NotificationManager.pop("Synchornisation", "Synchronisation récupérée!", "success");
        });

        list.add(sync);
        list.add(syncSave);
        return list;
    }

}
