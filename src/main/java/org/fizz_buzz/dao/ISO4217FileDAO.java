package org.fizz_buzz.dao;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ISO4217FileDAO implements ISO4217DAO {

    private static final String ISO4217_CURR_CODES_FILE_PATH = "CurrencyCodes.txt";

    private static ISO4217FileDAO instance;

    private ISO4217FileDAO() {
    }

    public synchronized static ISO4217DAO getInstance() {
        if (instance == null) {
            instance = new ISO4217FileDAO();
        }
        return instance;
    }

    @Override
    public List<String> readAll() {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource(ISO4217_CURR_CODES_FILE_PATH);

        try {
            Path path = Paths.get(url.toURI());
            return Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (URISyntaxException | IOException e) {
            return List.of();
        }
    }
}
