package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import java.net.URL;
import java.util.ResourceBundle;

public class FilterTab_Controller implements Initializable {
    @FXML
    private Button tab;
    @FXML
    private JFXButton tabClose;
    @FXML
    private AnchorPane root;

    private String tabName;
    private AddGem_Controller parent;
    private int code;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void load(String tagName, AddGem_Controller root, int code){
        this.parent = root;
        tabName = tagName;
        this.code = code;

        tab.setText(tagName);
    }

    @FXML
    private void triggerClose(){
        parent.filterTabClosed(tabName,code);
    }
}
