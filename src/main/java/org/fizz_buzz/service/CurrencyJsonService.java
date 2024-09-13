package org.fizz_buzz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fizz_buzz.model.Repository;
import org.fizz_buzz.model.SQLiteRepository;

public class CurrencyJsonService {

    private volatile static CurrencyJsonService instance;
    private final Repository repository;
    private final ObjectMapper objectMapper;

    private CurrencyJsonService(Repository repository) {
        this.repository = repository;
        objectMapper = new ObjectMapper();
    }

    public synchronized static CurrencyJsonService getInstance() {
        if (instance == null) {
            instance = new CurrencyJsonService(SQLiteRepository.getInstance());
        }
        return instance;
    }

    public String getCurrencies() {
        try {
            return objectMapper.writeValueAsString(repository.getCurrencies());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
