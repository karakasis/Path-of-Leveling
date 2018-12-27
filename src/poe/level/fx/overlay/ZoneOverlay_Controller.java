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
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import poe.level.data.Zone;

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
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
          }
        });
        container.setOnMouseDragged(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
            ZoneOverlay_Stage.prefX = mouseEvent.getScreenX() + dragDelta.x;
            ZoneOverlay_Stage.prefY = mouseEvent.getScreenY() + dragDelta.y;
          }
        });
    }
    
    public void init(Zone zone){
        
        BufferedImage img = null;
        if(zone.getImages().get(0).equals("none")){
            cacheLabelAlt.setText(zone.altImage());
            container.getChildren().add(cacheLabelAlt);
        }else{
            for(String s : zone.getImages()){
                    try {
                        //img = ImageIO.read(getClass().getResource("/zones/"+zone.getActName()+" - Overlay/"+s+".png"));
                        img = ImageIO.read(getClass().getResource("/zones/"+zone.getActName()+"/"+s+".png"));
                        Image iv = SwingFXUtils.toFXImage(img, null);
                        ImageView iv_res = new ImageView(iv);
                        iv_res.setFitWidth(256);
                        iv_res.setFitHeight(144);
                        container.getChildren().add(iv_res);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        
        cacheLabel.setText(zone.getZoneNote());
        container.getChildren().add(cacheLabel);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        cacheLabel = (Label) container.getChildren().get(1);
        cacheLabelAlt = (Label) container.getChildren().get(0);
        container.getChildren().clear();
    }    
    
}
