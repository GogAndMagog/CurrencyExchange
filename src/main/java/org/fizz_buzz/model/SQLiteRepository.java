package org.fizz_buzz.model;

import java.sql.DriverManager;
import java.sql.Connection;
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
    public List<CurrencyModel> getCurrencies() {
        List<CurrencyModel> currencies = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM currencies");
            while (rs.next()) {
                currencies.add(new CurrencyModel(rs.getInt("ID"),
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
    public CurrencyModel getCurrency(String currCode) {
        CurrencyModel currency = null;

        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT * FROM currencies WHERE Code = '%s'".formatted(currCode));
            if (rs.next()) {
                currency = new CurrencyModel(rs.getInt("ID"),
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
    public void addCurrency(CurrencyModel currency) {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("INSERT into  Currencies (code, fullname, sign)\n" +
                    "values ('%s', '%s', '%s');".formatted(currency.code(), currency.fullName(), currency.sign()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExchangeRateModel> getExchangeRates() {
        List<ExchangeRateModel> exchangeRates = new ArrayList<>();

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
                        new ExchangeRateModel(Integer.parseInt(rs.getString(1)),
                                new CurrencyModel(Integer.parseInt(rs.getString(2)),
                                        rs.getString(3),
                                        rs.getString(4),
                                        rs.getString(5)),
                                new CurrencyModel(Integer.parseInt(rs.getString(6)),
                                        rs.getString(7),
                                        rs.getString(8),
                                        rs.getString(9)),
                                Double.parseDouble(rs.getString(10))));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return exchangeRates;
    }
}
