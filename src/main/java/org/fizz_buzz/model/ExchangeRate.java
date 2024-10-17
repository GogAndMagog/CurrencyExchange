package org.fizz_buzz.model;

public record ExchangeRate(int ID,
                           Currency baseCurrency,
                           Currency targetCurrency,
                           double rate) {
}
