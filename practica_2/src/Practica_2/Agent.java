/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2;

import DBA.SuperAgent;
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
    private Vec3d gps;
    private int fuel;
    private int[][] radar = new int[11][11];
    private int[][] magnetic = new int[11][11];
    private int[][] elevation = new int[11][11];
    private Pair<Integer,Float> gonio;
    private static Object globalMap;
    private boolean status;
    private boolean goal;
    private ArrayList<Vec3d> trace;
    private int fuelWarning;
    private String id;
    private String key;
    private HashMap<String, Boolean> activeSensors;
    
    public Agent(AgentID aid) throws Exception {
        super(aid);
    }

    public Vec3d getGps() {
        return gps;
    }

    public int getFuel() {
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

    public boolean isStatus() {
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

    public String getKey() {
        return key;
    }

    public HashMap<String, Boolean> getActiveSensors() {
        return activeSensors;
    }
    
     
}
