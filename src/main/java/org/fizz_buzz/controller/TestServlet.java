package org.fizz_buzz.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.model.Repository;
import org.fizz_buzz.model.SQLiteRepository;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/")
public class TestServlet extends HttpServlet {

    @Override
    public void init() throws ServletException {
        // Servlet initialization code
        System.out.println("LifecycleServlet initialized.");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Request handling code
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h1>444LifecycleServlet at your service!</h1>");
        Repository repository = new SQLiteRepository();
        try {
            repository.putUser();
        }
        catch (Exception e) {
            out.println("<b>%s</b>".formatted(e.getMessage()));
        }
        out.println("<pre>%s</pre>".formatted(repository.getUsers()));
        out.println("</body></html>");
        System.out.println("LifecycleServlet service method called.");
    }

    @Override
    public void destroy() {
        // Servlet cleanup code
        System.out.println("LifecycleServlet destroyed.");
    }
}
