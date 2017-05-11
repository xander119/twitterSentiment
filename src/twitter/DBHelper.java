package twitter;

import weka.core.Instances;
import weka.experiment.InstanceQuery;

import java.sql.*;

/**
 * Created by Zechen on 2016/10/31.
 */
public class DBHelper {
    public static String url = "jdbc:mysql://localhost:3306/twitterResearch";
    public static String username = "root";
    public static String password = "root";

    public static Connection getConnection() {
        Connection connection = null;
        String url = "jdbc:mysql://localhost:3306/twitterResearch?useLegacyDatetimeCode=false";
        String username = "root";
        String password = "root";
        //url, username, password
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("No such Driver");
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection(url, username, password);

        } catch (SQLException e) {
            System.out.println("No such Database");
            e.printStackTrace();
        }
        return connection;
    }

    public static ResultSet excSelectQuery(Connection connection, String statement) {

        try {
            Statement connectionStatement = connection.createStatement();
            return connectionStatement.executeQuery(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static int excUpdateQuery(Connection connection, String statement) {

        try {
            Statement connectionStatement = connection.createStatement();
            return connectionStatement.executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public static Instances getTweetInstancesFromDB(String sql) {
        InstanceQuery instanceQuery = null;
        try {
            instanceQuery = new InstanceQuery();
            instanceQuery.setDatabaseURL(url);
            instanceQuery.setUsername(username);
            instanceQuery.setPassword(password);

            instanceQuery.setQuery(sql);
            return instanceQuery.retrieveInstances();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

}
