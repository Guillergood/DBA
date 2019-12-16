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
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
import java.util.logging.Level;
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
    private ArrayList<String> names;

    private Bureaucratic(String name) throws Exception{
        super(new AgentID(name));
        LOGGER = new Logger(this);
        names = new ArrayList<>();
        names.add("MOGCA");
        names.add("MOGCA_2");
        names.add("ALCON");
        names.add("REGCUE");
        Agent.Factory.create(names.get(0), Agent.AgentType.FLY, zombie_count,DEBUG);
        Agent.Factory.create(names.get(1), Agent.AgentType.FLY, zombie_count,DEBUG);
        Agent.Factory.create(names.get(2), Agent.AgentType.HAWK, zombie_count,DEBUG);
        Agent.Factory.create(names.get(3), Agent.AgentType.RESCUE, zombie_count,DEBUG);
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
}
