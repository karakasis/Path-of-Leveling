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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.glfw.GLFWKeyCallback;
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
import poe.level.keybinds.Hook;

/**
 *
 * @author Christos
 */
public class POELevelFx extends Application {
    
    public static String directory;
    public static String gemDir;
    public static ArrayList<Build> buildsLoaded;
    private Stage zone;
    private Stage exp;
    private Stage main;
    private Stage editor;
    private Stage leveling;
    private GLFWKeyCallback keyCallback;
    
    @Override
    public void init() throws Exception {
        addTrayIcon();
        
        System.out.println(org.lwjgl.Version.getVersion());
        POELevelFx.directory = new JFileChooser().getFileSystemView().getDefaultDirectory().toString();
       /*
        AppDirs appDirs = AppDirsFactory.getInstance();
        POELevelFx.directory = appDirs.getUserDataDir("POE-Level-FX", "0.1", "rainy");
        
        if (!new File(POELevelFx.directory + "\\GemIcons").exists()) {
            new File(POELevelFx.directory + "\\GemIcons").mkdirs();
        }
        if (!new File(POELevelFx.directory + "\\Builds").exists()) {
            new File(POELevelFx.directory + "\\Builds").mkdirs();
        }
        POELevelFx.gemDir = POELevelFx.directory;
        gemDir+="\\GemIcons\\";
*/
        System.out.println(POELevelFx.directory + "\\Path of Leveling");
        if (!new File(POELevelFx.directory + "\\Path of Leveling").exists()) {
            new File(POELevelFx.directory + "\\Path of Leveling").mkdirs();
        }
        if (!new File(POELevelFx.directory + "\\Path of Leveling\\Builds").exists()) {
            new File(POELevelFx.directory + "\\Path of Leveling\\Builds").mkdirs();
        }
        if (!new File(POELevelFx.directory + "\\Path of Leveling\\settings.txt").isFile()) {
            new File(POELevelFx.directory + "\\Path of Leveling\\settings.txt").createNewFile();
        }
        if (!new File(POELevelFx.directory + "\\Path of Leveling\\config.properties").isFile()) {
            new File(POELevelFx.directory + "\\Path of Leveling\\config.properties").createNewFile();
            
            Properties prop = new Properties();
            OutputStream output = null;

            try {

                    output = new FileOutputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");

                    // set the properties value
                    prop.setProperty("zones-toggle", "false");
                    Preferences_Controller.zones_toggle = false;
                    double a = 10;
                    prop.setProperty("zones-slider", String.valueOf(a));
                    Preferences_Controller.zones_slider = a;
                    prop.setProperty("zones-hotkey-show_hide", "F5");
                    try{
                        KeyCombination keyCombination = KeyCombination.keyCombination("F5");
                        System.out.println("key code : " + keyCombination.getName());
                        Preferences_Controller.zones_hotkey_show_hide_key = keyCombination;
                    }catch(IllegalArgumentException e){
                        System.out.println(":incorect:");
                        Preferences_Controller.zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
                    }
                    a = 15;
                    Preferences_Controller.level_slider = a;
                    prop.setProperty("level-slider", String.valueOf(a));
                    prop.setProperty("level-hotkey-remind", "F4");
                    prop.setProperty("poe-dir","");
                    Preferences_Controller.poe_log_dir = "";
                    try{
                        KeyCombination keyCombination = KeyCombination.keyCombination("F4");
                        System.out.println("key code : " + keyCombination.getName());
                        Preferences_Controller.level_hotkey_remind_key = keyCombination;
                    }catch(IllegalArgumentException e){
                        System.out.println(":incorect:");
                        Preferences_Controller.level_hotkey_remind_key = KeyCombination.NO_MATCH;
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

            }
      }else{
            
            Properties prop = new Properties();
            InputStream input = null;
            String zones_hotkey_show_hide = "";
            String level_hotkey_remind = "";
            try {

                    input = new FileInputStream(POELevelFx.directory + "\\Path of Leveling\\config.properties");

                    // load a properties file
                    prop.load(input);
                    Preferences_Controller.zones_toggle = Boolean.parseBoolean(prop.getProperty("zones-toggle"));
                    Preferences_Controller.zones_slider = Double.parseDouble(prop.getProperty("zones-slider"));
                    zones_hotkey_show_hide = prop.getProperty("zones-hotkey-show_hide");
                    Preferences_Controller.level_slider = Double.parseDouble(prop.getProperty("level-slider"));
                    level_hotkey_remind = prop.getProperty("level-hotkey-remind");
                    
                    Preferences_Controller.poe_log_dir = prop.getProperty("poe-dir") + "\\logs\\Client.txt";
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
            
            try{
                KeyCombination keyCombination = KeyCombination.keyCombination(zones_hotkey_show_hide);
                System.out.println("key code : " + keyCombination.getName());
                Preferences_Controller.zones_hotkey_show_hide_key = keyCombination;
            }catch(IllegalArgumentException e){
                System.out.println(":incorect:");
                Preferences_Controller.zones_hotkey_show_hide_key = KeyCombination.NO_MATCH;
            }
            
            try{
                KeyCombination keyCombination = KeyCombination.keyCombination(level_hotkey_remind);
                System.out.println("key code : " + keyCombination.getName());
                Preferences_Controller.level_hotkey_remind_key = keyCombination;
            }catch(IllegalArgumentException e){
                System.out.println(":incorect:");
                Preferences_Controller.level_hotkey_remind_key = KeyCombination.NO_MATCH;
            }
      }
        
        loadActsFromMemory();
        loadGemsFromMemory();
        loadBuildsFromMemory();
        
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
    
    private StringBuilder hack(){
        BufferedReader br = null;
        StringBuilder sb = null;
        try {
            br = new BufferedReader(new FileReader("gem.txt"));
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
    
    public void close(){
        //exp.close();
        main.close();
        //zone.close();
    }
    
    private void loadActsFromMemory(){
        InputStream in = POELevelFx.class.getResourceAsStream("/json/data.json");
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
                a.putZone(z);
            }
            ActHandler.getInstance().putAct(a);
        }
    }
    
    private void loadGemsFromMemory(){
                
        InputStream inG = POELevelFx.class.getResourceAsStream("/json/gems.json");
        Scanner sG = new Scanner(inG).useDelimiter("\\A");
        String jsonstringG = sG.hasNext() ? sG.next() : "";


        //JSONObject objG = new JSONObject(jsonstringG);
        JSONArray arrG = new JSONArray(jsonstringG);

        //GemHolder gemsH = new GemHolder();
        for (int i = 0; i < arrG.length(); i++)
        {
            JSONObject gemObj  = arrG.getJSONObject(i);
            Gem gem = new Gem();
            
            gem.name= gemObj.getString("name");
            System.out.println(gem.name);
            
            gem.required_lvl= gemObj.getInt("required_lvl");
            gem.isVaal=gemObj.getBoolean("isVaal");
            //gem.id = gemObj.getInt("id");
            gem.id = -1;
            gem.color= gemObj.getString("color");
            gem.iconPath= gemObj.getString("iconPath");
            gem.isRewarded = gemObj.getBoolean("isReward");
            
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

                }
                try{
                    gem.reward.quest_name= rewardObj.getString("quest_name");
                }catch(JSONException e){

                }
                try{
                    gem.reward.npc= rewardObj.getString("npc");
                }catch(JSONException e){

                }
                try{
                    gem.reward.act = rewardObj.getInt("act");
                }catch(JSONException e){

                }
                try{
                    gem.reward.town= rewardObj.getString("town");
                }catch(JSONException e){

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
                    System.out.println(gem.getGemName());
                }
                try{
                    buy_info.quest_name= buyObj.getString("quest_name");
                }catch(JSONException e){

                }
                try{
                    buy_info.npc= buyObj.getString("npc");
                }catch(JSONException e){

                }
                try{
                    buy_info.act = buyObj.getInt("act");
                }catch(JSONException e){

                }
                try{
                    buy_info.town= buyObj.getString("town");
                }catch(JSONException e){

                }
                gem.buy.add(buy_info);
            }
           
            /* uncomment to download images. also uncomment gemdir on top of class
            //CHECK cached images
            if (!new File(gemDir+""+gem.name+".png").exists()) {
                BufferedImage image = null;
                try {

                    URL url = new URL(gem.iconPath);
                    image = ImageIO.read(url);

                    ImageIO.write(image, "png",new File(gemDir+""+gem.name+".png"));

                    gem.gemIcon = SwingFXUtils.toFXImage(image, null);
                } catch (IOException e) {
                        e.printStackTrace();
                }
                System.err.println("Image not found and redownloaded: "+gemDir+""+gem.name+".png");
                
            }else{
                BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File(gemDir+""+gem.name+".png"));
                        
                        gem.gemIcon = SwingFXUtils.toFXImage(img, null);
                    } 
                    catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            */
            BufferedImage img = null;
                try {
                    img = ImageIO.read(getClass().getResource("/gems/"+gem.name+".png"));
                    gem.gemIcon = SwingFXUtils.toFXImage(img, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            
            gem.resizeImage();
            
            GemHolder.getInstance().putGem(gem);
            double a = (double)i/arrG.length();
            a = a * 100.0 ;
            
            notifyPreloader(new NewFXPreloader.ProgressNotification(a));
        }
    }
    
    private void loadBuildsFromMemory(){
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
                Build build = new Build(
                        bObj.getString("buildName"),
                        bObj.getString("className"),
                        bObj.getString("ascendancyName")
                );
                build.characterName = bObj.getString("characterName");
                build.level = bObj.getInt("level");
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
                        if(gemName.equals("Detonate Mines")){
                            System.out.println();
                        }
                        Gem gem = GemHolder.getInstance().createGemFromCache(gemName,build.getClassName());
                        if(gem == null){
                            System.out.println();
                        }
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
        
        System.out.println(stringValueBase64Encoded  + " when decoded is: " + stringValueBase64Decoded);
            
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
            bObj.put("ascendancyName",build.getAsc());
            bObj.put("level", build.level); //<change
            bObj.put("characterName",build.characterName);
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
        System.out.println(build_to_json);
        String stringValueBase64Encoded = Base64.getEncoder().encodeToString(build_to_json.getBytes());
        System.out.println(build_to_json  + " when Base64 encoded is: " + stringValueBase64Encoded);
        BufferedWriter bw = null;
        FileWriter fw = null;
        boolean done = false;
        try {
            fw = new FileWriter(POELevelFx.directory+"\\Path of Leveling\\Builds\\builds.txt");
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
        main = new Main_Stage(this);
        //zone = new ZoneOverlay_Stage();
        //exp = new LevelOverlay_Stage();
    }
    
    public void editor(){
        main.close();
        editor = new Editor_Stage(this);
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
        Controller controller = new Controller(zone_b, xp, level, Main_Stage.buildLoaded);
        
    }
    
    private void addTrayIcon() throws AWTException {
    final TrayIcon trayIcon = new TrayIcon(new ImageIcon(getClass().getResource("/icons/humility.png")).getImage(), "Path of Leveling");

    // Create a pop-up menu components
    final PopupMenu popup = new PopupMenu();
    final MenuItem shutdownItem = new MenuItem("Exit");

    //Add components to pop-up menu
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
}
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(POELevelFx.class, NewFXPreloader.class, args);
       
    }
    
}
