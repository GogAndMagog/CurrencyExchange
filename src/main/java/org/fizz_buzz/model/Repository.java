package org.fizz_buzz.model;

import org.fizz_buzz.controller.CurrencyModel;

import java.util.List;

public interface Repository {
    String getUsers();
    void putUser();
    List<CurrencyModel> getCurrencies();
    CurrencyModel getCurrency(String currCode);
    void addCurrency(CurrencyModel currency);
}
