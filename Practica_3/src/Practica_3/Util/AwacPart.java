/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Util;

import Practica_3.Main.Agent.AgentType;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sun.istack.internal.NotNull;
import com.sun.javafx.geom.Vec3d;
import java.util.ArrayList;

/**
 *
 * @author Alberto
 */
public class AwacPart {
    public String agent_name;
    public AgentType agent_type;
    public Vec3d vecPos;
    public Command.Direction direction;
    
    public static ArrayList<AwacPart> parse(@NotNull String string){
       JsonObject perceptionObject;
       perceptionObject = Json.parse(string).asObject();
       JsonArray drones = perceptionObject.get("awacs").asArray();
       
       ArrayList<AwacPart> value = new ArrayList<>();
       
       
      
        for (JsonValue dron : drones) {
            AwacPart awacPart = new AwacPart();
            JsonObject jsonDron= dron.asObject();
            
            
            if(jsonDron.get("name") != null){
                awacPart.agent_name = jsonDron.get("name").asString();
            }
            if(jsonDron.get("x") != null && jsonDron.get("y") != null && jsonDron.get("z") != null){
                double x = 0,y = 0,z = 0;
                x = jsonDron.get("x").asDouble();
                y = jsonDron.get("y").asDouble();
                z = jsonDron.get("z").asDouble();
                awacPart.vecPos.set(x, y, z);
            }
            if(jsonDron.get("rol") != null){
                String rol = jsonDron.get("rol").asString();
                AgentType type = AgentType.parse(rol);
                awacPart.agent_type =  type;
            }
            if(jsonDron.get("direction") != null){
                String direction = jsonDron.get("direction").asString();
                Command.Direction directionCommand = Command.Direction.parse(direction);
                awacPart.direction = directionCommand;
            }
            
            value.add(awacPart);
            
         }
       
       
        return value;
       
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

    @Override
    public String toString() {
        return "AwacPart{" + "agent_name=" + agent_name + ", agent_type=" + agent_type + ", vecPos=" + vecPos + ", direction=" + direction + '}';
    }
    
}
