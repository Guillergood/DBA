/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2.GUI.fxml;

import Practica_2.Agent;
import Practica_2.GUI.nodes.MapNode;
import Practica_2.GUI.nodes.RadarNode;
import Practica_2.Main;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;


/**
 * FXML Controller class
 *
 * @author brbco
 */
public class WindowController implements Initializable {

    @FXML
    private AnchorPane container;
    @FXML
    private ComboBox<Agent.Maps> cb_map;
    @FXML
    private Region spacerLeft;
    @FXML
    private FlowPane toolBox;
    @FXML
    private Region spacerRight;
    @FXML
    private ComboBox<?> cb_layout;
    @FXML
    private HBox vbox_menu;
    @FXML
    private Button btnPlay;
    @FXML
    private Button btnPause;
    @FXML
    private Button btnForward;
    @FXML
    private Button btnDebug;
    @FXML
    private Button btnStop;
    @FXML
    private FlowPane offlineNode;

    @FXML
    private void onPlayClick(ActionEvent event) {         
        boolean flag = statusProperty.get().equals(Status.STOP); 
        agentIsProcesingProperty.set(true);
        statusProperty.set(Status.RUNNING);        
        if(flag){
           mapNode.clear();
           createAgent();        
        }
    }

    @FXML
    private void onPauseClick(ActionEvent event) {        
        statusProperty.set(Status.PAUSE);
    }

    @FXML
    private void onForwardClick(ActionEvent event) {
        agentIsProcesingProperty.set(true);
        statusProperty.set(Status.PAUSE);
        step.run();
    }

    @FXML
    private void onDebugClick(ActionEvent event) {
        agentIsProcesingProperty.set(true);
        statusProperty.set(Status.PAUSE);
        mapNode.clear();
        createAgent();        
    }

    @FXML
    private void onStopClick(ActionEvent event) {
        statusProperty.set(Status.STOP);
    }

    public enum Status{
        STOP,
        RUNNING,
        PAUSE
    }
    public final ObjectProperty<Status> statusProperty = new SimpleObjectProperty<>();        
    public final ObjectProperty<Boolean> agentIsProcesingProperty = new SimpleObjectProperty<>();
    Runnable step = ()->{System.err.println("Unexpected STEP");};
    
    RadarNode radarNode = new RadarNode();
    MapNode mapNode = new MapNode(10);
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {    
        statusProperty.addListener((ObservableValue<? extends Status> obs,Status oldValue,Status newValue)->{
            toolBox.getChildren().clear();
            
            switch (newValue) {
                case STOP:
                    cb_map.disableProperty().set(false);
                    toolBox.getChildren().addAll(btnPlay,btnDebug);
                    step = ()->{System.err.println("Unexpected STEP");}; 
                    //container.getChildren().clear();
                    //container.getChildren().add(offlineNode);  
                    break;
                case RUNNING:
                    btnForward.disableProperty().set(true);
                    cb_map.disableProperty().set(true);
                    toolBox.getChildren().addAll(btnPause,btnForward,btnStop);
                    if(oldValue.equals(Status.STOP))
                        addContent();
                break;
                case PAUSE:
                    cb_map.disableProperty().set(true);
                    btnForward.disableProperty().set(false);
                    toolBox.getChildren().addAll(btnPlay,btnForward,btnStop); 
                    if(oldValue.equals(Status.STOP))
                        addContent();
                break;
                default:
                    throw new AssertionError();
            }
            
        });
        
        agentIsProcesingProperty.addListener((obs,oldValue,newValue)->{
            if(newValue)
            {
                toolBox.disableProperty().set(true);
                toolBox.setCursor(Cursor.WAIT);
            }
            else{
                toolBox.disableProperty().set(false);
                toolBox.setCursor(Cursor.DEFAULT);
            }
        });
        statusProperty.set(Status.STOP);        
                
        cb_map.setItems(FXCollections.observableArrayList(Agent.Maps.values()));
        cb_map.setValue(Agent.Maps.PLAYGROUND);
        
        //cb_mode.setItems(FXCollections.observableArrayList(Mode.values()));
        
        // Menu bar
        double cb_map_width = cb_map.getPrefWidth();       
        
        vbox_menu.widthProperty().addListener((obs,oldValue,newValue)->{ 
            double content_width = 0;
            for (Node child : toolBox.getChildren())            
                content_width+=child.getBoundsInLocal().getWidth();
            //Adding margin            
            content_width+= 50;
            
            toolBox.setPrefWidth(content_width);
            toolBox.autosize();
            
            double spacerLeft_width =((double)newValue-content_width)/2 - cb_map_width -8;
            spacerLeft.setPrefWidth(spacerLeft_width);
            spacerLeft.autosize();   
        });
        
              
    }    

    private void addContent(){
        container.getChildren().clear();
        
        SplitPane pane = new SplitPane(mapNode,radarNode);
        pane.orientationProperty().set(Orientation.HORIZONTAL);
        pane.getStyleClass().add("split-pane-h");
        
        AnchorPane.setTopAnchor(pane, 0d);
        AnchorPane.setBottomAnchor(pane, 0d);
        AnchorPane.setLeftAnchor(pane, 0d);
        AnchorPane.setRightAnchor(pane, 0d);        
        
        container.getChildren().add(pane);
        //container.getChildren().add(radarNode);  
    }
    
    private void createAgent(){          
        try {    
            Agent gb_agent = Main.getAgent(cb_map.getValue());
            gb_agent.statusProperty.bindBidirectional(statusProperty);  
            gb_agent.agentIsProcesingProperty.bindBidirectional(agentIsProcesingProperty);            
            gb_agent.addOvserver(radarNode);
            gb_agent.addOvserver(mapNode);
            step = () -> {gb_agent.nextStep();};
            Thread t = new Thread(() -> {gb_agent.execute();});
            t.setDaemon(true);
            t.start();
        } catch (Exception ex) {
            Logger.getLogger(WindowController.class.getName()).log(Level.SEVERE, null, ex);
            Platform.exit();
            System.exit(0);
        }        
    }
    
}
