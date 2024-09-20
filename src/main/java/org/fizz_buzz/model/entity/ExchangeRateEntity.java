package org.fizz_buzz.model.entity;

public record ExchangeRateEntity(int ID,
                                 CurrencyEntity baseCurrency,
                                 CurrencyEntity targetCurrency,
                                 double rate) {
}
