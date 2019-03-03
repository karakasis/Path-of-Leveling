/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Export_pastebin_Controller implements Initializable {

    private String pastebinURL;
    private String export_data;

    @FXML
    private AnchorPane verify_clipboard;

    @FXML
    private void copyToClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection;

        if (this.export_data == null) {
            return;
        } else if (this.pastebinURL == null) {
            // Copies Export-Data and opens pastebin.com
            selection = new StringSelection(this.export_data);
            clipboard.setContents(selection, selection);

            inflatePastebinLimit();

            openWebpage("https://www.pastebin.com/");
        } else {
            // Copies pastebin-URL
            selection = new StringSelection(this.pastebinURL);
            clipboard.setContents(selection, selection);
            inflateSuccessMessage();
        }
    }

    public void initPasteText(String export_data){
        if(export_data == null || export_data.isEmpty()) {
            inflateErrorMessage();
            return;
        } else {
            this.export_data = export_data;

            PastebinFactory factory = new PastebinFactory();
            Pastebin pastebin = factory.createPastebin("6843a1b84c4a35f98f5488c8671e9a60");
            final PasteBuilder pasteBuilder = factory.createPaste();

            pasteBuilder.setTitle("Path-Of-Leveling-Export");
            pasteBuilder.setRaw(export_data);
            // Which syntax will use the paste?
            pasteBuilder.setMachineFriendlyLanguage("text");
            // What is the visibility of this paste?
            pasteBuilder.setVisiblity(PasteVisiblity.Public);
            // When the paste will expire?
            pasteBuilder.setExpire(PasteExpire.OneDay);

            Paste paste = pasteBuilder.build();

            final Response<String> pasteResponse = pastebin.post(paste);
            if (pasteResponse.hasError()) {
                System.out.println("Unable to read paste content!");
                return;
            }
            // PasteBin URL
            String response = pasteResponse.get();
            System.out.println(response);

            // "Post limit, maximum pastes per 24h reached"
            if(response.startsWith("Post")) {
                inflatePastebinLimit();
                return;
            }
            this.pastebinURL = response;
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inflatePastebinLimit() {
        Pane newLoadedPane = null;
        try {
            newLoadedPane = FXMLLoader.load(getClass().getResource("pastebin_limit.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        verify_clipboard.getChildren().clear();
        verify_clipboard.getChildren().add(newLoadedPane);
    }

    public void inflateErrorMessage() {
        Pane newLoadedPane = null;
        try {
            newLoadedPane = FXMLLoader.load(getClass().getResource("generatePastebinError.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        verify_clipboard.getChildren().clear();
        verify_clipboard.getChildren().add(newLoadedPane);
    }

    private void inflateSuccessMessage() {
        Pane newLoadedPane = null;
        try {
            newLoadedPane = FXMLLoader.load(getClass().getResource("clipboard_verify.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        verify_clipboard.getChildren().clear();
        verify_clipboard.getChildren().add(newLoadedPane);
    }
}
