/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import Practica_3.Util.Command;
import Practica_3.Util.IJsonSerializable;
import com.sun.javafx.geom.Vec3d;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Alberto
 */
public class FlyAgent extends Agent {
    
    // Bounce values
    Random rand = new Random();
    private Bounce bounceDir;
    
    public FlyAgent(String id, float fuel_limit, boolean debug) throws Exception {
        super(id, AgentType.FLY, fuel_limit, 255, 20, 5,debug);
    }
    
    /**
     * The way the agent goes
     * @return The next action to perform
     */
    @Override
    protected IJsonSerializable chooseMovement() {
    /*
        Bounce es un enum que he creado en Agent, que representa los límites de la 
        zona de exploración. En este caso simbolizaría esto:

                
              --
              - -
        LEFT  -   -  RIGHT
              ------
               DOWN

        ***** EL ALGORITMO *****
        Para simplificar la forma de rebotar, se elige de entre los límites de
        exploración un borde aleatorio. Se elige una casilla aleatoria perteneciente
        al mismo, y se realiza un plan hacia dicha casilla. Si no es posible, se vuelve
        a generar un aleatorio.

        Una vez finalizado el plan, se elige una casilla del siguiente borde en sentido
        horario.
    */
        
        //Move to perform:
        Vec3d place = null;
        IJsonSerializable command = null;
        
        if(!plan.isEmpty()) {
            // Pop the cell to reach
            place = plan.pop();
            
            // MODE: EXPLORING
            if(agentStatus == Status.EXPLORING) {
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
            // MODE: IDLE
            else if(agentStatus == Status.IDLE) {
                command = Command.STOP;
            }
        }
        // Replan
        else {
            // If the hawk is not at max height, plan to ascend
            if(gps.z != MAX_HEIGHT) {
                plan = search(gps, new Vec3d(gps.x, gps.y, MAX_HEIGHT));
            }
            // If it is already at max height, plan to bounce
            else {
                plan = search(gps, bounce());
            }
            
            // Go explore
            agentStatus = Status.EXPLORING;
        }

        return command;
    }
    
    /**
     * Picks the next direction to move, "bouncing" in the exploration area
     * @author Juan Ocaña Valenzuela
     * @return the new objective
     */
    private Vec3d bounce() {
        // Change bounce value in clockwise order and choose a new objective
        Vec3d obj = null;
        double x;
        double y;
        
        switch(piece) {
            case LOWER:
                switch(bounceDir) {
                    case RIGHT:
                        bounceDir = Bounce.DOWN;
                        x = rand.nextInt(MAP_HEIGHT.getColsNum() - 2*RANGE) + RANGE;
                        y = MAP_HEIGHT.getColsNum() - RANGE;
                        obj = new Vec3d(x, y, MAP_HEIGHT.get((int)x, (int)y));
                        break;
                    case DOWN:
                        bounceDir = Bounce.LEFT;
                        x = RANGE;
                        y = rand.nextInt(MAP_HEIGHT.getRowsNum() - 2*RANGE) + RANGE;
                        obj = new Vec3d(x, y, MAP_HEIGHT.get((int)x, (int)y));
                        break;
                    case LEFT:
                        bounceDir = Bounce.RIGHT;
                        x = rand.nextInt(MAP_HEIGHT.getColsNum());
                        obj = new Vec3d(x, x,  MAP_HEIGHT.get((int)x,(int)x));
                        break;
                }
                break;
            case UPPER:
                switch(bounceDir) {
                    case LEFT:
                        bounceDir = Bounce.UP;
                        x = rand.nextInt(MAP_HEIGHT.getRowsNum() - 2*RANGE) + RANGE;
                        y = RANGE;
                        obj = new Vec3d(x, y, MAP_HEIGHT.get((int)x, (int)y));
                        break;
                    case RIGHT:
                        bounceDir = Bounce.LEFT;
                        x = rand.nextInt(MAP_HEIGHT.getColsNum());
                        obj = new Vec3d(x, x,  MAP_HEIGHT.get((int)x,(int)x));
                        break;
                    case UP:
                        bounceDir = Bounce.RIGHT;
                        x = MAP_HEIGHT.getRowsNum() - RANGE;
                        y = rand.nextInt(MAP_HEIGHT.getRowsNum() - 2*RANGE) + RANGE;
                        obj = new Vec3d(x, y, MAP_HEIGHT.get((int)x, (int)y));
                        break;
                }
                break;
                
            default:
                switch(bounceDir) {
                    case UP:
                        bounceDir = Bounce.RIGHT;
                        obj = new Vec3d(MAP_HEIGHT.getRowsNum() - RANGE, rand.nextInt(MAP_HEIGHT.getRowsNum() - 2*RANGE) + RANGE, gps.z);
                        break;
                    case RIGHT:
                        bounceDir = Bounce.DOWN;
                        obj = new Vec3d(rand.nextInt(MAP_HEIGHT.getColsNum() - 2*RANGE) + RANGE, MAP_HEIGHT.getColsNum() - RANGE,  gps.z);
                        break;
                    case DOWN:
                        bounceDir = Bounce.LEFT;
                        obj = new Vec3d(RANGE, rand.nextInt(MAP_HEIGHT.getRowsNum() - 2*RANGE) + RANGE, gps.z);
                        break;
                    case LEFT:
                        bounceDir = Bounce.UP;
                        obj = new Vec3d(rand.nextInt(MAP_HEIGHT.getColsNum() - 2*RANGE) + RANGE, RANGE,  gps.z);
                        break;
                
                }
                break;
        }
        return obj;  
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
    
}
