package org.fizz_buzz.service;

import org.fizz_buzz.model.CurrencyModel;

public record ConvertedAmount(CurrencyModel baseCurrency,
                              CurrencyModel targetCurrency,
                              double rate,
                              double amount,
                              double convertedAmount) {
}
