/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ResourceBundle;

import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.paste.Paste;
import com.besaba.revonline.pastebinapi.paste.PasteBuilder;
import com.besaba.revonline.pastebinapi.paste.PasteExpire;
import com.besaba.revonline.pastebinapi.paste.PasteVisiblity;
import com.besaba.revonline.pastebinapi.response.Response;
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
        String pastebinText = paste_area.getText();
        if(pastebinText == null || pastebinText.isEmpty()) {
            return;
        } else {
            PastebinFactory factory = new PastebinFactory();
            Pastebin pastebin = factory.createPastebin("6843a1b84c4a35f98f5488c8671e9a60");
            final PasteBuilder pasteBuilder = factory.createPaste();

            pasteBuilder.setTitle("Path-Of-Leveling-Export");

            pasteBuilder.setRaw(pastebinText);
            // Which syntax will use the paste?
            pasteBuilder.setMachineFriendlyLanguage("text");
            // What is the visibility of this paste?
            pasteBuilder.setVisiblity(PasteVisiblity.Public);
            // When the paste will expire?
            pasteBuilder.setExpire(PasteExpire.OneWeek);

            Paste paste = pasteBuilder.build();

            final Response<String> pasteResponse = pastebin.post(paste);
            if (pasteResponse.hasError()) {
                System.out.println("Unable to read paste content!");
                return;
            }
            // PasteBin URL
            String response = pasteResponse.get();

            // Saves to URL to the Clipboard
            StringSelection selection = new StringSelection(response);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);

            openWebpage(response);
        }
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
