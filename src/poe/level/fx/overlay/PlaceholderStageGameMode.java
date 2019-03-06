/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import poe.level.data.Controller;
import poe.level.fx.Preferences_Controller;

/**
 *
 * @author Christos
 */
public class PlaceholderStageGameMode extends Stage{

    private Preferences_Controller controller;
    private Controller parent_gameModeOn;

    public PlaceholderStageGameMode(Controller parent){
        this.parent_gameModeOn = parent;
    }

    public void loadSettings(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/preferences.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ZoneOverlay_Stage.class.getName()).log(Level.SEVERE, null, ex);
        }

        Scene scene = new Scene(ap);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        this.setScene(scene);
        //this.setAlwaysOnTop(true);
        this.setOnCloseRequest(event -> {
            this.hide();
        });

        this.show();
    }

    public void loadRecipes(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/RecipeOverlay.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ZoneOverlay_Stage.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<RecipeOverlay_Controller>getController().hookGameModeOn(parent_gameModeOn);

        Scene scene = new Scene(ap);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());

        this.setScene(scene);
        //this.setAlwaysOnTop(true);
        this.setOnCloseRequest(event -> {
            RecipeOverlay_Controller.gameModeOn = false;
            this.close();
        });

        this.show();
    }
    
}
