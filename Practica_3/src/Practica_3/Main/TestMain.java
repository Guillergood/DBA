/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Main;

import Practica_3.Util.Matrix;


/**
 *
 * @author Bruno García Trípoli
 */
public class TestMain {    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Matrix<Double> prueba = new Matrix(11, 11, 0);
        
        prueba.ranged_foreach(5, 5, 3, (x,y,v)->{
            return 1.0;
        });
        
        prueba.foreach(new Matrix.Operator<Double>() {
            @Override
            public Double operate(int x, int y, Double v) {
                synchronized(this){
                
                }
                
                return v;
            }
        });
    }
}
