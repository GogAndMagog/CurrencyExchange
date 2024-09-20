package org.fizz_buzz.service;

import org.fizz_buzz.model.entity.CurrencyEntity;

public record ConvertedAmount(CurrencyEntity baseCurrency,
                              CurrencyEntity targetCurrency,
                              double rate,
                              double amount,
                              double convertedAmount) {
}
