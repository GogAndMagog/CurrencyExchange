package org.fizz_buzz.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        if (req.getPathInfo().matches("/\\w{6}\\b")) {
            var exchangeRate = CurrencyJsonService.getInstance()
                    .getExchangeRate(req.getPathInfo().substring(1, 4).toUpperCase()
                            , req.getPathInfo().substring(4, 7).toUpperCase());
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
