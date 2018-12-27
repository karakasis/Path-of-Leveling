/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXTabPane;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import poe.level.data.Gem;
import poe.level.data.GemHolder;
import poe.level.data.Zone;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class AddGem_Controller implements Initializable {

    
    class TabLinker{
        QuestSplitPanel_Controller controller;
        Zone zone;
        int act;
        ArrayList<Gem> gemsOther;
        
        public TabLinker(QuestSplitPanel_Controller c , Zone z, int act ){
            controller = c;
            zone = z;
            this.act = act;
        }
        
        public TabLinker(QuestSplitPanel_Controller c , int act , ArrayList<Gem> gems){
            controller = c;
            this.act = act;
            gemsOther = gems; // careful
        }
        
    }
    
    @FXML
    private VBox actIbox;
    @FXML
    private VBox actIIbox;
    @FXML
    private VBox actIIIbox;
    @FXML
    private VBox actIVbox;
    @FXML
    private VBox otherVBox;
    @FXML
    private JFXTabPane tabpane;
    @FXML
    private TextField searchArea;
    /**
     * Initializes the controller class.
     */
    private GemsPanel_Controller parent;
    private ArrayList<TabLinker> tablinkers;
    private ArrayList<TabLinker> tablinkersOther;
    private TabLinker last = null;
    
    @FXML
    public void onEnter(ActionEvent ae){
       System.out.println("test") ;
        String text = searchArea.getText();
        searchArea.clear();
        
        ArrayList<HashMap<Zone, ArrayList<Gem>>> all = GemHolder.getInstance().getAll();
        int actFound = 0;
        Zone zoneFound = null;
        
        outerloop:
        for(int act = 0; act < all.size(); act++){
            HashMap<Zone, ArrayList<Gem>> actMap  = all.get(act);
            //loop in acts
            //loop in zones
            for(Zone zone : actMap.keySet()){
                ArrayList<Gem> gems = actMap.get(zone);
                for(Gem gem : gems){
                    if(gem.getGemName().toLowerCase().contains(text.toLowerCase())
                            || text.toLowerCase().contains(gem.getGemName().toLowerCase())){
                        actFound = act;
                        zoneFound = zone;
                        break outerloop;
                    }
                }
            }
            
        }
        
        if(zoneFound == null){
            for(TabLinker tl : tablinkersOther){
                for(Gem g: tl.gemsOther){
                    if(g.getGemName().toLowerCase().contains(text.toLowerCase())
                            || text.toLowerCase().contains(g.getGemName().toLowerCase())){
                        actFound = -1;
                        zoneFound = null;
                        tabpane.getSelectionModel().select(4); // selectes last tab
                        tl.controller.requestBorder();
                        if(last != null){
                            last.controller.resetBorder();  
                        }
                        last = tl;
                    }
                }
            }
        }
        
        if(zoneFound!=null){ //so if gem is found
            tabpane.getSelectionModel().select(actFound);
           for(TabLinker tl  : tablinkers){
               if(tl.act == actFound && tl.zone.equals(zoneFound)){
                   tl.controller.requestBorder();
                   if(last != null){
                        last.controller.resetBorder();
                    }
                    last = tl;
               }
           }
        }
    }
    
    public void start(GemsPanel_Controller parent){
        this.parent = parent;
    }
    
    public void callback(Gem g){
        parent.closePopup(g);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ArrayList<HashMap<Zone, ArrayList<Gem>>> all = GemHolder.getInstance().getAll();
        ArrayList<Zone> reorder = new ArrayList<>();
        
        ArrayList<Zone> reorder2 = new ArrayList<>();
        
        ArrayList<Zone> reorder3 = new ArrayList<>();
        
        ArrayList<Zone> reorder4 = new ArrayList<>();
        
        ArrayList<Gem> reorderOther = new ArrayList<>();
        HashSet<Integer> distinctLevels = new HashSet<>();
        
        for(Zone z : all.get(0).keySet()){
            reorder.add(z);
        }
        for(Zone z : all.get(1).keySet()){
            reorder2.add(z);
        }
        for(Zone z : all.get(2).keySet()){
            reorder3.add(z);
        }
        for(Zone z : all.get(3).keySet()){
            reorder4.add(z);
        }
        
        
        HashMap<Integer,ArrayList<Gem>> map = new HashMap<>();
        ArrayList<Gem> gemOther = GemHolder.getInstance().getGemOther();
        
        for(Gem g : gemOther){
            reorderOther.add(g);
            if(map.containsKey(g.getLevelAdded())){
                map.get(g.getLevelAdded()).add(g);
            }else{
                ArrayList<Gem> dd = new ArrayList<>();
                dd.add(g);
                map.put(g.getLevelAdded(), dd);
            }
            
            distinctLevels.add(g.getLevelAdded());
            
        }
        
        ArrayList<Integer> distinctLevels_order = new ArrayList<>();
        
        distinctLevels_order.addAll(distinctLevels);
        Collections.sort(distinctLevels_order);
        
        reorder.sort(Comparator.comparing(Zone::getZoneLevel));
        reorder2.sort(Comparator.comparing(Zone::getZoneLevel));
        reorder3.sort(Comparator.comparing(Zone::getZoneLevel));
        reorder4.sort(Comparator.comparing(Zone::getZoneLevel));
        reorderOther.sort(Comparator.comparing(Gem::getLevelAdded));
        
        tablinkers = new ArrayList<>();

        for(Zone z : reorder){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("questSplitPanel.fxml"));
            SplitPane con = null;
            try {
                con = (SplitPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            QuestSplitPanel_Controller controller = loader.<QuestSplitPanel_Controller>getController();
            actIbox.getChildren().add(con);
            controller.load(z,all.get(0).get(z), this);
            tablinkers.add(new TabLinker(controller,z,0));

        }
        for(Zone z : reorder2){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("questSplitPanel.fxml"));
            SplitPane con = null;
            try {
                con = (SplitPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            QuestSplitPanel_Controller controller = loader.<QuestSplitPanel_Controller>getController();
            actIIbox.getChildren().add(con);
            controller.load(z,all.get(1).get(z), this);
            tablinkers.add(new TabLinker(controller,z,1));
        }
        for(Zone z : reorder3){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("questSplitPanel.fxml"));
            SplitPane con = null;
            try {
                con = (SplitPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            QuestSplitPanel_Controller controller = loader.<QuestSplitPanel_Controller>getController();
            actIIIbox.getChildren().add(con);
            controller.load(z,all.get(2).get(z), this);
            tablinkers.add(new TabLinker(controller,z,2));
        }
        for(Zone z : reorder4){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("questSplitPanel.fxml"));
            SplitPane con = null;
            try {
                con = (SplitPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            QuestSplitPanel_Controller controller = loader.<QuestSplitPanel_Controller>getController();
            actIVbox.getChildren().add(con);
            controller.load(z,all.get(3).get(z), this);
            tablinkers.add(new TabLinker(controller,z,3));
        }
        tablinkersOther = new ArrayList<>();
        for(Integer a : distinctLevels_order){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("questSplitPanel.fxml"));
            SplitPane con = null;
            try {
                con = (SplitPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            QuestSplitPanel_Controller controller = loader.<QuestSplitPanel_Controller>getController();
            otherVBox.getChildren().add(con);
            controller.loadOther(map.get(a), a , this);
            tablinkersOther.add(new TabLinker(controller,4, map.get(a)));
        }
        
        
        // TODO
    }    
    
}
