/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.util.StringConverter;
import javax.xml.bind.JAXBException;

/**
 * Controller class of the FXML file.
 *
 * @author gabag
 */
public class CivVQuickRef_Controller implements Initializable {
    
    private static final String FILE = "CivVQuickRef.xml";
    
    @FXML
    private ComboBox civilizationList;
    
    @FXML
    private Label modName;
    
    @FXML
    private Label fileName;
    
    @FXML
    private ImageView civilizationImage;
    @FXML
    private Label civilizationName;
    @FXML
    private Label leaderName;
    @FXML
    private Label civilizationSkill;
    
    @FXML
    private Label unitName1;
    @FXML
    private Label unitType1;
    @FXML
    private Label unitReplaces1;
    @FXML
    private Label unitName2;
    @FXML
    private Label unitType2;
    @FXML
    private Label unitReplaces2;
    
    private Random rnd;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        Alert alert = new Alert(AlertType.ERROR, "", ButtonType.OK);
        
        rnd = new Random();
        
        try {
            loadData();
        } catch (JAXBException ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (FileNotFoundException ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (Exception ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }
    
    private void loadData() throws JAXBException, FileNotFoundException, Exception {
        
        CivVQuickRefDAO civDAO = new CivVQuickRefDAO_JAXB(FILE);
        
        ObservableList<CivilizationList.Civ> civList = FXCollections.observableList(civDAO.loadCivs());
        
        civilizationList.setItems(civList);
        civilizationList.setConverter(new StringConverter<CivilizationList.Civ>() {
            @Override
            public String toString(CivilizationList.Civ object) {
                return object.getCivname();
            }

            @Override
            public CivilizationList.Civ fromString(String string) {
                //throw new UnsupportedOperationException("Not supported yet.");
                return findCiv(civList, string);
            }
            
        });
        
        civilizationList.getSelectionModel().select(0);
        civilizationChanged();
        
        modName.setText(civDAO.loadModName());
        
        fileName.setText(civDAO.getSource());
    }
    
    private CivilizationList.Civ findCiv(ObservableList<CivilizationList.Civ> civList, String civName) {
        boolean found = false;
        int i = 0;
        
        while (!found && i < civList.size()) {
            found = civList.get(i).getCivname().equalsIgnoreCase(civName);
            if (!found)
                i++;
        }
        
        CivilizationList.Civ result = null;
        if (found)
            result = civList.get(i);
        
        return result;
    }
    
    /**
     * Handler of the random button pressed event. It selects a random civilization 
     * from the ComboBox.
     */
    @FXML
    public void randomPressed() {
        int numCivs = civilizationList.getItems().size();
        
        civilizationList.getSelectionModel().select(rnd.nextInt(numCivs));
        civilizationChanged();
    }
    
    /**
     * Handler for the ComboBox item changing event.
     */
    @FXML
    public void civilizationChanged() {
        CivilizationList.Civ selectedCiv = 
                (CivilizationList.Civ)civilizationList.getSelectionModel().getSelectedItem();
        
        File file = new File("img/" + selectedCiv.getCivimg() + ".png");
        Image image = new Image(file.toURI().toString());
        civilizationImage.setImage(image);
        
        civilizationName.setText(selectedCiv.getCivname());
        leaderName.setText(selectedCiv.getCivleader());
        civilizationSkill.setText(selectedCiv.getCivskill());
        
        unitName1.setText(selectedCiv.getCivunits().getUnit().get(0).getUnitname());
        unitType1.setText(selectedCiv.getCivunits().getUnit().get(0).getUnittype());
        unitReplaces1.setText(selectedCiv.getCivunits().getUnit().get(0).getUnitreplaces());
        
        unitName2.setText(selectedCiv.getCivunits().getUnit().get(1).getUnitname());
        unitType2.setText(selectedCiv.getCivunits().getUnit().get(1).getUnittype());
        unitReplaces2.setText(selectedCiv.getCivunits().getUnit().get(1).getUnitreplaces());
    }
    
}
