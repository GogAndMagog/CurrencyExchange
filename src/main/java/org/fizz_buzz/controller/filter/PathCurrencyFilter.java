package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.servlet.CurrencyServlet;
import org.fizz_buzz.controller.servlet.ExchangeRateServlet;
import org.fizz_buzz.util.HTTPHelper;

import java.io.IOException;

@WebFilter(filterName = PathCurrencyFilter.FILTER_NAME,
        urlPatterns = {CurrencyServlet.URL, ExchangeRateServlet.URL})
public class PathCurrencyFilter extends HttpFilter {

    public static final String FILTER_NAME = "PathCurrencyFilter";

    private static final String CURR_CODE_PATTERN = "/\\w{3}\\b";
    private static final String CURR_CODE_PAIR_PATTERN = "/\\w{6}\\b";

    private static final String CURRENCY_ERROR_MESSAGE = "Path parameters should look like \\\"currency/eur\\\"";
    private static final String CODE_PAIR_ERROR_MESSAGE = "Currencies should look like \\\"/USDBYN\\\"";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        //check that parameter is single and it's fit three letter currency code
        if (req.getServletPath().equals(CurrencyServlet.URL_PATTERN)
                && !req.getPathInfo().matches(CURR_CODE_PATTERN)) {
            HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, CURRENCY_ERROR_MESSAGE);
            return;
        }

        //check that parameter is couple of three letter currency codes
        if (req.getServletPath().equals(ExchangeRateServlet.URL_PATTERN)
                && !req.getPathInfo().matches(CURR_CODE_PAIR_PATTERN)) {
            HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, CODE_PAIR_ERROR_MESSAGE);
            return;
        }

        chain.doFilter(req, res);
    }
}
