package poe.level.fx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import poe.level.data.CharacterInfo;
import poe.level.data.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * FXML Controller class
 *
 * @author Protuhj
 */
public class SelectCharacter_PopupController implements Initializable {

    private final ArrayList<CharacterInfo> m_characterList;

    public SelectCharacter_PopupController(ArrayList<CharacterInfo> characterList) {
        this.m_characterList = characterList;
    }

        @FXML
    private Label lblDialogTitle;
    @FXML
    private VBox buildsBox;

    private Consumer<CharacterInfo> closePopupFunction;

    public void hook(Consumer<CharacterInfo> closePopupFunction) {
        this.closePopupFunction = closePopupFunction;
    }

    public void update(int id) {
        assert (m_characterList != null);
        closePopupFunction.accept(m_characterList.get(id));
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lblDialogTitle.setText("Characters :");
        assert (m_characterList != null);
        for (int i = 0; i < m_characterList.size(); i++) { //this might require to update the buildsLoaded on each new build added and removed
            CharacterInfo ci = m_characterList.get(i);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("buildEntry.fxml"));
            try {
                //this will add the AnchorPane to the VBox
                buildsBox.getChildren().add(loader.load());
            } catch (IOException ex) {
                Logger.getLogger(SelectBuild_PopupController.class.getName()).log(Level.SEVERE, null, ex);
            }
            BuildEntry_Controller bec = loader.<BuildEntry_Controller>getController(); //add controller to the linker class
            bec.init_for_popup(Util.charToImage(ci.className, ci.ascendancyName), ci.characterName, ci.ascendancyName, i, this::update);
        }
    }


}
