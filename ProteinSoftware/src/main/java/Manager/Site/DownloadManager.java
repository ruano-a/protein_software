package Manager.Site;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by ruano_a on 04/09/2016.
 */
public class DownloadManager {
    private int _bytes;
    private ProgressBar progressBar;
    private boolean stopOrdered;
    private Stage stage;
    private Label errorLabel;
    private Button stopButton;
    private int _actualBytes;

    private void buildStage(){
        BorderPane layout = new BorderPane();
        errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        progressBar = new ProgressBar();
        progressBar.setMaxSize(Double.MAX_VALUE, 30);
        progressBar.setProgress(0);
        stopButton = new Button("Stop");
        stopButton.setOnAction(event ->
        {
            stopOrdered = true;
            System.out.println("stop asked : " + _actualBytes + '/' + _bytes);
            stage.hide();
        });
        layout.setTop(errorLabel);
        layout.setCenter(progressBar);
        HBox hbox = new HBox();
        hbox.getChildren().add(stopButton);
        layout.setBottom(hbox);
        layout.setPadding(new Insets(20));
        // SET PARENT WINDOWS !
        stage = new Stage();
        stage.setScene(new Scene(layout, 300, 150));
        stage.setTitle("Downloading file...");
        stage.setOnCloseRequest(event -> stopOrdered = true);
    }
    public DownloadManager(int bytesTotal){
        _bytes = bytesTotal;
        stopOrdered = false;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                buildStage();
                stage.show();
            }
        });
    }
    public void update(int actualBytes){
        _actualBytes = actualBytes; // stored just for the print message in case of stop.
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                float percent = actualBytes;
                percent /= _bytes;
                progressBar.setProgress(percent);
                System.out.println(percent * 100 + "%");

            }
        });
    }
    public boolean stopOrdered(){
        return (stopOrdered);
    }
    public void downloadError(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                errorLabel.setText("There was en error during the download");
                stopButton.setText("Close");
                stopButton.setOnAction(event -> stage.hide());
            }
        });
    }
    public void finishedDownload(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stage.hide();
            }
        });
    }
}