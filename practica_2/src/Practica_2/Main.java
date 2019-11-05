/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2;

import es.upv.dsic.gti_ia.core.AgentsConnection;
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
    private static int runTimes = 1;
    private static int zombieCount = 0;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AgentsConnection.connect("isg2.ugr.es", 6000, VHOST, USER, PASSWORD, false);
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
    
    public static Agent getAgent(Agent.Maps map){
        String aid = String.format("GB_AGENT_run_%s", runTimes);
        if(zombieCount>0)
            aid = aid.concat("_z"+zombieCount);
        try { 
            Agent ret = new Agent(aid,map);
            runTimes++;
            return ret;
        } catch (Exception ex) {
            System.err.println(String.format("Agent with aid: \"%s\" already exist. Zombie Count increased, trying again.", aid));
            zombieCount++;
            return getAgent(map);
        }
    }
    
}
