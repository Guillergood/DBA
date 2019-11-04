/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2.GUI.nodes;

import Practica_2.Agent;
import Practica_2.interfaces.Observer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 *
 * @author brbco
 */
public class MapNode extends ScrollPane implements Observer<Agent> {

    Canvas canvas = new Canvas();
    public final DoubleProperty canvasWidthProperty = new SimpleDoubleProperty(null, "width", 2.0);       
    public final DoubleProperty canvasHeightProperty = new SimpleDoubleProperty(null, "height", 2.0); 
    public final DoubleProperty cellSizeProperty = new SimpleDoubleProperty(null, "height", 50.0); 
    private boolean isInitialized = false;
    
    
    ChangeListener<Number> resizeCanvas = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> obs, Number oldValue, Number newValue) {
            double cellSize = cellSizeProperty.get();
            double width = canvasWidthProperty.get() * cellSize;
            double height = canvasHeightProperty.get() * cellSize;
            System.out.println("resize("+width+", "+height+")");
            canvas.widthProperty().set(width);
            canvas.heightProperty().set(height);
        }
    };
    
    @Override
    public void update(Agent o, Object data) {
        Pair<Pair<Object,Object>,Object> parsedData = (Pair<Pair<Object,Object>,Object>) data;       
        int radar[][] = (int[][]) parsedData.getValue();
        
        
        if(!isInitialized)
        {
            Pair<Integer,Integer> mapSize = (Pair<Integer,Integer>) parsedData.getKey().getKey();
            Pair<Integer,Integer> flightLimits = (Pair<Integer,Integer>) parsedData.getKey().getValue();
            System.out.println(mapSize.toString());
            this.canvasWidthProperty.set(mapSize.getKey().doubleValue());
            this.canvasHeightProperty.set(mapSize.getValue().doubleValue());
            isInitialized = true;
        }
        
        System.out.println("canvas("+canvas.getWidth()+","+canvas.getHeight()+")");
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLUE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public MapNode(double cellSize) {
        AnchorPane.setTopAnchor(this, 0d);
        AnchorPane.setBottomAnchor(this, 0d);
        AnchorPane.setLeftAnchor(this, 0d);
        AnchorPane.setRightAnchor(this, 0d);
       
        canvasWidthProperty.addListener(resizeCanvas);
        canvasHeightProperty.addListener(resizeCanvas);
        cellSizeProperty.addListener(resizeCanvas);
        
        cellSizeProperty.set(cellSize);
        this.setContent(canvas);
    }
    
    
    
}
