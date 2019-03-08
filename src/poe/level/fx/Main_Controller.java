/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import poe.level.data.Build;
import poe.level.data.CharacterInfo;
import poe.level.data.Util;
import poe.level.fx.overlay.PlaceholderStageGameMode;
import poe.level.fx.overlay.RecipeOverlay_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Main_Controller implements Initializable {

    private final Logger m_logger = Logger.getLogger(Main_Controller.class.getName());

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

    Main_Stage parent;
    private JFXDialog addBuildPopup;
    private JFXDialog editCharacterPopup;
    private Build buildLoaded;

    void hookStage(Main_Stage stage){
        parent = stage;
    }

    class Delta { double x, y; }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        final Delta dragDelta = new Delta();
        dragPane.setOnMousePressed(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = parent.getX() - mouseEvent.getScreenX();
            dragDelta.y = parent.getY() - mouseEvent.getScreenY();
          }
        });
        dragPane.setOnMouseDragged(new EventHandler<MouseEvent>() {
          @Override public void handle(MouseEvent mouseEvent) {
            parent.setX(mouseEvent.getScreenX() + dragDelta.x);
            parent.setY(mouseEvent.getScreenY() + dragDelta.y);
          }
        });
    }

    @FXML
    private void startButton(){
        if(Preferences_Controller.poe_log_dir == null || Preferences_Controller.poe_log_dir.equals("")){
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Directory not set");
            alert.setContentText("Select the options button, and locate your Path of Exile installation folder!");
            alert.initOwner(parent);
            alert.showAndWait();
        }else{
            if(leveling.isSelected() && buildLoaded!=null){
                characterInfoPopup();
                //buildLoaded.level = temp_alert();
            }else if(leveling.isSelected() && buildLoaded == null){
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Build not set");
                alert.setContentText("Select a build or create a new one in the Editor panel.");
                alert.initOwner(parent);
                alert.showAndWait();
            }else{
                if(xp.isSelected() || zones.isSelected()){
                    characterInfoPopup();
                }else{
                    parent.start(zones.isSelected(), xp.isSelected(), leveling.isSelected());
                }
            }

        }
    }

    @FXML
    private void launchEditor(){
        parent.editor();
    }

    @FXML
    private void selectBuild(){
        buildPopup();
    }

    @FXML
    private void preferences(){
        preferencesPopup();
    }

    @FXML
    private void close(){
        parent.closeApp();
    }

    @FXML
    private void minimize(){
        parent.setIconified(true);
    }

    private void preferencesPopup() {
        /*
        PlaceholderStageGameMode placeholder_stageGameMode = new PlaceholderStageGameMode(null);
        placeholder_stageGameMode.loadSettings();
        Preferences_Controller.gameModeOn = true;
        //RecipeOverlay_Controller.gameModeOn = false;
        placeholder_stageGameMode.show();*/

        FXMLLoader loader = new FXMLLoader(getClass().getResource("preferences.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            m_logger.log(Level.SEVERE, null, ex);
        }
        loader.<Preferences_Controller>getController().hook(this);
        addBuildPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        addBuildPopup.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    closePrefPopup();
                }
            }
        });
        //addBuildPopup.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        //controller.passDialog(mLoad);
        addBuildPopup.show();
        addBuildPopup.setOnDialogOpened(new EventHandler<JFXDialogEvent>() {
            @Override
            public void handle(JFXDialogEvent event) {
                addBuildPopup.requestFocus();
            }
        });
    }

    private void characterInfoPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("characterInfo.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            m_logger.log(Level.SEVERE, null, ex);
        }
        loader.<CharacterInfo_Controller>getController().hook(this);
        if(buildLoaded!=null) {
            loader.<CharacterInfo_Controller>getController().init(buildLoaded);
        }

        editCharacterPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        //editCharacterPopup.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        //controller.passDialog(mLoad);
        editCharacterPopup.show();
    }

    private void buildPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("SelectBuild_Popup.fxml"));
        try {
            AnchorPane con = loader.load();
            loader.<SelectBuild_PopupController>getController().hook(this);
            addBuildPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            addBuildPopup.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
            //controller.passDialog(mLoad);
            addBuildPopup.show();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void closePopup(Build build){
        buildLoaded = build;
        addBuildPopup.close();
        decorateSelectButton();
    }

    void closePrefPopup(){
        addBuildPopup.close();
    }

    void closeCharacterPopup(CharacterInfo charInfo){
        if(xp.isSelected() || zones.isSelected()){
            Main_Stage.playerLevel = charInfo.level;
            Main_Stage.characterName = charInfo.characterName;
            //Main_Stage.playerLevel = temp_alert();
        }
        if(leveling.isSelected()){
            Main_Stage.buildLoaded = buildLoaded;
            buildLoaded.setCharacterInfo(charInfo);
            POELevelFx.saveBuildsToMemory();
        }

        editCharacterPopup.close();
        m_logger.info("Starting with character info: " + buildLoaded.getCharacterInfo());
        parent.start(zones.isSelected(), xp.isSelected(), leveling.isSelected());
    }

    private void decorateSelectButton(){
        //selectBuild.setGraphic(new ImageView(charToImage(buildLoaded.getClassName(),buildLoaded.getAsc())));
        //ImageView graphic = (ImageView) selectBuild.getGraphic();
        buildIcon.setImage(Util.charToImage(buildLoaded.getClassName(),buildLoaded.getAsc()));
        //graphic.setFitHeight(30);
        //graphic.setPreserveRatio(true);
        selectBuild.setText(buildLoaded.getName());
    }

    StackPane getRootPane() {
        return rootPane;
    }
}
