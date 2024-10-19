package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.fizz_buzz.controller.ParameterLength;
import org.fizz_buzz.controller.servlet.CurrenciesServlet;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

public class ReqParameterLengthFilter extends HttpFilter {

    private static final String PARAMETER_LONGER_THAN = "Parameter \\\"%s\\\" must be no longer than %d symbols";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(CurrenciesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            for (ParameterLength parameterLength : CurrenciesServlet.REQ_PARAMETER_LENGTHS) {
                if (isLonger(req, parameterLength)) {
                    HTTPHelper.sendJsonError(res,
                            HttpServletResponse.SC_BAD_REQUEST,
                            PARAMETER_LONGER_THAN.formatted(parameterLength.parameterName(), parameterLength.parameterLength()));
                    return;
                }
            }
        }

        super.doFilter(req, res, chain);
    }

    private boolean isLonger(HttpServletRequest req, ParameterLength parameterLength) {
        var value = req.getParameter(parameterLength.parameterName());
        return  value.length() > parameterLength.parameterLength();
    }

}
