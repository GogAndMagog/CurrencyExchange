package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.servlet.ExchangeRatesServlet;
import org.fizz_buzz.controller.servlet.ExchangeServlet;
import org.fizz_buzz.service.CurrencyJsonService;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

public class ReqDoubleParamsFilter extends HttpFilter {

    public static final String NAME = "ReqDoubleParamsFilter";

    private static final String PARAMETER_FLOATING_POINT_NUMBER = "\\\"%s\\\" must be floating point number";

    private final CurrencyJsonService currencyJsonService = CurrencyJsonService.getInstance();

    @Override
    public void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(ExchangeRatesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            if (!isDouble(req, res, ExchangeRatesServlet.PARAMETER_RATE)) {
                return;
            }
        }

        if (req.getRequestURL().toString().endsWith(ExchangeServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_GET)) {
            if (!isDouble(req, res, ExchangeServlet.PARAM_NAME_AMOUNT)) {
                return;
            }
        }

        chain.doFilter(req, res);
    }


    public boolean isDouble(HttpServletRequest req, HttpServletResponse res, String paramName) {
        try {
            Double.parseDouble(req.getParameter(paramName));
            return true;
        } catch (NumberFormatException numberFormatException) {
            HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, PARAMETER_FLOATING_POINT_NUMBER.formatted(paramName));
            return false;
        }
    }

}
