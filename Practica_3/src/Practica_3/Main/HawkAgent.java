/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import com.sun.javafx.geom.Vec3d;
import java.util.Random;

/**
 *
 * @author Alberto
 */
public class HawkAgent extends Agent{
    
    // Bounce values
    private int bounceAngle;
    Random rand = new Random();

    public HawkAgent(String id, float fuel_limit) throws Exception {
        super(id, AgentType.HAWK, fuel_limit, 230, 100, 41);
        bounceAngle = rand.nextInt(359);
    }
    
    /**
     * The way the agent goes
     * @return The next action to perform
     */
    @Override
    protected void chooseMovement() {
        // Si se ha llamado a este método es que se han detectado los bordes del
        // mapa mientras se seguía el plan, por lo que hay que "rebotar"
        
        // Para rebotar hay que intentar definir el ángulo opuesto al que se seguía
        // con una mutación ligera pseudoaleatoria, elegir una casilla del borde del mapa
        // en esa dirección y mandar al algoritmo a buscar un plan.
        
        //Move to perform:
        Vec3d place = null;
        IJsonSerializable command
        
        if(gps.z != MAX_HEIGHT) {
            // Ascend
            place = new Vec3d(gps.x, gps.y, MAX_HEIGHT);
        }
        else if(detectBorder()) {
            // Bounce
            bounceAngle = -bounceAngle + rand.nextInt(15);
            
            // Replanificar con nueva posición
            place = ...
        }
        
        // Refuel
        refuel();
        
        //search(place)
        performMovement(command);
        // Sería conveniente hablar de cómo vamos a mandar replanificar.
        // Si llamamos a search desde aquí este método sería un void
        // Si no, tendríamos que devolver la nueva casilla
        //return place;
    }
    
    private boolean detectBorder() {
        return (gps.x <= RANGE || gps.x >= map_explored.getColsNum() - RANGE ||
                gps.y <= RANGE || gps.y >= map_explored.getRowsNum() - RANGE);
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
