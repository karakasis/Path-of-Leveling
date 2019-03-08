/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;
import poe.level.data.*;
import poe.level.fx.SocketGroupsPanel_Controller.SocketGroupLinker;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * FXML Controller class
 *
 * @author Christos
 */
public class BuildsPanel_Controller implements Initializable {
    private final Logger m_logger = Logger.getLogger(BuildsPanel_Controller.class.getName());

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

        public void requestNameChange(String newName){
            build.buildName = newName;
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

    @FXML
    private JFXButton addBuild_button;
    @FXML
    private JFXButton removeBuild_button;
    @FXML
    private JFXListView buildsBox;

    private MainApp_Controller root;
    private SocketGroupsPanel_Controller sgc;
    private HashMap<Integer,BuildLinker> linker;
    private int activeBuildID;
    public String lastbuild_invalidated;
    public int lastbuild_invalidatedID;

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
            if(!active_build.isValid){
                //signal a graphic change
                active_build.isValid = true;
                updateBuildValidationBanner(activeBuildID);
            }
            return true;
        }else{
            System.out.println("Build invalidate.");
            lastbuild_invalidatedID = activeBuildID;
            return false;
        }
    }

    public boolean validateAll(){
        for(BuildLinker bl : linker.values()){
            Build active_build = bl.build;
            if(!active_build.validate()){
                if(active_build.isValid){
                    //active_build.isValid = false;
                    System.out.println("Build invalidate.");
                    lastbuild_invalidatedID = bl.id;
                    //updateBuildValidationBanner(bl.id);
                    return false;
                }

            }else if(active_build.validate() && !active_build.isValid){
                //if it passed validation but was marked as non valid
                //signal a graphic change
                active_build.isValid = true;
                updateBuildValidationBanner(bl.id);
            }
        }
        return true;
    }

    public String validateError(){
        Build active_build = linker.get(lastbuild_invalidatedID).build;
        lastbuild_invalidated = active_build.getName();
        return active_build.validate_failed_string();
    }

    public String validateAllError(){

        lastbuild_invalidated = "";
        for(BuildLinker bl : linker.values()){
            Build active_build = bl.build;
            if(!active_build.validate()){
                lastbuild_invalidated = active_build.getName();
                lastbuild_invalidatedID = bl.id; //not sure what this one does in this method.
                return active_build.validate_failed_string();
            }
        }
        return null;
    }

    public void setBuildToNonValid(){
        Build active_build = linker.get(lastbuild_invalidatedID).build;
        active_build.isValid = false;
        active_build.patch_missing_sgroups();
        updateBuildValidationBanner(lastbuild_invalidatedID);
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

    public void saveBuild() throws IOException {

        JSONArray builds_array = new JSONArray();
        HashSet<Integer> unique_ids = new HashSet<>();
        for( BuildLinker bl : linker.values()){
          buildTo64(bl.build, builds_array, unique_ids);
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
    }

    public String allTo64(){

        JSONArray builds_array = new JSONArray();
        HashSet<Integer> unique_ids = new HashSet<>();
        for( BuildLinker bl : linker.values()){
            buildTo64(bl.build, builds_array, unique_ids);
        }

        String build_to_json = builds_array.toString();
        System.out.println(build_to_json);
        String stringValueBase64Encoded = Base64.getEncoder().encodeToString(build_to_json.getBytes());
        System.out.println(build_to_json  + " when Base64 encoded is: " + stringValueBase64Encoded);
        return stringValueBase64Encoded;
    }

    public String activeTo64(){
        Build build = linker.get(activeBuildID).build;

        JSONArray builds_array = new JSONArray();

        HashSet<Integer> unique_ids = new HashSet<>();

        buildTo64(build, builds_array, unique_ids);

        String build_to_json = builds_array.toString();

        System.out.println(build_to_json);
        String stringValueBase64Encoded = Base64.getEncoder().encodeToString(build_to_json.getBytes());
        System.out.println(build_to_json  + " when Base64 encoded is: " + stringValueBase64Encoded);
        return stringValueBase64Encoded;
    }

    private void buildTo64(Build build, JSONArray builds_array, HashSet<Integer> unique_ids) {
      JSONObject bObj = new JSONObject();
      bObj.put("buildName",build.getName());
      bObj.put("className",build.getClassName());
      if(build.getAsc().equals("Assasin")) System.err.println("not fixed typo");
      bObj.put("ascendancyName",build.getAsc());
      bObj.put("isValid", build.isValid);
      bObj.put("level", build.getCharacterLevel()); //<change
      bObj.put("characterName",build.getCharacterName());

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
        sObj.put("note",sg.getNote());
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

    public boolean loadBuildsFromPastebin(String rawPaste){
        //pseudo for loop loads builds and panels put them
        //into buildlinker and add buildlinker to the list
        //TODO: remember to sign the build with the static method
        ArrayList<Build> buildsLoaded = new ArrayList<>();

        byte[] byteValueBase64Decoded = null;
        try{
            byteValueBase64Decoded = Base64.getDecoder().decode(rawPaste.trim());
        }catch(java.lang.IllegalArgumentException e){
            e.printStackTrace();
            return false;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        String stringValueBase64Decoded = new String(byteValueBase64Decoded);
//        System.out.println(stringValueBase64Decoded);

        //JSONArray obj = new JsonParser().parse(stringValueBase64Encoded).getAsJsonArray();
        try{

            JSONArray builds_array = new JSONArray(stringValueBase64Decoded);
            for (int i = 0; i < builds_array.length(); i++) {
                    JSONObject bObj = builds_array.getJSONObject(i);
                    String ascName = "";
                    if(bObj.getString("ascendancyName").equals("Assasin")){
                        System.err.println("Replaced assassin typo.");
                        ascName = "Assassin";
                    }else{
                        ascName = bObj.getString("ascendancyName");
                    }
                    Build build = new Build(
                            bObj.getString("buildName"),
                            bObj.getString("className"),
                            ascName
                    );
                    //when importing character name will not be passed.
                    build.setCharacterName("");
                    build.setCharacterLevel(0);
                    try{
                        //bObj.get("hasPob");
                        build.isValid = bObj.getBoolean("isValid");
                    }catch(org.json.JSONException e){
                        //if it doesnt exist prob its valid from earlier versions
                        build.isValid = true;
                    }
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
                        try{
                            //bObj.get("hasPob");
                            sg.addNote(sObj.getString("note"));
                        }catch(org.json.JSONException e){
                            sg.addNote("");
                        }
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

        }catch(Exception e){
            m_logger.log(Level.WARNING, "Exception while parsing POB response.", e);
            return false;
        }

        for(Build bl : buildsLoaded){
            loadNewBuild(bl);
            POELevelFx.buildsLoaded.add(bl); //this line is extra and not included in the method above
        }
        return true;


    }

    public Build addNewBuildFromPOB(String buildName, String className, String ascendancyName, ArrayList<ArrayList<String>> info){
        //addNewBuild(buildName,className,ascendancyName);
        Build pobBuild = new Build(buildName,className,ascendancyName); //make empty build

        pobBuild.setCharacterName("");
        pobBuild.setCharacterLevel(0);
        //main app controller will take care of pob link and hasPob tag
        //actually later i discovered that this brings a new bug
        //if u set it no non valid that means that during validation this build will be "accepted"
        //and be passed to write to memory, but then since it didnt passed the validation in the first place
        //it may have null values, most likely in sg.getactivegem. until then set it to true.
        //pobBuild.isValid = false; //obv needs checking after import from pob.
        pobBuild.isValid = true; //obv needs checking after import from pob.

        GemHolder.getInstance().className = pobBuild.getClassName();
        for(ArrayList<String> sgList : info){
            SocketGroup sg = new SocketGroup();
            for( String gemString : sgList){
                Gem gem = GemHolder.getInstance().createGemFromCache(gemString,pobBuild.getClassName());
                if(gem.isActive && sg.getActiveGem() == null){
                    sg.setActiveGem(gem);
                }
                sg.getGems().add(gem);//***check line 324 in GemsPanel_Controller;
            }
            pobBuild.getSocketGroup().add(sg);
        }
        //but we also need to link the build to the build panel
        //we do this by load method
        loadNewBuild(pobBuild);
        POELevelFx.buildsLoaded.add(pobBuild); //add reference to build list.
        return pobBuild;
    }

    public void addNewBuild(String buildName, String className, String ascendancyName){
        BuildLinker bl = new BuildLinker();
        int linker_id = bl.hook(this);
        linker.put(linker_id,bl);

        removeBuild_button.setDisable(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
        try {
            //this will add the AnchorPane to the VBox
            //buildsBox.getChildren().add(loader.load());
            buildsBox.getItems().add(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        bl.pec = loader.<BuildEntry_Controller>getController(); //add controller to the linker class
        bl.pec.init(Util.charToImage(className,ascendancyName), buildName, ascendancyName, bl);
        bl.build = new Build(buildName,className,ascendancyName);

        //every new build should be considered non-valid?
        //bl.build.isValid = false;
        bl.build.isValid = true;
        //actually later i discovered that this brings a new bug
        //if u set it no non valid that means that during validation this build will be "accepted"
        //and be passed to write to memory, but then since it didnt passed the validation in the first place
        //it may have null values, most likely in sg.getactivegem. until then set it to true.
        //also setting it to valid will cause it to be put through the validation process.

        bl.build.hasPob = false;
        bl.build.pobLink = "";
        for(SocketGroup sg : bl.build.getSocketGroup()){
            SocketGroupLinker sgl = sgc.new SocketGroupLinker(sg);
            //sgl.sg = sg;
            //sgl.generateLabel();
            bl.sgl_list.add(sgl);
        }
        POELevelFx.buildsLoaded.add(bl.build);
        root.toggleAllBuilds(true);
    }

    public void loadNewBuild(Build build){
        BuildLinker bl = new BuildLinker();
        int linker_id = bl.hook(this);
        linker.put(linker_id,bl);

        removeBuild_button.setDisable(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
        try {
            //this will add the AnchorPane to the VBox
            //buildsBox.getChildren().add(loader.load());
            buildsBox.getItems().add(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        bl.pec = loader.<BuildEntry_Controller>getController(); //add controller to the linker class
        bl.pec.init(Util.charToImage(build.getClassName(),build.getAsc())
                , build.getName(), build.getAsc(), bl);
        if(!build.isValid) bl.pec.initInvalidBackgroundColor();
        bl.build = build;
        //bl.build.isValid = true;
        for(SocketGroup sg : bl.build.getSocketGroup()){
            SocketGroupLinker sgl = sgc.new SocketGroupLinker(sg);
            //sgl.sg = sg;
            //sgl.generateLabel();
            bl.sgl_list.add(sgl);
        }

        root.toggleAllBuilds(true);
    }

    @FXML
    private void deleteBuild(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Build delete");
        alert.setHeaderText("You are about to delete Build : "+ linker.get(activeBuildID).build.getName());
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
           BuildLinker bl = linker.get(activeBuildID);
            if(!bl.build.isValid){
                root.toggleFooterVisibility(true);
            }
            POELevelFx.buildsLoaded.remove(bl.build);
            sgc.reset();
            //buildsBox.getChildren().remove(bl.pec.getRoot()); // remove from the UI
            buildsBox.getItems().remove(bl.pec.getRoot()); // remove from the UI
            buildsBox.getSelectionModel().clearSelection();
            linker.remove(bl.id); //remove from data
            root.toggleActiveBuilds(false);
            if(POELevelFx.buildsLoaded.isEmpty()){
                root.toggleAllBuilds(false);
            }
        }

        // and also remove from file system?

    }

    private void updateBuildValidationBanner(int id){
        BuildLinker bl = linker.get(id);
        bl.pec.setValidBackgroundColor(bl.build.isValid , id==activeBuildID);
        root.toggleFooterVisibility(bl.build.isValid);
    }

    public void update(int id){
        if(id!=activeBuildID){
            activeBuildID = id;
            root.toggleActiveBuilds(true);
            for (BuildLinker bl : linker.values()) {
                if(bl.id!=id){
                    bl.pec.reset(bl.build.isValid);
                }else{
                    //update the sgroup controller with new build info
                    GemHolder.getInstance().className = getCurrentClass(id);
                    sgc.hookBuild_Controller(this);
                    sgc.update(bl.sgl_list);
                    root.toggleFooterVisibility(bl.build.isValid);
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
        buildsBox.getSelectionModel().getSelectedItems().addListener(new ListChangeListener() {
            @Override
            public void onChanged(Change change) {
                boolean added = false;
                boolean removed = false;
                while (change.next()) {
                    if (change.wasAdded()) {
                        added = true;
                    } else if (change.wasRemoved()) {
                        removed = true;
                    }
                }
                removeBuild_button.setDisable(removed && !added);
            }
        });
    }


}
