/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import poe.level.data.Gem;
import poe.level.data.Zone;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class QuestSplitPanel_Controller implements Initializable {

    @FXML
    private HBox gemBox;
    @FXML
    private Label level_label;
    @FXML
    private Label quest_label;
    @FXML
    private SplitPane container;
    //private JFXButton gemButton;
    /**
     * Initializes the controller class.
     */
    private AddGem_Controller parent;

    public void loadOther(ArrayList<Gem> gems ,int order, AddGem_Controller parent){
        this.parent = parent;
        level_label.setText("Level "+order);
        //quest_label.setText(z.getZoneQuest());
        quest_label.setText("");
        int counter = 0;
        for(Gem g : gems){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gemButton.fxml"));
            //gemButton con = null;
            JFXButton gemButton= null;
            try {
                gemButton = (JFXButton) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            GemButton_Controller controller = loader.<GemButton_Controller>getController();
            controller.load(g,this);
            gemBox.getChildren().add(gemButton);
            counter++;
            /*
            if(counter%5 == 0){
                RowConstraints row = new RowConstraints();
                gemBox.getRowConstraints().add(row);
            }*/
        }
    }

    public void load(Zone z, ArrayList<Gem> gems, AddGem_Controller parent){
        this.parent = parent;
        level_label.setText("Level "+z.getZoneLevel());
        quest_label.setText(z.getZoneQuest());
        for(Gem g : gems){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gemButton.fxml"));
            //gemButton con = null;
            JFXButton gemButton= null;
            try {
                gemButton = (JFXButton) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            GemButton_Controller controller = loader.<GemButton_Controller>getController();
            controller.load(g,this);
            gemBox.getChildren().add(gemButton);
        }
    }

    public void callback(Gem g){
        parent.callback(g);
    }

    public void requestBorder(){
        container.setStyle("-fx-border-color: red;");
        // Ugly ugly way to scroll to the box
        if (container.getParent().getParent().getParent().getParent() instanceof ScrollPane) {
            ScrollPane parent = (ScrollPane) container.getParent().getParent().getParent().getParent();
            Bounds b = parent.getViewportBounds();
            parent.setVvalue(container.getLayoutY() * (1 / (((VBox)container.getParent()).getHeight() - b.getHeight())));
        }
    }

    void resetBorder(){
        resetBorder(false);
    }

    void resetBorder(boolean scrollToTop) {
        container.setStyle(null);
        if (scrollToTop && container.getParent().getParent().getParent().getParent() instanceof ScrollPane) {
            ScrollPane parent = (ScrollPane) container.getParent().getParent().getParent().getParent();
            parent.setVvalue(parent.getVmin());
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // TODO
    }

}
