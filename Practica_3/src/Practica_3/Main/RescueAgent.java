/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import Practica_3.Util.IJsonSerializable;
import com.sun.javafx.geom.Vec3d;
import java.util.List;

/**
 *
 * @author Alberto
 */
public class RescueAgent extends Agent{
    private static List<Vec3d> targetList;
    private Vec3d bestTarget;
    
    /**
     *
     * @param id
     * @param agent_type
     * @param fuel_limit
     * @param init_pos
     */
    public RescueAgent(int id, AgentType agent_type, float fuel_limit, Vec3d init_pos){
        
    }
    private void updateTargets() {
        
    }
    private void chooseBestTarget(){
        
    }

    @Override
    protected IJsonSerializable chooseMovement() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void fill_map_explored() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
