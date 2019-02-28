/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Christos
 */
public class Editor_Stage extends Stage {

    POELevelFx parent;
    MainApp_Controller editor_controller;

    public Editor_Stage(POELevelFx parent) {
        this.parent = parent;
        this.launcher();

        this.setOnCloseRequest(event -> {
            if (!editor_controller.custom_editor_exit_with_validate()) {
                event.consume();
            }
            // System.exit(40);
        });
    }

    public void launcher() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainApp.fxml"));
        StackPane sp = null;
        try {
            sp = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(Main_Stage.class.getName()).log(Level.SEVERE, null, ex);
        }
        editor_controller = loader.<MainApp_Controller>getController();
        editor_controller.hook(this);
        Scene scene = new Scene(sp);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        this.setScene(scene);

        this.show();
    }

    public void returnToLauncher() {
        parent.launcher();
    }
}
