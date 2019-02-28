/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.awt.Desktop;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class AboutPageController implements Initializable {

    @FXML
    JFXButton paypal;
    @FXML
    Hyperlink ee;
    @FXML
    Hyperlink arias;
    @FXML
    Hyperlink rainy;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ee.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openWebpage("https://www.youtube.com/channel/UCaFHfrY-6uGSAvmczp_7a6Q/featured");
            }
        });
        arias.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openWebpage("https://github.com/max-arias/poeGems");
            }
        });
        rainy.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                openWebpage("https://github.com/karakasis");
            }
        });

    }

    public void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void paypal() {
        openWebpage("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=XKQ7R4AWWVFR4");
    }

}
