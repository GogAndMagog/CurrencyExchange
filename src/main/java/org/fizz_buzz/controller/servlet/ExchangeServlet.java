package org.fizz_buzz.controller.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyExchangeJsonService;
import org.fizz_buzz.service.CurrencyExchangeService;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = ExchangeServlet.URL)
public class ExchangeServlet extends HttpServlet {

    public static final String URL = "/exchange";

    private static final String EXCHANGE_NOT_FOUND = "Exchange rate not found";

    public static final String PARAM_NAME_FROM = "from";
    public static final String PARAM_NAME_TO = "to";
    public static final String PARAM_NAME_AMOUNT = "amount";

    public static final String[] REQ_PARAMETERS = {PARAM_NAME_FROM, PARAM_NAME_TO, PARAM_NAME_AMOUNT};
    public static final String[] REQ_DOUBLE_PARAMETERS = {PARAM_NAME_AMOUNT};

    private final CurrencyExchangeService<String> currencyExchangeService = CurrencyExchangeJsonService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        var from = req.getParameter(PARAM_NAME_FROM);
        var to = req.getParameter(PARAM_NAME_TO);
        var amount = req.getParameter(PARAM_NAME_AMOUNT).replace(',','.');

        var exchangeRate = currencyExchangeService.exchange(from, to, Double.parseDouble(amount));
        if (exchangeRate != null
                && !exchangeRate.isEmpty()) {
            resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(exchangeRate);
        } else {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, EXCHANGE_NOT_FOUND);
        }
    }
}
