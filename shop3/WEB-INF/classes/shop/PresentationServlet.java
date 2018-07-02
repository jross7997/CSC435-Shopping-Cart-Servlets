/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Justin
 */
public class PresentationServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession();
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {

            //Check for Status Codes
            if (response.getStatus() == 201) {
                out.println(mapper.writeValueAsString("201: Object Created"));
            } else if (response.getStatus() == 204) {
                out.println(mapper.writeValueAsString("204: Object Deleted"));
            } else {
                //If no codes, print what was passed through
                out.println(mapper.writeValueAsString(request.getAttribute("print")));
                if (request.getAttribute("total") != null) {
                    out.println(mapper.writeValueAsString(request.getAttribute("total")));
                }
            }
        } finally {
            out.flush();
            out.close();
        }

    }

    //Always call the DoGet
    public void doPut(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }

    public void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        doGet(request, response);
    }

}
