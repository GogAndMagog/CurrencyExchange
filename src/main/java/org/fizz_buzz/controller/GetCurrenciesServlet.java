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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var code = req.getParameter("code");
        var name = req.getParameter("name");
        var sign = req.getParameter("sign");

        if (code == null || name == null || sign == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing parameters");
        }

        try {
            var answer =  CurrencyJsonService.getInstance().addCurrency(code, name, sign);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().println(answer);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
