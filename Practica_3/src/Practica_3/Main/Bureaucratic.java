/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import DBA.SuperAgent;
import Practica_3.Util.Logger;
import com.eclipsesource.json.JsonObject;
import es.upv.dsic.gti_ia.core.ACLMessage;
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
    private final static String VHOST = "Practica3";
    public final static String USER = "Backman";
    public final static String PASSWORD = "BVgFPXaM"; 
    
    private Bureaucratic(String name) throws Exception{
        super(new AgentID(name));
        LOGGER = new Logger(this);
    }

    @Override
    protected void execute() {
        super.execute(); 
        //TODO
    }
    
    /**
     * <p> Send a message to the controller. </p>     
     * @author Guillermo Bueno
     * @param recvId is the receiver id
     * @param performative is the variable as aspected
     * @param content is the content of the message
     * @param replyTo is to whom the message was sent
     * @param convID is the conversation ID
     */
    public void sendMessage(String recvId, String performative, String content, String replyTo, String convID) {
        ACLMessage outbox = new ACLMessage(); 
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Bellatrix"));
        outbox.setPerformative(performative);
        outbox.addReceiver(new AgentID(recvId));
        outbox.setInReplyTo(replyTo);
        outbox.setContent(content);
        if(convID != null)
            outbox.setConversationId(convID);
        this.send(outbox);
    }
    
    
    private boolean login() throws InterruptedException{
        boolean continua = true;
        while(continua){
            JsonObject jsonMsg = new JsonObject();
            jsonMsg.add("map", "playground");
            jsonMsg.add("user", USER);
            jsonMsg.add("password", PASSWORD);
            ACLMessage message = new ACLMessage(ACLMessage.SUBSCRIBE);
            message.setContent(jsonMsg.toString());
            send(message);

            ACLMessage checkin = receiveACLMessage();
            
            if(checkin.getPerformativeInt() == ACLMessage.INFORM){
                continua = false;
                String content = checkin.getContent();
                parseSession(content);
                parseDimensions(content);
                parseMap(content);
                divideMap();
                sendDrones();
                
            }
            
        }
        
        
        
        
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
