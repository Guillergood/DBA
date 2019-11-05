/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2;

import DBA.SuperAgent;
import Practica_2.GUI.fxml.WindowController;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
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
    private boolean status;
    private boolean goal;
    private ArrayList<Vec3d> trace;
    private final String id;
    private String key;
    // World dimensions
    private int min;
    private int max;
    private int dimx;
    private int dimy;
    // Footprint map
    private int footprints[][];
    //OnRefuel mode
    private boolean onRefuel;
    // Active sensors
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
        
        // OnRefuel at start is on:
        onRefuel = true;
    }
    
    /**
     * Decoding sensors information    
     * @author Alberto Gurrea
     */
    
    /**
     * <p> Send the first message to controller and return if the login was successful. </p>
     * @author Bruno Garcia
     * @author Alberto Gurrea
     * @author Guillermo Bueno
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
     * @author Guillermo Bueno
     * @return the action that Agent will perform
     */
    private Command chooseMovement() throws Exception {
        
        // Return value
        Command move;
        
        // Primero, si alguna de las casillas de alrededor muestra fuel warning, 
        // la acción es bajar y si está en el suelo repostar.
        for(int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if (fuelWarning(i, j))
                    onRefuel = true;
            }
        }
        
        if(onRefuel) {
            // If on floor, move down
            if((int)gps.z == radar[5][5]) {
                move = Command.REFUEL;
                onRefuel = false;
            }

            else
                move = Command.MOVE_DW;
        }
        else {
            // Octant which the goal is in
            int parts = 360/8;
            float angle = (float)gonio.getValue();
            int octant = (int)(angle/parts);

            // La idea es penalizar los movimientos que no sean moverse en el ángulo
            // del objetivo, y luego sumarle el esfuerzo para llegar, es decir, si 
            // la casilla está a elevation -15 y cada movimiento hacia arriba son 5, sería un esfuerzo de
            // 3. Elegir casilla, y después comprobar si está más alta que nosotros. Si es así, subir.

            // El código va a estar feo pero si funciona se pone bonito después
            int[] angleValue = new int[8];

            // El suyo
            ++angleValue[octant];

            // Los de al lado
            angleValue[(octant + 1) % angleValue.length] += 2;
            angleValue[(octant - 1) % angleValue.length] += 2;

            // Los otros
            angleValue[(octant + 2) % angleValue.length] += 3;
            angleValue[(octant - 2) % angleValue.length] += 3;

            // El opuesto
            angleValue[(octant + 3) % angleValue.length] += 4;

            // Moves to evaluate:
            ArrayList<Pair> possibleMoves = new ArrayList();
            possibleMoves.add(new Pair(Command.MOVE_N, evaluate(0, -1) + angleValue[0]));
            possibleMoves.add(new Pair(Command.MOVE_NE, evaluate(1, -1) + angleValue[1]));
            possibleMoves.add(new Pair(Command.MOVE_E, evaluate(1, 0) + angleValue[2]));
            possibleMoves.add(new Pair(Command.MOVE_SE, evaluate(1, 1) + angleValue[3]));
            possibleMoves.add(new Pair(Command.MOVE_S, evaluate(0, 1) + angleValue[4]));
            possibleMoves.add(new Pair(Command.MOVE_SW, evaluate(-1, 1) + angleValue[5]));
            possibleMoves.add(new Pair(Command.MOVE_W, evaluate(-1, 0) + angleValue[6]));
            possibleMoves.add(new Pair(Command.MOVE_NW, evaluate(-1, -1) + angleValue[7]));
            
            final Comparator comp = (o1, o2) -> {
                Pair<Command, Integer> o1Pair = (Pair) o1;
                Pair<Command, Integer> o2Pair = (Pair) o2;
                return o1Pair.getValue() - o2Pair.getValue();
            };
        
            possibleMoves.sort(comp);

            Command chosen = (Command)possibleMoves.get(0).getKey();
            if (above(chosen))
                move = Command.MOVE_UP;
            else
                move = chosen;
        }
 
        return move;
    }
    
    
    /**
     * <p>Evaluates vertical effort to reach a certain (nearby) place</p>
     * The place must be detected in the elevation and radar sensors.
     * @author Juan Ocaña
     * @param x X relative coordinate to the agent
     * @param y Y relative coordinate to the agent
     * @return evaluation
     */
    private int evaluate(int x, int y) {
        
        // Return value
        int effort = 1;
        
        // Agent position in sensor matrix
        int agent = 5;
        
        // Length of a move
        int moveUnit = 5;
        
        // RADAR: if height exceeds max, effort is MAX_VALUE
        if(radar[agent+x][agent+y] > max) {
            effort = Integer.MAX_VALUE;
        }
        
        // ELEVATION: check elevation and calculate how many moves needed to reach
        // the place if it is above the agent
        if(elevation[agent+x][agent+y]/moveUnit < 0)
            effort += Math.abs(elevation[agent+x][agent+y]/moveUnit);
        
        
        return effort;
    }
    
        /**
     * <p>Evaluates vertical effort to reach a certain (nearby) place</p>
     * The place must be detected in the elevation and radar sensors.
     * @author Juan Ocaña
     * @param x X relative coordinate to the agent
     * @param y Y relative coordinate to the agent
     * @return evaluation
     */
    private boolean fuelWarning(int x, int y) {
        
        // Agent position in sensor matrix
        int agent = 5;
        
        // Fuel consumption per move
        double fuelCon = 0.5;
        
        // ELEVATION: if height * fuelCon >= fuelWarning, warning is true
        return (elevation[agent+x][agent+y] * fuelCon >= fuel);
    }
    
    private boolean above(Command c) {
        
        // Agent position in sensor matrix
        int agent = 5;
        
        // Relative coordinates
        int x = 0;
        int y = 0;
        
        switch(c) {
            case MOVE_N:
                x = 0;
                y = -1;
                break;
            case MOVE_NE:
                x = 1;
                y = -1;
                break;
            case MOVE_E:
                x = 1;
                y = 0;
                break;
            case MOVE_SE:
                x = 1;
                y = 1;
                break;
            case MOVE_S:
                x = 0;
                y = 1;
                break;
            case MOVE_SW:
                x = -1;
                y = 1;
                break;
            case MOVE_W:
                x = -1;
                y = 0;
                break;
            case MOVE_NW:
                x = -1;
                y = -1;
                break;
        }
        
        return (elevation[agent+x][agent+y] < 0);
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
            
            if(keyValue != null)            
                this.key = keyValue.asString();
            
            // If response to login, save world dimensions and set footprints map
            if("login".equals(responseJson.get("in-reply-to").asString())) {
                dimx = responseJson.get("dimx").asInt();
                dimy = responseJson.get("dimy").asInt();
                min = responseJson.get("min").asInt();
                max = responseJson.get("max").asInt();
                
                // Footprints map initialized to 0
                footprints = new int[dimx][dimy];
                
                for(int i = 0; i < dimx; ++i) {
                    for(int j = 0; j < dimy; ++j) {
                        footprints[i][j] = 0;
                    }
                }
                
                System.out.println("WORLD");
            }
            
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
                
                // Register footprint
                ++footprints[(int)gps.x][(int)gps.y];
                
                // Choose next movement
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
        Platform.runLater(()->{
            observers.forEach((observer)->{
                if(observer instanceof RadarNode)
                {
                    Pair<Vec3d,int[][]> data = new Pair(this.gps,this.radar);
                    observer.update(this, data);
                }                
                else
                    observer.update(this, "Who are you?");
            });            
        });    
        if(statusProperty.get().equals(WindowController.Status.PAUSE))
            return;
        
        Thread t = new Thread(()->{try {
            Thread.sleep(50);
            gui_lock.release();
            } catch (InterruptedException ex) {
                Logger.getLogger(Agent.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        t.setDaemon(true);
        t.start();
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
