/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

import java.io.*;
import java.util.ArrayList;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

/**
 *
 * @author Justin
 */
public class ShoppingServlet extends HttpServlet {

    final String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";
//Print Shop

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
            //Get the Database
            ResultSet rset = query.selectAll(conn, stmt);
            ArrayList<Food> items = helper.prepareItemList(rset);

            RequestDispatcher dispatcher = context.getRequestDispatcher("/print");
            request.setAttribute("print", items);
            dispatcher.forward(request, response);

        } catch (Exception e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("SQL ERROR" + e));
            dispatcher.forward(request, response);
        }

    }

//Add to Cart
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        final String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Class Not Found ERROR" + ex));
            dispatcher.forward(request, response);
        }

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {
            HttpSession session = request.getSession();
            //Get URI to get the details of the item being added to your cart
            String URI = request.getRequestURI();
            Helper helper = new Helper();
            //Get the item being added

            QueryHelper query = new QueryHelper();

            String[] tokens = URI.split("/");
            String cartid = tokens[tokens.length - 1];

            ResultSet rset = query.selectById(conn, stmt, Integer.parseInt(cartid));
            Food inCart = helper.prepareItem(rset);
            //Item inCart = helper.searchById(URI, request, response, context);
            if (session.getAttribute("isLoggedIn") == null) {
                synchronized (session) {
                    //Add it to your session
                    ArrayList<Food> cart = (ArrayList<Food>) session.getAttribute("cart");
                    if (cart == null) {
                        cart = new ArrayList<Food>();
                        cart.add(inCart);
                        session.setAttribute("cart", cart);

                    } else {
                        cart.add(inCart);
                        session.setAttribute("cart", cart);

                    }
                }
            } else {
                User sess = (User)session.getAttribute("isLoggedIn");
                query.addCart(conn, stmt, inCart, sess.getId());
            }

            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", inCart);
            dispatcher.forward(request, response);
        } catch (Exception e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Servlet or IO ERROR: " + e));
            dispatcher.forward(request, response);
        }
    }
}
