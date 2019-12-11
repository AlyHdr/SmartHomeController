package com.emse.spring.faircorp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    final static String username = "sa";
    final static String password = "";
    final static String database_url = "jdbc:h2:mem:bigcorp;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
    Connection connection;
    public DatabaseHandler() {
        try {
            Class.forName("org.h2.Driver"); // 1.
        }
        catch (ClassNotFoundException e) {
            System.out.println("Unable to load JDBC Driver: "+ e);
        }
        try {
            this.connection = DriverManager.getConnection(database_url, username, password); // 3.
            System.out.println("Connection Success ! ---------------------------------------");
        }
        catch (SQLException e) {
            System.out.println("Unable to connect to the database: "+ e);
        }
    }
}
