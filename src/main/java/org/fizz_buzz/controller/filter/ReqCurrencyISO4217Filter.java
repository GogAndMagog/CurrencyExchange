package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.fizz_buzz.controller.servlet.CurrenciesServlet;
import org.fizz_buzz.dao.ISO4217DAO;
import org.fizz_buzz.dao.ISO4217FileDAO;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

public class ReqCurrencyISO4217Filter extends HttpFilter {

    private static final String CURR_CODE_NOT_IN_ISO4217 = "Currency code does not satisfy ISO 4217";

    private ISO4217DAO iso4217DAO = ISO4217FileDAO.getInstance();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(CurrenciesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)
        && !isMatchISO4217(req.getParameter(CurrenciesServlet.PARAMETER_CODE))) {
            HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, CURR_CODE_NOT_IN_ISO4217);
            return;
        }
            super.doFilter(req, res, chain);
    }

    private boolean isMatchISO4217(String currencyCode) {
        return iso4217DAO.readAll().stream().anyMatch(currencyCode::contains);
    }

}
