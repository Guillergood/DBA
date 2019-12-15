/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;


import Practica_3.Util.AwacPart;
import Practica_3.Util.Gonio;
import Practica_3.Util.IJsonSerializable;
import Practica_3.Util.Matrix;
import com.sun.javafx.geom.Vec3d;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alberto
 */
public abstract class Agent {
    private static List<String> GroupAgent;
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
    
    protected Agent(AgentType agent_type, float fuel_limit, Vec3d init_pos){
        // Y el id qué???? EH???????
        this.agent_type = agent_type;
        this.FUEL_LIMIT = fuel_limit;
        this.init_pos = init_pos;
    }
    
    protected void performMovement(IJsonSerializable msg) {
        
    }
    
    protected abstract IJsonSerializable chooseMovement();
        
    protected void updatePerception(){
        
    }
    
    protected abstract void fill_map_explored();
    
    public static boolean existAgent(String name){
        
        return false;
    }
    
    protected IJsonSerializable[] AStar(Vec3d start_pos, Vec3d end_pos){
        
    }
    
}



