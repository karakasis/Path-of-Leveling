/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import poe.level.data.Gem;
import poe.level.data.SocketGroup;
import poe.level.fx.GemsPanel_Controller.GemsPanelLinker;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class SocketGroupsPanel_Controller implements Initializable {

    public class SocketGroupLinker{
        Label sgLabel;
        SocketGroup sg;
        GemsPanelLinker gpl;
        
        public SocketGroupLinker(SocketGroup sgroup){
            sg = sgroup;
            generateLabel();
        }
        
        public SocketGroupLinker dupe(HashSet<Integer> s_id, HashSet<Integer> g_id){
            SocketGroupLinker sgl = new SocketGroupLinker(sg.dupe(s_id,g_id));
            return sgl;
        }
        
        public void generateLabel(){
            sgLabel = new Label();
            if(sg == null){
                sg = new SocketGroup(); //creating dummie
            }
            if(sg.getActiveGem()!=null){
              Image img = sg.getActiveGem().getSmallIcon(); 
              sgLabel.setGraphic(new ImageView(img));
              sgLabel.setText(sg.getActiveGem().getGemName());
            }else{
                sgLabel.setText("<empty group>");
            }      
        }
        
        public SocketGroup getSocketGroup(){
            return sg;
        }
        
        public Label getLabel(){
            Label label = new Label();
            if(sg.getActiveGem()!=null){
              Image img = sg.getActiveGem().getSmallIcon(); 
              label.setGraphic(new ImageView(img));
              label.setText(sg.getActiveGem().getGemName());
            }else{
                label.setText("<empty group>");
            }      
            return label;
        }

        public void changeLabel(){
            
            if(sg.getActiveGem()!=null){
              Image img = sg.getActiveGem().getSmallIcon(); 
              sgLabel.setGraphic(new ImageView(img));
              sgLabel.setText(sg.getActiveGem().getGemName());
            }else{
                sgLabel.setGraphic(null);
                sgLabel.setText("<empty group>");
            }
        }
    }
    
    @FXML
    private JFXButton addSocketGroup_button;
    @FXML
    private JFXButton removeSocketGroup_button;
    @FXML
    private JFXListView<Label> socketGroupView;
    
    @FXML
    private JFXButton duplicate_button;
    @FXML
    private JFXButton add_note_button;
    
    
    private MainApp_Controller root;
    private GemsPanel_Controller gpc;
    private SocketGroupsPanel_Controller self;
    private BuildsPanel_Controller bpc;
    private ArrayList<SocketGroupLinker> linker;
    private int activeSocketGroupID;
    
    public void hook(MainApp_Controller root){
        this.root = root;
    }
    
    public void hookGem_Controller(GemsPanel_Controller gpc){
        this.gpc = gpc;
    }
    
    public void hookBuild_Controller(BuildsPanel_Controller bpc){
        this.bpc = bpc;
    }
    
    public ArrayList<SocketGroupLinker> getLinker(){
        return linker;
    }
    
    public void requestNotePopup(){
        Socket_group_noteController notepop = root.notePopup();
        notepop.start(this,linker.get(activeSocketGroupID).sg.getNote());
    }
    
    public void noteChange(String newNote){
        linker.get(activeSocketGroupID).sg.addNote(newNote);
    }
    
    public void duplicate(){
        SocketGroupLinker sgl = linker.get(activeSocketGroupID);
        HashSet<Integer> sg_ids = new HashSet<>(); //receating all ids so far in this build...
        HashSet<Integer> gem_ids = new HashSet<>();
        for(SocketGroupLinker a : linker){
            if(!sg_ids.add(a.sg.id)){
                System.out.println("problems in duping potentially");
            }
            for(Gem g : a.sg.getGems()){
                if(!gem_ids.add(g.id)){
                    System.out.println("problems in duping potentially : gem section");
                }
            }
        }
        SocketGroupLinker duped_sgl = sgl.dupe(sg_ids,gem_ids);
        socketGroupView.getItems().add(duped_sgl.sgLabel); //add label to the socket groups on the left panel // this would be a new label cause we cant place 1 ref to 2 items
        linker.add(duped_sgl);
        bpc.addNewSocketGroup(duped_sgl.sg);
        socketGroupView.getSelectionModel().selectLast();
    }
    
    public void update(ArrayList<SocketGroupLinker> sgl_list){
        //leftGemBox.getChildren().clear();
        //rightGemBox.getChildren().clear();
        gpc.hidePanel();
        addSocketGroup_button.setDisable(false); //enable UI Button
        linker = sgl_list; // and this is by reference so that any change that will
        //happen within will actually apply to the build.
        ObservableList<Label> list = FXCollections.observableArrayList();
        for(SocketGroupLinker sgl : linker){
            list.add(sgl.sgLabel);
        }
        socketGroupView.setItems(list);
        /*
        if(list.isEmpty()){
            //removeSocketGroup_button.setDisable(true);
            socketGroupView.getItems().clear(); // weird call?
        }else{
            //removeSocketGroup_button.setDisable(false);
        }*/
        
    }
    
    @FXML
    private void addSocketGroup(){
        SocketGroupLinker sgl_dum = new SocketGroupLinker(null);
        linker.add(sgl_dum);
        socketGroupView.getItems().add(sgl_dum.sgLabel);
        bpc.addNewSocketGroup(sgl_dum.getSocketGroup());
    }
    
    @FXML
    private void removeSocketGroup(){
        int remove = socketGroupView.getSelectionModel().getSelectedIndex();
        for(SocketGroupLinker a : linker){
            if(a.sg.replaceGroup()){
                if(a.sg.getGroupReplaced().equals(linker.get(remove).sg))//if another group replaces with this one
                    a.sg.setReplaceGroup(false);
            }
            
        }
        if(remove != -1){
            bpc.removeSocketGroup(linker.get(remove).sg);
            socketGroupView.getItems().remove(remove);
            linker.remove(remove);
        }
        
        socketGroupView.getSelectionModel().clearSelection();
        removeSocketGroup_button.setDisable(true);
        gpc.hidePanel();
        //this is back-end though and hopefully it will pass the change to all data behind
        //it without needing to change each list.
        //an unorthodox way of doing it and totally retarded
    }
    
    public void reset(){
        socketGroupView.getItems().clear();
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        self = this;
        linker = new ArrayList<>();
        activeSocketGroupID = -1;
        socketGroupView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Label>() {
            @Override
            public void changed(ObservableValue<? extends Label> observable, Label oldValue, Label newValue) {
                // Your action here
                int id = socketGroupView.getSelectionModel().getSelectedIndex();
                if(activeSocketGroupID != id){
                    activeSocketGroupID = id;
                    if(activeSocketGroupID!= -1){
                        removeSocketGroup_button.setDisable(false); //active remove button
                        duplicate_button.setDisable(false);
                        add_note_button.setDisable(false);
                        gpc.hookSG_Controller(self);
                        //System.out.println("+_+++++++++++++++++++");
                        gpc.update(linker.get(activeSocketGroupID));
                        //gemVisiblePane.setVisible(true);
                    }else{
                        removeSocketGroup_button.setDisable(true); // disable remove button
                        duplicate_button.setDisable(true);
                        add_note_button.setDisable(true);
                        //gemVisiblePane.setVisible(false);
                    }
                }
            }
        });
    }    
}
