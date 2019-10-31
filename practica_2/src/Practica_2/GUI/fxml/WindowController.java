/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2.GUI.fxml;

import Practica_2.GUI.nodes.GridNode;
import Practica_2.GUI.nodes.RadarNode;
import Practica_2.Main;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;


/**
 * FXML Controller class
 *
 * @author brbco
 */
public class WindowController implements Initializable {

    @FXML
    private AnchorPane container;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        RadarNode child = new RadarNode();
        Main.getAgent().addOvserver(child);        
        container.getChildren().add(child);
        //Main.getAgent().notifyObservers();
    }    

    @FXML
    private void btnUpdate(ActionEvent event) {
        Main.getAgent().notifyObservers();
    }
    
}
