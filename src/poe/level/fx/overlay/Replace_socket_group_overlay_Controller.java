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
import javafx.scene.image.ImageView;
import poe.level.data.Gem;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Replace_socket_group_overlay_Controller implements Initializable {

    @FXML
    private ImageView gemAddedImage;
    @FXML
    private ImageView gemRemovedImage;
    @FXML
    private Label gemAddedName;
    @FXML
    private Label gemRemovedName;
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

    public void load(Gem add, Gem remove) {
        gemAddedImage.setImage(add.getIcon());
        gemRemovedImage.setImage(remove.getIcon());
        gemAddedName.setText(add.getGemName());
        gemRemovedName.setText(remove.getGemName());
        quest.setText(add.quest_name);
        npc.setText(add.npc);
        act.setText("Act " + add.act);
    }

}
