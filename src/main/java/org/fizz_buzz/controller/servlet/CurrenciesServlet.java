package org.fizz_buzz.controller.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = CurrenciesServlet.URL)
public class CurrenciesServlet extends HttpServlet {

    public static final String URL = "/currencies";

    public static final String PARAMETER_CODE = "code";
    public static final String PARAMETER_NAME = "name";
    public static final String PARAMETER_SIGN = "sign";

    private final CurrencyJsonService currencyJsonService = CurrencyJsonService.getInstance();

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
        var code = req.getParameter(PARAMETER_CODE);
        var name = req.getParameter(PARAMETER_NAME);
        var sign = req.getParameter(PARAMETER_SIGN);

        try {
            var answer =  currencyJsonService.addCurrency(code, name, sign);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType("application/json");
            resp.getWriter().println(answer);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().println(currencyJsonService.formJsonErr(e.getMessage()));
        }
    }
}
