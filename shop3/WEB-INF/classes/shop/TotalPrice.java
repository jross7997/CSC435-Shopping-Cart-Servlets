/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

/**
 *
 * @author Justin
 */
public class TotalPrice {
    //Used to show the total price in the cart
    private double total =0;
    
    public TotalPrice(double p){
        total = p;
    }
    
    public double getTotal(){
        return total;
    }
    
}
