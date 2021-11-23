package API.Interfaces;

import org.json.JSONObject;

/**
 * Created by Aurélien on 15/01/2017.
 */
public interface IConfigManager {

    void setParams(JSONObject params);

    void setGlobal(JSONObject params);

    void setConfig(JSONObject params);

    JSONObject getParams();

    JSONObject getGlobal();

    JSONObject getConfig();

    JSONObject getModules();

    JSONObject getModule(String key);

    void setModules(JSONObject modules);

    void setModule(String key, JSONObject object);

    void saveParams();
}
