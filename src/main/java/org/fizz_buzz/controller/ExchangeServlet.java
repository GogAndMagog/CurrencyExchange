package org.fizz_buzz.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/exchange")
public class ExchangeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        var from = req.getParameter("from");
        var to = req.getParameter("to");
        var amount = req.getParameter("amount");

        if (from != null && to != null && amount != null) {
            var exchangeRate = CurrencyJsonService.getInstance()
                    .exchange(from, to, Double.parseDouble(amount));
            if (exchangeRate != null
                    && !exchangeRate.isEmpty()) {
                resp.setContentType("application/json");
                resp.setStatus(HttpServletResponse.SC_OK);
                out.println(exchangeRate);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Exchange rate not found");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong parameters");
        }

    }
}
