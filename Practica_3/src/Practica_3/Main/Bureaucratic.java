/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import DBA.SuperAgent;
import Practica_3.Util.Logger;
import es.upv.dsic.gti_ia.core.AgentID;

/**
 *
 * @author Alberto
 * @author Bruno García Trípoli
 */
public class Bureaucratic extends SuperAgent{
    public final boolean DEBUG = false;
    private static Bureaucratic INSTANCE = null;
    private static int zombie_count = 0;
    private final Logger LOGGER;
    
    private Bureaucratic(String name) throws Exception{
        super(new AgentID(name));
        LOGGER = new Logger(this);
    }

    @Override
    protected void execute() {
        super.execute(); 
        //TODO
    }
    
    public static Bureaucratic getInstance() {
        if(INSTANCE==null){
            String name = (zombie_count==0)?"Bureaucratic_B":"Bureaucratic_B_Z"+zombie_count;
            try {
                INSTANCE = new Bureaucratic(name);
            } catch (Exception ex) {
                System.err.println(String.format("Agent with aid: \"%s\" already exist. Zombie Count increased, trying again...", name));
                zombie_count++;
                return getInstance();
            }
        }
        return INSTANCE;
    } 
 
}
