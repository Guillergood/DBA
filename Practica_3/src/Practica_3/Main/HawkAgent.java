/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import com.sun.javafx.geom.Vec3d;

/**
 *
 * @author Alberto
 */
public class HawkAgent extends Agent{
    public HawkAgent(String id, float fuel_limit, Vec3d init_pos) throws Exception {
        super(id, AgentType.FLY, fuel_limit, init_pos);
    }
}
