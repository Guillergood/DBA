/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Util;

import Practica_3.Main.Agent;
import Practica_3.Main.Bureaucratic;
import es.upv.dsic.gti_ia.core.ACLMessage;

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
    
    public void Error(String msg,Object... args){
        System.err.println(header()+ String.format(msg, args));
    }
    
    public void Info(String msg,Object... args){
        System.out.println(header()+ String.format(msg, args));
    }
    
    public void printACLMessage(ACLMessage msg){
        String performative = msg.getPerformative();
        String content= msg.getContent();
        String conversationId = msg.getConversationId();
        String sender = msg.getSender().getLocalName();
        String reciver = msg.getReceiver().getLocalName();
        switch(msg.getPerformativeInt())
        {
            case ACLMessage.REFUSE:
            case ACLMessage.FAILURE:
            case ACLMessage.NOT_UNDERSTOOD:
               Error("[\"%s\" -> \"%s\"] %s %s:CONVERSATION-ID %s"
                       ,sender,reciver,performative,content,conversationId);    
            default:
               Info("[\"%s\" -> \"%s\"] %s %s:CONVERSATION-ID %s"
                       ,sender,reciver,performative,content,conversationId);    
        }
    }
}
