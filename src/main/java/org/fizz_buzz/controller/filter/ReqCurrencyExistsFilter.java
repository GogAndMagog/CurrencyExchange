package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.servlet.ExchangeRatesServlet;
import org.fizz_buzz.dao.CurrencyDAO;
import org.fizz_buzz.dao.CurrencySqliteDAO;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

public class ReqCurrencyExistsFilter extends HttpFilter{
    private static final String CURRENCY_DOES_NOT_EXIST = "Currency %s does not exist";

    private CurrencyDAO currencyDAO = CurrencySqliteDAO.getInstance();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(ExchangeRatesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            for (String parameter : ExchangeRatesServlet.REQ_CURR_PARAMETERS) {
                if (!isCurrCodeExists(req, res, parameter)) {
                    HTTPHelper.sendJsonError(res,
                            HttpServletResponse.SC_BAD_REQUEST,
                            CURRENCY_DOES_NOT_EXIST.formatted(req.getParameter(parameter)));
                    return;
                }
            }
        }

        chain.doFilter(req, res);
    }

    public boolean isCurrCodeExists(HttpServletRequest req, HttpServletResponse res, String paramName) {
        return currencyDAO.readByCode(req.getParameter(paramName)).isPresent();
    }
}
