/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package poe.level.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXProgressBar;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import poe.level.data.GithubHelper;

/**
 * FXML Controller class
 *
 * @author Christos
 */
public class UpdaterController implements Initializable {

    @FXML
    private JFXProgressBar progressbar;
    @FXML
    private Label label;
    @FXML
    private Label final_label;
    @FXML
    private AnchorPane updatePane;
    @FXML
    private AnchorPane askUpdatePane;
    @FXML
    private JFXButton acceptUpdate;
    @FXML
    private JFXButton declineUpdate;
    @FXML
    private JFXButton cancelUpdate;
    @FXML
    private Label lblUpdate;
    @FXML
    private TextArea txtReleaseNotes;
    @FXML
    private Label lblWarningText;


    public static GithubHelper.ReleaseInfo newReleaseInfo;

    private static double SPACE_KB = 1024;
    private static double SPACE_MB = 1024 * SPACE_KB;
    private static double SPACE_GB = 1024 * SPACE_MB;
    private static double SPACE_TB = 1024 * SPACE_GB;

    private boolean initialized;
    public static boolean cancelDownload;
    public static boolean allowUpdate;
    public static boolean declUpdate;
    public static boolean waitForInput;

    private static String bytes2String(long sizeInBytes) {

        NumberFormat nf = new DecimalFormat();
        nf.setMaximumFractionDigits(2);

        try {
            if ( sizeInBytes < SPACE_KB ) {
                return nf.format(sizeInBytes) + " Byte(s)";
            } else if ( sizeInBytes < SPACE_MB ) {
                return nf.format(sizeInBytes/SPACE_KB) + " KB";
            } else if ( sizeInBytes < SPACE_GB ) {
                return nf.format(sizeInBytes/SPACE_MB) + " MB";
            } else if ( sizeInBytes < SPACE_TB ) {
                return nf.format(sizeInBytes/SPACE_GB) + " GB";
            } else {
                return nf.format(sizeInBytes/SPACE_TB) + " TB";
            }
        } catch (Exception e) {
            return sizeInBytes + " Byte(s)";
        }

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initialized = false;
        cancelDownload = false;
        allowUpdate = false;
        declUpdate = false;
        waitForInput= true;
        lblUpdate.setText("Version " + newReleaseInfo.version + " is available. Do you wish to update? (" + bytes2String(newReleaseInfo.byteSize) + ")");
        txtReleaseNotes.setText(newReleaseInfo.releaseNotes);
        String updatedJarName = "PathOfLeveling-" + newReleaseInfo.version + ".jar";;
        if (Files.exists(Paths.get(updatedJarName))) {
            lblWarningText.setText("Notice: it appears that you have already downloaded this file. Make sure you ran " + updatedJarName);
            lblWarningText.setVisible(true);
        } else {
            lblWarningText.setVisible(false);
        }
    }

    @FXML
    private void accept(){
        cancelDownload = false;
        askUpdatePane.setVisible(false);
        updatePane.setVisible(true);
        allowUpdate = true;
    }

    @FXML
    private void decline(){
        allowUpdate = false;
        declUpdate = true;
    }

    @FXML
    private void cancel(){
        askUpdatePane.setVisible(true);
        updatePane.setVisible(false);
        //and reset maybe idk or stop updating somehow
        cancelDownload = true;

    }

    public void notify(Double prog){
        if(!initialized){
            final_label.setText("/" + (bytes2String(newReleaseInfo.byteSize)));
            initialized = true;
        }
        String done = (bytes2String((long)(double)prog));
        label.setText(done);
        Double progress_made = prog/newReleaseInfo.byteSize;
        progressbar.setProgress(progress_made);
        //label.setText(prog.intValue() + " %");
    }

}
