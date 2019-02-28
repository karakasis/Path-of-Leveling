/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import poe.level.data.Build;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class SelectBuild_PopupController implements Initializable {

    private static Image charToImage(String className, String asc) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(BuildsPanel_Controller.class.getResource("/classes/" + className + "/" + asc + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(BuildsPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return SwingFXUtils.toFXImage(img, null);
    }

    @FXML
    private VBox buildsBox;

    private Main_Controller root;

    public void hook(Main_Controller root) {
        this.root = root;
    }

    public void update(int id) {

        Build selected = POELevelFx.buildsLoaded.get(id);
        root.closePopup(selected);
    }

    /**
     * Initializes the controller class.
     */
    ArrayList<Build> bec_list;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        bec_list = new ArrayList<>();
        for (int i = 0; i < POELevelFx.buildsLoaded.size(); i++) { // this might require to update the buildsLoaded on
                                                                   // each new build added and removed
            Build b = POELevelFx.buildsLoaded.get(i);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
            try {
                // this will add the AnchorPane to the VBox
                buildsBox.getChildren().add(loader.load());
            } catch (IOException ex) {
                Logger.getLogger(SelectBuild_PopupController.class.getName()).log(Level.SEVERE, null, ex);
            }
            BuildEntry_Controller bec = loader.<BuildEntry_Controller>getController(); // add controller to the linker
                                                                                       // class
            bec.init_for_popup(charToImage(b.getClassName(), b.getAsc()), b.getName(), b.getAsc(), i, this);
        }
    }

}
