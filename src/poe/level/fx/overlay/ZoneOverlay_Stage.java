/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx.overlay;

import static poe.level.fx.POELevelFx.saveBuildsToMemory;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import poe.level.data.Zone;
import poe.level.fx.Preferences_Controller;

/**
 *
 * @author Christos
 */
public class ZoneOverlay_Stage extends Stage {

    private boolean isVisible;
    public static double prefX;
    public static double prefY;

    public ZoneOverlay_Stage() {

        this.setAlwaysOnTop(true);
        this.initStyle(StageStyle.TRANSPARENT);

        if (Preferences_Controller.zones_overlay_pos[0] == -200
                && Preferences_Controller.zones_overlay_pos[1] == -200) {

            Rectangle2D primScreenBounds = Screen.getPrimary().getVisualBounds();
            primScreenBounds.getMinY();
            prefX = 200.0;
            prefY = primScreenBounds.getMinY();

            Preferences_Controller.updateZonesPos(prefX, prefY);
        } else {
            prefX = Preferences_Controller.zones_overlay_pos[0];
            prefY = Preferences_Controller.zones_overlay_pos[1];
        }

        this.setOnCloseRequest(event -> {
            System.out.println("Closing level:: ");
            if (saveBuildsToMemory()) {
                System.out.println("Successfully saved checkpoint");
            } else {
                System.out.println("Checkpoint save failed");
            }
            System.exit(10);
        });
    }

    public void queue(Zone currentZone) {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/poe/level/fx/overlay/ZoneOverlay.fxml"));
        AnchorPane ap = null;
        try {
            ap = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ZoneOverlay_Stage.class.getName()).log(Level.SEVERE, null, ex);
        }
        ZoneOverlay_Controller controller = loader.<ZoneOverlay_Controller>getController();
        controller.init(currentZone);
        Scene scene = new Scene(ap);
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        bindKeyEvent(scene);
        this.setScene(scene);

        controller.hookStage(this);

        this.setX(prefX);
        this.setY(prefY);
        this.setHeight(0);

        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return getHeight();
            }

            @Override
            public void setValue(Double value) {
                setHeight(value);
            }
        };

        Timeline slideIn = new Timeline();

        KeyValue kv = new KeyValue(writableWidth, 256d);
        KeyFrame kf_slideIn = new KeyFrame(Duration.millis(500), kv);

        if (Preferences_Controller.zones_toggle) {
            KeyFrame kf_delay = new KeyFrame(Duration.millis(Preferences_Controller.zones_slider * 1000));
            slideIn.getKeyFrames().addAll(kf_slideIn, kf_delay);
            Timeline slideOut = new Timeline();
            KeyFrame kf_slideOut = new KeyFrame(Duration.millis(500), new KeyValue(writableWidth, 0.0));
            slideOut.getKeyFrames().add(kf_slideOut);
            slideOut.setOnFinished(e -> Platform.runLater(() -> {
                System.out.println("Ending");
                this.hide();
            }));
            slideIn.setOnFinished(e -> Platform.runLater(() -> slideOut.play()));
        } else {
            slideIn.getKeyFrames().addAll(kf_slideIn);
        }

        slideIn.play();
        this.show();

    }

    public void showPanel() {
        isVisible = true;
        this.setX(prefX);
        this.setY(prefY);
        this.setHeight(0);

        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return getHeight();
            }

            @Override
            public void setValue(Double value) {
                setHeight(value);
            }
        };

        Timeline slideIn = new Timeline();

        KeyValue kv = new KeyValue(writableWidth, 169d);
        KeyFrame kf_slideIn = new KeyFrame(Duration.millis(500), kv);
        slideIn.getKeyFrames().addAll(kf_slideIn);
        slideIn.play();
    }

    public void hidePanel() {

        isVisible = false;

        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {
                return getHeight();
            }

            @Override
            public void setValue(Double value) {
                setHeight(value);
            }
        };

        Timeline timeline = new Timeline();
        KeyFrame endFrame = new KeyFrame(Duration.millis(500), new KeyValue(writableWidth, 0.0));
        timeline.getKeyFrames().add(endFrame);
        timeline.setOnFinished(e -> Platform.runLater(() -> {
            System.out.println("Ending");
            this.hide();
        }));
        timeline.play();
    }

    public void reset() {
        if (!Preferences_Controller.zones_toggle || isVisible) {
            hidePanel();
        }
    }

    public void event_show_hide() {
        if (isVisible) {
            hidePanel();
        } else {
            showPanel();
        }
    }

    public void bindKeyEvent(Scene scene) {
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (Preferences_Controller.zones_hotkey_show_hide_key.match(event)) {
                    if (isVisible) {
                        hidePanel();
                    } else {
                        showPanel();
                    }
                }

            }
        });
    }

}
