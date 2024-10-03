package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.servlet.CurrenciesServlet;
import org.fizz_buzz.controller.servlet.ExchangeRatesServlet;
import org.fizz_buzz.controller.servlet.ExchangeServlet;
import org.fizz_buzz.service.CurrencyJsonService;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

public class ReqParamsFilter extends HttpFilter {

    public static final String NAME = "ReqParamsFilter";

    private static final String PARAMETER_REQUIRED = "\"%s\"-parameter required";

    private final CurrencyJsonService currencyJsonService = CurrencyJsonService.getInstance();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(ExchangeRatesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            if (!isExistReqParam(req, res, ExchangeRatesServlet.PARAMETER_BASE_CURR_CODE)) {
                return;
            }
            if (!isExistReqParam(req, res, ExchangeRatesServlet.PARAMETER_TARGET_CURR_CODE)) {
                return;
            }
            if (!isExistReqParam(req, res, ExchangeRatesServlet.PARAMETER_RATE)) {
                return;
            }
        }

        if (req.getRequestURL().toString().endsWith(ExchangeServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_GET)) {
            if (!isExistReqParam(req, res, ExchangeServlet.PARAM_NAME_FROM)) {
                return;
            }
            if (!isExistReqParam(req, res, ExchangeServlet.PARAM_NAME_TO)) {
                return;
            }
            if (!isExistReqParam(req, res, ExchangeServlet.PARAM_NAME_AMOUNT)) {
                return;
            }
        }

        if (req.getRequestURL().toString().endsWith(CurrenciesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            if (!isExistReqParam(req, res, CurrenciesServlet.PARAMETER_CODE)) {
                return;
            }
            if (!isExistReqParam(req, res, CurrenciesServlet.PARAMETER_NAME)) {
                return;
            }
            if (!isExistReqParam(req, res, CurrenciesServlet.PARAMETER_SIGN)) {
                return;
            }
        }

        chain.doFilter(req, res);
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
