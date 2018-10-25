/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.patchworkgalaxy.display.models;

import java.util.ArrayList;

/**
 *
 * @author Beverly_hills
 */
public class MemoryOfShipHashCodes {
    public static ArrayList<ParticleEmitterReverseFireball> FireballsForMoving = new ArrayList<>();
    public static ArrayList<Integer> HashCodesAllShips = new ArrayList<>();
    public static ArrayList<Integer> HashCodesAllShipsNewest = new ArrayList<>();
    public static void clearNewest(){
       HashCodesAllShipsNewest.clear(); 
    }
    public static void addShip(int hashcode){
        if(HashCodesAllShips.indexOf(hashcode)==-1){
        HashCodesAllShips.add(new Integer(hashcode));
        }
    }
    public static void addShipToNewest(int hashcode){
        if(HashCodesAllShipsNewest.indexOf(hashcode)==-1){
        HashCodesAllShipsNewest.add(new Integer(hashcode));
        }
    }
}
