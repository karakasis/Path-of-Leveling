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
import java.security.Key;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import poe.level.data.ActHandler;
import poe.level.data.Controller;
import poe.level.data.Zone;
import poe.level.keybinds.GlobalKeyListener;

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
    private TextField recipe_preview_hotkey;
    @FXML
    private TextField next_gem_hotkey;
    @FXML
    private TextField previous_gem_hotkey;
    @FXML
    private TextField lock_keybinds_hotkey;

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
    @FXML
    private JFXToggleButton betaGemUItoggle;

    @FXML
    private AnchorPane betaHotkey_pane;

    //API

    @FXML
    private JFXTextField txtAccountName;

    public static String poe_account_name;
    //

    private Main_Controller root;
    private String key_bind = "";
    private String directory;

    public static boolean zones_toggle;

    public static boolean zones_text_toggle;
    public static boolean zones_images_toggle;

    public static boolean zones_passive_toggle;
    public static boolean zones_trial_toggle;
    public static boolean zones_recipe_toggle;

    public static boolean gem_UI_toggle;

    public static double zones_slider;
    public static double level_slider;

    public static KeyCombination zones_hotkey_show_hide_key;
    public static KeyCombination level_hotkey_remind_key;
    public static KeyCombination recipe_hotkey_mark_key;
    public static KeyCombination recipe_hotkey_preview_key;
    public static KeyCombination level_hotkey_beta_next_key;
    public static KeyCombination level_hotkey_beta_previous_key;
    public static KeyCombination lock_keybinds_hotkey_key;

    private KeyCombination zones_hotkey_show_hide_key_tmp;
    private KeyCombination level_hotkey_remind_key_tmp;
    private KeyCombination recipe_hotkey_mark_key_tmp;
    public static KeyCombination recipe_hotkey_preview_key_tmp;
    public static KeyCombination level_hotkey_beta_next_key_tmp;
    public static KeyCombination level_hotkey_beta_previous_key_tmp;
    public static KeyCombination lock_keybinds_hotkey_key_tmp;


    public static String poe_log_dir;

    public static double[] zones_overlay_pos;
    public static double[] level_overlay_pos;
    public static double[] gem_overlay_pos;

    public static boolean gameModeOn;
    private HashMap<String,String> hotkeyDefaults;
    private Controller parent_gameModeOn;

    public void hookGameModeOn(Controller parent_gameModeOn){
        this.parent_gameModeOn = parent_gameModeOn;
    }

    public Preferences_Controller() {
    }

    public static void updateZonesPos(double x, double y){
        System.out.println("updateZonesPos x:" + x + " y: " + y);
        if(zones_overlay_pos == null){
            zones_overlay_pos = new double[2];
        }
        zones_overlay_pos[0] = x;
        zones_overlay_pos[1] = y;

        //replace the changes in prop file
        FileInputStream in = null;
        Properties props = new Properties();
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
            props.setProperty("zones-overlay-pos", zones_overlay_pos[0] + "," + zones_overlay_pos[1]);
            props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public static void updateLevelPos(double x, double y){
        System.out.println("updateLevelPos x:" + x + " y: " + y);
        if(level_overlay_pos == null){
            level_overlay_pos = new double[2];
        }
        level_overlay_pos[0] = x;
        level_overlay_pos[1] = y;

        //replace the changes in prop file
        FileInputStream in = null;
        Properties props = new Properties();
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
            props.setProperty("level-overlay-pos", level_overlay_pos[0] + "," + level_overlay_pos[1]);
            props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }


    }

    public static void updateGemsPos(double x, double y){
        System.out.println("updateGemsPos x:" + x + " y: " + y);
        if(gem_overlay_pos == null){
            gem_overlay_pos = new double[2];
        }
        gem_overlay_pos[0] = x;
        gem_overlay_pos[1] = y;

        //replace the changes in prop file
        FileInputStream in = null;
        Properties props = new Properties();
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
            props.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
            props.setProperty("gems-overlay-pos", gem_overlay_pos[0] + "," + gem_overlay_pos[1]);
            props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    public static void updateRecipeFile(String zoneName){ // default is replace with true the false value

        //replace the changes in prop file
        FileInputStream in = null;
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
            props.setProperty(zoneName, "true");
            props.store(out, null);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    public static void resetRecipesFile(){
        Properties prop = new Properties();
        OutputStream output = null;
        try{
            output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
        }

        for(Zone z : ActHandler.getInstance().getZonesWithRecipes()){
            String thanksGGG = z.name + " [L" + z.getZoneLevel() + "]";
            System.out.println(thanksGGG);
            prop.setProperty(thanksGGG, "false");
            ActHandler.getInstance().recipeMap.put(z, false);
        }

        try {
            // save properties to project root folder
            prop.store(output, null);
            System.out.println("Recipe properties file created successfully in " + POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void hook(Main_Controller root){
        this.root = root;
    }

    //i think i dont need to load and save overlay positions here.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.err.println("controller init called");
    gameModeOn = false;

    hotkeyDefaults = new HashMap<>();
    hotkeyDefaults.put("zones-hotkey-show_hide","F4");
    hotkeyDefaults.put("level-hotkey-remind","F5");
    hotkeyDefaults.put("recipe-hotkey-mark","F6");
    hotkeyDefaults.put("recipe-hotkey-preview","F7");
    hotkeyDefaults.put("level-hotkey-beta-next","F8");
    hotkeyDefaults.put("level-hotkey-beta-previous","F9");
    hotkeyDefaults.put("lock-keybinds","F12");

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
        String parseGemUI = prop.getProperty("gem-beta-UI-toggle");
        if(parseGemUI == null){
            gem_UI_toggle = true; //default
        }else{
            gem_UI_toggle = Boolean.parseBoolean(prop.getProperty("gem-beta-UI-toggle"));
        }

        zones_slider = Double.parseDouble(prop.getProperty("zones-slider"));
        level_slider = Double.parseDouble(prop.getProperty("level-slider"));

        directory = prop.getProperty("poe-dir");
        if(!(directory==null || directory.equals(""))){
            poe_log_dir = directory + "\\logs\\Client.txt";
            poe_installation.setText(directory);
        }

        //API
        poe_account_name = prop.getProperty("poe-account-name", "");
        //API

        zones_hotkey_show_hide_key = loadKeybinds(
                prop
                ,"zones-hotkey-show_hide"
                ,show_hide_hotkey_zone
        );
        zones_hotkey_show_hide_key_tmp = zones_hotkey_show_hide_key ;

        level_hotkey_remind_key = loadKeybinds(
                prop
                ,"level-hotkey-remind"
                ,remind_gems
        );
        level_hotkey_remind_key_tmp = level_hotkey_remind_key;

        recipe_hotkey_mark_key = loadKeybinds(
                prop
                ,"recipe-hotkey-mark"
                ,mark_recipe_hotkey
        );
        recipe_hotkey_mark_key_tmp = recipe_hotkey_mark_key;

        recipe_hotkey_preview_key = loadKeybinds(
                prop
                ,"recipe-hotkey-preview"
                ,recipe_preview_hotkey
        );
        recipe_hotkey_preview_key_tmp = recipe_hotkey_preview_key;

        level_hotkey_beta_next_key = loadKeybinds(
                prop
                ,"level-hotkey-beta-next"

                ,next_gem_hotkey
        );
        level_hotkey_beta_next_key_tmp = level_hotkey_beta_next_key;

        level_hotkey_beta_previous_key = loadKeybinds(
                prop
                ,"level-hotkey-beta-previous"
                ,previous_gem_hotkey
        );
        level_hotkey_beta_previous_key_tmp = level_hotkey_beta_previous_key;

        lock_keybinds_hotkey_key = loadKeybinds(
                prop
                ,"lock-keybinds"
                ,lock_keybinds_hotkey
        );
        lock_keybinds_hotkey_key_tmp = lock_keybinds_hotkey_key;

	} catch (IOException ex) {
		ex.printStackTrace();
	} finally {
            if (input != null) {
                    try {
                            input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            toggle.setSelected(zones_toggle);
            text_toggle.setSelected(zones_text_toggle);
            images_toggle.setSelected(zones_images_toggle);
            trial_toggle.setSelected(zones_trial_toggle);
            recipe_toggle.setSelected(zones_recipe_toggle);
            passive_toggle.setSelected(zones_passive_toggle);
            betaGemUItoggle.setSelected(gem_UI_toggle);
            betaHotkey_pane.setVisible(gem_UI_toggle);

            //API
            txtAccountName.setText(poe_account_name);
            //API

            if(zones_toggle){
                sliderZones.setVisible(true);
                hideText.setVisible(true);

                sliderZones.setValue(zones_slider);
            }else{
                sliderZones.setVisible(false);
                hideText.setVisible(false);
            }
            sliderLevel.setValue(level_slider);
	}

        show_hide_hotkey_zone.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                zones_hotkey_show_hide_key_tmp = handleKeybindEdit(
                        event
                        ,show_hide_hotkey_zone
                        ,0
                );
            }
        });

        remind_gems.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                level_hotkey_remind_key_tmp = handleKeybindEdit(
                        event
                        ,remind_gems
                        ,1
                );
            }
        });

        mark_recipe_hotkey.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                recipe_hotkey_mark_key_tmp = handleKeybindEdit(
                         event
                        ,mark_recipe_hotkey
                        ,2
                );
            }
        });

        recipe_preview_hotkey.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                recipe_hotkey_preview_key_tmp = handleKeybindEdit(
                        event
                        ,recipe_preview_hotkey
                        ,3
                );
            }
        });

        next_gem_hotkey.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                level_hotkey_beta_next_key_tmp = handleKeybindEdit(
                        event
                        ,next_gem_hotkey
                        ,4
                );
            }
        });

        previous_gem_hotkey.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                level_hotkey_beta_previous_key_tmp = handleKeybindEdit(
                        event
                        ,previous_gem_hotkey
                        ,5
                );
            }
        });

        lock_keybinds_hotkey.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                lock_keybinds_hotkey_key_tmp = handleKeybindEdit(
                        event
                        ,lock_keybinds_hotkey
                        ,6
                );
            }
        });

    }

    private KeyCombination loadKeybinds(Properties prop, String propertyName, TextField kc_field){
        String loadProp = prop.getProperty(propertyName);
        if(loadProp == null) loadProp = hotkeyDefaults.get(propertyName);
        KeyCombination keyCombination;
        try{
            keyCombination = KeyCombination.keyCombination(loadProp);
            System.out.println("key code : " + keyCombination.getName());
        }catch(Exception e){
            System.out.println(":incorect:");
            keyCombination = KeyCombination.NO_MATCH;
        }
        kc_field.setText(loadProp);
        return keyCombination;
    }

    private KeyCombination saveKeybinds(Properties prop, String propertyName, String kc_field_text){
        prop.setProperty(propertyName, kc_field_text);
        KeyCombination keyCombination;
        try{
            keyCombination = KeyCombination.keyCombination(kc_field_text);
            System.out.println(" Saved key code zones: " + keyCombination.getName());
        }catch(Exception e){
            System.out.println(":incorect:");
            keyCombination = KeyCombination.NO_MATCH;
        }
        return keyCombination;
    }

    private KeyCombination handleKeybindEdit(KeyEvent event, TextField kc_field, int nodeID){
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
        KeyCombination kc_temp;
        try{
            kc_temp = KeyCombination.keyCombination(key_bind);
            isBeingUsed(kc_temp,nodeID);
            kc_field.setText(key_bind);
            System.out.println("key code : " + kc_temp.getName());
            //zones_hotkey_show_hide_key = keyCombination;
        }catch(IllegalArgumentException e){
            kc_field.setText("");
            System.out.println(":incorect:");
            kc_temp = KeyCombination.NO_MATCH;
        }finally{
            key_bind = "";
        }
        return kc_temp;
    }

    private static boolean checkEquality(String[] s1, String[] s2) {
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
        String[] preview = null;
        String[] next = null;
        String[] previous = null;
        String[] lock = null;
        if(zones_hotkey_show_hide_key_tmp!=null)
            zone = zones_hotkey_show_hide_key_tmp.toString().split("\\+");
        if(level_hotkey_remind_key_tmp!=null)
            level = level_hotkey_remind_key_tmp.toString().split("\\+");
        if(recipe_hotkey_mark_key_tmp!=null)
            recipe = recipe_hotkey_mark_key_tmp.toString().split("\\+");
        if(recipe_hotkey_preview_key_tmp!=null)
            preview = recipe_hotkey_preview_key_tmp.toString().split("\\+");
        if(level_hotkey_beta_next_key_tmp!=null)
            next = level_hotkey_beta_next_key_tmp.toString().split("\\+");
        if(level_hotkey_beta_previous_key_tmp!=null)
            previous = level_hotkey_beta_previous_key_tmp.toString().split("\\+");
        if(lock_keybinds_hotkey_key_tmp!=null)
            lock = lock_keybinds_hotkey_key_tmp.toString().split("\\+");
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
                if(checkEquality(input,preview)){
                    recipe_preview_hotkey.clear();
                    recipe_hotkey_preview_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,next)){
                    next_gem_hotkey.clear();
                    level_hotkey_beta_next_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,previous)){
                    previous_gem_hotkey.clear();
                    level_hotkey_beta_previous_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,lock)){
                    lock_keybinds_hotkey.clear();
                    lock_keybinds_hotkey_key_tmp = null;
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
                if(checkEquality(input,preview)){
                    recipe_preview_hotkey.clear();
                    recipe_hotkey_preview_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,next)){
                    next_gem_hotkey.clear();
                    level_hotkey_beta_next_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,previous)){
                    previous_gem_hotkey.clear();
                    level_hotkey_beta_previous_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,lock)){
                    lock_keybinds_hotkey.clear();
                    lock_keybinds_hotkey_key_tmp = null;
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
                if(checkEquality(input,preview)){
                    recipe_preview_hotkey.clear();
                    recipe_hotkey_preview_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,next)){
                    next_gem_hotkey.clear();
                    level_hotkey_beta_next_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,previous)){
                    previous_gem_hotkey.clear();
                    level_hotkey_beta_previous_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,lock)){
                    lock_keybinds_hotkey.clear();
                    lock_keybinds_hotkey_key_tmp = null;
                    return true;
                }
                return false;
            case 3:
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
                if(checkEquality(input,recipe)){
                    mark_recipe_hotkey.clear();
                    recipe_hotkey_mark_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,next)){
                    next_gem_hotkey.clear();
                    level_hotkey_beta_next_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,previous)){
                    previous_gem_hotkey.clear();
                    level_hotkey_beta_previous_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,lock)){
                    lock_keybinds_hotkey.clear();
                    lock_keybinds_hotkey_key_tmp = null;
                    return true;
                }
                return false;
            case 4:
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
                if(checkEquality(input,previous)){
                    previous_gem_hotkey.clear();
                    level_hotkey_beta_previous_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,preview)){
                    recipe_preview_hotkey.clear();
                    recipe_hotkey_preview_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,recipe)){
                    mark_recipe_hotkey.clear();
                    recipe_hotkey_mark_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,lock)){
                    lock_keybinds_hotkey.clear();
                    lock_keybinds_hotkey_key_tmp = null;
                    return true;
                }
                return false;
            case 5:
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
                if(checkEquality(input,recipe)){
                    mark_recipe_hotkey.clear();
                    recipe_hotkey_mark_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,preview)){
                    recipe_preview_hotkey.clear();
                    recipe_hotkey_preview_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,next)){
                    next_gem_hotkey.clear();
                    level_hotkey_beta_next_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,lock)){
                    lock_keybinds_hotkey.clear();
                    lock_keybinds_hotkey_key_tmp = null;
                    return true;
                }
                return false;
            case 6:
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
                if(checkEquality(input,recipe)){
                    mark_recipe_hotkey.clear();
                    recipe_hotkey_mark_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,preview)){
                    recipe_preview_hotkey.clear();
                    recipe_hotkey_preview_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,next)){
                    next_gem_hotkey.clear();
                    level_hotkey_beta_next_key_tmp = null;
                    return true;
                }
                if(checkEquality(input,previous)){
                    previous_gem_hotkey.clear();
                    level_hotkey_beta_previous_key_tmp = null;
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
        Properties prop = new Properties();
        FileInputStream in = null;
        try {
            in = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
            prop.load(in);
        } catch (IOException ex) {
            Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(Preferences_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
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
                    String toggleS_gemUI;
                    boolean toggled_reset = false;
                    if(betaGemUItoggle.isSelected()){
                        if(!gem_UI_toggle){
                            toggled_reset = true;
                        }
                        toggleS_gemUI = "true";
                        gem_UI_toggle = true;
                    }else{
                        if(gem_UI_toggle){
                            toggled_reset = true;
                        }
                        toggleS_gemUI = "false";
                        gem_UI_toggle = false;
                    }
                    if(toggled_reset) parent_gameModeOn.gemUItoggled(gem_UI_toggle);
                    //changing the big decimal number to 1 decimal apparently
                    zones_slider = (int)sliderZones.getValue();
                    level_slider = (int)sliderLevel.getValue();
                    prop.setProperty("zones-toggle", toggleS);
                    prop.setProperty("zones-text-toggle", toggleS_text);
                    prop.setProperty("zones-images-toggle", toggleS_images);
                    prop.setProperty("zones-trial-toggle", toggleS_trial);
                    prop.setProperty("zones-recipe-toggle", toggleS_recipe);
                    prop.setProperty("zones-passive-toggle", toggleS_passive);
                    prop.setProperty("gem-beta-UI-toggle", toggleS_gemUI);

                    prop.setProperty("zones-slider", String.valueOf(zones_slider));
                    prop.setProperty("level-slider", String.valueOf(level_slider));

                    poe_log_dir = directory + "\\logs\\Client.txt";
                    prop.setProperty("poe-dir",directory);

                    //API
                    poe_account_name = txtAccountName.getText();
                    prop.setProperty("poe-account-name", poe_account_name);
                    //API

                    zones_hotkey_show_hide_key = saveKeybinds(prop,"zones-hotkey-show_hide",show_hide_hotkey_zone.getText());
                    level_hotkey_remind_key = saveKeybinds(prop,"level-hotkey-remind",remind_gems.getText());
                    recipe_hotkey_mark_key = saveKeybinds(prop,"recipe-hotkey-mark",mark_recipe_hotkey.getText());
                    recipe_hotkey_preview_key = saveKeybinds(prop,"recipe-hotkey-preview",recipe_preview_hotkey.getText());
                    level_hotkey_beta_next_key = saveKeybinds(prop,"level-hotkey-beta-next",next_gem_hotkey.getText());
                    level_hotkey_beta_previous_key = saveKeybinds(prop,"level-hotkey-beta-previous",previous_gem_hotkey.getText());
                    lock_keybinds_hotkey_key = saveKeybinds(prop,"lock-keybinds",lock_keybinds_hotkey.getText());


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
                    if(!gameModeOn)
                        root.closePrefPopup();
                    else
                        GlobalKeyListener.setUpKeybinds();

        }
    }

    @FXML
    private void cancel(){
        if(!gameModeOn)
            root.closePrefPopup();
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

    @FXML
    private void betaGemUItoggle_action(){
        if(betaGemUItoggle.isSelected()){
            gem_UI_toggle = true;
            betaHotkey_pane.setVisible(true);
        }else{
            gem_UI_toggle = false;
            betaHotkey_pane.setVisible(false);
        }
    }

}
