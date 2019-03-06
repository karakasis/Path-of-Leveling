/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import poe.level.data.Controller;
import poe.level.data.Zone;
import static poe.level.fx.POELevelFx.saveBuildsToMemory;
import poe.level.fx.Preferences_Controller;

/**
 *
 * @author Christos
 */
public class ZoneOverlay_Stage  extends Stage{
    
    private Controller parent;
    private boolean isVisible;
    public static double prefX;
    public static double prefY;
    private ZoneOverlay_Controller controller;
    
    public ZoneOverlay_Stage(){
        
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.TRANSPARENT);
        
        if(Preferences_Controller.zones_overlay_pos[0] == -200 
                && Preferences_Controller.zones_overlay_pos[1] == -200){
            
            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            primScreenBounds.getMinY();
            prefX = 200.0;
            prefY = primScreenBounds.getMinY();
            
            Preferences_Controller.updateZonesPos(prefX, prefY);
        }else{
            prefX = Preferences_Controller.zones_overlay_pos[0];
            prefY = Preferences_Controller.zones_overlay_pos[1];
        }
        
        this.setOnCloseRequest(event -> {
            System.out.println("Closing level:: ");
            if(saveBuildsToMemory()){
                System.out.println("Successfully saved checkpoint");
            }else{
                System.out.println("Checkpoint save failed");
            }
            System.exit(10);
        });
    }
    
    public void hookController(Controller c){
        parent = c;
        
    }



    public void queue(Zone currentZone){
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/ZoneOverlay.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ZoneOverlay_Stage.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller = loader.<ZoneOverlay_Controller>getController();
        controller.init(currentZone);
        Scene scene = new Scene(ap);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        bindKeyEvent(scene);
        this.setScene(scene);
        
        
        controller.hookStage(this);

        
        this.setX(prefX);
        //this.setY(prefY);
        this.setY(prefY-256.0);
        //this.setHeight(0);



        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {

                //return getHeight();
                return getY();
            }

            @Override
            public void setValue(Double value)
            {
                //setHeight(value);
                setY(value);
            }
        };


        Timeline slideIn = new Timeline();
       
        KeyValue kv = new KeyValue(writableWidth, 0d);
        KeyFrame kf_slideIn = new KeyFrame(Duration.millis(500), kv);
        
        if(Preferences_Controller.zones_toggle){
            KeyFrame kf_delay = new KeyFrame(Duration.millis(Preferences_Controller.zones_slider * 1000)); 
            slideIn.getKeyFrames().addAll(kf_slideIn,kf_delay);
            Timeline slideOut = new Timeline();
            KeyFrame kf_slideOut = new KeyFrame(Duration.millis(500), new KeyValue(writableWidth, -256d));
            slideOut.getKeyFrames().add(kf_slideOut);
            slideOut.setOnFinished(e -> Platform.runLater(() -> {System.out.println("Ending"); isVisible = false; this.hide();}));
            slideIn.setOnFinished(e -> Platform.runLater(() -> slideOut.play()));
        }else{
            slideIn.getKeyFrames().addAll(kf_slideIn);
        }

        //if(isVisible) hidePanel();
        this.show();
        slideIn.play();
        isVisible = true;
         
    }
    
    public void showPanel(){
        isVisible = true;
        this.show();
        this.setX(prefX);
        //this.setY(prefY);
        //this.setHeight(0);

        this.setY(prefY-256.0);



        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {

                //return getHeight();
                return getY();
            }

            @Override
            public void setValue(Double value)
            {
                //setHeight(value);
                setY(value);
            }
        };


        Timeline slideIn = new Timeline();
       
        KeyValue kv = new KeyValue(writableWidth, 0d);
        KeyFrame kf_slideIn = new KeyFrame(Duration.millis(500), kv);
        slideIn.getKeyFrames().addAll(kf_slideIn);
        slideIn.play();
    }
    
    public void hidePanel(){



        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {

                //return getHeight();
                return getY();
            }

            @Override
            public void setValue(Double value)
            {
                //setHeight(value);
                setY(value);
            }
        };
         
        Timeline timeline = new Timeline();
        KeyFrame endFrame = new KeyFrame(Duration.millis(500), new KeyValue(writableWidth, -256d));
        timeline.getKeyFrames().add(endFrame);
        timeline.setOnFinished(e -> Platform.runLater(() -> {System.out.println("Ending"); isVisible = false; this.hide();}));
        timeline.play();
    }
    
    public void reset(){
        if(!Preferences_Controller.zones_toggle || isVisible){
            hidePanel();
        }
    }
    
    public void event_show_hide(){
        if(isVisible){
            hidePanel();
        }else{
            showPanel();
        }
    }
    
    public void event_mark_recipe(){

        if(isVisible){

        }else{
            showPanel();

        }

        controller.playRecipeAnimation();
    }
    
    public void bindKeyEvent(Scene scene){
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (Preferences_Controller.zones_hotkey_show_hide_key.match(event)) { 
                    if(isVisible){
                        hidePanel();
                    }else{
                        showPanel();
                    }
                }
                
            }
        });
    }
    
}
