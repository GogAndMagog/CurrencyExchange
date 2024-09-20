package org.fizz_buzz.model;

import org.fizz_buzz.model.entity.CurrencyEntity;
import org.fizz_buzz.model.entity.ExchangeRateEntity;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLiteRepository implements Repository {

    private final String DB_URL = "jdbc:sqlite::resource:ex1.db";
    private final static String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
    private volatile static Repository instance;

    private SQLiteRepository() {
    }

    public synchronized static Repository getInstance() {
        if (instance == null) {
            instance = new SQLiteRepository();
        }
        return instance;
    }

    static {
        try {
            Class.forName(SQLITE_JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //DELETE
    public String getUsers() {
        StringBuilder users = new StringBuilder();
        users.append("Test SELECT:");

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

                while (resultSet.next()) {
                    users.append("""
                            Id: %d Name: %s Lastname: %s Age: %d \n\r
                            """.formatted(Integer.parseInt(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("lastname"),
                            Integer.parseInt(resultSet.getString("age"))));
                }
            }
        } catch (SQLException e) {
            users.append(e.getMessage());
        }

        return users.toString();
    }

    //DELETE
    @Override
    public void putUser() {

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT COUNT(*) AS rowsNumber FROM users");
            int rowsNumber;
            if (rs.next()) {
                rowsNumber = rs.getInt("rowsNumber");
            } else {
                System.out.println("No rows found");
                return;
            }

            statement.executeUpdate(
                    "INSERT INTO users (id, name, lastname, age) VALUES(%s, 'TestName', 'TestLastName', 2);".formatted(rowsNumber));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CurrencyEntity> getCurrencies() {
        List<CurrencyEntity> currencies = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM currencies");
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

    @Override
    public CurrencyEntity getCurrency(String currCode) {
        CurrencyEntity currency = null;

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM currencies WHERE Code = '%s'".formatted(currCode));
            if (rs.next()) {
                currency = new CurrencyEntity(rs.getInt("ID"),
                        rs.getString("Code"),
                        rs.getString("FullName"),
                        rs.getString("Sign"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return currency;
    }

    @Override
    public void addCurrency(CurrencyEntity currency) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT INTO  Currencies (code, fullname, sign)\n" +
                    "values ('%s', '%s', '%s');".formatted(currency.code(), currency.fullName(), currency.sign()));
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRateEntity> getExchangeRates() {
        List<ExchangeRateEntity> exchangeRates = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery("""
                    SELECT ExchangeRates.ID,
                           BaseCurrencies.*,
                           TargetCurrencies.*,
                           ExchangeRates.Rate
                    FROM ExchangeRates
                             JOIN Currencies AS BaseCurrencies
                                  ON ExchangeRates.BaseCurrencyId = BaseCurrencies.ID
                             JOIN Currencies AS TargetCurrencies
                                  ON ExchangeRates.TargetCurrencyId = TargetCurrencies.ID;""");

            while (rs.next()) {
                exchangeRates.add(
                        new ExchangeRateEntity(rs.getInt(1),
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

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRates;
    }

    @Override
    public ExchangeRateEntity getExchangeRate(String baseCurrency, String targetCurrency) {
        ExchangeRateEntity exchangeRate = null;

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery("""
                    SELECT ExchangeRates.ID,
                           BaseCurrencies.*,
                           TargetCurrencies.*,
                           ExchangeRates.Rate
                    FROM ExchangeRates
                             JOIN Currencies AS BaseCurrencies
                                  ON ExchangeRates.BaseCurrencyId = BaseCurrencies.ID
                             JOIN Currencies AS TargetCurrencies
                                  ON ExchangeRates.TargetCurrencyId = TargetCurrencies.ID
                               WHERE BaseCurrencies.Code = '%s'
                                 AND TargetCurrencies.Code = '%s'""".formatted(baseCurrency, targetCurrency));

            if (rs.next()) {
                exchangeRate =
                        new ExchangeRateEntity(rs.getInt(1),
                                new CurrencyEntity(rs.getInt(2),
                                        rs.getString(3),
                                        rs.getString(4),
                                        rs.getString(5)),
                                new CurrencyEntity(rs.getInt(6),
                                        rs.getString(7),
                                        rs.getString(8),
                                        rs.getString(9)),
                                rs.getDouble(10));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRate;
    }

    @Override
    public void addExchangeRate(String baseCurrencyCode, String targetCurrencyCode, Double rate) {
        String query = """
                    INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
                    SELECT BaseCurrency.ID,
                           TargetCurrency.ID,
                           ?
                    FROM Currencies AS BaseCurrency
                             JOIN Currencies AS TargetCurrency
                    WHERE BaseCurrency.Code = ?
                      AND TargetCurrency.Code = ?;""";

        try (Connection connection = DriverManager.getConnection(DB_URL);
//             Statement statement = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDouble(1, rate);
            preparedStatement.setString(2, baseCurrencyCode);
            preparedStatement.setString(3, targetCurrencyCode);
            preparedStatement.executeUpdate();

            var rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
            }
//            statement.executeUpdate("""
//                    INSERT INTO ExchangeRates (BaseCurrencyId, TargetCurrencyId, Rate)
//                    SELECT BaseCurrency.ID,
//                           TargetCurrency.ID,
//                           %s
//                    FROM Currencies AS BaseCurrency
//                             JOIN Currencies AS TargetCurrency
//                    WHERE BaseCurrency.Code = '%s'
//                      AND TargetCurrency.Code = '%s';"""
//                    .formatted(rate, baseCurrencyCode, targetCurrencyCode));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateExchangeRate(String baseCurrencyCode, String targetCurrencyCode, Double rate) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    UPDATE ExchangeRates
                    SET Rate = %s
                    WHERE ExchangeRates.BaseCurrencyId = (SELECT ID FROM Currencies WHERE Code = '%s')
                      AND ExchangeRates.TargetCurrencyId = (SELECT ID FROM Currencies WHERE Code = '%s');"""
                    .formatted(rate, baseCurrencyCode, targetCurrencyCode));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
