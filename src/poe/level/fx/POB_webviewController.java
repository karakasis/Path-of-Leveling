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
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class POB_webviewController implements Initializable {

    @FXML
    private WebView pob;
    
    public void open(String url){
        //final WebEngine webEngine = pob.getEngine();
        WebEngine engine = pob.getEngine();
        engine.load(url);
        /*
        Hyperlink hpl = new Hyperlink(url);
        
        hpl.setOnAction(new EventHandler<ActionEvent>() {
          @Override public void handle(ActionEvent e) {
              webEngine.load(url);
          }
      });*/
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
