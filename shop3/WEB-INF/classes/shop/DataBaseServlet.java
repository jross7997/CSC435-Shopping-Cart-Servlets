/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/**
 *
 * @author Justin
 */
public class DataBaseServlet extends HttpServlet {

    final String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";
    
//Print shop
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found ERROR" + ex));
            dispatcher.forward(request, response);
        }
        
        //Print the entire database
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {
            Helper helper = new Helper();
            QueryHelper query = new QueryHelper();
            ServletContext context = getServletContext();
            
            //Get all the items from the database
            ResultSet rset = query.selectAll(conn, stmt);
            ArrayList<Food> items = helper.prepareItemList(rset);
            //print JSON
            RequestDispatcher dispatcher = context.getRequestDispatcher("/print");
            request.setAttribute("print", items);
            dispatcher.forward(request, response);

        } catch (Exception e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ( e));
            dispatcher.forward(request, response);
        }
    }

    //Add to DataBase
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found ERROR" + ex));
            dispatcher.forward(request, response);
        }

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {

            QueryHelper query = new QueryHelper();
            //Get info from the URI
            String URI = request.getRequestURI();
            String[] tokens = URI.split("/");
            //Replace all "-" with a space
            for (int i = 0; i < tokens.length; i++) {
                if (tokens[i].contains("-")) {
                    tokens[i] = tokens[i].replace("-", " ");
                }
            }
            //Get the information
            // /data/name/calories/fat/sodium
            String name = tokens[tokens.length - 4];
            String calories = tokens[tokens.length - 3];
            String fat = tokens[tokens.length - 2];
            String sodium = tokens[tokens.length - 1];
            //Send in the mySQL command and execute it
            ResultSet rset = query.selectAll(conn, stmt);
            //Get the highest item ID
            int newId = 0;
            while (rset.next()) {
                String id = rset.getString("id");
                int temp = Integer.parseInt(id);
                if (temp > newId) {
                    newId = temp;
                }
            }
            newId += 1;
            //create a new Item with the new Id
            Food add = new Food(name, calories, fat, sodium, newId);
            //update Query object
            query.setId(newId);
            query.setName(name);
            query.setCaloiries(calories);
            query.setFat(fat);
            query.setSodium(sodium);
            //Insert the new item to the database
            query.insertShop(conn, stmt);
            
            //Print the info of the item that was just added
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", add);
            dispatcher.forward(request, response);

        } catch (Exception e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found or SQL or Number Format or JSON ERROR" + e));
            dispatcher.forward(request, response);
        }

    }

    //Delete an Item
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found ERROR" + ex));
            dispatcher.forward(request, response);
        }

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {
            //Class.forName("com.mysql.jdbc.Driver");
            Helper helper = new Helper();
            QueryHelper query = new QueryHelper();
            //Get the id of the item to be deleted
            String URI = request.getRequestURI();
            String[] tokens = URI.split("/");
            String delId = tokens[tokens.length - 1];
            ResultSet rset = query.selectById(conn, stmt, Integer.parseInt(delId));
            Food del = helper.prepareItem(rset);
            //Delete the item
            query.deleteById(conn, stmt, Integer.parseInt(delId));
            //go to View
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", del);
            dispatcher.forward(request, response);

        } catch (SQLException | NumberFormatException | JsonProcessingException e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found or SQL or Number Format or JSON ERROR" + e));
            dispatcher.forward(request, response);
        }

    }

    //Update an Item
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found ERROR" + ex));
            dispatcher.forward(request, response);
        }

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {

            Helper helper = new Helper();
            //get the info for the item thats being updated
            String URI = request.getRequestURI();
            String[] tokens = URI.split("/");
            //get the new name, price, and the id to write over
            // /data/Id-to-update/new-name/new-base-price/new-calories/new-fat/new-sodium
            String updateId = tokens[tokens.length - 5];
            String newname = tokens[tokens.length - 4];
            String newCalorie = tokens[tokens.length - 3];
            String newFat = tokens[tokens.length - 2];
            String newSodium = tokens[tokens.length - 1];
            if (newname.contains("-")) {
                newname = newname.replace("-", " ");
            }
            Food f = new Food(newname, newCalorie, newFat, newSodium, Integer.parseInt(updateId));
            QueryHelper query = new QueryHelper(f.getID(), f.getName(), f.getCalories(), f.getFat(), f.getSodium());
            //Update the database
            query.updateShop(conn, stmt);
            //Print the info of the item that's just been updated
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", f);
            dispatcher.forward(request, response);

        } catch (SQLException | NumberFormatException | JsonProcessingException e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found or SQL or Number Format or JSON ERROR" + e));
            dispatcher.forward(request, response);
        }
    }

}
