package org.fizz_buzz.model;

public record ExchangeRateModel(int ID,
                                CurrencyModel baseCurrency,
                                CurrencyModel targetCurrency,
                                double rate) {
}
