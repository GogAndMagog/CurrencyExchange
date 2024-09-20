package org.fizz_buzz.model.dao;

import org.fizz_buzz.model.entity.ExchangeRateEntity;

import java.util.List;
import java.util.Optional;

public interface ExchangeRateDAO {
    Optional<ExchangeRateEntity> create(String baseCurrencyCode, String targetCurrencyCode, double rate);
    List<ExchangeRateEntity> readAll();
    Optional<ExchangeRateEntity> readByCodePair(String baseCurrencyCode, String targetCurrencyCode);
    Optional<ExchangeRateEntity> updateRate(String baseCurrencyCode, String targetCurrencyCode, double rate);
}
