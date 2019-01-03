/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import poe.level.data.Build;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class CharacterInfo_Controller implements Initializable {

    @FXML
    private TextField name;
    @FXML
    private TextField level;
    @FXML
    private Label error;
    
    private Main_Controller root;
    
    public void hook(Main_Controller root){
        this.root = root;
    }
    
    public void init(Build build){
        if(build.characterName.equals("")){
            name.setPromptText("Your in-game character name.");
        }else{
            name.setPromptText("");
            name.setText(build.characterName);
        }
        
        if(build.level == -1){
            level.setPromptText("The level of your character.");
        }else{
            level.setPromptText("");
            level.setText(build.level+"");
        }
    }
    
    @FXML
    private void start(){
        error.setVisible(false);
        boolean start = true;
        int parseInt = 1;
        try{
            parseInt = Integer.parseInt(level.getText());
            if(parseInt > 100 || parseInt <=0){
                throw new NumberFormatException();
            }
        }catch(NumberFormatException e){
            error.setText("Character Level is invalid.");
            error.setVisible(true);
            start = false;
        }
        
        if(name.getText().equals("")){
            name.setPromptText("Your in-game character name.");
            start = false;
        }
        
        if(start){
            root.closeCharacterPopup(name.getText(), parseInt);
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
