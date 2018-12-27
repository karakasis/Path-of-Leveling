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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import poe.level.data.Controller;

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
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
          }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
          }
        });
    }
    
    public void update(int playerLvl, int zoneLvl){
        int safe = Controller.findSafe(playerLvl)[2];
        if(playerLvl + safe < zoneLvl || playerLvl - safe > zoneLvl){
            player.setStyle("-fx-border-color : red; -fx-text-fill: red;");
            zone.setStyle("-fx-border-color : red; -fx-text-fill: red;");
            safezone.setStyle("-fx-border-color : red; -fx-text-fill: red;");
            xpmulti.setStyle("-fx-border-color : red; -fx-text-fill: red;");
        }else{
            player.setStyle("-fx-border-color : white; -fx-text-fill: white;");
            zone.setStyle("-fx-border-color : white; -fx-text-fill: white;");
            safezone.setStyle("-fx-border-color : white; -fx-text-fill: white;");
            xpmulti.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        }
        player.setText("Current Level: "+ playerLvl);
        zone.setText("Monster Level: "+ zoneLvl);
        safezone.setText("Safezone: -+" + Controller.findSafe(playerLvl)[2]);
        xpmulti.setText("XP Multiplier: "+ (int)Controller.findxpmulti(playerLvl, zoneLvl)+"%");
    }
    
    public void reset(int playerLvl){
        player.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        zone.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        safezone.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        xpmulti.setStyle("-fx-border-color : white; -fx-text-fill: white;");
        player.setText("Current LVL : "+ playerLvl);
        zone.setText("Monster LVL : - ");
        safezone.setText("Safezone : -");
        xpmulti.setText("XP Multiplier : -");
    }
    
    public void setPlayerLevel(int level){
        player.setText("Current Level: "+ level);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        root.setFocusTraversable(true);
        
    }    
    
}
