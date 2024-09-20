package org.fizz_buzz.model.dao;

import org.fizz_buzz.model.entity.CurrencyEntity;

import java.util.List;
import java.util.Optional;

public interface CurrencyDAO {
    Optional<CurrencyEntity> create(CurrencyEntity currency);
    Optional<CurrencyEntity> readById(int id);
    Optional<CurrencyEntity> readByCode(String code);
    List<CurrencyEntity> readAll();
}
