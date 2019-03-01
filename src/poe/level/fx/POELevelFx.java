/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Base64;
//import java.util.Base64;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.sun.javafx.application.LauncherImpl;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.input.KeyCombination;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import poe.level.data.Act;
import poe.level.data.ActHandler;
import poe.level.data.Build;
import poe.level.data.Controller;
import poe.level.data.Gem;
import poe.level.data.Gem.Info;
import poe.level.data.GemHolder;
import poe.level.data.SocketGroup;
import poe.level.data.Zone;
import poe.level.keybinds.GlobalKeyListener;

/**
 *
 * @author Christos
 */
public class POELevelFx extends Application {

    public static String directory;
    public static ArrayList<Build> buildsLoaded;
    private Stage main;
    private Stage editor;
    private static String update_path_prefix = "https://github.com/karakasis/Path-of-Leveling/releases/download/";
    private static String update_path_suffix = "/PathOfLeveling.jar";
    private static String version = "v0.65-alpha";
    private static boolean is_new_version;
    // v0.5-alpha <- between

    public void update() {
        URL url;

        try {
            url = new URL(update_path_prefix + "" + POELevelFx.version + "" + update_path_suffix);
            HttpURLConnection httpConnection = (HttpURLConnection) (url.openConnection());
            long completeFileSize = httpConnection.getContentLength();
            UpdaterController.finalSize = completeFileSize;
            java.io.BufferedInputStream in = new java.io.BufferedInputStream(httpConnection.getInputStream());
            java.io.FileOutputStream fos = new java.io.FileOutputStream(
                    "PathOfLeveling-" + POELevelFx.version + ".jar");
            java.io.BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
            byte[] data = new byte[1024];
            long downloadedFileSize = 0;
            int x = 0;
            while ((x = in.read(data, 0, 1024)) >= 0) {
                if (UpdaterController.cancelDownload) {
                    bout.close();
                    in.close();
                    File file = new File("PathOfLeveling-" + POELevelFx.version + ".jar");
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

                bout.write(data, 0, x);
            }
            bout.close();
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void declineUpdateFromPreload() {
        is_new_version = false;
        try {
            init();
        } catch (Exception ex) {
            Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("test");
    }

    @Override
    public void init() throws Exception {

        boolean restart = false;
        if (is_new_version) {
            
            while(true){
                    System.err.println("");
                if(UpdaterController.allowUpdate){
                    System.out.println("allowed");
                    UpdaterController.allowUpdate = false;
                    update();
                    break;
                }
                if(UpdaterController.declUpdate){
                    is_new_version = false;
                    restart = true;
                    break;
                }
            }
            if(restart) {
                //LauncherImpl.launchApplication(POELevelFx.class, NewFXPreloader.class, null);
                init();
            }
        } else {
            Platform.setImplicitExit(false);
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
            if (!new File(POELevelFx.directory + "\\Path of Leveling\\config.properties").isFile()) {
                new File(POELevelFx.directory + "\\Path of Leveling\\config.properties").createNewFile();

                Properties prop = new Properties();
                OutputStream output = null;

                try {

                    output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");

                    // set the properties value
                    prop.setProperty("zones-toggle", "false");
                    Preferences_Controller.zones_toggle = false;
                    prop.setProperty("zones-text-toggle", "true");
                    Preferences_Controller.zones_text_toggle = true;
                    prop.setProperty("zones-trial-toggle", "false");
                    Preferences_Controller.zones_trial_toggle = false;
                    prop.setProperty("zones-passive-toggle", "true");
                    Preferences_Controller.zones_passive_toggle = true;
                    prop.setProperty("zones-images-toggle", "true");
                    Preferences_Controller.zones_images_toggle = true;
                    double a = 10;
                    prop.setProperty("zones-slider", String.valueOf(a));
                    Preferences_Controller.zones_slider = a;
                    prop.setProperty("zones-hotkey-show_hide", "F5");
                    try {
                        KeyCombination keyCombination = KeyCombination.keyCombination("F5");
                        System.out.println("key code : " + keyCombination.getName());
                        Preferences_Controller.zones_hotkey_show_hide_key = keyCombination;
                    } catch (IllegalArgumentException e) {
                        System.out.println(":incorect:");
                        Preferences_Controller.zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
                    }
                    a = 15;
                    Preferences_Controller.level_slider = a;
                    prop.setProperty("level-slider", String.valueOf(a));
                    prop.setProperty("level-hotkey-remind", "F4");
                    prop.setProperty("poe-dir", "");
                    Preferences_Controller.poe_log_dir = "";
                    try {
                        KeyCombination keyCombination = KeyCombination.keyCombination("F4");
                        System.out.println("key code : " + keyCombination.getName());
                        Preferences_Controller.level_hotkey_remind_key = keyCombination;
                    } catch (IllegalArgumentException e) {
                        System.out.println(":incorect:");
                        Preferences_Controller.level_hotkey_remind_key = KeyCombination.NO_MATCH;
                    }

                    // new changes

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
            } else {

                Properties prop = new Properties();
                InputStream input = null;
                String zones_hotkey_show_hide = "";
                String level_hotkey_remind = "";
                try {

                    input = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");

                    // load a properties file
                    prop.load(input);
                    Preferences_Controller.zones_toggle = Boolean.parseBoolean(prop.getProperty("zones-toggle"));
                    Preferences_Controller.zones_text_toggle = Boolean
                            .parseBoolean(prop.getProperty("zones-text-toggle"));
                    Preferences_Controller.zones_images_toggle = Boolean
                            .parseBoolean(prop.getProperty("zones-images-toggle"));
                    Preferences_Controller.zones_trial_toggle = Boolean
                            .parseBoolean(prop.getProperty("zones-trial-toggle"));
                    Preferences_Controller.zones_passive_toggle = Boolean
                            .parseBoolean(prop.getProperty("zones-passive-toggle"));
                    Preferences_Controller.zones_slider = Double.parseDouble(prop.getProperty("zones-slider"));
                    zones_hotkey_show_hide = prop.getProperty("zones-hotkey-show_hide");
                    Preferences_Controller.level_slider = Double.parseDouble(prop.getProperty("level-slider"));
                    level_hotkey_remind = prop.getProperty("level-hotkey-remind");
                    if (!(prop.getProperty("poe-dir") == null || prop.getProperty("poe-dir").equals(""))) {
                        Preferences_Controller.poe_log_dir = prop.getProperty("poe-dir") + "\\logs\\Client.txt";
                    }
                    // new changes
                    // a bug is introduced at this point. users with older versions will not have
                    // those lines in their prop files and a null error will pop up
                    // quick fix if null add the line manually from code.
                    String[] zones_pos = null;
                    try {
                        zones_pos = prop.getProperty("zones-overlay-pos").toString().split(",");
                    } catch (NullPointerException e) {
                        // hopefully the update..Pos method will write the values without crashing.
                        // my only issue is that i am already opening the prop file in read mode, and
                        // then from within
                        // im calling a write function.
                        System.out
                                .println("zones-overlay-pos was missing from config.properties and is manually added.");
                        zones_pos = new String[2];
                        zones_pos[0] = "-200.0";
                        zones_pos[1] = "-200.0";
                    }
                    Preferences_Controller.updateZonesPos(Double.parseDouble(zones_pos[0]),
                            Double.parseDouble(zones_pos[1]));

                    String[] level_pos = null;
                    try {
                        level_pos = prop.getProperty("level-overlay-pos").toString().split(",");
                    } catch (NullPointerException e) {
                        System.out
                                .println("level-overlay-pos was missing from config.properties and is manually added.");
                        level_pos = new String[2];
                        level_pos[0] = "-200.0";
                        level_pos[1] = "-200.0";
                    }
                    Preferences_Controller.updateLevelPos(Double.parseDouble(level_pos[0]),
                            Double.parseDouble(level_pos[1]));

                    String[] gem_pos = null;
                    try {
                        gem_pos = prop.getProperty("gems-overlay-pos").toString().split(",");
                    } catch (NullPointerException e) {
                        System.out
                                .println("gems-overlay-pos was missing from config.properties and is manually added.");
                        gem_pos = new String[2];
                        gem_pos[0] = "-200.0";
                        gem_pos[1] = "-200.0";
                    }
                    Preferences_Controller.updateGemsPos(Double.parseDouble(gem_pos[0]),
                            Double.parseDouble(gem_pos[1]));

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

                try {
                    KeyCombination keyCombination = KeyCombination.keyCombination(zones_hotkey_show_hide);
                    System.out.println("key code : " + keyCombination.getName());
                    Preferences_Controller.zones_hotkey_show_hide_key = keyCombination;
                } catch (IllegalArgumentException e) {
                    System.out.println(":incorect:");
                    Preferences_Controller.zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
                }

                try {
                    KeyCombination keyCombination = KeyCombination.keyCombination(level_hotkey_remind);
                    System.out.println("key code : " + keyCombination.getName());
                    Preferences_Controller.level_hotkey_remind_key = keyCombination;
                } catch (IllegalArgumentException e) {
                    System.out.println(":incorect:");
                    Preferences_Controller.level_hotkey_remind_key = KeyCombination.NO_MATCH;
                }

            }

            StringBuilder raw = readRawToString();
            String replace = raw.toString().replace('-','+').replace('_','/').trim();
            //save the replaced-values base64 string - optional
            PrintWriter out = new PrintWriter("decoded.txt");
            out.println(replace);
            out.close();
            //read into byte array using apache commons base64
            byte[] byteValueBase64Decoded = null;
            try{
                byteValueBase64Decoded = org.apache.commons.codec.binary.Base64.decodeBase64(replace);
            }catch(java.lang.IllegalArgumentException e){
                e.printStackTrace();
                return;
            }
            String inflatedXML = "";
            try{
                //inflate 
                inflatedXML = inflate(byteValueBase64Decoded);
            }catch(IOException e){
                
            }catch(DataFormatException e){
                
            }
            System.out.println(inflatedXML);
            out = new PrintWriter("pathofbuilding.txt");
            out.println(inflatedXML);
            out.close();
        //JSONArray obj = new JsonParser().parse(stringValueBase64Encoded).getAsJsonArray();
        //JSONArray builds_array = new JSONArray(stringValueBase64Decoded);
        
            loadActsFromMemory();
            loadGemsFromMemory();
            loadBuildsFromMemory();

        }
    }

    private String inflate(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
            int count = inflater.inflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        System.out.println("Original: " + data.length);
        System.out.println("Compressed: " + output.length);
        return new String(output);
    }

    private StringBuilder readRawToString() {
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

    private ArrayList<String[]> mergeTags() {
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
                while (line != null) {
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

    public void close() {
        // exp.close();
        main.close();
        // zone.close();
    }

    private void loadActsFromMemory() {
        InputStream in = POELevelFx.class.getResourceAsStream("/json/data.json");
        Scanner s = new Scanner(in);
        s.useDelimiter("\\A");
        String jsonstring = s.hasNext() ? s.next() : "";
        s.close();

        JSONObject obj = new JSONObject(jsonstring);
        JSONArray arr = obj.getJSONArray("acts");

        // ActHandler actsH = new ActHandler();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject actobj = arr.getJSONObject(i);
            String actname = actobj.getString("act");
            int actid = actobj.getInt("actid");
            JSONArray zonesAr = actobj.getJSONArray("zones");
            Act a = new Act(actid, actname);
            for (int j = 0; j < zonesAr.length(); j++) {
                JSONObject zoneObj = zonesAr.getJSONObject(j);
                ArrayList<String> zoneImages = new ArrayList<>();
                JSONArray zonesImagAr = zoneObj.getJSONArray("image");
                for (int k = 0; k < zonesImagAr.length(); k++) {
                    zoneImages.add(zonesImagAr.getString(k));
                }
                Zone z = new Zone(zoneObj.getString("name"), zoneObj.getInt("level"), zoneImages,
                        zoneObj.getString("altimage"), zoneObj.getString("note"), zoneObj.getBoolean("haspassive"),
                        zoneObj.getBoolean("hastrial"), zoneObj.getString("quest"),
                        zoneObj.getBoolean("questRewardsSkills"), actname, actid);
                a.putZone(z);
            }
            ActHandler.getInstance().putAct(a);
        }
    }

    private void loadGemsFromMemory() {

        InputStream inG = POELevelFx.class.getResourceAsStream("/json/gems.json");
        Scanner sG = new Scanner(inG);
        sG.useDelimiter("\\A");
        String jsonstringG = sG.hasNext() ? sG.next() : "";
        sG.close();

        JSONArray arrG = new JSONArray(jsonstringG);

        for (int i = 0; i < arrG.length(); i++) {
            JSONObject gemObj = arrG.getJSONObject(i);
            Gem gem = new Gem();

            gem.name = gemObj.getString("name");
            gem.required_lvl = gemObj.getInt("required_lvl");
            gem.isVaal = gemObj.getBoolean("isVaal");
            gem.id = -1;
            gem.color = gemObj.getString("color");
            gem.iconPath = gemObj.getString("iconPath");
            gem.isRewarded = gemObj.getBoolean("isReward");

            if (gem.isRewarded) {
                JSONObject rewardObj = gemObj.getJSONObject("reward");
                gem.reward = gem.new Info();
                try {
                    JSONArray chars = rewardObj.getJSONArray("available_to");
                    ArrayList<String> chars_list = new ArrayList<>();
                    for (int j = 0; j < chars.length(); j++) {
                        chars_list.add(chars.getString(j));
                    }
                    gem.reward.available_to = chars_list;
                    gem.reward.quest_name = rewardObj.getString("quest_name");
                    gem.reward.npc = rewardObj.getString("npc");
                    gem.reward.act = rewardObj.getInt("act");
                } catch (JSONException e) {
                }
            }
            JSONArray buy_arrays = gemObj.getJSONArray("buy");
            gem.buy = new ArrayList<>();
            for (int j = 0; j < buy_arrays.length(); j++) {
                JSONObject buyObj = buy_arrays.getJSONObject(j);
                Info buy_info = gem.new Info();
                try {
                    JSONArray chars = buyObj.getJSONArray("available_to");
                    ArrayList<String> chars_list = new ArrayList<>();
                    for (int k = 0; k < chars.length(); k++) {
                        chars_list.add(chars.getString(k));
                    }
                    buy_info.available_to = chars_list;
                    buy_info.quest_name = buyObj.getString("quest_name");
                    buy_info.npc = buyObj.getString("npc");
                    buy_info.act = buyObj.getInt("act");
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println(gem.getGemName());
                }
                gem.buy.add(buy_info);
            }

            BufferedImage img = null;
            try {
                img = ImageIO.read(getClass().getResource("/gems/" + gem.name + ".png"));
                gem.gemIcon = SwingFXUtils.toFXImage(img, null);
            } catch (IOException e) {
                e.printStackTrace();
            }

            gem.resizeImage();

            // load tags - new feature
            gem.isActive = gemObj.getBoolean("isActive");
            gem.isSupport = gemObj.getBoolean("isSupport");
            JSONArray tags = gemObj.getJSONArray("gemTags");
            // new arraylist is in constructor
            for (int j = 0; j < tags.length(); j++) {
                gem.tags.add(tags.getString(j));
            }

            GemHolder.getInstance().putGem(gem);
            double a = (double) i / arrG.length();
            a = a * 100.0;

            notifyPreloader(new NewFXPreloader.ProgressNotification(a));
        }
    }

    public static void reloadBuilds() {
        loadBuildsFromMemory();
    }

    private static void loadBuildsFromMemory() {
        // pseudo for loop loads builds and panels put them
        // into buildlinker and add buildlinker to the list
        // TODO: remember to sign the build with the static method
        buildsLoaded = new ArrayList<>();
        String stringValueBase64Encoded = "";
        if (new File(POELevelFx.directory + "\\Path of Leveling\\Builds\\builds.txt").exists()) {
            BufferedReader br = null;
            FileReader fr = null;
            try {
                fr = new FileReader(POELevelFx.directory + "\\Path of Leveling\\Builds\\builds.txt");
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
            try {
                byteValueBase64Decoded = Base64.getDecoder().decode(stringValueBase64Encoded);
            } catch (java.lang.IllegalArgumentException e) {
                e.printStackTrace();
                return;
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            String stringValueBase64Decoded = new String(byteValueBase64Decoded);

            JSONArray builds_array = new JSONArray(stringValueBase64Decoded);
            for (int i = 0; i < builds_array.length(); i++) {
                JSONObject bObj = builds_array.getJSONObject(i);
                Build build = new Build(bObj.getString("buildName"), bObj.getString("className"),
                        bObj.getString("ascendancyName"));
                try {
                    build.isValid = bObj.getBoolean("isValid");
                } catch (org.json.JSONException e) {
                    // if it doesnt exist prob its valid from earlier versions
                    build.isValid = true;
                }
                build.characterName = bObj.getString("characterName");
                build.level = bObj.getInt("level");
                try {
                    build.hasPob = bObj.getBoolean("hasPob");
                    if (build.hasPob) {
                        build.pobLink = bObj.getString("pobLink");
                    }
                } catch (org.json.JSONException e) {
                    build.hasPob = false;
                    build.pobLink = "";
                }
                GemHolder.getInstance().className = build.getClassName();
                JSONArray socket_group_array = bObj.getJSONArray("socketGroup");
                for (int j = 0; j < socket_group_array.length(); j++) {
                    JSONObject sObj = socket_group_array.getJSONObject(j);
                    SocketGroup sg = new SocketGroup();
                    sg.id = sObj.getInt("id");
                    sg.setFromGroupLevel(sObj.getInt("fromGroupLevel"));
                    sg.setUntilGroupLevel(sObj.getInt("untilGroupLevel"));
                    sg.setReplaceGroup(sObj.getBoolean("replaceGroup"));
                    sg.setReplacesGroup(sObj.getBoolean("replacesGroup"));
                    try {
                        sg.addNote(sObj.getString("note"));
                    } catch (org.json.JSONException e) {
                        sg.addNote("");
                    }
                    if (sg.replaceGroup()) {
                        int id_replace = sObj.getInt("socketGroupReplace");
                        sg.id_replace = id_replace;
                    }
                    if (sg.replacesGroup()) {
                        int id_replaces = sObj.getInt("socketGroupThatReplaces");
                        sg.id_replaces = id_replaces;
                    }
                    int active_id = sObj.getInt("active");
                    sg.active_id = active_id;
                    JSONArray gems_array = sObj.getJSONArray("gem");
                    for (int k = 0; k < gems_array.length(); k++) {
                        JSONObject gObj = gems_array.getJSONObject(k);
                        String gemName = gObj.getString("name");
                        if (gemName.equals("Detonate Mines")) {
                            System.out.println();
                        }
                        Gem gem = GemHolder.getInstance().createGemFromCache(gemName, build.getClassName());
                        if (gem == null) {
                            System.out.println();
                        }
                        gem.id = gObj.getInt("id");
                        gem.level_added = gObj.getInt("level_added");
                        gem.replaced = gObj.getBoolean("replaced");
                        gem.replaces = gObj.getBoolean("replaces");
                        if (gem.replaced) {
                            int id_replaced = gObj.getInt("replaceWith");
                            gem.id_replaced = id_replaced;

                        }
                        if (gem.replaces) {
                            int id_replaces = gObj.getInt("replacesGem");
                            gem.id_replaces = id_replaces;
                        }
                        sg.getGems().add(gem);
                    }
                    build.getSocketGroup().add(sg);
                }

                // update data links
                for (SocketGroup sg : build.getSocketGroup()) {
                    if (sg.active_id != -1) {
                        for (Gem g : sg.getGems()) {
                            if (g.id == sg.active_id) {
                                sg.setActiveGem(g);
                                break;
                            }
                        }
                    }
                    // this is super simple because the action is super complex and i will prob fuck
                    // up
                    // i could potentially save a lot of search time but im bad
                    if (sg.replaceGroup()) {
                        for (SocketGroup sg1 : build.getSocketGroup()) {
                            if (sg.id_replace == sg1.id) {
                                sg.setGroupReplaced(sg1);
                                break;
                            }
                        }
                    }
                    if (sg.replacesGroup()) {
                        for (SocketGroup sg1 : build.getSocketGroup()) {
                            if (sg.id_replaces == sg1.id) {
                                sg.setGroupThatReplaces(sg1);
                                break;
                            }
                        }
                    }

                    for (Gem g : sg.getGems()) {
                        // if g active id != -1
                        if (g.replaces) {
                            for (Gem g1 : sg.getGems()) {
                                if (g1.id == g.id_replaces) {
                                    g.replacesGem = g1;
                                    break;
                                }
                            }
                        }
                        if (g.replaced) {
                            for (Gem g1 : sg.getGems()) {
                                if (g1.id == g.id_replaced) {
                                    g.replacedWith = g1;
                                    break;
                                }
                            }
                        }

                    }
                }
                buildsLoaded.add(build);
            }

            System.out.println(stringValueBase64Encoded + " when decoded is: " + stringValueBase64Decoded);

        }
    }

    private static int sign_jsons(HashSet<Integer> unique_ids) {
        if (unique_ids == null)
            unique_ids = new HashSet<>();
        int ran;
        do {
            ran = ThreadLocalRandom.current().nextInt(1, 999999);
        } while (unique_ids.contains(ran));
        unique_ids.add(ran);
        return ran;
    }

    public static boolean saveBuildsToMemory() {

        JSONArray builds_array = new JSONArray();
        HashSet<Integer> unique_ids = new HashSet<>();
        for (Build build : buildsLoaded) {
            JSONObject bObj = new JSONObject();
            bObj.put("buildName", build.getName());
            bObj.put("className", build.getClassName());
            bObj.put("ascendancyName", build.getAsc());
            bObj.put("level", build.level); // <change
            bObj.put("characterName", build.characterName);
            bObj.put("isValid", build.isValid);
            bObj.put("hasPob", build.hasPob);
            bObj.put("pobLink", build.pobLink);

            JSONArray socket_group_array = new JSONArray();
            bObj.put("socketGroup", socket_group_array);
            for (SocketGroup sg : build.getSocketGroup()) {
                JSONObject sObj = new JSONObject();
                if (sg.id == -1)
                    sg.id = sign_jsons(unique_ids);
                sObj.put("id", sg.id);
                if (sg.getActiveGem().id == -1) {
                    sg.getActiveGem().id = sign_jsons(unique_ids);
                }
                sObj.put("active", sg.getActiveGem().id);
                sObj.put("fromGroupLevel", sg.getFromGroupLevel());
                sObj.put("untilGroupLevel", sg.getUntilGroupLevel());
                sObj.put("replaceGroup", sg.replaceGroup());
                sObj.put("replacesGroup", sg.replacesGroup());
                sObj.put("note", sg.getNote());
                if (sg.replaceGroup()) {
                    if (sg.getGroupReplaced().id == -1) {
                        sg.getGroupReplaced().id = sign_jsons(unique_ids);
                    }
                    sObj.put("socketGroupReplace", sg.getGroupReplaced().id);
                }
                if (sg.replacesGroup()) {
                    if (sg.getGroupThatReplaces().id == -1) {
                        sg.getGroupThatReplaces().id = sign_jsons(unique_ids);
                    }
                    sObj.put("socketGroupThatReplaces", sg.getGroupThatReplaces().id);
                }
                JSONArray gems_array = new JSONArray();
                for (Gem g : sg.getGems()) {
                    JSONObject gObj = new JSONObject();
                    if (g.id == -1) {
                        g.id = sign_jsons(unique_ids);
                    }
                    gObj.put("id", g.id);
                    gObj.put("name", g.getGemName());
                    gObj.put("level_added", g.getLevelAdded());
                    gObj.put("replaced", g.replaced);
                    gObj.put("replaces", g.replaces);
                    if (g.replaced) {
                        if (g.replacedWith.id == -1) {
                            g.replacedWith.id = sign_jsons(unique_ids);
                        }
                        gObj.put("replaceWith", g.replacedWith.id);
                    }
                    if (g.replaces) {
                        if (g.replacesGem.id == -1) {
                            g.replacesGem.id = sign_jsons(unique_ids);
                        }
                        gObj.put("replacesGem", g.replacesGem.id);
                    }
                    gems_array.put(gObj);
                }
                sObj.put("gem", gems_array);
                socket_group_array.put(sObj);

            }
            // now we need to connect data

            builds_array.put(bObj);
        }

        String build_to_json = builds_array.toString();

        System.out.println(build_to_json);
        String stringValueBase64Encoded = Base64.getEncoder().encodeToString(build_to_json.getBytes());
        System.out.println(build_to_json + " when Base64 encoded is: " + stringValueBase64Encoded);
        BufferedWriter bw = null;
        FileWriter fw = null;
        boolean done = false;
        try {
            fw = new FileWriter(POELevelFx.directory + "\\Path of Leveling\\Builds\\builds.txt");
            bw = new BufferedWriter(fw);
            bw.write(stringValueBase64Encoded);
            System.out.println("Done");
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
        if (is_new_version) {
            new UpdaterStage();
        } else {
            main = new Main_Stage(this);
        }
    }

    public void editor() {
        main.close();
        editor = new Editor_Stage(this);
    }

    public void launcher() {
        editor.close();
        main = new Main_Stage(this);
    }

    public void start(boolean zone_b, boolean xp, boolean level) {
        main.close();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Hook.run();
                GlobalKeyListener.run();
            }
        });
        new Controller(zone_b, xp, level, Main_Stage.buildLoaded);
    }

    private void addTrayIcon() throws AWTException {
        final TrayIcon trayIcon = new TrayIcon(new ImageIcon(getClass().getResource("/icons/humility.png")).getImage(),
                "Path of Leveling");

        // Create a pop-up menu components
        final PopupMenu popup = new PopupMenu();
        final MenuItem shutdownItem = new MenuItem("Exit");

        // Add components to pop-up menu
        popup.add(shutdownItem);

        trayIcon.setPopupMenu(popup);
        trayIcon.setImageAutoSize(true); // So the icon auto-sizes

        SystemTray.getSystemTray().add(trayIcon);

        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) { // Left click on tray icon (single click !)
                    Platform.runLater(() -> {
                        // code to show things...
                    });
                }
            }
        });

        shutdownItem.addActionListener(evnt -> {
            // code to exit the program
            // save stuff
            if (saveBuildsToMemory()) {
                System.out.println("Successfully saved checkpoint");
            } else {
                System.out.println("Checkpoint save failed");
            }
            try {
                GlobalScreen.unregisterNativeHook();
            } catch (NativeHookException e1) {
                e1.printStackTrace();
            }
            System.exit(20);
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        setUpDirectories();
        // remove below for debuging.
        // setUpLog();
//        if (checkForNewVersion()) {
        is_new_version = true;
        LauncherImpl.launchApplication(POELevelFx.class, UpdatePreloader.class, args);
//        } else {
        is_new_version = false;
        LauncherImpl.launchApplication(POELevelFx.class, NewFXPreloader.class, args);
//        }

    }

    public static void setUpDirectories() {
        POELevelFx.directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
        System.out.println(POELevelFx.directory + "\\Path of Leveling");
        if (!new File(POELevelFx.directory + "\\Path of Leveling").exists()) {
            new File(POELevelFx.directory + "\\Path of Leveling").mkdirs();
        }
        if (!new File(POELevelFx.directory + "\\Path of Leveling\\Builds").exists()) {
            new File(POELevelFx.directory + "\\Path of Leveling\\Builds").mkdirs();
        }
        if (!new File(POELevelFx.directory + "\\Path of Leveling\\settings.txt").isFile()) {
            try {
                new File(POELevelFx.directory + "\\Path of Leveling\\settings.txt").createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (!new File(POELevelFx.directory + "\\Path of Leveling\\log.txt").isFile()) {
            try {
                new File(POELevelFx.directory + "\\Path of Leveling\\log.txt").createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(POELevelFx.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static boolean checkForNewVersion() {
        URL url;
        String input = "";
        try {
            // get URL content

            String a = "https://raw.githubusercontent.com/karakasis/Path-of-Leveling/master/version.txt";
            url = new URL(a);
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            input = br.readLine();
            br.close();

            System.out.println("Done");

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String new_git_version = input;
        System.out.println("Current Version: " + POELevelFx.version);
        System.out.println("New Version: " + new_git_version);
        if (!POELevelFx.version.equals(new_git_version)) {
            POELevelFx.version = new_git_version;
            return true;
        } else {
            return false;
        }
    }

}
