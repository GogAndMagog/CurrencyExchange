package org.fizz_buzz.dao;

import org.fizz_buzz.exception.EntityAlreadyExists;
import org.fizz_buzz.model.Currency;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencySqliteDAO implements CurrencyDAO {

    private final static String COLUMN_ID = "ID";
    private final static String COLUMN_CODE = "Code";
    private final static String COLUMN_FULL_NAME = "FullName";
    private final static String COLUMN_SIGN = "Sign";

    private final static String CURRENCY = "currency";

    private static CurrencyDAO instance;

    private CurrencySqliteDAO() {
    }

    public synchronized static CurrencyDAO getInstance() {
        if (instance == null) {
            instance = new CurrencySqliteDAO();
        }
        return instance;
    }

    @Override
    public Optional<Currency> create(Currency currency) {
        String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, currency.code());
            statement.setString(2, currency.fullName());
            statement.setString(3, currency.sign());
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return Optional.of(new Currency(rs.getInt(1),
                        currency.code(),
                        currency.fullName(),
                        currency.sign()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 19) {
                throw new EntityAlreadyExists(CURRENCY);
            } else {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Optional<Currency> readById(int id) {
        String query = "SELECT * FROM Currencies WHERE ID = ?";

        Currency currency = null;

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                currency = new Currency(rs.getInt(COLUMN_ID),
                        rs.getString(COLUMN_CODE),
                        rs.getString(COLUMN_FULL_NAME),
                        rs.getString(COLUMN_SIGN));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(currency);
    }

    @Override
    public Optional<Currency> readByCode(String code) {
        String query = "SELECT * FROM Currencies WHERE Code = ?";

        Currency currency = null;

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);

            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                currency = new Currency(rs.getInt(COLUMN_ID),
                        rs.getString(COLUMN_CODE),
                        rs.getString(COLUMN_FULL_NAME),
                        rs.getString(COLUMN_SIGN));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(currency);
    }

    @Override
    public List<Currency> readAll() {
        List<Currency> currencies = new ArrayList<>();
        String query = "SELECT * FROM main.Currencies";

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            var rs = statement.executeQuery();
            while (rs.next()) {
                currencies.add(new Currency(rs.getInt(COLUMN_ID),
                        rs.getString(COLUMN_CODE),
                        rs.getString(COLUMN_FULL_NAME),
                        rs.getString(COLUMN_SIGN)));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currencies;
    }
}
