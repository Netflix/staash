package com.netflix.paas.storage.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlService {
    public static void createDbInMySql(String dbName) {
        System.out.println("-------- MySQL JDBC Connection Testing ------------");
        
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }
     
        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection = null;
        Statement stmt = null;
     
        try {
            connection = DriverManager
            .getConnection("jdbc:mysql://localhost:3306/","root", "");
            String sql = "CREATE DATABASE "+dbName;
            stmt = connection.createStatement();
            stmt.executeUpdate(sql);
            System.out.println("Database created successfully...");
     
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }
    }
    public static void createTableInDb(String schema, String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            System.out.println("Connecting to a selected database...");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+schema,"root", "");
            System.out.println("Connected database successfully...");
            
            System.out.println("Creating table in given database...");
            Statement stmt = conn.createStatement();
            
//            String sql = "CREATE TABLE REGISTRATION " +
//                         "(id INTEGER not NULL, " +
//                         " first VARCHAR(255), " + 
//                         " last VARCHAR(255), " + 
//                         " age INTEGER, " + 
//                         " PRIMARY KEY ( id ))"; 

            stmt.executeUpdate(query);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static void insertRowIntoTable(String db, String table, String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver");

            System.out.println("Connecting to a selected database...");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db,"root", "");
            System.out.println("Connected database successfully...");
            
            System.out.println("Creating table in given database...");
            Statement stmt = conn.createStatement();
            
//            String sql = "CREATE TABLE REGISTRATION " +
//                         "(id INTEGER not NULL, " +
//                         " first VARCHAR(255), " + 
//                         " last VARCHAR(255), " + 
//                         " age INTEGER, " + 
//                         " PRIMARY KEY ( id ))"; 

            stmt.executeUpdate(query);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public static ResultSet executeRead(String db, String query) {
        // TODO Auto-generated method stub
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/"+db,"root", "");
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(query);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

}
