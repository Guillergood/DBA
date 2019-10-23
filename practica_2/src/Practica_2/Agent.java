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
import com.sun.javafx.geom.Vec3d;
import es.upv.dsic.gti_ia.core.AgentID;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;

/**
 *
 * @author Guillermo
 */
public class Agent extends SuperAgent {
    // <editor-fold defaultstate="collapsed" desc="Vars">
    private Vec3d gps;
    private double fuel;
    private int[][] radar = new int[11][11];
    private int[][] magnetic = new int[11][11];
    private int[][] elevation = new int[11][11];
    private Pair gonio;
    private static Object globalMap;
    private Status status = Status.OFFLINE;
    private boolean goal;
    private ArrayList<Vec3d> trace;
    private int fuelWarning;
    private final String id;
    private String key;
    private HashMap<String, Boolean> activeSensors;
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
        MOVE_NW("moveNW"),
        MOVE_N("moveN"),
        MOVE_NE("moveNE"),
        MOVE_SW("moveSW"),
        MOVE_S("moveS"),
        MOVE_SE("moveSE"),
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

    public Status getStatus() {
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

    private boolean login(){
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    
    private boolean logout(){
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    
    private Command chooseMovement(){
        //TODO
        throw new java.lang.UnsupportedOperationException("Not supported yet.");
    }
    
    private boolean checkStatus(){
        switch (status) {
            case OFFLINE:
            case CRASHED:    
                return false;
            case OPERATIVE:
                return true;
            default:
                throw new AssertionError();
        }
    }
    
    @Override
    protected void execute() {
        super.execute(); 
        if(!login()) return;
        while(checkStatus() && !goal)
        {
            //TODO: Get msg from controller
            //TODO: Update sensors
            Command act = chooseMovement();
            //TODO: Act send msg to controller
        }    
        logout();
    }    
    
}
