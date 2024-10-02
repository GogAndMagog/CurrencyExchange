package org.fizz_buzz.controller.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = ExchangeRatesServlet.URL)
public class ExchangeRatesServlet extends HttpServlet {

    public static final String URL = "/exchangeRates";

    public static final String PARAMETER_BASE_CURR_CODE = "baseCurrencyCode";
    public static final String PARAMETER_TARGET_CURR_CODE = "targetCurrencyCode";
    public static final String PARAMETER_RATE = "rate";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            out.println(CurrencyJsonService.getInstance().getExchangeRates());
            resp.setStatus(200);
            resp.setContentType("application/json");
        } catch (Exception e) {
            out.println(e.getMessage());
            resp.setStatus(500);
            resp.setContentType("text/plain");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var baseCurrencyCode = req.getParameter(PARAMETER_BASE_CURR_CODE);
        var targetCurrencyCode = req.getParameter(PARAMETER_TARGET_CURR_CODE);
        var rate = req.getParameter(ExchangeRatesServlet.PARAMETER_RATE);

        try {
            var answer = CurrencyJsonService
                    .getInstance()
                    .addExchangeRate(baseCurrencyCode,
                            targetCurrencyCode,
                            Double.parseDouble(rate));

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().println(answer);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
