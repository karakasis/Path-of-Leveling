/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.sun.deploy.util.StringUtils;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javax.imageio.ImageIO;
import jdk.nashorn.internal.parser.JSONParser;
import org.json.JSONArray;
import org.json.JSONObject;
import poe.level.data.Build;
import poe.level.data.Gem;
import poe.level.data.GemHolder;
import poe.level.data.SocketGroup;
import poe.level.fx.AddBuild_Controller;
import poe.level.fx.BuildEntry_Controller;
import poe.level.fx.MainApp_Controller;
import poe.level.fx.SocketGroupsPanel_Controller.SocketGroupLinker;


/**
 * FXML Controller class
 *
 * @author Christos
 */
public class BuildsPanel_Controller implements Initializable {

    public class BuildLinker{
        public BuildEntry_Controller pec;
        public Build build;
        public int id;
        public BuildsPanel_Controller root;
        public ArrayList<SocketGroupLinker> sgl_list = new ArrayList<>();
        
        public int hook(BuildsPanel_Controller root){
            this.root = root;
            id = BuildsPanel_Controller.sign();
            return id;
        }

        public void delete(){
            root.deleteBuild();
        }
        
        public void update(){
            root.update(id);
        }
    }
    
    private static HashSet<Integer> randomIDs;
    public static int sign(){
        if(randomIDs == null) randomIDs = new HashSet<>();
        int ran;
        do{
            ran = ThreadLocalRandom.current().nextInt(1,999999);
        }while(randomIDs.contains(ran));
        randomIDs.add(ran);
        return ran;
    }
    
    private static Image charToImage(String className, String asc){
        BufferedImage img = null;
        try {
            img = ImageIO.read(BuildsPanel_Controller.class.getResource("/classes/"+className+"/"+asc+".png"));
        } catch (IOException ex) {
            Logger.getLogger(BuildsPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return SwingFXUtils.toFXImage(img, null);
    }
    
    @FXML
    private JFXButton addBuild_button;
    @FXML
    private JFXButton removeBuild_button;
    @FXML
    private VBox buildsBox;
    
    private MainApp_Controller root;
    private SocketGroupsPanel_Controller sgc;
    private HashMap<Integer,BuildLinker> linker;
    private int activeBuildID;
    
    public void hook(MainApp_Controller root){
        this.root = root;
    }
    
    public void hookSG_Controller(SocketGroupsPanel_Controller sgc){
        this.sgc = sgc;
    }
    
    public void loadBuilds(ArrayList<Build> buildsLoaded){
        for(Build b : buildsLoaded){
            loadNewBuild(b);
        }
    }
    
    public void addNewSocketGroup(SocketGroup sg){
        linker.get(activeBuildID).build.getSocketGroup().add(sg);
    }
    
    public void removeSocketGroup(SocketGroup sg){
        linker.get(activeBuildID).build.getSocketGroup().remove(sg);
    }
    
    @FXML 
    private void addNewBuild(){
        root.buildPopup();
    }
    
    public boolean validate(){
        Build active_build = linker.get(activeBuildID).build;
        if(active_build.validate()){
            System.out.println("Success.");
            return true;
        }else{
            System.out.println("Build invalidate.");
            return false;
        }
    }
    
    public boolean validateAll(){
        for(BuildLinker bl : linker.values()){
            Build active_build = bl.build;
            if(!active_build.validate()){
                System.out.println("Build invalidate.");
                return false;
            }
        }
        return true;
    }
    
    public Build getCurrentBuild(){
        return linker.get(activeBuildID).build;
    }
    
    
    public int sign_jsons(HashSet<Integer> unique_ids){
        if(unique_ids == null) unique_ids = new HashSet<>();
        int ran;
        do{
            ran = ThreadLocalRandom.current().nextInt(1,999999);
        }while(unique_ids.contains(ran));
        unique_ids.add(ran);
        return ran;
    }
    
    public void saveBuild() throws IOException{
        
        
        
        JSONArray builds_array = new JSONArray();
        HashSet<Integer> unique_ids = new HashSet<>();
        for( BuildLinker bl : linker.values()){
            Build build = bl.build;
            JSONObject bObj = new JSONObject();
            bObj.put("buildName",build.getName());
            bObj.put("className",build.getClassName());
            bObj.put("ascendancyName",build.getAsc());
            bObj.put("level", 0); //<change
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

        try {
            fw = new FileWriter(POELevelFx.directory+"\\Path of Leveling\\Builds\\builds.txt");
            bw = new BufferedWriter(fw);
            bw.write(stringValueBase64Encoded);
            System.out.println("Done");
        } catch (IOException e) {
            e.printStackTrace();
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

	
        //PrintWriter out = new PrintWriter(POELevelFx.directory+"\\Builds\\builds.txt");
        //out.println(stringValueBase64Encoded);
        //gson.toJson(linker.get(activeBuildID).build,new FileWriter(POELevelFx.directory+"\\Builds\\builds.txt"));
    }
    
    public void addNewBuild(String buildName, String className, String ascendancyName){
        BuildLinker bl = new BuildLinker();
        int linker_id = bl.hook(this);
        linker.put(linker_id,bl);
        
        removeBuild_button.setDisable(false);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
        try {
            //this will add the AnchorPane to the VBox
            buildsBox.getChildren().add(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        bl.pec = loader.<BuildEntry_Controller>getController(); //add controller to the linker class
        bl.pec.init(charToImage(className,ascendancyName), buildName, ascendancyName, bl);
        bl.build = new Build(buildName,className,ascendancyName);
        for(SocketGroup sg : bl.build.getSocketGroup()){
            SocketGroupLinker sgl = sgc.new SocketGroupLinker(sg);
            //sgl.sg = sg;
            //sgl.generateLabel();
            bl.sgl_list.add(sgl);
        }
        POELevelFx.buildsLoaded.add(bl.build);
    }
    
    public void loadNewBuild(Build build){
        BuildLinker bl = new BuildLinker();
        int linker_id = bl.hook(this);
        linker.put(linker_id,bl);
        
        removeBuild_button.setDisable(false);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
        try {
            //this will add the AnchorPane to the VBox
            buildsBox.getChildren().add(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        bl.pec = loader.<BuildEntry_Controller>getController(); //add controller to the linker class
        bl.pec.init(charToImage(build.getClassName(),build.getAsc())
                , build.getName(), build.getAsc(), bl);
        bl.build = build;
        for(SocketGroup sg : bl.build.getSocketGroup()){
            SocketGroupLinker sgl = sgc.new SocketGroupLinker(sg);
            //sgl.sg = sg;
            //sgl.generateLabel();
            bl.sgl_list.add(sgl);
        } 
        
    }
    
    @FXML
    private void deleteBuild(){
        BuildLinker bl = linker.get(activeBuildID);
        POELevelFx.buildsLoaded.remove(bl.build);
        sgc.reset();
        buildsBox.getChildren().remove(bl.pec.getRoot()); // remove from the UI
        linker.remove(bl.id); //remove from data
        // and also remove from file system?
        
    }
    
    public void update(int id){
        if(id!=activeBuildID){
            activeBuildID = id;
            for (BuildLinker bl : linker.values()) {
                if(bl.id!=id){
                    bl.pec.reset();
                }else{
                    //update the sgroup controller with new build info
                    GemHolder.getInstance().className = getCurrentClass(id);
                    sgc.hookBuild_Controller(this);
                    sgc.update(bl.sgl_list);
                }
            }
            //root.buildChanged(id);
            
        }
    }

    public ArrayList<SocketGroup> getSocketGroups(int id){
        BuildLinker bl = linker.get(id);
        return bl.build.getSocketGroup();
    }
    
    public String getCurrentClass(int id){
        BuildLinker bl = linker.get(id);
        return bl.build.getClassName();
    }
     /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
        linker = new HashMap<>();
        //loadBuildsFromMemory();
    }  

    
}
