package org.fizz_buzz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fizz_buzz.model.CurrencyModel;
import org.fizz_buzz.model.Repository;
import org.fizz_buzz.model.SQLiteRepository;

public class CurrencyJsonService {

    private volatile static CurrencyJsonService instance;
    private final Repository repository;
    private final ObjectMapper objectMapper;
    private static final String USD_CURR_CODE = "USD";

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

    public String getExchangeRates() {
        try {
            return objectMapper.writeValueAsString(repository.getExchangeRates());
        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String getExchangeRate(String basesCode, String targetCode) {
        try {
            var exchangeRate = repository.getExchangeRate(basesCode, targetCode);
            if (exchangeRate != null) {
                return objectMapper.writeValueAsString(exchangeRate);
            } else {
                return "";
            }
        } catch (JsonProcessingException | RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public String addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        try {
            repository.addExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            return objectMapper.writeValueAsString(repository.getExchangeRate(baseCurrencyCode, targetCurrencyCode));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        try {
            repository.updateExchangeRate(baseCurrencyCode, targetCurrencyCode, rate);
            var updatedExchangeRate = repository.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            if (updatedExchangeRate != null) {
                return objectMapper.writeValueAsString(updatedExchangeRate);
            } else {
                return "";
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public String exchange(String baseCurrencyCode, String targetCurrencyCode, Double amount) {
        ConvertedAmount convertedAmount = null;

        CurrencyModel baseCurrency = null;
        CurrencyModel targetCurrency = null;
        double rate = 0;

        try {
            var exchangeRate = repository.getExchangeRate(baseCurrencyCode, targetCurrencyCode);
            if (exchangeRate == null) {
                exchangeRate = repository.getExchangeRate(targetCurrencyCode, baseCurrencyCode);
                if (exchangeRate != null) {
                    baseCurrency = exchangeRate.targetCurrency();
                    targetCurrency = exchangeRate.baseCurrency();
                    rate = 1 / exchangeRate.rate();
                }
            } else {
                baseCurrency = exchangeRate.baseCurrency();
                targetCurrency = exchangeRate.targetCurrency();
                rate = exchangeRate.rate();
            }
            if (exchangeRate == null) {
                var exchangeRateUsdBaseCurrency = repository.getExchangeRate(USD_CURR_CODE, baseCurrencyCode);
                var exchangeRateUsdTargetCurrency = repository.getExchangeRate(USD_CURR_CODE, targetCurrencyCode);

                if (exchangeRateUsdBaseCurrency != null && exchangeRateUsdTargetCurrency != null) {
                    baseCurrency = exchangeRateUsdBaseCurrency.targetCurrency();
                    targetCurrency = exchangeRateUsdBaseCurrency.targetCurrency();
                    rate = exchangeRateUsdTargetCurrency.rate() / exchangeRateUsdBaseCurrency.rate();
                }
            }
            if (baseCurrency != null && targetCurrency != null) {
                convertedAmount = new ConvertedAmount(
                        baseCurrency,
                        targetCurrency,
                        rate,
                        amount,
                        amount * rate
                );
                return objectMapper.writeValueAsString(convertedAmount);
            } else {
                return "";
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
