package org.fizz_buzz.model.dao;

import org.fizz_buzz.model.SQLConnectionManager;
import org.fizz_buzz.model.entity.CurrencyEntity;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencySqliteDAO implements CurrencyDAO {

    private final String DB_URL = "jdbc:sqlite::resource:ex1.db";

    @Override
    public Optional<CurrencyEntity> create(CurrencyEntity currency) {
        String query = "INSERT INTO Currencies (Code, FullName, Sign) VALUES (?, ?, ?)";

        try (Connection connection = SQLConnectionManager.getConnection() ;
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, currency.code());
            statement.setString(2, currency.fullName());
            statement.setString(3, currency.sign());
            statement.executeUpdate();
            var rs = statement.getGeneratedKeys();
            if (rs.next()) {
                return Optional.of(new CurrencyEntity(rs.getInt(1),
                        currency.code(),
                        currency.fullName(),
                        currency.sign()));
            } else {
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CurrencyEntity> readById(int id) {
        String query = "SELECT * FROM Currencies WHERE ID = ?";

        CurrencyEntity currency = null;

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);

            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                currency = new CurrencyEntity(rs.getInt("ID"),
                        rs.getString("Code"),
                        rs.getString("FullName"),
                        rs.getString("Sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(currency);
    }

    @Override
    public Optional<CurrencyEntity> readByCode(String code) {
        String query = "SELECT * FROM Currencies WHERE Code = ?";

        CurrencyEntity currency = null;

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);

            var rs = preparedStatement.executeQuery();
            if (rs.next()) {
                currency = new CurrencyEntity(rs.getInt("ID"),
                        rs.getString("Code"),
                        rs.getString("FullName"),
                        rs.getString("Sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return Optional.ofNullable(currency);
    }

    @Override
    public List<CurrencyEntity> readAll() {
        List<CurrencyEntity> currencies = new ArrayList<>();
        String query = "SELECT * FROM main.Currencies";

        try (Connection connection = SQLConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            var rs = statement.executeQuery();
            while (rs.next()) {
                currencies.add(new CurrencyEntity(rs.getInt("ID"),
                        rs.getString("Code"),
                        rs.getString("FullName"),
                        rs.getString("Sign")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currencies;
    }
}
