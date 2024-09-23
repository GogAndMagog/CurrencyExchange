package org.fizz_buzz.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = ExchangeServlet.URL)
public class ExchangeServlet extends HttpServlet {

    public static final String URL = "/exchange";

    private static final String EXCHANGE_NOT_FOUND = "Exchange rate not found";

    public static final String PARAM_NAME_FROM = "from";
    public static final String PARAM_NAME_TO = "to";
    public static final String PARAM_NAME_AMOUNT = "amount";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        var from = req.getParameter(PARAM_NAME_FROM);
        var to = req.getParameter(PARAM_NAME_TO);
        var amount = req.getParameter(PARAM_NAME_AMOUNT);

        var exchangeRate = CurrencyJsonService.getInstance()
                .exchange(from, to, Double.parseDouble(amount));
        if (exchangeRate != null
                && !exchangeRate.isEmpty()) {
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(exchangeRate);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, EXCHANGE_NOT_FOUND);
        }

    }
}
