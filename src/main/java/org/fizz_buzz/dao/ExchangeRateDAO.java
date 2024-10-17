package org.fizz_buzz.dao;

import org.fizz_buzz.model.ExchangeRate;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO {
    Optional<ExchangeRate> create(String baseCurrencyCode, String targetCurrencyCode, double rate);
    List<ExchangeRate> readAll();
    Optional<ExchangeRate> readByCodePair(String baseCurrencyCode, String targetCurrencyCode);
    Optional<ExchangeRate> updateRate(String baseCurrencyCode, String targetCurrencyCode, double rate);
}
