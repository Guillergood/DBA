/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import DBA.SuperAgent;
import Practica_3.Util.Logger;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.hp.hpl.jena.util.iterator.Map1;
import edu.emory.mathcs.backport.java.util.Arrays;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.stream.Stream;
import javafx.util.Pair;

/**
 *
 * @author Alberto
 * @author Bruno García Trípoli
 */
public class Bureaucratic extends SuperAgent{
    public final boolean DEBUG = true;
    private static Bureaucratic INSTANCE = null;
    private static int zombie_count = 0;
    private final Logger LOGGER;
    private final static String VHOST = "Practica3";
    public final static String USER = "Backman";
    public final static String PASSWORD = "BVgFPXaM";
    public final static String MAP_NAME = "playground";
    private ArrayList<String> names;
    private Agent[] agents = new Agent[4];
    private String session;
    private String convID;


    private Bureaucratic(String name) throws Exception{
        super(new AgentID(name));
        LOGGER = new Logger(this);
        names = new ArrayList<>();
    }



    @Override
    protected void execute() {
        super.execute();
        System.out.println();
        ACLMessage result = suscribe();
        createAgents();
        checkInAgents(result);
        try {
            waitStop();
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Bureaucratic.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ACLMessage suscribe(){
        
        
        ACLMessage result = null;
        do{
            JsonObject jsonMsg = new JsonObject();
            jsonMsg.add("map", MAP_NAME);
            jsonMsg.add("user", USER);
            jsonMsg.add("password", PASSWORD);
            this.sendMessage(ACLMessage.SUBSCRIBE, jsonMsg.toString(), new AgentID("Bellatrix"));
            try {
                result = this.getMsg();
            } catch (InterruptedException ex) {
                java.util.logging.Logger.getLogger(Bureaucratic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }while(result.getPerformativeInt()!=ACLMessage.INFORM);
        return result;
    }

    public void createAgents(){
        switch(MAP_NAME){
            //case "playground":
            //case "map1":
            //case "map2":
            //case "map3":
            //case "map4":
            default:
                names.add("REGCUE2");
                names.add("REGCUE1");
                names.add("ALCON");
                names.add("REGCUE");
                agents[0] = Agent.Factory.create(names.get(0), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                agents[1] = Agent.Factory.create(names.get(1), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                agents[2] = Agent.Factory.create(names.get(2), Agent.AgentType.HAWK, zombie_count,DEBUG);
                agents[3] = Agent.Factory.create(names.get(3), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                break;     
        }        
        
    }
    
    public void checkInAgents(ACLMessage result){
        int x = 0;
        int y = 0;
        LOGGER.Info("CheckInAgents: bandera");
        for(Agent agent : agents){
            LOGGER.Info("CheckInAgents: %s", agent.getAid().getLocalName());
            Executors.newSingleThreadExecutor().submit(()->agent.execute());
            ACLMessage receiveACLMessage = null;
            result.setReceiver(agent.getAid());
            result.setSender(this.getAid());
            //Send suscribe
            send(result);
            do{
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("x", x);
                jsonObject.add("y", y);
                
                this.sendMessage(ACLMessage.REQUEST, jsonObject.toString(), agent.getAid());
                
                try {
                    receiveACLMessage = getMsg();
                } catch (InterruptedException ex) {
                    LOGGER.Error("PETO EN EL CHECKINAGENTS DE BUREAUCRATIC");
                }
                //TODO cambiar si falla
            }while(receiveACLMessage.getPerformativeInt() != ACLMessage.INFORM);

            
            x=+1;
        }
        
    }
    public void waitStop() throws InterruptedException{
        ArrayList<AgentID> agentes = new ArrayList<>();
        boolean trash;
        int stopCounter = 0;
        JsonObject perceptionObject;
        while(stopCounter < 4){
            trash=false;
            ACLMessage acl_msg = receiveACLMessage();
            if(DEBUG)
                LOGGER.printACLMessage(acl_msg);

            if(acl_msg.getPerformativeInt() == ACLMessage.REQUEST){
                perceptionObject = Json.parse(acl_msg.getContent()).asObject();
                String object = perceptionObject.get("command").asString();

                if(object.equals("stop")){

                    for(int i =0;i<agentes.size();i++){
                        if(acl_msg.getReceiver() == agentes.get(i)){
                            trash=true;
                        }
                    }
                    if(!trash){
                        agentes.add(acl_msg.getReceiver());
                        stopCounter++;
                    }
                }
            }
        }
    }

    @Override
    protected void sendMessage(Integer performative, String content, AgentID receiver){
        ACLMessage outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.addReceiver(receiver);
        outbox.setPerformative(performative);
        outbox.setContent(content);
        if(DEBUG)
            LOGGER.printACLMessage(outbox);
        //super.sendMessage(performative, content, receiver);
        this.send(outbox);
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

    /**
     * <p> Get a message from controller and return the content. </p>
     * @author Guillermo Bueno
     * @author Bruno García Trípoli
     * @throws InterruptedException
     * @return the content of message as String.
     */
    private ACLMessage getMsg() throws InterruptedException{
        ACLMessage acl_msg = receiveACLMessage();

        if(DEBUG)
            LOGGER.printACLMessage(acl_msg);

        return acl_msg;
    }
   
    public String parseSession(String content){
        JsonObject perceptionObject;
        String session = null;
        perceptionObject = Json.parse(content).asObject();
        if(perceptionObject.get("session") != null){
            session = perceptionObject.get("session").asString();
        }

        return session;
    }

    public int[][] parseMap(String content, int dimx, int dimy){
        int[][] map = new int[dimx][dimy];
        JsonObject perceptionObject;
        JsonArray array;
        perceptionObject = Json.parse(content).asObject();

        if(perceptionObject.get("map") != null){
            array = perceptionObject.get("map").asArray();
            int count=0;
            for(int i = 0; i < dimx; i++){
                for(int j = 0; j < dimy; j++){
                    map[i][j] = array.get(count).asInt();
                    count++;
                }
            }
        }

        return map;
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

    private Pair<Integer,Integer> parseDimensions(String content) {
       JsonObject perceptionObject;
       perceptionObject = Json.parse(content).asObject();
       JsonValue dimxValue = perceptionObject.get("dimx");
       JsonValue dimyValue = perceptionObject.get("dimy");
       Pair<Integer,Integer> mapSize = null;
       if(dimxValue != null && dimyValue != null)
            mapSize = new Pair(dimxValue.asInt(),dimyValue.asInt());
       return mapSize;
    }

    @Override
    public void send(ACLMessage result)
    {
        LOGGER.printACLMessage(result);
        super.send(result);
    }

}
