/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.response.Response;
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
    
    private int code;
    private String pasteResponseString;
    
    @FXML
    private void paste_fetch(){
        String text = paste_area.getText();
        String pasteKey = "";
        boolean valid = false;
        if(text.startsWith("https://pastebin.com/")){
            valid = true;
            pasteKey = text.substring(21, text.length());
        }else{
            System.out.println("Link is not a pastebin!");
            paste_error.setVisible(true);
            paste_error.setText("Link is not pastebin");
            paste_area.clear();
            return;
        }
        
        final PastebinFactory factory = new PastebinFactory();
        final Pastebin pastebin = factory.createPastebin("6843a1b84c4a35f98f5488c8671e9a60");
        //final String pasteKey = "LAZD9ZCs";
        final Response<String> pasteResponse = pastebin.getRawPaste(pasteKey);
        if (pasteResponse.hasError()) {
          System.out.println("Unable to read paste content!");
          failed();
          return;
        }
        paste_error.setVisible(false);
        //paste_error.setText("Valid");
        System.out.println(pasteResponse.get());
        pasteResponseString = pasteResponse.get();
        if(valid) root.fetch_pob_paste(text,code);
    }
    
    public void hook(MainApp_Controller root, int code){
        this.code = code; //0 is create , 1 is link
        this.root = root;
    }
    
    public String getResponse(){
        return pasteResponseString;
    }
    
    public void success(){
        paste_error.setVisible(false);
        //paste_error.setText("Valid");
    }
    
    public void failed(){
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
