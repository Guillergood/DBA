/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2;

import DBA.SuperAgent;
import Practica_2.GUI.fxml.WindowController;
import Practica_2.GUI.nodes.MapNode;
import Practica_2.GUI.nodes.RadarNode;
import Practica_2.interfaces.Observable;
import Practica_2.interfaces.Observer;
import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;
import com.sun.javafx.geom.Vec3d;
import es.upv.dsic.gti_ia.core.ACLMessage;
import es.upv.dsic.gti_ia.core.AgentID;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

/**
 *
 * @author Guillermo
 */
public class Agent extends SuperAgent implements Observable{
    // <editor-fold defaultstate="collapsed" desc="Vars">
    private Vec3d gps;
    private double fuel;
    private int[][] radar = new int[11][11];
    private int[][] magnetic = new int[11][11];
    private int[][] elevation = new int[11][11];
    private Pair<Float,Float> gonio;
    private static Object globalMap;
    private boolean status; //TODO: check
    private boolean goal;
    private ArrayList<Vec3d> trace;
    private int fuelWarning;
    private final String id;
    private String key;
    private HashMap<String, Boolean> activeSensors = new HashMap<String,Boolean>(){{
        put("gps",true);  
        put("fuel",true);  
        put("radar",true);  
        put("elevation",true);  
        put("magnetic",true); 
        put("gonio",true);  
        //status and goal are always turned on
    }};  
    private final Maps map;
    private Pair<Integer,Integer> mapSize;
    private Pair<Integer, Integer> flightLimits;
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="Enum">
    public static enum Maps{
        PLAYGROUND("Playground","playground"),
        CASE_STUDY("Case Study","case_study"),
        MAP1("Map 1","map1"),
        MAP2("Map 2","map2"),
        MAP3("Map 3","map3"),
        MAP4("Map 4","map4"),
        MAP5("Map 5","map5"),
        MAP6("Map 6","map6"),
        MAP7("Map 7","map7"),
        MAP8("Map 8","map8"),
        MAP9("Map 9","map9"),
        MAP10("Map 10","map10");
    
        Maps(String name,String json_input){
            this.name = name;
            this.json_value = json_input;
        }
        private String name;
        private String json_value;

        @Override
        public String toString() {
            return name;
        }
        
        public String toJsonValue(){
            return json_value;            
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

    public Pair<Float, Float> getGonio() {
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

    public Maps getMap() {
        return map;
    }   
    
    // </editor-fold>
    private List<Observer<Agent>> observers = new ArrayList();
    public final ObjectProperty<WindowController.Status> statusProperty = new SimpleObjectProperty<>();
    public final ObjectProperty<Boolean> agentIsProcesingProperty = new SimpleObjectProperty<>();
    private java.util.concurrent.Semaphore lock = new java.util.concurrent.Semaphore(0);
    private java.util.concurrent.Semaphore gui_lock = new java.util.concurrent.Semaphore(0);
    
    public Agent(String id, Maps map) throws Exception {
        super(new AgentID(id));
        this.id = id;
        gps = new Vec3d();
        trace = new ArrayList<>();
        this.map = map;  
        statusProperty.addListener((obs,oldValue,newValue)->{
            if(newValue.equals(WindowController.Status.RUNNING))
                lock.release();
            else if(newValue.equals(WindowController.Status.STOP))
                lock.release();
        });        
    }
    
    /**
     * Decoding sensors information    
     * @author Alberto Gurrea
     */
    
    /**
     * <p> Send the first message to controller and return if the login was successful. </p>
     * @author Bruno Garcia
     * @author Alberto Gurrea
     */
    private void sensorsParser(String source){        
        JsonArray radarTemp = null;
        boolean radarBool = false;
        JsonArray elevationTemp = null;
        boolean elevationBool = false;
        JsonArray magneticTemp = null;
        boolean magneticBool = false;
        JsonObject perceptionObject;
        perceptionObject = Json.parse(source).asObject();
        JsonObject object = perceptionObject.get("perceptions").asObject();
        //object = Json.parse(source).asObject();
        
        System.out.println("\nperceptionObject: " + perceptionObject);
        System.out.println("\nobject: " + object);
        
        if(object.get("gps") != null){
            System.out.println("\nGPS cogido");
            gps.set(object.get("gps").asObject().get("x").asInt(),object.get("gps").asObject().get("y").asInt(),object.get("gps").asObject().get("z").asInt());
            System.out.println("\nGPS es: "+ gps.toString());
        }
        
        if(object.get("fuel") != null){
            System.out.println("\nFUEL cogido");
            fuel = object.get("fuel").asDouble();
            System.out.println("\nFUEL es: "+ fuel);
        }
        
        if(object.get("radar") != null){
            System.out.println("\nRADAR cogido");
            radarTemp = object.get("radar").asArray();
            radarBool = true;
            System.out.println("\nRADAR es: "+ radarTemp + " " + radarBool);
        }
        if(object.get("elevation") != null){
            System.out.println("\nELEVATION cogido");
            elevationTemp = object.get("elevation").asArray();
            elevationBool=true;
            System.out.println("\nELEVATION es: "+ elevationTemp + " " + elevationBool);
        }
        if(object.get("magnetic") != null){
            System.out.println("\nMAGNETIC cogido");
            magneticTemp = object.get("magnetic").asArray();
            magneticBool = true;
            System.out.println("\nMAGNETIC es: "+ magneticTemp + " " + magneticBool);
        }
        if(object.get("goal") != null){
            goal = object.get("goal").asBoolean();
        }
        int i = 0;
        int j = 0;
        for(int c=0; c<121;c++){
            i = c%11;
            j = c/11;
            
            if(radarBool) radar[i][j] = radarTemp.get(c).asInt();            
            if(elevationBool) elevation[i][j] = elevationTemp.get(c).asInt();
            if(magneticBool) magnetic[i][j] = magneticTemp.get(c).asInt();
        }
        
        if(object.get("gonio") != null ){
            JsonObject gonioObject = object.get("gonio").asObject();
            Float key = gonioObject.get("distance").asFloat();
            Float value = gonioObject.get("angle").asFloat();
            gonio = new Pair(key,value);
        } 
    }
    
    /**
     * <p> Send a message to the controller. </p>     
     * @author Bruno Garcia
     */
    private void sendMsg(String msg){
        ACLMessage acl_msg = new ACLMessage();
        acl_msg.setSender(this.getAid());
        acl_msg.setReceiver(new AgentID("Bellatrix"));
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
        jsonMsg.add("map", map.toJsonValue());
        activeSensors.forEach((String key,Boolean value) -> jsonMsg.add(key, value));
        jsonMsg.add("user", Main.USER);
        jsonMsg.add("password", Main.PASSWORD);
        System.out.println(jsonMsg.toString());
        sendMsg(jsonMsg.toString());
        return checkStatus();
    }   
    
    /**
     * <p> Try get the perceptions from controller and save it on internals vars. </p>
     * @author Bruno Garcia
     * @return true, if is the operation successful.
     */
    private boolean updatePerception(){
        try {
            String perception_str = getMsg();
            if(perception_str==null)
                return false;
            else if(perception_str.isEmpty())
                return false;
            
            sensorsParser(perception_str);
            this.notifyObservers();
            return true;
        } catch (InterruptedException ex) {
            return false;
        }
    }
    
    /**
     * <p> Choose the next action to perform </p>
     * Currently moves based on the angle of gonio.
     * For test purposes only,
     * @author Juan Ocaña
     * @return the action that Agent will perform
     */
    private Command chooseMovement() throws Exception {
        double distance = (double)gonio.getKey();
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
        
        return movement;
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
        
        try{
            ACLMessage newIn = this.receiveACLMessage();
            JsonObject injson = Json.parse(newIn.getContent()).asObject();
            
            if(injson != null){
                return traceProcess();
            }
            return false;
            
        } catch (InterruptedException ex){
            System.err.println("Error");
            return false;
        }

        
        
    }
    
    private boolean traceProcess(){
        try {
            System.out.println("Recibiendo traza");
            ACLMessage newIn = this.receiveACLMessage();
            JsonObject injson = Json.parse(newIn.getContent()).asObject();
            
            
            if(injson.get("result").asString().equals("ok")){
                ACLMessage finalMsg = this.receiveACLMessage();
                JsonObject finalJson = Json.parse(finalMsg.getContent()).asObject();
                JsonArray ja = finalJson.get("trace").asArray();
            
                byte data[] = new byte [ja.size()];
                for( int i= 0; i<data.length; i++){
                    data[i] = (byte) ja.get(i).asInt();
                }
                FileOutputStream fos = new FileOutputStream("mitraza.png");
                fos.write(data);
                fos.close();
            }
            System.out.println(injson);
            System.out.println("Traza guardada");
            return true;
        } catch (InterruptedException | IOException ex) {
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
            if(response == null){
                return false;
            }
            JsonObject responseJson = Json.parse(response).asObject();
            //System.out.println("\nRESPONSE JSON LOGIN: " + responseJson.toString());
            JsonValue resultValue = responseJson.get("result");
            JsonValue keyValue = responseJson.get("key");
            JsonValue dimxValue = responseJson.get("dimx");
            JsonValue dimyValue = responseJson.get("dimy");
            JsonValue minValue = responseJson.get("min");
            JsonValue maxValue = responseJson.get("max");
            
            if(keyValue != null)            
                this.key = keyValue.asString();     
            if(dimxValue != null && dimyValue != null)
                this.mapSize = new Pair(dimxValue.asInt(),dimyValue.asInt());
            if(minValue != null && maxValue!= null)
                this.flightLimits = new Pair(minValue.asInt(),maxValue.asInt());
            if (resultValue.asString().equals("ok"))
            {
                System.out.println("> STATUS OK");
                return true;
            }                
            else 
            {
                System.err.println("Error "+response);
                return false;
            }                
        } catch (InterruptedException ex) {
            return false;
        }   
    }
    
    @Override
    public void execute() {
        super.execute(); 
        
        System.out.println();
        System.out.println("> LOGIN");
        if(!login()) return;
        do
        {         
            System.out.println("> UPDATE PERCEPTION");
            if(!updatePerception())
                break;             
            
            try {
                if(statusProperty.get().equals(WindowController.Status.PAUSE))
                    lock.acquire();
                
                Command act = chooseMovement();
                System.out.println("> MOVE \""+act+"\"");
                sendAction(act);
            } catch (InterruptedException ex) {
                Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                break; 
            }           
            
        }while(checkStatus() && !isGoal() && !statusProperty.get().equals(WindowController.Status.STOP));        
        System.out.println("> LOGOUT");
        logout();     
        Platform.runLater(()->statusProperty.set(WindowController.Status.STOP));
    }
   
    @Override
    public void addOvserver(Observer o) {
        observers.add(o);
    }
    @Override
    public void notifyObservers() {
        java.util.concurrent.Semaphore aux_s = new java.util.concurrent.Semaphore(0);
        long start_time = System.currentTimeMillis();        
        
        Thread t = new Thread(()->{
            try {
                aux_s.acquire();
                long end_time = System.currentTimeMillis();
                long difference = end_time-start_time;
                System.out.println("Redraw time(ms): "+difference);
                if(difference<25)
                    Thread.sleep(25-difference);
                agentIsProcesingProperty.set(false);
                gui_lock.release();
            } catch (InterruptedException ex) {
                Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.setDaemon(true);
        t.start();
        
        Platform.runLater(()->{            
            observers.forEach((observer)->{
                if(observer instanceof RadarNode)
                {
                    Object data[] = new Object[4];
                    data[0] = gps;
                    data[1] = flightLimits;
                    data[2] = radar;
                    data[3] = magnetic;
                    //Pair<Vec3d,int[][]> data = new Pair(this.gps,this.radar);
                    observer.update(this, data);
                }     
                else if(observer instanceof MapNode)
                {
                    Object data[] = new Object[5];
                    data[0] = mapSize;
                    data[1] = flightLimits;
                    data[2] = gps;
                    data[3] = radar;
                    data[4] = magnetic;
                    
                    observer.update(this, data);
                }
                else
                    observer.update(this, "Who are you?");    
            });
            
            aux_s.release();
        });    
        if(statusProperty.get().equals(WindowController.Status.PAUSE))
            return;
        
        
        try {
            gui_lock.acquire();
        } catch (InterruptedException ex) {
            Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public void nextStep(){
        if(statusProperty.get().equals(WindowController.Status.PAUSE))
            lock.release();
    }
    
}
