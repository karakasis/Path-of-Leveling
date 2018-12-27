/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.sun.deploy.util.StringUtils;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import poe.level.data.Build;
import poe.level.data.Gem;
import poe.level.data.GemHolder;
import poe.level.data.SocketGroup;
import poe.level.fx.AddBuild_Controller;
import poe.level.fx.BuildEntry_Controller;
import poe.level.fx.MainApp_Controller;
import poe.level.fx.SocketGroupsPanel_Controller.SocketGroupLinker;


/**
 * FXML Controller class
 *
 * @author Christos
 */
public class SelectBuild_PopupController implements Initializable {
    
    private static Image charToImage(String className, String asc){
        BufferedImage img = null;
        try {
            img = ImageIO.read(BuildsPanel_Controller.class.getResource("/classes/"+className+"/"+asc+".png"));
        } catch (IOException ex) {
            Logger.getLogger(BuildsPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return SwingFXUtils.toFXImage(img, null);
    }
    
    @FXML
    private VBox buildsBox;
    
    private Main_Controller root;
    
    public void hook(Main_Controller root){
        this.root = root;
    }
    
    public void update(int id){
        
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
        for(int i=0; i<POELevelFx.buildsLoaded.size(); i++ ){ //this might require to update the buildsLoaded on each new build added and removed
            Build b = POELevelFx.buildsLoaded.get(i);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
            try {
                //this will add the AnchorPane to the VBox
                buildsBox.getChildren().add(loader.load());
            } catch (IOException ex) {
                Logger.getLogger(SelectBuild_PopupController.class.getName()).log(Level.SEVERE, null, ex);
            }
            BuildEntry_Controller bec = loader.<BuildEntry_Controller>getController(); //add controller to the linker class
            bec.init_for_popup(charToImage(b.getClassName(),b.getAsc())
                , b.getName(), b.getAsc(), i ,this);
        }
    }  

    
}
