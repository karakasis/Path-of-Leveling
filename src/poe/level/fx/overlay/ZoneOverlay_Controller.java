/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javax.imageio.ImageIO;

import javafx.util.Duration;
import poe.level.data.Controller;
import poe.level.data.Zone;
import poe.level.fx.Preferences_Controller;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class ZoneOverlay_Controller implements Initializable {

    class Delta { double x, y; }

    @FXML
    private AnchorPane root;
    @FXML
    private HBox container;
    @FXML
    private ImageView passive_book;
    @FXML
    private ImageView trial;
    @FXML
    private ImageView recipe;
    @FXML
    private Label recipeMarkedLabel;

    private double initialX;
    private double initialY;
    private Label cacheLabel;
    private Label cacheLabelAlt;
    /**
     * Initializes the controller class.
     */

    public void hookStage(Stage stage){

        final Delta dragDelta = new Delta();
        container.setOnMousePressed(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            // record a delta distance for the drag and drop operation.
              if(!Controller.LOCK && mouseEvent.isPrimaryButtonDown()){
                  Controller.PRESS = true;
                  dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                  dragDelta.y = stage.getY() - mouseEvent.getScreenY();
              }
          }
        });
        container.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK && mouseEvent.isPrimaryButtonDown()) {
                    stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                    stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                    ZoneOverlay_Stage.prefX = mouseEvent.getScreenX() + dragDelta.x;
                    ZoneOverlay_Stage.prefY = mouseEvent.getScreenY() + dragDelta.y;
                }
            }
        });
        container.setOnMouseReleased(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK && Controller.PRESS) {
                    Controller.PRESS = false;
                    Preferences_Controller.updateZonesPos(ZoneOverlay_Stage.prefX, ZoneOverlay_Stage.prefY);
                }
          }
        });
    }

    public void playTown(){
        trial.setVisible(false);
        recipe.setVisible(false);
        passive_book.setVisible(false);
        container.getChildren().clear();
        cacheLabel.setText("Zone Unidentified");
        container.getChildren().add(cacheLabel);
    }

    public void setOpacity(double value){
        root.setOpacity(value);
    }

    public double getOpacity(){
        return root.getOpacity();
    }

    public void init(Zone zone){

        BufferedImage img = null;
        if(Preferences_Controller.zones_trial_toggle){
            if(zone.hasTrial){
                trial.setVisible(true);
            }else{
                trial.setVisible(false);
            }
        }else{
            trial.setVisible(false);
        }
        if(Preferences_Controller.zones_recipe_toggle){
            if(zone.hasRecipe){
                recipe.setVisible(true);
            }else{
                recipe.setVisible(false);
            }
        }else{
            recipe.setVisible(false);
        }
        if(Preferences_Controller.zones_passive_toggle){
            if(zone.hasPassive){
                passive_book.setVisible(true);
            }else{
                passive_book.setVisible(false);
            }
        }else{
            passive_book.setVisible(false);
        }
        if(Preferences_Controller.zones_images_toggle){
            if(zone.getImages().get(0).equals("none")){
                cacheLabelAlt.setPadding(new Insets(0,10,0,10));
                cacheLabelAlt.setText(zone.altImage());
                container.getChildren().add(cacheLabelAlt);
            }else{
                for(String s : zone.getImages()){
                        try {
                            //img = ImageIO.read(getClass().getResource("/zones/"+zone.getActName()+" - Overlay/"+s+".png"));
                            img = ImageIO.read(getClass().getResource("/zones/"+zone.getActName()+"/"+s+".png"));
                            //System.out.println("Loaded image.");
                            Image iv = SwingFXUtils.toFXImage(img, null);
                            ImageView iv_res = new ImageView(iv);
                            iv_res.setFitWidth(256);
                            iv_res.setFitHeight(144);
                            container.getChildren().add(iv_res);
                        } catch (IOException e) {
                            System.err.println("IOException when reading zone : " +zone.name);
                            e.printStackTrace();
                        }
                }
            }
        }
        if(Preferences_Controller.zones_text_toggle){
            cacheLabel.setPadding(new Insets(0,10,0,10));
            cacheLabel.setText(zone.getZoneNote());
            container.getChildren().add(cacheLabel);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        //root.setFocusTraversable(true);
        cacheLabel = (Label) container.getChildren().get(1);
        cacheLabelAlt = (Label) container.getChildren().get(0);
        container.getChildren().clear();
    }

    public void playRecipeAnimation(){
        recipeMarkedLabel.setVisible(true);

        Timeline slideIn = new Timeline();

        KeyFrame kf_slideIn = new KeyFrame(Duration.millis(5000));
        slideIn.getKeyFrames().addAll(kf_slideIn);
        slideIn.setOnFinished(e -> Platform.runLater(() -> recipeMarkedLabel.setVisible(false)));
        slideIn.play();
    }

}
