/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXToggleButton;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.util.StringConverter;
import poe.level.data.*;
import poe.level.fx.SocketGroupsPanel_Controller.SocketGroupLinker;
import sun.plugin.javascript.navig.Anchor;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class GemsPanel_Controller implements Initializable {

    public class GemsPanelLinker{
        HashMap<Integer,GemLinker> gl_map;
        GemsPanel_Controller root;
        int id;
        ArrayList<Integer> ids;
        int active_gemLinker;
        int popup_id;
        SocketGroup sg;
        GemLinker currentMain;
        public int panel_mapper;

        public int hook(GemsPanel_Controller root, SocketGroup sg){
            this.root = root;
            this.sg = sg;
            id = GemsPanel_Controller.sign();
            //acts as constructor
            ids = new ArrayList<>();
            active_gemLinker = -1;
            popup_id = -1;
            currentMain = null;
            return id;
        }

        public void loadGemPanel(int gemId){
            ids.add(gemId);
        }

        public void removeGemPanel(int gemId){
            for(int i = 0; i< ids.size() ; i++) {
                if(ids.get(i) == gemId){
                    ids.remove(i);
                    break;
                }
            }
        }

        public void setActiveLinker(GemLinker gl){
            currentMain = gl;
        }

        public void setActiveGemLinker(int gemId){

            if(active_gemLinker!=gemId){
                active_gemLinker = gemId;
                for (GemLinker gl : gl_map.values()) {
                    //if(!gl.gem.getGemName().equals("<empty group>")){
                        if(gl.id!=active_gemLinker){
                            gl.resetUI();
                        }
                    //}
                }
            }
            root.removeGemPanel_button.setDisable(false);
        }

        public void autoSelectActiveGem(Gem g){
            root.activeSkillGroup.setValue(g);
        }

        public void updateGemData(Gem g, int id){
            root.gemUpdate(g, id);
        }

        public void requestPopup(int id){
            popup_id = id;
            root.requestPopup();
        }
    }

    public class GemLinker{
        Gem gem;
        int id;
        GemsPanelLinker root;
        GemEntry_Controller controller;

        public int hook(GemsPanelLinker root){
            this.root = root;
            id = GemsPanel_Controller.sign();
            return id;
        }

        public void hookController(GemEntry_Controller controller){
            this.controller = controller;
        }

        public void start(){
            controller.input(this, id);
        }

        public void load(){
            controller.load(this,id,gem);
        }

        public void updateGemData(Gem gem){
            for(Gem g : root.sg.getGems()){
                if(g.replaced){
                    System.out.println(g.getGemName()+" has replacement.");
                    //if any gem was replaced with the old one
                    if(g.replacedWith.equals(this.gem)){

                        System.out.println(g.getGemName()+" replaces with old gem :" + this.gem.getGemName());
                        //it will now replace the new one
                        g.replacedWith = gem;
                        System.out.println(g.getGemName()+" replaces with new gem :" + gem.getGemName());

                    }else{
                       System.out.println(g.getGemName()+" has replacement, but not "+this.gem.getGemName());
                    }
                }
            }
            this.gem = gem;
            root.updateGemData(gem, id);
            //here we have access to the socket group so we can do this from within
            if(root.sg.getActiveGem() == null && gem.isActive){
               root.autoSelectActiveGem(this.gem);
            }
            root.root.updateAllComboBox();
        }

        public void levelChanged(){
            if(root.sg.getActiveGem()!=null){
                if(root.sg.getActiveGem().equals(gem)){
                    root.root.levelChanged(root.sg.getActiveGem().getLevelAdded());
                }
            }
        }

        public void clicked(){
            root.setActiveGemLinker(id);
        }

        public void resetUI(){
            controller.reset();
        }

        public void requestPopup(){
            root.requestPopup(id);
        }

        public SocketGroup getSiblings(){
            return root.sg;
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

    public static ObservableList<Label> getMainGems(SocketGroup selected){
        ObservableList<Label> listG = FXCollections.observableArrayList();
        for(Gem g : selected.getGems()){
            if(!g.getGemName().equals("<empty group>")){
                Label l = new Label();
                Image img = g.getIcon();
                if(img!=null){
                    l.setGraphic(new ImageView(img));
                }
                l.setText(g.getGemName().toString());
                listG.add(l);
            }
        }
        return listG;
    }

    public static ArrayList<SocketGroup> getReplaceableGroups(
            ArrayList<SocketGroupLinker> sgl_list){
        ArrayList<SocketGroup> sg_list= new ArrayList<>();
        for(SocketGroupLinker a : sgl_list){
           sg_list.add(a.sg);
        }
        return sg_list;
    }
    @FXML
    private TilePane gemContainer;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private JFXToggleButton replaceGroup;
    @FXML
    private JFXComboBox<SocketGroup> replaceGroupBox;
    @FXML
    private Label untilLevelLabel;
    @FXML
    private JFXComboBox<Gem> activeSkillGroup;
    @FXML
    private JFXButton addGemPanel_button;
    @FXML
    private JFXButton removeGemPanel_button;
    @FXML
    private Spinner<Integer> fromLevel;
    @FXML
    private Spinner<Integer> untilLevel;


    private MainApp_Controller root;
    private SocketGroupsPanel_Controller sgc;
    private ArrayList<SocketGroupLinker> linker;
    private SocketGroupLinker current_sgl;

    public void hook(MainApp_Controller root){
        this.root = root;
    }

    public void hookSG_Controller(SocketGroupsPanel_Controller sgc){
        this.sgc = sgc;
    }

    public void requestPopup(){
        AddGem_Controller gemPopup = root.gemPopup();
        gemPopup.start(this);
    }


    public void closePopup(Gem g){
        root.gemClosePopup();
        current_sgl.gpl.gl_map.get(current_sgl.gpl.popup_id).controller.callback(g);
    }

    public void requestNotePopup(){
        Socket_group_noteController notepop = root.notePopup();
        //notepop.start(this,current_sgl.sg.getNote());
    }

    public void noteChange(String newNote){
        current_sgl.sg.addNote(newNote);
    }

    /*
    public void closeNotePopup(String note){
        root.noteClosePopup();
        current_sgl.sg.addNote(note);
    }*/

    private boolean lockClear = false;

    //this will get called from within the listener of the listview.
    //and is responsible for loading previously created gem panels.
    public void update(SocketGroupLinker sgl){
        current_sgl = sgl;

        //leftGemBox.getChildren().clear();
        //rightGemBox.getChildren().clear();
        gemContainer.getChildren().clear();
        removeGemPanel_button.setDisable(true);

        rootPane.setVisible(true);
        linker = sgc.getLinker(); //<<< fix

        //System.out.println("Clearing... start");
        lockClear = true;
        activeSkillGroup.getItems().clear();
        replaceGroupBox.getItems().clear();
        //lockClear = false;
        //System.out.println("Clearing... end");

        ArrayList<Gem> gems = current_sgl.sg.getGems();
        activeSkillGroup.getItems().addAll(gems);
        activeSkillGroup.setCellFactory(lv -> new GemListCell());
        activeSkillGroup.setButtonCell(new GemListCell());
        activeSkillGroup.setConverter(new GemToString());

        ArrayList<SocketGroup> sg_list= new ArrayList<>();
        for(SocketGroupLinker a : linker){
            if(!a.equals(sgl))
                sg_list.add(a.sg);
        }

        replaceGroupBox.getItems().addAll(sg_list);
        replaceGroupBox.setCellFactory(lv -> new SocketGroupListCell());
        replaceGroupBox.setButtonCell(new SocketGroupListCell());
        replaceGroupBox.setConverter(new SocketGroupToString());



        if(sgl.gpl==null){
            sgl.gpl = new GemsPanelLinker();
            sgl.gpl.hook(this,sgl.sg);
            sgl.gpl.gl_map = new HashMap<>();

            for(Gem g : sgl.sg.getGems()){
                GemLinker gl = new GemLinker();
                sgl.gpl.gl_map.put(gl.hook(sgl.gpl), gl);
                gl.gem = g;

                //if(!g.getGemName().equals("<empty group>")){

                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("gemEntry.fxml"));
                        AnchorPane gemEntry = loader.load();
                        gemContainer.getChildren().add(gemEntry);
                        sgl.gpl.loadGemPanel(gl.id);

                        gl.hookController(loader.<GemEntry_Controller>getController());
                        if(gl.gem.getGemName().equals("<empty group>")){
                            gl.start();
                        }else{
                            sgl.gpl.sg.linkGem(g,gl.id);
                            gl.load();
                        }

                    } catch (IOException ex) {
                        Logger.getLogger(GemsPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
                    }

               // }
            }
        }else{


            for(int gem_id : sgl.gpl.ids){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("gemEntry.fxml"));
                try {
                    AnchorPane gemEntry = loader.load();
                    gemContainer.getChildren().add(gemEntry);
                } catch (IOException ex) {
                    Logger.getLogger(GemsPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }
                sgl.gpl.gl_map.get(gem_id).hookController(loader.<GemEntry_Controller>getController());
                //loader.setController(sgl.gpl.gl_map.get(gem_id).controller);
                sgl.gpl.gl_map.get(gem_id).load();
            }

        }



        SocketGroup sg = sgl.sg;

        if(sg.replaceGroup()){

            untilLevelLabel.setVisible(true);
            untilLevel.setVisible(true);
            replaceGroup.setSelected(true);
            replaceGroupBox.setVisible(true);
            //replaceGroupBox.getItems().addAll(GemsPanel_Controller.getReplaceableGroups(linker));

            if(sg.getGroupReplaced()!=null){
                replaceGroupBox.setValue(sg.getGroupReplaced());
                untilLevel.getValueFactory().setValue(sg.getGroupReplaced().getFromGroupLevel());
                //replaceGroupBox.getEditor().setText(sg.getGroupReplaced().getActiveGem().getGemName());
            }
        }else{
                replaceGroup.setSelected(false);
                replaceGroupBox.setVisible(false);
                untilLevelLabel.setVisible(false);
                untilLevel.setVisible(false);
        }
        //activeSkillGroup.getItems().addAll(current_sgl.sg.getGems());

        //System.out.println("Im out");
        if(sg.getActiveGem()!=null){
        //System.out.println("Now im in..");
            activeSkillGroup.setValue(sg.getActiveGem());

            current_sgl.changeLabel();
            for(GemLinker gl : current_sgl.gpl.gl_map.values()){
                if(gl.gem.equals(sg.getActiveGem())){
                    current_sgl.gpl.setActiveLinker(gl);
                    break;
                }
            }
            //activeSkillGroup.getEditor().setText(sg.getActiveGem().getGemName());
        }
        fromLevel.getValueFactory().setValue( sg.getFromGroupLevel());
        //lockClear = true;
        untilLevel.getValueFactory().setValue( sg.getUntilGroupLevel());
        lockClear = false;

    }

    @FXML
    private void addNote(){
        requestNotePopup();
    }

    @FXML
    private void duplicateGroup(){
        //current_sgl.sg;
        //sgc.duplicate(current_sgl);
    }

    @FXML
    private void addGemPanel(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("gemEntry.fxml"));
        GemLinker gl = new GemLinker();
        current_sgl.gpl.gl_map.put(gl.hook(current_sgl.gpl), gl);
        gl.gem = GemHolder.getInstance().tossDummie();
        try {
            AnchorPane gemEntry = loader.load();
            gemContainer.getChildren().add(gemEntry);
            current_sgl.gpl.loadGemPanel(gl.id);
        } catch (IOException ex) {
            Logger.getLogger(GemsPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        gl.hookController(loader.<GemEntry_Controller>getController());
        gl.start();
    }

    @FXML
    private void removeGemPanel(){
        GemLinker get = current_sgl.gpl.gl_map.get(current_sgl.gpl.active_gemLinker);
        current_sgl.gpl.removeGemPanel(current_sgl.gpl.active_gemLinker);
        /*
        int active_gemLinker = current_sgl.gpl.active_gemLinker;
        GemLinker gemLinker = current_sgl.gpl.gl_map.get(active_gemLinker);
        Gem gem = gemLinker.gem;*/

        GemEntry_Controller thisiswrong = get.controller;


        removeGem(get.gem); //remove gem from socketGroup class
        current_sgl.gpl.gl_map.remove(current_sgl.gpl.active_gemLinker);//let go of the GemLinker reference

        //current.sgl//remove from the vbox lists
        if(thisiswrong!=null){
            gemContainer.getChildren().remove(thisiswrong.getRoot());
        }
        if(gemContainer.getChildren().isEmpty()){
            current_sgl.triggerDelete();
        }
        removeGemPanel_button.setDisable(true);
    }

    @FXML
    private void toggleReplaceGroup(){
        if(replaceGroup.isSelected()){
            replaceGroupBox.setVisible(true);
            untilLevelLabel.setVisible(true);
            untilLevel.setVisible(true);
        }else{
            replaceGroupBox.setVisible(false);
            untilLevelLabel.setVisible(false);
            untilLevel.setVisible(false);
            current_sgl.sg.setReplaceGroup(false);
        }
    }

    @FXML
    private void groupReplaceValueChanged(){
        //this will prob change cuase of new combo box ><<<<<><><>
        //Label selection = (Label)replaceGroupBox.getSelectionModel().getSelectedItem();
        //String text = selection.getText();
        if(!lockClear){
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            current_sgl.sg.setGroupReplaced(replaceGroupBox.getValue());
            current_sgl.sg.setReplaceGroup(true);
            /*for(SocketGroupLinker sgl: linker){
                if(sgl.sg.equals(replaceGroupBox.getValue())){
                    sgl.sg.setReplacesGroup(true);
                    sgl.sg.setGroupThatReplaces(current_sgl.sg);
                }
            }*/
            groupSliderChange(current_sgl.sg.getGroupReplaced().getFromGroupLevel(),1);
            untilLevel.getValueFactory().setValue(current_sgl.sg.getGroupReplaced().getFromGroupLevel());

        }

        /*
        for(SocketGroupLinker sgl : linker){
            if(sgl.sg.getActiveGem().getGemName().equals(text)){
                current_sgl.sg.setActiveGem(sgl.sg.getActiveGem());
                current_sgl.sg.setReplaceGroup(true);
                current_sgl.sg.setGroupReplaced(sgl.getSocketGroup());
                break;
            }
        }*/
    }

    @FXML
    private void mainGemValueChanged(){
            //System.out.println("Triggered ! ");
        //this will prob change cuase of new combo box ><<<<<><><>
        //Label selection = (Label)activeSkillGroup.getSelectionModel().getSelectedItem();
        //String text = selection.getText();
        if(current_sgl.sg.getActiveGem() == null){
            //System.out.println("Active1: null");
        }else{
            //System.out.println("Active1: "+current_sgl.sg.getActiveGem().toString());
        }
        if(!lockClear)
            current_sgl.sg.setActiveGem(activeSkillGroup.getValue());
        if(current_sgl.sg.getActiveGem() == null){
            //System.out.println("Active2: null");
        }else{
            //System.out.println("Active2: "+current_sgl.sg.getActiveGem().toString());
        }
        //activeSkillGroup.
        if(!lockClear){
            for(GemLinker gl : current_sgl.gpl.gl_map.values()){
                if(gl.gem.equals(current_sgl.sg.getActiveGem())){
                    current_sgl.gpl.setActiveLinker(gl);
                    break;
                }
            }
            current_sgl.changeLabel();
            if(current_sgl.sg.getActiveGem()!=null){
                groupSliderChange(current_sgl.sg.getActiveGem().getLevelAdded(),0);
                fromLevel.getValueFactory().setValue(current_sgl.sg.getActiveGem().getLevelAdded());
            }
            if(current_sgl.sg.replaceGroup()){
                groupSliderChange(current_sgl.sg.getGroupReplaced().getFromGroupLevel(),1);
                untilLevel.getValueFactory().setValue(current_sgl.sg.getGroupReplaced().getFromGroupLevel());
            }
        }
        /*
        for(Gem g : current_sgl.sg.getGems()){
            if(g.getGemName().equals(text)){
                current_sgl.sg.setActiveGem(g);
                activeSkillGroup.getEditor().setText(text);
                current_sgl.generateLabel();
                break;
            }
        }*/
    }

    private void groupSliderChange(int newValue, int code){
        if(code == 0){
            current_sgl.sg.setFromGroupLevel(newValue);
           // fromLevel.setText(current_sgl.sg.getFromGroupLevel()+"");
            if(current_sgl.sg.getActiveGem()!=null){
                current_sgl.sg.getActiveGem().level_added = newValue;
                //System.out.println("current Main testing :");
                if(current_sgl.gpl.currentMain!=null){
                    //System.out.println("entered this time :");
                    current_sgl.gpl.currentMain.controller.groupLevelChanged(newValue);
                }
            }
            //untilLevel.getValueFactory().setMin(newValue);
            if(untilLevel.getValueFactory().getValue() <=newValue){
                untilLevel.getValueFactory().setValue(newValue+1);
            }
        }else if(code == 1){
            current_sgl.sg.setUntilGroupLevel(newValue);
        }
    }

    //this is probably when u select  a new gem from the list
    public void gemUpdate(Gem gem, int id){

        Gem old = current_sgl.sg.putGem(gem,id);
        //if it was an active gem.
        if(old!=null){
            lockClear = true;
            activeSkillGroup.getItems().clear();
            lockClear = false;
            activeSkillGroup.getItems().addAll(current_sgl.sg.getGems());
            current_sgl.sg.setActiveGem(gem);
            activeSkillGroup.setValue(gem);
            current_sgl.changeLabel();
            /*
            for(SocketGroupLinker a : linker){
                if(a.sg.replaceGroup()){
                    if(a.sg.getGroupReplaced().getActiveGem().equals(old))//if another group replaces with this one
                        a.sg.setActiveGem(gem);
                }
            }
            current_sgl.sg.setActiveGem(gem);
            activeSkillGroup.setValue(gem);
            current_sgl.changeLabel();*/
        }else{
            int change = current_sgl.sg.doubleCheck();
            if(change!=-1)
                activeSkillGroup.getItems().set(change, gem);
            else
                activeSkillGroup.getItems().add(gem);
        }
        /*
        if(current_sgl.sg.getActiveGem() == null){
            System.out.println("Active: null");
        }else{

        System.out.println("Active: "+current_sgl.sg.getActiveGem().toString());
        }*/
        //activeSkillGroup.setItems(GemsPanel_Controller.getMainGems(current_sgl.sg));
    }

    //this is when you actually delete the gem panel?
    public void removeGem(Gem gem){


        Gem replacement = null;
        if(current_sgl.sg.getActiveGem()!=null){
            if(current_sgl.sg.getActiveGem().equals(gem)){

                for(SocketGroupLinker a : linker){
                    if(a.sg.replaceGroup()){
                        if(a.sg.getGroupReplaced().equals(current_sgl.sg))//if another group replaces with this one
                            a.sg.setReplaceGroup(false);
                    }
                }
                for(Gem g : current_sgl.sg.getGems()){
                    if(g.isActive && g != gem) {
                        replacement = g;
                        break;
                    }
                }
                if(replacement== null){
                    for(Gem g : current_sgl.sg.getGems()){
                        if(g != gem) {
                            replacement = g;
                            break;
                        }
                    }
                }
            }
        }



        current_sgl.sg.getGems().remove(gem);
        activeSkillGroup.getItems().remove(gem);

        //activeSkillGroup.setItems(GemsPanel_Controller.getMainGems(current_sgl.sg));
        /*
        if(current_sgl.sg.getActiveGem().getGemName()
                .equals(gem.getGemName())){
            activeSkillGroup.getSelectionModel().clearSelection();
            current_sgl.sg.setActiveGem(null);
            activeSkillGroup.getEditor().setText("");
            current_sgl.generateLabel();
        }*/


        if(replacement!=null){
            System.out.println("Replacement" + replacement.getGemName());
            activeSkillGroup.setValue(replacement);
        }

        for(Gem g : current_sgl.sg.getGems()){
            if(g.replaced){
                if(g.replacedWith.equals(gem)){
                    g.replaced = false;
                    g.replacedWith = null;
                    updateAllComboBox();
                    break;
                }
            }

        }
    }

    public void updateAllComboBox(){
        for(GemLinker  gl : current_sgl.gpl.gl_map.values()){
            gl.controller.updateComboBox();
        }
    }

    public void hidePanel(){
        rootPane.setVisible(false);
    }

    public void levelChanged(int level){
        groupSliderChange(level,0);
        fromLevel.getValueFactory().setValue(level);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        rootPane.setVisible(false);
        /*
      Initializes the controller class.
     */ /**
         * Initializes the controller class.
         */
        SpinnerValueFactory<Integer> valueFactoryFrom = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        SpinnerValueFactory<Integer> valueFactoryUntil = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1);
        fromLevel.setValueFactory(valueFactoryFrom);
        fromLevel.setStyle(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        untilLevel.setValueFactory(valueFactoryUntil);
        untilLevel.setStyle(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        fromLevel.setEditable(true);
        untilLevel.setEditable(true);

        Util.addIntegerLimiterToIntegerSpinner(fromLevel, valueFactoryFrom);
        Util.addIntegerLimiterToIntegerSpinner(untilLevel, valueFactoryUntil);


        valueFactoryFrom.valueProperty().addListener((arg0, arg1, arg2) -> groupSliderChange(fromLevel.getValue(),0));

        valueFactoryUntil.valueProperty().addListener((arg0, arg1, arg2) -> groupSliderChange(untilLevel.getValue(),1));
    }

}
