/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2.GUI.nodes;

import Practica_2.Agent;
import com.sun.javafx.geom.Vec3d;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 *
 * @author brbco
 */
public class RadarNode extends GridNode{    
    public RadarNode() {
        super(11, 11);
    }
    
    @Override
    public void update(Agent o, Object data) {
        Object parsedData[] = (Object[]) data;
        Vec3d gps = (Vec3d) parsedData[0];
        Pair<Integer,Integer> flightLimits = (Pair<Integer,Integer>) parsedData[1];
        String gps_str = (gps==null)?"offline":
                "{"+gps.x+", "+gps.y+", "+gps.z+"}";
        int[][] radar = (int[][]) parsedData[2];        
        int[][] magnetic = (int[][]) parsedData[3];        
        
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                int value = radar[i][j];
                Label label = new Label(""+value);
                if(value>=flightLimits.getValue())
                    label.setStyle("-fx-background-color: darkred");
                if(value>flightLimits.getValue() && value<flightLimits.getValue() && value>gps.z)
                    label.setStyle("-fx-background-color: red");
               
                if(i==5 && j==5)
                {
                    String str_tp = String.format("Agent GPS: %s\nMap Height: %d",gps_str,value);
                    if(gps!=null)
                    {

                        double agentHeight = gps.z;
                        int height_gap = (int) (agentHeight-value);
                        str_tp = str_tp.concat("\nHeight gap: "+height_gap);
                    }                        
                    
                    Tooltip tooltip = new Tooltip(str_tp);
                    this.setCell(i, j, label,tooltip , Color.LIGHTGREEN,Color.web("#9affae"));
                }                
                else if(value == 0)
                    this.setCell(i, j, null, new Tooltip("End of map"), Color.RED);
                else if(magnetic[i][j]!=0)
                    this.setCell(i, j, label, new Tooltip("<goal>\nHeight: "+value), Color.YELLOW,Color.LIGHTYELLOW);
                else
                    this.setCell(i, j, label, new Tooltip(""+value), Color.LIGHTGRAY,Color.web("#e7e7e7"));
            }
        }
        
    }
    
}
