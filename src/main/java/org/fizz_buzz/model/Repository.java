package org.fizz_buzz.model;

import org.fizz_buzz.model.entity.CurrencyEntity;
import org.fizz_buzz.model.entity.ExchangeRateEntity;

import java.util.List;

public interface Repository {
    String getUsers();
    void putUser();

    List<CurrencyEntity> getCurrencies();
    CurrencyEntity getCurrency(String currCode);
    void addCurrency(CurrencyEntity currency);

    List<ExchangeRateEntity> getExchangeRates();
    ExchangeRateEntity getExchangeRate(String baseCurrency, String targetCurrency);
    void addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, Double rate);
    void updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, Double rate);
}
