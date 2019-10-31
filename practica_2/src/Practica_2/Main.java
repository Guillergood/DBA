/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2;

import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Guillermo
 */
public class Main extends Application{
    static final int PREFERRED_WIDTH = 650;
    static final int PREFERRED_HEIGHT = 482;
    private final static String VHOST = "Practica2";
    public final static String USER = "Backman";
    public final static String PASSWORD = "BVgFPXaM";
    private static Agent GB_agent = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AgentsConnection.connect("isg2.ugr.es", 6000, VHOST, USER, PASSWORD, false);
        
        try {
            GB_agent = new Agent("GB_AGENT");            
            //GB_agent.execute();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("./GUI/fxml/Window.fxml"));        
        
        Scene scene = new Scene(root);
        
        stage.setTitle("Titulo provisional");
        stage.setWidth(PREFERRED_WIDTH);
        stage.setHeight(PREFERRED_HEIGHT);
        stage.setMinWidth(PREFERRED_WIDTH);
        stage.setMinHeight(PREFERRED_HEIGHT);
        stage.setScene(scene);
        stage.show();  
    }
    
    public static Agent getAgent(){return Main.GB_agent;}
    
}
