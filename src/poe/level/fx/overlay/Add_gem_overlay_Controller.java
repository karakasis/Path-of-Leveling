/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import poe.level.data.Gem;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Add_gem_overlay_Controller implements Initializable {

    @FXML
    private ImageView gemAddedImage;
    @FXML
    private ImageView socketGroupImage;
    @FXML
    private Label gemAddedName;
    @FXML
    private Label quest;
    @FXML
    private Label npc;
    @FXML
    private Label act;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    public void load(Gem add, Gem sg){
        gemAddedImage.setImage(add.getIcon());
        socketGroupImage.setImage(sg.getIcon());
        gemAddedName.setText(add.getGemName());
        quest.setText(add.quest_name);
        npc.setText(add.npc);
        if(add.act == 0){
            act.setText("Drop only");
        }else{
            act.setText("Act " + add.act);
        }
    }
}
