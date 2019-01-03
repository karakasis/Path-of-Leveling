/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXDialog;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import poe.level.data.Build;
import poe.level.fx.overlay.ZoneOverlay_Controller;
import poe.level.fx.overlay.ZoneOverlay_Stage;

/**
 *
 * @author Christos
 */
public class Main_Stage extends Stage{
    
    Main_Controller controller;
    POELevelFx parent;
    public static Build buildLoaded;
    public static int playerLevel;
    public static String characterName;
    
    public Main_Stage(POELevelFx parent){
        this.parent = parent;
        this.launcher();
    }
 
    public void launcher(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
        StackPane sp = null;
        try {
            sp = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main_Stage.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller = loader.<Main_Controller>getController();
        
        Scene scene = new Scene(sp);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);


        this.setScene(scene);
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.TRANSPARENT);
        
        controller.hookStage(this);
        
        this.show();
    }
    
    public void editor(){
        parent.editor();
    }
    
    public void start(boolean zone, boolean xp, boolean level){
        parent.start(zone,xp,level);
    }
    
    public void closeApp(){
        System.exit(30);
        //parent.close();
    }

}
