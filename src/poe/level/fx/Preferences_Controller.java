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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private TextField mark_recipe_hotkey;
    @FXML
    private TextField remind_gems;
    @FXML
    private JFXTextField poe_installation;
    
    @FXML
    private JFXToggleButton text_toggle;
    @FXML
    private JFXToggleButton images_toggle;
    @FXML
    private JFXToggleButton passive_toggle;
    @FXML
    private JFXToggleButton trial_toggle;
    @FXML
    private JFXToggleButton recipe_toggle;
    /**
     * Initializes the controller class.
     */
    
    private Main_Controller root;
    private String zones_hotkey_show_hide;
    private String level_hotkey_remind;
    private String recipe_hotkey_mark;
    private String key_bind = "";
    private String directory;
    
    public static boolean zones_toggle;
    
    public static boolean zones_text_toggle;
    public static boolean zones_images_toggle;
    
    public static boolean zones_passive_toggle;
    public static boolean zones_trial_toggle;
    public static boolean zones_recipe_toggle;
    
    public static double zones_slider;
    public static double level_slider;
    
    public static KeyCombination zones_hotkey_show_hide_key;
    public static KeyCombination level_hotkey_remind_key;
    public static KeyCombination recipe_hotkey_mark_key;
    
    private KeyCombination zones_hotkey_show_hide_key_tmp;
    private KeyCombination level_hotkey_remind_key_tmp;
    private KeyCombination recipe_hotkey_mark_key_tmp;
    
    
    public static String poe_log_dir;
    
    public static double[] zones_overlay_pos;
    public static double[] level_overlay_pos;
    public static double[] gem_overlay_pos;
    
    public static void updateZonesPos(double x, double y){
        if(zones_overlay_pos == null){
            zones_overlay_pos = new double[2];
        }
        zones_overlay_pos[0] = x;
        zones_overlay_pos[1] = y;
        
        //replace the changes in prop file
        FileInputStream in = null;
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        props.setProperty("zones-overlay-pos", zones_overlay_pos[0] + "," + zones_overlay_pos[1]);
        try {
            props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void updateLevelPos(double x, double y){
        if(level_overlay_pos == null){
            level_overlay_pos = new double[2];
        }
        level_overlay_pos[0] = x;
        level_overlay_pos[1] = y;
        
        //replace the changes in prop file
        FileInputStream in = null;
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        props.setProperty("level-overlay-pos", level_overlay_pos[0] + "," + level_overlay_pos[1]);
        try {
            props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void updateGemsPos(double x, double y){
        if(gem_overlay_pos == null){
            gem_overlay_pos = new double[2];
        }
        gem_overlay_pos[0] = x;
        gem_overlay_pos[1] = y;
        
        //replace the changes in prop file
        FileInputStream in = null;
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        props.setProperty("gems-overlay-pos", gem_overlay_pos[0] + "," + gem_overlay_pos[1]);
        try {
            props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void updateRecipeFile(String zoneName){ // default is replace with true the false value
        
        //replace the changes in prop file
        FileInputStream in = null;
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        props.setProperty(zoneName, "true");
        try {
            props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void hook(Main_Controller root){
        this.root = root;
    }
    
    //i think i dont need to load and save overlay positions here.
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
                zones_text_toggle = Boolean.parseBoolean(prop.getProperty("zones-text-toggle"));
                zones_images_toggle = Boolean.parseBoolean(prop.getProperty("zones-images-toggle"));
                zones_passive_toggle = Boolean.parseBoolean(prop.getProperty("zones-passive-toggle"));
                String parseTrial = prop.getProperty("zones-trial-toggle");
                    if(parseTrial == null){
                        zones_trial_toggle = true; //default
                    }else{
                        zones_trial_toggle = Boolean.parseBoolean(prop.getProperty("zones-trial-toggle"));
                    }
                String parseRecipe = prop.getProperty("zones-recipe-toggle");
                    if(parseRecipe == null){
                        zones_recipe_toggle = true; //default
                    }else{
                        zones_recipe_toggle = Boolean.parseBoolean(prop.getProperty("zones-recipe-toggle"));
                    }
                zones_slider = Double.parseDouble(prop.getProperty("zones-slider"));
                zones_hotkey_show_hide = prop.getProperty("zones-hotkey-show_hide");
                level_slider = Double.parseDouble(prop.getProperty("level-slider"));
                level_hotkey_remind = prop.getProperty("level-hotkey-remind");
                recipe_hotkey_mark = prop.getProperty("recipe-hotkey-mark");
                if(recipe_hotkey_mark == null) recipe_hotkey_mark = "F6";
                directory = prop.getProperty("poe-dir");
                if(!(directory==null || directory.equals(""))){
                    poe_log_dir = directory + "\\logs\\Client.txt";
                    poe_installation.setText(directory);
                }
                

                try{
                    KeyCombination keyCombination = KeyCombination.keyCombination(zones_hotkey_show_hide);
                    System.out.println("key code : " + keyCombination.getName());
                    zones_hotkey_show_hide_key = keyCombination;
                }catch(Exception e){
                    System.out.println(":incorect:");
                    zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
                }
                
                try{
                    KeyCombination keyCombination = KeyCombination.keyCombination(level_hotkey_remind);
                    System.out.println("key code : " + keyCombination.getName());
                    level_hotkey_remind_key = keyCombination;
                }catch(Exception e){
                    System.out.println(":incorect:");
                    level_hotkey_remind_key = KeyCombination.NO_MATCH;
                }
                
                try{
                    KeyCombination keyCombination = KeyCombination.keyCombination(recipe_hotkey_mark);
                    System.out.println("key code : " + keyCombination.getName());
                    recipe_hotkey_mark_key = keyCombination;
                }catch(Exception e){
                    System.out.println(":incorect:");
                    recipe_hotkey_mark_key = KeyCombination.NO_MATCH;
                }
                zones_hotkey_show_hide_key_tmp = zones_hotkey_show_hide_key;
                level_hotkey_remind_key_tmp = level_hotkey_remind_key;
                recipe_hotkey_mark_key_tmp = recipe_hotkey_mark_key;
                
                
	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
            if (input != null) {
                    try {
                            input.close();
                            
                            toggle.setSelected(zones_toggle);
                            text_toggle.setSelected(zones_text_toggle);
                            images_toggle.setSelected(zones_images_toggle);
                            trial_toggle.setSelected(zones_trial_toggle);
                            recipe_toggle.setSelected(zones_recipe_toggle);
                            passive_toggle.setSelected(zones_passive_toggle);
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
                            mark_recipe_hotkey.setText(recipe_hotkey_mark);
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
                    zones_hotkey_show_hide_key_tmp = KeyCombination.keyCombination(key_bind);
                    isBeingUsed(zones_hotkey_show_hide_key_tmp,0);
                    show_hide_hotkey_zone.setText(key_bind);
                    System.out.println("key code : " + zones_hotkey_show_hide_key_tmp.getName());
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
                    level_hotkey_remind_key_tmp = KeyCombination.keyCombination(key_bind);
                    isBeingUsed(level_hotkey_remind_key_tmp,1);
                    remind_gems.setText(key_bind);
                    System.out.println("key code : " + level_hotkey_remind_key_tmp.getName());
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
        
        mark_recipe_hotkey.setOnKeyPressed(new EventHandler<KeyEvent>() {
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
                    recipe_hotkey_mark_key_tmp = KeyCombination.keyCombination(key_bind);
                    isBeingUsed(recipe_hotkey_mark_key_tmp,2);
                    mark_recipe_hotkey.setText(key_bind);
                    
                    System.out.println("key code : " + recipe_hotkey_mark_key_tmp.getName());
                    //level_hotkey_remind_key = keyCombination;
                }catch(IllegalArgumentException e){
                    mark_recipe_hotkey.setText("");
                    System.out.println(":incorect:");
                    //level_hotkey_remind_key = KeyCombination.NO_MATCH;
                }finally{
                    key_bind = "";
                }
            }
        });
                
    }    

    private boolean checkEquality(String[] s1, String[] s2) {
        if (s1 == s2)
                return true;

        if (s1 == null || s2 == null)
                return false;

        int n = s1.length;
        if (n != s2.length)
                return false;

        for (int i = 0; i < n; i++) {
                if (!s1[i].equals(s2[i]))
                        return false;
        }

        return true;
    }
    
    private boolean isBeingUsed(KeyCombination key, int nodeID){
        //this will get messy fast if we add more keybinds but for now it works
        String[] input = key.toString().split("\\+");
        String[] zone = null;
        String[] level = null;
        String[] recipe = null;
        if(zones_hotkey_show_hide_key_tmp!=null)
        zone = zones_hotkey_show_hide_key_tmp.toString().split("\\+");
        if(level_hotkey_remind_key_tmp!=null)
        level = level_hotkey_remind_key_tmp.toString().split("\\+");
        if(recipe_hotkey_mark_key_tmp!=null)
        recipe = recipe_hotkey_mark_key_tmp.toString().split("\\+");
        switch (nodeID) {
            case 0:
                if(checkEquality(input,level)){
                    remind_gems.clear();
                    level_hotkey_remind_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,recipe)){
                    mark_recipe_hotkey.clear();
                    recipe_hotkey_mark_key_tmp = null;
                    return true;
                }
                return false;
            case 1:
                if(checkEquality(input,zone)) {
                    show_hide_hotkey_zone.clear();
                    zones_hotkey_show_hide_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,recipe)){
                    mark_recipe_hotkey.clear();
                    recipe_hotkey_mark_key_tmp = null;
                    return true;
                }
                return false;
            case 2:
                if(checkEquality(input,zone)){
                    show_hide_hotkey_zone.clear();
                    zones_hotkey_show_hide_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,level)){
                    remind_gems.clear();
                    level_hotkey_remind_key_tmp = null;
                    return true;
                }
                return false;
            default:
                break;
        }
        return false;
    }
    
    @FXML
    private void save(){
        //replace the changes in prop file this is to AVOID OVERWRITE, AND ONLY APPEND
        Properties prop = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        prop = new Properties();
        try {
            prop.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            in.close();
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
            OutputStream output = null;

            try {


                    // set the properties value
                    String toggleS;
                    if(toggle.isSelected()){
                        toggleS = "true";
                        zones_toggle = true;
                    }else{
                        toggleS = "false";
                        zones_toggle = false;
                    }
                    String toggleS_text;
                    if(text_toggle.isSelected()){
                        toggleS_text = "true";
                        zones_text_toggle = true;
                    }else{
                        toggleS_text = "false";
                        zones_text_toggle = false;
                    }
                    String toggleS_images;
                    if(images_toggle.isSelected()){
                        toggleS_images = "true";
                        zones_images_toggle = true;
                    }else{
                        toggleS_images = "false";
                        zones_images_toggle = false;
                    }
                    String toggleS_trial;
                    if(trial_toggle.isSelected()){
                        toggleS_trial = "true";
                        zones_trial_toggle = true;
                    }else{
                        toggleS_trial = "false";
                        zones_trial_toggle = false;
                    }
                    String toggleS_recipe;
                    if(recipe_toggle.isSelected()){
                        toggleS_recipe = "true";
                        zones_recipe_toggle = true;
                    }else{
                        toggleS_recipe = "false";
                        zones_recipe_toggle = false;
                    }
                    String toggleS_passive;
                    if(passive_toggle.isSelected()){
                        toggleS_passive = "true";
                        zones_passive_toggle = true;
                    }else{
                        toggleS_passive = "false";
                        zones_passive_toggle = false;
                    }
                    zones_slider = sliderZones.getValue();
                    level_slider = sliderLevel.getValue();
                    prop.setProperty("zones-toggle", toggleS);
                    prop.setProperty("zones-text-toggle", toggleS_text);
                    prop.setProperty("zones-images-toggle", toggleS_images);
                    prop.setProperty("zones-trial-toggle", toggleS_trial);
                    prop.setProperty("zones-recipe-toggle", toggleS_recipe);
                    prop.setProperty("zones-passive-toggle", toggleS_passive);
                    prop.setProperty("zones-slider", String.valueOf(zones_slider));
                    prop.setProperty("zones-hotkey-show_hide", show_hide_hotkey_zone.getText());
                    prop.setProperty("level-slider", String.valueOf(level_slider));
                    prop.setProperty("level-hotkey-remind", remind_gems.getText());
                    prop.setProperty("recipe-hotkey-mark", mark_recipe_hotkey.getText());
                    poe_log_dir = directory + "\\logs\\Client.txt";
                    prop.setProperty("poe-dir",directory);
                    try{
                        KeyCombination keyCombination = KeyCombination.keyCombination(show_hide_hotkey_zone.getText());
                        System.out.println(" Saved key code zones: " + keyCombination.getName());
                        zones_hotkey_show_hide_key = keyCombination;
                    }catch(Exception e){
                        System.out.println(":incorect:");
                        zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
                    }

                    try{
                        KeyCombination keyCombination = KeyCombination.keyCombination(remind_gems.getText());
                        System.out.println(" Saved key code gems: " + keyCombination.getName());
                        level_hotkey_remind_key = keyCombination;
                    }catch(Exception e){
                        System.out.println(":incorect:");
                        level_hotkey_remind_key = KeyCombination.NO_MATCH;
                    }
                    
                    try{
                        KeyCombination keyCombination = KeyCombination.keyCombination(mark_recipe_hotkey.getText());
                        System.out.println(" Saved key code recipes: " + keyCombination.getName());
                        recipe_hotkey_mark_key = keyCombination;
                    }catch(Exception e){
                        System.out.println(":incorect:");
                        recipe_hotkey_mark_key = KeyCombination.NO_MATCH;
                    }
                    
                    output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
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
             //do something ?
             
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
