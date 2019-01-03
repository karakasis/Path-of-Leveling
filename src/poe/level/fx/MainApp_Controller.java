/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import poe.level.data.Build;
import poe.level.data.Gem;
import poe.level.data.GemHolder;
import poe.level.data.SocketGroup;

/**
 *
 * @author Christos
 */
public class MainApp_Controller implements Initializable {
    
    @FXML
    private AnchorPane buildsAnchorPane;
    @FXML
    private AnchorPane socketGroupsAnchorPane;
    @FXML 
    private AnchorPane gemsPanelAnchorPane;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label label;
    @FXML
    private StackPane rootPane;
    @FXML
    private MenuItem export_pastebin_active;
    @FXML
    private MenuItem export_pastebin_all;
    @FXML
    private MenuItem export_clipboard_active;
    @FXML
    private MenuItem export_clipboard_all;
    
    
    JFXDialog addBuildPopup;
    JFXDialog addGemPopup;
    JFXDialog buildPreviewPopup;
    
    //Controllers
    
    String build;
    String className;
    String ascendancy;
    ObservableList<Build> buildList;
    HashMap<BuildEntry_Controller,ObservableList<Label>> buildToSocketGroupMap;
    
    
    int count;
    int buildId;
    int socketGroupId;
    
    
    BuildsPanel_Controller buildspanel_controller;
    SocketGroupsPanel_Controller socketgroups_controller;
    GemsPanel_Controller gemspanel_controller;
    Editor_Stage parent;
    
    public void hook(Editor_Stage parent){
        this.parent = parent;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        buildToSocketGroupMap = new HashMap<>();
        
        //inflate buildspanel
        if(POELevelFx.buildsLoaded.isEmpty()){ //toggle all builds
            toggleAllBuilds(false);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BuildsPanel.fxml"));
        try {
            buildsAnchorPane.getChildren().add(loader.load());
            //buildsAnchorPane = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        buildspanel_controller = loader.<BuildsPanel_Controller>getController();
        buildspanel_controller.hook(this);
       
        loader = new FXMLLoader(getClass().getResource("SocketGroupsPanel.fxml"));
        try {
            socketGroupsAnchorPane.getChildren().add(loader.load());
            //buildsAnchorPane = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        socketgroups_controller = loader.<SocketGroupsPanel_Controller>getController();
        socketgroups_controller.hook(this);
        buildspanel_controller.hookSG_Controller(socketgroups_controller);
        
        if(POELevelFx.buildsLoaded!=null){
            buildspanel_controller.loadBuilds(POELevelFx.buildsLoaded);
        }
        
        loader = new FXMLLoader(getClass().getResource("GemsPanel.fxml"));
        try {
            gemsPanelAnchorPane.getChildren().add(loader.load());
            //buildsAnchorPane = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        gemspanel_controller = loader.<GemsPanel_Controller>getController();
        gemspanel_controller.hook(this);
        socketgroups_controller.hookGem_Controller(gemspanel_controller);
        
    }    
    
    //handling the more visual actions that occur
    //within the main app fxml
    
    //signals a new build and popup the dialog
    
    public void buildPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addBuild.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<AddBuild_Controller>getController().hook(buildspanel_controller,this);
        
        addBuildPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        //controller.passDialog(mLoad);
        addBuildPopup.show();
    }
    
    public void closePopup(){
        addBuildPopup.close();
    }
    
    public AddGem_Controller gemPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addGem.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        AddGem_Controller controller = loader.<AddGem_Controller>getController();
        
        addGemPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        //controller.passDialog(mLoad);
        addGemPopup.show();
        return controller;
    }
    
    public void gemClosePopup(){
        addGemPopup.close();
    }
    
    @FXML
    private void validateBuild(){
        if(buildspanel_controller.validate()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BuildProgressPreview.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            BuildProgressPreview_Controller controller = loader.<BuildProgressPreview_Controller>getController();

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            controller.start(buildspanel_controller.getCurrentBuild());
            buildPreviewPopup.show();
        }
    }
    
    @FXML
    private void saveAllBuilds(){
        try {
            if(buildspanel_controller.validateAll())
                buildspanel_controller.saveBuild();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void validateToLauncher(){
        try {
            if(buildspanel_controller.validateAll()){
                buildspanel_controller.saveBuild();
                parent.returnToLauncher();
            }
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    Pastebin_import_Controller paste_controller;
    
    @FXML
    private void importFromPastebin(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pastebin_import.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            paste_controller = loader.<Pastebin_import_Controller>getController();

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            paste_controller.hook(this);
            buildPreviewPopup.show();
    }
    
    @FXML
    private void exportToPastebinAll(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("export_pastebin.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            loader.<Export_pastebin_Controller>getController().initPasteText(buildspanel_controller.allTo64());

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            buildPreviewPopup.show();
    }
    
    @FXML
    private void exportToPastebinActive(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("export_pastebin.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            loader.<Export_pastebin_Controller>getController().initPasteText(buildspanel_controller.activeTo64());

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            buildPreviewPopup.show();
    }
    
    public void toggleActiveBuilds(boolean toggle){
        export_pastebin_active.setDisable(!toggle);
        export_clipboard_active.setDisable(!toggle);
    }
    
    public void toggleAllBuilds(boolean toggle){
        export_pastebin_all.setDisable(!toggle);
        export_clipboard_all.setDisable(!toggle);
    }
    
    public void fetchPaste(String pasteRaw){
        if(buildspanel_controller.loadBuildsFromPastebin(pasteRaw)){
            paste_controller.success();
            buildPreviewPopup.close();
        }else{
            paste_controller.failed();
        }
    }
    
    @FXML
    private void about(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutPage.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER).show();
    }
}
              /*
        BufferedImage before = null;
        try {
            before = ImageIO.read(getClass().getResource("/classes/"+className+"/"+ascendancy+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
                int w = before.getWidth();
    int h = before.getHeight();
    // Create a new image of the proper size
    int w2 = (int) (w * 0.2);
    int h2 = (int) (h * 0.2);
    BufferedImage after = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
    AffineTransform scaleInstance = AffineTransform.getScaleInstance(0.2, 0.2);
    AffineTransformOp scaleOp 
        = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);

    after = scaleOp.filter(before, after);
            Image image = SwingFXUtils.toFXImage(after, null);
                //ImageIcon imageIcon = new ImageIcon(dimg);
                
                */