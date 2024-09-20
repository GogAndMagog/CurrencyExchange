package org.fizz_buzz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fizz_buzz.model.dao.CurrencyDAO;
import org.fizz_buzz.model.entity.CurrencyEntity;
import org.fizz_buzz.model.dao.CurrencySqliteDAO;
import org.fizz_buzz.model.dao.ExchangeRateDAO;
import org.fizz_buzz.model.dao.ExchangeRateSqliteDAO;
import org.fizz_buzz.model.Repository;
import org.fizz_buzz.model.SQLiteRepository;
import org.fizz_buzz.model.entity.ExchangeRateEntity;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class CurrencyJsonService {

    private volatile static CurrencyJsonService instance;
    private final ObjectMapper objectMapper;
    private static final String USD_CURR_CODE = "USD";

    private final CurrencyDAO currencyDAO;
    private final ExchangeRateDAO exchangeRateDAO;

    private CurrencyJsonService(Repository repository) {
        currencyDAO = new CurrencySqliteDAO();
        exchangeRateDAO = new ExchangeRateSqliteDAO();
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
            return objectMapper.writeValueAsString(currencyDAO.readAll());
        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getCurrency(String currCode) {
        AtomicReference<String> json = new AtomicReference<>("");

        currencyDAO.readByCode(currCode)
                .ifPresent(currencyModel -> {
                    try {
                        json.set(objectMapper.writeValueAsString(currencyModel));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return json.get();
    }

    public String addCurrency(String currCode, String name, String sign) {
        AtomicReference<String> json = new AtomicReference<>("");

        currencyDAO.create(new CurrencyEntity(-1, currCode.toUpperCase(), name, sign))
                .ifPresent(currencyModel -> {
                    try {
                        json.set(objectMapper.writeValueAsString(currencyModel));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return json.get();
    }

    public String getExchangeRates() {
        try {
            return objectMapper.writeValueAsString(exchangeRateDAO.readAll());
        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        AtomicReference<String> json = new AtomicReference<>("");

        exchangeRateDAO.readByCodePair(baseCurrencyCode, targetCurrencyCode)
                .ifPresent(currencyModel -> {
                    try {
                        json.set(objectMapper.writeValueAsString(currencyModel));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return json.get();
    }

    public String addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        AtomicReference<String> json = new AtomicReference<>("");

        exchangeRateDAO.create(baseCurrencyCode, targetCurrencyCode, rate)
                .ifPresent(currencyModel -> {
                    try {
                        json.set(objectMapper.writeValueAsString(currencyModel));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return json.get();
    }

    public String updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        AtomicReference<String> json = new AtomicReference<>("");

        exchangeRateDAO.updateRate(baseCurrencyCode, targetCurrencyCode, rate)
                .ifPresent(currencyModel -> {
                    try {
                        json.set(objectMapper.writeValueAsString(currencyModel));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });

        return json.get();
    }

    public String exchange(String baseCurrencyCode, String targetCurrencyCode, Double amount) {
        var exchangeRate = exchangeRateDAO.readByCodePair(baseCurrencyCode, targetCurrencyCode);
        if (exchangeRate.isEmpty()) {
            exchangeRate = getExchangeRateFromReversedExchangeRate(baseCurrencyCode, targetCurrencyCode);
        }
        if (exchangeRate.isEmpty()) {
            exchangeRate = getExchangeRateFromThirdCurrency(USD_CURR_CODE, baseCurrencyCode, targetCurrencyCode);
        }

        if (exchangeRate.isPresent()) {
            var convertedAmount = new ConvertedAmount(
                    exchangeRate.get().baseCurrency(),
                    exchangeRate.get().targetCurrency(),
                    exchangeRate.get().rate(),
                    amount,
                    amount * exchangeRate.get().rate());
            try {
                return objectMapper.writeValueAsString(convertedAmount);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "";
        }
    }

    private Optional<ExchangeRateEntity> getExchangeRateFromReversedExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        var reversedExchangeRate = exchangeRateDAO.readByCodePair(targetCurrencyCode, baseCurrencyCode);
        if (reversedExchangeRate.isPresent()) {
            return Optional.of(new ExchangeRateEntity(-1,
                    reversedExchangeRate.get().targetCurrency(),
                    reversedExchangeRate.get().baseCurrency(),
                    1 / reversedExchangeRate.get().rate()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<ExchangeRateEntity> getExchangeRateFromThirdCurrency(String thirdCurrency,
                                                                          String baseCurrencyCode,
                                                                          String targetCurrencyCode) {
        var thirdCurrencyToBaseCode = exchangeRateDAO.readByCodePair(thirdCurrency, baseCurrencyCode);
        var thirdCurrencyToTargetCode = exchangeRateDAO.readByCodePair(thirdCurrency, targetCurrencyCode);

        if (thirdCurrencyToBaseCode.isPresent()
                && thirdCurrencyToTargetCode.isPresent()
                && thirdCurrencyToBaseCode.get().rate() != 0) {
            return Optional.of(new ExchangeRateEntity(0,
                    thirdCurrencyToBaseCode.get().targetCurrency(),
                    thirdCurrencyToTargetCode.get().targetCurrency(),
                    thirdCurrencyToTargetCode.get().rate() / thirdCurrencyToBaseCode.get().rate()));
        } else {
            return Optional.empty();
        }
    }
}
