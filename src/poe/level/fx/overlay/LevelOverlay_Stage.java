/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import poe.level.fx.Main_Stage;
import static poe.level.fx.POELevelFx.saveBuildsToMemory;
import poe.level.fx.Preferences_Controller;
import static poe.level.fx.overlay.ZoneOverlay_Stage.prefX;
import static poe.level.fx.overlay.ZoneOverlay_Stage.prefY;

/**
 *
 * @author Christos
 */
public class LevelOverlay_Stage extends Stage{
    
    private LevelOverlay_Controller controller;
    public static double prefX;
    public static double prefY;
    
    public LevelOverlay_Stage(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/LevelOverlay.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ZoneOverlay_Stage.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller = loader.<LevelOverlay_Controller>getController();
        
        Scene scene = new Scene(ap);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        this.setOnCloseRequest(event -> {
            System.out.println("Closing level:: ");
            if(saveBuildsToMemory()){
                System.out.println("Successfully saved checkpoint");
            }else{
                System.out.println("Checkpoint save failed");
            }
            System.exit(10);
        });
        
        this.setScene(scene);
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.TRANSPARENT);
        
        if(Preferences_Controller.level_overlay_pos[0] == -200 
                && Preferences_Controller.level_overlay_pos[1] == -200){
            
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            prefX = primScreenBounds.getWidth()/2 - 736/2;
            prefY = primScreenBounds.getMaxY() - 100;
            
            Preferences_Controller.updateLevelPos(prefX, prefY);
        }else{
            prefX = Preferences_Controller.level_overlay_pos[0];
            prefY = Preferences_Controller.level_overlay_pos[1];
        }
        
        this.setX(prefX);
        this.setY(prefY);
        
        controller.hookStage(this);
        controller.setPlayerLevel(Main_Stage.playerLevel);
        this.show();
    }
    
    public void update(int playerLvl, int zoneLvl){
        controller.update(playerLvl,zoneLvl);
    }
    
    public void reset(int playerLvl){
        controller.reset(playerLvl);
    }
}
