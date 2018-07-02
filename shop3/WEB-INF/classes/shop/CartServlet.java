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
public class CartServlet extends HttpServlet {

    final String localHost = "jdbc:mysql://localhost:3306/itemshop?useSSL=false";

    //get Cart
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

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {
            HttpSession session = request.getSession();
            ArrayList<Food> foodList = new ArrayList<Food>();
            TotalPrice cost = null;

            //If you're not logged in, check the session cart
            if (session.getAttribute("isLoggedIn") == null) {
                //Check if the cart exists or is empty
                ArrayList<Food> cart = (ArrayList<Food>) session.getAttribute("cart");
                if (cart == null || cart.isEmpty()) {
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                    request.setAttribute("print", "Empty");
                    dispatcher.forward(request, response);
                } else {
                    double total = 0;
                    for (Food i : cart) {
                        total += i.getPrice();
                    }
                    cost = new TotalPrice(total);
                    foodList = cart;
                }
                //If logged in, ccheck database
            } else {
                Helper helper = new Helper();
                QueryHelper query = new QueryHelper();
                User user = (User) session.getAttribute("isLoggedIn");
                ResultSet rset = query.cartInnerJoin(conn, stmt, user.getId());
                foodList = helper.prepareCart(rset);
                double total = 0;
                //If logged in, get better price
                for (Food i : foodList) {
                    total += i.getSalesPrice();
                }
                cost = new TotalPrice(total);
            }

            //Print the cart and the total price
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", foodList);
            request.setAttribute("total", cost);
            dispatcher.forward(request, response);

        } catch (Exception e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", e);
            dispatcher.forward(request, response);
        }
    }
 /*-----------------------------------------------------------------------------------------------------------*/
    //Add an item to your cart
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        //start jdbc
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
            Helper helper = new Helper();
            //Get the id parameter to be added to your cart
            String id = request.getParameter("id");
            //make sure that there was something passed in
            if (id != null) {
                int idnum = Integer.parseInt(id);
                //Get the item in question
                QueryHelper query = new QueryHelper();
                ResultSet rset = query.selectById(conn, stmt, idnum);
                Food inCart = helper.prepareItem(rset);

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
                    User sess = (User) session.getAttribute("isLoggedIn");
                    query.addCart(conn, stmt, inCart, sess.getId());
                }

                //Print the item that was added
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                request.setAttribute("print", inCart);
                dispatcher.forward(request, response);

            }
        } catch (Exception e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", e);
            dispatcher.forward(request, response);
        }

    }

    /*-----------------------------------------------------------------------------------------------------------*/
    //Remove an item from your cart
    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        try (Connection conn = DriverManager.getConnection(localHost, "user", "1234");
                Statement stmt = conn.createStatement();) {
            HttpSession session = request.getSession();
            //get the id parameter of the item to be removed
            String id = request.getParameter("id");
            if (id != null) {
                int idnum = Integer.parseInt(id);
                Food del = null;

                if (session.getAttribute("isLoggedIn") == null) {
                    synchronized (session) {
                        //Remove the item from the cart
                        ArrayList<Food> cart = (ArrayList<Food>) session.getAttribute("cart");
                        for (Food i : cart) {
                            if (i.getID() == idnum) {
                                del = i;
                                cart.remove(i);
                                break;
                            }
                        }
                    }
                } else {
                    QueryHelper query = new QueryHelper();
                    Helper helper = new Helper();
                    ResultSet rset = query.selectCartById(conn, stmt, idnum);
                    del = helper.prepareItem(rset);
                    query.deleteCartById(conn, stmt, idnum);
                }
                //Tell User an item has been deleted
                response.setStatus(204);
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                dispatcher.forward(request, response);

            } else {
                //if the format was wrong, alert the user
                RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
                request.setAttribute("print", ("Wrong Format. must be '?id = ...' "));
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/print");
            request.setAttribute("print", ("Number Format or JSON ERROR: " + e));
            dispatcher.forward(request, response);
        }

    }

}
