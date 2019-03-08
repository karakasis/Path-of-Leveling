/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import poe.level.data.Controller;
import poe.level.data.Gem;
import poe.level.fx.Preferences_Controller;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class GemOverlay_Controller implements Initializable {

    class Delta { double x, y; }

    public void hookStage(Stage stage){

        final Delta dragDelta = new Delta();
        gem_overlay_container.setOnMousePressed(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
              if(!Controller.LOCK) {
                  // record a delta distance for the drag and drop operation.
                  dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                  dragDelta.y = stage.getY() - mouseEvent.getScreenY();
              }
          }
        });
        gem_overlay_container.setOnMouseDragged(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
              if(!Controller.LOCK) {
                  stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                  stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                  GemOverlay_Stage.prefX = mouseEvent.getScreenX() + dragDelta.x;
                  GemOverlay_Stage.prefY = mouseEvent.getScreenY() + dragDelta.y;
              }
          }
        });
        gem_overlay_container.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK) {
                    //update the prop file
                    Preferences_Controller.updateGemsPos(GemOverlay_Stage.prefX, GemOverlay_Stage.prefY);
                }
            }
        });
    }

    @FXML
    private VBox gem_overlay_container;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
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
        gem_overlay_container.getChildren().add(ap);

    }

    public void gemReplace(Gem add, Gem remove, Gem socketgroup){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/replace_gem_overlay_beta.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GemOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<Replace_gem_overlay_Controller>getController().load(add,remove,socketgroup);
        gem_overlay_container.getChildren().add(ap);
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
        gem_overlay_container.getChildren().add(ap);
    }

    public void reset(){
        gem_overlay_container.getChildren().clear();
    }

}
