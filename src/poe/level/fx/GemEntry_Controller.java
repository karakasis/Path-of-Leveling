/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import poe.level.data.Gem;
import poe.level.data.GemListCell;
import poe.level.data.GemToString;
import poe.level.fx.GemsPanel_Controller.GemLinker;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class GemEntry_Controller implements Initializable {

    @FXML
    private JFXComboBox<Gem> gemSelected;
    @FXML
    private Button selectGemButton;
    @FXML
    private AnchorPane disablePanel;
    @FXML
    private JFXSlider levelSlider;
    @FXML
    private JFXTextField act;
    @FXML
    private JFXTextField soldBy;
    @FXML
    private JFXComboBox<Gem> replaceGem;
    @FXML
    private JFXToggleButton replaceToggle;
    @FXML
    private AnchorPane root;
    @FXML
    private JFXButton backButton;
    @FXML
    private Label label_with;
    @FXML
    private Label levelLabel;

    int id;

    GemLinker parent;
    Gem selectedGem;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        replaceGem.setVisible(false);

        levelSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                levelChanged();

            }
        });
    }

    public void input(GemLinker parent, int id) {
        this.parent = parent;
        this.id = id;

        label_with.setVisible(false);
        replaceGem.setVisible(false);
        replaceToggle.setSelected(false);

        for (Gem g_sib : parent.getSiblings().getGems()) {
            if (!g_sib.equals(selectedGem)) {
                replaceGem.getItems().add(g_sib);
            }
        }
        replaceGem.setCellFactory(lv -> new GemListCell());
        replaceGem.setButtonCell(new GemListCell());
        replaceGem.setConverter(new GemToString<Gem>());

        // add open window gem selection pop up functionallity
        parent.requestPopup();

    }

    public void load(GemLinker parent, int id, Gem g) {
        this.parent = parent;

        this.id = id;

        for (Gem g_sib : parent.getSiblings().getGems()) {
            if (!g_sib.equals(g)) {
                replaceGem.getItems().add(g_sib);
            }
        }
        replaceGem.setCellFactory(lv -> new GemListCell());
        replaceGem.setButtonCell(new GemListCell());
        replaceGem.setConverter(new GemToString<Gem>());

        selectedGem = g;
        label_with.setVisible(false);
        replaceGem.setVisible(false);
        replaceToggle.setSelected(false);
        if (!selectedGem.getGemName().equals("<empty group>")) {
            // also a way to remove effects from the disablePanel
            disablePanel.setDisable(false);
            disablePanel.setEffect(null);
            // change button
            selectGemButton.setGraphic(new ImageView(g.getSmallIcon()));
            selectGemButton.setText(g.getGemName());
            levelSlider.setValue(selectedGem.getLevelAdded());
            act.setText("Act " + selectedGem.act);
            soldBy.setText(selectedGem.npc);
            if (selectedGem.replaced) {
                label_with.setVisible(true);
                replaceGem.setVisible(true);
                replaceToggle.setSelected(true);
                replaceGem.setValue(selectedGem.replacedWith);
            } else {
                label_with.setVisible(false);
                replaceGem.setVisible(false);
                replaceToggle.setSelected(false);
            }
        }

    }

    @FXML
    private void gemPress() {
        parent.requestPopup();
    }

    public void callback(Gem g) {
        // also a way to remove effects from the disablePanel
        disablePanel.setDisable(false);
        disablePanel.setEffect(null);
        // change button
        selectGemButton.setGraphic(new ImageView(g.getSmallIcon()));
        selectGemButton.setText(g.getGemName());

        selectedGem = g.dupeGem();
        levelSlider.setValue(selectedGem.getLevelAdded());
        act.setText("Act " + selectedGem.act);
        soldBy.setText(selectedGem.npc);
        if (selectedGem.replaced) {
            label_with.setVisible(true);
            replaceGem.setVisible(true);
            replaceToggle.setSelected(true);
        } else {
            label_with.setVisible(false);
            replaceGem.setVisible(false);
            replaceToggle.setSelected(false);
        }

        parent.updateGemData(selectedGem);
    }

    public void updateComboBox() {
        replaceGem.getItems().clear();
        for (Gem g_sib : parent.getSiblings().getGems()) {
            if (!g_sib.equals(selectedGem)) {
                replaceGem.getItems().add(g_sib);
            }
        }

        if (selectedGem != null && selectedGem.replaced) {
            if (selectedGem.replacedWith != null) {
                System.out.println(
                        selectedGem.getGemName() + " replaces with : " + selectedGem.replacedWith.getGemName());
            } else {
                System.out.println(selectedGem.getGemName() + " has null replacement ");
            }

            replaceGem.setValue(selectedGem.replacedWith);
        } else {
            replaceGem.getSelectionModel().clearSelection();
            replaceToggle.setSelected(false);
        }
    }

    public void levelChanged() {
        selectedGem.level_added = (int) levelSlider.getValue();
        levelLabel.setText(selectedGem.level_added + "");
        parent.levelChanged();
    }

    public void groupLevelChanged(int value) {
        levelSlider.setValue(value);
        levelLabel.setText(selectedGem.level_added + "");
    }

    public void reset() {
        root.setStyle("color2: transparent;");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public AnchorPane getRoot() {
        return root;
    }

    public Gem getGem() {
        return selectedGem;
    }

}
