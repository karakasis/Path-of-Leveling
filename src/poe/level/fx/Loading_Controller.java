/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXProgressBar;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Loading_Controller implements Initializable {

    @FXML
    private JFXProgressBar progressbar;
    @FXML
    private Label label;
    @FXML
    private AnchorPane downloadPanel;
    @FXML
    private Label downloadGemText;
    private int lastProgress;
    private int lastProgressSeenByGem;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        lastProgress = 0;
    }    
    
    public void notify(Double prog){
        progressbar.setProgress(prog/100.0);
        label.setText(prog.intValue() + " %");
        lastProgress = prog.intValue();
        if(lastProgressSeenByGem + 1< lastProgress ) downloadPanel.setVisible(false);
    }

    public void gemDownload(String gemName){
        lastProgressSeenByGem = lastProgress;
        downloadPanel.setVisible(true);
        downloadGemText.setText(gemName);
    }
    
}
