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
    protected final int MAX_HEIGHT;
    protected final int VISIBILITY;
    protected final int RANGE;
    protected final float FUEL_LIMIT;
    protected final boolean DEBUG;
    private Status agentStatus;
    private AgentID inReplyTo;
    private String conversationId;
    
    // Map parameters
    protected Matrix<Integer> MAP_HEIGHT;
    protected Matrix<Double> map_explored;
    protected Vec3d init_pos;
    
    // Perception parameters
    protected Gonio mini_gonio;
    protected Matrix<Integer> infrared;
    protected Vec3d gps;
    protected double fuel;
    protected ArrayList<AwacPart> awacs;
    protected boolean status;
    protected boolean goal;
    
    // Tourists remaining
    private int to_rescue;

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
     * @param height
     * @param visibility
     * @param range
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

    
    /**
     * @author Guillermo Bueno
     * Updates the agent perception at demand
     */
    protected void updatePerception() {
        ACLMessage outbox = new ACLMessage(); 
        outbox.setSender(this.getAid());
        outbox.setReceiver(new AgentID("Bellatrix"));
        outbox.setPerformative(ACLMessage.getPerformative(ACLMessage.QUERY_REF));
        outbox.setContent("");
        outbox.setReplyTo(inReplyTo);
        outbox.setConversationId(conversationId);
        this.send(outbox);
        
        try {
            getMsg();
        } catch (Exception e) {
            if(DEBUG){
                System.out.println(this.toString()+ " excepcion en el updatePerception");
            }
            e.printStackTrace();
        }
        
    }
    
    private void initializeMap(JsonObject jObject) throws InterruptedException{        
        int dimx = jObject.get("dimx").asInt();
        int dimy = jObject.get("dimy").asInt();
        JsonArray json_array = jObject.get("map").asArray();
        
        MAP_HEIGHT = new Matrix<Integer>(dimx,dimy,Integer.class);
        MAP_HEIGHT.foreach((x,y,v)->json_array.get(x+y*dimx).asInt());
    }
    
    private void getTrace(){
        throw new UnsupportedOperationException("Guille pa ti");
    }
    
    private boolean checkIn(int x,int y) throws InterruptedException{
        JsonObject jsonMsg = new JsonObject();
        jsonMsg.add("command", Command.CHECK_IN.getJsonValue());
        jsonMsg.add("session", session);
        jsonMsg.add("x", x);
        jsonMsg.add("y", y);   
        
        sendMessage(ACLMessage.REQUEST, jsonMsg.toString(), new AgentID("Bellatrix"));
        
        ACLMessage result = receiveACLMessage();
        if( result.getPerformativeInt() != ACLMessage.INFORM)
            return false;
        return true;
    }

    @Override
    protected void execute() {
        super.execute();
        try {         
            //Step 1: Recibe First INFORM from Bureaucratic
            ACLMessage subscribeRetMsg = receiveACLMessage();                        
            JsonObject jObject = Json.parse(subscribeRetMsg.getContent()).asObject();
            session = jObject.get("session").asString();
            conversationId = subscribeRetMsg.getConversationId();
            //Step 1.2: Initialize Map
            initializeMap(jObject);
            
            {//Step 2: Check In
                int x,y;
                do{
                    String msg_bureaucratic_checkIn = getMsg();
                    JsonObject perceptionObject = Json.parse(msg_bureaucratic_checkIn).asObject();
                    x = perceptionObject.get("x").asInt();
                    y = perceptionObject.get("y").asInt();
                }while(checkIn(x,y));
            }
            //Step 3: Loop
            do{
                updatePerception();
                IJsonSerializable command = chooseMovement();
                performMovement(command);                
            }while(to_rescue>0 && !gps.equals(init_pos));
            
            //Step 4: Exit
            performMovement(Command.STOP);
            getTrace();
            
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    private void getMsg() throws InterruptedException{
        ACLMessage acl_msg;
        acl_msg = receiveACLMessage();
        
        if(DEBUG){
            System.out.println(toString() + " recibio -> " 
                    + acl_msg.getPerformative() + " " 
                    + acl_msg.getContent());
        } 
        
        String contenido = acl_msg.getContent();

        switch(acl_msg.getPerformativeInt()){
            // ESTA PARTE ESTA HECHA PARA QUE EL AGENTE RECIBA PETICION DEL
            // BUREAUCRATIC
            case ACLMessage.REQUEST:
                parseFirstMessage(contenido);
                break;
            /* MENSAJES DEL CONTROLADOR, PUEDE SER:
                - PERCEPCION.
                (EL NUMERO DE ELEMENTOS EN EL JSON ES 1: result:{...}).
                - INFORMACION DEL REQUEST. 
                (EL NUMERO DE ELEMENTOS EN EL JSON ES 1: result:{ok}).
                
                - QUE SE NOTIFIQUE ALEMAN DESDE NUESTROS DRONES. 
                (EL NUMERO DE ELEMENTOS EN EL JSON ES 2: result e id).
                
                - TRAZA DE EJECUCIÓN (PREDECIDA POR UN AGREE)
                
            */
            case ACLMessage.INFORM:
                if(acl_msg.getSender().getLocalName().equalsIgnoreCase("Bellatrix")){
                    int numberElements = numberElementsInContentMessage(contenido);
                    
                    if(numberElements > 1){
                        if(numberElements == 2){
                            //GERMAN SAVED
                        }
                        else if (numberElements == 5){
                            //RESPUESTA DEL CHECKIN
                        }
                    }
                    else{
                        if(!emptyContentMessage(contenido)){
                            sensorsParser(contenido);
                        }
                    }
                }
                else{
                    germanFound(contenido);
                }
                
                
                break;
            // SOLO SE RECIBE CUANDO SE HACE UN CANCEL
                //***************************//
            // Y CUANDO EL AGENTE RECIBE LAS COORDENADAS DEL BUREAUCRATIC
            // PARA INDICARLE QUE TODO ESTA CORRECTO.
            case ACLMessage.AGREE:
                break;
            /* MENSAJES DE ERROR:
                
                NOTA: SUELE TENER UN CAMPO DETAILS, CON UNA RAZON DE EL ERROR.
                HAY QUE JUGAR CON EL REPLY-WITH Y EL SENDER
                AgentID sender = acl_msg.getSender();
                String replyWith = acl_msg.getReplyWith();
            */
            case ACLMessage.NOT_UNDERSTOOD:
            case ACLMessage.FAILURE:
            case ACLMessage.REFUSE:
                break;
                          
            default:
                throw new UnsupportedOperationException("PERFORMATIVA NO ACEPTADA: " 
                        + acl_msg.getPerformative());
        }
        
        
   
        
        
         
        
         
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
           // If the position is below the floor
           pos.z < MAP_HEIGHT.get((int)pos.x, (int)pos.y)
        )
            return Double.POSITIVE_INFINITY;
        else
            return 1;
    }
    
    /**
     * Heuristic
     * The linear distance between two points
     * @param start
     * @param end
     * @return 
     */
    private double h(Vec3d pointA, Vec3d pointB) {
        return Math.sqrt(((pointA.x)-(pointB.x))*((pointA.x)-(pointB.x)) +
                         ((pointA.y)-(pointB.y))*((pointA.y)-(pointB.y)) +
                         ((pointA.z)-(pointB.z))*((pointA.z)-(pointB.z)));
    }
    
    
    private void sensorsParser(String source){        
        JsonObject perceptionObject;
        perceptionObject = Json.parse(source).asObject();
        JsonObject object = perceptionObject.get("result").asObject();
        
        //System.out.println("\nperceptionObject: " + perceptionObject);
        //System.out.println("\nobject: " + object);
        
        if(object.get("gps") != null){
            //System.out.println("\nGPS cogido");
            gps.set(object.get("gps").asObject().get("x").asInt(),object.get("gps").asObject().get("y").asInt(),object.get("gps").asObject().get("z").asInt());
            //System.out.println("\nGPS es: "+ gps.toString());
        }
        
        if(object.get("fuel") != null){
            //System.out.println("\nFUEL cogido");
            fuel = object.get("fuel").asDouble();
            //System.out.println("\nFUEL es: "+ fuel);
        }
        
        if(object.get("infrared") != null){
            //System.out.println("\nINFRARED cogido");
            JsonArray arrayInfrared = object.get("infrared").asArray();
            
            /*
                Anotación:
                Infrared siempre da la esquina noreste del dron(el cual no
                tiene orientacion, siempre mira al norte).
                La información se da en filas.
            */
            
            infrared.foreach((x,y,v)-> arrayInfrared.get(x+y*RANGE).asInt());
            
            
            //System.out.println("\nINFRARED es: "+ torescue );
        }
        if(object.get("torescue") != null){
            //System.out.println("\nTORESCUE cogido");
            to_rescue = object.get("torescue").asInt();
            
            //System.out.println("\nTORESCUE es: "+ torescue );
        }
        if(object.get("status") != null){
            //System.out.println("\nSTATUS cogido");
            status = object.get("status").asString();
            
            //System.out.println("\nSTATUS es: "+ status );
        }
        if(object.get("goal") != null){
            goal = object.get("goal").asBoolean();
        }
        if(object.get("energy") != null){
            //System.out.println("\nENERGY cogido");
            energy = object.get("energy").asDouble();
            //System.out.println("\nENERGY es: "+ energy );
        }
        if(object.get("cancel") != null){
            cancel = object.get("cancel").asBoolean();
        }
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
                    continue;
            }            
        }
    }

    public static boolean existAgent(String name){
        
        return false;
    }
    
    // -----------------------
    //ENUMS


   
    
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
    
}
