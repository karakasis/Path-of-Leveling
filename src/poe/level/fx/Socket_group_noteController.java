/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Socket_group_noteController implements Initializable {

    private SocketGroupsPanel_Controller parent;
    private String note;
    @FXML
    private TextArea note_area;

    public void start(SocketGroupsPanel_Controller parent, String note) {
        this.parent = parent;
        this.note = note;
        note_area.setText(note);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        note_area.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue,
                    final String newValue) {
                // this will run whenever text is changed
                note = newValue;
                parent.noteChange(newValue);
            }
        });
    }

}
