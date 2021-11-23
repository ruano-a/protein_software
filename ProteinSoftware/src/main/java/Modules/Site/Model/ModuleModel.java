package Modules.Site.Model;

import Common.BaseModel;
import Manager.Site.HttpDownloadUtility;
import ModuleLib.ModuleLoader;
import Protein.Core;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class ModuleModel extends BaseModel {

    private IntegerProperty id;
    private StringProperty name;
    private StringProperty link;
    private StringProperty imgpath;
    private StringProperty jarpath;
    private JSONObject json;
    private SimpleObjectProperty<Image> image;

    public ModuleModel(){
        this.id = new SimpleIntegerProperty(-1);
        this.name = new SimpleStringProperty();
        this.link = new SimpleStringProperty();
        this.imgpath = new SimpleStringProperty();
        this.jarpath = new SimpleStringProperty();
        this.image = new SimpleObjectProperty<>();
    }

    public void update(){
        this.id.set(this.json.getInt("id"));
        this.name.set(this.json.getString("name"));
        this.imgpath.set(this.json.getString("imgpath"));
        this.jarpath.set(this.json.getString("filepath"));
        this.link.set(Core.getSiteManager().getAwsUrl()+this.jarpath.get());
        this.image.set(new Image(Core.getSiteManager().getAwsUrl()+this.imgpath));
        System.out.println(Core.getSiteManager().getAwsUrl()+this.imgpath.get());
    }

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
        this.update();
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getLink() {
        return link.get();
    }

    public StringProperty linkProperty() {
        return link;
    }

    public void setLink(String link) {
        this.link.set(link);
    }

    public String getImgpath() {
        return imgpath.get();
    }

    public StringProperty imgpathProperty() {
        return imgpath;
    }

    public void setImgpath(String imgpath) {
        this.imgpath.set(imgpath);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public Image getImage() {
        return image.get();
    }

    public SimpleObjectProperty<Image> imageProperty() {
        return image;
    }

    public void setImage(Image image) {
        this.image.set(image);
    }

    public void download(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpDownloadUtility downloader = new HttpDownloadUtility() {
                        @Override
                        public void onDownloadDone() {
                            super.onDownloadDone();
                            System.out.println("DOWNLOAD DONE");
//                            System.out.println(savedFile.getPath());
//                            if (savedFile.exists()){
//                                Core.getModuleManager().initialize(savedFile);
//                            }
                        }
                    };
                    downloader.downloadFileInThread(link.get() ,Core.getBaseUrl()+ File.separator+"modules");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
