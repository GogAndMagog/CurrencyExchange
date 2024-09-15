package org.fizz_buzz.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(urlPatterns = "/currency/*")
public class CurrencyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        PrintWriter out = resp.getWriter();

        if (isValidParams(req.getPathInfo())) {
            String currCode = req.getPathInfo().split("/")[1].toUpperCase();
            resp.setContentType("application/json");

            var currency = CurrencyJsonService.getInstance().getCurrency(currCode);
            if (currency != null
            && !currency.isEmpty()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                out.println(currency);
            }
            else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND,"Currency not found");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong parameters");
        }
    }

    private boolean isValidParams(String paramsString) {
        //check that parameter is single and it's fit three letter currency code
        if (paramsString != null && !paramsString.isEmpty()) {
            var params = paramsString.split("/");
            return params.length == 2
                    && params[1].length() == 3;
        } else return false;
    }
}
