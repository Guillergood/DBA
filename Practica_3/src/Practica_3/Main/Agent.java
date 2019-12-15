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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alberto
 */
public abstract class Agent extends SuperAgent {
    private List<String> GroupAgent;
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
     * The way the agent works
     * @return An IJsonSerializable action to perform
     */
    protected abstract IJsonSerializable chooseMovement();
    
    public enum AgentType{
        RESCUE, FLY, SPARROW, HAWK;
        
        public final String display_name;
        public final String json_value;
        private static final HashMap<String,AgentType> NAME_LOOKUP = new HashMap<String, AgentType>();
        
        AgentType(String display_name, String json_value){
            this.display_name = display_name;
            this.json_value = json_value;
            initializeTableLookup();
        }
        
        private static void initializeTableLookup(){
            NAME_LOOKUP.put("rescue", RESCUE);
            NAME_LOOKUP.put("fly", FLY);
            NAME_LOOKUP.put("sparrow", SPARROW);
            NAME_LOOKUP.put("hawk", HAWK);
        }
        
        public static AgentType parse(String name){
            AgentType type;
            name = name.toLowerCase();
           
            type = NAME_LOOKUP.get(name);
            if(type == null)
                throw new RuntimeException("Unsupported drone type.");
            
            
            return type;
        }

        @Override
        public String toString() {
            return "AgentType{" + "display_name=" + display_name + ", json_value=" + json_value + '}';
        }
        
    }
    public enum Status{
        IDLE, EXPLORING, EXPLORING_PLACE, GOING_RESCUE, GOING_HOME;
    }
}



