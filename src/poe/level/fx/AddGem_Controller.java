/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXTabPane;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
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
    @FXML
    private ScrollPane scroll1;
    @FXML
    private ScrollPane scroll2;
    @FXML
    private ScrollPane scroll3;
    @FXML
    private ScrollPane scroll4;
    @FXML
    private ScrollPane scroll5;
    @FXML
    private ScrollPane scroll6;
    @FXML
    private AnchorPane filtersPaneBig;
    @FXML
    private AnchorPane filtersPaneSmall;
    @FXML
    private AnchorPane contentPanel;
    @FXML
    private FlowPane filtersLayoutSmall;
    @FXML
    private FlowPane filtersLayoutBig;
    @FXML
    private TilePane filteredGemsPane;
    @FXML
    private ScrollPane filteredPane;
    @FXML
    private JFXCheckBox activeCbox;
    @FXML
    private JFXCheckBox supportCbox;


    /**
     * Initializes the controller class.
     */
    private GemsPanel_Controller parent;
    private ArrayList<TabLinker> tablinkers;
    private ArrayList<TabLinker> tablinkersOther;
    private TabLinker last = null;
    private ArrayList<String> m_gemNames = new ArrayList<>();

    private void loadFilteredGems(ArrayList<Gem> a){

    }

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

    private void superScroll(int act_code, double h){
        if(act_code!=-1)
        for(TabLinker tl : tablinkers){
            if(tl.act == act_code){
                tl.controller.scrollListener(h);
            }
        }
        //assume drop only tab for -1
        else
            for(TabLinker tl : tablinkersOther){
                tl.controller.scrollListener(h);
            }
    }

    void callback(Gem g){
        parent.closePopup(g);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        scroll1.hvalueProperty().addListener(
        (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

            System.out.println("Scroll 1 horizontal value:" + scroll1.getHvalue());
            //levelScroll.setHvalue(outerScroll.getHvalue());
            superScroll(1, scroll1.getHvalue());

        });
        scroll2.hvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

                    //System.out.println("Scroll 1 horizontal value:" + scroll1.getHvalue());
                    //levelScroll.setHvalue(outerScroll.getHvalue());
                    superScroll(2, scroll2.getHvalue());

                });
        scroll3.hvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

                    //System.out.println("Scroll 1 horizontal value:" + scroll1.getHvalue());
                    //levelScroll.setHvalue(outerScroll.getHvalue());
                    superScroll(3, scroll3.getHvalue());

                });
        scroll4.hvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

                    //System.out.println("Scroll 1 horizontal value:" + scroll1.getHvalue());
                    //levelScroll.setHvalue(outerScroll.getHvalue());
                    superScroll(4, scroll4.getHvalue());

                });
        scroll5.hvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

                    //System.out.println("Scroll 1 horizontal value:" + scroll1.getHvalue());
                    //levelScroll.setHvalue(outerScroll.getHvalue());
                    superScroll(5, scroll5.getHvalue());

                });
        scroll6.hvalueProperty().addListener(
                (ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {

                    //System.out.println("Scroll 1 horizontal value:" + scroll6.getHvalue());
                    //levelScroll.setHvalue(outerScroll.getHvalue());
                    superScroll(-1, scroll6.getHvalue());

                });
        // TODO
    }
    private HashMap<String,Integer> checkboxToCode;
    private ArrayList<HashSet<Gem>> gemSetUnionHolder;
    private HashMap<Gem, Node> cachedFXMLSmap;

    public void load(double rootW){
        //divide in 3 categories
        HashSet<String> gemTags = new HashSet<>();
        HashSet<String> activeTags = new HashSet<>();
        HashSet<String> supportTags = new HashSet<>();
        HashSet<Gem> activeGems = new HashSet<>(); // this will be our set id 0 see later
        HashSet<Gem> supportGems = new HashSet<>(); // this will be our set id 1 see later
        checkboxToCode = new HashMap<>(); // a map to link checkboxes clicked with the filters-sets.
        cachedFXMLSmap = new HashMap<>(); // a map to link each gem with an fxml window.
        //an event to listen later on the checkbox
        EventHandler eh = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (event.getSource() instanceof JFXCheckBox) {
                    JFXCheckBox chk = (JFXCheckBox) event.getSource();
                    System.out.println("Action performed on checkbox " + chk.getText());
                    customCheckboxClicker(chk.getText(), chk.isSelected());
                }
            }
        };
        //find all available tags at the time being. prob not a matter of change any time soon so consider hardcode to save 500ms or so
        for(Gem gem : GemHolder.getInstance().getGemsClass()){
            for(String s : gem.tags){
                gemTags.add(s);
                if(gem.isActive) {activeTags.add(s); activeGems.add(gem);}
                else {supportTags.add(s); supportGems.add(gem);}
            }
            //create the cached versions
            FXMLLoader loader = new FXMLLoader(getClass().getResource("gemButton.fxml"));
            //gemButton con = null;
            JFXButton gemButton= null;
            try {
                gemButton = (JFXButton) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            GemButton_Controller controller = loader.<GemButton_Controller>getController();
            controller.loadFiltering(gem,this);
            cachedFXMLSmap.put(gem,gemButton);
        }

        //once everything is loaded
        activeTags.remove("Active");
        supportTags.remove("Support");

        //find the exclusive gems. When selecting filter Active that will eliminate for example
        //all filters that cant co-exist with active tag.
        HashSet<String> active_excl = new HashSet<>();
        HashSet<String> support_excl = new HashSet<>();
        //mutual are the remaining tags that can exist in both filter scenarios
        HashSet<String> mutual = new HashSet<>();

        System.out.println("Active tags exclusive. ");
        for(String s : activeTags){
            if(!supportTags.contains(s)){
                active_excl.add(s); //System.out.println(s);
            }
        }
        System.out.println("Support tags exclusive. ");
        for(String s : supportTags){
            if(!activeTags.contains(s)){
                support_excl.add(s); //System.out.println(s);
            }
        }
        System.out.println("Mutual tags. ");
        for(String s : gemTags){
            if(!active_excl.contains(s) && !support_excl.contains(s)){
                mutual.add(s); //System.out.println(s);
            }
        }

        //NOTE FOR NOW I WILL NOT USE THE MUTUALLY EXCLUDED GROUPS.

        //at first load everything into the big panel
        //also clear the things inside - loaded from fxml
        filtersLayoutBig.getChildren().clear();

        //at the same time create the X-tags sets.
        //given certain id's to match with arraylist.get will simplify this
        gemSetUnionHolder = new ArrayList<>();

        //we wanna start by 0 and 1 for active and support
        //also we want to link the checkboxes with some ID's
        gemSetUnionHolder.add(activeGems); //0
        gemSetUnionHolder.add(supportGems); //1
        //manually link them
        checkboxToCode.put("Active",0);
        checkboxToCode.put("Support",1);
        // set listener for premade buttons
        activeCbox.setOnAction(eh);
        supportCbox.setOnAction(eh);
        int counter = 2;
        for(String s : mutual){
            HashSet<Gem> dummy = new HashSet<>();
            for(Gem g : GemHolder.getInstance().getGemsClass()){
                if(g.tags.contains(s)){ //arraylist contains is a thing?
                    dummy.add(g);// this set will contain all gems that have tag X.
                }
            }
            //add the set
            gemSetUnionHolder.add(dummy);
            //display the checkbox and set listener
            JFXCheckBox jfxCheckBox = new JFXCheckBox(s);
            jfxCheckBox.setOnAction(eh); //eh is set up above.
            filtersLayoutBig.getChildren().add(jfxCheckBox);
            //link the id
            checkboxToCode.put(s, counter++);
        }
        //do the same for the other 2 categories
        for(String s : support_excl){
            HashSet<Gem> dummy = new HashSet<>();
            for(Gem g : GemHolder.getInstance().getGemsClass()){
                if(g.tags.contains(s)){ //arraylist contains is a thing?
                    dummy.add(g);// this set will contain all gems that have tag X.
                }
            }
            //add the set
            gemSetUnionHolder.add(dummy);
            //display the checkbox and set listener
            JFXCheckBox jfxCheckBox = new JFXCheckBox(s);
            jfxCheckBox.setOnAction(eh); //eh is set up above.
            filtersLayoutBig.getChildren().add(jfxCheckBox);
            //link the id
            checkboxToCode.put(s, counter++);
        }
        for(String s : active_excl){
            HashSet<Gem> dummy = new HashSet<>();
            for(Gem g : GemHolder.getInstance().getGemsClass()){
                if(g.tags.contains(s)){ //arraylist contains is a thing?
                    dummy.add(g);// this set will contain all gems that have tag X.
                }
            }
            //add the set
            gemSetUnionHolder.add(dummy);
            //display the checkbox and set listener
            JFXCheckBox jfxCheckBox = new JFXCheckBox(s);
            jfxCheckBox.setOnAction(eh); //eh is set up above.
            filtersLayoutBig.getChildren().add(jfxCheckBox);
            //link the id
            checkboxToCode.put(s, counter++);
        }

        //lastly we need a set of strings-tags to represent the selected filters. more code in the customCheckboxClicker below.




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
                con.setPrefWidth(rootW); //< here setting stretch
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

    @FXML
    private void toggleFilters(){
        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {

                //return getHeight();
                return AnchorPane.getTopAnchor(contentPanel);
            }

            @Override
            public void setValue(Double value)
            {
                //setHeight(value);
                AnchorPane.setTopAnchor(contentPanel, value);
            }
        };


        Timeline slideIn = new Timeline();

        KeyValue kv = new KeyValue(writableWidth, 43d);
        KeyFrame kf_slideIn = new KeyFrame(Duration.millis(400), kv);
        slideIn.getKeyFrames().addAll(kf_slideIn);

        slideIn.setOnFinished(e -> Platform.runLater(() -> {filtersPaneBig.setVisible(false);
            filtersPaneSmall.setVisible(true); setUpTabs();}));

        slideIn.play();


    }

    private void setUpTabs(){
        filtersLayoutSmall.getChildren().clear();
        int code = 0;
        for(String s : tagsActive){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("filterTab.fxml"));
            //gemButton con = null;
            AnchorPane filterTab= null;
            try {
                filterTab = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(QuestSplitPanel_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            FilterTab_Controller controller = loader.<FilterTab_Controller>getController();
            controller.load(s,this,code++);
            filtersLayoutSmall.getChildren().add(filterTab);
        }
    }

    public void filterTabClosed(String s, int code){
        customCheckboxClicker(s,false);
        Integer integer = checkboxToCode.get(s);
        JFXCheckBox node = null;
        if(integer== 0 || integer ==1){
            if(integer == 0){
                activeCbox.setSelected(false);
            }else{
                supportCbox.setSelected(false);
            }
        }
        else {
             node = (JFXCheckBox)filtersLayoutBig.getChildren().get(integer-2); //small mistake on code atm
             node.setSelected(false);
        }

        filtersLayoutSmall.getChildren().remove(code);
    }

    @FXML
    private void toggleFiltersVisible(){
        WritableValue<Double> writableWidth = new WritableValue<Double>() {
            @Override
            public Double getValue() {

                //return getHeight();
                return AnchorPane.getTopAnchor(contentPanel);
            }

            @Override
            public void setValue(Double value)
            {
                //setHeight(value);
                AnchorPane.setTopAnchor(contentPanel, value);
            }
        };


        Timeline slideIn = new Timeline();

        KeyValue kv = new KeyValue(writableWidth, 153d);
        KeyFrame kf_slideIn = new KeyFrame(Duration.millis(400), kv);
        slideIn.getKeyFrames().addAll(kf_slideIn);

        slideIn.setOnFinished(e -> Platform.runLater(() -> {filtersPaneBig.setVisible(true);
            filtersPaneSmall.setVisible(false);}));

        slideIn.play();

    }

    @FXML
    private void clearFilters(){
        gemsDisplayed.clear();
        filteredGemsPane.getChildren().clear();
        for(Node e : filtersLayoutBig.getChildren()){
             JFXCheckBox a =  (JFXCheckBox) e;
             a.setSelected(false);
        }
        activeCbox.setSelected(false);
        supportCbox.setSelected(false);
        tagsActive.clear();
        tabpane.setVisible(true);
        filteredPane.setVisible(false);
    }

    private HashSet<String> tagsActive = new HashSet<>();
    private HashSet<Gem> gemsDisplayed = new HashSet<>();


    private void customCheckboxClicker(String tagName,boolean isSelected){
        gemsDisplayed.clear();
        filteredGemsPane.getChildren().clear();
        if(isSelected){
            tagsActive.add(tagName);
        }else{
            tagsActive.remove(tagName);
        }

        System.out.print("New search looking for tags : ");
        for(String s : tagsActive){
            System.out.print(s+ " - ");
        }
        System.out.println();

        if(tagsActive.isEmpty()) {tabpane.setVisible(true); filteredPane.setVisible(false);}
        else {tabpane.setVisible(false); filteredPane.setVisible(true);}

        System.out.print("Gems found : ");
        for(Gem g : GemHolder.getInstance().getGemsClass()){
            if(g.tags.containsAll(tagsActive)){
                //gemsDisplayed.add(g);
                System.out.print(g.getGemName()+ " - ");
                filteredGemsPane.getChildren().add(cachedFXMLSmap.get(g));
            }
        }
        System.out.println();
        /* in complete union code
        for(String s : tagsActive){
            Integer set_code = checkboxToCode.get(s);
            HashSet<Gem> intersection_part = gemSetUnionHolder.get(set_code);
            gemsDisplayed.addAll(intersection_part);
        }*/


    }

}
