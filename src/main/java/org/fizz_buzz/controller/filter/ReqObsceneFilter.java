package org.fizz_buzz.controller.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.fizz_buzz.controller.servlet.CurrenciesServlet;
import org.fizz_buzz.dao.ObsceneVocabularyDAO;
import org.fizz_buzz.dao.ObsceneVocabularyFileDAO;
import org.fizz_buzz.util.HTTPHelper;
import org.fizz_buzz.util.ProjectConstants;

import java.io.IOException;

public class ReqObsceneFilter extends HttpFilter {

    private static final String CONTAINS_OBSCENE = "Parameter \\\"%s\\\" contains obscene vocabulary";

    private ObsceneVocabularyDAO vocabularyDAO = ObsceneVocabularyFileDAO.getInstance();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        if (req.getRequestURL().toString().endsWith(CurrenciesServlet.URL)
                && req.getMethod().equalsIgnoreCase(ProjectConstants.METHOD_POST)) {
            for (String obsceneParameter : CurrenciesServlet.REQ_OBSCENE_PARAMETERS) {
                if (isObscene(req.getParameter(obsceneParameter))) {
                    HTTPHelper.sendJsonError(res,
                            HttpServletResponse.SC_BAD_REQUEST,
                            CONTAINS_OBSCENE.formatted(obsceneParameter));
                    return;
                }
            }
        }
        super.doFilter(req, res, chain);
    }

    private boolean isObscene(String parameterValue) {
        return vocabularyDAO.readAll().stream().anyMatch(parameterValue.toLowerCase()::contains);
    }
}
