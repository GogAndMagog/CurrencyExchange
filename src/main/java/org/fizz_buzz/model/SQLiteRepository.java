package org.fizz_buzz.model;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLiteRepository implements Repository {
    private final String dbUrl = "jdbc:sqlite:C:\\Users\\user\\IdeaProjects\\Currency Exchange\\src\\main\\resources\\ex1.db";

    public String getUsers() {
        StringBuilder users = new StringBuilder();

        String name = "jdbc:sqlite::resource:ex1.db";

        try (Connection connection = DriverManager.getConnection(name);
             Statement statement = connection.createStatement()) {
            try (ResultSet resultSet = statement.executeQuery("SELECT * FROM users")) {

                while (resultSet.next()) {
                    users.append("""
                            Id: %d Name: %s Lastname: %s Age: %d
                            """.formatted(Integer.parseInt(resultSet.getString("id")),
                            resultSet.getString("name"),
                            resultSet.getString("lastname"),
                            Integer.parseInt(resultSet.getString("age"))));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users.toString();
    }

    @Override
    public void putUser() {
        String name = "jdbc:sqlite::resource:ex1.db";

        try (Connection connection = DriverManager.getConnection(name);
             Statement statement = connection.createStatement()) {
            var rs = statement.executeQuery("SELECT COUNT(*) AS rowsNumber FROM users");
            int rowsNumber;
            if (rs.next()) {
                rowsNumber = rs.getInt("rowsNumber");
            }
            else {
                System.out.println("No rows found");
                return;
            }

            statement.executeUpdate(
                        "INSERT INTO users (id, name, lastname, age) VALUES(%s, 'TestName', 'TestLastName', 2);".formatted(rowsNumber));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
