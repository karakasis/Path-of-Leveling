/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import poe.level.data.CharacterInfo;
import poe.level.data.Util;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * FXML Controller class
 *
 * @author Protuhj
 */


public class CharacterEntry_Controller implements Initializable {

    @FXML
    ImageView banner;
    @FXML
    Label characterName;
    @FXML
    Label lblAscendancy;
    @FXML
    AnchorPane root;
    @FXML
    Label lblLeague;

    private Consumer<Integer> buildSelectorCallback;
    private int id_for_popup;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        root.setOnMouseClicked(event -> onPress());
    }

    void init_for_popup(CharacterInfo charInfo, int id, Consumer<Integer> callback){
        banner.setImage(Util.charToImage(charInfo.className, charInfo.ascendancyName));

        this.characterName.setText(charInfo.characterName);
        this.lblAscendancy.setText(charInfo.ascendancyName + "     lvl. " + charInfo.level);
        this.lblLeague.setText(charInfo.league);
        this.buildSelectorCallback = callback;
        this.id_for_popup = id;
    }

    private void onPress() {
        if(buildSelectorCallback != null) {
            buildSelectorCallback.accept(id_for_popup);
        }
    }

}
