package org.fizz_buzz.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fizz_buzz.dao.ExchangeRateDAO;
import org.fizz_buzz.dao.ExchangeRateSqliteDAO;
import org.fizz_buzz.model.ExchangeRate;
import org.fizz_buzz.util.ProjectConstants;

import java.util.Optional;

public class CurrencyExchangeJsonService implements CurrencyExchangeService<String> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ExchangeRateDAO exchangeRateDAO = ExchangeRateSqliteDAO.getInstance();

    private static CurrencyExchangeService<String> instance;

    private CurrencyExchangeJsonService() {
    }

    public synchronized static CurrencyExchangeService<String> getInstance() {
        if (instance == null) {
            instance = new CurrencyExchangeJsonService();
        }
        return instance;
    }

    @Override
    public String exchange(String baseCurrencyCode, String targetCurrencyCode, Double amount) {
        var exchangeRate = exchangeRateDAO.readByCodePair(baseCurrencyCode, targetCurrencyCode);

        if (exchangeRate.isEmpty()) {
            exchangeRate = getExchangeRateFromReversedExchangeRate(baseCurrencyCode, targetCurrencyCode);
        }

        if (exchangeRate.isEmpty()) {
            exchangeRate = getExchangeRateFromThirdCurrency(ProjectConstants.USD_CURR_CODE, baseCurrencyCode, targetCurrencyCode);
        }

        if (exchangeRate.isPresent()) {
            var convertedAmount = new ConvertedAmount(
                    exchangeRate.get().baseCurrency(),
                    exchangeRate.get().targetCurrency(),
                    exchangeRate.get().rate(),
                    amount,
                    amount * exchangeRate.get().rate());
            try {
                return objectMapper.writeValueAsString(convertedAmount);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "";
        }
    }

    private Optional<ExchangeRate> getExchangeRateFromReversedExchangeRate(String baseCurrencyCode, String targetCurrencyCode) {
        var reversedExchangeRate = exchangeRateDAO.readByCodePair(targetCurrencyCode, baseCurrencyCode);
        if (reversedExchangeRate.isPresent()) {
            return Optional.of(new ExchangeRate(-1,
                    reversedExchangeRate.get().targetCurrency(),
                    reversedExchangeRate.get().baseCurrency(),
                    1 / reversedExchangeRate.get().rate()));
        } else {
            return Optional.empty();
        }
    }

    private Optional<ExchangeRate> getExchangeRateFromThirdCurrency(String thirdCurrency,
                                                                    String baseCurrencyCode,
                                                                    String targetCurrencyCode) {
        var thirdCurrencyToBaseCode = exchangeRateDAO.readByCodePair(thirdCurrency, baseCurrencyCode);
        var thirdCurrencyToTargetCode = exchangeRateDAO.readByCodePair(thirdCurrency, targetCurrencyCode);

        if (thirdCurrencyToBaseCode.isPresent()
                && thirdCurrencyToTargetCode.isPresent()
                && thirdCurrencyToBaseCode.get().rate() != 0) {
            return Optional.of(new ExchangeRate(0,
                    thirdCurrencyToBaseCode.get().targetCurrency(),
                    thirdCurrencyToTargetCode.get().targetCurrency(),
                    thirdCurrencyToTargetCode.get().rate() / thirdCurrencyToBaseCode.get().rate()));
        } else {
            return Optional.empty();
        }
    }

}
