/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2;

import DBA.SuperAgent;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sun.javafx.geom.Vec3d;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.util.Pair;

/**
 *
 * @author Guillermo
 */
public class Agent extends SuperAgent {
    // <editor-fold defaultstate=Command.collapsed" desc="Vars">
    private Vec3d gps;
    private double fuel;
    private int[][] radar = new int[11][11];
    private int[][] magnetic = new int[11][11];
    private int[][] elevation = new int[11][11];
    private Pair gonio;
    private static Object globalMap;
    private boolean status; //TODO: check
    private boolean goal;
    private ArrayList<Vec3d> trace;
    private int fuelWarning;
    private final String id;
    private String key;
    private HashMap<String, Boolean> activeSensors = new HashMap<String,Boolean>(){{
        put("gps",false);  
        put("fuel",false);  
        put("radar",false);  
        put("elevation",false);  
        put("magnetic",false); 
        put("gonio",false);  
        //status and goal are always turned on
    }};  
    private final String map_name = "map10";
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Enum">
    private enum Status{
        OFFLINE("offline"),
        OPERATIVE("operative"),
        CRASHED("crashed"),
        ERROR("any error");
    
        Status(String name){
            this.name = name;
        }
        private String name;

        @Override
        public String toString() {
            return name;
        }
        
    }
    private enum Command{
        MOVE_N("moveN"),
        MOVE_NE("moveNE"),
        MOVE_E("moveE"),
        MOVE_SE("moveSE"),
        MOVE_S("moveS"),
        MOVE_SW("moveSW"),
        MOVE_W("moveW"),
        MOVE_NW("moveNW"),
        MOVE_UP("moveUP"),
        MOVE_DW("moveDW"),
        REFUEL("refuel");        
    
        Command(String name){
            this.name = name;
        }
        private String name;

        @Override
        public String toString() {
            return name;
        }
        
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Getters">
    public Vec3d getGps() {
        return gps;
    }

    public double getFuel() {
        return fuel;
    }

    public int[][] getRadar() {
        return radar;
    }

    public int[][] getMagnetic() {
        return magnetic;
    }

    public int[][] getElevation() {
        return elevation;
    }

    public Pair<Integer, Float> getGonio() {
        return gonio;
    }

    public static Object getGlobalMap() {
        return globalMap;
    }

    public boolean getStatus() {
        return status;
    }

    public boolean isGoal() {
        return goal;
    }

    public ArrayList<Vec3d> getTrace() {
        return trace;
    }

    public int getFuelWarning() {
        return fuelWarning;
    }

    public String getId() {
        return id;
    }

    public HashMap<String, Boolean> getActiveSensors() {
        return activeSensors;
    }
    // </editor-fold>
    
    public Agent(String id) throws Exception {
        super(new AgentID(id));
        this.id = id;
    }
    
    private void sensorsParser(String source){
        JsonObject object = new JsonObject();
        JsonArray radarTemp;
        JsonArray elevationTemp;
        JsonArray magneticTemp;
        object = Json.parse(source).asObject();
        gps.set(object.get("gps").asObject().get("x").asInt(),object.get("gps").asObject().get("y").asInt(),object.get("gps").asObject().get("z").asInt());

        fuel = object.get("fuel").asDouble();

        radarTemp = object.get("radar").asArray();
        elevationTemp = object.get("elevation").asArray();
        magneticTemp = object.get("magnetic").asArray();

        int i = 0;
        int j = 0;
        for(int c=0; c<radarTemp.size();c++){
            radar[j][i] = radarTemp.get(c).asInt();
            elevation[j][i] = elevationTemp.get(c).asInt();
            magnetic[j][i] = magneticTemp.get(c).asInt();

            if(i == 11){
                i=0;
                j++;
            }
        }

        gonio = new Pair(object.get("gonio").asObject().get("distance").asInt(),object.get("gonio").asObject().get("angle").asFloat());
    }
    
    /**
     * <p> Send a message to the controller. </p>     
     * @author Bruno Garcia
     */
    private void sendMsg(String msg){
        ACLMessage acl_msg = new ACLMessage();
        acl_msg.setSender(this.getAid());
        acl_msg.setReceiver(new AgentID("controlador"));
        acl_msg.setContent(msg);
        this.send(acl_msg);
    }
    
    /**
     * <p> Get a message from controller and return the content. </p>     
     * @author Bruno Garcia
     * @throws InterruptedException
     * @return the content of message as String.
     */
    private String getMsg() throws InterruptedException{
         ACLMessage acl_msg = new ACLMessage();
         acl_msg = this.receiveACLMessage();
         return acl_msg.getContent();
    }
    
    /**
     * <p> Send the first message to controller and return if the login was successful. </p>
     * @author Bruno Garcia
     * @return if login was successful
     */
    private boolean login(){
        JsonObject jsonMsg = new JsonObject();
        jsonMsg.add("command", "login");
        jsonMsg.add("map", map_name);
        activeSensors.forEach((String key,Boolean value) -> jsonMsg.add(key, value));
        jsonMsg.add("user", Main.USER);
        jsonMsg.add("password", Main.PASSWORD);
        
        sendMsg(jsonMsg.toString());
        return checkStatus();
    }   
    
    /**
     * <p> Choose the next action to perform </p>
     * TODO(is not implemented yet)
     * @author 
     * @return the action that Agent will perform
     */
    private Command chooseMovement(){
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    
    /**
     * <p> Send the action to perform to the controller. </p>
     * @param command - the action to perform.
     * @author Bruno Garcia
     */
    private void sendAction(Command command){
        JsonObject jsonMsg = new JsonObject();
        jsonMsg.add("command", command.toString());
        jsonMsg.add("key", this.key);
        sendMsg(jsonMsg.toString());
    }
    
    /**
     * <p> Send the last message to controller and return if the logout was successful. </p>
     * TODO: get trace
     * @author Bruno Garcia
     * @return if logout was successful
     */
    private boolean logout(){
        JsonObject jsonMsg = new JsonObject();
        jsonMsg.add("command", "logout");
        jsonMsg.add("key", this.key);
        sendMsg(jsonMsg.toString());
        
        try {
            String response = getMsg();
            //TODO: get trace
            return true;
        } catch (InterruptedException ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        }         
        return false;
    }

    /**
     * <p> Check if last message was successful </p>     
     * @author Bruno Garcia
     * @return the status with controller
     */
    private boolean checkStatus(){
       try {
            String response = getMsg();
            JsonObject responseJson = Json.parse(response).asObject();
            JsonValue resultValue = responseJson.get("result");
            JsonValue keyValue = responseJson.get("key");
            
            if(keyValue != null)            
                this.key = keyValue.asString();            
            
            if (resultValue.asString().equals("ok"))
                return true;
            else 
            {
                System.err.println("Error {\"result\": \""+resultValue.asString()+"\", \"in-reply-to\": \""+responseJson.get("in-reply-to").asString());
                return false;
            }                
        } catch (InterruptedException ex) {
            return false;
        }   
    }
    
    @Override
    protected void execute() {
        super.execute(); 
        if(!login()) return;
        do
        {
            //TODO: Get perception msg from controller
            //TODO: Update sensors
            Command act = chooseMovement();
            sendAction(act);
        }while(checkStatus() && !goal);
        logout();            
    }
    
    /**
     * The agent plans a path to follow, it is determined by some heuristics, 
     * @author Guillermo Bueno Vargas
     * @throws Exception, when the movementCode is below 0 or above 8
     * @throws Exception, when gonio has not been initialized or does not cointain correct values
     * @return whether the path is possible or not and the movements
     */
    
    Pair<Boolean, ArrayList> pathFinding() throws Exception {
        
        final int MOVEMENTS_THRESHOLD = 3;
        
        if(gonio == null || (gonio.getKey() == null || gonio.getValue() == null) ){
            throw new Exception("ERROR GONIO NOT INIZIALIZED OR CONTAINS WRONG VALUES");
        }
        
        int lastDistance = (int)gonio.getKey();
        ArrayList<Command> movements = new ArrayList<>();
        Boolean possiblePlan = false;
        
        while(lastDistance < MOVEMENTS_THRESHOLD){
            int distance = (int)gonio.getKey();
            float angle = (float)gonio.getValue();

            // We have 8 movements, so the angle/45º will tell us where to move

            int parts = 360/8; //45 degrees

            int movementCode =(int) (angle/parts);

            Command movement;

            switch(movementCode){
                case 0:
                    movement = Command.MOVE_N;
                    break;
                case 1:
                    movement = Command.MOVE_NE;
                    break;
                case 2:
                    movement = Command.MOVE_E;
                    break;
                case 3:
                    movement = Command.MOVE_SE;
                     break;
                case 4:
                    movement = Command.MOVE_S;
                    break;
                case 5:
                    movement = Command.MOVE_SW;
                    break;
                case 6:
                    movement = Command.MOVE_W;
                    break;
                case 7:
                    movement = Command.MOVE_NW;
                    break;
                default:
                    throw new Exception("ERROR WHILE PLANNING WHERE TO GO");
            }
            
            movements.add(movement);
            lastDistance = distance;
       
        }
        
        
        
        if(movements.size() > 0){
            possiblePlan = true;
        }
        
        return new Pair<>(possiblePlan, movements);
    }

    
    
}
