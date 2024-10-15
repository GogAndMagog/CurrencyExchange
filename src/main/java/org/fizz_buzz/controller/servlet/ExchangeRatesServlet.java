package org.fizz_buzz.controller.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = ExchangeRatesServlet.URL)
public class ExchangeRatesServlet extends HttpServlet {

    public static final String URL = "/exchangeRates";

    public static final String PARAMETER_BASE_CURR_CODE = "baseCurrencyCode";
    public static final String PARAMETER_TARGET_CURR_CODE = "targetCurrencyCode";
    public static final String PARAMETER_RATE = "rate";

    public static final String[] REQ_PARAMETERS = {PARAMETER_BASE_CURR_CODE, PARAMETER_TARGET_CURR_CODE, PARAMETER_RATE};
    public static final String[] REQ_DOUBLE_PARAMETERS = {PARAMETER_RATE};

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        try {
            out.println(CurrencyJsonService.getInstance().getExchangeRates());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
        } catch (Exception e) {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
            resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
            resp.getWriter().println(answer);
        } catch (Exception e) {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
