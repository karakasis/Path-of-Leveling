package poe.level.fx.overlay;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class RecipePane_Controller implements Initializable {
    @FXML
    private VBox vbox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void load(AnchorPane a){
        vbox.getChildren().add(a);
    }
}
