package org.fizz_buzz.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/currencies")
public class GetCurrenciesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter out = resp.getWriter();
        try {
            out.println(CurrencyJsonService.getInstance().getCurrencies());
            resp.setStatus(200);
            resp.setContentType("application/json");
        }
        catch (Exception e) {
            out.println(e.getMessage());
            resp.setStatus(500);
            resp.setContentType("text/plain");
        }
    }
}
