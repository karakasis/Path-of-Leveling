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
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class ValidateErrorPopupController implements Initializable {

    @FXML
    private TextArea paste_error;
    @FXML 
    private Label build_label;
    
    public void setUp(String build_name, String error){
        if(build_name!=null){
            String build_con = "";
            if(build_name.length()>=20){
                build_con = build_name.substring(0, 20);
                build_con+="..";
            }else{
                build_con = build_name;
            }
            build_label.setText(build_con);
        }else{
            build_label.setText("no_name");
        }
        
        paste_error.setText(error);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
