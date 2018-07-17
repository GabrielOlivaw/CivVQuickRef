/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref.createciv.controller;

import civvquickref.CivilizationList;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author gabag
 */
public class CreateCiv_Controller implements Initializable {
    
    private static final String IMG_FOLDER = "./data/img/";
    
    @FXML
    private TextField civName;
    @FXML
    private TextField civLeader;
    @FXML
    private TextArea civSkill;

    @FXML
    private Button addImageButton;
    
    @FXML
    private ImageView civilizationImage;
    
    private FileChooser imageChooser;
    
    private URI imageURI;
    
    @FXML
    private TextField unitName1;
    @FXML
    private ComboBox unitType1;
    @FXML
    private TextField unitReplaces1;
    
    @FXML
    private TextField unitName2;
    @FXML
    private ComboBox unitType2;
    @FXML
    private TextField unitReplaces2;
    
    private ObservableList<String> unitTypeSource;
    
    private CivilizationList.Civ civilization;
    
    private boolean civilizationCreated;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        civilizationCreated = false;
        
        initializeFileChooser();
        initializeComboBox();
    }

    private void initializeFileChooser() {
        imageChooser = new FileChooser();
        imageChooser.setTitle("Choose civilization icon");
        imageChooser.getExtensionFilters().add(new ExtensionFilter("Image files",
                Arrays.asList("*.jpg", "*.png")));

    }
    
    private void initializeComboBox() {
        unitTypeSource = FXCollections.observableList(Arrays.asList("Unit", "Building", "Improvement"));
        
        unitType1.setItems(unitTypeSource);
        unitType2.setItems(unitTypeSource);
        unitType1.getSelectionModel().selectFirst();
        unitType2.getSelectionModel().selectFirst();
    }

    @FXML
    public void addImageButtonPressed(ActionEvent event) {
        File file = imageChooser.showOpenDialog(civName.getScene().getWindow());
        if (file != null) {
            imageURI = file.toURI();

            civilizationImage.setImage(new Image(imageURI.toString()));

            System.out.println(FilenameUtils.getName(imageURI.getPath()));
        }
    }

    @FXML
    public void cancelButtonPressed(ActionEvent event) {
        ((Button) event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    public void acceptButtonPressed(ActionEvent event) {
        civilization = new CivilizationList.Civ();
        civilization.setCivname(civName.getText());
        civilization.setCivleader(civLeader.getText());
        civilization.setCivskill(civSkill.getText());
        String civImgStr = "";
        if (imageURI != null) {
            try {
                civImgStr = FilenameUtils.getName(imageURI.getPath());
                copyImg();
            } catch (IOException ex) {
                Logger.getLogger(CreateCiv_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        civilization.setCivimg(civImgStr);
        
        CivilizationList.Civ.Civunits.Unit unit1 = 
                new CivilizationList.Civ.Civunits.Unit();
        
        unit1.setUnitname(unitName1.getText());
        unit1.setUnittype(unitType1.getSelectionModel().getSelectedItem().toString());
        unit1.setUnitreplaces(unitReplaces1.getText());
        
        CivilizationList.Civ.Civunits.Unit unit2 = 
                new CivilizationList.Civ.Civunits.Unit();
        
        unit2.setUnitname(unitName2.getText());
        unit2.setUnittype(unitType2.getSelectionModel().getSelectedItem().toString());
        unit2.setUnitreplaces(unitReplaces2.getText());
        
        CivilizationList.Civ.Civunits civunits = new CivilizationList.Civ.Civunits();
        civunits.getUnit().add(unit1);
        civunits.getUnit().add(unit2);
        civilization.setCivunits(civunits);
        
        civilizationCreated = true;
        
        ((Button) event.getSource()).getScene().getWindow().hide();
    }
    
    /**
     * This method copies the selected image into the img folder of the program 
     * data folder, if it doesn't exist there. This is done because the XML with 
     * the civilization data stores the names of the civilization image files 
     * inside the img folder.
     * @throws java.io.IOException
     */
    public void copyImg() throws IOException {
        File selectedImg = new File(imageURI.getPath());
        File targetImgLocation = new File(IMG_FOLDER + FilenameUtils.
                getName(imageURI.getPath()));
        if (!targetImgLocation.exists())
            Files.copy(Paths.get(imageURI), targetImgLocation.toPath());
            
    }
    
    public CivilizationList.Civ getCivilization() {
        return this.civilization;
    }
    
    public URI getImageURI() {
        return this.imageURI;
    }
    
    public boolean isCivilizationCreated() {
        return this.civilizationCreated;
    }

}
