/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref.controller;

import civvquickref.CivilizationList;
import civvquickref.createciv.controller.CreateCiv_Controller;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import javax.imageio.ImageIO;
import javax.xml.bind.JAXBException;

/**
 * Controller class of the FXML file.
 *
 * @author gabag
 */
public class CivVQuickRef_Controller implements Initializable {

    private static final String DATA_FOLDER = "./data/";
    private static final String IMG_FOLDER = "./data/img/";
    private static final String FILE = "CivVQuickRef.xml";

    private CivVQuickRefDAO civDAO;

    @FXML
    private ComboBox civilizationList;

    private ObservableList<CivilizationList.Civ> civilizationListSource;

    @FXML
    private Label modName;

    private String mod;

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
            initializeFiles();
            loadData();
        } catch (URISyntaxException ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (FileNotFoundException ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (IOException ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        } catch (Exception ex) {
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Creates the file system with the initial data.
     *
     * Note: Windows manages relative file routes correctly. However, Linux
     * (tested with Ubuntu 17.10) saves the data folder in /home/ directory if
     * the .jar was double clicked. If the execution happened in command line at
     * the appropriate folder there is no such problem.
     *
     * @throws URISyntaxException
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void initializeFiles() throws URISyntaxException, FileNotFoundException, IOException {

        // Check if the data folder exists.
        File dataFolder = new File(DATA_FOLDER);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // Check if the XML exists.
        File xmlFile = new File(DATA_FOLDER + FILE);
        if (!xmlFile.exists()) {
            initializeXml();
        }

        // Check if the img folder exists.
        File imgFolder = new File(IMG_FOLDER);
        if (!imgFolder.exists()) {
            imgFolder.mkdir();
            initializeImg();
        }
    }

    /**
     * Gets the XML resource with the initial data and copies it as a file
     * inside the data folder already created.
     */
    private void initializeXml() {
        BufferedReader xmlReader = null;
        BufferedWriter xmlWriter = null;
        try {
            xmlReader = new BufferedReader(new InputStreamReader(getClass().
                    getClassLoader().getResourceAsStream("data/" + FILE), "UTF-8"));
            xmlWriter = new BufferedWriter(new FileWriter(DATA_FOLDER + FILE));
            String line;
            while ((line = xmlReader.readLine()) != null) {
                xmlWriter.write(line);
                xmlWriter.newLine();
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CivVQuickRef_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CivVQuickRef_Controller.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (xmlReader != null) {
                    xmlReader.close();
                }
                if (xmlWriter != null) {
                    xmlWriter.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(CivVQuickRef_Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Gets all the image resources used by the initial XML and copies them as
     * files inside the img folder. To know how many images it has to load, a
     * txt file is used as a lookup of all the existing image resources.
     */
    private void initializeImg() {
        File dataFolder = new File(IMG_FOLDER);

        dataFolder.mkdir();

        BufferedReader imgLookupReader = null;
        try {
            imgLookupReader = new BufferedReader(new InputStreamReader(
                    getClass().getClassLoader().getResourceAsStream("data/imglookup.txt")));

            String imgFile;
            BufferedImage img;
            while ((imgFile = imgLookupReader.readLine()) != null) {
                img = ImageIO.read(getClass().getClassLoader().getResource("data/img/" + imgFile));
                ImageIO.write(img, "png", new File(IMG_FOLDER + imgFile));
            }
        } catch (IOException ex) {
        } finally {
            try {
                if (imgLookupReader != null) {
                    imgLookupReader.close();
                }
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Loads all the data contained in the XML file.
     *
     * @throws JAXBException
     * @throws FileNotFoundException
     * @throws URISyntaxException
     * @throws Exception
     */
    private void loadData() throws JAXBException, FileNotFoundException,
            URISyntaxException, Exception {

        civDAO = new CivVQuickRefDAO_JAXB(DATA_FOLDER + FILE);

        civilizationListSource = FXCollections.observableList(civDAO.loadCivs());

        civilizationList.setItems(civilizationListSource);
        civilizationList.setConverter(new StringConverter<CivilizationList.Civ>() {
            @Override
            public String toString(CivilizationList.Civ object) {
                return object.getCivname();
            }

            @Override
            public CivilizationList.Civ fromString(String string) {
                //throw new UnsupportedOperationException("Not supported yet.");
                return findCiv(civilizationListSource, string);
            }

        });

        civilizationList.getSelectionModel().selectFirst();
        civilizationChanged();

        mod = civDAO.loadModName();
        modName.setText(mod);

        fileName.setText(civDAO.getSource());
    }

    /**
     * Finds a Civ (civilzation) object inside the civilization list used by the
     * ComboBox.
     *
     * @param civList List of civilizations.
     * @param civName Name of the civilization to find.
     * @return The wanted civilization object.
     */
    private CivilizationList.Civ findCiv(ObservableList<CivilizationList.Civ> civList, String civName) {
        boolean found = false;
        int i = 0;

        while (!found && i < civList.size()) {
            found = civList.get(i).getCivname().equalsIgnoreCase(civName);
            if (!found) {
                i++;
            }
        }

        CivilizationList.Civ result = null;
        if (found) {
            result = civList.get(i);
        }

        return result;
    }

    /**
     * Handler of the random button pressed event. It selects a random
     * civilization from the ComboBox.
     *
     * @throws java.net.URISyntaxException
     */
    @FXML
    public void randomPressed() throws URISyntaxException {
        int numCivs = civilizationList.getItems().size();

        civilizationList.getSelectionModel().select(rnd.nextInt(numCivs));
        civilizationChanged();
    }

    /**
     * Handler for the ComboBox item changing event. 
     * 
     * Possible problem: When using the sort method with the ObservableList 
     * which contains all the elements of the ComboBox, the onAction event 
     * handled by this method is fired, leading to an InvocationTargetException. 
     * This is solved by doing a null check on the selected item of the ComboBox. 
     * 
     * https://stackoverflow.com/questions/40561850/javafx-combobox-setitems-triggers-onaction-event
     *
     * @throws java.net.URISyntaxException
     */
    @FXML
    public void civilizationChanged() throws URISyntaxException {
        
        CivilizationList.Civ selectedCiv
                = (CivilizationList.Civ) civilizationList.getSelectionModel().getSelectedItem();
        
        if (selectedCiv != null) {
            File file = new File(IMG_FOLDER + selectedCiv.getCivimg());
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

    /**
     * This method opens a new Create Civilization window and obtains the
     * created civilization when the accept button is pressed in said window.
     *
     * @param event
     */
    @FXML
    public void createCivMenuSelected(ActionEvent event) {
        Parent root;

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().
                    getResource("civvquickref/createciv/view/createciv.fxml"));
            root = loader.load();
            //root = FXMLLoader.load(getClass().getClassLoader().getResource("civvquickref/createciv/view/createciv.fxml"));

            Scene createCivScene = new Scene(root);

            Stage createCivStage = new Stage();
            createCivStage.setTitle("Create civilization");
            createCivStage.setScene(createCivScene);
            createCivStage.setOnHiding((WindowEvent event1) -> {
                Platform.runLater(() -> {
                    CreateCiv_Controller createController = loader.getController();
                    if (createController.isCivilizationCreated()) {
                        try {
                            civilizationListSource.add(createController.getCivilization());
                            FXCollections.sort(civilizationListSource,
                                    new CivNameComparator());
                            civDAO.saveCivs(civilizationListSource, mod);
                        } catch (Exception ex) {
                            Logger.getLogger(CivVQuickRef_Controller.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    //civilizationImage.setImage(new Image(createController.getImageURI().toString()));

                    // TODO Copy chosen image from created civ into img folder.
                });
            });

            createCivStage.show();
        } catch (IOException ex) {
            Logger.getLogger(CivVQuickRef_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This class implements the comparison of two Civ objects by their 
     * civilization names.
     */
    private class CivNameComparator implements Comparator<CivilizationList.Civ> {

        @Override
        public int compare(CivilizationList.Civ o1, CivilizationList.Civ o2) {
            String civ1 = o1.getCivname().toLowerCase();
            String civ2 = o2.getCivname().toLowerCase();
            return civ1.compareTo(civ2);
        }

    }

}
