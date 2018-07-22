/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref.controller;

import civvquickref.dao.CivVQuickRefDAO;
import civvquickref.dao.CivVQuickRefDAO_JAXB;
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
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
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
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
    private static final String CIV_NO_IMG = "civnoimg.png";
    private String xmlFile = "CivVQuickRef.xml";

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

    private Stage createCivStage;
    private Stage editCivStage;
    private Stage helpStage;

    private Alert errorAlert;
    private Alert infoAlert;
    private Alert confirmAlert;

    private FileChooser xmlFileChooser;

    private Random rnd;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        errorAlert = new Alert(AlertType.ERROR, "", ButtonType.OK);
        infoAlert = new Alert(AlertType.INFORMATION, "", ButtonType.OK);
        confirmAlert = new Alert(AlertType.CONFIRMATION, "", ButtonType.OK, ButtonType.CANCEL);

        rnd = new Random();

        try {
            initializeXmlFileChooser();
            initializeFiles();
            loadData(DATA_FOLDER + xmlFile);
        } catch (URISyntaxException ex) {
            showErrorAlert(ex);
        } catch (FileNotFoundException ex) {
            showErrorAlert(ex);
        } catch (IOException ex) {
            showErrorAlert(ex);
        } catch (Exception ex) {
            showErrorAlert(ex);
        }
    }

    private void showErrorAlert(Exception ex) {
        errorAlert.setHeaderText(ex.getClass().getSimpleName());
        errorAlert.setContentText(ex.getMessage());
        errorAlert.showAndWait();
    }

    private void initializeXmlFileChooser() {
        xmlFileChooser = new FileChooser();
        String curPath = Paths.get(DATA_FOLDER).toAbsolutePath().normalize().toString();
        xmlFileChooser.setInitialDirectory(new File(curPath));
        xmlFileChooser.setTitle("Choose Civilization XML file");
        xmlFileChooser.getExtensionFilters().add(new ExtensionFilter("XML files", "*.xml"));
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
        File dataInitFolder = new File(DATA_FOLDER);
        if (!dataInitFolder.exists()) {
            dataInitFolder.mkdir();
        }

        // Check if the XML exists.
        File xmlInitFile = new File(DATA_FOLDER + this.xmlFile);
        if (!xmlInitFile.exists()) {
            initializeXml();
        }

        // Check if the img folder exists.
        File imgInitFolder = new File(IMG_FOLDER);
        if (!imgInitFolder.exists()) {
            imgInitFolder.mkdir();
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
                    getClassLoader().getResourceAsStream("civvquickref/resources/data/" + xmlFile), "UTF-8"));
            xmlWriter = new BufferedWriter(new FileWriter(DATA_FOLDER + xmlFile));
            String line;
            while ((line = xmlReader.readLine()) != null) {
                xmlWriter.write(line);
                xmlWriter.newLine();
            }
        } catch (UnsupportedEncodingException ex) {
            showErrorAlert(ex);
        } catch (IOException ex) {
            showErrorAlert(ex);
        } finally {
            try {
                if (xmlReader != null) {
                    xmlReader.close();
                }
                if (xmlWriter != null) {
                    xmlWriter.close();
                }
            } catch (IOException ex) {
                showErrorAlert(ex);
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
                    getClass().getClassLoader().getResourceAsStream(
                            "civvquickref/resources/data/imglookup.txt")));

            String imgFile;
            BufferedImage img;
            while ((imgFile = imgLookupReader.readLine()) != null) {
                img = ImageIO.read(getClass().getClassLoader().getResource(
                        "civvquickref/resources/data/img/" + imgFile));
                ImageIO.write(img, "png", new File(IMG_FOLDER + imgFile));
            }
        } catch (IOException ex) {
            showErrorAlert(ex);
        } finally {
            try {
                if (imgLookupReader != null) {
                    imgLookupReader.close();
                }
            } catch (IOException ex) {
                showErrorAlert(ex);
            }
        }
    }

    /**
     * Loads all the data contained in the XML file.
     *
     * @param xmlFileRoute The route of the XML file to load
     * @throws JAXBException
     * @throws FileNotFoundException
     * @throws URISyntaxException
     * @throws Exception
     */
    private void loadData(String xmlFileRoute) throws JAXBException, FileNotFoundException,
            URISyntaxException, Exception {

        civDAO = new CivVQuickRefDAO_JAXB(xmlFileRoute);

        civilizationListSource = FXCollections.observableList(civDAO.loadCivs());

        civilizationList.setItems(civilizationListSource);
        if (civilizationList.getConverter() != null) {
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
        }

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

        if (numCivs > 0) {
            civilizationList.getSelectionModel().select(rnd.nextInt(numCivs));
            civilizationChanged();
        }
    }

    /**
     * Handler for the ComboBox item changing event.
     *
     * Possible problem: When using the sort method with the ObservableList
     * which contains all the elements of the ComboBox, the onAction event
     * handled by this method is fired, leading to an InvocationTargetException.
     * This is solved by doing a null check on the selected item of the
     * ComboBox.
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

            CivilizationList.Civ.Civunits.Unit unit1 = selectedCiv.getCivunits().getUnit().get(0);
            unitName1.setText(unit1.getUnitname());
            unitType1.setText(unit1.getUnittype());
            unitReplaces1.setText(unit1.getUnitreplaces());

            CivilizationList.Civ.Civunits.Unit unit2 = selectedCiv.getCivunits().getUnit().get(1);
            unitName2.setText(unit2.getUnitname());
            unitType2.setText(unit2.getUnittype());
            unitReplaces2.setText(unit2.getUnitreplaces());
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

            if (createCivStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().
                        getResource("civvquickref/resources/view/createciv.fxml"));
                root = loader.load();

                Scene createCivScene = new Scene(root);

                createCivStage = new Stage();
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
                                showErrorAlert(ex);
                            }
                        }
                    });
                });
            }

            createCivStage.setIconified(false);
            createCivStage.requestFocus();
            createCivStage.show();
        } catch (IOException ex) {
            showErrorAlert(ex);
        }
    }

    @FXML
    public void editCivMenuSelected(ActionEvent event) {

        if (civilizationListSource.isEmpty()) {
            infoAlert.setContentText("The civilization list is empty.");
            infoAlert.showAndWait();
        } else {
            Parent root;

            try {
                if (editCivStage == null) {
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().
                            getResource("civvquickref/resources/view/createciv.fxml"));
                    root = loader.load();

                    CreateCiv_Controller editController = loader.getController();
                    int index = civilizationList.getSelectionModel().getSelectedIndex();
                    editController.setCivilization(civilizationListSource.get(index));
                    editController.initializeCivFields();

                    Scene editCivScene = new Scene(root);

                    editCivStage = new Stage();
                    editCivStage.setTitle("Edit civilization");
                    editCivStage.setScene(editCivScene);
                    editCivStage.setOnHiding((WindowEvent event1) -> {
                        Platform.runLater(() -> {
                            if (!editController.isCivilizationCreated()) {
                                try {
                                    FXCollections.sort(civilizationListSource,
                                            new CivNameComparator());
                                    civDAO.saveCivs(civilizationListSource, mod);
                                } catch (Exception ex) {
                                    showErrorAlert(ex);
                                }
                            }
                            //civilizationImage.setImage(new Image(createController.getImageURI().toString()));

                            // TODO Copy chosen image from created civ into img folder.
                        });
                    });
                }

                editCivStage.setIconified(false);
                editCivStage.requestFocus();
                editCivStage.show();
            } catch (IOException ex) {
                showErrorAlert(ex);
            }
        }
    }

    @FXML
    public void deleteCivMenuSelected(ActionEvent event) {
        if (civilizationListSource.isEmpty()) {
            infoAlert.setContentText("The civilization list is empty.");
            infoAlert.showAndWait();
        } else {
            confirmAlert.setContentText("Are you sure you want to delete the "
                    + "selected civilization?");
            confirmAlert.showAndWait();

            if (confirmAlert.getResult() == ButtonType.OK) {
                try {
                    int selectedIndex = civilizationList.getSelectionModel().getSelectedIndex();
                    civilizationListSource.remove(selectedIndex);
                    civDAO.saveCivs(civilizationListSource, mod);
                } catch (Exception ex) {
                    errorAlert.setHeaderText(ex.getClass().getSimpleName());
                    errorAlert.setContentText("There was an error while saving "
                            + "the changes.");
                    errorAlert.showAndWait();
                }
            }
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

    /**
     * Open menu handler. It shows an open dialog to allow the user to choose a
     * XML file. Then, it tries to load its data, checking if the file has the
     * correct CivilizationVGame XML structure.
     *
     * @param event
     */
    @FXML
    public void openMenuSelected(ActionEvent event) {
        File file = xmlFileChooser.showOpenDialog(modName.getScene().getWindow());

        /*
        Very important: we use the xmlPath local variable to temporarily save the 
        route of the XML file and then we pass it to the xmlFile global variable 
        after creating the DAO with the route and checking if that file has the 
        correct XML element structure.
         */
        String xmlPath;
        if (file != null) {
            try {
                xmlPath = file.getPath();
                loadData(xmlPath);
                xmlFile = xmlPath;
            } catch (JAXBException ex) {
                // 
                civDAO = new CivVQuickRefDAO_JAXB(xmlFile);
                errorAlert.setContentText("The chosen XML file doesn't have the "
                        + "proper CivilizationVGame structure.");
                errorAlert.showAndWait();
            } catch (FileNotFoundException ex) {
                showErrorAlert(ex);
            } catch (URISyntaxException ex) {
                showErrorAlert(ex);
            } catch (Exception ex) {
                showErrorAlert(ex);
            }
        }
    }

    @FXML
    public void newMenuSelected(ActionEvent event) {
        File file = xmlFileChooser.showSaveDialog(modName.getScene().getWindow());

        if (file != null) {
            emptyCivs();
            xmlFile = file.getPath();

            civDAO = new CivVQuickRefDAO_JAXB(xmlFile);
            fileName.setText(civDAO.getSource());

            mod = "";
            TextInputDialog input = new TextInputDialog();
            input.setTitle("Mod name");
            input.setHeaderText("Insert new CivilizationVGame mod name");
            input.setContentText("Mod name:");
            Optional<String> result = input.showAndWait();
            result.ifPresent(newModName -> {
                mod = newModName;

                modName.setText(mod);
            });
        }
    }

    @FXML
    public void exitMenuSelected(ActionEvent event) {

        confirmAlert.setHeaderText("Exit");
        confirmAlert.setContentText("Are you sure you want to exit?");
        confirmAlert.showAndWait();

        if (confirmAlert.getResult() == ButtonType.OK)
            Platform.exit();
    }

    @FXML
    public void helpMenuSelected(ActionEvent event) {
        Parent root;

        try {
            if (helpStage == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().
                        getResource("civvquickref/resources/view/civvquickref_help.fxml"));
                root = loader.load();

                Scene helpScene = new Scene(root);

                helpStage = new Stage();
                helpStage.setTitle("Help");
                helpStage.setScene(helpScene);

                helpStage.setResizable(false);
            }

            helpStage.setIconified(false);
            helpStage.requestFocus();
            helpStage.show();
        } catch (IOException ex) {
            Logger.getLogger(CivVQuickRef_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void emptyCivs() {
        civilizationListSource.clear();

        modName.setText("Core - Mod name");

        civilizationName.setText("Civilization name");
        leaderName.setText("Leader name");
        civilizationSkill.setText("Civilization skill");

        File file = new File(IMG_FOLDER + CIV_NO_IMG);
        Image image = new Image(file.toURI().toString());
        civilizationImage.setImage(image);

        unitName1.setText("Unit name");
        unitType1.setText("Unit type");
        unitReplaces1.setText("Unit replaces");

        unitName2.setText("Unit name");
        unitType2.setText("Unit type");
        unitReplaces2.setText("Unit replaces");
    }

}
