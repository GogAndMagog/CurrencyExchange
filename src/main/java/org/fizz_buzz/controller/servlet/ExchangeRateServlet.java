package org.fizz_buzz.controller.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.dao.ExchangeRateDAO;
import org.fizz_buzz.dao.ExchangeRateSqliteDAO;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

@WebServlet(urlPatterns = ExchangeRateServlet.URL)
public class ExchangeRateServlet extends HttpServlet {

    public static final String URL = "/exchangeRate/*";
    public static final String URL_PATTERN = "/exchangeRate";

    public static final String RATE_PATTERN = "rate=.+";

    public static final String EXCHANGE_RATE_NOT_FOUND = "Exchange rate not found";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateDAO exchangeRateDAO = ExchangeRateSqliteDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();

        var exchangeRate = getExchangeRate(req.getPathInfo().substring(1, 4).toUpperCase()
                        , req.getPathInfo().substring(4, 7).toUpperCase());
        if (exchangeRate != null
                && !exchangeRate.isEmpty()) {
            resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(exchangeRate);
        } else {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, EXCHANGE_RATE_NOT_FOUND);
        }
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if (!method.equals(ProjectConstants.METHOD_PATCH)) {
            super.service(req, resp);
            return;
        }
        this.doPatch(req, resp);
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        double rate;

        var bodyParams = req.getReader().readLine();
        rate = getRate(bodyParams);

        var exchangeRate = updateExchangeRate(req.getPathInfo().substring(1, 4).toUpperCase()
                        , req.getPathInfo().substring(4, 7).toUpperCase()
                        , rate);
        if (exchangeRate != null
                && !exchangeRate.isEmpty()) {
            resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(exchangeRate);
        } else {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, EXCHANGE_RATE_NOT_FOUND);
        }
    }

    private double getRate(String bodyParams) {
        var params = bodyParams.trim().split("&");
        for (String param : params) {
            if (param.matches(RATE_PATTERN)) {
                return Double.parseDouble(param.split("=")[1].replace(',','.'));
            }
        }

        return -1;
    }

    public String getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        AtomicReference<String> json = new AtomicReference<>("");

        exchangeRateDAO.readByCodePair(baseCurrencyCode, targetCurrencyCode)
                .ifPresent(currencyModel -> {
                    try {
                        json.set(objectMapper.writeValueAsString(currencyModel));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return json.get();
    }

    public String updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        AtomicReference<String> json = new AtomicReference<>("");

        exchangeRateDAO.updateRate(baseCurrencyCode, targetCurrencyCode, rate)
                .ifPresent(currencyModel -> {
                    try {
                        json.set(objectMapper.writeValueAsString(currencyModel));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return json.get();
    }

}
