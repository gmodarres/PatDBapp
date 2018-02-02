/*
 * stdpat_DB - Project study patient database 
 * For efficient data evaluation and interpretation
 *
 * Copyright (C) CCRI - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Gerda modarres <gerrda.modarres@ccri.at>, August 2017
 *
 */
package myClass;

import frames.SetConnection;
import static frames.SetConnection.devmode;
import java.sql.*;
import javax.swing.*;

/**
 *
 * Database connection
 */
public class DBconnect {

    Connection conn = null;

    public static String USER;
    public static String PWD;
    public static String CONN_STR;
        
    public static Connection ConnecrDb() {
                   
        try {
            // toggle 2 for TEST
            SetConnection.get_connData(); // toggle 1
            //JOptionPane.showMessageDialog(null,CONN_STR);
            Class.forName("com.mysql.jdbc.Driver");

            //String USER = "queryuser";
            //String PWD = "pwd";
            //String CONN_STR = "jdbc:mysql://localhost:3306/pat_db";
            
            Connection conn = DriverManager.getConnection(CONN_STR, USER, PWD);       // toggle 1
            
            //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pat_db", "queryuser", "pwd");      // toggle 2  ... OR:
            //Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pat_db?autoReconnect=true&useSSL=false", "queryuser", "pwd");   // toggle 2   
            //JOptionPane.showMessageDialog(null, "Connection Established: ");
        
            return conn;
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
}
