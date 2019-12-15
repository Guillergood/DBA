/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;


import DBA.SuperAgent;
import Practica_3.Util.AwacPart;
import Practica_3.Util.Gonio;
import Practica_3.Util.IJsonSerializable;
import Practica_3.Util.Logger;
import Practica_3.Util.Matrix;
import com.sun.javafx.geom.Vec3d;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author Alberto
 */
public abstract class Agent extends SuperAgent {
    private String id;
    protected Matrix<Integer> MAP_HEIGHT;
    protected Matrix<Double> map_explored;
    protected HashSet<AgentID> agents;
    private final AgentType agent_type;
    protected final float FUEL_LIMIT;
    private Vec3d init_pos;
    private Gonio mini_gonio;
    private Matrix<Integer> infrared;
    private Vec3d gps;
    private float fuel;
    private ArrayList<AwacPart> awacs;
    private boolean status;
    private boolean goal;
    private int to_rescue;
    private Status AgentStatus;
    protected final Logger LOGGER;
    


    /**
     * Default constructor
     * Initializes an agent with its basic variables
     * @author Juan Ocaña Valenzuela
     * @author Bruno García Trípoli
     * @param id The agent ID
     * @param type The agent type (hawk, sparrow, fly or rescue)
     * @param f_limit The fuel limit of this unit
     * @param init Initial position of the agent
     * @throws java.lang.Exception
     */
    protected Agent(String id, AgentType type, float f_limit) throws Exception{  
        super(new AgentID(id));
        agent_type = type;
        FUEL_LIMIT = f_limit;
        LOGGER = new Logger(this);
    }

    /**
     * Sends the agent movement to the controller
     * @param move The action to perform
     */
    protected void performMovement(IJsonSerializable move) {
        throw new UnsupportedOperationException("Estamos trabajando en ello");
    }

    /**
     * The way the agent goes
     * Defines the new pseudo-objective the agent has to reach
     * @return A position in the map (Vec3d)
     */
    protected abstract Vec3d chooseMovement();
    
    /**
     * Updates the agent perception at demand
     */
    protected void updatePerception() {
        throw new UnsupportedOperationException("Guille pa ti");
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
     */
    public void sendMessage(String recvId, String performative, String content) {
        ACLMessage outbox = new ACLMessage(); 
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Bellatrix"));
        outbox.setPerformative(performative);
        outbox.addReceiver(new AgentID(recvId));
        outbox.setContent(content);
        this.send(outbox);
    }
    
      /**
     * <p> Send a message to the controller. </p>     
     * @author Guillermo Bueno
     * @param recvId is the receiver id
     * @param performative is the variable as aspected
     * @param content is the content of the message
     * @param replyTo is to whom the message was sent
     */
    public void sendMessage(String recvId, String performative, String content, String replyTo) {
        ACLMessage outbox = new ACLMessage(); 
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Bellatrix"));
        outbox.setPerformative(performative);
        outbox.addReceiver(new AgentID(recvId));
        outbox.setInReplyTo(replyTo);
        outbox.setContent(content);
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
     * @throws InterruptedException
     * @return the content of message as String.
     */
    private String getMsg() throws InterruptedException{
         ACLMessage acl_msg = new ACLMessage();
         acl_msg = this.receiveACLMessage();
         /*Performatives performative = null;
         try {
            performative = Performatives.valueOf(acl_msg.getPerformative());
            if(performative == null){
                throw new  Exception("UNHANDLED PERFORMATIVE TRANSLATION");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        AgentID sender = acl_msg.getSender();
        
        if(sender.name.equals("Bellatrix")){
            
        }
        else{
            switch(sender){
            case REQUEST:
                break;
            case QUERY_REF:
                break;
            case INFORM:
                break;
            case FAILURE:
                break;
            case AGREE:
                break;
            case NOT_UNDERSTOOD:
                break;
            default:
                throw new RuntimeException("UNHANDLED PERFORMATIVE");
         }
        }
        
        
        
        
        
         
         /*switch(performative){
            case REQUEST:
                break;
            case QUERY_REF:
                break;
            case INFORM:
                break;
            case FAILURE:
                break;
            case AGREE:
                break;
            case NOT_UNDERSTOOD:
                break;
            default:
                throw new RuntimeException("UNHANDLED PERFORMATIVE");
         }*/
         
        
         
        
         return acl_msg.getContent();
    }
    
    
    /**
     * Finds the shortest path between two points with the Fringe Search algorithm
     * 
     * @param start The start point of the route
     * @param end The finish point
     * @return The plan to follow
     */
    protected ArrayList<IJsonSerializable> search(Vec3d start, Vec3d end) {
        throw new UnsupportedOperationException("Luego lo hago");
    }

    /**
     * @author Bruno García Trípoli
     * 
     * Fill the explored map using the awacs sensor and the type of the 
     * rest of agents
     */
    protected void fill_map_explored(){   
        Double cost = 10.0;
        
        for(AwacPart agent : awacs)
        {            
            Matrix.Operator<Double> operator = (x,y,value)->{                
                return (MAP_HEIGHT.get(x, y)<agent.getY())?cost:value;
            };
            switch(agent.agent_type)
            {
                case FLY:
                    map_explored.ranged_foreach((int)agent.getX(), (int)agent.getZ(), 5,operator);                    
                    break;
                case SPARROW:
                    map_explored.ranged_foreach((int)agent.getX(), (int)agent.getZ(), 11,operator);                    
                    break;
                case HAWK:
                    map_explored.ranged_foreach((int)agent.getX(), (int)agent.getZ(), 41,operator);                    
                    break;
                default:
                    continue;
            }            
        }
    }

    public static boolean existAgent(String name){
        
        return false;
    }
    
    // -----------------------
    //ENUMS
    
    public enum Performatives{
        REQUEST(0, "request"), QUERY_REF(1,"query_ref"), INFORM(2,"inform"), SUBSCRIBE(3,"subscribe"), FAILURE(4,"failure"), AGREE(5,"agree"), NOT_UNDERSTOOD(6,"not_understood");
        
        private int number;
        private String string;

        private Performatives(int number, String string) {
            this.number = number;
            this.string = string;
        }
        public String getName(){
            return string;
        }
        public int getNumber(){
            return number;
        }
        
        
    }
    
    public enum Status{
        IDLE, EXPLORING, EXPLORING_PLACE, GOING_RESCUE, GOING_HOME;
    }
    
    public enum AgentType{
        RESCUE("rescue"), FLY("fly"), SPARROW("sparrow"), HAWK("hawk");

        public final String display_name;
        private static AgentType[] VALUES = values();
        private static final Map<String,AgentType> NAME_LOOKUP = Arrays.stream(VALUES).collect(Collectors.toMap(AgentType::getName, (agentType) -> {
           return agentType;
        }));

        AgentType(String display_name){
            this.display_name = display_name;
        }
               
        public static AgentType parse(String name){
            AgentType type;
            name = name.toLowerCase();

            type = NAME_LOOKUP.get(name);
            if(type == null)
                throw new RuntimeException("Unsupported drone type.");


            return type;
        }

        public String getName(){
            return display_name;
        }
        
        @Override
        public String toString() {
            return "AgentType{" + getName() + '}';
        }

    }

    public static class Factory{
        private static Map<String,Integer> ZOMBIE_MAP = new HashMap<>();
       
        public static Agent create(String name,AgentType type, float fuel_limit){
            String final_name = name;
            if(ZOMBIE_MAP.containsKey(name))
                final_name = name+"_Z"+ZOMBIE_MAP.get(name);
            else
                ZOMBIE_MAP.put(name, 0);
            
            try{                
                switch(type){
                    case FLY:
                        return new FlyAgent(final_name,fuel_limit);
                    case SPARROW:
                        return null;
                    case HAWK:
                        return new HawkAgent(final_name,fuel_limit);
                    case RESCUE:
                        return new RescueAgent(final_name,fuel_limit);
                    default:
                        return null;
                }
            }catch (Exception ex){
                System.err.println(String.format("Agent with aid: \"%s\" already exist. Zombie Count increased, trying again...", final_name));
                int count = ZOMBIE_MAP.get(name);
                ZOMBIE_MAP.put(name, ++count);
                return Factory.create(name,type,fuel_limit);
            }           
        }
    }   
    
}
