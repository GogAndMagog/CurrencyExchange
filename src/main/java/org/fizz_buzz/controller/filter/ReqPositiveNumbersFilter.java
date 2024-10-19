package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.servlet.ExchangeRatesServlet;
import org.fizz_buzz.controller.servlet.ExchangeServlet;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

public class ReqPositiveNumbersFilter extends HttpFilter {

    private static final String PARAMETER_MUST_BE_POSITIVE = "Parameter \\\"%s\\\" must be positive value";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(ExchangeRatesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            for (var parameter : ExchangeRatesServlet.REQ_DOUBLE_PARAMETERS) {
                if (!isPositive(req, parameter)) {
                    HTTPHelper.sendJsonError(res,
                            HttpServletResponse.SC_BAD_REQUEST,
                            PARAMETER_MUST_BE_POSITIVE.formatted(parameter));
                    return;
                }
            }
        }

        if (req.getRequestURL().toString().endsWith(ExchangeServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_GET)) {
            for (var parameter : ExchangeServlet.REQ_DOUBLE_PARAMETERS) {
                if (!isPositive(req, parameter)) {
                    HTTPHelper.sendJsonError(res,
                            HttpServletResponse.SC_BAD_REQUEST,
                            PARAMETER_MUST_BE_POSITIVE.formatted(parameter));
                    return;
                }
            }
        }

        super.doFilter(req, res, chain);
    }

    private boolean isPositive(HttpServletRequest req, String parameter) {
        return !req.getParameter(parameter).startsWith("-");
    }
}
