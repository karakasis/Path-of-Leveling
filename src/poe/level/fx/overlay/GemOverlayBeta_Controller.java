package poe.level.fx.overlay;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import poe.level.data.Controller;
import poe.level.data.Gem;
import poe.level.fx.Preferences_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GemOverlayBeta_Controller implements Initializable {

    class Delta { double x, y; }

    public void hookStage(Stage stage){

        final Delta dragDelta = new Delta();
        rootDrag.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK && mouseEvent.isPrimaryButtonDown()) {
                    Controller.PRESS = true;
                    // record a delta distance for the drag and drop operation.
                    dragDelta.x = stage.getX() - mouseEvent.getScreenX();
                    dragDelta.y = stage.getY() - mouseEvent.getScreenY();
                }
            }
        });
        rootDrag.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK && mouseEvent.isPrimaryButtonDown()) {
                    stage.setX(mouseEvent.getScreenX() + dragDelta.x);
                    stage.setY(mouseEvent.getScreenY() + dragDelta.y);
                    GemOverlay_Stage.prefX = mouseEvent.getScreenX() + dragDelta.x;
                    GemOverlay_Stage.prefY = mouseEvent.getScreenY() + dragDelta.y;
                }
            }
        });
        rootDrag.setOnMouseReleased(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent mouseEvent) {
                if(!Controller.LOCK &&  Controller.PRESS) {
                    //update the prop file
                    Controller.PRESS = false;
                    Preferences_Controller.updateGemsPos(GemOverlay_Stage.prefX, GemOverlay_Stage.prefY);
                }
            }
        });
    }

    @FXML
    private AnchorPane rootDrag;
    @FXML
    private StackPane rootSwapPane;
    private int activePanel;
    private int maxPages;
    @FXML
    private Label mainGem;
    @FXML
    private Label pagination;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        titles = new ArrayList<>();
        rootDrag.setFocusTraversable(true);
    }

    public void hide(){
        rootSwapPane.setVisible(false);
    }

    public void show(){
        rootSwapPane.setVisible(true);
    }

    public void initPanel(){
        for(Node n : rootSwapPane.getChildren()) n.setVisible(false);
        rootSwapPane.getChildren().get(0).setVisible(true);
        activePanel = 0;
        maxPages = rootSwapPane.getChildren().size();
        pagination.setText(activePanel+1 + "/" + maxPages);
        mainGem.setText(titles.get(activePanel));
    }

    public void slide(int slideDirection){
        //0 is left 1 is right
        if(rootSwapPane.isVisible()){
            //System.err.println("Before : "+(activePanel+1)+"/"+maxPages);
            if(slideDirection == 0){
                //System.err.println("Sliding to left..");
                if(activePanel - 1 >= 0){
                    rootSwapPane.getChildren().get(activePanel).setVisible(false);
                    rootSwapPane.getChildren().get(--activePanel).setVisible(true);
                    //System.err.println("Displaying : "+(activePanel+1)+"/"+maxPages);
                }
            }else if(slideDirection == 1){
                //System.err.println("Sliding to right..");
                if(activePanel + 1 < maxPages){
                    rootSwapPane.getChildren().get(activePanel).setVisible(false);
                    activePanel++;
                    rootSwapPane.getChildren().get(activePanel).setVisible(true);
                    //System.err.println("Displaying : "+(activePanel+1)+"/"+maxPages);
                }
            }
            pagination.setText(activePanel+1 + "/" + maxPages);
            mainGem.setText(titles.get(activePanel));
        }

    }

    public void socketGroupReplace(Gem add, Gem remove){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/replace_socket_group_overlay_beta.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GemOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<Replace_socket_group_overlay_Controller>getController().load(add,remove);
        Group group = new Group();
        group.getChildren().add(ap);
        rootSwapPane.getChildren().add(group);
        titles.add("Replace Socket Groups");

    }

    public void gemReplace(Gem add, Gem remove, Gem socketgroup){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/replace_gem_overlay_beta.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GemOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<Replace_gem_overlay_Controller>getController().load(add,remove,socketgroup);
        Group group = new Group();
        group.getChildren().add(ap);
        rootSwapPane.getChildren().add(group);
        titles.add(socketgroup.getGemName());
    }

    public void addGem(Gem add, Gem socketgroup){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/add_gem_overlay_beta.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(GemOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<Add_gem_overlay_Controller>getController().load(add,socketgroup);
        Group group = new Group();
        group.getChildren().add(ap);
        rootSwapPane.getChildren().add(group);
        titles.add(socketgroup.getGemName());
    }

    private ArrayList<String> titles;
    public void reset(){
        rootSwapPane.getChildren().clear();
        titles.clear();
    }

    public void defaultTitle(){
        mainGem.setText("Path of Leveling");
        pagination.setText("");
    }

    public void fade(double opacity){
        rootSwapPane.setOpacity(opacity);
    }

    public double getOpacity(){
        return rootSwapPane.getOpacity();
    }
}