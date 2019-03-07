package poe.level.fx.overlay;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import poe.level.data.Controller;
import poe.level.data.Gem;
import poe.level.fx.Preferences_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GemOverlayBeta_Controller implements Initializable {

    class Delta { double x, y; }

    public void hookStage(Stage stage){

        final Delta dragDelta = new Delta();
        rootSwapPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK) {
                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                    dragDelta.y = stage.getY() - mouseEvent.getScreenY();
                }
            }
        });
        rootSwapPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK) {
                    stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                    stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                    GemOverlay_Stage.prefX = mouseEvent.getScreenX() + dragDelta.x;
                    GemOverlay_Stage.prefY = mouseEvent.getScreenY() + dragDelta.y;
                }
            }
        });
        rootSwapPane.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK) {
                    //update the prop file
                    Preferences_Controller.updateGemsPos(GemOverlay_Stage.prefX, GemOverlay_Stage.prefY);
                }
            }
        });
    }

    @FXML
    private StackPane rootSwapPane;
    private int activePanel;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void initPanel(){
        for(Node n : rootSwapPane.getChildren()) n.setVisible(false);
        rootSwapPane.getChildren().get(0).setVisible(true);
        activePanel = 0;
    }

    public void slide(int slideDirection){
        //0 is left 1 is right
        System.err.println("Before : "+(activePanel+1)+"/"+rootSwapPane.getChildren().size());
        if(slideDirection == 0){
            System.err.println("Sliding to left..");
            if(activePanel - 1 >= 0){
                rootSwapPane.getChildren().get(activePanel).setVisible(false);
                rootSwapPane.getChildren().get(--activePanel).setVisible(true);
                System.err.println("Displaying : "+(activePanel+1)+"/"+rootSwapPane.getChildren().size());
            }
        }else if(slideDirection == 1){
            System.err.println("Sliding to right..");
            if(activePanel + 1 < rootSwapPane.getChildren().size()){
                rootSwapPane.getChildren().get(activePanel).setVisible(false);
                activePanel++;
                rootSwapPane.getChildren().get(activePanel).setVisible(true);
                System.err.println("Displaying : "+(activePanel+1)+"/"+rootSwapPane.getChildren().size());
            }
        }
    }

    public void socketGroupReplace(Gem add, Gem remove){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/replace_socket_group_overlay.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GemOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<Replace_socket_group_overlay_Controller>getController().load(add,remove);
        Group group = new Group();
        group.getChildren().add(ap);
        rootSwapPane.getChildren().add(group);

    }

    public void gemReplace(Gem add, Gem remove, Gem socketgroup){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/replace_gem_overlay.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GemOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<Replace_gem_overlay_Controller>getController().load(add,remove,socketgroup);
        Group group = new Group();
        group.getChildren().add(ap);
        rootSwapPane.getChildren().add(group);
    }

    public void addGem(Gem add, Gem socketgroup){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/add_gem_overlay.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GemOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<Add_gem_overlay_Controller>getController().load(add,socketgroup);
        Group group = new Group();
        group.getChildren().add(ap);
        rootSwapPane.getChildren().add(group);
    }

    public void reset(){
        rootSwapPane.getChildren().clear();
    }

}