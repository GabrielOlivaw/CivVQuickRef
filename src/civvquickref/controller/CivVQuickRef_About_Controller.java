/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref.controller;

import civvquickref.CivVQuickRef;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

/**
 *
 * @author gabag
 */
public class CivVQuickRef_About_Controller implements Initializable {
    
    @FXML
    private Hyperlink githubLink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    @FXML
    public void githubLinkClicked (ActionEvent event) {
        CivVQuickRef.openLinkBrowser("https://" + githubLink.getText());
    }
    
}
