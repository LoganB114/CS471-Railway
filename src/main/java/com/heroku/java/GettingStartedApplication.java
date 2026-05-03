package com.heroku.java;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@Controller
public class GettingStartedApplication {
    private final DataSource dataSource;

    @Autowired
    public GettingStartedApplication(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @SuppressWarnings("unused")
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @SuppressWarnings("unused")
    @GetMapping("/database")
    String database(Map<String, Object> model) {
        System.out.println("Print statement inside the GettingstartedApplication.database() method. Logan Byrum");
        try (Connection connection = dataSource.getConnection()) {
            final var statement = connection.createStatement();
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS table_timestamp_and_random_string (tick timestamp, random_string varchar(50))");
            statement.executeUpdate("INSERT INTO table_timestamp_and_random_string VALUES (now(), '" + getRandomString() + "')");

            final var resultSet = statement.executeQuery("SELECT tick, random_string FROM table_timestamp_and_random_string");
            final var output = new ArrayList<>();
            while (resultSet.next()) {
                output.add("Read from DB: " + resultSet.getTimestamp("tick") + " " + resultSet.getString("random_string"));
            }

            model.put("records", output);
            return "database";

        } catch (Throwable t) {
            model.put("message", t.getMessage());
            return "error";
        }
    }

    private static String getRandomString() {
        StringBuilder output = new StringBuilder();
        Random rand = new Random();
        for (int i = 0; i < 10; i++) {
            output.append((char) ('a' + rand.nextInt(26)));
        }
        return output.toString();
    }

    public static void main(String[] args) {
        SpringApplication.run(GettingStartedApplication.class, args);
    }
}
