/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import poe.level.data.Build;
import poe.level.data.CharacterInfo;
import poe.level.data.Util;
import poeapi.POEAPIHelper;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class CharacterInfo_Controller implements Initializable {

    @FXML
    private TextField txtManualName;
    @FXML
    private TextField txtManualLevel;
    @FXML
    private Label lblManualError;

    @FXML
    private TextField txtAccountName;
    @FXML
    private Label lblAutoError;
    @FXML
    private ImageView buildIcon;
    @FXML
    private JFXButton btnSelectCharacter;

    private JFXDialog addBuildPopup;

    private final CharacterInfo m_selectedCharacterInfo = new CharacterInfo();
    private Main_Controller root;

    public void hook(Main_Controller root) {
        this.root = root;
    }

    public void init(Build build) {
        if (build.getCharacterName().isEmpty()) {
            txtManualName.setPromptText("Your in-game character name.");
        } else {
            txtManualName.setPromptText("");
            txtManualName.setText(build.getCharacterName());
        }

        if (build.getCharacterLevel() == -1) {
            txtManualLevel.setPromptText("The level of your character.");
        } else {
            txtManualLevel.setPromptText("");
            txtManualLevel.setText(Integer.toString(build.getCharacterLevel()));
        }
        m_selectedCharacterInfo.copyFrom(build.getCharacterInfo());
        decorateSelectButton(m_selectedCharacterInfo);
    }

    @FXML
    private void startManual() {
        lblManualError.setVisible(false);
        boolean start = true;
        int parseInt = 1;
        try {
            parseInt = Integer.parseInt(txtManualLevel.getText());
            if (parseInt > 100 || parseInt <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            lblManualError.setText("Character Level is invalid.");
            lblManualError.setVisible(true);
            start = false;
        }

        if (txtManualName.getText().equals("")) {
            txtManualName.setPromptText("Your in-game character name.");
            start = false;
        }

        if (start) {
            m_selectedCharacterInfo.characterName = txtManualName.getText();
            m_selectedCharacterInfo.level = parseInt;
            root.closeCharacterPopup(m_selectedCharacterInfo);
        }
    }

    @FXML
    private void startAuto() {
        lblAutoError.setVisible(false);
        boolean start = true;
        if (m_selectedCharacterInfo.level == -1 || m_selectedCharacterInfo.characterName.trim().isEmpty()) {
            lblAutoError.setVisible(true);
            lblAutoError.setText("Invalid Character");
            start = false;
        }
        if (start) {
            root.closeCharacterPopup(m_selectedCharacterInfo);
        }
    }


    @FXML
    public void buildPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectBuild_Popup.fxml"));
        try {
            ArrayList<CharacterInfo> characters = POEAPIHelper.getCharacters(txtAccountName.getText());
            if (characters == null || characters.isEmpty()) {
                lblAutoError.setVisible(true);
                lblAutoError.setText("Failed to get your characters");
                return;
            } else {
                lblAutoError.setVisible(false);
            }
            loader.setController(new SelectCharacter_PopupController(characters));
            AnchorPane con = loader.load();
            SelectCharacter_PopupController controller = loader.getController();
            controller.hook(this::closePopup);
            con.setPrefWidth(400);
            addBuildPopup = new JFXDialog(root.getRootPane(), con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            addBuildPopup.show();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public void closePopup(CharacterInfo charInfo) {
        if (addBuildPopup != null) {
            addBuildPopup.close();
        }
        decorateSelectButton(charInfo);
        m_selectedCharacterInfo.copyFrom(charInfo);
    }

    private void decorateSelectButton(CharacterInfo charInfo) {
        //selectBuild.setGraphic(new ImageView(charToImage(buildLoaded.getClassName(),buildLoaded.getAsc())));
        //ImageView graphic = (ImageView) selectBuild.getGraphic();
        buildIcon.setImage(Util.charToImage(charInfo.className, charInfo.ascendancyName));
        //graphic.setFitHeight(30);
        //graphic.setPreserveRatio(true);
        btnSelectCharacter.setText(charInfo.characterName);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        txtAccountName.setText(Preferences_Controller.poe_account_name);
    }

}
