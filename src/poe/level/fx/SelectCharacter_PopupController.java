package poe.level.fx;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import poe.level.data.CharacterInfo;

import java.io.IOException;
import java.net.URL;
import java.util.*;
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

    SelectCharacter_PopupController(ArrayList<CharacterInfo> characterList) {
        this.m_characterList = characterList;
    }

    @FXML
    private JFXListView charactersBox;
    @FXML
    private JFXComboBox<String> cmbLeagueFilter;

    private final HashMap<Node, String> m_NodeToLeague = new HashMap<>();
    private Consumer<CharacterInfo> closePopupFunction;

    public void hook(Consumer<CharacterInfo> closePopupFunction) {
        this.closePopupFunction = closePopupFunction;
    }

    public void update(int id) {
        assert (m_characterList != null);
        closePopupFunction.accept(m_characterList.get(id));
    }

    private void filterChanged() {
        filterCharacters(cmbLeagueFilter.getValue());
    }

    private void filterCharacters(String league) {
        /*
        for (Node node : charactersBox.getItems()) {
            node.setVisible(league.isEmpty() || "All".equalsIgnoreCase(league) || m_NodeToLeague.get(node).equalsIgnoreCase(league));
        }*/
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        assert (m_characterList != null);
        Set<String> leagueList = new LinkedHashSet<>();
        leagueList.add("All");
        for (int i = 0; i < m_characterList.size(); i++) { //this might require to update the buildsLoaded on each new build added and removed
            CharacterInfo ci = m_characterList.get(i);
            leagueList.add(ci.league);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("characterEntry.fxml"));
            try {
                Node n = loader.load();
                n.managedProperty().bind(n.visibleProperty());
                m_NodeToLeague.put(n, ci.league);
                //this will add the AnchorPane to the VBox
                charactersBox.getItems().add(n);
            } catch (IOException ex) {
                Logger.getLogger(SelectBuild_PopupController.class.getName()).log(Level.SEVERE, null, ex);
            }
            CharacterEntry_Controller cec = loader.getController(); //add controller to the linker class
            cec.init_for_popup(ci, i, this::update);
        }
        cmbLeagueFilter.getItems().addAll(leagueList);
        cmbLeagueFilter.setOnAction(event -> filterChanged());

    }


}
