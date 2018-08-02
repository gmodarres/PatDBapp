/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClass;

import frames.ShowSQL;

/**
 *
 * @author gerda.modarres
 */
public class ShowSqlSelector {

    public static boolean ShowSqlIsOpen = false;
    static String sqlShowWindow = null;

    public static void showSqlInWindow(String sql, String source) {
        if (ShowSqlIsOpen == true) {
            sqlShowWindow = sql;
            ShowSQL.txtArea_showSQL.append(source + "  :   " + sqlShowWindow + "\n");

        } else {
            //new ShowSQL().setVisible(true);
            //ShowSqlIsOpen = true;
        }
    }
}
