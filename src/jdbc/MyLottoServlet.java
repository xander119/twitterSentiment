package jdbc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class MyLottoServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ArrayList<Integer> numbers = new ArrayList<Integer>();

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<HTML>");
        out.println("<HEAD>");
        out.println("<TITLE>Lotto Template</TITLE>");
        out.println("</HEAD>");
        out.println("<BODY>");
        out.println("</HEAD>");

        out.println("<BODY>");


        boolean duplicate = false;
        for (int i = 1; i < 7; i++) {
            String idxName = Integer.toString(i);
            String number = request.getParameter(idxName);

            if (number == null || number.equals("")) {
                break;
            }

            int num = Integer.valueOf(number);
            if (numbers.contains(num)) {
                duplicate = true;
            }
            numbers.add(num);
        }

        int size = numbers.size();

        boolean found = false;
        for (Integer number : numbers) {
            if (number < 1 || number > 42) {
                found = true;
            }
        }

        if (found) {
            out.println("Number cannot be greater than 42 or less than 1.<br/><INPUT type=button value=Back onclick=window.history.back()>");
        } else if (duplicate) {
            out.println("Number cannot be duplicated.<br/><INPUT type=button value=Back onclick=window.history.back()>");
        } else if (size != 6) {
            out.println("Not Enough Value.Please do again.<br/><INPUT type=button value=Back onclick=window.history.back()>");
        } else {
            Collections.sort(numbers);

            saveToDB(numbers);

            StringBuilder builder = new StringBuilder();
            for (Map.Entry entry : readFromDB().entrySet()) {
                builder.append(entry.getKey());
                builder.append(entry.getValue().toString());
                builder.append("<br/>");
            }

            out.println("The list of Numbers you have entered are :<br/>" + builder.toString()
                    + "<br/><INPUT type=button value=Back onclick=window.history.back()>");

        }
        out.println("</BODY>");
        out.println("</ HTML>");
        out.close();


    }

    public void saveToDB(ArrayList<Integer> numbers) {
        Connection connection = getConnection();
        PreparedStatement addNumber = null;

        try {
            addNumber = connection.prepareStatement("INSERT INTO lotto(first,second,thrid,fourth,fifth,sixth) VALUES(?, ?, ?, ?, ?, ?)");

            int index = 1;
            for (Integer number : numbers) {
                addNumber.setInt(index, number);
                index++;
            }

            addNumber.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, ArrayList<Integer>> readFromDB() {
        Map<String, ArrayList<Integer>> numbersMap = new HashMap<>();
        Connection connection = getConnection();
        Statement numberStatement = null;
        ArrayList<Integer> numbers = null;
        try {
            numberStatement = connection.createStatement();
            ResultSet resultSet = numberStatement.executeQuery("SELECT * FROM lotto");

            int count = 0;
            while (resultSet.next()) {
                numbers = new ArrayList<>();
                for (int colIndex = 0; colIndex < 7; colIndex++) {
                    numbers.add(resultSet.getInt(colIndex));
                }
                numbersMap.put("Guess " + (count++), numbers);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return numbersMap;
    }

    private Connection getConnection() {
        Connection connection = null;

        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("No such Driver");
            e.printStackTrace();
        }
        try {
            connection = DriverManager.getConnection("jdbc:odbc:Lotto");

        } catch (SQLException e) {
            System.out.println("No such Database");
            e.printStackTrace();
        }
        return connection;
    }
}
