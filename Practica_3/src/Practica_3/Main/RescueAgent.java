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
    private final int MAX_CAPACITY;
    private int capacity;

    public RescueAgent(String id, float fuel_limit, boolean debug) throws Exception {
        super(id, AgentType.FLY, fuel_limit, 255, 1, 1, debug);
        MAX_CAPACITY = capacity = 10;
    }
    
    /**
     * The way the agent goes
     * @return The next action to perform
     */
    @Override
    protected IJsonSerializable chooseMovement() {
        
        
        throw new UnsupportedOperationException("Paluego");
    }
    
    private void updateTargets() {

    }
    private void chooseBestTarget(){

    }

}
