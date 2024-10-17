package org.fizz_buzz.service;

import org.fizz_buzz.model.Currency;

public record ConvertedAmount(Currency baseCurrency,
                              Currency targetCurrency,
                              double rate,
                              double amount,
                              double convertedAmount) {
}
