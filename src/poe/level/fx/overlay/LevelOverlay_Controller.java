/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import poe.level.data.Controller;
import poe.level.fx.Preferences_Controller;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class LevelOverlay_Controller implements Initializable {

    class Delta { double x, y; }

    @FXML
    private AnchorPane root;
    @FXML
    private Label player;
    @FXML
    private Label zone;
    @FXML
    private Label safezone;
    @FXML
    private Label xpmulti;

    /**
     * Initializes the controller class.
     */

    public void hookStage(Stage stage){

        final LevelOverlay_Controller.Delta dragDelta = new LevelOverlay_Controller.Delta();
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            // record a delta distance for the drag and drop operation.
              if(!Controller.LOCK) {
                  dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                  dragDelta.y = stage.getY() - mouseEvent.getScreenY();
              }
          }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
              if(!Controller.LOCK) {
                  stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                  stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                  LevelOverlay_Stage.prefX = mouseEvent.getScreenX() + dragDelta.x;
                  LevelOverlay_Stage.prefY = mouseEvent.getScreenY() + dragDelta.y;
              }
          }
        });
        root.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK) {
                    Preferences_Controller.updateLevelPos(LevelOverlay_Stage.prefX, LevelOverlay_Stage.prefY);
                }
            }
        });
    }

    public void update(int playerLvl, int zoneLvl){
        int safe = Controller.findSafe(playerLvl)[2];
        if(playerLvl + safe < zoneLvl || playerLvl - safe > zoneLvl){
            //player.setStyle("-fx-border-color : red; -fx-text-fill: red;");
            //zone.setStyle("-fx-border-color : red; -fx-text-fill: red;");
            //safezone.setStyle("-fx-border-color : red; -fx-text-fill: red;");
            //xpmulti.setStyle("-fx-border-color : red; -fx-text-fill: red;");
        }else{
            //player.setStyle("-fx-border-color : white; -fx-text-fill: white;");
            //zone.setStyle("-fx-border-color : white; -fx-text-fill: white;");
            //safezone.setStyle("-fx-border-color : white; -fx-text-fill: white;");
            //xpmulti.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        }
        player.setText(playerLvl + "");
        zone.setText(zoneLvl + "");
        safezone.setText("-+" + Controller.findSafe(playerLvl)[2]) ;
        xpmulti.setText((int)Controller.findxpmulti(playerLvl, zoneLvl)+"");
    }

    public void reset(int playerLvl){
        //player.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        //zone.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        //safezone.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        //xpmulti.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        player.setText(playerLvl + "");
        zone.setText(" - ");
        safezone.setText(" - ");
        xpmulti.setText(" - ");
    }

    public void setPlayerLevel(int level){
        player.setText(level+"");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        root.setFocusTraversable(true);
        Tooltip level_t = new Tooltip("Indicates your current level");
        level_t.setStyle("-fx-font-size: 16");
        player.setTooltip(level_t);
        Tooltip zone_t = new Tooltip("Indicates the area monster level");
        zone_t.setStyle("-fx-font-size: 16");
        zone.setTooltip(zone_t);

        Tooltip safe_t = new Tooltip("Your character level must be within these range to get maximum experience");
        safe_t.setStyle("-fx-font-size: 16");
        safezone.setTooltip(safe_t);
        Tooltip xp_t = new Tooltip("The percentage of experience your character is gaining in this area");
        xp_t.setStyle("-fx-font-size: 16");
        xpmulti.setTooltip(xp_t);
    }

}
