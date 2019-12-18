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
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.stream.Stream;
import javafx.util.Pair;

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
        ACLMessage result = suscribe();
        createAgents();
        sendSubscribe(result);
        checkInAgents();
        waitStop();
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
            case "playground":
                names.add("REGCUE2");
                names.add("REGCUE1");
                names.add("ALCON");
                names.add("REGCUE");
                agents[0] = Agent.Factory.create(names.get(0), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                agents[1] = Agent.Factory.create(names.get(1), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                agents[2] = Agent.Factory.create(names.get(2), Agent.AgentType.HAWK, zombie_count,DEBUG);
                agents[3] = Agent.Factory.create(names.get(3), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                break;
            case "map1":
                names.add("REGCUE2");
                names.add("REGCUE1");
                names.add("ALCON");
                names.add("REGCUE");
                agents[0] = Agent.Factory.create(names.get(0), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                agents[1] = Agent.Factory.create(names.get(1), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                agents[2] = Agent.Factory.create(names.get(2), Agent.AgentType.HAWK, zombie_count,DEBUG);
                agents[3] = Agent.Factory.create(names.get(3), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                break;                
            case "map2":
                names.add("MOGCA");
                names.add("MOGCA_2");
                names.add("ALCON");
                names.add("REGCUE");
                agents[0] = Agent.Factory.create(names.get(0), Agent.AgentType.FLY, zombie_count,DEBUG);
                agents[1] = Agent.Factory.create(names.get(1), Agent.AgentType.FLY, zombie_count,DEBUG);
                agents[2] = Agent.Factory.create(names.get(2), Agent.AgentType.HAWK, zombie_count,DEBUG);
                agents[3] = Agent.Factory.create(names.get(3), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                break;
            case "map3":
                names.add("MOGCA");
                names.add("MOGCA_2");
                names.add("ALCON");
                names.add("REGCUE");
                agents[0] = Agent.Factory.create(names.get(0), Agent.AgentType.FLY, zombie_count,DEBUG);
                agents[1] = Agent.Factory.create(names.get(1), Agent.AgentType.FLY, zombie_count,DEBUG);
                agents[2] = Agent.Factory.create(names.get(2), Agent.AgentType.HAWK, zombie_count,DEBUG);
                agents[3] = Agent.Factory.create(names.get(3), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                break;
            case "map4":
                names.add("MOGCA");
                names.add("MOGCA_2");
                names.add("ALCON");
                names.add("REGCUE");
                agents[0] = Agent.Factory.create(names.get(0), Agent.AgentType.FLY, zombie_count,DEBUG);
                agents[1] = Agent.Factory.create(names.get(1), Agent.AgentType.FLY, zombie_count,DEBUG);
                agents[2] = Agent.Factory.create(names.get(2), Agent.AgentType.HAWK, zombie_count,DEBUG);
                agents[3] = Agent.Factory.create(names.get(3), Agent.AgentType.RESCUE, zombie_count,DEBUG);
                break;             
        }
    }
    
    public void checkInAgents(){
        int x = 0;
        int y = 0;
        
        for(int i = 0; i < 4; ++i){
            JsonObject jsonObject = new JsonObject();
            ACLMessage acl_msg = new ACLMessage();
            acl_msg.addReceiver(agents[i].getAid());
            jsonObject.add("x", x);
            jsonObject.add("y", y);
            acl_msg.setContent(jsonObject.toString());
            
            ACLMessage recieveACLMessage = null;
            try {
                recieveACLMessage = receiveACLMessage();
            } catch (InterruptedException ex) {
                LOGGER.Error("PETO EN EL CHECKINAGENTS DE BUREAUCRATIC");
            }
            
            
            
            
            // CAMBIAR ESTO
            
            if(recieveACLMessage != null && recieveACLMessage.getPerformativeInt() != ACLMessage.AGREE){
                i--;
            }
           
            x=+1;
        }
          
        
    }
    public void waitStop(){
        
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
    
    private void login() throws InterruptedException{
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
                String session = parseSession(content);
                Pair<Integer,Integer> dims = parseDimensions(content);
                int[][] fullMap;
                fullMap = parseMap(content, dims.getKey(), dims.getValue());
                
                
                jsonMsg = new JsonObject();
                jsonMsg.add("session", session);
                jsonMsg.add("dimx", dims.getKey());
                jsonMsg.add("dimy", dims.getValue());
                ACLMessage spreadInformation = new ACLMessage(ACLMessage.INFORM);
                spreadInformation.setContent(jsonMsg.toString());
                spreadInformation.setSender(this.getAid());
                spreadInformation.setReceiver(new AgentID("Bellatrix"));
                spreadInformation.addReceiver(new AgentID(names.get(0)));
                spreadInformation.addReceiver(new AgentID(names.get(1)));
                spreadInformation.addReceiver(new AgentID(names.get(2)));
                spreadInformation.addReceiver(new AgentID(names.get(3)));
                spreadInformation.setConversationId(checkin.getConversationId());
                send(spreadInformation);

            }
            

         

        }

        


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

    private void sendSubscribe(ACLMessage result) {
        for(Agent agent: agents){
            result.addReceiver(agent.getAid());
        }
        send(result);
    }
    
}
