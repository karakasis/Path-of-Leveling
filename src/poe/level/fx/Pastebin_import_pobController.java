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

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Pastebin_import_pobController implements Initializable {

    @FXML
    private TextField paste_area;
    @FXML
    private Label paste_error;
    private MainApp_Controller root;

    @FXML
    private void paste_fetch() {
        String text = paste_area.getText();
        if (text.startsWith("https://pastebin.com/")) {
            root.fetch_pob_paste(text);
        }
    }

    public void hook(MainApp_Controller root) {
        this.root = root;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        paste_error.setVisible(false);
    }

}
