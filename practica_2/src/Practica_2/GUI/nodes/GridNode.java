/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2.GUI.nodes;

import Practica_2.Agent;
import Practica_2.interfaces.Observer;
import com.sun.istack.internal.Nullable;
import java.util.Timer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;

/**
 *
 * @author Bruno Garcia
 */
public class GridNode extends ScrollPane implements Observer<Agent>{
    private final int numCols,numRows;
    private Double cellSize = Double.NaN;
    private FitMode fitMode = FitMode.NONE;
    protected GridPane gridPane;

    @Override
    public void update(Agent o, Object data) {
        System.out.println("me actualizo");
    }

    public enum FitMode{
        VERTICAL,
        HORIZONTAL,
        NONE
    };
    
    public GridNode(int numCols,int numRows) {
        this(numCols,numRows,FitMode.VERTICAL,Double.NaN);        
    }
    
    public GridNode(int numCols,int numRows, FitMode fitMode) {
        this(numCols,numRows,fitMode,Double.NaN);        
    }
    
    public GridNode(int numCols,int numRows,double cellSize){
        this(numCols,numRows,FitMode.NONE,cellSize);
    }
    
    private GridNode(int numCols,int numRows,FitMode fitModeIn,double cellSize){
        super();
        this.numCols = numCols;
        this.numRows = numRows;   
        this.fitMode = fitModeIn;
        this.cellSize = cellSize;        
        
        AnchorPane.setTopAnchor(this, 0d);
        AnchorPane.setLeftAnchor(this, 0d);
        AnchorPane.setRightAnchor(this, 0d);
        AnchorPane.setBottomAnchor(this, 0d);
        
        FlowPane flowPane = new FlowPane(); 
        this.heightProperty().addListener((obs,oldValue,newValue)->flowPane.minHeightProperty().set((double)newValue - 10));
        this.widthProperty().addListener((obs,oldValue,newValue)->flowPane.minWidthProperty().set((double)newValue - 10));
        
        gridPane = new GridPane();
        gridPane.gridLinesVisibleProperty().set(true);
        gridPane.alignmentProperty().set(Pos.CENTER);
        
        ColumnConstraints column_constraint = new ColumnConstraints();
        column_constraint.setHalignment(HPos.CENTER);
        RowConstraints row_constraint = new RowConstraints();
        row_constraint.setValignment(VPos.CENTER);
        
        ChangeListener<Number> cl = (obs,oldValue,newValue)->{ 
            double height=0,width=0;  
            
            switch (this.fitMode) {
                case VERTICAL:
                    height = this.heightProperty().get();
                    this.cellSize = height/numRows;
                    width = this.cellSize*numCols;  
                    break;
                case HORIZONTAL:
                    width = this.widthProperty().get();
                    this.cellSize = width/numRows;
                    height = this.cellSize*numRows;
                break;
                case NONE:                    
                    if(Double.isNaN(this.cellSize))
                        this.cellSize = 50d; //default value
                    
                    height = this.cellSize*numRows;
                    width = this.cellSize*numCols;  
                break;
                default:
                    throw new AssertionError();
            }    
            
            row_constraint.setPrefHeight(this.cellSize);
            column_constraint.setPrefWidth(this.cellSize);
            gridPane.setPrefSize(width, height);            
        };
        
        this.heightProperty().addListener(cl);
        this.widthProperty().addListener(cl);
        
        for (int i = 0; i < numCols; i++)            
            gridPane.getColumnConstraints().add(column_constraint);
        for (int i = 0; i < numRows; i++)   
            gridPane.getRowConstraints().add(row_constraint);
        
        switch (this.fitMode) {
            case VERTICAL:
                this.setFitToHeight(true);                
                this.setFitToWidth(false);
                flowPane.setAlignment(Pos.CENTER);
                break;
            case HORIZONTAL:
                this.setFitToWidth(false);
                this.setFitToWidth(true);
                flowPane.setAlignment(Pos.CENTER);
            break;
            case NONE:
                this.setFitToWidth(false);
                this.setFitToWidth(false);
                flowPane.setAlignment(Pos.CENTER);
                break;
            default:
                throw new AssertionError();
        }        
        
        flowPane.getChildren().add(gridPane);
        gridPane.setStyle("-fx-background-color: #666;");
        this.setContent(flowPane);
        //this.getChildren().add(gridPane);
        this.autosize();
    }
    
    private void internalSetCell(int i, int j,@Nullable Node child,@Nullable Tooltip tooltip,Color bg,@Nullable GridNode.IsHoverMethod method){
        if(i>=numCols || i<0)
            throw new IndexOutOfBoundsException();
        if(j>=numRows || j<0)
            throw new IndexOutOfBoundsException();
        
        FlowPane pane = new FlowPane();
        pane.setAlignment(Pos.CENTER);
        if(child!=null)
            pane.getChildren().add(child);
        
        if(tooltip!=null)
        {
            tooltip.autoHideProperty().set(true);
            Tooltip.install(pane, tooltip);
        }
        
        
        String colorHex = getColorHex(bg);
        String style = String.format("-fx-background-color: #232323, %s; -fx-background-insets: 0, 0 0 2 2 ;",colorHex);
        pane.setStyle(style);
        
        pane.hoverProperty().addListener((ObservableValue<? extends Boolean> obs,Boolean oldValue,Boolean newValue)->{           
            if(method!=null)
                method.invoke(newValue,pane);
            if(tooltip!=null)
            {
                if(tooltip.showingProperty().get())
                    tooltip.hide();
            }                       
        });
        
        if(tooltip!=null)
            pane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    double x = mouseEvent.getScreenX() + 10;
                    double y = mouseEvent.getScreenY() + 10;
                    tooltip.show(pane, x, y);

                    Timer t = new java.util.Timer();                
                    t.schedule( 
                            new java.util.TimerTask() {
                                @Override
                                public void run() {
                                    Platform.runLater(()->tooltip.hide());                                
                                    t.cancel();
                                }
                            }, 
                            4000 
                    );
                }
            });
        
        this.gridPane.add(pane, i, j);        
    }
    
    public void setCell(int i, int j,@Nullable Node child,@Nullable Tooltip tooltip,Color bg){
        this.internalSetCell(i, j, child, tooltip, bg,null);
    }
    
    public void setCell(int i, int j,@Nullable Node child,@Nullable Tooltip tooltip,Color bg,Color hl_bg){
        String colorHex = getColorHex(bg);
        String style = String.format("-fx-background-color: #232323, %s; -fx-background-insets: 0, 0 0 2 2 ;",colorHex);
        String colorHighlightHex =  getColorHex(hl_bg);        
        String styleHighlight = String.format("-fx-background-color: #232323, %s; -fx-background-insets: 0, 1 1 3 3",colorHighlightHex);
                
        this.internalSetCell(i, j, child, tooltip, bg,(Boolean isHover,Node node)->{
            if(isHover)
                node.styleProperty().set(styleHighlight);
            else
                node.styleProperty().set(style);
        });
    }
    
    private String getColorHex(Color color){
        String colorHex = String.format( "#%02X%02X%02X",
            (int)( color.getRed() * 255 ),
            (int)( color.getGreen() * 255 ),
            (int)( color.getBlue() * 255 ) );
        return colorHex;
    }
    
    private String getColorHexHighlight(Color color,int offset){
        String colorHex = String.format( "#%02X%02X%02X",
            Math.min((int)( color.getRed() * 255 + offset),255),
            Math.min((int)( color.getGreen() * 255 + offset),255),
            Math.min((int)( color.getBlue() * 255 + offset),255));
        return colorHex;
    }
    
    private static interface IsHoverMethod{
        void invoke(Boolean isHover,Node container);    
    }
}
