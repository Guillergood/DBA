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
        IJsonSerializable command = null;
        Vec3d place = null;
        
        // MODE: IDLE
        if(agentStatus == Status.IDLE) {
            if(targetList.isEmpty()) {
                command = Command.STOP;
            }
            else {
                // Go rescue someone
                chooseBestTarget();
                plan = search(gps, bestTarget);
                agentStatus = Status.GOING_RESCUE; 
            }
        }
        
        // MODE: GOING_RESCUE
        if(agentStatus == Status.GOING_RESCUE) {
            // If fuel warning, change mode to refuel
            if(fuel <= (2 * MAP_HEIGHT.get((int)place.x, (int)place.y) + 2)) {
                agentStatus = Status.REFUEL;
            }
            // If global fuel warning, go home
            else if(fuelRemaining <= 1.5 * h(gps, init_pos)) {
                plan = search(gps,init_pos);
                agentStatus = Status.GOING_HOME;
            }
            // Else, move to the next cell in the plan
            else {
                command = move(place);
            }
        }
        // MODE: GOING_HOME
        else if(agentStatus == Status.GOING_HOME) {
            if(gps != init_pos) {
                // Come back home directly
                command = move(place);    
            }
            else
                agentStatus = Status.IDLE;
        }
        
        return command;
    }
    
        /**
     * Calculates the next command to reach an adjacent position
     * @author Juan Ocaña Valenzuela
     * @param pos
     * @return 
     */
    private Command.Direction move(Vec3d pos) {
        Command.Direction dir = null;
        
        // MoveN
        if(gps.x == pos.x && gps.y > pos.y && gps.z == pos.z)
            dir = Command.Direction.NORTH;
        // MoveNE
        else if(gps.x < pos.x && gps.y > pos.y && gps.z == pos.z)
            dir = Command.Direction.NORTHEAST;
        // MoveE
        else if(gps.x < pos.x && gps.y == pos.y && gps.z == pos.z)
            dir = Command.Direction.EAST;
        // MoveSE
        else if(gps.x < pos.x && gps.y < pos.y && gps.z == pos.z)
            dir = Command.Direction.SOUTHEAST;
        // MoveS
        else if(gps.x == pos.x && gps.y < pos.y && gps.z == pos.z)
            dir = Command.Direction.SOUTH;
        // MoveSW
        else if(gps.x > pos.x && gps.y < pos.y && gps.z == pos.z)
            dir = Command.Direction.SOUTHWEST;
        // MoveW
        else if(gps.x > pos.x && gps.y == pos.y && gps.z == pos.z)
            dir = Command.Direction.WEST;
        // MoveNW
        else if(gps.x > pos.x && gps.y > pos.y && gps.z == pos.z)
            dir = Command.Direction.NORTHWEST;
        // MoveUP
        else if(gps.x == pos.x && gps.y == pos.y && gps.z < pos.z)
            dir = Command.Direction.UP;
        // MoveDW
        else if(gps.x == pos.x && gps.y == pos.y && gps.z > pos.z)
            dir = Command.Direction.DOWN;
        
        return dir;
    }
    
    private void updateTargets() {

    }
    private void chooseBestTarget(){

    }

}
