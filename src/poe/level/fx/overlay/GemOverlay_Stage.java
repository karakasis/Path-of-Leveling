/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import poe.level.data.Build;
import poe.level.data.Gem;
import poe.level.data.SocketGroup;
import static poe.level.fx.POELevelFx.saveBuildsToMemory;
import poe.level.fx.Preferences_Controller;
import static poe.level.fx.overlay.ZoneOverlay_Stage.prefX;
import static poe.level.fx.overlay.ZoneOverlay_Stage.prefY;

/**
 *
 * @author Christos
 */
public class GemOverlay_Stage extends Stage{
    
    ArrayList<Integer> level_list;
    ArrayList<Gem> gemsOnThisLevel_local;
    HashSet<Integer> totalLevels;
    HashMap<Integer,ArrayList<Gem>> gemsOnLevelsMap;
    HashMap<Gem,SocketGroup> gemToSocket_map;
    GemOverlay_Controller controller;
    
    
    public static double prefX;
    public static double prefY;
    
    private boolean isPlaying;
    
    public GemOverlay_Stage(Build build){
        
        generateLevels(build);
        
        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.TRANSPARENT);
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        //double screenRightEdge = primScreenBounds.getMaxX() ;
        //if the settings are on default load the optimized settings based on screen measurements
        if(Preferences_Controller.gem_overlay_pos[0] == -200 
                && Preferences_Controller.gem_overlay_pos[1] == -200){
            
            double screenRightEdge = primScreenBounds.getMinX();
            prefX = screenRightEdge;
            prefY = primScreenBounds.getMinY();
            
            Preferences_Controller.updateGemsPos(prefX, prefY);
        }else{
            prefX = Preferences_Controller.gem_overlay_pos[0];
            prefY = Preferences_Controller.gem_overlay_pos[1];
        }
        gemsOnThisLevel_local = new ArrayList<>();
        loadFXML();
        this.setOnCloseRequest(event -> {
            System.out.println("Closing gem:: ");
            if(saveBuildsToMemory()){
                System.out.println("Successfully saved checkpoint");
            }else{
                System.out.println("Checkpoint save failed");
            }
            System.exit(11);
        });
    }
    
    public void generateLevels(Build build){
        for(SocketGroup sg : build.getSocketGroup()){
            if(sg.replaceGroup()){
                for(SocketGroup sg2 : build.getSocketGroup()){
                    if(sg.getGroupReplaced().equals(sg2)){
                        sg2.setReplacesGroup(true);
                        sg2.setGroupThatReplaces(sg);
                        break;
                    }
                }
            }
            for(Gem g : sg.getGems()){
                if(g.replaced){
                    for(Gem g2 : sg.getGems()){
                        if(g.replacedWith.equals(g2)){
                            g2.replaces = true;
                            g2.replacesGem = g;
                            break;
                        }
                    }
                }
            }
        }
        
        totalLevels = new HashSet<>();
        gemsOnLevelsMap = new HashMap<>();
        gemToSocket_map = new HashMap<>();
        //calcute the baseline
        ArrayList<SocketGroup> sorted_sg = new ArrayList<>();
        for(SocketGroup sg : build.getSocketGroup()){
            sorted_sg.add(sg);
        }
        sorted_sg.sort(new Comparator<SocketGroup>() {
		@Override
		public int compare(SocketGroup o1, SocketGroup o2) {
			return o1.getActiveGem().getLevelAdded() - o2.getActiveGem().getLevelAdded();
		}

	});
        
        //here we add each gem to according level it gets added.
        for(SocketGroup sg : sorted_sg){
            for(Gem g : sg.getGems()){
                totalLevels.add(g.level_added);
                if(gemsOnLevelsMap.containsKey(g.level_added)){
                    gemsOnLevelsMap.get(g.level_added).add(g);
                }else{
                    ArrayList<Gem> gemsOnThisLevel_local = new ArrayList<>();
                    gemsOnThisLevel_local.add(g);
                    gemsOnLevelsMap.put(g.level_added, gemsOnThisLevel_local);
                }
                gemToSocket_map.put(g, sg);
            }
        }

        
        //int totalLevelPoints = totalLevels.size();
        //int baselineSize = totalLevelPoints*100;
        
        //now we need to implement the visual offset for each level.
        level_list = new ArrayList<>();
        level_list.addAll(totalLevels);
        Collections.sort(level_list);
        
        
    }
    
    public void loadFXML(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/GemOverlay.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GemOverlay_Stage.class.getName()).log(Level.SEVERE, null, ex);
        }
        controller = loader.<GemOverlay_Controller>getController();
        
        Scene scene = new Scene(ap);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);


        this.setScene(scene);
        controller.hookStage(this);
        
        this.show();
    }
    
    public void animate(){
        
        
        Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
        //double screenRightEdge = primScreenBounds.getMaxX() ;
        double screenRightEdge = primScreenBounds.getMinX();
        this.setX(prefX);
        this.setY(prefY);
        this.setWidth(0);
        this.setHeight(primScreenBounds.getHeight());


        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return getWidth();
            }

            @Override
            public void setValue(Double value) {
                //setX(screenRightEdge + value);
                setWidth(value);
            }
        };

        Timeline slideIn = new Timeline();
       
        KeyValue kv = new KeyValue(writableWidth, 322d);
        KeyFrame kf_slideIn = new KeyFrame(Duration.millis(500), kv);
        KeyFrame kf_delay = new KeyFrame(Duration.millis(Preferences_Controller.level_slider * 1000)); 
        slideIn.getKeyFrames().addAll(kf_slideIn,kf_delay);

        Timeline slideOut = new Timeline();
        KeyFrame kf_slideOut = new KeyFrame(Duration.millis(500), new KeyValue(writableWidth, 20.0));
        slideOut.getKeyFrames().add(kf_slideOut);
        
        slideOut.setOnFinished(e -> Platform.runLater(() -> {System.out.println("Ending");isPlaying = false; this.hide();}));
        slideIn.setOnFinished(e -> Platform.runLater(() -> slideOut.play()));
        
        slideIn.play();
        isPlaying = true;
        this.show();
    }
    
    public void update(int level){
        if(level_list.contains(level) && !isPlaying){
            controller.reset();
            gemsOnThisLevel_local.clear();
            gemsOnThisLevel_local = new ArrayList<>(gemsOnLevelsMap.get(level)); //very important or it will get deleted in the map
            for(Gem g : gemsOnThisLevel_local){
                //THE REPLACE IS HERE for socket group
                if(gemToSocket_map.get(g).getActiveGem().equals(g) && gemToSocket_map.get(g).replacesGroup()){
                    //IF THIS GEM IS AN ACTIVE GEM AND ITS SOCKET GROUP REPLACES ANOTHER ONE
                    Gem add_this = g;
                    Gem remove_this = gemToSocket_map.get(g).getGroupThatReplaces().getActiveGem();
                    controller.socketGroupReplace(add_this,remove_this);
                    
                }
                if(g.replaces){
                    Gem add_this = g;
                    Gem remove_this = g.replacesGem;
                    controller.gemReplace(add_this,remove_this,gemToSocket_map.get(g).getActiveGem());
                    //IF THIS GEM replaces another gem
                }else{
                    controller.addGem(g,gemToSocket_map.get(g).getActiveGem());
                }
            }
            
            animate();
        }else if(!level_list.contains(level)){
            System.err.println("No gems available in this level : "+ level);
        }else if(isPlaying){
            System.err.println("A level animation is currently playing");
        }
    }
    
    public void event_remind(){
        if(!isPlaying){
            System.err.println("Reminding gems.");
            controller.reset();
            for(Gem g : gemsOnThisLevel_local){
                //THE REPLACE IS HERE for socket group
                if(gemToSocket_map.get(g).getActiveGem().equals(g) && gemToSocket_map.get(g).replacesGroup()){
                    //IF THIS GEM IS AN ACTIVE GEM AND ITS SOCKET GROUP REPLACES ANOTHER ONE
                    Gem add_this = g;
                    Gem remove_this = gemToSocket_map.get(g).getGroupThatReplaces().getActiveGem();
                    controller.socketGroupReplace(add_this,remove_this);
                    
                }
                if(g.replaces){
                    Gem add_this = g;
                    Gem remove_this = g.replacesGem;
                    controller.gemReplace(add_this,remove_this,gemToSocket_map.get(g).getActiveGem());
                    //IF THIS GEM replaces another gem
                }else{
                    controller.addGem(g,gemToSocket_map.get(g).getActiveGem());
                }
            }
            
            animate();
        }else{
            System.err.println("Cant remind animation is still playing.");
        }
    }
    
}
