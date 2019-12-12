/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Util;

import Practica_3.Main.Agent.AgentType;
import com.sun.javafx.geom.Vec3d;

/**
 *
 * @author Alberto
 */
public class AwacPart {
    public String agent_name;
    public AgentType agent_type;
    public Vec3d vecPos;
    public Command.Direction direction;
    
    public static AwacPart parse(String string){
        
    }
    public double getX(){
        return vecPos.x;
    }
    public double getY(){
        return vecPos.y;
    }
    public double getZ(){
        return vecPos.z;
    }
    public boolean isRemote(){
        
    }

    @Override
    public String toString() {
        return "AwacPart{" + "agent_name=" + agent_name + ", agent_type=" + agent_type + ", vecPos=" + vecPos + ", direction=" + direction + '}';
    }
    
}
