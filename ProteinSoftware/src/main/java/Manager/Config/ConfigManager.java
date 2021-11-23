package Manager.Config;

import API.Interfaces.IConfigManager;
import Protein.Core;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ConfigManager implements IConfigManager {
    private File configFile;
    private JSONObject params;
    private JSONObject config;
    private JSONObject global;
    private JSONObject modules;

    public ConfigManager(){

        this.configFile = new File(Core.getBaseUrl().concat("config.json"));
        this.config = this.getFileJSON();
        if (this.config.getJSONArray("instances").length() > 0){
            this.params = this.config.getJSONArray("instances").getJSONObject(this.config.getJSONArray("instances").length() - 1);
            this.config.getJSONArray("instances").remove(this.config.getJSONArray("instances").length() - 1);
        } else {
            this.params = new JSONObject();
        }
        this.global = this.config.getJSONObject("global");
        this.modules = this.global.getJSONObject("modules");
        Core.getFileManager().saveFile(this.configFile, this.config.toString(), true);
    }

    public JSONObject getFileJSON(){
        JSONObject res = null;

        if (this.configFile.exists()){
            try {
                res = new JSONObject(Core.getFileManager().getFileContent(this.configFile));
                if (!res.has("instances")){
                    res.put("instances", new JSONArray());
                }
                if (!res.has("global")){
                    res.put("global", new JSONObject());
                }
            } catch (JSONException e){
                res = new JSONObject("{'instances': [], 'global': {'modules': {}}}");
            }
        } else {
            res = new JSONObject("{'instances': [], 'global': {'modules': {}}}");
        }
        return res;
    }

    public JSONObject getConfig() {
        return config;
    }

    public void setConfig(JSONObject config) {
        this.config = config;
    }

    @Override
    public JSONObject getParams() {
        return params;
    }

    @Override
    public void setParams(JSONObject params) {
        this.params = params;
    }

    public JSONObject getGlobal() {
        return global;
    }

    public void setGlobal(JSONObject global) {
        this.global = global;
    }

    public JSONObject getModules() {
        return modules;
    }

    public void setModules(JSONObject modules) {
        this.modules = modules;
    }

    public JSONObject getModule(String key){
        if (this.modules.has(key)){
            return this.modules.getJSONObject(key);
        }
        return new JSONObject();
    }

    public void setModule(String key, JSONObject object){
        if (this.modules.has(key)){
            this.modules.remove(key);
        }
        this.modules.put(key, object);
    }

    @Override
    public void saveParams(){
        JSONObject lastconfig = this.getFileJSON();
        if (lastconfig.has("instances")){
            lastconfig.getJSONArray("instances").put(this.params);
        }
        else {
            lastconfig.put("instances", new JSONArray(this.params));
        }
        lastconfig.remove("global");
        this.global.remove("modules");
        this.global.put("modules", this.modules);
        lastconfig.put("global", this.global);
        Core.getFileManager().saveFile(this.configFile, lastconfig.toString(), true);
    }
}
