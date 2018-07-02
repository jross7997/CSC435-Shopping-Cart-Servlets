/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 *
 * @author Justin
 */
public class Helper {
//Helper has some commonly used methods

    public Helper() {

    }

    //Print the Shop
    public ArrayList<Food> prepareItemList(ResultSet rset) throws IOException {
        Food temp;
        ArrayList<Food> itemList = new ArrayList<Food>();
        //get each item in the database and add it to an arraylist
        try {
            while (rset.next()) {
                String id = rset.getString("id");
                String name = rset.getString("Item_name");
                String calories = rset.getString("calories");
                String fat = rset.getString("fat");
                String sodium = rset.getString("sodium");
                temp = new Food(name, calories, fat, sodium, Integer.parseInt(id));
                itemList.add(temp);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        //Print the arraylist
        return itemList;
    }

    //Print an item by it's ID
    public Food prepareItem(ResultSet rset) throws IOException {
        Food inCart = null;
        try {
            while (rset.next()) {
                String id = rset.getString("id");
                String name = rset.getString("Item_name");
                //String price = rset.getString("price");
                String calories = rset.getString("calories");
                String fat = rset.getString("fat");
                String sodium = rset.getString("sodium");
                inCart = new Food(name, calories, fat, sodium, Integer.parseInt(id));
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        return inCart;
    }

    //Return the list of users.
    public ArrayList<User> prepareUserList(ResultSet rset) throws IOException {
        User temp;
        ArrayList<User> userList = new ArrayList<User>();
        //get each item in the database and add it to an arraylist
        try {
            while (rset.next()) {
                String id = rset.getString("id");
                String name = rset.getString("username");
                temp = new User(name, Integer.parseInt(id));
                userList.add(temp);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        //Print the arraylist
        return userList;
    }

    //Get the data from an API
    public ArrayList<Food> getAPIFood() {
        ArrayList<Food> food = new ArrayList<Food>();
        String myURL = "https://api.nutritionix.com/v1_1/search/?results=0%3A20&cal_min=0&cal_max=50000&fields=item_name%2Cnf_total_fat%2Cnf_calories%2Cnf_sodium&appId=88cf0044&appKey=90cbe0b7c7beeb26b938584e189be6fd";
        try {
            HttpClient client = HttpClients.custom().setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build()).build();
            //  HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(myURL);
            HttpResponse response = client.execute(request);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            // Get the response
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            CallInfo temp = new CallInfo();
            temp = mapper.readValue(new URL(myURL), CallInfo.class);
            //System.out.println(temp.getTotal());
            ArrayList<FoodInfo> hits = temp.getHitList();
            Fields t = new Fields();
            Food tempFood = new Food();

            //Put all of the food in an ArrayList
            for (FoodInfo f : hits) {
                t = f.getFields();
                String itemName = t.getItemName();
                tempFood.setName(itemName);

                String calories = t.getCalories();
                tempFood.setCalories(calories);

                String fat = t.getFat();
                tempFood.setFat(fat);

                String sodium = t.getSodium();
                tempFood.setSodium(sodium);
                food.add(tempFood);
            }

        } catch (IOException | UnsupportedOperationException e) {
            System.out.println(e);
        }
        return food;
    }
    
    //Prepare the cart to be printed
    public ArrayList<Food> prepareCart(ResultSet rset){
         Food temp;
        ArrayList<Food> itemList = new ArrayList<Food>();
        //get each item in the database and add it to an arraylist
        try {
            while (rset.next()) {
                String id = rset.getString("Item_id");
                String name = rset.getString("Item_name");
                String calories = rset.getString("calories");
                String fat = rset.getString("fat");
                String sodium = rset.getString("sodium");
                temp = new Food(name, calories, fat, sodium, Integer.parseInt(id));
                itemList.add(temp);
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
        //Print the arraylist
        return itemList;
    }

    //Get the max ID of the items in the database
    public int getMaxID(ResultSet rset) {
        int max = 0;
        try {
            while (rset.next()) {
                String id = rset.getString("id");
                int temp = Integer.parseInt(id);
                if(temp > max){
                    max = temp;
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
            return max;
        }

    }

