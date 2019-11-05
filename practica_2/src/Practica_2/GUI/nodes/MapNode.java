/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2.GUI.nodes;

import Practica_2.Agent;
import Practica_2.interfaces.Observer;
import com.sun.javafx.geom.Rectangle;
import com.sun.javafx.geom.Vec3d;
import java.util.HashMap;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;

/**
 *
 * @author brbco
 */
public class MapNode extends ScrollPane implements Observer<Agent> {

    Canvas canvas = new Canvas();
    Canvas canvas2 = new Canvas();
    public final DoubleProperty canvasWidthProperty = new SimpleDoubleProperty(null, "width", 2.0);       
    public final DoubleProperty canvasHeightProperty = new SimpleDoubleProperty(null, "height", 2.0); 
    public final DoubleProperty cellSizeProperty = new SimpleDoubleProperty(null, "height", 50.0); 
    private boolean isInitialized = false;
    private Pair<Integer,Integer> oldPOS = null;
    private HashMap<Pair<Integer,Integer>,Integer> drawedCells = new HashMap();
    private Pair<Integer,Integer> colorLerpLimits;
    private Pane content = new Pane();
    
    
    ChangeListener<Number> resizeCanvas = new ChangeListener<Number>() {
        @Override
        public void changed(ObservableValue<? extends Number> obs, Number oldValue, Number newValue) {
            double cellSize = cellSizeProperty.get();
            double width = canvasWidthProperty.get() * cellSize;
            double height = canvasHeightProperty.get() * cellSize;
            
            canvas.widthProperty().set(width);
            canvas.heightProperty().set(height);
            canvas2.widthProperty().set(width);
            canvas2.heightProperty().set(height);
        }
    };
    
    @Override
    public void update(Agent o, Object data) {
        Object parsedData[] = (Object[]) data;  
        Vec3d gps = (Vec3d) parsedData[2];
        int radar[][] = (int[][]) parsedData[3];        
        int magnetic[][] = (int[][]) parsedData[4];        
        
        if(!isInitialized)
        {
            Pair<Integer,Integer> mapSize = (Pair<Integer,Integer>) parsedData[0];
            colorLerpLimits = (Pair<Integer,Integer>) parsedData[1];
            
            double mapWidth = mapSize.getKey().doubleValue();
            double mapHeight = mapSize.getValue().doubleValue();
            this.canvasWidthProperty.set(mapWidth);
            this.canvasHeightProperty.set(mapHeight);
            
            double cellSize = cellSizeProperty.get();
            double wp = (gps.x+1)*cellSize/(mapWidth*cellSize);
            double hp = (gps.y+1)*cellSize/(mapHeight*cellSize);
            
            Platform.runLater(()->{
                this.setHvalue(wp);
                this.setVvalue(hp);  
            });                      
            
            isInitialized = true;
        }               
        
        GraphicsContext gc = canvas.getGraphicsContext2D();
        GraphicsContext gc2 = canvas2.getGraphicsContext2D();
        
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                int x = (int) (gps.x - (5-i));
                int y = (int) (gps.y - (5-j));
                Pair<Integer,Integer> pos = new Pair(x,y);
                
                if(i==5 && j==5)
                {
                    if(oldPOS!=null)
                    {   
                        double cellSize = this.cellSizeProperty.get();
                        double cx = x*cellSize + cellSize/2;
                        double cy = y*cellSize + cellSize/2;
                        double old_cx = oldPOS.getKey()*cellSize + cellSize/2;
                        double old_cy = oldPOS.getValue()*cellSize + cellSize/2;
                        
                        gc2.setStroke(Color.GREEN);
                        gc2.setLineWidth(2);
                        
                        int oldHeight = drawedCells.get(oldPOS);
                        fillCell(oldPOS.getKey(),oldPOS.getValue(),gc,oldHeight);
                        gc2.strokeLine(old_cx,old_cy, cx, cy);
                    }
                    fillCell(x,y,gc,Color.GREEN);
                    drawedCells.put(pos, radar[i][j]);
                    oldPOS = new Pair(x,y);                    
                }
                else if(!drawedCells.containsKey(pos))
                {
                    try{
                        int height = radar[i][j];
                        drawedCells.put(pos, height);
                        if(magnetic[i][j]!=0)
                            fillCell(x,y,gc,Color.YELLOW);
                        else
                            fillCell(x,y,gc,height);
                    }catch(IndexOutOfBoundsException ex){
                        continue;
                    }                    
                }
                
            }
        }
       
    }

    public MapNode(double cellSize) {
        AnchorPane.setTopAnchor(this, 0d);
        AnchorPane.setBottomAnchor(this, 0d);
        AnchorPane.setLeftAnchor(this, 0d);
        AnchorPane.setRightAnchor(this, 0d);
        this.setStyle("-fx-border-color: red;");
       
        canvasWidthProperty.addListener(resizeCanvas);
        canvasHeightProperty.addListener(resizeCanvas);
        cellSizeProperty.addListener(resizeCanvas);        
        cellSizeProperty.set(cellSize);     
       
        content.getChildren().addAll(canvas,canvas2);
        this.setContent(content);
    }
    
    private Point2D getCell(int x,int y){
        double width = canvasWidthProperty.get();
        double height = canvasHeightProperty.get();
        double cellSize = cellSizeProperty.get();
        if( (x<0 || x>width) || (y<0 || y>height) )
            throw new IndexOutOfBoundsException();
        
        Point2D start = new Point2D(x*cellSize,y*cellSize);
        //Point2D end = new Point2D(start.getX()+cellSize,start.getY()+cellSize);        
        return start;
    }    
    
    private void fillCell(int x,int y,GraphicsContext gc, int height)
    {
        if(height==0)
        {
            fillCell(x,y,gc,Color.RED);
            return;
        }
        
        int min = colorLerpLimits.getKey();
        int max = colorLerpLimits.getValue();
        double lerpValue = ((double)(height-min))/((double)(max-min));
        lerpValue = Math.max(0, Math.min(lerpValue, 1)); //Clamp 0 1
                    
        Color color = Color.grayRgb((int) (lerpValue*255));
        fillCell(x,y,gc,color);
    }
    
    private void fillCell(int x,int y,GraphicsContext gc,Color color)
    {
        Point2D cellPos = getCell(x,y);
        double cellSize = cellSizeProperty.get();        
              
        gc.setFill(color);
        gc.fillRect(cellPos.getX(), cellPos.getY(),
                cellSize, cellSize);
    }    
    
    public void clear(){
        isInitialized = false;
        oldPOS = null;
        drawedCells.clear();
        GraphicsContext gc = canvas.getGraphicsContext2D();
        GraphicsContext gc2 = canvas2.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc2.clearRect(0, 0, canvas2.getWidth(), canvas2.getHeight());
    }
}
