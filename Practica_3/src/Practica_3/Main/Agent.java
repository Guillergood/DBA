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
import Practica_3.Util.Matrix;
import com.sun.javafx.geom.Vec3d;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final AgentType agent_type;
    protected final float FUEL_LIMIT;
    private final Vec3d init_pos;
    private Gonio mini_gonio;
    private Matrix<Integer> infrared;
    private Vec3d gps;
    private float fuel;
    private ArrayList<AwacPart> awacs;
    private boolean status;
    private boolean goal;
    private int to_rescue;
    private Status AgentStatus;


    /**
     * Default constructor
     * Initializes an agent with its basic variables
     * @author Juan Ocaña Valenzuela
     * @param id The agent ID
     * @param type The agent type (hawk, sparrow, fly or rescue)
     * @param f_limit The fuel limit of this unit
     * @param init Initial position of the agent
     * @throws java.lang.Exception
     */
    protected Agent(String id, AgentType type, float f_limit, Vec3d init) throws Exception {        
        super(new AgentID(id));
        agent_type = type;
        FUEL_LIMIT = f_limit;
        init_pos = init;
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

}
