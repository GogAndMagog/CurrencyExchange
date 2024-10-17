package org.fizz_buzz.dao;

import org.fizz_buzz.model.Currency;

import java.util.List;
import java.util.Optional;

public interface CurrencyDAO {
    Optional<Currency> create(Currency currency);
    Optional<Currency> readById(int id);
    Optional<Currency> readByCode(String code);
    List<Currency> readAll();
}
