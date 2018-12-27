/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class Preferences_Controller implements Initializable {

    @FXML
    private JFXToggleButton toggle;
    @FXML
    private JFXSlider sliderZones;
    @FXML
    private JFXSlider sliderLevel;
    @FXML 
    private Label hideText;
    @FXML
    private TextField show_hide_hotkey_zone;
    @FXML
    private TextField remind_gems;
    @FXML
    private JFXTextField poe_installation;
    /**
     * Initializes the controller class.
     */
    
    private Main_Controller root;
    private String zones_hotkey_show_hide;
    private String level_hotkey_remind;
    private String key_bind = "";
    private String directory;
    
    public static boolean zones_toggle;
    public static double zones_slider;
    public static double level_slider;
    
    public static KeyCombination zones_hotkey_show_hide_key;
    public static KeyCombination level_hotkey_remind_key;
    public static String poe_log_dir;
    
    public void hook(Main_Controller root){
        this.root = root;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        Properties prop = new Properties();
	InputStream input = null;

	try {
            
		input = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");

		// load a properties file
		prop.load(input);
                zones_toggle = Boolean.parseBoolean(prop.getProperty("zones-toggle"));
                zones_slider = Double.parseDouble(prop.getProperty("zones-slider"));
                zones_hotkey_show_hide = prop.getProperty("zones-hotkey-show_hide");
                level_slider = Double.parseDouble(prop.getProperty("level-slider"));
                level_hotkey_remind = prop.getProperty("level-hotkey-remind");
                directory = prop.getProperty("poe-dir");
                poe_log_dir = directory + "\\logs\\Client.txt";
                poe_installation.setText(directory);

                try{
                    KeyCombination keyCombination = KeyCombination.keyCombination(zones_hotkey_show_hide);
                    System.out.println("key code : " + keyCombination.getName());
                    zones_hotkey_show_hide_key = keyCombination;
                }catch(IllegalArgumentException e){
                    System.out.println(":incorect:");
                    zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
                }
                
                try{
                    KeyCombination keyCombination = KeyCombination.keyCombination(level_hotkey_remind);
                    System.out.println("key code : " + keyCombination.getName());
                    level_hotkey_remind_key = keyCombination;
                }catch(IllegalArgumentException e){
                    System.out.println(":incorect:");
                    level_hotkey_remind_key = KeyCombination.NO_MATCH;
                }
                
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
            if (input != null) {
                    try {
                            input.close();
                            
                            toggle.setSelected(zones_toggle);
                            if(zones_toggle){
                                sliderZones.setVisible(true);
                                hideText.setVisible(true);
                                
                                sliderZones.setValue(zones_slider);
                            }else{
                                sliderZones.setVisible(false);
                                hideText.setVisible(false);
                            }
                            
                            show_hide_hotkey_zone.setText(zones_hotkey_show_hide);
                            
                            sliderLevel.setValue(level_slider);
                            remind_gems.setText(level_hotkey_remind);
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
            }
	}

        show_hide_hotkey_zone.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.isAltDown()){
                    key_bind = "Alt+";
                    if(event.getCode().getName().equals("Alt")){
                        key_bind = "Alt";
                    }else{
                        key_bind += event.getCode().getName();
                    }
                }else if(event.isControlDown()){
                    key_bind = "Ctrl+";
                    if(event.getCode().getName().equals("Ctrl")){
                        key_bind = "Ctrl";
                    }else{
                        key_bind += event.getCode().getName();
                    }
                    
                }else if(event.isShiftDown()){
                    key_bind = "Shift+";
                    if(event.getCode().getName().equals("Shift")){
                        key_bind = "Shift";
                    }else{
                        key_bind += event.getCode().getName();
                    }
                    
                }else{
                    key_bind += event.getCode().getName();
                }
                try{
                    KeyCombination keyCombination = KeyCombination.keyCombination(key_bind);
                    show_hide_hotkey_zone.setText(key_bind);
                    System.out.println("key code : " + keyCombination.getName());
                    //zones_hotkey_show_hide_key = keyCombination;
                }catch(IllegalArgumentException e){
                    show_hide_hotkey_zone.setText("");
                    System.out.println(":incorect:");
                    //zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
                }finally{
                    key_bind = "";
                }


            }
        });
        /*
        show_hide_hotkey_zone.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                //show_hide_hotkey_zone.clear();
                if(!(event.isAltDown() || event.isControlDown() || event.isShiftDown())){
                    System.out.println("key_bind on release : " + key_bind);
                    show_hide_hotkey_zone.setText(key_bind);
                    key_bind = "";
                }
            }
        });*/
        
        remind_gems.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.isAltDown()){
                    key_bind = "Alt+";
                    if(event.getCode().getName().equals("Alt")){
                        key_bind = "Alt";
                    }else{
                        key_bind += event.getCode().getName();
                    }
                }else if(event.isControlDown()){
                    key_bind = "Ctrl+";
                    if(event.getCode().getName().equals("Ctrl")){
                        key_bind = "Ctrl";
                    }else{
                        key_bind += event.getCode().getName();
                    }
                    
                }else if(event.isShiftDown()){
                    key_bind = "Shift+";
                    if(event.getCode().getName().equals("Shift")){
                        key_bind = "Shift";
                    }else{
                        key_bind += event.getCode().getName();
                    }
                    
                }else{
                    key_bind += event.getCode().getName();
                }
                try{
                    KeyCombination keyCombination = KeyCombination.keyCombination(key_bind);
                    remind_gems.setText(key_bind);
                    System.out.println("key code : " + keyCombination.getName());
                    //level_hotkey_remind_key = keyCombination;
                }catch(IllegalArgumentException e){
                    remind_gems.setText("");
                    System.out.println(":incorect:");
                    //level_hotkey_remind_key = KeyCombination.NO_MATCH;
                }finally{
                    key_bind = "";
                }
            }
        });
                
    }    
    
    @FXML
    private void save(){
        Properties prop = new Properties();
            OutputStream output = null;

            try {

                    output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");

                    // set the properties value
                    String toggleS;
                    if(toggle.isSelected()){
                        toggleS = "true";
                        zones_toggle = true;
                    }else{
                        toggleS = "false";
                        zones_toggle = false;
                    }
                    zones_slider = sliderZones.getValue();
                    level_slider = sliderLevel.getValue();
                    prop.setProperty("zones-toggle", toggleS);
                    prop.setProperty("zones-slider", String.valueOf(zones_slider));
                    prop.setProperty("zones-hotkey-show_hide", show_hide_hotkey_zone.getText());
                    prop.setProperty("level-slider", String.valueOf(level_slider));
                    prop.setProperty("level-hotkey-remind", remind_gems.getText());
                    prop.setProperty("poe-dir",directory);
                    try{
                        KeyCombination keyCombination = KeyCombination.keyCombination(show_hide_hotkey_zone.getText());
                        System.out.println("key code : " + keyCombination.getName());
                        zones_hotkey_show_hide_key = keyCombination;
                    }catch(IllegalArgumentException e){
                        System.out.println(":incorect:");
                        zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
                    }

                    try{
                        KeyCombination keyCombination = KeyCombination.keyCombination(remind_gems.getText());
                        System.out.println("key code : " + keyCombination.getName());
                        level_hotkey_remind_key = keyCombination;
                    }catch(IllegalArgumentException e){
                        System.out.println(":incorect:");
                        level_hotkey_remind_key = KeyCombination.NO_MATCH;
                    }
                    
                    // save properties to project root folder
                    prop.store(output, null);

            } catch (IOException io) {
                    io.printStackTrace();
            } finally {
                    if (output != null) {
                            try {
                                    output.close();
                            } catch (IOException e) {
                                    e.printStackTrace();
                            }
                    }
                    root.closePrefPopup();

            }
    }
    
    @FXML
    private void onToggle(){
        if(toggle.isSelected()){
            sliderZones.setVisible(true);
            hideText.setVisible(true);
            sliderZones.setValue(zones_slider);
        }else{
            sliderZones.setVisible(false);
            hideText.setVisible(false);
        }
    }
    
    @FXML
    private void locateLog(){
        
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Locate Path of Exile Installation Folder"); 
        File selectedDirectory = directoryChooser.showDialog(root.parent.getScene().getWindow());

        if(selectedDirectory == null){
             //No Directory selected
        }else{
             directory = selectedDirectory.getAbsolutePath();
             System.out.println(selectedDirectory.getAbsolutePath());
             //String directoryA = directory + "\\logs\\Client.txt";
             System.out.println(directory + "\\logs\\Client.txt");
             //System.out.println(directoryA);
             poe_installation.setText(directory);
        }
    }
    
}
