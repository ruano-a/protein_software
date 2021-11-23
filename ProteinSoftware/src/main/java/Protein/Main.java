package Protein;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    //TODO Refonte des css

    @Override
    public void start(Stage primaryStage) throws Exception{
        Core core = new Core(primaryStage);

        core.init();
        core.start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
