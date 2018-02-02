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
import net.proteanit.sql.DbUtils;

/**
 *
 * @author gerda.modarres
 */
public class IdManagement {
    
    private void get_queryLabIDs(String sql, PreparedStatement pst, ResultSet rs, Connection conn, javax.swing.JTable table_queryIDs){

        try{            
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            
            while(rs.next()){
                String id = rs.getString("result_id");
                all_ids = all_ids + "'" + id + "',";
                //txtArea_test.append("'"+id+"',");  // test
            }
            if (all_ids.length() > 1) {
                all_ids = all_ids.substring(0, (all_ids.length() - 1));

                String sql2 = "SELECT distinct s.lab_id, result_id, p.pat_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND result_id IN ( " + all_ids + " )";
                
                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();

                table_queryIDs.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_queryIDs);
                
                if (table_queryIDs.getColumnModel().getColumnCount() > 0) {
                    table_queryIDs.getColumnModel().getColumn(0).setPreferredWidth(85);      // 75
                    table_queryIDs.getColumnModel().getColumn(0).setMaxWidth(120);           // 80
                    table_queryIDs.getColumnModel().getColumn(1).setPreferredWidth(60);
                    table_queryIDs.getColumnModel().getColumn(1).setMaxWidth(60);
                    table_queryIDs.getColumnModel().getColumn(2).setPreferredWidth(60);
                    table_queryIDs.getColumnModel().getColumn(2).setMaxWidth(60);
                    table_queryIDs.getColumnModel().getColumn(3).setPreferredWidth(80);
                    table_queryIDs.getColumnModel().getColumn(3).setMaxWidth(130);
                    //table_queryIDs.getColumnModel().getColumn(4).setPreferredWidth(80);
                    //table_queryIDs.getColumnModel().getColumn(4).setMaxWidth(130);
                    table_queryIDs.getColumnModel().getColumn(5).setPreferredWidth(30);
                    table_queryIDs.getColumnModel().getColumn(5).setMaxWidth(30);
                    table_queryIDs.getColumnModel().getColumn(6).setPreferredWidth(90);      // 80
                    table_queryIDs.getColumnModel().getColumn(6).setMaxWidth(120);           // 80
                    //table_queryIDs.getColumnModel().getColumn(7).setPreferredWidth(30);
                    //table_queryIDs.getColumnModel().getColumn(7).setMaxWidth(30);
                }
            }
            //get ids of sql query to count patients affected in get_statistics()
            get_ids(sql, pst, rs, conn);

        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }
        
    private void get_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn, Log my_log, String ids) {
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            String id_rem = "";
            
            while (rs.next()) {
                //this.rs_sizeList.add(rs.getString("array_sub_id"));
                String id = rs.getString("result_id");
                if (!id.equals(id_rem)){
                    id_rem = id;
                    all_ids = all_ids +"'"+id+"',";
                }else{
                    //JOptionPane.showMessageDialog(null, "id already in list: " + id + "  "+ id_rem); // test
                }
                //Combobox_id.addItem(id);          // test
                //txtArea_test.append("'"+id+"',"); // test
            }
            this.ids = all_ids;

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
    }
    
    
}
