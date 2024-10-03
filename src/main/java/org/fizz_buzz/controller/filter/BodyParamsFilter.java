package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.controller.servlet.ExchangeRateServlet;
import org.fizz_buzz.service.CurrencyJsonService;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

@WebFilter(urlPatterns = { ExchangeRateServlet.URL })
public class BodyParamsFilter extends HttpFilter {

    private static final String BODY_MUST_CONTAIN_RATE = "Body parameters must contain \\\"rate\\\"";
    private static final String RATE_MUST_BE_FLOAT = "Body parameter \\\"rate\\\" must be floating point number";
    private static final String NO_BODY_PARAMETERS = "No body parameters";

    private final CurrencyJsonService currencyJsonService = CurrencyJsonService.getInstance();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if ( ExchangeRateServlet.URL.contains(req.getServletPath())
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_PATCH)) {
            var bodyParams = req.getReader().readLine();

            if (bodyParams == null
                    || bodyParams.isEmpty()) {
                HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, NO_BODY_PARAMETERS);
                return;
            }

            if (!isContainRate(bodyParams)) {
                HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, BODY_MUST_CONTAIN_RATE);
                return;
            }

            if (!isRateDouble(bodyParams)) {
                HTTPHelper.sendJsonError(res, HttpServletResponse.SC_BAD_REQUEST, RATE_MUST_BE_FLOAT);
                return;
            }

            req.getReader().reset();
        }

        chain.doFilter(req, res);
    }

    private boolean isContainRate(String bodyParams) {
        var params = bodyParams.trim().split("&");
        for (String param : params) {
            if (param.matches(ExchangeRateServlet.RATE_PATTERN)) {
                return true;
            }
        }

        return false;
    }

    private boolean isRateDouble(String bodyParams) {
        var params = bodyParams.trim().split("&");
        for (String param : params) {
            if (param.matches(ExchangeRateServlet.RATE_PATTERN)) {
                try {
                    Double.parseDouble(param.split("=")[1]);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
        }

        return false;
    }
}
