package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.servlet.CurrenciesServlet;
import org.fizz_buzz.controller.servlet.ExchangeRatesServlet;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

public class ReqCurrencyFilter extends HttpFilter {
    private static final String CURR_CODE_PATTERN = "\\w{3}\\b";

    private static final String WRONG_CURR_CODE = "\\\"Currency\\\" must be three letter code";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(CurrenciesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)
                && !req.getParameter(CurrenciesServlet.PARAMETER_CODE).matches(CURR_CODE_PATTERN)) {
            for (String parameter : CurrenciesServlet.REQ_CURR_PARAMETERS) {
                if (!isCurrCode(req, res, parameter)) {
                    HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, WRONG_CURR_CODE);
                    return;
                }
            }
        }

        if (req.getRequestURL().toString().endsWith(ExchangeRatesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            for (String parameter : ExchangeRatesServlet.REQ_CURR_PARAMETERS) {
                if (!isCurrCode(req, res, parameter)) {
                    HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, WRONG_CURR_CODE);
                    return;
                }
            }
        }

        chain.doFilter(req, res);
    }

    public boolean isCurrCode(HttpServletRequest req, HttpServletResponse res, String paramName) {
        return req.getParameter(paramName).matches(CURR_CODE_PATTERN);
    }

}