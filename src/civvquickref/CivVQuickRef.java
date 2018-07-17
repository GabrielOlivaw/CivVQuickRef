/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This is a quick reference guide about Sid Meier's Civilization V, the 
 * popular strategy game.
 * It shows information about the different civilizations you can play.
 * This application uses XML to save and load said information using JAXB.
 *
 * @author gabag
 */
public class CivVQuickRef extends Application{
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resources/view/civvquickref.fxml"));
        
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("CivVQuickRef");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        launch(args);
        
    }
}
