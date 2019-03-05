/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.events.JFXDialogEvent;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import poe.level.data.Build;
import poe.level.fx.overlay.RecipeOverlay_Controller;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 *
 * @author Christos
 */
public class MainApp_Controller implements Initializable {

    @FXML
    private AnchorPane container;
    @FXML
    private AnchorPane buildsAnchorPane;
    @FXML
    private AnchorPane socketGroupsAnchorPane;
    @FXML
    private AnchorPane gemsPanelAnchorPane;
    @FXML
    private SplitPane splitPane;
    @FXML
    private Label label;
    @FXML
    private StackPane rootPane;
    @FXML
    private MenuItem export_pastebin_active;
    @FXML
    private MenuItem export_pastebin_all;
    @FXML
    private MenuItem export_clipboard_active;
    @FXML
    private MenuItem export_clipboard_all;
    @FXML
    private MenuItem get_pob_link;
    @FXML
    private MenuItem open_pob_view;
    @FXML
    private MenuItem link_active_pob;
    @FXML
    private Label footerValid;


    JFXDialog addBuildPopup;
    JFXDialog socketGroupGemPopup;
    Socket_group_noteController m_socketGroupGemPopupController;
    JFXDialog addGemPopup;
    AddGem_Controller m_addGemPopupController;
    JFXDialog buildPreviewPopup;

    //Controllers

    String build;
    String className;
    String ascendancy;
    ObservableList<Build> buildList;
    HashMap<BuildEntry_Controller,ObservableList<Label>> buildToSocketGroupMap;


    int count;
    int buildId;
    int socketGroupId;


    BuildsPanel_Controller buildspanel_controller;
    SocketGroupsPanel_Controller socketgroups_controller;
    GemsPanel_Controller gemspanel_controller;
    Editor_Stage parent;

    public void hook(Editor_Stage parent){
        this.parent = parent;
    }

    public void resize(double h, double w){
        System.out.println(h + "" + w);
        container.prefWidth(w);
        container.prefHeight(h);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        buildToSocketGroupMap = new HashMap<>();
        //option to reload builds from memory on start
        POELevelFx.reloadBuilds();
        //inflate buildspanel
        if(POELevelFx.buildsLoaded.isEmpty()){ //toggle all builds
            toggleAllBuilds(false);
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BuildsPanel.fxml"));
        try {
            buildsAnchorPane.getChildren().add(loader.load());
            //buildsAnchorPane = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        buildspanel_controller = loader.<BuildsPanel_Controller>getController();
        buildspanel_controller.hook(this);

        loader = new FXMLLoader(getClass().getResource("SocketGroupsPanel.fxml"));
        try {
            socketGroupsAnchorPane.getChildren().add(loader.load());
            //buildsAnchorPane = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        socketgroups_controller = loader.<SocketGroupsPanel_Controller>getController();
        socketgroups_controller.hook(this);
        buildspanel_controller.hookSG_Controller(socketgroups_controller);

        if(POELevelFx.buildsLoaded!=null){
            buildspanel_controller.loadBuilds(POELevelFx.buildsLoaded);
        }

        loader = new FXMLLoader(getClass().getResource("GemsPanel.fxml"));
        try {
            gemsPanelAnchorPane.getChildren().add(loader.load());
            //buildsAnchorPane = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        gemspanel_controller = loader.<GemsPanel_Controller>getController();
        gemspanel_controller.hook(this);
        socketgroups_controller.hookGem_Controller(gemspanel_controller);

    }

    //handling the more visual actions that occur
    //within the main app fxml

    //signals a new build and popup the dialog

    public void buildPopup() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addBuild.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        loader.<AddBuild_Controller>getController().hook(buildspanel_controller,this);

        addBuildPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        //controller.passDialog(mLoad);
        addBuildPopup.show();
    }

    public void closePopup(){
        addBuildPopup.close();
    }

    public AddGem_Controller gemPopup() {
        System.out.println("gemPopup");
        if (addGemPopup == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addGem.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            m_addGemPopupController = loader.getController();
            addGemPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            addGemPopup.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if (event.getCode() == KeyCode.ESCAPE) {
                        gemClosePopup();
                    }
                }
            });
            addGemPopup.show();
            addGemPopup.setOnDialogOpened(new EventHandler<JFXDialogEvent>() {
                @Override
                public void handle(JFXDialogEvent event) {
                    addGemPopup.requestFocus();
                }
            });
        } else {
            addGemPopup.show();
        }
        return m_addGemPopupController;
    }

    public void gemClosePopup(){
        addGemPopup.close();
    }

    public Socket_group_noteController notePopup() {
        System.out.println("Note popup");
        if (socketGroupGemPopup == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("socket_group_note.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            m_socketGroupGemPopupController = loader.getController();

            socketGroupGemPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            socketGroupGemPopup.setCacheContainer(true);
            //controller.passDialog(mLoad);
        }
        socketGroupGemPopup.show();
        return m_socketGroupGemPopupController;
    }

    public void noteClosePopup(){
        socketGroupGemPopup.close();
    }

    private boolean revalidation_switch = false;
    private boolean launcher_switch = false;
    private boolean export_switch = false;

    public void sayNoToValidation(){
        buildPreviewPopup.close();
    }

    public void sayYesToValidation(){
        buildPreviewPopup.close();
        buildspanel_controller.setBuildToNonValid();
        if(revalidation_switch){
            if(export_switch){
                revalidation_switch = false;
                export_switch = false;
                exportToPastebinAll();
            }else{
                if(launcher_switch){
                    revalidation_switch = false;
                    launcher_switch = false;
                    validateToLauncher();
                }else{
                    revalidation_switch = false;
                    launcher_switch = false;
                    saveAllBuilds();
                }
            }
        }else{
            if(export_switch){
                export_switch = false;
                exportToPastebinActive();
            }
        }
    }

    @FXML
    private void validateBuild(){
        if(buildspanel_controller.validate()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BuildProgressPreview.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            BuildProgressPreview_Controller controller = loader.<BuildProgressPreview_Controller>getController();

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            controller.start(buildspanel_controller.getCurrentBuild());
            buildPreviewPopup.show();
        }else{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ValidateErrorPopup.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            loader.<ValidateErrorPopupController>getController().setUp(buildspanel_controller.lastbuild_invalidated
                    , buildspanel_controller.validateError(),this);
            buildPreviewPopup.show();
        }
    }

    @FXML
    private void saveAllBuilds(){
        try {
            if(buildspanel_controller.validateAll())
                buildspanel_controller.saveBuild();
            else{
                revalidation_switch = true;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ValidateErrorPopup.fxml"));
                AnchorPane con = null;
                try {
                    con = (AnchorPane) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }

                buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
                //controller.passDialog(mLoad);
                loader.<ValidateErrorPopupController>getController().setUp(buildspanel_controller.lastbuild_invalidated,
                        buildspanel_controller.validateAllError(),this);
                buildPreviewPopup.show();
            }
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private boolean validateToLauncher(){
        try {
            if(buildspanel_controller.validateAll()){
                buildspanel_controller.saveBuild();
                parent.returnToLauncher();
                return true;
            }else{
                revalidation_switch = true;
                launcher_switch=true;
                FXMLLoader loader = new FXMLLoader(getClass().getResource("ValidateErrorPopup.fxml"));
                AnchorPane con = null;
                try {
                    con = (AnchorPane) loader.load();
                } catch (IOException ex) {
                    Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
                }

                buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
                //controller.passDialog(mLoad);
                loader.<ValidateErrorPopupController>getController().setUp(buildspanel_controller.lastbuild_invalidated,
                        buildspanel_controller.validateAllError(),this);
                buildPreviewPopup.show();
                return false;
            }
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean custom_editor_exit_with_validate(){
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Save changes");
        alert.setHeaderText("Any changes you made will be saved upon exit.");
        alert.setContentText("Are you ok with this?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            return validateToLauncher();
        }else{
            parent.returnToLauncher();
            return true;
        }
    }

    Pastebin_import_Controller paste_controller;
    Pastebin_import_pobController paste_pob_controller;

    @FXML
    private void importFromPastebin(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pastebin_import.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            paste_controller = loader.<Pastebin_import_Controller>getController();

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            paste_controller.hook(this);
            buildPreviewPopup.show();
    }

    @FXML
    private void exportToPastebinAll(){
        if(buildspanel_controller.validateAll()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("export_pastebin.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            loader.<Export_pastebin_Controller>getController().initPasteText(buildspanel_controller.allTo64());

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            buildPreviewPopup.show();
        }else{
            revalidation_switch = true;
            export_switch=true;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ValidateErrorPopup.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            loader.<ValidateErrorPopupController>getController().setUp(buildspanel_controller.lastbuild_invalidated,
                    buildspanel_controller.validateAllError(),this);
            buildPreviewPopup.show();
        }

    }

    @FXML
    private void exportToPastebinActive(){
        if(buildspanel_controller.validate()){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("export_pastebin.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            loader.<Export_pastebin_Controller>getController().initPasteText(buildspanel_controller.activeTo64());

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            buildPreviewPopup.show();
        }else{
            export_switch = true;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ValidateErrorPopup.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            loader.<ValidateErrorPopupController>getController().setUp(buildspanel_controller.lastbuild_invalidated
                    , buildspanel_controller.validateError(),this);
            buildPreviewPopup.show();
        }

    }

    public void toggleActiveBuilds(boolean toggle){
        export_pastebin_active.setDisable(!toggle);
        export_clipboard_active.setDisable(!toggle);
        link_active_pob.setDisable(!toggle);
        togglePobMenus(toggle);
    }

    public void toggleAllBuilds(boolean toggle){
        export_pastebin_all.setDisable(!toggle);
        export_clipboard_all.setDisable(!toggle);
    }

    public void toggleFooterVisibility(boolean valid){
        if(!valid){
            footerValid.setVisible(true);
        }else{
            footerValid.setVisible(false);
        }
    }

    public void fetchPaste(String pasteRaw){
        if(buildspanel_controller.loadBuildsFromPastebin(pasteRaw)){
            paste_controller.success();
            buildPreviewPopup.close();
        }else{
            paste_controller.failed();
        }
    }

    public void fetch_pob_paste(String pasteLink, int code){
      // code : 0 is create , 1 is link pob
      if(code == 0){
          //and make a new build.
          Build newBuild = extractBuildFromPOBPastebin(pastebin_import_pobController.getResponse());
          if(newBuild != null){
              newBuild.hasPob = true;
              newBuild.pobLink = pasteLink;
          }
      }else if(code == 1){
          buildspanel_controller.getCurrentBuild().hasPob = true;
          buildspanel_controller.getCurrentBuild().pobLink = pasteLink;

          togglePobMenus(true); //< requires active build selected.
      }

        buildPreviewPopup.close();

    }

    private Build extractBuildFromPOBPastebin(String raw){
        String replace = raw.replace('-','+').replace('_','/').trim();
        /*
        //save the replaced-values base64 string - optional
        PrintWriter out = new PrintWriter("decoded.txt");
        out.println(replace);
        out.close();*/

        byte[] byteValueBase64Decoded = null;
        try{
            byteValueBase64Decoded = Base64.getDecoder().decode(replace);
        }catch(java.lang.IllegalArgumentException e){
            e.printStackTrace();
            return null;
        }
        String inflatedXML = "";
        try{
            //inflate
            inflatedXML = inflate(byteValueBase64Decoded);
        }catch(IOException e){

        }catch(DataFormatException e){

        }
        //System.out.println(inflatedXML);
        /* //optional
        out = new PrintWriter("pathofbuilding.txt");
        out.println(inflatedXML);
        out.close();*/

        //JSONArray obj = new JsonParser().parse(stringValueBase64Encoded).getAsJsonArray();
        //JSONArray builds_array = new JSONArray(stringValueBase64Decoded);

        //parse XML
        ArrayList<ArrayList<String>> skills = new ArrayList<>();
        Document doc = convertStringToXMLDocument(inflatedXML);
        NodeList nList = doc.getElementsByTagName("Skill");
        NodeList buildInfo = doc.getElementsByTagName("Build");
        String className = "";
        String asc = "";
        String bandit = "";
        if(buildInfo.getLength() == 1){
            //check for bandit here
            Node info = buildInfo.item(0);
            Element eElement = (Element) info;
            className = eElement.getAttribute("className");
            asc = eElement.getAttribute("ascendClassName");
            bandit = eElement.getAttribute("bandit");
        }else{
            System.out.println("error");
        }
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            System.out.println("\nCurrent Element :" + nNode.getNodeName());
            ArrayList<String> skillNames = new ArrayList<>();
            for (int gemTemp = 0; gemTemp < nNode.getChildNodes().getLength(); gemTemp++) {
                Node item = nNode.getChildNodes().item(gemTemp);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) item;
                    String attribute = eElement.getAttribute("nameSpec");
                    System.out.println(attribute);
                    skillNames.add(attribute);
                }
            }
            if(!skillNames.isEmpty())
                skills.add(skillNames);

         }

        return buildspanel_controller.addNewBuildFromPOB("New build", className, asc,skills);

    }

    private String inflate(byte[] data) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!inflater.finished()) {
         int count = inflater.inflate(buffer);
         outputStream.write(buffer, 0, count);
        }
        outputStream.close();
        byte[] output = outputStream.toByteArray();
        System.out.println("Original: " + data.length);
        System.out.println("Compressed: " + output.length);
        return new String(output);
    }

     private Document convertStringToXMLDocument(String xmlString)
    {
        //Parser that produces DOM object trees from XML content
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        //API to obtain DOM Document instance
        DocumentBuilder builder = null;
        try
        {
            //Create DocumentBuilder with default configuration
            builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
            return doc;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private void togglePobMenus(boolean toggle){
        if(toggle){
            if(buildspanel_controller.getCurrentBuild().hasPob){
                get_pob_link.setDisable(!toggle);
                open_pob_view.setDisable(!toggle);
            }else{
                get_pob_link.setDisable(true);
                open_pob_view.setDisable(true);
            }
        }else{
            get_pob_link.setDisable(true);
            open_pob_view.setDisable(true);
        }
    }

    @FXML
    private void getPOB(){
         FXMLLoader loader = new FXMLLoader(getClass().getResource("clipboard_verify.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            buildPreviewPopup.show();
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(buildspanel_controller.getCurrentBuild().pobLink);
            clipboard.setContent(content);
    }

    private Pastebin_import_pobController pastebin_import_pobController;

    @FXML
    private void createFromPOB(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pastebin_import_pob.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            pastebin_import_pobController = loader.<Pastebin_import_pobController>getController();
            pastebin_import_pobController.hook(this,0);

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            //paste_pob_controller.hook(this);
            buildPreviewPopup.show();
    }

    @FXML
    private void linkPOB(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pastebin_import_pob.fxml"));
            AnchorPane con = null;
            try {
                con = (AnchorPane) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            loader.<Pastebin_import_pobController>getController().hook(this,1);

            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            //paste_pob_controller.hook(this);
            buildPreviewPopup.show();
    }

    @FXML
    private void openPOBwebview(){

            /*
            FXMLLoader loader = new FXMLLoader(getClass().getResource("POB_webview.fxml"));
            AnchorPane con = null;
            try {
            con = (AnchorPane) loader.load();
            } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
            loader.<POB_webviewController>getController().open("https://poe.technology/poebuddy/SSGAJpNZ");
            //loader.<POB_webviewController>getController().open("https://www.reddit.com/r/pathofexile/comments/aca7vl/path_of_leveling_a_tool_written_in_java_with_an/");


            buildPreviewPopup = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
            //controller.passDialog(mLoad);
            buildPreviewPopup.show();
            */
            String prefix = "https://poe.technology/poebuddy/?pastebin=";
            if(buildspanel_controller.getCurrentBuild().hasPob){
                try {
                    Desktop.getDesktop().browse(new URL(prefix+buildspanel_controller.getCurrentBuild().pobLink).toURI());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

    }


    @FXML
    private void about(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutPage.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER).show();
    }

    @FXML
    private void version(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("version.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER).show();
    }
    private JFXDialog jfxDialog;

    @FXML
    private void recipePopup(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("overlay/RecipeOverlay.fxml"));
        AnchorPane con = null;
        try {
            con = (AnchorPane) loader.load();
            loader.<RecipeOverlay_Controller>getController().hook(this);
        } catch (IOException ex) {
            Logger.getLogger(MainApp_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        jfxDialog = new JFXDialog(rootPane, con, JFXDialog.DialogTransition.CENTER);
        jfxDialog.show();
    }

    public void refreshRecipePopup(){
        jfxDialog.close();
        recipePopup();
    }
}
              /*
        BufferedImage before = null;
        try {
            before = ImageIO.read(getClass().getResource("/classes/"+className+"/"+ascendancy+".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
                int w = before.getWidth();
    int h = before.getHeight();
    // Create a new image of the proper size
    int w2 = (int) (w * 0.2);
    int h2 = (int) (h * 0.2);
    BufferedImage after = new BufferedImage(w2, h2, BufferedImage.TYPE_INT_ARGB);
    AffineTransform scaleInstance = AffineTransform.getScaleInstance(0.2, 0.2);
    AffineTransformOp scaleOp
        = new AffineTransformOp(scaleInstance, AffineTransformOp.TYPE_BILINEAR);

    after = scaleOp.filter(before, after);
            Image image = SwingFXUtils.toFXImage(after, null);
                //ImageIcon imageIcon = new ImageIcon(dimg);

                */