/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.awt.Desktop;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Export_pastebin_Controller implements Initializable {

    @FXML
    private TextArea paste_area;
    
    @FXML
    private void pastebin_url(){
        openWebpage("https://pastebin.com/");
    }
    
    public void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void initPasteText(String paste){
        paste_area.setText(paste);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        paste_area.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            paste_area.selectAll();
        });
    }    
    
}
