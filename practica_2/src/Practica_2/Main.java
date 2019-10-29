/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2;

import es.upv.dsic.gti_ia.core.AgentsConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Guillermo
 */
public class Main {
    private final static String VHOST = "Practica2";
    public final static String USER = "Backman";
    public final static String PASSWORD = "BVgFPXaM";
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AgentsConnection.connect("isg2.ugr.es", 6000, VHOST, USER, PASSWORD, false);
        try {
            Agent GB_agent = new Agent("GB_AGENT");
            GB_agent.execute();
        } catch (Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
}
