/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import Practica_3.Util.Command;
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
    
    public RescueAgent(String id, float fuel_limit, Vec3d init_pos) throws Exception {
        super(id, AgentType.FLY, fuel_limit, init_pos);
    }
    
    /**
     * The way the agent works
     * @return The next action to perform
     */
    @Override
    protected IJsonSerializable chooseMovement() {
        IJsonSerializable move = Command.Direction.UP;
        
        // Ahora lo relleno
        return move;
    }
    
    private void updateTargets() {
        
    }
    private void chooseBestTarget(){
        
    }
}
