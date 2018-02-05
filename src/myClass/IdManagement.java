/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClass;

import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
//import myClass.Log;

/**
 *
 * @author gerda.modarres
 */
public class IdManagement { // instead of 326 lines :-)
    
    static Log my_log;
  
    public static String get_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn, String selectID) {      
        String ids = null;
    
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            String id_rem = "";
            String id = null;
            while (rs.next()) {
                //this.rs_sizeList.add(rs.getString("array_sub_id"));
                switch (selectID) {
                    case "pat_id":
                        id = rs.getString("pat_id");        // chng sql --->  lab_id is needed for table_resultID
                        break;
                    case "lab_id":
                        id = rs.getString("lab_id");
                        break;
                    case "result_id":
                        id = rs.getString("result_id");
                        break;
                    default:
                        break;
                }
                if (!id.equals(id_rem)){
                    id_rem = id;
                    all_ids = all_ids +"'"+id+"',";
                }else{
                    //JOptionPane.showMessageDialog(null, "id already in list: " + id + "  "+ id_rem); // test
                }
                //Combobox_id.addItem(id);        // test
                //txtArea_test.append("'"+id+"',");  // test
            }
            //this.ids = all_ids;
            ids = all_ids;
            //JOptionPane.showMessageDialog(null,"IdManage: "+ids); // test

        } catch (HeadlessException | SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            my_log.logger.warning(e.toString() + "\n\t\t\t\t\t\tERROR-SOURCE-SQL: "+sql);
        } finally {
            try {
                //rs.close(); pst.close(); //conn.close();
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                //if (conn != null) { conn.close();}
            } catch (SQLException e) {
            }
        }
        return ids;
    }
    

}