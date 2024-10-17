package org.fizz_buzz.service;

public interface CurrencyExchangeService <T> {
    T exchange(String baseCurrencyCode, String targetCurrencyCode, Double amount);
}
