/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import poe.level.fx.BuildsPanel_Controller.BuildLinker;

/**
 * FXML Controller class
 *
 * @author Christos
 */


public class BuildEntry_Controller implements Initializable {

    @FXML
    ImageView banner;
    @FXML
    Label buildName;
    @FXML
    Label ascendAndLevel;
    @FXML
    AnchorPane root;
    @FXML
    AnchorPane disabledPanel;
    @FXML
    AnchorPane nonValidPanel;
    @FXML
    private TextField editBuildName;

    BuildLinker parent;
    Consumer<Integer> buildSelectorCallback;
    int id_for_popup;
    boolean isDisabledInLauncher;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        isDisabledInLauncher = false;
        // TODO
        root.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.err.println("Clicked root " + event.getClickCount() + " times");
                onPress();
            }
          });

        buildName.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.err.println("Clicked label" + event.getClickCount() + " times");
              if (event.getClickCount()==2) {
                editBuildName.setVisible(true);
                editBuildName.setText(buildName.getText());
                //tab.setGraphic(textField);
                editBuildName.selectAll();
                editBuildName.requestFocus();
              }
            }
          });

        editBuildName.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (!editBuildName.isFocused())
                {
                    buildName.setText(editBuildName.getText());
                    parent.requestNameChange(editBuildName.getText());
                    editBuildName.setVisible(false);
                }
            }
        });

        editBuildName.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
              buildName.setText(editBuildName.getText());
              parent.requestNameChange(editBuildName.getText());
              editBuildName.setVisible(false);
            }
          });

    }

    public void init(Image iv, String buildName,String ascendAndLevel, BuildLinker parent){
        banner.setImage(iv);

        this.buildName.setText(buildName);
        //this.ascendAndLevel.setText(ascendAndLevel+" lvl.95");
        this.ascendAndLevel.setText(ascendAndLevel);
        this.parent = parent;
    }

    public void init_for_popup(Image iv, String buildName,String ascendAndLevel,int id, Consumer<Integer> callback){
        banner.setImage(iv);

        this.buildName.setText(buildName);
        //this.ascendAndLevel.setText(ascendAndLevel+" lvl.95");
        this.ascendAndLevel.setText(ascendAndLevel);
        this.buildSelectorCallback = callback;
        this.id_for_popup = id;
    }

    public AnchorPane getRoot(){
        return root;
    }

    public void reset(boolean validationColor){
        //root.setStyle("-fx-background-color: transparent;"
                //+"-fx-border-style: solid;");
        if(validationColor){
            root.setStyle("color: transparent;");

            //buildName.setTextFill(Color.BLACK);
            //ascendAndLevel.setTextFill(Color.web("#656565"));
        }
        else
            root.setStyle("color: rgba(217, 215, 215, 0.8);");
    }

    public void setValidBackgroundColor(boolean validationColor,boolean isBuildActive){
        //root.setStyle("-fx-background-color: transparent;"
                //+"-fx-border-style: solid;");
        if(!isBuildActive){//so if build is not active set to transparent or gray.
            if(validationColor)
                root.setStyle("color: transparent;");
            else
                root.setStyle("color: rgba(217, 215, 215, 0.8);");
        }

    }

    public void initInvalidBackgroundColor(){
        root.setStyle("color: rgba(217, 215, 215, 0.8);");
    }

    public void initDisabledBuild(){
        isDisabledInLauncher = true;
        BoxBlur b = new BoxBlur();
        b.setWidth(5.0);
        b.setHeight(5.0);
        b.setIterations(1);
        Blend bl = new Blend();
        bl.setMode(BlendMode.SRC_OVER);
        bl.setOpacity(1.0);
        bl.setTopInput(b);
        disabledPanel.setEffect(b);
        //disabledPanel.setVisible(true);
        nonValidPanel.setVisible(true);
    }

    private void onPress() {
        //root.setStyle("-fx-background-color: PeachPuff;"
                //+"-fx-border-style: solid;");
        if(parent!=null){
            //root.setStyle("color: rgba(58, 44, 189, 0.4);"); old violet color
            //buildName.setTextFill(Color.WHITE);
            //ascendAndLevel.setTextFill(Color.web("#f6f6f6"));
            //buildName.setTextFill(Color.web("#0076a3"));
            root.setStyle("color: rgba(0, 150, 201, 1);"); //new blue for consistency

            parent.update();
        }
        if(buildSelectorCallback != null && !isDisabledInLauncher) {
            buildSelectorCallback.accept(id_for_popup);
        }


    }

}
