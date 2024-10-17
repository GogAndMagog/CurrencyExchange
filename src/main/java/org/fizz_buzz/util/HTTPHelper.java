package org.fizz_buzz.util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class HTTPHelper {

    public static void sendJsonError(HttpServletResponse res, int status, String message) {
        res.setStatus(status);
        res.setContentType(ProjectConstants.JSON_CONTENT_TYPE);
        try {
            res.getWriter()
                    .println(formJsonErr(message));
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private static String formJsonErr(String message)
    {
        return "{\"message\": \"%s\"}".formatted(message);
    }
}
