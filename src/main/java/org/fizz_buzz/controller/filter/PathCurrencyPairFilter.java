package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.ExchangeRateServlet;

import java.io.IOException;

//@WebFilter(filterName = PathCurrencyPairFilter.FILTER_NAME ,
//        urlPatterns = ExchangeRateServlet.URL)
public class PathCurrencyPairFilter extends HttpFilter {

    public static final String FILTER_NAME = "PathCurrencyPairFilter";
    private static final String ERROR_MESSAGE = "Currencies should look like \"/USDBYN\"";
    private static final String PAIR_CURR_CODE_PATTERN = "/\\w{6}\\b";

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        //check that parameter is couple of three letter currency codes
        if (!req.getPathInfo().matches(PAIR_CURR_CODE_PATTERN)) {
            res.sendError(HttpServletResponse.SC_BAD_REQUEST, ERROR_MESSAGE);
            return;
        }
        chain.doFilter(req, res);
    }
}
