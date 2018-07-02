/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Justin
 */
public class LoginServlet extends HttpServlet {

    final String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";

    @Override
    //Check to see if logged in
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        //Startup jdbc
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found ERROR" + ex));
            dispatcher.forward(request, response);
        }

        //Initialize Connections and Statement
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {

            HttpSession session = request.getSession();
            
            //If not logged in, print false
            if (session.getAttribute("isLoggedIn") == null) {
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                request.setAttribute("print", false);
                dispatcher.forward(request, response);
            } else {
                Helper helper = new Helper();
                QueryHelper query = new QueryHelper();
                //If logged in, print all of the users
                Class.forName("com.mysql.jdbc.Driver");
                //Get all of the users from the MySQL database
                ResultSet rset = query.selectAllLogins(conn, stmt);
                ArrayList<User> users = helper.prepareUserList(rset);
                //Print the array List               
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                request.setAttribute("print", users);
                dispatcher.forward(request, response);
            }
        } catch (ServletException | IOException | ClassNotFoundException | SQLException | NumberFormatException e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Servlet or IO or Class Not Found or SQL or Number Format ERROR:" + e));
            dispatcher.forward(request, response);
        }
    }

    //Log-In
    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        
        //startup jdbc
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found ERROR" + ex));
            dispatcher.forward(request, response);
        }

        //Check to see if the user exists
        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {
            //send in the query of the username
            String user = request.getParameter("user");
            HttpSession session = request.getSession();

            if (user != null) {
                if (user.contains("-")) {
                    user = user.replace("-", " ");
                }
                Class.forName("com.mysql.jdbc.Driver");
                Helper helper = new Helper();
                QueryHelper query = new QueryHelper();
                //Send a mySQL command to get each of the users that have the same name
                query.setUserName(user);
                ResultSet rset = query.selectUserByName(conn, stmt);

                String foundUser = null;
                String foundid = null;
                User loggedIn = null;
                while (rset.next()) {
                    foundUser = rset.getString("username");
                    foundid = rset.getString("id");
                }

                //check if the user exists, if it does, update the session with the user info
                if (foundUser != null) {
                    if (user.equals(foundUser)) {
                        synchronized (session) {
                            loggedIn = new User(foundUser, Integer.parseInt(foundid));
                            session.setAttribute("isLoggedIn", loggedIn);
                        }
                    }
                }
                
                //transfer the cart from the session to the database
                ArrayList<Food> cart = (ArrayList<Food>)session.getAttribute("cart");
                if(cart != null){
                    for(Food f: cart){
                        query.addCart(conn, stmt, f, loggedIn.getId());
                    }   
                }
                
                //If logged in, print your user info
                if (loggedIn != null) {
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                    request.setAttribute("print", loggedIn);
                    dispatcher.forward(request, response);
                    // if not, print "no such user"
                } else {
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                    request.setAttribute("print", "No Such User");
                    dispatcher.forward(request, response);
                }
            } else {
                //if the format was wrong, alert the user
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                request.setAttribute("print", ("Wrong Format. must be '?user = ...' "));
                dispatcher.forward(request, response);
            }
        } catch (ClassNotFoundException | SQLException | NumberFormatException e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found or SQL or Number Format ERROR: " + e));
            dispatcher.forward(request, response);
        }

    }

//LogOut
    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try {
            //If the session is active, invalidate it
            HttpSession session = request.getSession();
            if (session != null) {
                synchronized (session) {
                    session.invalidate();
                }
            }
            //alert the user that they logged out
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Logged Out"));
            dispatcher.forward(request, response);
        } catch (ServletException | IOException e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Servlet or IO ERROR: " + e));
            dispatcher.forward(request, response);
        }
    }

//Sign Up
    @Override
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
            //Class.forName("com.mysql.jdbc.Driver");
            Helper helper = new Helper();
            //Sign up by sending in a user parameter
            String name = request.getParameter("user");
            //Replace all "-" with spaces
            if (name != null) {
                if (name.contains("-")) {
                    name = name.replace("-", " ");
                }
                //Get each user with the same name
                QueryHelper query = new QueryHelper();
                query.setUserName(name);
                ResultSet rset = query.selectUserByName(conn, stmt);
                boolean exists = false;
                while (rset.next()) {
                    exists = true;
                }
                //If the user doesn't exist, assign it a new id and add to the database
                if (!exists) {
                    rset = query.selectAllLogins(conn, stmt);
                    int id = helper.getMaxID(rset);
                    id++;
                    //Random r = new Random();
                    // int id = r.nextInt(501);
                    query.setUserId(id);
                    query.insertUser(conn, stmt);
                    User add = new User(name, id);
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                    request.setAttribute("print", add);
                    dispatcher.forward(request, response);
                    //If the user does exist, inform the user
                } else {
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                    request.setAttribute("print", "Username Already Exists");
                    dispatcher.forward(request, response);
                }
            } else {
                //Print Wrong Format Message
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                request.setAttribute("print", ("Wrong Format. must be '?user = ...' "));
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found or SQL or Number Format or JSON ERROR" + e));
            dispatcher.forward(request, response);
        }
    }
}
