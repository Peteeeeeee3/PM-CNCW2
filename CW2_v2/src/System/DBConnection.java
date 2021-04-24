package System;

import java.sql.*;

public class DBConnection {
    private Connection connection;

    //connects to localhost
    public void connectToDB() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/bapers_v4", "root", "");
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
        } catch (SQLException sqle) {
            System.out.println("Database connection error: " + sqle.getMessage());
        }
    }

    //terminates connection to localhost
    public void disconnectFromDB() {
        try {
            connection.close();
        } catch (SQLException sqle) {
            System.out.println("Database disconnect error: " + sqle.getMessage());
        }
    }

    //handles reading from database. Call this function if reading is required.
    public ResultSet read(PreparedStatement sql) {
        try {
            //execute query
            ResultSet rs = sql.executeQuery();
            //commit changes
            connection.commit();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //handles writing to database. Call this function if writing is required.
    public void write(PreparedStatement sql) {
        try {
            //execute query
            sql.executeUpdate();
            //commit changes
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public DBConnection() {
        try {
            //initialise JDBC driver for class
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Database driver initialisation error: " + ex.getMessage());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
