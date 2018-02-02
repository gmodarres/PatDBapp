/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
//import myClass.Log;

/**
 *
 * @author gerda.modarres
 */
public class IdManagement {
    
    static Log my_log;
    
    /*public static String get_r_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn) {
        String x_resultIDs=null;
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_r_ids = "";
            String r_id_rem = "";
            while (rs.next()) {
                String r_id = rs.getString("result_id");

                if (!r_id.equals(r_id_rem)) {
                    r_id_rem = r_id;
                    all_r_ids = all_r_ids + "'" + r_id + "',";
                } else {
                    //JOptionPane.showMessageDialog(null, "id already in list: " + id + "  "+ id_rem); // test
                }
                //txtArea_test.append("'"+r_id+"',");  // test
            }

            //this.ST_resultIDs = all_r_ids;
            x_resultIDs = all_r_ids;
            //JOptionPane.showMessageDialog(null,x_resultIDs); // test

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                //rs.close(); pst.close(); //conn.close();
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                //if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
        return x_resultIDs;
    }*/
    
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
                if (selectID.equals("pat_id")){
                    id = rs.getString("pat_id");        // chng sql --->  lab_id is needed for table_resultID
                } else if (selectID.equals("lab_id")){
                    id = rs.getString("lab_id");
                }else if (selectID.equals("result_id")){
                    id = rs.getString("result_id");
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
            JOptionPane.showMessageDialog(null,"IdManage: "+ids); // test

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            my_log.logger.warning(e.toString() + "\n\t\t\t\t\t\tERROR-SOURCE-SQL: "+sql);
        } finally {
            try {
                //rs.close(); pst.close(); //conn.close();
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                //if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
        return ids;
    }
    

}