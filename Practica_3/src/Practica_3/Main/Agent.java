/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;


import DBA.SuperAgent;
import Practica_3.Util.AwacPart;
import Practica_3.Util.Command;
import Practica_3.Util.Gonio;
import Practica_3.Util.IJsonSerializable;
import Practica_3.Util.Logger;
import Practica_3.Util.Matrix;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sun.javafx.geom.Vec3d;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javafx.util.Pair;

/**
 *
 * @author Alberto
 */
public abstract class Agent extends SuperAgent {
    
    // Agent parameters
    private String id;
    private String session;
    private final AgentType agent_type;
    protected int MAX_HEIGHT;
    protected int VISIBILITY;
    protected int RANGE;
    protected final float FUEL_LIMIT;
    protected float fuelRemaining;
    protected final boolean DEBUG;
    protected Status agentStatus;
    private AgentID inReplyTo;
    private String conversationId;
    private AgentID bureaucraticID;

    // Map parameters
    protected Matrix<Integer> MAP_HEIGHT;
    protected Matrix<Double> map_explored;
    protected Vec3d init_pos;
    protected MapPiece piece;
    
    // Perception parameters
    protected Gonio mini_gonio;
    protected Matrix<Integer> infrared;
    protected Vec3d gps;
    protected double fuel;
    protected ArrayList<AwacPart> awacs;
    protected boolean status;
    protected boolean goal;
    protected double energy;
    protected boolean cancel;
    
    // Tourists remaining
    private int to_rescue;
    
    // Plan
    protected Stack<Vec3d> plan;

    // Logger
    protected final Logger LOGGER;

    /**
     * Default constructor
     * Initializes an agent with its basic variables
     * @author Juan Ocaña Valenzuela
     * @author Bruno García Trípoli
     * @param id The agent ID
     * @param type The agent type (hawk, sparrow, fly or rescue)
     * @param f_limit The fuel limit of this unit
     * @param height The max height
     * @param visibility The visibility of mini gonio sensor
     * @param range The range of infrared sensor
     * @param debug
     * @throws java.lang.Exception
     */
    protected Agent(String id, AgentType type, float f_limit, int height, int visibility, int range, boolean debug) throws Exception{  
        super(new AgentID(id));
        agent_type = type;
        FUEL_LIMIT = f_limit;
        LOGGER = new Logger(this);
        MAX_HEIGHT = height;
        VISIBILITY = visibility;
        RANGE = range;
        DEBUG = debug;
        infrared = new Matrix<>(RANGE,RANGE,Integer.class);
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
     * Decides the next action to perform, whether it is following
     * the current plan, refueling or making a new plan and start 
     * following it
     * @return An IJsonSerializable
     */
    protected abstract IJsonSerializable chooseMovement();
    
    /**
     * Calculates a plan to refuel and go back where the agent was just before
     */
    protected void refuel() {
        // Greedy para repostar, entrar en modo refuel
    }
    
    /**
     * When the agent is low on total fuel, goes home
     */
    protected void goHome() {
        // search hacia el punto de partida
    }

    protected void getAlemanPerception(ACLMessage msg){
        throw new UnsupportedOperationException("Overload this on Rescue");
    }
    
    /**
     * @author Guillermo Bueno
     * @author Bruno García Trípoli
     * Updates the agent perception at demand
     */
    protected void updatePerception() {  
        AgentID BellatrixID = new AgentID("Bellatrix");
        this.sendMessage(ACLMessage.QUERY_REF, "", BellatrixID);
        
         try { 
            ACLMessage result= getMsg();
            if(result.getSender()!=BellatrixID)
                getAlemanPerception(result);
            else if(result.getPerformativeInt() == ACLMessage.INFORM)
                sensorsParser(result.getContent());
            else{
                LOGGER.Error("Mensaje inesperado:");
                LOGGER.printACLMessage(result);
            }
               
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initializeMap(JsonObject jObject) throws InterruptedException{        
        int dimx = jObject.get("dimx").asInt();
        int dimy = jObject.get("dimy").asInt();
        JsonArray json_array = jObject.get("map").asArray();
        
        MAP_HEIGHT = new Matrix<>(dimx,dimy,Integer.class);
        //MAP_HEIGHT.foreach((x,y,v)->json_array.get(x+y*dimx).asInt());
        int z = 0;
        
        for(int k = 0; k < dimy && z < json_array.size(); ++k)
            for(int i = 0; i < dimx && z < json_array.size(); ++i, ++z)
                MAP_HEIGHT.set(i, k, json_array.get(z).asInt());
        
            
        
    }
    
    private boolean getTrace() throws FileNotFoundException, IOException{
        try {
            //System.out.println("Recibiendo traza");
            ACLMessage newIn = this.receiveACLMessage();
            JsonObject injson = Json.parse(newIn.getContent()).asObject();


            if(injson.get("result").asString().equals("ok")){
                ACLMessage finalMsg = this.receiveACLMessage();
                JsonObject finalJson = Json.parse(finalMsg.getContent()).asObject();
                JsonArray ja = finalJson.get("trace").asArray();

                byte data[] = new byte [ja.size()];
                for( int i= 0; i<data.length; i++){
                    data[i] = (byte) ja.get(i).asInt();
                }
                FileOutputStream fos = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
            }
            System.out.println(injson);
            System.out.println("Traza guardada");
            return true;
        } catch (InterruptedException | IOException ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    /**
     * TODO
     * @param x
     * @param y
     * @return
     * @throws InterruptedException 
     */
    private boolean checkIn(int x,int y) throws InterruptedException{
        JsonObject jsonMsg = new JsonObject();
        jsonMsg.add("command", Command.CHECK_IN.getJsonValue());
        jsonMsg.add("session", session);
        jsonMsg.add("rol", "rescue");
        jsonMsg.add("x", x);
        jsonMsg.add("y", y);   
        
        sendMessage(ACLMessage.REQUEST, jsonMsg.toString(), new AgentID("Bellatrix"),conversationId);
        
        ACLMessage result = receiveACLMessage();        
        sendMessage(result.getPerformativeInt(),result.getContent(),bureaucraticID,conversationId);          
        return result.getPerformativeInt() == ACLMessage.INFORM;
    }

    @Override
    protected void execute() {
        super.execute();
        try {         
            //Step 1: Recibe First INFORM from Bureaucratic
            ACLMessage subscribeRetMsg = getMsg();                       
            JsonObject jObject = Json.parse(subscribeRetMsg.getContent()).asObject();
            session = jObject.get("session").asString();
            conversationId = subscribeRetMsg.getConversationId();
            bureaucraticID = subscribeRetMsg.getSender();
            //Step 1.2: Initialize Map
            initializeMap(jObject);
            
            //Step 2: Check In
             /**
             * Check In Method:
             * 1: send msg {command=checkin}
             * 2: get result
             * 3: send result to Bureaucratic
             * 4: return if result is valid. If not Bureaucratic will send a msg with new coords.
             */
            int x,y;
            do{
                String msg_bureaucratic_checkIn = getMsg().getContent();
                JsonObject perceptionObject = Json.parse(msg_bureaucratic_checkIn).asObject();
                x = perceptionObject.get("x").asInt();
                y = perceptionObject.get("y").asInt();
            }while(checkIn(x,y));
            
            //Step 3: Loop
            do{
                updatePerception();
                IJsonSerializable command = chooseMovement();
                performMovement(command);                
            }while(to_rescue>0 && !gps.equals(init_pos));
            
            //Step 4: Exit
            performMovement(Command.STOP);
            try {
                getTrace();
            } catch (IOException ex) {
                java.util.logging.Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    protected void sendMessage(Integer performative, String content, AgentID receiver,String convID){
        ACLMessage outbox = new ACLMessage();
        outbox.setSender(this.getAid());
        outbox.addReceiver(receiver);
        outbox.setPerformative(performative);
        outbox.setContent(content);
        outbox.setConversationId(convID);
        if(DEBUG)
            LOGGER.printACLMessage(outbox);
        //super.sendMessage(performative, content, receiver);
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
    
    private void parseFirstMessage(String source) {
        JsonObject object;
        object = Json.parse(source).asObject();      

        // Map piece
        piece = MapPiece.valueOf(object.get("mapPiece").asString());
        
        // Map dimensions
        // Hay que poner esto en algún lado
        
        // Start point 
        
    }
    
    
    /**
     * Checks if the message sent has empty content
     * 
     * @param contenido Json to check
     * @return Number of elements contained by the json.
     */
    private int numberElementsInContentMessage(String contenido) {
        
        int countJson = 0;
        JsonArray jsonArray = Json.parse(contenido).asArray();
        
        
        for(JsonValue algo:jsonArray){
            countJson++;
        }
        
        
        return countJson;
    }
    
    /**
     * Checks if the message sent has empty content
     * 
     * @param contenido Json to check
     * @return If has empty content or not.
     */
    private boolean emptyContentMessage(String contenido) {
        
        JsonObject perceptionObject;
        perceptionObject = Json.parse(contenido).asObject();
        String object = perceptionObject.get("result").asString();
        
        
        
        
        return object.equalsIgnoreCase("ok");
    }
    
    /**
     * Finds the shortest path between two points with the Fringe Search algorithm
     * 
     * @author Juan Ocaña Valenzuela
     * @param start The start point of the route
     * @param end The finish point
     * @return The path to follow
     */
    protected Stack<Vec3d> search(Vec3d start, Vec3d end) {
        // The list of exploring/explored nodes
        LinkedList<Vec3d> nodes = new LinkedList();
        // The cached cost of each node
        HashMap<Vec3d, Pair<Integer, Vec3d>> cache = new HashMap();
        // The current fringe value
        double flimit = h(start, end);
        // The current status of the search
        boolean found = false;
        
        // Introduce the start node into nodes and put it into the cache
        nodes.add(start);
        cache.put(start, new Pair(0, null));
        
        
        while(!nodes.isEmpty() && !found) {
            Double fmin = Double.POSITIVE_INFINITY;
            // Iterate the list until it reaches the end
            ListIterator it = nodes.listIterator();
            
            while(it.hasNext()) {
                Vec3d current = (Vec3d)it.next();
                
                // Check if the current node is evaluable or is the goal
                // G and F values
                double g = cache.get(current).getKey();
                double f = g + h(current, end);
                
                if(f > flimit) {
                    fmin = Math.min(f, fmin);
                    continue;
                }
                
                if(current.equals(end)) {
                    found = true;
                    break;
                }
                
                // If it's good, we expand it
                for(int i = -1; i <= 1; ++i) {
                    for(int j = -1; j <= 1; ++j) {
                        // The child
                        Vec3d child = new Vec3d(current.x + i, current.y + j, current.z);
                        
                        // If we are checking the current one, continue
                        if(child.equals(current))
                            continue;
                        
                        // Cost of the child
                        double g_child = g + cost(child);
                        
                        // If g is cached we check it
                        if(cache.get(child) != null) {
                            double g_cached = cache.get(child).getKey();
                            // If previous paths were better, we don't care
                            // about this one
                            if(g_cached <= g_child)
                                continue;
                        }
                        
                        // If child is already in the list, we remove it
                        nodes.remove(child);
                        
                        // Insert the child in the list and updates the cache
                        nodes.add(child);
                        cache.put(child, new Pair(g_child, current));
                    }
                }
                
                // Remove this node from the list
                nodes.remove(current);
            }
            
            // Update the fringe
            flimit = fmin;
        }
        
        // Once finished, returns the path to follow
        Stack path = new Stack();  
        reversePath(end, cache, path);
        return path;
        
    }

    /**
     * Recursively creates an ordered path given the end node and its
     * correspondant parent's hash map
     * @author Juan Ocaña Valenzuela
     * @param node the end node
     * @param cache the hash map which contains the tree relationship
     * @param plan A reference to a stack
     */
    private void reversePath(Vec3d node, HashMap<Vec3d, Pair<Integer, Vec3d>> cache, Stack<Vec3d> plan) {
        Vec3d parent = cache.get(node).getValue();
        if(parent != null) {
            plan.push(node);
            reversePath(parent, cache, plan);
        }
    }

    /**
     * Cost of a move
     * @return 1 if the position is viable, INFINITY if not
     */
    private double cost(Vec3d pos) {
        if
        (
           // If the position is over the map limits
           pos.x < 0 || pos.x > map_explored.getColsNum() ||
           pos.y < 0 || pos.y > map_explored.getColsNum() ||
           pos.z > MAX_HEIGHT ||
           // If the position is below the floor
           pos.z < MAP_HEIGHT.get((int)pos.x, (int)pos.y)
        )
            return Double.POSITIVE_INFINITY;
        else
            return (map_explored.get((int)pos.x, (int)pos.y) + 1);
    }
    
    /**
     * Heuristic
     * The linear distance between two points
     * @param start
     * @param end
     * @return 
     */
    protected double h(Vec3d pointA, Vec3d pointB) {
        return Math.sqrt(((pointA.x)-(pointB.x))*((pointA.x)-(pointB.x)) +
                         ((pointA.y)-(pointB.y))*((pointA.y)-(pointB.y)) +
                         ((pointA.z)-(pointB.z))*((pointA.z)-(pointB.z)));
    }
    
    private void sensorsParser(String source){        
        JsonObject perceptionObject;
        perceptionObject = Json.parse(source).asObject();
        JsonObject object = perceptionObject.get("result").asObject();
        
        if(object.get("gps") != null)           
            gps.set(object.get("gps").asObject().get("x").asInt(),object.get("gps").asObject().get("y").asInt(),object.get("gps").asObject().get("z").asInt());                    
        
        if(object.get("fuel") != null)            
            fuel = object.get("fuel").asDouble();
        
        if(object.get("infrared") != null){
            JsonArray arrayInfrared = object.get("infrared").asArray();  
            
            /*
                Anotación:
                Infrared siempre da la esquina noreste del dron(el cual no
                tiene orientacion, siempre mira al norte).
                La información se da en filas.
            */            
            infrared.foreach((x,y,v)-> arrayInfrared.get(x+y*RANGE).asInt());
        }
        if(object.get("torescue") != null)
            to_rescue = object.get("torescue").asInt();
        
        if(object.get("status") != null)
            status = object.get("status").asString().equals("operative");
            
        if(object.get("goal") != null)
            goal = object.get("goal").asBoolean();
        
        
        if(object.get("energy") != null)
            energy = object.get("energy").asDouble();
        
        if(object.get("cancel") != null)
            cancel = object.get("cancel").asBoolean();        
        
        if(object.get("gonio") != null ){
            JsonObject gonioObject = object.get("gonio").asObject();
            Float key = gonioObject.get("distance").asFloat();
            Float value = gonioObject.get("angle").asFloat();
            mini_gonio = new Gonio(key,value);
        } 
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
            }            
        }
    }

    public static boolean existAgent(String name){
        
        return false;
    }
    
    // -----------------------
    //ENUMS

    public enum Status{
        IDLE, EXPLORING, REFUEL, GOING_RESCUE, GOING_HOME;
    }
    
    public enum MapPiece {
        UPPER, LOWER, FULL;
    }
    
    public enum Bounce {
        UP, RIGHT, DOWN, LEFT;
    }
    
    public enum AgentType{
        RESCUE("rescue"), FLY("fly"), SPARROW("sparrow"), HAWK("hawk");

        public final String display_name;
        private static final AgentType[] VALUES = values();
        private static final Map<String,AgentType> NAME_LOOKUP = Arrays.stream(VALUES).collect(Collectors.toMap(AgentType::getName, (agentType) -> agentType));

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
       
        public static Agent create(String name,AgentType type, float fuel_limit, boolean debug){
            String final_name = name;
            if(ZOMBIE_MAP.containsKey(name))
                final_name = name+"_Z"+ZOMBIE_MAP.get(name);
            else
                ZOMBIE_MAP.put(name, 0);
            
            try{                
                switch(type){
                    case FLY:
                        return new FlyAgent(final_name,fuel_limit,debug);
                    case SPARROW:
                        return null;
                    case HAWK:
                        return new HawkAgent(final_name,fuel_limit,debug);
                    case RESCUE:
                        return new RescueAgent(final_name,fuel_limit,debug);
                    default:
                        return null;
                }
            }catch (Exception ex){
                System.err.println(String.format("Agent with aid: \"%s\" already exist. Zombie Count increased, trying again...", final_name));
                int count = ZOMBIE_MAP.get(name);
                ZOMBIE_MAP.put(name, ++count);
                return Factory.create(name,type,fuel_limit,debug);
            }           
        }
    }   
    
    
    
    /*
    /**
     * <p> Send a message to the controller. </p>     
     * @author Guillermo Bueno
     * @param recvId is the receiver id
     * @param performative is the variable as aspected
     * @param content is the content of the message
     * @param replyTo is to whom the message was sent
     * @param convID is the conversation ID
     *//*
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
        if(DEBUG)
            LOGGER.printACLMessage(outbox);
        this.send(outbox);
    }
    /**
   * <p> Send a message to the controller. </p>     
   * @author Guillermo Bueno
   * @param recvId is the receiver id
   * @param performative is the variable as aspected
   * @param content is the content of the message
   *//*
  public void sendMessage(String recvId, String performative, String content) {
      ACLMessage outbox = new ACLMessage(); 
      outbox.setSender(this.getAid());
      outbox.setReceiver(new AgentID("Bellatrix"));
      outbox.setPerformative(performative);
      outbox.addReceiver(new AgentID(recvId));
      outbox.setContent(content);
      if(DEBUG)
          LOGGER.printACLMessage(outbox);
      this.send(outbox);
  }
    */
    
}
