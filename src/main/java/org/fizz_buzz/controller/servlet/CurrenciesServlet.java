package org.fizz_buzz.controller.servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.dao.CurrencyDAO;
import org.fizz_buzz.dao.CurrencySqliteDAO;
import org.fizz_buzz.model.Currency;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicReference;

@WebServlet(urlPatterns = CurrenciesServlet.URL)
public class CurrenciesServlet extends HttpServlet {

    public static final String URL = "/currencies";

    public static final String PARAMETER_CODE = "code";
    public static final String PARAMETER_NAME = "name";
    public static final String PARAMETER_SIGN = "sign";

    public static final String[] REQ_PARAMETERS = {PARAMETER_CODE, PARAMETER_NAME, PARAMETER_SIGN};
    public static final String[] REQ_CURR_PARAMETERS = {PARAMETER_CODE};

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyDAO currencyDAO = CurrencySqliteDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        PrintWriter out = resp.getWriter();
        try {
            out.println(getCurrencies());
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
            var answer =  addCurrency(code, name, sign);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
            resp.getWriter().println(answer);
        } catch (Exception e) {
            HTTPHelper.sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public String getCurrencies() {
        try {
            return objectMapper.writeValueAsString(currencyDAO.readAll());
        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String addCurrency(String currCode, String name, String sign) {
        AtomicReference<String> json = new AtomicReference<>("");

        currencyDAO.create(new Currency(-1, currCode.toUpperCase(), name, sign))
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
