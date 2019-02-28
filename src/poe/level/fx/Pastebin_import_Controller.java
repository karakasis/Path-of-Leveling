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
import com.besaba.revonline.pastebinapi.*;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.response.Response;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Pastebin_import_Controller implements Initializable {

    @FXML
    private TextField paste_area;
    @FXML
    private Label paste_error;
    private MainApp_Controller root;

    @FXML
    private void paste_fetch() {
        String text = paste_area.getText();
        String pasteKey = "";
        if (text.startsWith("https://pastebin.com/")) {
            pasteKey = text.substring(21, text.length());
        }
        final PastebinFactory factory = new PastebinFactory();
        final Pastebin pastebin = factory.createPastebin("6843a1b84c4a35f98f5488c8671e9a60");
        // final String pasteKey = "LAZD9ZCs";
        final Response<String> pasteResponse = pastebin.getRawPaste(pasteKey);
        if (pasteResponse.hasError()) {
            System.out.println("Unable to read paste content!");
            paste_error.setVisible(true);
            paste_error.setText("Invalid pastebin");
            paste_area.clear();
            return;
        }
        paste_error.setVisible(false);
        // paste_error.setText("Valid");
        System.out.println(pasteResponse.get());
        root.fetchPaste(pasteResponse.get());
    }

    public void hook(MainApp_Controller root) {
        this.root = root;
    }

    public void success() {
        paste_error.setVisible(false);
        // paste_error.setText("Valid");
    }

    public void failed() {
        paste_error.setVisible(true);
        paste_error.setText("Invalid pastebin");
        paste_area.clear();
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
