/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXTabPane;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import poe.level.data.Gem;
import poe.level.data.GemHolder;
import poe.level.data.Zone;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        TabLinker(QuestSplitPanel_Controller c, Zone z, int act){
            controller = c;
            zone = z;
            this.act = act;
        }

        TabLinker(QuestSplitPanel_Controller c, int act, ArrayList<Gem> gems){
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
    private VBox actVIbox;
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
    private ArrayList<String> m_gemNames = new ArrayList<>();

    public void onEnter(){
        String searchText = searchArea.getText().trim().toLowerCase();

        if (searchText.isEmpty()) {
          return;
        }

        ArrayList<HashMap<Zone, ArrayList<Gem>>> all = GemHolder.getInstance().getAll();
        int actFound = 0;
        Zone zoneFound = null;

        boolean exactMatch = false;
        for (String str : m_gemNames) {
          if (str.equalsIgnoreCase(searchText)) {
            exactMatch = true;
            break;
          }
        }
        for(int act = 0; act < all.size() && zoneFound == null; act++){
            HashMap<Zone, ArrayList<Gem>> actMap  = all.get(act);
            //loop in acts
            //loop in zones
            for(Zone zone : actMap.keySet()){
                ArrayList<Gem> gems = actMap.get(zone);
                for(Gem gem : gems){
                    if(!exactMatch && gem.getGemName().toLowerCase().contains(searchText)) {
                        actFound = act;
                        zoneFound = zone;
                        break;
                    } else if (exactMatch && gem.getGemName().equalsIgnoreCase(searchText)) {
                        actFound = act;
                        zoneFound = zone;
                        break;
                    }
                }
                if (zoneFound != null) {
                    break;
                }
            }

        }

        if(zoneFound == null) {
            for(TabLinker tl : tablinkersOther){
                for(Gem g: tl.gemsOther){
                    if(g.getGemName().toLowerCase().contains(searchText)) {
                        actFound = -1;
                        zoneFound = null;
                        tabpane.getSelectionModel().selectLast();
                        tl.controller.requestBorder();
                        if(last != null && !last.equals(tl)){
                            last.controller.resetBorder();
                        }
                        last = tl;
                        break;
                    }
                }
            }
        }

        if(zoneFound != null) { //so if gem is found
            tabpane.getSelectionModel().select(actFound);
            for(TabLinker tl : tablinkers){
                if(tl.act == actFound && tl.zone.equals(zoneFound)){
                    tl.controller.requestBorder();
                    if(last != null && !last.equals(tl)){
                        last.controller.resetBorder();
                    }
                    last = tl;
                    break;
                }
            }
        }
    }

    public void start(GemsPanel_Controller parent){
        this.parent = parent;
        reset();
    }

    /**
     * Reset the UI if it's closed then re-opened.
     */
    private void reset() {
      tabpane.getSelectionModel().selectFirst();
      searchArea.setText("");
      if(last != null) {
          last.controller.resetBorder(true);
          last = null;
      }
    }

    void callback(Gem g){
        parent.closePopup(g);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ArrayList<HashMap<Zone, ArrayList<Gem>>> all = GemHolder.getInstance().getAll();


        ArrayList<Gem> reorderOther = new ArrayList<>();
        HashSet<Integer> distinctLevels = new HashSet<>();

        ArrayList<Zone> reorder = new ArrayList<>(all.get(0).keySet());
        ArrayList<Zone> reorder2 = new ArrayList<>(all.get(1).keySet());
        ArrayList<Zone> reorder3 = new ArrayList<>(all.get(2).keySet());
        ArrayList<Zone> reorder4 = new ArrayList<>(all.get(3).keySet());
        ArrayList<Zone> reorder6 = new ArrayList<>(all.get(4).keySet());


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

        ArrayList<Integer> distinctLevels_order = new ArrayList<>(distinctLevels);
        Collections.sort(distinctLevels_order);

        reorder.sort(Comparator.comparing(Zone::getZoneLevel));
        reorder2.sort(Comparator.comparing(Zone::getZoneLevel));
        reorder3.sort(Comparator.comparing(Zone::getZoneLevel));
        reorder4.sort(Comparator.comparing(Zone::getZoneLevel));
        reorder6.sort(Comparator.comparing(Zone::getZoneLevel));
        reorderOther.sort(Comparator.comparing(Gem::getLevelAdded));

        tablinkers = new ArrayList<>();

        fillBox(1, actIbox, all.get(0), reorder);
        fillBox(2, actIIbox, all.get(1), reorder2);
        fillBox(3, actIIIbox, all.get(2), reorder3);
        fillBox(4, actIVbox, all.get(3), reorder4);
        fillBox(5, actVIbox, all.get(4), reorder6);
        tablinkersOther = new ArrayList<>();
        for(Integer a : distinctLevels_order){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("questSplitPanel.fxml"));
                SplitPane con = fxmlLoader.load();
                QuestSplitPanel_Controller controller = fxmlLoader.getController();
                otherVBox.getChildren().add(con);
                controller.loadOther(map.get(a), a , this);
                tablinkersOther.add(new TabLinker(controller,5, map.get(a)));
                map.get(a).forEach(gem -> m_gemNames.add(gem.name));
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        m_gemNames.sort(Comparator.naturalOrder());
        JFXAutoCompletePopup<String> autoCompletePopup = new JFXAutoCompletePopup<>();
        autoCompletePopup.getSuggestions().addAll(m_gemNames);
        //SelectionHandler sets the value of the comboBox
        autoCompletePopup.setSelectionHandler(event -> searchArea.setText(event.getObject()));
        searchArea.textProperty().addListener(observable -> {
            String searchVal = searchArea.getText();
            if (searchVal == null) {
                return;
            }
            searchVal = searchVal.trim().toLowerCase();
            String finalSearchVal = searchVal;
            autoCompletePopup.filter(item -> item.toLowerCase().contains(finalSearchVal));
            //Hide the autocomplete popup if the filtered suggestions is empty or when the box's original popup is open
            if (searchVal.isEmpty() || autoCompletePopup.getFilteredSuggestions().isEmpty()) {
                autoCompletePopup.hide();
            } else {
                autoCompletePopup.show(searchArea);
            }
        });

        // TODO
    }

    private void fillBox(int whichAct, VBox whichActBox, HashMap<Zone, ArrayList<Gem>> gemHash, ArrayList<Zone> zoneList) {

        for(Zone z : zoneList){
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("questSplitPanel.fxml"));
                SplitPane con = fxmlLoader.load();

                QuestSplitPanel_Controller controller = fxmlLoader.getController();
                whichActBox.getChildren().add(con);
                controller.load(z, gemHash.get(z), this);
                tablinkers.add(new TabLinker(controller,z, whichAct - 1));
                gemHash.get(z).forEach(gem -> m_gemNames.add(gem.name));
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }

        }
    }

}
