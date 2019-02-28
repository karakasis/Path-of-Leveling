/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.jfoenix.controls.JFXButton;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import poe.level.data.Build;
import poe.level.data.Gem;
import poe.level.data.GemHolder;
import poe.level.data.SocketGroup;
import poe.level.fx.SocketGroupsPanel_Controller.SocketGroupLinker;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class BuildsPanel_Controller implements Initializable {

    public class BuildLinker {
        public BuildEntry_Controller pec;
        public Build build;
        public int id;
        public BuildsPanel_Controller root;
        public ArrayList<SocketGroupLinker> sgl_list = new ArrayList<>();

        public int hook(BuildsPanel_Controller root) {
            this.root = root;
            id = BuildsPanel_Controller.sign();
            return id;
        }

        public void update() {
            root.update(id);
        }
    }

    private static HashSet<Integer> randomIDs;

    public static int sign() {
        if (randomIDs == null)
            randomIDs = new HashSet<>();
        int ran;
        do {
            ran = ThreadLocalRandom.current().nextInt(1, 999999);
        } while (randomIDs.contains(ran));
        randomIDs.add(ran);
        return ran;
    }

    private static Image charToImage(String className, String asc) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(BuildsPanel_Controller.class.getResource("/classes/" + className + "/" + asc + ".png"));
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
    private HashMap<Integer, BuildLinker> linker;
    private int activeBuildID;
    public String lastbuild_invalidated;
    public int lastbuild_invalidatedID;

    public void hook(MainApp_Controller root) {
        this.root = root;
    }

    public void hookSG_Controller(SocketGroupsPanel_Controller sgc) {
        this.sgc = sgc;
    }

    public void loadBuilds(ArrayList<Build> buildsLoaded) {
        for (Build b : buildsLoaded) {
            loadNewBuild(b);
        }
    }

    public void addNewSocketGroup(SocketGroup sg) {
        linker.get(activeBuildID).build.getSocketGroup().add(sg);
    }

    public void removeSocketGroup(SocketGroup sg) {
        linker.get(activeBuildID).build.getSocketGroup().remove(sg);
    }

    @FXML
    private void addNewBuild() {
        root.buildPopup();
    }

    public boolean validate() {
        Build active_build = linker.get(activeBuildID).build;
        if (active_build.validate()) {
            System.out.println("Success.");
            if (!active_build.isValid) {
                // signal a graphic change
                active_build.isValid = true;
                updateBuildValidationBanner(activeBuildID);
            }
            return true;
        } else {
            System.out.println("Build invalidate.");
            lastbuild_invalidatedID = activeBuildID;
            return false;
        }
    }

    public boolean validateAll() {
        for (BuildLinker bl : linker.values()) {
            Build active_build = bl.build;
            if (!active_build.validate()) {
                if (active_build.isValid) {
                    // active_build.isValid = false;
                    System.out.println("Build invalidate.");
                    lastbuild_invalidatedID = bl.id;
                    // updateBuildValidationBanner(bl.id);
                    return false;
                }

            } else if (active_build.validate() && !active_build.isValid) {
                // if it passed validation but was marked as non valid
                // signal a graphic change
                active_build.isValid = true;
                updateBuildValidationBanner(bl.id);
            }
        }
        return true;
    }

    public String validateError() {
        Build active_build = linker.get(lastbuild_invalidatedID).build;
        lastbuild_invalidated = active_build.getName();
        return active_build.validate_failed_string();
    }

    public String validateAllError() {

        lastbuild_invalidated = "";
        for (BuildLinker bl : linker.values()) {
            Build active_build = bl.build;
            if (!active_build.validate()) {
                lastbuild_invalidated = active_build.getName();
                lastbuild_invalidatedID = bl.id; // not sure what this one does in this method.
                return active_build.validate_failed_string();
            }
        }
        return null;
    }

    // for better update-ability i need to make this client sided and not attached
    // to json.
    // some thoughts: if you try to open the app and not visit editor, a non-valid
    // build will not show up as non valid.
    // so basically since it will not be saved the app will not know that its not
    // valid.
    // if i save the non valid tag to builds, all users will have valid builds b4
    // the update, so it wont mess up anything.
    // i just need to check again with null exceptions
    public void setBuildToNonValid() {
        Build active_build = linker.get(lastbuild_invalidatedID).build;
        active_build.isValid = false;
        updateBuildValidationBanner(lastbuild_invalidatedID);
    }

    public Build getCurrentBuild() {
        return linker.get(activeBuildID).build;
    }

    public int sign_jsons(HashSet<Integer> unique_ids) {
        if (unique_ids == null)
            unique_ids = new HashSet<>();
        int ran;
        do {
            ran = ThreadLocalRandom.current().nextInt(1, 999999);
        } while (unique_ids.contains(ran));
        unique_ids.add(ran);
        return ran;
    }

    public void saveBuild() throws IOException {

        JSONArray builds_array = new JSONArray();
        HashSet<Integer> unique_ids = new HashSet<>();
        for (BuildLinker bl : linker.values()) {
            Build build = bl.build;
            JSONObject bObj = new JSONObject();
            bObj.put("isValid", build.isValid);
            bObj.put("buildName", build.getName());
            bObj.put("className", build.getClassName());
            bObj.put("ascendancyName", build.getAsc());
            bObj.put("level", build.level); // <change
            bObj.put("characterName", build.characterName);

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

                sObj.put("note", sg.getNote());
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

        // Gson gson = new Gson();
        // String build_to_json = gson.toJson(linker.get(activeBuildID).build);
        System.out.println(build_to_json);
        String stringValueBase64Encoded = Base64.getEncoder().encodeToString(build_to_json.getBytes());
        System.out.println(build_to_json + " when Base64 encoded is: " + stringValueBase64Encoded);
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(POELevelFx.directory + "\\Path of Leveling\\Builds\\builds.txt");
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

        // PrintWriter out = new
        // PrintWriter(POELevelFx.directory+"\\Builds\\builds.txt");
        // out.println(stringValueBase64Encoded);
        // gson.toJson(linker.get(activeBuildID).build,new
        // FileWriter(POELevelFx.directory+"\\Builds\\builds.txt"));
    }

    public String allTo64() {

        JSONArray builds_array = new JSONArray();
        HashSet<Integer> unique_ids = new HashSet<>();
        for (BuildLinker bl : linker.values()) {
            Build build = bl.build;
            JSONObject bObj = new JSONObject();
            bObj.put("buildName", build.getName());
            bObj.put("className", build.getClassName());
            bObj.put("ascendancyName", build.getAsc());
            bObj.put("isValid", build.isValid);
            bObj.put("level", build.level); // <change
            bObj.put("characterName", build.characterName);

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
        return stringValueBase64Encoded;
    }

    public String activeTo64() {
        Build build = linker.get(activeBuildID).build;

        JSONArray builds_array = new JSONArray();

        HashSet<Integer> unique_ids = new HashSet<>();

        JSONObject bObj = new JSONObject();
        bObj.put("buildName", build.getName());
        bObj.put("className", build.getClassName());
        bObj.put("ascendancyName", build.getAsc());
        bObj.put("isValid", build.isValid);
        bObj.put("level", build.level); // <change
        bObj.put("characterName", build.characterName);

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
            sObj.put("note", sg.getNote());
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

        String build_to_json = builds_array.toString();

        System.out.println(build_to_json);
        String stringValueBase64Encoded = Base64.getEncoder().encodeToString(build_to_json.getBytes());
        System.out.println(build_to_json + " when Base64 encoded is: " + stringValueBase64Encoded);
        return stringValueBase64Encoded;
    }

    public boolean loadBuildsFromPastebin(String rawPaste) {
        // pseudo for loop loads builds and panels put them
        // into buildlinker and add buildlinker to the list
        // TODO: remember to sign the build with the static method
        ArrayList<Build> buildsLoaded = new ArrayList<>();

        String stringValueBase64Encoded = rawPaste.trim();
        byte[] byteValueBase64Decoded = null;
        try {
            byteValueBase64Decoded = Base64.getDecoder().decode(stringValueBase64Encoded);
        } catch (java.lang.IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String stringValueBase64Decoded = new String(byteValueBase64Decoded);

        // JSONArray obj = new
        // JsonParser().parse(stringValueBase64Encoded).getAsJsonArray();
        try {

            JSONArray builds_array = new JSONArray(stringValueBase64Decoded);
            for (int i = 0; i < builds_array.length(); i++) {
                JSONObject bObj = builds_array.getJSONObject(i);
                Build build = new Build(bObj.getString("buildName"), bObj.getString("className"),
                        bObj.getString("ascendancyName"));
                // when importing character name will not be passed.
                build.characterName = "";
                build.level = 0;
                try {
                    // bObj.get("hasPob");
                    build.isValid = bObj.getBoolean("isValid");
                } catch (org.json.JSONException e) {
                    // if it doesnt exist prob its valid from earlier versions
                    build.isValid = true;
                }
                try {
                    // bObj.get("hasPob");
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
                // build.level
                for (int j = 0; j < socket_group_array.length(); j++) {
                    JSONObject sObj = socket_group_array.getJSONObject(j);
                    SocketGroup sg = new SocketGroup();
                    sg.id = sObj.getInt("id");
                    sg.setFromGroupLevel(sObj.getInt("fromGroupLevel"));
                    sg.setUntilGroupLevel(sObj.getInt("untilGroupLevel"));
                    sg.setReplaceGroup(sObj.getBoolean("replaceGroup"));
                    sg.setReplacesGroup(sObj.getBoolean("replacesGroup"));
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
                    try {
                        // bObj.get("hasPob");
                        sg.addNote(sObj.getString("note"));
                    } catch (org.json.JSONException e) {
                        sg.addNote("");
                    }
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
                        sg.getGems().add(gem);// ***check line 324 in GemsPanel_Controller;
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

        } catch (Exception e) {
            return false;
        }

        for (Build bl : buildsLoaded) {
            loadNewBuild(bl);
            POELevelFx.buildsLoaded.add(bl); // this line is extra and not included in the method above
        }
        return true;

    }

    public void addNewBuild(String buildName, String className, String ascendancyName) {
        BuildLinker bl = new BuildLinker();
        int linker_id = bl.hook(this);
        linker.put(linker_id, bl);

        removeBuild_button.setDisable(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
        try {
            // this will add the AnchorPane to the VBox
            buildsBox.getChildren().add(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        bl.pec = loader.<BuildEntry_Controller>getController(); // add controller to the linker class
        bl.pec.init(charToImage(className, ascendancyName), buildName, ascendancyName, bl);
        bl.build = new Build(buildName, className, ascendancyName);
        // every new build should be considered non-valid?
        bl.build.isValid = false;
        bl.build.hasPob = false;
        bl.build.pobLink = "";
        for (SocketGroup sg : bl.build.getSocketGroup()) {
            SocketGroupLinker sgl = sgc.new SocketGroupLinker(sg);
            // sgl.sg = sg;
            // sgl.generateLabel();
            bl.sgl_list.add(sgl);
        }
        POELevelFx.buildsLoaded.add(bl.build);
        root.toggleAllBuilds(true);
    }

    public void loadNewBuild(Build build) {
        BuildLinker bl = new BuildLinker();
        int linker_id = bl.hook(this);
        linker.put(linker_id, bl);

        removeBuild_button.setDisable(false);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
        try {
            // this will add the AnchorPane to the VBox
            buildsBox.getChildren().add(loader.load());
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }

        bl.pec = loader.<BuildEntry_Controller>getController(); // add controller to the linker class
        bl.pec.init(charToImage(build.getClassName(), build.getAsc()), build.getName(), build.getAsc(), bl);
        bl.build = build;
        // bl.build.isValid = true;
        for (SocketGroup sg : bl.build.getSocketGroup()) {
            SocketGroupLinker sgl = sgc.new SocketGroupLinker(sg);
            // sgl.sg = sg;
            // sgl.generateLabel();
            bl.sgl_list.add(sgl);
        }

        root.toggleAllBuilds(true);
    }

    @FXML
    private void deleteBuild() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Build delete");
        alert.setHeaderText("You are about to delete Build : " + linker.get(activeBuildID).build.getName());
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            BuildLinker bl = linker.get(activeBuildID);
            if (!bl.build.isValid) {
                root.toggleFooterVisibility(true);
            }
            POELevelFx.buildsLoaded.remove(bl.build);
            sgc.reset();
            buildsBox.getChildren().remove(bl.pec.getRoot()); // remove from the UI
            linker.remove(bl.id); // remove from data
            root.toggleActiveBuilds(false);
            if (POELevelFx.buildsLoaded.isEmpty()) {
                root.toggleAllBuilds(false);
            }
        }

        // and also remove from file system?

    }

    private void updateBuildValidationBanner(int id) {
        BuildLinker bl = linker.get(id);
        bl.pec.setValidBackgroundColor(bl.build.isValid, id == activeBuildID);
        root.toggleFooterVisibility(bl.build.isValid);
    }

    public void update(int id) {
        if (id != activeBuildID) {
            activeBuildID = id;
            root.toggleActiveBuilds(true);
            for (BuildLinker bl : linker.values()) {
                if (bl.id != id) {
                    bl.pec.reset(bl.build.isValid);
                } else {
                    // update the sgroup controller with new build info
                    GemHolder.getInstance().className = getCurrentClass(id);
                    sgc.hookBuild_Controller(this);
                    sgc.update(bl.sgl_list);
                    root.toggleFooterVisibility(bl.build.isValid);
                }
            }
            // root.buildChanged(id);

        }
    }

    public String getCurrentClass(int id) {
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
        // loadBuildsFromMemory();
    }

}
