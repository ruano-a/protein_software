package Modules.FindReplace;

import Items.Module;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

/**
 * Classe permettant de ...
 *
 * @author Paul Lacazedieu
 * @version 0.1
 */
public class FindReplaceModule extends Module {

    private AnchorPane layout;
    private FindReplaceController findReplaceController;

    public FindReplaceModule() {
        super("Find&Replace");
        this.layout = new AnchorPane();
        this.setIcon(new Image(getClass().getResource("/Modules/FindReplace/test.png").toString()));
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Modules/FindReplace/FindReplace.fxml"));
            Node findReplaceView = loader.load();
            AnchorPane.setBottomAnchor(findReplaceView, 0.0);
            AnchorPane.setTopAnchor(findReplaceView, 0.0);
            AnchorPane.setRightAnchor(findReplaceView, 0.0);
            AnchorPane.setLeftAnchor(findReplaceView, 0.0);
            this.findReplaceController = loader.getController();
            this.findReplaceController.setView(findReplaceView);
            this.layout.getChildren().add(this.findReplaceController.getView());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public Node getLayout() {
        return this.layout;
    }
}
