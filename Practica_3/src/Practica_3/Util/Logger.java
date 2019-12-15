/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Util;

import Practica_3.Main.Agent;
import Practica_3.Main.Bureaucratic;

/**
 *
 * @author Bruno García Trípoli
 */
public class Logger {
    private final String agent_name;    
    
    public Logger(Agent agent){
        agent_name=agent.getName();
    }
    
    public Logger(Bureaucratic agent){
        agent_name=agent.getName();
    }
    
    private String header(){return "> "+agent_name+":";}
    
    public void Info(String msg){
        System.out.println(header()+msg);
    }
    
    public void Info(String msg,Object... args){
        System.out.println(header()+ String.format(msg, args));
    }
}
