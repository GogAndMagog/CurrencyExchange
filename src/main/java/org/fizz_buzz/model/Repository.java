package org.fizz_buzz.model;

import java.util.List;

public interface Repository {
    String getUsers();
    void putUser();

    List<CurrencyModel> getCurrencies();
    CurrencyModel getCurrency(String currCode);
    void addCurrency(CurrencyModel currency);

    List<ExchangeRateModel> getExchangeRates();
    ExchangeRateModel getExchangeRate(String baseCurrency, String targetCurrency);
    void addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, Double rate);
}
