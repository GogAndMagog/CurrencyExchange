package org.fizz_buzz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fizz_buzz.controller.CurrencyModel;
import org.fizz_buzz.model.Repository;
import org.fizz_buzz.model.SQLiteRepository;

import java.util.Currency;

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
        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrency(String currCode) {
        try {
            var currency = repository.getCurrency(currCode);
            if (currency != null) {
                return objectMapper.writeValueAsString(repository.getCurrency(currCode));
            } else {
                return "";
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String addCurrency(String currCode, String name, String sign) {
        repository.addCurrency(new CurrencyModel(-1, currCode.toUpperCase(), name, sign));
        try {
            return objectMapper.writeValueAsString(repository.getCurrency(currCode));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
