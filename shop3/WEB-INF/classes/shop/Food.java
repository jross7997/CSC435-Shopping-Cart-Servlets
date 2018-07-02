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
public class Food {
    //Item information

    private String name;
    //private String brand;
    private String calories;
    private String fat;
    private String sodium;
    private double price = 0;
    private double salesPrice = 0;
    private int ID = 0;

    public Food() {
        name = calories = fat = sodium = null;
        price = 0;
        ID = 0;
    }

    public Food(String n, String c, String f, String s,int id) {
        name = n;
        //brand = b;
        calories = c;
        if (f == null) {
            fat = "0";
        } else {
            fat = f;
        }
        sodium = s;
        price =(Double.parseDouble(calories)+ Double.parseDouble(sodium) + Double.parseDouble(fat))/100;
        salesPrice = price/2;
        ID = id;
    }

    public String getName() {
        return name;
    }

//    public String getBrand() {
//        return brand;
//    }
    public String getCalories() {
        return calories;
    }

    public String getFat() {
        return fat;
    }

    public String getSodium() {
        return sodium;
    }

    public double getPrice() {
        return price;
    }
    public double getSalesPrice() {
        return salesPrice;
    }
    public int getID() {
        return ID;
    }

    public void setName(String n) {
        name = n;
    }

//    public void setBrand(String b) {
//        brand = b;
//    }
    public void setCalories(String c) {
        calories = c;
    }

    public void setFat(String f) {
        fat = f;
    }

    public void setSodium(String s) {
        sodium = s;
    }

    public void setPrice(double p) {
        price = p;
        salesPrice = price/2;
    }

    public void setID(int i) {
        ID = i;
    }

    public String toString() {
        return "[" + ID + " " + name + " " + price + "]";
    }
}
