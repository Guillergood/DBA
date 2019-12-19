/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;



import Practica_3.Util.Command;
import Practica_3.Util.Command.Direction;
import Practica_3.Util.IJsonSerializable;
import com.sun.javafx.geom.Vec3d;
import java.util.Random;

/**
 *
 * @author Alberto
 */
public class HawkAgent extends Agent{
    
    // Bounce values
    private Bounce bounceDir;
    Random rand = new Random();


    public HawkAgent(String id, float fuel_limit, boolean debug) throws Exception {
        super(id, AgentType.FLY, fuel_limit, 230, 100, 41, debug);
        bounceDir = Bounce.UP;
    }
    
    /**
     * The way the agent goes
     * @author Juan Ocaña Valenzuela
     * @return The next action to perform
     */
    @Override
    protected IJsonSerializable chooseMovement() {
    /*
        Bounce es un enum que he creado en Agent, que representa los límites de la 
        zona de exploración. En este caso simbolizaría esto:

                UP
              -------
              -     -
        LEFT  -     -  RIGHT
              -------
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
     
        return obj;  
    }
    
    /**
     * Calculates the next command to reach an adjacent position
     * @author Juan Ocaña Valenzuela
     * @param pos
     * @return 
     */
    private Direction move(Vec3d pos) {
        Direction dir = null;
        
        // MoveN
        if(gps.x == pos.x && gps.y > pos.y && gps.z == pos.z)
            dir = Direction.NORTH;
        // MoveNE
        else if(gps.x < pos.x && gps.y > pos.y && gps.z == pos.z)
            dir = Direction.NORTHEAST;
        // MoveE
        else if(gps.x < pos.x && gps.y == pos.y && gps.z == pos.z)
            dir = Direction.EAST;
        // MoveSE
        else if(gps.x < pos.x && gps.y < pos.y && gps.z == pos.z)
            dir = Direction.SOUTHEAST;
        // MoveS
        else if(gps.x == pos.x && gps.y < pos.y && gps.z == pos.z)
            dir = Direction.SOUTH;
        // MoveSW
        else if(gps.x > pos.x && gps.y < pos.y && gps.z == pos.z)
            dir = Direction.SOUTHWEST;
        // MoveW
        else if(gps.x > pos.x && gps.y == pos.y && gps.z == pos.z)
            dir = Direction.WEST;
        // MoveNW
        else if(gps.x > pos.x && gps.y > pos.y && gps.z == pos.z)
            dir = Direction.NORTHWEST;
        // MoveUP
        else if(gps.x == pos.x && gps.y == pos.y && gps.z < pos.z)
            dir = Direction.UP;
        // MoveDW
        else if(gps.x == pos.x && gps.y == pos.y && gps.z > pos.z)
            dir = Direction.DOWN;
        
        return dir;
    }
    
    // No sé si devolver un booleano o la posición del alemán, mañana vemos
    // En el segundo caso (más probable), comprobaría el gonio y el infrared
    // NOTA: podríamos guardar una lista de alemanes detectados para no repetir
    // NOTA: ¿hay que propagar el haber encontrado un alemán? Yo digo que sí
    private boolean detectTourist() {
        // for(int i : infrared) ... 
        return false;
    }
}
