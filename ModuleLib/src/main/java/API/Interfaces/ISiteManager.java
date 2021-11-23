package API.Interfaces;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import org.json.JSONObject;

import java.util.Map;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public interface ISiteManager {
    String getUrl();

    void setUrl(String url);

    String getAwsUrl();

    void setAwsUrl(String awsUrl);

    String getToken();

    StringProperty tokenProperty();

    void setToken(String token);

    public int getUserid();

    public IntegerProperty useridProperty();

    public void setUserid(int userid);

    JSONObject request(String type, String route, Map<String, Object> params);

    JSONObject requestAws(String type, String route);
}
