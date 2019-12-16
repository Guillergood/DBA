/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import Practica_3.Util.IJsonSerializable;
/**
 *
 * @author Alberto
 */
public class FlyAgent extends Agent {
    public FlyAgent(String id, float fuel_limit, boolean debug) throws Exception {
        super(id, AgentType.FLY, fuel_limit, 255, 20, 5,debug);
    }
    
    /**
     * The way the agent goes
     * @return The next action to perform
     */
    @Override
    protected IJsonSerializable chooseMovement() {
        // Si se ha llamado a este método es que el plan anterior ha finalizado
        // y se debe "rebotar" hacia una dirección opuesta.
        
        // Para rebotar hay que intentar definir el ángulo opuesto al que se seguía
        // con una mutación ligera pseudoaleatoria, elegir una casilla del borde del mapa
        // en esa dirección y mandar al algoritmo a buscar un plan.
        
        throw new UnsupportedOperationException("Paluego");
    }
    
}
