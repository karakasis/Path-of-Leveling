package poe.level.fx.overlay;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Accordion;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import poe.level.data.ActHandler;
import poe.level.data.Controller;
import poe.level.data.Zone;
import poe.level.fx.MainApp_Controller;
import poe.level.fx.Preferences_Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RecipeOverlay_Controller implements Initializable{
    @FXML
    private Accordion accordion;

    private MainApp_Controller parent;
    private Controller parent_gameModeOn;
    public static boolean gameModeOn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameModeOn = false;
        HashMap<Integer, ArrayList<Zone>> zoneRecipeActMapper = ActHandler.getInstance().getZoneRecipeActMapper();
        HashMap<Zone, Boolean> recipeMap = ActHandler.getInstance().recipeMap;
        for(int i = 0 ; i<12; i++){
            if(zoneRecipeActMapper.containsKey(i)){
                ArrayList<Zone> zones = zoneRecipeActMapper.get(i);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("RecipePaneInflater.fxml"));
                TitledPane tPane = null;
                try {
                    tPane = loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(RecipeOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }

                for(Zone z : zones){
                    FXMLLoader loader2 = new FXMLLoader(getClass().getResource("RecipeAreaInflater.fxml"));
                    AnchorPane aPane = null;
                    try {
                        aPane = loader2.load();
                    } catch (IOException ex) {
                        Logger.getLogger(RecipeOverlay_Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    loader2.<RecipeArea_Controller>getController().init(z,z.rInfo.tooltip,recipeMap.get(z));
                    loader.<RecipePane_Controller>getController().load(aPane);
                }
                accordion.getPanes().add(tPane);
                if(i == 11) tPane.setText("Epilogue");
                else tPane.setText("Act "+ i);
            }
        }
    }

    @FXML
    private void reset(){
        for(Zone z : ActHandler.getInstance().recipeMap.keySet()){
            ActHandler.getInstance().recipeMap.replace(z, false);
        }
        Preferences_Controller.resetRecipesFile();
        if(!gameModeOn) parent.refreshRecipePopup();
        else parent_gameModeOn.refreshRecipePopup();
    }

    public void hook(MainApp_Controller parent){
        this.parent = parent;
    }

    public void hookGameModeOn(Controller parent){
        this.parent_gameModeOn = parent;
    }
}
