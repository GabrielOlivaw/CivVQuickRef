/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;

/**
 *
 * @author gabag
 */
public class CivVQuickRef_Help_Controller implements Initializable {
    
    @FXML
    private TextArea helpText;
    
    private Alert errorAlert;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
        errorAlert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        
        getReadme();
    }
    
    private void showErrorAlert(Exception ex) {
        errorAlert.setHeaderText(ex.getClass().getSimpleName());
        errorAlert.setContentText(ex.getMessage());
        errorAlert.showAndWait();
    }
    
    /**
     * Get the README content and appends it to the TextArea of the help window.
     *
     * @param helpText
     */
    private void getReadme() {
        BufferedReader xmlReader = null;
        
        int pos = helpText.getCaretPosition();

        try {
            xmlReader = new BufferedReader(new InputStreamReader(getClass().
                    getClassLoader().getResourceAsStream("civvquickref/resources/data/README.txt"), "UTF-8"));
            String line;
            while ((line = xmlReader.readLine()) != null) {
                helpText.appendText(line);
                helpText.appendText("\n");
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
            } catch (IOException ex) {
                showErrorAlert(ex);
            }
        }
        
        helpText.positionCaret(pos);
    }
    
}
