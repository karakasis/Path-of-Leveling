/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import poe.level.fx.BuildsPanel_Controller.BuildLinker;

/**
 * FXML Controller class
 *
 * @author Christos
 */

public class BuildEntry_Controller implements Initializable {

    @FXML
    ImageView banner;
    @FXML
    Label buildName;
    @FXML
    Label ascendAndLevel;
    @FXML
    AnchorPane root;

    BuildLinker parent;
    SelectBuild_PopupController parent2;
    int id_for_popup;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void init(Image iv, String buildName, String ascendAndLevel, BuildLinker parent) {
        banner.setImage(iv);

        this.buildName.setText(buildName);
        // this.ascendAndLevel.setText(ascendAndLevel+" lvl.95");
        this.ascendAndLevel.setText(ascendAndLevel);
        this.parent = parent;
    }

    public void init_for_popup(Image iv, String buildName, String ascendAndLevel, int id,
            SelectBuild_PopupController parent) {
        banner.setImage(iv);

        this.buildName.setText(buildName);
        // this.ascendAndLevel.setText(ascendAndLevel+" lvl.95");
        this.ascendAndLevel.setText(ascendAndLevel);
        this.parent2 = parent;
        this.id_for_popup = id;
    }

    public AnchorPane getRoot() {
        return root;
    }

    public void reset(boolean validationColor) {
        // root.setStyle("-fx-background-color: transparent;"
        // +"-fx-border-style: solid;");
        if (validationColor)
            root.setStyle("color: transparent;");
        else
            root.setStyle("color: rgba(217, 215, 215, 0.8);");
    }

    public void setValidBackgroundColor(boolean validationColor, boolean isBuildActive) {
        // root.setStyle("-fx-background-color: transparent;"
        // +"-fx-border-style: solid;");
        if (!isBuildActive) {// so if build is not active set to transparent or gray.
            if (validationColor)
                root.setStyle("color: transparent;");
            else
                root.setStyle("color: rgba(217, 215, 215, 0.8);");
        }

    }

    @FXML
    private void onPress(ActionEvent event) {
        // root.setStyle("-fx-background-color: PeachPuff;"
        // +"-fx-border-style: solid;");
        if (parent != null) {
            root.setStyle("color: rgba(58, 44, 189, 0.4);");
            parent.update();
        }
        if (parent2 != null)
            parent2.update(id_for_popup);
    }

}
