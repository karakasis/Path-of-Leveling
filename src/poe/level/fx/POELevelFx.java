/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.sun.javafx.application.LauncherImpl;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.WritableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import net.harawata.appdirs.AppDirs;
import net.harawata.appdirs.AppDirsFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import poe.level.data.Act;
import poe.level.data.ActHandler;
import poe.level.data.Build;
import poe.level.data.Controller;
import poe.level.data.Gem;
import poe.level.data.GemHolder;
import poe.level.data.SocketGroup;
import poe.level.data.Tail;
import poe.level.data.Zone;
import poe.level.fx.overlay.GemOverlay_Stage;
import poe.level.fx.overlay.LevelOverlay_Stage;
import poe.level.fx.overlay.ZoneOverlay_Stage;
import poe.level.fx.overlay.ZoneOverlay_Controller;

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
    
    @Override
    public void init() throws Exception {
        
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
            try{
                gem.quest_name= gemObj.getString("quest_name");
            }catch(JSONException e){
                
            }
            try{
            gem.npc= gemObj.getString("npc");
            }catch(JSONException e){
                
            }
            try{
            gem.act = gemObj.getInt("act");
            }catch(JSONException e){
                
            }
            try{
            gem.town= gemObj.getString("town");
            }catch(JSONException e){
                
            }
            gem.required_lvl= gemObj.getInt("required_lvl");
            gem.isVaal=gemObj.getBoolean("isVaal");
            //gem.id = gemObj.getInt("id");
            gem.id = -1;
            gem.color= gemObj.getString("color");
            gem.iconPath= gemObj.getString("iconPath");
            
            try{
                JSONArray chars = gemObj.getJSONArray("available_to");
                for(int j=0;j<chars.length();j++){
                    gem.putChar(chars.getString(j));
                }
            }catch(JSONException e){
                
            }
            /*
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
                System.err.println("Image not found and redownloaded: " + gemDir+""+gem.name+".png");
                
            }else{
                BufferedImage img = null;
                    try {
                        img = ImageIO.read(new File(gemDir+""+gem.name+".png"));
                        
                        gem.gemIcon = SwingFXUtils.toFXImage(img, null);
                    } 
                    catch (IOException e) {
                        e.printStackTrace();
                    }
            }*/
            
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
            
        byte[] byteValueBase64Decoded = Base64.getDecoder().decode(stringValueBase64Encoded);
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
                        Gem gem = GemHolder.getInstance().createGemFromCache(gemName);
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
        Controller controller = new Controller(zone_b, xp, level, Main_Stage.buildLoaded);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(POELevelFx.class, NewFXPreloader.class, args);
       
    }
    
}
