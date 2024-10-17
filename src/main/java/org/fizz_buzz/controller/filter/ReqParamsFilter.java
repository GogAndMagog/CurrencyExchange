package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.servlet.CurrenciesServlet;
import org.fizz_buzz.controller.servlet.ExchangeRatesServlet;
import org.fizz_buzz.controller.servlet.ExchangeServlet;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

public class ReqParamsFilter extends HttpFilter {

    private static final String PARAMETER_REQUIRED = "\\\"%s\\\"-parameter required";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(ExchangeRatesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            for (String parameter : ExchangeRatesServlet.REQ_PARAMETERS) {
                if (!processReqParams(req, res, ExchangeRatesServlet.REQ_PARAMETERS)) {
                    return;
                }
            }
        }

        if (req.getRequestURL().toString().endsWith(ExchangeServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_GET)) {
            if (!processReqParams(req, res, ExchangeServlet.REQ_PARAMETERS)) {
                return;
            }
        }

        if (req.getRequestURL().toString().endsWith(CurrenciesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            if (!processReqParams(req, res, CurrenciesServlet.REQ_PARAMETERS)) {
                return;
            }
        }

        chain.doFilter(req, res);
    }

    public boolean processReqParams(HttpServletRequest req, HttpServletResponse res, String[] parameters) {
        boolean passed = true;

        for (String parameter : parameters) {
            if (!isExistReqParam(req, res, parameter)) {
                passed = false;
                break;
            }
        }

        return passed;
    }

    public boolean isExistReqParam(HttpServletRequest req, HttpServletResponse res, String paramName) {
        if (req.getParameter(paramName) == null
                || req.getParameter(paramName).isEmpty()
                || req.getParameter(paramName).isBlank()) {
            HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, PARAMETER_REQUIRED.formatted(paramName));
            return false;
        }

        return true;
    }
}
