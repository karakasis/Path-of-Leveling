package poe.level.fx.overlay;

import com.jfoenix.controls.JFXCheckBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import poe.level.data.Zone;
import poe.level.fx.Preferences_Controller;

import java.net.URL;
import java.util.ResourceBundle;

public class RecipeArea_Controller implements Initializable {
    @FXML
    private Label zoneName;

    @FXML
    private Label recipeTooltip;

    @FXML
    private JFXCheckBox checkBox;

    private Zone zoneSaved;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Preferences_Controller.updateRecipeFile(zoneSaved);
            }
        });
    }

    public void init(Zone zone, String recipe, boolean check){
        this.zoneSaved = zone;
        if(zoneName!=null) zoneName.setText(zone.name);
        if(recipeTooltip!=null) recipeTooltip.setText(recipe);
        if(checkBox != null){
            checkBox.setSelected(check);
            //checkBox.setDisable(true);
            checkBox.setStyle("-fx-opacity: 1");
        }
    }
}
