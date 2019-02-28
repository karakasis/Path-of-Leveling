/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXToggleButton;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import poe.level.data.Build;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Main_Controller implements Initializable {

    @FXML
    private JFXButton selectBuild;
    @FXML
    private StackPane rootPane;
    @FXML
    private JFXToggleButton zones;
    @FXML
    private JFXToggleButton xp;
    @FXML
    private JFXToggleButton leveling;
    @FXML
    private ImageView buildIcon;
    @FXML
    private Pane dragPane;

    /**
     * Initializes the controller class.
     */

    private static Image charToImage(String className, String asc) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(BuildsPanel_Controller.class.getResource("/classes/" + className + "/" + asc + ".png"));
        } catch (IOException ex) {
            Logger.getLogger(BuildsPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        return SwingFXUtils.toFXImage(img, null);
    }

    Main_Stage parent;
    private JFXDialog addBuildPopup;
    private JFXDialog editCharacterPopup;
    private Build buildLoaded;

    public void hookStage(Main_Stage stage) {
        parent = stage;
    }

    class Delta {
        double x, y;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        final Delta dragDelta = new Delta();
        dragPane.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = parent.getX() - mouseEvent.getScreenX();
                dragDelta.y = parent.getY() - mouseEvent.getScreenY();
            }
        });
        dragPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                parent.setX(mouseEvent.getScreenX() + dragDelta.x);
                parent.setY(mouseEvent.getScreenY() + dragDelta.y);
            }
        });
    }

    @FXML
    private void startButton() {
        if (Preferences_Controller.poe_log_dir == null || Preferences_Controller.poe_log_dir.equals("")) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Directory not set");
            alert.setContentText("Select the options button, and locate your Path of Exile installation folder!");
            alert.initOwner(parent);
            alert.showAndWait();
        } else {
            if (leveling.isSelected() && buildLoaded != null) {
                characterInfoPopup();
                // buildLoaded.level = temp_alert();
            } else if (leveling.isSelected() && buildLoaded == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Build not set");
                alert.setContentText("Select a build or create a new one in the Editor panel.");
                alert.initOwner(parent);
                alert.showAndWait();
            } else {
                if (xp.isSelected() || zones.isSelected()) {
                    characterInfoPopup();
                } else {
                    parent.start(zones.isSelected(), xp.isSelected(), leveling.isSelected());
                }
            }

        }
    }

    public int temp_alert() {
        TextInputDialog dialog = new TextInputDialog("walter");
        dialog.setTitle("Temporary level check");
        dialog.setContentText("Enter the level of this character:");

        dialog.initOwner(parent);
        // Traditional way to get the response value.
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            System.out.println("Your name: " + result.get());
        }
        return Integer.parseInt(result.get());
        // The Java 8 way to get the response value (with lambda expression).
        // result.ifPresent(name -> System.out.println("Your name: " + name));
    }

    @FXML
    private void launchEditor() {
        parent.editor();
    }

    @FXML
    private void selectBuild() {
        buildPopup();
    }

    @FXML
    private void preferences() {
        preferencesPopup();
    }

    @FXML
    private void close() {
        parent.closeApp();
    }

    @FXML
    private void minimize() {
        parent.setIconified(true);
    }

    public void preferencesPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("preferences.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<Preferences_Controller>getController().hook(this);

        addBuildPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        // controller.passDialog(mLoad);
        addBuildPopup.show();
    }

    public void characterInfoPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("characterInfo.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<CharacterInfo_Controller>getController().hook(this);
        if (buildLoaded != null)
            loader.<CharacterInfo_Controller>getController().init(buildLoaded);

        editCharacterPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        // controller.passDialog(mLoad);
        editCharacterPopup.show();
    }

    public void buildPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectBuild_Popup.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<SelectBuild_PopupController>getController().hook(this);
        addBuildPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        // controller.passDialog(mLoad);
        addBuildPopup.show();
    }

    public void closePopup(Build build) {
        buildLoaded = build;
        addBuildPopup.close();
        decorateSelectButton();
    }

    public void closePrefPopup() {
        addBuildPopup.close();
    }

    public void closeCharacterPopup(String characterName, int level) {
        if (xp.isSelected() || zones.isSelected()) {
            Main_Stage.playerLevel = level;
            Main_Stage.characterName = characterName;
            // Main_Stage.playerLevel = temp_alert();
        }
        if (leveling.isSelected()) {
            Main_Stage.buildLoaded = buildLoaded;
            buildLoaded.characterName = characterName;
            buildLoaded.level = level;
        }
        System.out.println("Character : " + characterName);
        System.out.println("Level : " + level);
        editCharacterPopup.close();

        parent.start(zones.isSelected(), xp.isSelected(), leveling.isSelected());
    }

    private void decorateSelectButton() {
        // selectBuild.setGraphic(new
        // ImageView(charToImage(buildLoaded.getClassName(),buildLoaded.getAsc())));
        // ImageView graphic = (ImageView) selectBuild.getGraphic();
        buildIcon.setImage(charToImage(buildLoaded.getClassName(), buildLoaded.getAsc()));
        // graphic.setFitHeight(30);
        // graphic.setPreserveRatio(true);
        selectBuild.setText(buildLoaded.getName());
    }
}
