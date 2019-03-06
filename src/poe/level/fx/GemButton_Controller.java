/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import poe.level.data.Gem;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class GemButton_Controller implements Initializable {

    @FXML
    private JFXButton gemButton;
    
    private Gem gem;
    private QuestSplitPanel_Controller parent;
    private AddGem_Controller parentFiltering;
    /**
     * Initializes the controller class.
     */
    public void load(Gem g,QuestSplitPanel_Controller parent){
        this.parent = parent;
        gem = g;
        gemButton.setGraphic(new ImageView(g.gemIcon));
        gemButton.setText(g.getGemName());
        String toolt = "";
        for(String s : gem.tags) toolt+=s + " /";
        toolt.substring(0,toolt.length()-2);
        gemButton.setTooltip(new Tooltip(toolt));
    }

    public void loadFiltering(Gem g,AddGem_Controller parent){
        this.parentFiltering = parent;
        gem = g;
        gemButton.setGraphic(new ImageView(g.gemIcon));
        gemButton.setText(g.getGemName());
        String toolt = "";
        for(String s : gem.tags) toolt+=s + " /";
        toolt.substring(0,toolt.length()-2);
        gemButton.setTooltip(new Tooltip(toolt));
    }
    
    @FXML
    private void gemClick(){
        if(parent!=null)
        parent.callback(gem);
        else
        parentFiltering.callback(gem);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }    
    
}
