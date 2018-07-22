/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package civvquickref;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This is a quick reference guide about Sid Meier's Civilization V, the 
 * popular strategy game.
 * It shows information about the different civilizations you can play.
 * This application uses XML to save and load said information using JAXB.
 * 
 * Warning about NetBeans JAXB binding: the generated classes will have comments 
 * using the system locale, so if those comments have strange characters, it will 
 * mess with the UTF-8 encoding and it will prevent you from building the project. 
 * Solution: go to NetBeans installation folder\etc\netbeans.conf and add these 
 * options to netbeans_default_options inside the quotes: 
 * -J-Dfile.encoding=UTF-8 -J-Duser.language=en
 * Then, restart the NetBeans IDE, delete the existing JAXB binding and create 
 * another one. Now every comment in the generated classes will be in english 
 * and the problem with strange characters will be prevented.
 * 
 * @author gabag
 */
public class CivVQuickRef extends Application{
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().
                getResource("civvquickref/resources/view/civvquickref.fxml"));
        
        Scene scene = new Scene(root);
        
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
        });
        
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
