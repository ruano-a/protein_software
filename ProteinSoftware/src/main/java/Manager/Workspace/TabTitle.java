package Manager.Workspace;

import Extractor.SVGExtractor;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import org.json.JSONObject;

/**
 * Created by paull on 27/03/2016.
 */
public class TabTitle extends HBox{
    private Button          text;
    private Button          closeBtn;
    private WorkspaceTab    tab;

    public TabTitle(WorkspaceTab tab){
        if (tab.getTitle() == null){
            tab.setTitle("untitled");
        }
        this.text = new Button(tab.getTitle());
        this.tab = tab;
        this.closeBtn = new Button(null, SVGExtractor.GetSVGPath("/Icons/close.svg"));
        this.closeBtn.setMnemonicParsing(false);

        this.closeBtn.setOnAction(event -> {});

        this.closeBtn.setPrefWidth(30);

        this.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY){
                    tab.getTabLister().getWorkspace().setActiveTab(tab);
                }
                else if (event.getButton() == MouseButton.MIDDLE){
                    tab.getTabLister().getWorkspace().removeTab(tab);
                }
            }
        });
        this.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getStyleClass().add("hover");
                if (tab.isClosable()){
                    showCloseButton(true);
                }
            }
        });
        this.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                getStyleClass().remove("hover");
                if (tab.isClosable()) {
                    showCloseButton(false);
                }
            }
        });

        this.closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tab.getTabLister().getWorkspace().removeTab(tab);
            }
        });

        this.getStyleClass().add("tab-title");
        this.text.getStyleClass().remove("button");
        this.text.getStyleClass().add("text");
        this.closeBtn.getStyleClass().add("close");

        this.getChildren().addAll(text, closeBtn);
        this.text.setMouseTransparent(true);


        this.text.setPrefWidth(Integer.MAX_VALUE);
        this.text.setMaxWidth(Integer.MAX_VALUE);

        showCloseButton(false);
    }

    public void showCloseButton(boolean s){
        if (s){
            /*
            closeBtn.setDisable(false);
            closeBtn.setVisible(true);
            */
            this.getChildren().add(closeBtn);
        }
        else{
            /*
            closeBtn.setDisable(true);
            closeBtn.setVisible(false);
            */
            this.getChildren().remove(closeBtn);
        }
    }

    public void setText(String s){
        this.text.setText(s);
    }

    public WorkspaceTab getTab(){
        return tab;
    }

    public void showIsModified(boolean v){
        if (v){
            this.text.setText(((tab.getTitle() != null) ? tab.getTitle() : "untitled") + "~");
        } else {
            this.text.setText(((tab.getTitle() != null) ? tab.getTitle() : "untitled"));
        }
    }
}
