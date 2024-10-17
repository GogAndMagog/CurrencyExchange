package org.fizz_buzz.controller.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.dao.CurrencyDAO;
import org.fizz_buzz.dao.CurrencySqliteDAO;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

@WebServlet(urlPatterns = CurrencyServlet.URL)
public class CurrencyServlet extends HttpServlet {

    public static final String URL = "/currency/*";
    public static final String URL_PATTERN = "/currency";

    private static final String CURRENCY_NOT_FOUND = "Currency not found";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyDAO currencyDAO = CurrencySqliteDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter out = resp.getWriter();
        String currCode = req.getPathInfo().split("/")[1].toUpperCase();
        resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);

        var currency = getCurrency(currCode);
        if (currency != null
                && !currency.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(currency);
        } else {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, CURRENCY_NOT_FOUND);
        }
    }

    public String getCurrency(String currCode) {
        AtomicReference<String> json = new AtomicReference<>("");

        currencyDAO.readByCode(currCode)
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
