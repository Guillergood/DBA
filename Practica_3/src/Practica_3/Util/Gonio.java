/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_3.Util;

/**
 *
 * @author Alberto
 */
public class Gonio {
    public double distance;
    public double angle;
    
    public Gonio(double distance, double angle){
        this.distance = distance;
        this.angle = angle;
    }
    //TODO no es necesario puesto que arriba siempre se inicializa, asi que no
    //es necesario comprobar que está vacio.
    /*public boolean isEmpty(){
        return ;
    }*/

    @Override
    public String toString() {
        return "Gonio{" + "distance=" + distance + ", angle=" + angle + '}';
    }
    
}
