/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Preloader;
import javafx.application.Preloader.ProgressNotification;
import javafx.application.Preloader.StateChangeNotification;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Simple Preloader Using the ProgressBar Control
 *
 * @author Christos
 */
public class UpdatePreloader extends Preloader {
    
    ProgressBar bar;
    Stage stage;
    UpdaterController controller;
    
    private Scene createPreloaderScene() {
       bar = new ProgressBar();
        //BorderPane p = new BorderPane();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("updater.fxml"));
        AnchorPane p = null;
        try {
            p = loader.load();
            
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //at this point this class has done enough, and we need to contact with the 
        //appropriate controllers.
        controller = loader.<UpdaterController>getController();
        return new Scene(p);           
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(createPreloaderScene());        
        stage.show();
    }
    
    @Override
    public void handleStateChangeNotification(StateChangeNotification scn) {
        if (scn.getType() == StateChangeNotification.Type.BEFORE_START) {
            stage.hide();
        }
        POELevelFx application = (POELevelFx) scn.getApplication();
        if(application != null )
            controller.hookMain(application);
    }
    
    @Override
    public void handleProgressNotification(ProgressNotification pn) {
        //System.out.println(pn.getProgress());
        //controller.notify(pn.getProgress());
        //bar.setProgress(pn.getProgress());
    }    
    
    @Override
   public void handleApplicationNotification(PreloaderNotification arg0) {
          
          if (arg0 instanceof ProgressNotification) {
             ProgressNotification pn= (ProgressNotification) arg0;
                controller.notify(pn.getProgress());
          }
    }   
    
}
