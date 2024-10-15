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

@WebServlet(urlPatterns = CurrenciesServlet.URL)
public class CurrenciesServlet extends HttpServlet {

    public static final String URL = "/currencies";

    public static final String PARAMETER_CODE = "code";
    public static final String PARAMETER_NAME = "name";
    public static final String PARAMETER_SIGN = "sign";

    public static final String[] REQ_PARAMETERS = {PARAMETER_CODE, PARAMETER_NAME, PARAMETER_SIGN};

    private final CurrencyJsonService currencyJsonService = CurrencyJsonService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter out = resp.getWriter();
        try {
            out.println(CurrencyJsonService.getInstance().getCurrencies());
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
        }
        catch (Exception e) {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
            resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
            resp.getWriter().println(answer);
        } catch (Exception e) {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
