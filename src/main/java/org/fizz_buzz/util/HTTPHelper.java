package org.fizz_buzz.util;

import jakarta.servlet.http.HttpServletResponse;
import org.fizz_buzz.service.CurrencyJsonService;

import java.io.IOException;
import java.util.Map;

public class HTTPHelper {
    private static final CurrencyJsonService currencyJsonService = CurrencyJsonService.getInstance();

    public static void sendJsonError(HttpServletResponse res, int status, String message) {
        res.setStatus(status);
        res.setContentType("application/json");
        try {
            res.getWriter()
                    .println(currencyJsonService.formJsonErr(message));
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
