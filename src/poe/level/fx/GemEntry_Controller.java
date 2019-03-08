/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;

import java.net.URL;
import java.util.ResourceBundle;

import com.sun.javafx.geom.BaseBounds;
import com.sun.javafx.geom.transform.BaseTransform;
import com.sun.javafx.scene.BoundsAccessor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Effect;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import poe.level.data.Gem;
import poe.level.data.GemListCell;
import poe.level.data.GemToString;
import poe.level.data.Util;
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
    private JFXTextField act;
    @FXML
    private JFXTextField soldBy;
    @FXML
    private JFXTextField town;
    @FXML
    private JFXComboBox<Gem> replaceGem;
    @FXML
    private JFXToggleButton replaceToggle;
    @FXML
    private AnchorPane root;
    @FXML
    private AnchorPane outerRoot;
    @FXML
    private JFXButton backButton;
    @FXML
    private Spinner<Integer> levelSlider;


    public static boolean skip = false;
    int id;

    GemLinker parent;
    Gem selectedGem;
    int selected;
    private boolean lockClear = false;

    private void handleSpin(ObservableValue<? extends Integer> observableValue, Integer oldValue, Integer newValue) {
        try {
            if (newValue == null) {
                levelSlider.getValueFactory().setValue(selectedGem.level_added);
            }else{
                levelChanged();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        replaceGem.setVisible(false);
        ObservableList<Label> list = FXCollections.observableArrayList();
        SpinnerValueFactory<Integer> valueFactoryFrom =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);

        levelSlider.setValueFactory(valueFactoryFrom);
        levelSlider.setEditable(true);
        levelSlider.valueProperty().addListener(this::handleSpin);
        Util.addIntegerLimiterToIntegerSpinner(levelSlider, valueFactoryFrom);

        //ArrayList<Gem> gems = GemHolder.getInstance().getGemsClass();
        /*
        for(Gem gem : gems){
            list.add(gem.getLabel());
            /*
            Label l = new Label();
            l.setText(gem.name.toString());
            Image img = new Image(gem.iconPath);
            l.setGraphic(new ImageView(img));
            list.add(l);
        }
*/
        /*
        ArrayList<Gem> gems = GemHolder.getInstance().getGemsClass();
        gemSelected.getItems().addAll(gems);
        gemSelected.setCellFactory(lv -> new GemListCell());
        gemSelected.setButtonCell(new GemListCell());
        gemSelected.setConverter(new GemToString());*/
     /*
        gemSelected.getEditor().textProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable,
                                            String oldValue, String newValue) {
                if(!GemEntry_Controller.skip){
                    GemEntry_Controller.skip = true;
                    gemSelected.show();
                    if(newValue.equals("")){
                        ArrayList<Gem> gemsDefault = GemHolder.getInstance().getGemsClass();
                        gemSelected.getItems().clear();
                        gemSelected.getItems().addAll(gemsDefault);
                    }else{
                        ArrayList<Gem> gemsQuery = GemHolder.getInstance().custom(newValue);
                        gemSelected.getItems().clear();
                        gemSelected.getItems().addAll(gemsQuery);
                    }
                    GemEntry_Controller.skip = false;
                }




            }
        });
        */
     /*
        gemSelected.getEditor().focusedProperty().addListener(
            (observable, oldValue, newValue) -> {
               if (newValue) {
               // Select the content of the field when the field gets the focus.
                Platform.runLater(gemSelected.getEditor()::selectAll);
               }
            }
        );*/
        //TextFields.bindAutoCompletion(gemSelected.getEditor(), gemSelected.getItems(),new GemToString());
        /*new AutoCompleteComboBoxListener(gemSelected);*/
        selected = -1;
    }

    public void input(GemLinker parent,int id){
        this.parent = parent;
        this.id = id;


               replaceGem.setVisible(false);
               replaceToggle.setSelected(false);

        for(Gem g_sib : parent.getSiblings().getGems()){
            if(!g_sib.equals(selectedGem)){
                replaceGem.getItems().add(g_sib);
            }
        }
        replaceGem.setCellFactory(lv -> new GemListCell());
        replaceGem.setButtonCell(new GemListCell());
        replaceGem.setConverter(new GemToString());

        //add open window gem selection pop up functionallity
        parent.requestPopup();

    }

    public void load(GemLinker parent,int id, Gem g){
        this.parent = parent;

        this.id = id;

        for(Gem g_sib : parent.getSiblings().getGems()){
            if(!g_sib.equals(g)){
                replaceGem.getItems().add(g_sib);
            }
        }
        replaceGem.setCellFactory(lv -> new GemListCell());
        replaceGem.setButtonCell(new GemListCell());
        replaceGem.setConverter(new GemToString());

        selectedGem = g;
        replaceGem.setVisible(false);
        replaceToggle.setSelected(false);
        if(!selectedGem.getGemName().equals("<empty group>")){
            //ObservableList<Label> list = gemSelected.getItems();
            //also a way to remove effects from the disablePanel
            disablePanel.setDisable(false);
            disablePanel.setEffect(null);
            //change button
            selectGemButton.setGraphic(new ImageView(g.getSmallIcon()));
            selectGemButton.setText(g.getGemName());
            //gemSelected.getSelectionModel().select(selectedGem);
            /*
            for(Label a : list){
                if(a.getText().equals(selectedGem.getGemName())){
                    gemSelected.getSelectionModel().select(a);
                    break;
                }
            }*/
            levelSlider.getValueFactory().setValue(selectedGem.getLevelAdded());
            if(selectedGem.act == 0){
                act.setText("Drop only");
            }else{
                act.setText("Act " + selectedGem.act);
            }
            soldBy.setText(selectedGem.npc);
            town.setText(selectedGem.town);
            if(selectedGem.replaced){
               replaceGem.setVisible(true);
               replaceToggle.setSelected(true);
               replaceGem.setValue(selectedGem.replacedWith);
            }else{
               replaceGem.setVisible(false);
               replaceToggle.setSelected(false);
            }
        }

    }

    public void toggleReplace(){
        if(replaceToggle.isSelected()){
            replaceGem.setVisible(true);
        }else{
            replaceGem.setVisible(false);
            replaceGem.getSelectionModel().clearSelection();
            selectedGem.replaced = false;
            selectedGem.replacedWith = null;
        }
    }

    @FXML
    private void gemPress(){
        parent.requestPopup();
    }

    public void callback(Gem g){
        //also a way to remove effects from the disablePanel
        disablePanel.setDisable(false);
        disablePanel.setEffect(null);

        //change button
        selectGemButton.setGraphic(new ImageView(g.getSmallIcon()));
        selectGemButton.setText(g.getGemName());

        selectedGem = g.dupeGem();
        if(selectedGem.act == 0){
            act.setText("Drop only");
        }else{
            act.setText("Act " + selectedGem.act);
        }
        soldBy.setText(selectedGem.npc);
        town.setText(selectedGem.town);
        if(selectedGem.replaced){
           replaceGem.setVisible(true);
           replaceToggle.setSelected(true);
        }else{
           replaceGem.setVisible(false);
           replaceToggle.setSelected(false);
        }


        parent.updateGemData(selectedGem);
        levelSlider.getValueFactory().setValue(selectedGem.getLevelAdded());
    }

    public void onGemSelect(){
            gemSelected.hide();
            //selected = gemSelected.getSelectionModel().getSelectedIndex();
            Gem g = (Gem) gemSelected.getValue();
            //gemSelected.getEditor().setText(g.getGemName());

            selectedGem = g.dupeGem();
            levelSlider.getValueFactory().setValue(selectedGem.getLevelAdded());
            act.setText("Act " + selectedGem.act);
            soldBy.setText(selectedGem.npc);
            town.setText(selectedGem.town);
            if(selectedGem.replaced){
               replaceGem.setVisible(true);
               replaceToggle.setSelected(true);
            }else{
               replaceGem.setVisible(false);
               replaceToggle.setSelected(false);
            }
            parent.updateGemData(selectedGem);

    }

    public void updateComboBox(){
        lockClear = true;
        replaceGem.getItems().clear();
        for(Gem g_sib : parent.getSiblings().getGems()){
            if(!g_sib.equals(selectedGem)){
                replaceGem.getItems().add(g_sib);
            }
        }

        if(selectedGem!= null && selectedGem.replaced){
            if(selectedGem.replacedWith != null){
                System.out.println(selectedGem.getGemName()+" replaces with : "+ selectedGem.replacedWith.getGemName());
            }else{
                System.out.println(selectedGem.getGemName()+" has null replacement ");
            }

            replaceGem.setValue(selectedGem.replacedWith);
        }else{
            replaceGem.getSelectionModel().clearSelection();
            replaceToggle.setSelected(false);
        }

        lockClear = false;
    }

    public void levelChanged(){
        selectedGem.level_added = (int) levelSlider.getValue();
        parent.levelChanged();
    }

    public void groupLevelChanged(int value){
        levelSlider.getValueFactory().setValue(value);
    }

    public void replaceChanged(){
        if(!lockClear){
            System.out.println(">>Called in the middle<<");
            selectedGem.replaced = replaceToggle.isSelected();
            selectedGem.replacedWith = replaceGem.getValue();
            if(selectedGem.replaced){
                replaceGem.setVisible(true);
                replaceToggle.setSelected(true);
             }else{
                replaceGem.setVisible(false);
                replaceToggle.setSelected(false);
             }
        }

    }

    public void onClick(){
        root.setStyle("color2: onclick-color-inner;");
        outerRoot.setStyle("color4: onclick-color-outer;");

        parent.clicked();
    }

    public void reset(){
        //root.setStyle("-fx-background-color: transparent;"
                //+"-fx-border-style: solid;");

        root.setStyle("color2: transparent;");
        outerRoot.setStyle("color4: transparent;");
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public AnchorPane getRoot(){
        return root;
    }

    public Gem getGem(){
        return selectedGem;
    }


}
