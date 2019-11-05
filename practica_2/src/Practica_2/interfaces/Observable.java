/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Practica_2.interfaces;

/**
 *
 * @author brbco
 */
public interface Observable {
    void addOvserver(Observer o);
    void notifyObservers();
}
