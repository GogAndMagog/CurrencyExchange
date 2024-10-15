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
            if (!filterDoubleParameters(req, res, ExchangeRatesServlet.REQ_DOUBLE_PARAMETERS)) {
                return;
            }
        }

        if (req.getRequestURL().toString().endsWith(ExchangeServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_GET)) {
            if (!filterDoubleParameters(req, res, ExchangeServlet.REQ_DOUBLE_PARAMETERS)) {
                return;
            }
        }

        chain.doFilter(req, res);
    }

    public boolean filterDoubleParameters(HttpServletRequest req, HttpServletResponse res, String[] parameters){
        boolean passed = true;

        for (String parameter : parameters) {
            if (!isDouble(req, res, ExchangeServlet.PARAM_NAME_AMOUNT)) {
                passed = false;
                break;
            }
        }

        return passed;
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
