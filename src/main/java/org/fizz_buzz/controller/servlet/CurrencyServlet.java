package org.fizz_buzz.controller.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;
import org.fizz_buzz.util.HTTPHelper;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = CurrencyServlet.URL)
public class CurrencyServlet extends HttpServlet {

    public static final String URL = "/currency/*";
    public static final String URL_PATTERN = "/currency";

    private static final String CURRENCY_NOT_FOUND = "Currency not found";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter out = resp.getWriter();
        String currCode = req.getPathInfo().split("/")[1].toUpperCase();
        resp.setContentType("application/json");

        var currency = CurrencyJsonService.getInstance().getCurrency(currCode);
        if (currency != null
                && !currency.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_OK);
            out.println(currency);
        } else {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, CURRENCY_NOT_FOUND);
        }
    }
}
