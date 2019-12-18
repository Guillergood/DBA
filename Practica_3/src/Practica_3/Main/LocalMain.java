/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import es.upv.dsic.gti_ia.core.AgentsConnection;


/**
 *
 * @author Bruno García Trípoli
 */
public class LocalMain {
    private final static String VHOST = "Practica3";
    public final static String USER = "Backman";
    public final static String PASSWORD = "BVgFPXaM"; 
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AgentsConnection.connect("isg2.ugr.es", 6000, VHOST, USER, PASSWORD, false);
        Bureaucratic.getInstance().execute();
    }
}
