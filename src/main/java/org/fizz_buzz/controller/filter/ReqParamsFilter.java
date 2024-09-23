package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.CurrenciesServlet;
import org.fizz_buzz.controller.ExchangeRatesServlet;
import org.fizz_buzz.controller.ExchangeServlet;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

@WebFilter(filterName = "URLParamsFilter",
        urlPatterns = {ExchangeRatesServlet.URL, ExchangeServlet.URL, CurrenciesServlet.URL})
public class ReqParamsFilter extends HttpFilter {

    private static final String PARAMETER_REQUIRED = "\"%s\"-parameter required";
    private static final String PARAMETER_FLOATING_POINT_NUMBER = "\"%s\" must be floating point number";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(ExchangeRatesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            if (req.getParameter(ExchangeRatesServlet.PARAMETER_BASE_CURR_CODE) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(ExchangeRatesServlet.PARAMETER_BASE_CURR_CODE));
                return;
            }
            if (req.getParameter(ExchangeRatesServlet.PARAMETER_TARGET_CURR_CODE) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(ExchangeRatesServlet.PARAMETER_TARGET_CURR_CODE));
                return;
            }
            if (req.getParameter(ExchangeRatesServlet.PARAMETER_RATE) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(ExchangeRatesServlet.PARAMETER_RATE));
                return;
            } else if (!isDouble(req, ExchangeRatesServlet.PARAMETER_RATE)) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_FLOATING_POINT_NUMBER.formatted(ExchangeRatesServlet.PARAMETER_RATE));
                return;
            }
        }

        if (req.getRequestURL().toString().endsWith(ExchangeServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_GET)) {
            if (req.getParameter(ExchangeServlet.PARAM_NAME_FROM) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(ExchangeServlet.PARAM_NAME_FROM));
                return;
            }
            if (req.getParameter(ExchangeServlet.PARAM_NAME_TO) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(ExchangeServlet.PARAM_NAME_TO));
                return;
            }
            if (req.getParameter(ExchangeServlet.PARAM_NAME_AMOUNT) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(ExchangeServlet.PARAM_NAME_AMOUNT));
                return;
            } else if (!isDouble(req, ExchangeServlet.PARAM_NAME_AMOUNT)) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_FLOATING_POINT_NUMBER.formatted(ExchangeServlet.PARAM_NAME_AMOUNT));
                return;
            }
        }

        if (req.getRequestURL().toString().endsWith(CurrenciesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            if (req.getParameter(CurrenciesServlet.PARAMETER_CODE) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(CurrenciesServlet.PARAMETER_CODE));
                return;
            }
            if (req.getParameter(CurrenciesServlet.PARAMETER_NAME) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(CurrenciesServlet.PARAMETER_NAME));
                return;
            }
            if (req.getParameter(CurrenciesServlet.PARAMETER_SIGN) == null) {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        PARAMETER_REQUIRED.formatted(CurrenciesServlet.PARAMETER_SIGN));
                return;
            }
        }

        chain.doFilter(req, res);
    }

    private boolean isDouble(HttpServletRequest req, String param) {
        try {
            Double.parseDouble(req.getParameter(param));
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
