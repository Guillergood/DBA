/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;


import Practica_3.Util.AwacPart;
import Practica_3.Util.Gonio;
import Practica_3.Util.Matrix;
import com.sun.javafx.geom.Vec3d;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alberto
 */
public abstract class Agent {
    private List<String> GroupAgent;
    protected final Matrix<Integer> MAP_HEIGHT;
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
        private static final Map<String,AgentType> NAME_LOOKUP;
        
        AgentType(String display_name, String json_value){
            this.display_name = display_name;
            this.json_value = json_value;
            
        }
        public static AgentType parse(String name){
            
        }
        public String toString(){
            
        }
    }
    public enum Status{
        IDLE, EXPLORING, EXPLORING_PLACE, GOING_RESCUE, GOING_HOME;
    }
}



