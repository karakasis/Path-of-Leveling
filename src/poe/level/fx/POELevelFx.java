/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.sun.javafx.application.LauncherImpl;
import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.*;
//import java.util.Base64;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.javafx.css.StyleManager;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import poe.level.PreloadNotification.GemDownloadNotification;
import poe.level.data.*;
import poe.level.data.Gem.Info;
import poe.level.keybinds.GlobalKeyListener;

/**
 *
 * @author Christos
 */
public class POELevelFx extends Application {
    //search for public void start(Stage stage) throws Exception for css

    /***************************************************************************
     * Change to true if a build is being pushed to the master branch for public release.
     ***************************************************************************/
    public static final boolean MASTER_RELEASE = false;
    /*************************************************************************************/
    /* Update this when you are pushing a new release version, must match the GitHub release tag name!!
    **************************************************************************************/
    public static final String version = "v0.74-beta";


    public static boolean DEBUG = false;

    private static final String DEBUG_BRANCH_NAME = "development";
    private static final String RELEASE_BRANCH_NAME = "master";
    public static String BRANCH_NAME = MASTER_RELEASE ? RELEASE_BRANCH_NAME : DEBUG_BRANCH_NAME;
    private static final String REPO_OWNER = "karakasis";
    public static String directory;
    public static String gemsJSONFileName;
    public static String gemsTimeFileName;
    public static String dataJSONFileName;
    public static String dataTimeFileName;
    public static String gemsIconsLocation;
    public static ArrayList<Build> buildsLoaded;
    private static final Logger m_logger = Logger.getLogger(POELevelFx.class.getName());
    private Stage zone;
    private Stage exp;
    private Stage main;
    private Stage editor;
    private Stage leveling;
    private static GithubHelper.ReleaseInfo newReleaseInfo = null;
    private static boolean is_new_version;
    //v0.5-alpha <- between

    public void update() {
        assert (newReleaseInfo != null);
        URL url;

        String outFileName = "PathOfLeveling-" + newReleaseInfo.version + ".jar";
        try {
            url = new URL(newReleaseInfo.downloadURL);
            HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
            BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
            FileOutputStream fos = new java.io.FileOutputStream(outFileName);
            int bufferSize = 8192;
            BufferedOutputStream bout = new BufferedOutputStream(fos, bufferSize);
            byte[] data = new byte[bufferSize];
            long downloadedFileSize = 0;
            int x;
            while ((x = in.read(data, 0, bufferSize)) >= 0) {
                if (UpdaterController.cancelDownload) {
                    bout.close();
                    in.close();
                    File file = new File(outFileName);
                    file.delete();
                    try {
                        init();
                    } catch (Exception ex) {
                        Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    break;
                }
                downloadedFileSize += x;

                notifyPreloader(new UpdatePreloader.ProgressNotification(downloadedFileSize));


                //System.out.println(downloadedFileSize);
                bout.write(data, 0, x);
            }
            bout.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private KeyCombination setKeybinds(Properties prop, String propertyName, String defaultValue){
        prop.setProperty(propertyName, defaultValue);
        KeyCombination keyCombination = null;
        try{
            keyCombination = KeyCombination.keyCombination(defaultValue);
            System.out.println("-POELevelFx- Setting keybind for : " + keyCombination.getName() +" for " + propertyName);
        }catch(Exception e){
            System.out.println("-POELevelFx- Setting keybind for :" + propertyName + " failed.");
            keyCombination = KeyCombination.NO_MATCH;
        }
        return keyCombination;
    }

    private KeyCombination loadKeybinds(Properties prop, String propertyName, String defaultValue){
        //check if hotkey is null, on older versions
        String loadProp = prop.getProperty(propertyName);
        KeyCombination keyCombination = null;
        //this should load the keybind on the controller but not overwrite
        if(loadProp == null){
            //loadProp = defaultValue; <- load default or
            return KeyCombination.NO_MATCH;
        }
        try{
            keyCombination = KeyCombination.keyCombination(loadProp);
            System.out.println("-POELevelFx- Loading keybind " + keyCombination.getName() +" for " + propertyName);
        }catch(Exception e){
            System.out.println("-POELevelFx- Loading keybind for " + propertyName+ " failed.");
            keyCombination = KeyCombination.NO_MATCH;
        }
        return keyCombination;
    }

    @Override
    public void init() throws Exception {

        boolean restart = false;
        setUpFonts();
        if(is_new_version){


            while(true) {
                if(UpdaterController.allowUpdate){
                    UpdaterController.allowUpdate = false;
                    update();
                    break;
                }
                if(UpdaterController.declUpdate){
                    is_new_version = false;
                    restart = true;
                    break;
                }
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ie) {
                    // ignore
                }
            }
            if(restart) {
                //LauncherImpl.launchApplication(POELevelFx.class, NewFXPreloader.class, null);
                init();
            }
            //System.exit(-10);
        }else{

            HashMap<String,String> hotkeyDefaults;
            //changed the default keys
            hotkeyDefaults = new HashMap<>();
            hotkeyDefaults.put("zones-hotkey-show_hide","F4");
            hotkeyDefaults.put("level-hotkey-remind","F5");
            hotkeyDefaults.put("recipe-hotkey-mark","Page Down");
            hotkeyDefaults.put("recipe-hotkey-preview","Page Up");
            hotkeyDefaults.put("level-hotkey-beta-next","Right");
            hotkeyDefaults.put("level-hotkey-beta-previous","Left");
            hotkeyDefaults.put("lock-keybinds","F12");

            if (!new File(POELevelFx.directory + "\\Path of Leveling\\config.properties").isFile()) {
            new File(POELevelFx.directory + "\\Path of Leveling\\config.properties").createNewFile();

            Properties prop = new Properties();
            OutputStream output = null;


            try {
                    output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");

                    // set the properties value
                    prop.setProperty("zones-toggle", "true");
                    Preferences_Controller.zones_toggle = true;
                    prop.setProperty("zones-text-toggle", "true");
                    Preferences_Controller.zones_text_toggle = true;
                    prop.setProperty("zones-trial-toggle", "true");
                    Preferences_Controller.zones_trial_toggle = true;
                    prop.setProperty("zones-passive-toggle", "true");
                    Preferences_Controller.zones_passive_toggle = true;
                    prop.setProperty("zones-recipe-toggle", "true");
                    Preferences_Controller.zones_recipe_toggle = true;
                    prop.setProperty("zones-images-toggle", "true");
                    Preferences_Controller.zones_images_toggle = true;
                    prop.setProperty("gem-beta-UI-toggle", "true");
                    Preferences_Controller.gem_UI_toggle = true;

                    double a = 10;
                    prop.setProperty("zones-slider", String.valueOf(a));
                    Preferences_Controller.zones_slider = a;

                    a = 15;
                    Preferences_Controller.level_slider = a;
                    prop.setProperty("level-slider", String.valueOf(a));

                    prop.setProperty("poe-dir","");
                    Preferences_Controller.poe_log_dir = "";

                Preferences_Controller.zones_hotkey_show_hide_key = setKeybinds(
                            prop
                            ,"zones-hotkey-show_hide"
                            ,hotkeyDefaults.get("zones-hotkey-show_hide")
                    );
                Preferences_Controller.level_hotkey_remind_key = setKeybinds(
                            prop
                            ,"level-hotkey-remind"
                            ,hotkeyDefaults.get("level-hotkey-remind")
                    );
                Preferences_Controller.recipe_hotkey_mark_key = setKeybinds(
                            prop
                            ,"recipe-hotkey-mark"
                            ,hotkeyDefaults.get("recipe-hotkey-mark")
                    );
                Preferences_Controller.recipe_hotkey_preview_key = setKeybinds(
                            prop
                            ,"recipe-hotkey-preview"
                            ,hotkeyDefaults.get("recipe-hotkey-preview")
                    );
                Preferences_Controller.level_hotkey_beta_next_key = setKeybinds(
                            prop
                            ,"level-hotkey-beta-next"
                            ,hotkeyDefaults.get("level-hotkey-beta-next")
                    );
                Preferences_Controller.level_hotkey_beta_previous_key = setKeybinds(
                            prop
                            ,"level-hotkey-beta-previous"
                            ,hotkeyDefaults.get("level-hotkey-beta-previous")
                    );
                Preferences_Controller.lock_keybinds_hotkey_key = setKeybinds(
                        prop
                        ,"lock-keybinds"
                        ,hotkeyDefaults.get("lock-keybinds")
                );


                    //new changes

                    prop.setProperty("gems-overlay-pos", "-200.0,-200.0");
                    Preferences_Controller.updateGemsPos(-200.0, -200.0);
                    prop.setProperty("level-overlay-pos", "-200.0,-200.0");
                    Preferences_Controller.updateLevelPos(-200.0, -200.0);
                    prop.setProperty("zones-overlay-pos", "-200.0,-200.0");
                    Preferences_Controller.updateZonesPos(-200.0, -200.0);

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

                }
            }else{
                    boolean patchKeybind = false;
                  Properties prop = new Properties();
                  InputStream input = null;
                  try {

                          input = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");

                          // load a properties file
                          prop.load(input);
                          Preferences_Controller.zones_toggle = Boolean.parseBoolean(prop.getProperty("zones-toggle"));
                          Preferences_Controller.zones_text_toggle = Boolean.parseBoolean(prop.getProperty("zones-text-toggle"));
                          Preferences_Controller.zones_images_toggle = Boolean.parseBoolean(prop.getProperty("zones-images-toggle"));

                          //this 3 settings were added in later versions so im trying to avoid errors.
                          String parseRecipe = prop.getProperty("zones-recipe-toggle");
                          if(parseRecipe == null){
                              Preferences_Controller.zones_recipe_toggle = true; //default
                          }else{
                              Preferences_Controller.zones_recipe_toggle = Boolean.parseBoolean(prop.getProperty("zones-recipe-toggle"));
                          }
                          String parseTrial = prop.getProperty("zones-trial-toggle");
                          if(parseTrial == null){
                              Preferences_Controller.zones_trial_toggle = true; //default
                          }else{
                              Preferences_Controller.zones_trial_toggle = Boolean.parseBoolean(prop.getProperty("zones-trial-toggle"));
                          }
                          String parseGemUI = prop.getProperty("gem-beta-UI-toggle");
                          if(parseTrial == null){
                              Preferences_Controller.gem_UI_toggle = true; //default
                          }else{
                              Preferences_Controller.gem_UI_toggle = Boolean.parseBoolean(prop.getProperty("gem-beta-UI-toggle"));
                          }

                          Preferences_Controller.zones_passive_toggle = Boolean.parseBoolean(prop.getProperty("zones-passive-toggle"));

                          Preferences_Controller.zones_slider = Double.parseDouble(prop.getProperty("zones-slider"));
                          Preferences_Controller.level_slider = Double.parseDouble(prop.getProperty("level-slider"));

                          if(!(prop.getProperty("poe-dir")==null || prop.getProperty("poe-dir").equals(""))){
                              Preferences_Controller.poe_log_dir = prop.getProperty("poe-dir") + "\\logs\\Client.txt";

                              System.out.println("Selected Path of exile log location : " + Preferences_Controller.poe_log_dir);
                              System.out.println(Preferences_Controller.poe_log_dir + "\\logs\\Client.txt");
                          }else{
                              System.out.println("Path of exile log location is not set.");
                          }

                          //API
                          Preferences_Controller.poe_account_name = prop.getProperty("poe-account-name", "");

                            //new changes to overlay positions persist
                            //a bug is introduced at this point. users with older versions will not have
                            //those lines in their prop files and a null error will pop up
                            //quick fix if null add the line manually from code.
                            String[] zones_pos = null;
                            try{
                                zones_pos = prop.getProperty("zones-overlay-pos").toString().split(",");
                            }catch(NullPointerException e){
                                //hopefully the update..Pos method will write the values without crashing.
                                //my only issue is that i am already opening the prop file in read mode, and then from within
                                //im calling a write function.
                                System.out.println("zones-overlay-pos was missing from config.properties and is manually added.");
                                zones_pos= new String[2];
                                zones_pos[0] = "-200.0";
                                zones_pos[1] = "-200.0";
                            }
                            Preferences_Controller.updateZonesPos(Double.parseDouble(zones_pos[0])
                                    , Double.parseDouble(zones_pos[1]));

                            String[] level_pos = null;
                            try{
                                 level_pos = prop.getProperty("level-overlay-pos").toString().split(",");
                            }catch(NullPointerException e){
                                System.out.println("level-overlay-pos was missing from config.properties and is manually added.");
                                level_pos= new String[2];
                                level_pos[0] = "-200.0";
                                level_pos[1] = "-200.0";
                            }
                            Preferences_Controller.updateLevelPos(Double.parseDouble(level_pos[0])
                                    , Double.parseDouble(level_pos[1]));

                            String[] gem_pos = null;
                            try{
                                gem_pos = prop.getProperty("gems-overlay-pos").toString().split(",");
                            }catch(NullPointerException e){
                                System.out.println("gems-overlay-pos was missing from config.properties and is manually added.");
                                gem_pos= new String[2];
                                gem_pos[0] = "-200.0";
                                gem_pos[1] = "-200.0";
                            }
                            Preferences_Controller.updateGemsPos(Double.parseDouble(gem_pos[0])
                                    , Double.parseDouble(gem_pos[1]));

                      Preferences_Controller.zones_hotkey_show_hide_key = loadKeybinds(
                                prop
                                ,"zones-hotkey-show_hide"
                                ,hotkeyDefaults.get("zones-hotkey-show_hide")
                        );
                      Preferences_Controller.level_hotkey_remind_key = loadKeybinds(
                              prop
                              ,"level-hotkey-remind"
                              ,hotkeyDefaults.get("level-hotkey-remind")
                      );
                      Preferences_Controller.recipe_hotkey_mark_key = loadKeybinds(
                              prop
                              ,"recipe-hotkey-mark"
                              ,hotkeyDefaults.get("recipe-hotkey-mark")
                      );
                      Preferences_Controller.recipe_hotkey_preview_key = loadKeybinds(
                              prop
                              ,"recipe-hotkey-preview"
                              ,hotkeyDefaults.get("recipe-hotkey-preview")
                      );
                      Preferences_Controller.level_hotkey_beta_next_key = loadKeybinds(
                              prop
                              ,"level-hotkey-beta-next"
                              ,hotkeyDefaults.get("level-hotkey-beta-next")
                      );
                      Preferences_Controller.level_hotkey_beta_previous_key = loadKeybinds(
                              prop
                              ,"level-hotkey-beta-previous"
                              ,hotkeyDefaults.get("level-hotkey-beta-previous")
                      );
                      Preferences_Controller.lock_keybinds_hotkey_key = loadKeybinds(
                              prop
                              ,"lock-keybinds"
                              ,hotkeyDefaults.get("lock-keybinds")
                      );
                      if(prop.containsKey("level-hotkey-beta-next") && prop.containsKey("level-hotkey-beta-previous"))
                      if(prop.getProperty("level-hotkey-beta-next").equals("Left")
                       && prop.getProperty("level-hotkey-beta-previous").equals("Right")) {
                          patchKeybind = true;
                      }

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
                  }
                  if(patchKeybind) {
                      OutputStream output = null;


                      try {
                          output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");
                          prop.setProperty("level-hotkey-beta-next","Right");
                          prop.setProperty("level-hotkey-beta-previous","Left");
                          prop.store(output, null);
                      }    // save properties to project root folder
                      catch (IOException io) {
                          io.printStackTrace();
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

                }


            // Only check for new JSON files when running in release mode
            if (!DEBUG) {
                checkForNewJSON();
            }

            //StringBuilder raw = readRawToString();
            try {
                loadActsFromMemory();
                loadGemsFromMemory();
                //GemHolder.getInstance().init_remaining_in_pool();
                loadBuildsFromMemory();
                loadRecipesProperties();
            } catch (Exception e) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, "Error during Path of Leveling start up: " + e.getMessage(), e);
                notifyPreloader(new Preloader.ErrorNotification("init", "Error during Path of Leveling start up: " + e.getMessage(), e));

            }

        }



        /*
              ArrayList<String[]> mergeTags = mergeTags();
              ArrayList<Gem> gems = GemHolder.getInstance().gems;
              boolean active = false;
              for(int i=0; i<mergeTags.size(); i++){
                  if(mergeTags.get(i)[0].equals("Abyssal Cry")){
                      active = true;
                  }
                  boolean found = false;
                  for(int j=0; j<gems.size(); j++){
                      if(mergeTags.get(i)[0].equals(gems.get(j).name)){
                          gems.get(j).isActive = active;
                          gems.get(j).isSupport = !active;
                          for(int k = 1 ; k< mergeTags.get(i).length; k++){
                              gems.get(j).tags.add( mergeTags.get(i)[k]);
                          }
                          found = true;
                          break;
                      }
                  }
                  if(!found){
                      System.out.println("not found : " + mergeTags.get(i)[0]);
                  }
                  found = false;
              }

              ArrayList<Gem> gems2 = GemHolder.getInstance().gems;
              for(Gem g : gems2){
                  if(g.isActive == g.isSupport){
                      System.out.println("same active : " + g.name);

                  }
                  if(g.tags == null || g.tags.size() == 0){
                      System.out.println("no tag : " + g.name);
                  }
              }
              System.out.println(gems2);

              GemHolder.getInstance().init_remaining_in_pool();
              */

     /*
        StringBuilder hack = hack();
        String[] split = hack.toString().split("\r\n|\t");

        ArrayList<String> s = new ArrayList<>();
        ArrayList<ArrayList<String>> s_col = new ArrayList<>();
        GemHolder.getInstance().pool();
        for(int i=0; i<split.length;i++){
            String name = split[i];
            s.add(name.replace("\\", ""));
            if(name.equals("Act 6 after Fallen from Grace from Lilly Roth with any character.")){
                s_col.add(s);
                GemHolder.getInstance().updateGemInfo(s);
                s = new ArrayList<>();
            }

        }

        GemHolder.getInstance().init_remaining_in_pool();


        s_col.size();
        */
    }

    private StringBuilder readRawToString(){
        BufferedReader br = null;
        StringBuilder sb = null;
        try {
            br = new BufferedReader(new FileReader("raw.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            sb = new StringBuilder();
            String line = null;
            try {
                line = br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                try {
                    line = br.readLine();
                } catch (IOException ex) {
                    Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            String everything = sb.toString();
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return sb;
    }

    private ArrayList<String[]> mergeTags(){
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("tags.txt"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
        }
        ArrayList<String[]> list_tags = new ArrayList<>();
        try {
            String line = null;
            try {

                line = br.readLine();
                while(line != null){
                    String[] tags = line.split(",");
                    list_tags.add(tags);
                    line = br.readLine();
                }
            } catch (IOException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }

        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list_tags;
    }

    public void close(){
        //exp.close();
        main.close();
        //zone.close();
    }

    private void loadRecipesProperties(){
        if (!new File(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties").isFile()) {
            try {
                new File(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties").createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }

            Properties prop = new Properties();
            OutputStream output = null;
            try{
                output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }

            for(Zone z : ActHandler.getInstance().getZonesWithRecipes()){
                String thanksGGG = z.name + " [L" + z.getZoneLevel() + "]";
                //System.out.println(thanksGGG);
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


        }else{
            Properties prop = new Properties();
            InputStream input = null;
            boolean writeStream = false;
            try {

                input = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");

                // load a properties file
                prop.load(input);
                for(Zone z : ActHandler.getInstance().getZonesWithRecipes()){
                    String thanksGGG = z.name + " [L" + z.getZoneLevel() + "]";
                    writeStream = writeStream || !prop.containsKey(thanksGGG);
                    String propVal = prop.getProperty(thanksGGG, "false");
                    prop.put(thanksGGG, propVal);
                    ActHandler.getInstance().recipeMap.put(z, Boolean.parseBoolean(propVal));
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (input != null) {
                    try {
                        input.close();
                        System.out.println("Recipe properties loaded successfully ");

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if(writeStream){
                System.out.println("Need to update recipes");
                OutputStream output = null;
                try{
                    output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");
                    prop.store(output, null);
                    System.out.println("Recipe properties file PATCHED successfully in " + POELevelFx.directory + "\\Path of Leveling\\recipesFound.properties");
                } catch (IOException ex) {
                    Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
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

        }
    }

    private void loadActsFromMemory() throws IOException {
        InputStream in = new FileInputStream(dataJSONFileName);
        Scanner s = new Scanner(in).useDelimiter("\\A");
        String jsonstring = s.hasNext() ? s.next() : "";


        JSONObject obj = new JSONObject(jsonstring);
        JSONArray arr = obj.getJSONArray("acts");

        //ActHandler actsH = new ActHandler();
        for (int i = 0; i < arr.length(); i++)
        {
            JSONObject actobj  = arr.getJSONObject(i);
            String actname = actobj.getString("act");
            int actid = actobj.getInt("actid");
            JSONArray zonesAr = actobj.getJSONArray("zones");
            Act a = new Act(actid, actname);
            for(int j= 0; j<zonesAr.length();j++){
                JSONObject zoneObj = zonesAr.getJSONObject(j);
                ArrayList<String> zoneImages = new ArrayList<>();
                JSONArray zonesImagAr = zoneObj.getJSONArray("image");
                for(int k=0; k<zonesImagAr.length(); k++){
                    zoneImages.add(zonesImagAr.getString(k));
                }
                Zone z = new Zone(
                    zoneObj.getString("name"),
                    zoneObj.getInt("level"),
                    zoneImages,
                    zoneObj.getString("altimage"),
                    zoneObj.getString("note"),
                    zoneObj.getBoolean("haspassive"),
                    zoneObj.getBoolean("hastrial"),
                    zoneObj.getString("quest"),
                    zoneObj.getBoolean("questRewardsSkills"),
                    actname,actid
                );
                //manual zone recipe load
                z.hasRecipe = zoneObj.getBoolean("hasRecipe");
                if(z.hasRecipe){
                    //recipeInfo rInfo = z.new recipeInfo();
                    JSONObject recipeObj = zoneObj.getJSONObject("recipe");
                    if(recipeObj != null){
                        z.rInfo.tooltip = recipeObj.getString("tooltip");
                        /* // we wont be using this information so we might as well
                        //not use space for no reason.
                        JSONArray recipeMods = recipeObj.getJSONArray("mods");
                        rInfo.mods = new ArrayList<>();
                        for(int k=0; k<recipeMods.length(); k++){
                            rInfo.mods.add(recipeMods.getString(k));
                        }*/
                    }
                    ActHandler.getInstance().putZone(z);
                }
                a.putZone(z);
            }
            ActHandler.getInstance().putAct(a);

        }
        System.out.println("Zone data loaded.");
    }

    private void loadGemsFromMemory() throws FileNotFoundException {

        InputStream inG = new FileInputStream(gemsJSONFileName);
        Scanner sG = new Scanner(inG).useDelimiter("\\A");
        String jsonstringG = sG.hasNext() ? sG.next() : "";

        //JSONObject objG = new JSONObject(jsonstringG);
        JSONArray arrG = new JSONArray(jsonstringG);

        //GemHolder gemsH = new GemHolder();
        for (int i = 0; i < arrG.length(); i++)
        {
            JSONObject gemObj  = arrG.getJSONObject(i);
            Gem gem = new Gem(gemObj.getString("name"));

            gem.required_lvl= gemObj.getInt("required_lvl");
            gem.isVaal=gemObj.getBoolean("isVaal");
            //gem.id = gemObj.getInt("id");
            gem.id = -1;
            gem.color= gemObj.getString("color");
            gem.iconPath= gemObj.getString("iconPath");
            gem.isRewarded = gemObj.getBoolean("isReward");
            JSONArray alt_name = gemObj.optJSONArray("alt_name");
            if (alt_name != null) {
                gem.addAltNamesFromJSON(alt_name.iterator());
            }

            if(gem.isRewarded){
                JSONObject rewardObj  = gemObj.getJSONObject("reward");
                gem.reward = gem.new Info();
                try{
                    JSONArray chars = rewardObj.getJSONArray("available_to");
                    ArrayList<String> chars_list = new ArrayList<>();
                    for(int j=0;j<chars.length();j++){
                        chars_list.add(chars.getString(j));
                        //gem.putChar(chars.getString(j));

                    }
                    gem.reward.available_to = chars_list;
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
                try{
                    gem.reward.quest_name= rewardObj.getString("quest_name");
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
                try{
                    gem.reward.npc= rewardObj.getString("npc");
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
                try{
                    gem.reward.act = rewardObj.getInt("act");
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
                try{
                    gem.reward.town= rewardObj.getString("town");
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
            }
            JSONArray buy_arrays = gemObj.getJSONArray("buy");
            gem.buy = new ArrayList<>();
            for(int j=0;j<buy_arrays.length();j++){
                JSONObject buyObj  = buy_arrays.getJSONObject(j);
                Info buy_info = gem.new Info();
                try{
                    JSONArray chars = buyObj.getJSONArray("available_to");
                    ArrayList<String> chars_list = new ArrayList<>();
                    for(int k=0;k<chars.length();k++){
                        chars_list.add(chars.getString(k));
                        //gem.putChar(chars.getString(j));

                    }
                    buy_info.available_to = chars_list;
                }catch(JSONException e){
                    e.printStackTrace();
                    logErrorGem(gem.getGemName());
                }
                try{
                    buy_info.quest_name= buyObj.getString("quest_name");
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
                try{
                    buy_info.npc= buyObj.getString("npc");
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
                try{
                    buy_info.act = buyObj.getInt("act");
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
                try{
                    buy_info.town= buyObj.getString("town");
                }catch(JSONException e){
                    logErrorGem(gem.getGemName());
                }
                gem.buy.add(buy_info);
            }

            BufferedImage img;
            try {
                File gemFile = new File(gemsIconsLocation + gem.getGemName() + ".png");
                if (gemFile.exists()) {
                    img = ImageIO.read(gemFile);
                } else {
                    if (DEBUG) {
                        // If we're debugging, all icons should exist, since we're pointing at the local directory
                        notifyPreloader(new Preloader.ErrorNotification("loadGemsFromMemory", "Missing gem icon for: " + gem.getGemName() + "! Is your repo up to date?", null));
                    }
                    //TODO
                    notifyPreloader(new GemDownloadNotification(gem.getGemName()));
                    m_logger.info("Gem " + gemFile.getName() + " doesn't exist in " + gemsIconsLocation + " downloading");
                    img = downloadGemIcon(gem, true);
                    if (img == null) {
                        m_logger.info("Gem " + gemFile.getName() + " Failed to download from Github, trying " + gem.iconPath);
                        img = downloadGemIcon(gem, false);
                    }
                }
                if (img != null) {
                    gem.gemIcon = SwingFXUtils.toFXImage(img, null);
                    gem.resizeImage();
                } else {
                    m_logger.warning("Failed to get the gem icon for: " + gem.getGemName());
                    gem.gemIcon = null;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            //load tags - new feature
            gem.isActive = gemObj.getBoolean("isActive");
            gem.isSupport = gemObj.getBoolean("isSupport");
            JSONArray tags = gemObj.getJSONArray("gemTags");
            for(int j=0;j<tags.length();j++){
                gem.tags.add(tags.getString(j));
            }

            GemHolder.getInstance().putGem(gem);
            double a = (double)i/arrG.length();
            a = a * 100.0 ;

            notifyPreloader(new NewFXPreloader.ProgressNotification(a));
        }
        System.out.println("Gem data loaded");


    }

    private BufferedImage downloadGemIcon(Gem gem, boolean fromGithub) {
        BufferedImage image = null;
        try {
            URL url;
            if (fromGithub) {
                url = new URL("https://raw.githubusercontent.com/" + REPO_OWNER + "/Path-of-Leveling/" + BRANCH_NAME + "/gems/" + gem.getGemName().replaceAll(" ", "%20") + ".png");
            } else {
                url = new URL(gem.iconPath);
            }
            image = ImageIO.read(url);

            ImageIO.write(image, "png", new File(gemsIconsLocation + gem.getGemName() + ".png"));
        } catch (IOException e) {
            m_logger.log(Level.SEVERE, "IOException while read/writing the new gem icon", e);
        }
        return image;
    }


    private void logErrorGem(String gemName){
        System.out.println("Gem : " +gemName+ " had errors in loading.");
    }

    public static void reloadBuilds(){
        loadBuildsFromMemory();
    }

    private static void loadBuildsFromMemory(){
        //pseudo for loop loads builds and panels put them
        //into buildlinker and add buildlinker to the list
        //TODO: remember to sign the build with the static method
        buildsLoaded = new ArrayList<>();
        String stringValueBase64Encoded = "";
        if (new File(POELevelFx.directory+"\\Path of Leveling\\Builds\\builds.txt").exists()){
            BufferedReader br = null;
            FileReader fr = null;
            try {
                fr = new FileReader(POELevelFx.directory+"\\Path of Leveling\\Builds\\builds.txt");
                br = new BufferedReader(fr);
                stringValueBase64Encoded = br.readLine();


            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
            if (br != null)
                    br.close();
            if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            byte[] byteValueBase64Decoded = null;
            try{
            byteValueBase64Decoded = Base64.getDecoder().decode(stringValueBase64Encoded);
        }catch(java.lang.IllegalArgumentException e){
            e.printStackTrace();
            return;
        }
        catch(Exception e){
            e.printStackTrace();
            return;
        }
        //byte[] byteValueBase64Decoded = Base64.getDecoder().decode(stringValueBase64Encoded);
        String stringValueBase64Decoded = new String(byteValueBase64Decoded);

        //JSONArray obj = new JsonParser().parse(stringValueBase64Encoded).getAsJsonArray();
        JSONArray builds_array = new JSONArray(stringValueBase64Decoded);
        for (int i = 0; i < builds_array.length(); i++) {
                JSONObject bObj = builds_array.getJSONObject(i);
                String ascName = "";
                if(bObj.getString("ascendancyName").equals("Assasin")){
                    System.err.println("Replaced assassin typo. poelevelfx load");
                    ascName = "Assassin";
                }else{
                    ascName = bObj.getString("ascendancyName");
                }
                Build build = new Build(
                        bObj.getString("buildName"),
                        bObj.getString("className"),
                        ascName
                );
                try{
                    //bObj.get("hasPob");
                    build.isValid = bObj.getBoolean("isValid");
                }catch(org.json.JSONException e){
                    //if it doesnt exist prob its valid from earlier versions
                    build.isValid = true;
                }
                build.setCharacterName(bObj.getString("characterName"));
                build.setCharacterLevel(bObj.getInt("level"));
                try{
                    //bObj.get("hasPob");
                    build.hasPob = bObj.getBoolean("hasPob");
                    if(build.hasPob){
                        build.pobLink = bObj.getString("pobLink");
                    }
                }catch(org.json.JSONException e){
                    build.hasPob = false;
                    build.pobLink = "";
                }
                GemHolder.getInstance().className = build.getClassName();
                JSONArray socket_group_array = bObj.getJSONArray("socketGroup");
                //build.level
                for (int j = 0; j < socket_group_array.length(); j++) {
                    JSONObject sObj = socket_group_array.getJSONObject(j);
                    SocketGroup sg = new SocketGroup();
                    sg.id = sObj.getInt("id");
                    sg.setFromGroupLevel(sObj.getInt("fromGroupLevel"));
                    sg.setUntilGroupLevel(sObj.getInt("untilGroupLevel"));
                    sg.setReplaceGroup(sObj.getBoolean("replaceGroup"));
                    sg.setReplacesGroup(sObj.getBoolean("replacesGroup"));
                    try{
                        //bObj.get("hasPob");
                        sg.addNote(sObj.getString("note"));
                    }catch(org.json.JSONException e){
                        sg.addNote("");
                    }
                    if(sg.replaceGroup()){
                        int id_replace = sObj.getInt("socketGroupReplace");
                        sg.id_replace = id_replace;
                    }
                    if(sg.replacesGroup()){
                        int id_replaces = sObj.getInt("socketGroupThatReplaces");
                        sg.id_replaces = id_replaces;
                    }
                    int active_id = sObj.getInt("active");
                    sg.active_id = active_id;
                    JSONArray gems_array = sObj.getJSONArray("gem");
                    for(int k = 0 ; k<gems_array.length(); k++ ){
                        JSONObject gObj = gems_array.getJSONObject(k);
                        String gemName = gObj.getString("name");
                        /*
                        if(gemName.equals("Detonate Mines")){
                            System.out.println();
                        }*/
                        Gem gem = GemHolder.getInstance().createGemFromCache(gemName,build.getClassName());
                        if(gem != null) {
                            gem.id = gObj.getInt("id");
                            gem.level_added = gObj.getInt("level_added");
                            gem.replaced = gObj.getBoolean("replaced");
                            gem.replaces = gObj.getBoolean("replaces");
                            if(gem.replaced){
                                int id_replaced = gObj.getInt("replaceWith");
                                gem.id_replaced = id_replaced;

                            }
                            if(gem.replaces){
                                int id_replaces = gObj.getInt("replacesGem");
                                gem.id_replaces = id_replaces;
                            }
                            sg.getGems().add(gem);//***check line 324 in GemsPanel_Controller;
                        } else {
                            System.out.println("-POELevelFX- Trying to read"+gemName + " from GemHolder was a name mismatch for class "+build.getClassName());
                        }
                    }
                    build.getSocketGroup().add(sg);
                }


                //update data links
                for(SocketGroup sg : build.getSocketGroup()){
                    if(sg.active_id!=-1){
                        for(Gem g : sg.getGems()){
                            if(g.id == sg.active_id){
                                sg.setActiveGem(g);
                                break;
                            }
                        }
                    }
                    //this is super simple because the action is super complex and i will prob fuck up
                    //i could potentially save a lot of search time but im bad
                    if(sg.replaceGroup()){
                        for(SocketGroup sg1 : build.getSocketGroup()){
                            if(sg.id_replace == sg1.id){
                                sg.setGroupReplaced(sg1);
                                break;
                            }
                        }
                    }
                    if(sg.replacesGroup()){
                        for(SocketGroup sg1 : build.getSocketGroup()){
                            if(sg.id_replaces == sg1.id){
                                sg.setGroupThatReplaces(sg1);
                                break;
                            }
                        }
                    }

                    for(Gem g : sg.getGems()){
                        //if g active id != -1
                        if(g.replaces){
                            for(Gem g1 : sg.getGems()){
                                if(g1.id == g.id_replaces){
                                    g.replacesGem = g1;
                                    break;
                                }
                            }
                        }
                        if(g.replaced){
                            for(Gem g1: sg.getGems()){
                                if(g1.id == g.id_replaced){
                                    g.replacedWith = g1;
                                    break;
                                }
                            }
                        }

                    }
                }
                buildsLoaded.add(build);
        }

        //System.out.println(stringValueBase64Encoded  + " when decoded is: " + stringValueBase64Decoded);
          System.out.println("Loaded builds successfully from " + POELevelFx.directory + "\\Path of Leveling\\Builds\\builds.txt");

        }
    }

    private static int sign_jsons(HashSet<Integer> unique_ids){
        if(unique_ids == null) unique_ids = new HashSet<>();
        int ran;
        do{
            ran = ThreadLocalRandom.current().nextInt(1,999999);
        }while(unique_ids.contains(ran));
        unique_ids.add(ran);
        return ran;
    }

    public static boolean saveBuildsToMemory(){

        JSONArray builds_array = new JSONArray();
        HashSet<Integer> unique_ids = new HashSet<>();
        for( Build build : buildsLoaded){
            JSONObject bObj = new JSONObject();
            bObj.put("buildName",build.getName());
            bObj.put("className",build.getClassName());
            if(build.getAsc().equals("Assasin")) System.err.println("not fixed typo. poelevelfx issue");
            bObj.put("ascendancyName",build.getAsc());
            bObj.put("level", build.getCharacterLevel()); //<change
            bObj.put("characterName",build.getCharacterName());
            bObj.put("isValid", build.isValid);
            bObj.put("hasPob",build.hasPob);
            bObj.put("pobLink",build.pobLink);

            JSONArray socket_group_array = new JSONArray();
            bObj.put("socketGroup", socket_group_array);
            for(SocketGroup sg : build.getSocketGroup()){
                JSONObject sObj = new JSONObject();
                if(sg.id == -1)
                    sg.id = sign_jsons(unique_ids);
                sObj.put("id",sg.id);
                if(sg.getActiveGem().id == -1){
                    sg.getActiveGem().id = sign_jsons(unique_ids);
                }
                sObj.put("active", sg.getActiveGem().id);
                sObj.put("fromGroupLevel", sg.getFromGroupLevel());
                sObj.put("untilGroupLevel", sg.getUntilGroupLevel());
                sObj.put("replaceGroup", sg.replaceGroup());
                sObj.put("replacesGroup", sg.replacesGroup());
                sObj.put("note",sg.getNote());
                if(sg.replaceGroup()){
                    if(sg.getGroupReplaced().id == -1){
                        sg.getGroupReplaced().id = sign_jsons(unique_ids);
                    }
                    sObj.put("socketGroupReplace", sg.getGroupReplaced().id);
                }
                if(sg.replacesGroup()){
                    if(sg.getGroupThatReplaces().id == -1){
                        sg.getGroupThatReplaces().id = sign_jsons(unique_ids);
                    }
                    sObj.put("socketGroupThatReplaces", sg.getGroupThatReplaces().id);
                }
                JSONArray gems_array = new JSONArray();
                for(Gem g : sg.getGems()){
                    JSONObject gObj = new JSONObject();
                    if(g.id == -1){
                        g.id = sign_jsons(unique_ids);
                    }
                    gObj.put("id",g.id);
                    gObj.put("name", g.getGemName());
                    gObj.put("level_added", g.getLevelAdded());
                    gObj.put("replaced", g.replaced);
                    gObj.put("replaces", g.replaces);
                    if(g.replaced){
                        if(g.replacedWith.id == -1){
                            g.replacedWith.id  = sign_jsons(unique_ids);
                        }
                        gObj.put("replaceWith", g.replacedWith.id);
                    }
                    if(g.replaces){
                        if(g.replacesGem.id == -1){
                            g.replacesGem.id = sign_jsons(unique_ids);
                        }
                        gObj.put("replacesGem", g.replacesGem.id);
                    }
                    gems_array.put(gObj);
                }
                sObj.put("gem", gems_array);
                socket_group_array.put(sObj);

            }
            //now we need to connect data


            builds_array.put(bObj);
        }

        String build_to_json = builds_array.toString();

        //Gson gson = new Gson();
        //String build_to_json = gson.toJson(linker.get(activeBuildID).build);
        //System.out.println(build_to_json);
        String stringValueBase64Encoded = Base64.getEncoder().encodeToString(build_to_json.getBytes());
        //System.out.println(build_to_json  + " when Base64 encoded is: " + stringValueBase64Encoded);
        BufferedWriter bw = null;
        FileWriter fw = null;
        boolean done = false;
        try {
            fw = new FileWriter(POELevelFx.directory+"\\Path of Leveling\\Builds\\builds.txt");
            bw = new BufferedWriter(fw);
            bw.write(stringValueBase64Encoded);
            System.out.println("Svaed builds to "+ POELevelFx.directory+"\\Path of Leveling\\Builds\\builds.txt");
            done = true;
        } catch (IOException e) {
            e.printStackTrace();
            done = false;
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return done;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Application.setUserAgentStylesheet(Application.STYLESHEET_MODENA);
        //StyleManager.getInstance().addUserAgentStylesheet(getClass().getResource("/styles/modena_dark.css").toExternalForm());
        StyleManager.getInstance().addUserAgentStylesheet(getClass().getResource("/styles/style.css").toExternalForm());

        if(is_new_version){
            new UpdaterStage();
        }else{
            main = new Main_Stage(this);
        }
        //zone = new ZoneOverlay_Stage();
        //exp = new LevelOverlay_Stage();
    }

    public void editor(){
        main.close();
        editor = new Editor_Stage(this);
        editor.setMaximized(true);
    }

    public void launcher(){
        editor.close();
        main = new Main_Stage(this);
    }

    public void start(boolean zone_b, boolean xp, boolean level){
        /*
        main.close();
        if(zone_b){
            this.zone = new ZoneOverlay_Stage();
        }
        if(xp){
            this.exp = new LevelOverlay_Stage();
        }
        if(level){
            this.leveling = new GemOverlay_Stage(Main_Stage.buildLoaded);
        }




        Platform.runLater(new Runnable(){


            @Override
            public void run() {
                Controller controller = new Controller(zone, exp, leveling, Main_Stage.buildLoaded);
            }
         });
        */
        main.close();
        Platform.runLater(new Runnable(){
                @Override
                public void run() {
                    //Hook.run();
                    GlobalKeyListener.run();
                }
        });
        controller = new Controller(zone_b, xp, level, Main_Stage.buildLoaded);

    }
    Controller controller;

    private void addTrayIcon() throws AWTException {
    final TrayIcon trayIcon = new TrayIcon(new ImageIcon(getClass().getResource("/icons/The_Explorer_card_art.png")).getImage(), "Path of Leveling");

    // Create a pop-up menu components
    final PopupMenu popup = new PopupMenu();
    final MenuItem shutdownItem = new MenuItem("Exit");
    final MenuItem settingsItem = new MenuItem("Settings");

    //Add components to pop-up menu
    popup.add(settingsItem);
    popup.add(shutdownItem);

    trayIcon.setPopupMenu(popup);
    trayIcon.setImageAutoSize(true); //So the icon auto-sizes

    SystemTray.getSystemTray().add(trayIcon);

    trayIcon.addMouseListener(new MouseAdapter() {
        @Override public void mouseClicked(java.awt.event.MouseEvent e) {
            if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) { //Left click on tray icon (single click !)
                Platform.runLater(() -> {
                    //code to show things...
                });
            }
        }
    });

    shutdownItem.addActionListener(evnt -> {
        //code to exit the program
        //save stuff
        if(saveBuildsToMemory()){
            System.out.println("Successfully saved checkpoint");
        }else{
            System.out.println("Checkpoint save failed");
        }
        try {
                GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e1) {
                e1.printStackTrace();
        }
        System.exit(20);
    });

    settingsItem.addActionListener(evnt -> {
        //code to exit the program
        //save stuff
        if(controller!=null){
            controller.settings_event();
        }
    });
}


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Negate the following if you want to test release mode
        // .gitignore shouldn't exist in the release environment, if it doesn't exist, then play nice, no debuggery.
        if (Files.exists(Paths.get("./.gitignore"))) {
            System.out.println("Detected that we're running in a development environment");
            DEBUG = true;
            BRANCH_NAME = DEBUG_BRANCH_NAME;
        } else {
            System.out.println("Running in release mode");
        }

        setUpDirectories();

        if (!DEBUG) {
            setUpLog();
            GithubHelper.checkRateLimited();
            if (checkForNewVersion()) {
                is_new_version = true;
                // New release info should NEVER be null here!
                assert (newReleaseInfo != null);
                UpdaterController.newReleaseInfo = newReleaseInfo;
                LauncherImpl.launchApplication(POELevelFx.class, UpdatePreloader.class, args);
            } else {
                is_new_version = false;
                LauncherImpl.launchApplication(POELevelFx.class, NewFXPreloader.class, args);
            }
        } else {
            is_new_version = false;
            LauncherImpl.launchApplication(POELevelFx.class, NewFXPreloader.class, args);
        }
    }

    public static void setUpDirectories(){
        POELevelFx.directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();

        System.out.println(POELevelFx.directory + "\\Path of Leveling");
        if (!new File(POELevelFx.directory + "\\Path of Leveling").exists()) {
            new File(POELevelFx.directory + "\\Path of Leveling").mkdirs();
        }
        if (!new File(POELevelFx.directory + "\\Path of Leveling\\Builds").exists()) {
            new File(POELevelFx.directory + "\\Path of Leveling\\Builds").mkdirs();
        }
        createFileIfNotExists(POELevelFx.directory + "\\Path of Leveling\\settings.txt");
        createFileIfNotExists(POELevelFx.directory + "\\Path of Leveling\\log.txt");

        if (!DEBUG) {
            gemsJSONFileName = POELevelFx.directory + "\\Path of Leveling\\json\\gems.json";
            gemsTimeFileName = POELevelFx.directory + "\\Path of Leveling\\json\\gemsjson.time";
            dataJSONFileName = POELevelFx.directory + "\\Path of Leveling\\json\\data.json";
            dataTimeFileName = POELevelFx.directory + "\\Path of Leveling\\json\\datajson.time";
            if (!new File(POELevelFx.directory + "\\Path of Leveling\\json").exists()) {
                if (new File(POELevelFx.directory + "\\Path of Leveling\\json").mkdirs()) {
                    try {
                        Files.setAttribute(Paths.get(POELevelFx.directory + "\\Path of Leveling\\json"), "dos:hidden", true);
                    } catch (IOException ex) {
                        Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, "Exception while attempting to set json directory as hidden", ex);
                    }
                } else {
                    m_logger.severe("Failed to create JSON directory!");
                }
            }
            gemsIconsLocation = POELevelFx.directory + "\\Path of Leveling\\gems\\icons\\";
            if (!new File(gemsIconsLocation).exists()) {
                if (new File(gemsIconsLocation).mkdirs()) {
                    try {
                        Files.setAttribute(Paths.get(POELevelFx.directory + "\\Path of Leveling\\gems\\"), "dos:hidden", true);
                    } catch (IOException ex) {
                        Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, "Exception while attempting to set gems directory as hidden", ex);
                    }
                } else {
                    m_logger.severe("Failed to create gems directory!");
                }
            }
        } else {
            gemsJSONFileName = "json\\gems.json";
            gemsTimeFileName = "json\\gemsjson.time";
            dataJSONFileName = "json\\data.json";
            dataTimeFileName = "json\\datajson.time";
            gemsIconsLocation = "gems\\";
        }

    }

    private static boolean createFileIfNotExists(String path) {
        File filePath = new File(path);
        if (!filePath.isFile()) {
            try {
                return filePath.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }

    public void setUpFonts() throws Exception{
        Platform.setImplicitExit( false );
        addTrayIcon();
        Font.loadFont(POELevelFx.class.getResource("/fonts/Fontin-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/Fontin-Italic.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/Fontin-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/Fontin-SmallCaps.ttf").toExternalForm(), 10);

        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-Thin.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-ThinItalic.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-Regular.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-ThinItalic.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-MediumItalic.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-Medium.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-Light.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-LightItalic.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-Italic.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-Bold.ttf").toExternalForm(), 10);
        Font.loadFont(POELevelFx.class.getResource("/fonts/AlegreyaSansSC-BoldItalic.ttf").toExternalForm(), 10);
    }

    public static void setUpLog(){
        File f = new File(POELevelFx.directory + "\\Path of Leveling\\log.txt");
        if(f.length()>= 53287796 ){//that would be about 50mbs
            /* implement a good delete top N lines to empty some space.
            BufferedReader br = null;
            FileReader fr = null;
                ArrayList<String> restoffile = new ArrayList<>();
            try {

                fr = new FileReader(POELevelFx.directory + "\\Path of Leveling\\log.txt");
                br = new BufferedReader(fr);

                String sCurrentLine;

                br = new BufferedReader(fr);
                int counter = 0;
                while ((sCurrentLine = br.readLine()) != null) {
                    //System.out.println(sCurrentLine);
                    counter++;
                    if(counter >= 50000){
                        restoffile.add(sCurrentLine);
                        restoffile.
                        //System.out.println(restoffile.size());
                    }
                }

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                try {

                    if (br != null)
                        br.close();

                    if (fr != null)
                        fr.close();

                } catch (IOException ex) {

                    ex.printStackTrace();

                }

            }
            */ //until then just delete all.
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(f);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.print("");
            writer.close();
        }
        PrintStream printStream= null;
        try {
            printStream = new PrintStream(new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\log.txt", true));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.setOut(printStream);
        System.setErr(printStream);
        //System.setOut(new PrintStream(f));
        System.out.println();
        System.out.println();
        System.out.println("=====new launch=====");
        System.out.println(new Timestamp(System.currentTimeMillis()).toString());
        System.out.println();
        System.out.println();
    }

    public static boolean checkForNewVersion(){
        GithubHelper.ReleaseInfo releaseInfo = GithubHelper.getLatestReleaseInfo();
        if (releaseInfo == null) {
            return false;
        }

        System.out.println("Current Version: " + POELevelFx.version);
        System.out.println("New Version: " + releaseInfo.version);
        if(!POELevelFx.version.equalsIgnoreCase(releaseInfo.version)){
            POELevelFx.newReleaseInfo = releaseInfo;
            return true;
        } else {
            return false;
        }
    }

    public void checkForNewJSON() {
        GithubHelper gh = new GithubHelper(REPO_OWNER, BRANCH_NAME);
        gh.init();
        File gemsJSONFile = new File(gemsJSONFileName);
        File gemsTimeFile = new File(gemsTimeFileName);
        try {
            gh.downloadGemsJsonFileIfNeeded(gemsJSONFile, gemsTimeFile);
        } catch (IOException e) {
            Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, "IOException trying to update gems.json", e);
        }
        File dataJSONFile = new File(dataJSONFileName);
        File dataTimeFile = new File(dataTimeFileName);
        try {
            gh.downloadDataJsonFileIfNeeded(dataJSONFile, dataTimeFile);
        } catch (IOException e) {
            Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, "IOException trying to update data.json", e);
        }
    }




}
