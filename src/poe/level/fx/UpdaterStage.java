/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author Christos
 */
public class UpdaterStage extends Stage{
    
    
    POELevelFx parent;
    MainApp_Controller editor_controller;
    
    public UpdaterStage(){
        this.launcher();
        
        this.setOnCloseRequest(event -> {
            System.exit(50);
        });
    }
    
    public void launcher(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateFinished.fxml"));
        StackPane sp = null;
        try {
            sp = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(UpdaterStage.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scene scene = new Scene(sp);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        this.setScene(scene);
        
        this.show();
    }
    
}
    

