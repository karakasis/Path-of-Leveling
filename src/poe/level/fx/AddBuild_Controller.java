/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class AddBuild_Controller implements Initializable {

    @FXML
    JFXTextField buildName;
    @FXML
    JFXComboBox select;
    @FXML
    JFXComboBox selectAsc;
    @FXML
    JFXButton addButton;
    
    
    JFXDialog parentDialog;
    int selectedClassIndex;
    String build;
    String className;
    String ascendancy;
    
    private BuildsPanel_Controller root;
    private MainApp_Controller closer;
    /**
     * Initializes the controller class.
     */
    
    public void hook(BuildsPanel_Controller root, MainApp_Controller closer){
        this.closer = closer;
        this.root = root;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        /*
        Marauder
        Ranger
        Witch
        Duelist
        Templar
        Shadow
        Scion
        */
        addButton.setDisable(true);
        select.getItems().add("Marauder");
        select.getItems().add("Ranger");
        select.getItems().add("Witch");
        select.getItems().add("Duelist");
        select.getItems().add("Templar");
        select.getItems().add("Shadow");
        select.getItems().add("Scion");
        
        buildName.textProperty().addListener((observable, oldValue, newValue) -> {
            addButton.setDisable(true);
            if(!buildName.getText().equals("")){
                if(select.getSelectionModel().getSelectedIndex()!= -1){
                    if(selectAsc.getSelectionModel().getSelectedIndex()!= -1){
                        addButton.setDisable(false);
                    }
                }
            }
        });
    }    
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        //when user presses add. Callback to MainApp_Controller.
        root.addNewBuild(buildName.getText(),className,ascendancy);
        closer.closePopup();
    }
    
    @FXML
    private void classChanged(ActionEvent event) {
        selectedClassIndex = select.getSelectionModel().getSelectedIndex();
        if(selectedClassIndex == 0){
            className = "Marauder";
            selectAsc.getItems().clear();
            selectAsc.getItems().add("Juggernaut");
            selectAsc.getItems().add("Berserker");
            selectAsc.getItems().add("Chieftain");
        }else if(selectedClassIndex == 1){
            className = "Ranger";
            selectAsc.getItems().clear();
            selectAsc.getItems().add("Deadeye");
            selectAsc.getItems().add("Raider");
            selectAsc.getItems().add("Pathfinder");
        }else if(selectedClassIndex == 2){
            className = "Witch";
            selectAsc.getItems().clear();
            selectAsc.getItems().add("Necromancer");
            selectAsc.getItems().add("Occultist");
            selectAsc.getItems().add("Elementalist");
        }else if(selectedClassIndex == 3){
            className = "Duelist";
            selectAsc.getItems().clear();
            selectAsc.getItems().add("Slayer");
            selectAsc.getItems().add("Gladiator");
            selectAsc.getItems().add("Champion");
        }else if(selectedClassIndex == 4){
            className = "Templar";
            selectAsc.getItems().clear();
            selectAsc.getItems().add("Inquisitor");
            selectAsc.getItems().add("Hierophant");
            selectAsc.getItems().add("Guardian");
        }else if(selectedClassIndex == 5){
            className = "Shadow";
            selectAsc.getItems().clear();
            selectAsc.getItems().add("Assasin");
            selectAsc.getItems().add("Saboteur");
            selectAsc.getItems().add("Trickster");
        }else if(selectedClassIndex == 6){
            className = "Scion";
            selectAsc.getItems().clear();
            selectAsc.getItems().add("Ascendant");
        }
        addButton.setDisable(true);
        if(!buildName.getText().equals("")){
            if(select.getSelectionModel().getSelectedIndex()!= -1){
                if(selectAsc.getSelectionModel().getSelectedIndex()!= -1){
                    addButton.setDisable(false);
                }
            }
        }
        
    }
    
    @FXML
    private void ascChanged(ActionEvent event) {
        ascendancy = (String) selectAsc.getSelectionModel().getSelectedItem();
        System.out.println(ascendancy);
        if(!buildName.getText().equals("")){
            addButton.setDisable(false);
        }else{
            addButton.setDisable(true);
        }
    }
    
    
    public void passDialog(JFXDialog parent){
        this.parentDialog = parent;
    }
    
}
