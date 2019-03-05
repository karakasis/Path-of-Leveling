package poe.level.fx.overlay;

import com.jfoenix.controls.JFXCheckBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class RecipeArea_Controller implements Initializable {
    @FXML
    private Label zoneName;

    @FXML
    private Label recipeTooltip;

    @FXML
    private JFXCheckBox checkBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void init(String zone, String recipe, boolean check){
        if(zoneName!=null) zoneName.setText(zone);
        if(recipeTooltip!=null) recipeTooltip.setText(recipe);
        if(checkBox != null){
            checkBox.setSelected(check);
            checkBox.setDisable(true);
            checkBox.setStyle("-fx-opacity: 1");
        }
    }
}
