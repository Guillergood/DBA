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
        Pair<Vec3d,int[][]> parsedData = (Pair<Vec3d,int[][]>) data;
        String gps_str = (parsedData.getKey()==null)?"offline":
                "{"+parsedData.getKey().x+", "+parsedData.getKey().y+", "+parsedData.getKey().z+"}";
        int[][] radar = parsedData.getValue();        
        
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                int value = radar[i][j];
                if(i==5 && j==5)
                {
                    String str_tp = String.format("Agent GPS: %s\nMap Height: %d",gps_str,value);
                    if(parsedData.getKey()!=null)
                    {
                        double agentHeight = parsedData.getKey().y;
                        int height_gap = (int) (agentHeight-value);
                        str_tp = str_tp.concat("\nHeight gap: "+height_gap);
                    }                        
                    
                    Tooltip tooltip = new Tooltip(str_tp);
                    this.setCell(i, j, new Label(""+value),tooltip , Color.LIGHTGREEN,Color.web("#9affae"));
                }                
                else if(value == 0)
                    this.setCell(i, j, null, new Tooltip("End of map"), Color.RED);
                else
                    this.setCell(i, j, new Label(""+value), new Tooltip(""+value), Color.LIGHTGRAY,Color.web("#e7e7e7"));
            }
        }
        
    }
    
}
