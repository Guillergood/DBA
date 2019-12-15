/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import Practica_3.Util.Command.Direction;
import Practica_3.Util.IJsonSerializable;
import com.sun.javafx.geom.Vec3d;

/**
 *
 * @author Alberto
 */
public class FlyAgent extends Agent {
    public FlyAgent(String id, float fuel_limit, Vec3d init_pos) throws Exception {
        super(id, AgentType.FLY, fuel_limit, init_pos);
    }
    
    /**
     * The way the agent goes
     * @return The next action to perform
     */
    @Override
    protected Vec3d chooseMovement() {
        // Si se ha llamado a este método es que el plan anterior ha finalizado
        // y se debe "rebotar" hacia una dirección opuesta.
        
        // Para rebotar hay que intentar definir el ángulo opuesto al que se seguía
        // con una mutación ligera pseudoaleatoria, elegir una casilla del borde del mapa
        // en esa dirección y mandar al algoritmo a buscar un plan.
        
        throw new UnsupportedOperationException("Paluego");
    }

    @Override
    protected void fill_map_explored() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
