/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author Justin
 */
public class QueryHelper {
//A class who's sole purpose is to make calls to the SQL Database
    private int id = 0;
    private String name = null;
    private String strPrice = null;
    private String strSalePrice = null;
    private String calories = null;
    private String fat = null;
    private String sodium = null;
    private String uName = null;
    private int userId = 0;

    public QueryHelper() {
    }

    public QueryHelper(int i, String n, String c, String f, String s) {
        id = i;
        name = n;
        calories = c;
        fat = f;
        sodium = s;
    }
    
//Setters
    public void setId(int i) {
        this.id = i;
    }

    public void setName(String n) {
        this.name = n;
    }

    public void setStrPrice(String p) {
        strPrice = p;
        strSalePrice = Double.toString((Double.parseDouble(p)) / 2);
    }

    public void setStrSalesPrice(String p) {
        strSalePrice = p;
    }

    public void setCaloiries(String c) {
        calories = c;
    }

    public void setFat(String f) {
        fat = f;
    }

    public void setSodium(String s) {
        sodium = s;
    }

    public void setUserName(String u) {
        uName = u;
    }

    public void setUserId(int i) {
        userId = i;
    }

    //Delete the entire Database
    public void deleteAPIData(Connection conn, Statement stmt) throws SQLException{
        String sqlStr = "delete from food";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.execute();
    }
    
    //Populate the Database from the API
    public void insertAPIData(Connection conn, Statement stmt, ArrayList<Food> food) throws SQLException {
        String sqlStr = "insert into food values (?,?,?,?,?)";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        for (Food f : food) {

            prep.setInt(1, f.getID());
            prep.setString(2, f.getName());
            prep.setString(3, f.getCalories());
            prep.setString(4, f.getFat());
            prep.setString(5, f.getSodium());
            prep.execute();
        }
    }

    //Insert a new item into the shop
    public void insertShop(Connection conn, Statement stmt) throws SQLException {
        String sqlStr = "insert into food values (?,?,?,?,?)";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setInt(1, id);
        prep.setString(2, name);
        prep.setString(3, calories);
        prep.setString(4, fat);
        prep.setString(5, sodium);
        prep.execute();
    }

    //Get very item from the food database
    public ResultSet selectAll(Connection conn, Statement stmt) throws SQLException {
        String sqlStr = "select * from food";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        ResultSet rset = prep.executeQuery();
        return rset;
    }

    //Select a food item by it's ID
    public ResultSet selectById(Connection conn, Statement stmt, int i) throws SQLException {
        String sqlStr = "select * from food where id =?";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setInt(1, i);
        ResultSet rset = prep.executeQuery();
        return rset;
    }

    //Delete an item from the database by ID
    public void deleteById(Connection conn, Statement stmt, int i) throws SQLException {
        String sqlStr = "delete from food where id=?";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setInt(1, i);
        prep.execute();
    }

    //Update an item in the shop
    public void updateShop(Connection conn, Statement stmt) throws SQLException {
        String sqlStr = "update food set Item_name=?,calories=?, fat=?,sodium=? where id =?";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setString(1, name);
        prep.setString(2, calories);
        prep.setString(3, fat);
        prep.setString(4, sodium);
        prep.setInt(5, id);
        prep.executeUpdate();
    }
    
    //Get all of the logins
    public ResultSet selectAllLogins(Connection conn, Statement stmt) throws SQLException {
        String sqlStr = "select * from logins";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        ResultSet rset = prep.executeQuery();
        return rset;
    }
    
    //Get a specific login
    public ResultSet selectUserByName(Connection conn, Statement stmt) throws SQLException {
        String sqlStr = "select * from logins where username =?";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setString(1, uName);
        ResultSet rset = prep.executeQuery();
        return rset;
    }

    //add a new user to the login database
    public void insertUser(Connection conn, Statement stmt) throws SQLException {
        String sqlStr = "insert into logins values (?, ?)";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setInt(1, userId);
        prep.setString(2, uName);
        prep.execute();
    }

    //Add an item to the cart database
    public void addCart(Connection conn, Statement stmt, Food f, int i) throws SQLException {
        String sqlStr = "insert into cart values (?, ?, ?)";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setInt(1, i);
        prep.setInt(2, f.getID());
        prep.setString(3, f.getName());
        prep.execute();
    }

    //Delete an item from the cart database
    public void deleteCartById(Connection conn, Statement stmt, int i) throws SQLException {
        String sqlStr = "delete from cart where Item_id=? LIMIT 1";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setInt(1, i);
        prep.execute();
    }
    
    //Select an item from the cart
    public ResultSet selectCartById(Connection conn, Statement stmt, int i) throws SQLException {
        String sqlStr = "select * from cart where Item_id =?";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setInt(1, i);
        ResultSet rset = prep.executeQuery();
        return rset;
    }
    
    //Inner join the Logins, food, and cart databases.
    public ResultSet cartInnerJoin(Connection conn, Statement stmt, int uId) throws SQLException {
        String sqlStr = "select logins.username,cart.User_id,cart.Item_id,food.Item_name,food.calories,";
        sqlStr = sqlStr + "food.fat,food.sodium from logins inner join cart on logins.";
        sqlStr = sqlStr + "id = ? and cart.User_id = ? inner join food on cart.Item_id = food.id";
        PreparedStatement prep = conn.prepareStatement(sqlStr);
        prep.setInt(1, uId);
        prep.setInt(2, uId);
        ResultSet rset = prep.executeQuery();
        return rset;
    }

}
