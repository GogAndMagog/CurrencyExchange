package org.fizz_buzz.model.dao;

import org.fizz_buzz.model.SQLConnectionManager;
import org.fizz_buzz.model.entity.CurrencyEntity;
import org.fizz_buzz.model.entity.ExchangeRateEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateSqliteDAO implements ExchangeRateDAO {

    private final String DB_URL = "jdbc:sqlite::resource:ex1.db";

    @Override
    public Optional<ExchangeRateEntity> create(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        String query = """
                    INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
                    SELECT BaseCurrency.ID,
                           TargetCurrency.ID,
                           ?
                    FROM Currencies AS BaseCurrency
                             JOIN Currencies AS TargetCurrency
                    WHERE BaseCurrency.Code = ?
                      AND TargetCurrency.Code = ?;
                """;

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, rate);
            preparedStatement.setString(2, baseCurrencyCode);
            preparedStatement.setString(3, targetCurrencyCode);

            if (preparedStatement.executeUpdate() == 1) {
                return this.readByCodePair(baseCurrencyCode, targetCurrencyCode);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRateEntity> readAll() {
        List<ExchangeRateEntity> exchangeRates = new ArrayList<>();
        String query = """
                SELECT ExchangeRates.ID,
                       BaseCurrencies.*,
                       TargetCurrencies.*,
                       ExchangeRates.Rate
                FROM ExchangeRates
                         JOIN Currencies AS BaseCurrencies
                              ON ExchangeRates.BaseCurrencyId = BaseCurrencies.ID
                         JOIN Currencies AS TargetCurrencies
                              ON ExchangeRates.TargetCurrencyId = TargetCurrencies.ID""";
        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            var rs = statement.executeQuery();

            while (rs.next()) {
                exchangeRates.add(new ExchangeRateEntity(rs.getInt(1),
                        new CurrencyEntity(rs.getInt(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5)),
                        new CurrencyEntity(rs.getInt(6),
                                rs.getString(7),
                                rs.getString(8),
                                rs.getString(9)),
                        rs.getDouble(10)));
            }
            return exchangeRates;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRateEntity> readByCodePair(String baseCurrencyCode, String targetCurrency) {

        String query = """
                SELECT ExchangeRates.ID,
                       BaseCurrencies.*,
                       TargetCurrencies.*,
                       ExchangeRates.Rate
                FROM ExchangeRates
                         JOIN Currencies AS BaseCurrencies
                              ON ExchangeRates.BaseCurrencyId = BaseCurrencies.ID
                         JOIN Currencies AS TargetCurrencies
                              ON ExchangeRates.TargetCurrencyId = TargetCurrencies.ID
                           WHERE BaseCurrencies.Code = ?
                             AND TargetCurrencies.Code = ?""";

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, baseCurrencyCode);
            statement.setString(2, targetCurrency);
            var rs = statement.executeQuery();

            if (rs.next()) {
                return Optional.of(new ExchangeRateEntity(rs.getInt(1),
                        new CurrencyEntity(rs.getInt(2),
                                rs.getString(3),
                                rs.getString(4),
                                rs.getString(5)),
                        new CurrencyEntity(rs.getInt(6),
                                rs.getString(7),
                                rs.getString(8),
                                rs.getString(9)),
                        rs.getDouble(10)));
            } else {
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ExchangeRateEntity> updateRate(String baseCurrencyCode, String targetCurrencyCode, double rate) {
        String query = """
                UPDATE ExchangeRates
                SET Rate = ?
                WHERE ExchangeRates.BaseCurrencyId = (SELECT ID FROM Currencies WHERE Code = ?)
                  AND ExchangeRates.TargetCurrencyId = (SELECT ID FROM Currencies WHERE Code = ?);""";

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setDouble(1, rate);
            statement.setString(2, baseCurrencyCode);
            statement.setString(3, targetCurrencyCode);

            if (statement.executeUpdate() == 1) {
                return this.readByCodePair(baseCurrencyCode, targetCurrencyCode);
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
