package com.company;

import java.sql.*;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    public static void main(String[] args) {

        AtomicInteger count = new AtomicInteger(getId());
        System.out.println("Nachricht: ");

        while (true) {
            new Thread(() -> {
                while (true) {
                    //System.out.println("...");
                    if (count.get() != getId()) {
                        executeQuery();
                        System.out.println("Nachricht: ");
                        count.getAndIncrement();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            String message = getUserInput();
            executeUpdate(message);
        }
    }

    private static String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    private static void executeUpdate(String message) {
        Statement stmt = null;
        Connection conn = getConnectionToDb();
        String query = "INSERT INTO `messages`(`message`) VALUES ('" + message + "')";

        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static void executeQuery() {
        Statement stmt = null;
        Connection conn = getConnectionToDb();
        String query = "SELECT * from messages";
        System.out.println();
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String message = rs.getString("message");
                Date date = rs.getDate("created_at");
                System.out.println(date + " " + message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private static int getId() {
        Statement stmt = null;
        Connection conn = getConnectionToDb();
        String query = "SELECT * from messages";
        int id = 0;

        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return id;
    }

    private static Connection getConnectionToDb() {
        Connection conn = null;
        String url = "jdbc:mysql://localhost:3306/messenger?user=root";
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }
}
