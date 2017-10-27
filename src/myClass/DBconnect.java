package myClass;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gerda.modarres
 */
import frames.SetConnection;
import static frames.SetConnection.devmode;
import java.sql.*;
import javax.swing.*;

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
