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
import javafx.scene.control.Label;
import javafx.scene.paint.Paint;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Export_pastebin_Controller implements Initializable {

    private String pastebinURL = "https://www.pastebin.com/";
    private String export_data;

    private final String EXPORT_DATA_CLIPBOARD = "Export-Data has successfully been copied to your Clipboard. You can now open pastebin and paste it manually!";
    private final String LINK_CLIPBOARD = "Pastebin link has successfully been copied to your Clipboard";
    private final String LINK_CREATED = "Your Pastebin Link has successfully been created! You can now copy it to your clipboard or open it directly!";
    private final String PASTEBIN_LIMIT = "Unfortunately the limit of pastebins in 24h has been reached. You can still copy the raw export data and paste it manually to pastebin!";
    private final String GENERAL_ERROR = "Something went wrong!";

    private final javafx.scene.paint.Paint COLOR_SUCCESS = javafx.scene.paint.Paint.valueOf("#8ecc5f");
    private final javafx.scene.paint.Paint COLOR_ERROR = Paint.valueOf("#CD5C5C");

    @FXML
    private Label message_label;

    @FXML
    private void copy() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection;

        if (this.export_data == null) {
            return;
        } else if (this.pastebinURL.equals("https://www.pastebin.com/")) {
            // Copies Raw Export-Data
            selection = new StringSelection(this.export_data);
            clipboard.setContents(selection, selection);
            showMessage(EXPORT_DATA_CLIPBOARD, true);
        } else {
            // Copies pastebin-URL
            selection = new StringSelection(this.pastebinURL);
            clipboard.setContents(selection, selection);
            showMessage(LINK_CLIPBOARD, true);
        }
    }

    @FXML
    private void openPastebin() {
        openWebpage(pastebinURL);
    }

    public void initPasteText(String export_data) {
        if (export_data == null || export_data.isEmpty()) {
            showMessage(GENERAL_ERROR, false);
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
            if (response.startsWith("Post")) {
                showMessage(PASTEBIN_LIMIT, false);
                return;
            }
            showMessage(LINK_CREATED, true);
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

    private void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new URL(urlString).toURI());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String message, boolean successful) {
        if (successful) {
            message_label.setTextFill(COLOR_SUCCESS);
        } else {
            message_label.setTextFill(COLOR_ERROR);
        }

        message_label.setText(message);
    }
}