package org.fizz_buzz.dao;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObsceneVocabularyFileDAO implements ObsceneVocabularyDAO {

    private static final String[] ObsceneVocabularyPaths = {"ObsceneVocabularyEN.txt",
            "ObsceneVocabularyRU.txt"};

    private static ObsceneVocabularyFileDAO instance;

    private ObsceneVocabularyFileDAO() {
    }

    public synchronized static ObsceneVocabularyDAO getInstance() {
        if (instance == null) {
            instance = new ObsceneVocabularyFileDAO();
        }
        return instance;
    }

    @Override
    public List<String> readAll() {
        List<String> obsceneWords = new ArrayList<>();
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();

        for (String obsceneVocabularyPath : ObsceneVocabularyPaths) {

            try {
                URL url = classloader.getResource(obsceneVocabularyPath);
                Path path = Paths.get(url.toURI());
                obsceneWords.addAll(Files.readAllLines(path, StandardCharsets.UTF_8));
            } catch (URISyntaxException | IOException | RuntimeException e) {
            }
        }

        return obsceneWords;
    }
}
