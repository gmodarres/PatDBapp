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
package frames;

import static frames.ArrayQuery.AQ_resultIDs;
import static frames.PatientBrowse.PB_resultIDs;
import static frames.ResultWindow.updateIntrpr;
import static frames.SetConnection.personalConfig;
import static frames.SampleBrowse.SB_resultIDs;
import static frames.SubtypeBrowse.ST_resultIDs;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import myClass.DBconnect;
import myClass.ColumnFitAdapter;
import myClass.BoundsPopupMenuListener;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.TableModel;
import net.proteanit.sql.DbUtils;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import org.ini4j.Ini;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import myClass.CustomSorter;
import myClass.Log;
import myClass.OSDetector;

public class SearchResult extends javax.swing.JFrame {

    String ids = null;
    String mod_sql = null;
    
    String query_labIDs = null;
    String lab_ids = null;
    
    JTable outTable = null;  
    String defaultPath = null;

    static DefaultTableModel moveTableModel = null;
    static String source = null;        // Name for header in FreeTable
    static String tableMoving = null;   // Name of table that is moved to FreeTable, to set row sorter
    
    static String resultMoving = null;  // TEST 
    //static String resultIDMoving = null;
    static boolean IntrprWindowIsOpen = false;
    
    String click_result = null;
    static String click_lID = null;
    
    Log my_log;
    
    /**
     * Creates new form patDB_search_result
     */
    public SearchResult() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        getIniData();
        setIcons();
        initial_table_array();
        initial_table_fish();
        initial_table_zg_iscn();
        initial_table_zg_result();
        initial_table_statistics();
        initial_table_queryIDs();
        invokeTabs();
        table_array.getTableHeader().addMouseListener(new ColumnFitAdapter());
        table_fish.getTableHeader().addMouseListener(new ColumnFitAdapter()); 
        table_zg_iscn.getTableHeader().addMouseListener(new ColumnFitAdapter()); 
        // combobox wide        
        BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
        ComboBox_projPat.addPopupMenuListener( listener );
        ComboBox_projPat.setPrototypeDisplayValue("ItemWWW");
        
        ToolTipManager.sharedInstance().setEnabled(false);
        
        my_log.logger.info("open SearchResult()");
     }
    
    private void invokeTabs(){
        tab_array.getRootPane().setDefaultButton(A_btn_search);
        tab_main.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (tab_main.getSelectedIndex() == 0) {        // Array
                    tab_array.getRootPane().setDefaultButton(A_btn_search);
                } else if (tab_main.getSelectedIndex() == 1) { // Fish
                    tab_fish.getRootPane().setDefaultButton(F_btn_search);
                } else if (tab_main.getSelectedIndex() == 2) { // CG
                    tab_ZG.getRootPane().setDefaultButton(Z_btn_search);
                }
            }
        });
    }
    
    private void setIcons(){       
        JLabel lbl_array = new JLabel("Array");
        ImageIcon img1 = new javax.swing.ImageIcon(getClass().getResource("/ico/array_label_small.png"));
        lbl_array.setIcon(img1);
        lbl_array.setIconTextGap(10);
        tab_main.setTabComponentAt(0, lbl_array);
        
        JLabel lbl_fish = new JLabel("FISH");
        ImageIcon img2 = new javax.swing.ImageIcon(getClass().getResource("/ico/fish_label_small.png"));
        lbl_fish.setIcon(img2);
        lbl_fish.setIconTextGap(10);
        tab_main.setTabComponentAt(1, lbl_fish);
    
        JLabel lbl_zg = new JLabel("Cytogenetics");
        ImageIcon img3 = new javax.swing.ImageIcon(getClass().getResource("/ico/zg_label_small.png"));
        lbl_zg.setIcon(img3);
        lbl_zg.setIconTextGap(10);
        tab_main.setTabComponentAt(2, lbl_zg);
    }
    
    private void initial_table_array() {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String sql = "SELECT array_sub_id as ID, ma_nom, result_id, chr, arr_type as type, cnst, arr_call, size, loc_start, loc_end, cyto_regions AS cyto_regions, genes FROM arr_result";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_array.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_array);   
            // resize column width
            jScrollPane1.setViewportView(table_array);
            if (table_array.getColumnModel().getColumnCount() > 0) {
                table_array.getColumnModel().getColumn(0).setPreferredWidth(65);    // 50
                table_array.getColumnModel().getColumn(0).setMaxWidth(65);          // 50
                table_array.getColumnModel().getColumn(1).setPreferredWidth(300);   // ma_nom
                table_array.getColumnModel().getColumn(1).setMaxWidth(600);         // ma_nom

                table_array.getColumnModel().getColumn(2).setPreferredWidth(60);
                table_array.getColumnModel().getColumn(2).setMaxWidth(60);
                table_array.getColumnModel().getColumn(3).setPreferredWidth(55); // chr
                table_array.getColumnModel().getColumn(3).setMaxWidth(55);
                table_array.getColumnModel().getColumn(4).setPreferredWidth(55); // type
                table_array.getColumnModel().getColumn(4).setMaxWidth(100);
                table_array.getColumnModel().getColumn(5).setPreferredWidth(60);
                table_array.getColumnModel().getColumn(5).setMaxWidth(60);
                table_array.getColumnModel().getColumn(6).setPreferredWidth(110);
                table_array.getColumnModel().getColumn(6).setMaxWidth(200);
                table_array.getColumnModel().getColumn(7).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(7).setMaxWidth(100);
                table_array.getColumnModel().getColumn(8).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(8).setMaxWidth(100);
                table_array.getColumnModel().getColumn(9).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(9).setMaxWidth(100);
                table_array.getColumnModel().getColumn(10).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(10).setMaxWidth(200);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {                                                         // TODO:  Variante ...? besser als unten? 
            try { rs.close(); } catch (Exception e) { /* ignored */ }
            try { pst.close(); } catch (Exception e) { /* ignored */ }
            try { conn.close(); } catch (Exception e) { /* ignored */ }
          }
    }

    private void update_table_array(){              // is needed for lab_id search in A_txt_lab_id
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        String sql = "SELECT array_sub_id as ID, ma_nom, a.result_id, chr, arr_type as type, cnst, arr_call, size, loc_start, loc_end, cyto_regions AS cyto_regions, genes FROM arr_result a, main_result m " +
            "WHERE a.result_id=m.result_id AND lab_ID=?";
        
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, A_txt_lab_id.getText());
            rs = pst.executeQuery();
            //if (rs.next()) {      // this skips first row!   
            table_array.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_array); 

            // resize column width
            jScrollPane1.setViewportView(table_array);
            if (table_array.getColumnModel().getColumnCount() > 0) {
                table_array.getColumnModel().getColumn(0).setPreferredWidth(65);    // 50
                table_array.getColumnModel().getColumn(0).setMaxWidth(65);          // 50
                table_array.getColumnModel().getColumn(1).setPreferredWidth(300);   // ma_nom
                table_array.getColumnModel().getColumn(1).setMaxWidth(600);         // ma_nom

                table_array.getColumnModel().getColumn(2).setPreferredWidth(60);
                table_array.getColumnModel().getColumn(2).setMaxWidth(60);
                table_array.getColumnModel().getColumn(3).setPreferredWidth(55); // chr
                table_array.getColumnModel().getColumn(3).setMaxWidth(55);
                table_array.getColumnModel().getColumn(4).setPreferredWidth(55); // type
                table_array.getColumnModel().getColumn(4).setMaxWidth(100);
                table_array.getColumnModel().getColumn(5).setPreferredWidth(60);
                table_array.getColumnModel().getColumn(5).setMaxWidth(60);
                table_array.getColumnModel().getColumn(6).setPreferredWidth(110);
                table_array.getColumnModel().getColumn(6).setMaxWidth(200);
                table_array.getColumnModel().getColumn(7).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(7).setMaxWidth(100);
                table_array.getColumnModel().getColumn(8).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(8).setMaxWidth(100);
                table_array.getColumnModel().getColumn(9).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(9).setMaxWidth(100);
                table_array.getColumnModel().getColumn(10).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(10).setMaxWidth(200);
            }
            //} //END if(rs.next)
            if (table_array.getRowCount() == 0) {
                JOptionPane.showMessageDialog(null, "lab_id does not exist!");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }
   
    private void initial_table_fish(){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        
        String sql = "SELECT fish_sub_id as ID, result_id, i_count, m_count, percent, region1, reg1_sig as sig1, region2, reg2_sig as sig2, fsn_sig, fish_chng, result, material FROM fish_result r\n"
                + ", fish_probe p WHERE r.probe_no=p.probe_no";

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            
            //ResultSetMetaData rsmd = rs.getMetaData();
            //String col = rs.getMetaData().getColumnTypeName(5);  // Info COLUMN CLASSES!!! ???
            //JOptionPane.showMessageDialog(null, rsmd);
            
            table_fish.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_fish);   // not working ?

            // resize column width
            jScrollPane2.setViewportView(table_fish);
            if (table_fish.getColumnModel().getColumnCount() > 0) {
                table_fish.getColumnModel().getColumn(0).setPreferredWidth(65); //55
                table_fish.getColumnModel().getColumn(0).setMaxWidth(65);       //55

                table_fish.getColumnModel().getColumn(1).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(1).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(2).setPreferredWidth(55);
                table_fish.getColumnModel().getColumn(2).setMaxWidth(55);

                table_fish.getColumnModel().getColumn(3).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(3).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(4).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(4).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(5).setPreferredWidth(100);
                table_fish.getColumnModel().getColumn(5).setMaxWidth(300);
                table_fish.getColumnModel().getColumn(6).setPreferredWidth(45);
                table_fish.getColumnModel().getColumn(6).setMaxWidth(45);
                table_fish.getColumnModel().getColumn(7).setPreferredWidth(100);
                table_fish.getColumnModel().getColumn(7).setMaxWidth(300);
                table_fish.getColumnModel().getColumn(8).setPreferredWidth(45);
                table_fish.getColumnModel().getColumn(8).setMaxWidth(45);

                table_fish.getColumnModel().getColumn(9).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(9).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(10).setPreferredWidth(100);  // fish_chng
                table_fish.getColumnModel().getColumn(10).setMaxWidth(500);

                table_fish.getColumnModel().getColumn(12).setPreferredWidth(110); // material
                table_fish.getColumnModel().getColumn(12).setMaxWidth(200);
            }

        } catch (Exception e) {
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
  
    private void update_table_fish() {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        String sql = "SELECT fish_sub_id as ID, r.result_id, i_count, m_count, percent, region1, reg1_sig as sig1, region2, reg2_sig as sig2, fsn_sig, fish_chng, result, material "
                + "FROM fish_result r, fish_probe p, main_result m WHERE r.probe_no=p.probe_no AND r.result_id=m.result_id AND lab_id=?";
        
        try {

            pst = conn.prepareStatement(sql);
            pst.setString(1, F_txt_lab_id.getText());
            rs = pst.executeQuery();
            //ResultSetMetaData meta = rs.getMetaData();
            //int numberOfColumns = meta.getColumnCount();

            //if (rs.next()) {      // this skips first row!   
            table_fish.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_fish);
            // resize column width
            jScrollPane2.setViewportView(table_fish);
            if (table_fish.getColumnModel().getColumnCount() > 0) {
                table_fish.getColumnModel().getColumn(0).setPreferredWidth(65); //55
                table_fish.getColumnModel().getColumn(0).setMaxWidth(65);       //55

                table_fish.getColumnModel().getColumn(1).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(1).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(2).setPreferredWidth(55);
                table_fish.getColumnModel().getColumn(2).setMaxWidth(55);

                table_fish.getColumnModel().getColumn(3).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(3).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(4).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(4).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(5).setPreferredWidth(100);
                table_fish.getColumnModel().getColumn(5).setMaxWidth(300);
                table_fish.getColumnModel().getColumn(6).setPreferredWidth(45);
                table_fish.getColumnModel().getColumn(6).setMaxWidth(45);
                table_fish.getColumnModel().getColumn(7).setPreferredWidth(100);
                table_fish.getColumnModel().getColumn(7).setMaxWidth(300);
                table_fish.getColumnModel().getColumn(8).setPreferredWidth(45);
                table_fish.getColumnModel().getColumn(8).setMaxWidth(45);

                table_fish.getColumnModel().getColumn(9).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(9).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(10).setPreferredWidth(100);  // fish_chng
                table_fish.getColumnModel().getColumn(10).setMaxWidth(500);

                table_fish.getColumnModel().getColumn(12).setPreferredWidth(110); // material
                table_fish.getColumnModel().getColumn(12).setMaxWidth(200);
            }
            //}//END if(rs.next)
            if (table_fish.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(null, "lab_id does not exist!");
                }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) { rs.close(); }
                if (pst != null) { pst.close(); }
                if (conn != null) { conn.close(); }
            } catch (Exception e) {
            }
        }

    }
    
    private void initial_table_zg_iscn(){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String sql = "SELECT i.klon_id as Klon,i.result_id, chr_cnt as Chr, mitos_cnt as Mitosen, cp, iscn as ISCN, material, stim FROM zg_iscn i, main_result m Where i.result_id=m.result_id";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_zg_iscn.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_zg_iscn);

            // resize column width
            jScrollPane3.setViewportView(table_zg_iscn);
            if (table_zg_iscn.getColumnModel().getColumnCount() > 0) {
                table_zg_iscn.getColumnModel().getColumn(0).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(0).setMaxWidth(60);
                table_zg_iscn.getColumnModel().getColumn(1).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(1).setMaxWidth(60);
                table_zg_iscn.getColumnModel().getColumn(2).setPreferredWidth(45);
                table_zg_iscn.getColumnModel().getColumn(2).setMaxWidth(45);
                table_zg_iscn.getColumnModel().getColumn(3).setPreferredWidth(55);   
                table_zg_iscn.getColumnModel().getColumn(3).setMaxWidth(55);   
                table_zg_iscn.getColumnModel().getColumn(4).setPreferredWidth(50);   
                table_zg_iscn.getColumnModel().getColumn(4).setMaxWidth(50); 
                table_zg_iscn.getColumnModel().getColumn(5).setPreferredWidth(500);   // iscn
                table_zg_iscn.getColumnModel().getColumn(5).setMaxWidth(800);         // iscn
                table_zg_iscn.getColumnModel().getColumn(6).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(6).setMaxWidth(150);
                table_zg_iscn.getColumnModel().getColumn(7).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(7).setMaxWidth(200);
            }

        } catch (Exception e) {
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

    private void update_table_zg_iscn() {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String sql = "SELECT i.klon_id as Klon,i.result_id, chr_cnt as Chr, mitos_cnt as Mitosen, cp, iscn as ISCN, material, stim FROM zg_iscn i, main_result m"
                    + " WHERE i.result_id=m.result_id AND lab_id=?";

            pst = conn.prepareStatement(sql);
            pst.setString(1, Z_txt_lab_id.getText());
            rs = pst.executeQuery();
            table_zg_iscn.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_zg_iscn);

            // resize column width
            jScrollPane3.setViewportView(table_zg_iscn);
            if (table_zg_iscn.getColumnModel().getColumnCount() > 0) {
                table_zg_iscn.getColumnModel().getColumn(0).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(0).setMaxWidth(60);
                table_zg_iscn.getColumnModel().getColumn(1).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(1).setMaxWidth(60);
                table_zg_iscn.getColumnModel().getColumn(2).setPreferredWidth(45);
                table_zg_iscn.getColumnModel().getColumn(2).setMaxWidth(45);
                table_zg_iscn.getColumnModel().getColumn(3).setPreferredWidth(55);
                table_zg_iscn.getColumnModel().getColumn(3).setMaxWidth(55);
                table_zg_iscn.getColumnModel().getColumn(4).setPreferredWidth(50);
                table_zg_iscn.getColumnModel().getColumn(4).setMaxWidth(50);
                table_zg_iscn.getColumnModel().getColumn(5).setPreferredWidth(500);   // iscn
                table_zg_iscn.getColumnModel().getColumn(5).setMaxWidth(800);         // iscn
                table_zg_iscn.getColumnModel().getColumn(6).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(6).setMaxWidth(150);
                table_zg_iscn.getColumnModel().getColumn(7).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(7).setMaxWidth(200);
            }
        } catch (Exception e) {
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
    
    private void initial_table_zg_result() {
        DefaultTableModel model = (DefaultTableModel) table_zg_result.getModel();
        model.setRowCount(0);
        jScrollPane4.setViewportView(table_zg_result);
        if (table_zg_result.getColumnModel().getColumnCount() > 0) {
            table_zg_result.getColumnModel().getColumn(0).setPreferredWidth(60);
            table_zg_result.getColumnModel().getColumn(0).setMaxWidth(60);
            table_zg_result.getColumnModel().getColumn(1).setPreferredWidth(70);
            table_zg_result.getColumnModel().getColumn(1).setMaxWidth(70);
            table_zg_result.getColumnModel().getColumn(2).setPreferredWidth(60);
            table_zg_result.getColumnModel().getColumn(2).setMaxWidth(60);
            table_zg_result.getColumnModel().getColumn(3).setPreferredWidth(70);
            table_zg_result.getColumnModel().getColumn(3).setMaxWidth(100);
            table_zg_result.getColumnModel().getColumn(4).setPreferredWidth(50);
            table_zg_result.getColumnModel().getColumn(4).setMaxWidth(70);
        }
    }
        
    private void update_table_zg_result(){ /* EMPTY */ }
    
    private void initial_table_statistics(){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String sql = "SELECT  ( SELECT COUNT(*) FROM  patient ) AS patients, " +
            "        ( SELECT COUNT(*) FROM arr_result ) AS array, " +
            "        ( SELECT COUNT(*) FROM fish_result ) AS fish, " +
            "        ( SELECT COUNT(*) FROM zg_result ) AS ZG, "+
            "        (0) AS query, "+
            "        (0) AS \"result IDs\", "+
            "        (0) AS \"pat affected\"";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_statistics.setModel(DbUtils.resultSetToTableModel(rs));
            jScrollPane7.setViewportView(table_statistics);
            if (table_statistics.getColumnModel().getColumnCount() > 0) {
                table_statistics.getColumnModel().getColumn(0).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(0).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(1).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(1).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(2).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(2).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(3).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(3).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(4).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(4).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(5).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(5).setMaxWidth(100);
            }
            
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
    
    private void get_statistics(String sql){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try{
            // from get_ids()  ... 1,2,3,4,5,  --> get rid of "," at the end:
            String ids = this.ids;
            if (ids.length() > 1) {
                ids = ids.substring(0, (ids.length() - 1));
            }else{
                //JOptionPane.showMessageDialog(null, "no IDs");
                ids = "0";
            }

            String sql2 = "SELECT  ( SELECT COUNT(*) FROM  patient ) AS patients, \n" +
            "( SELECT COUNT(*) FROM  arr_result ) AS array, \n" +
            "( SELECT COUNT(*) FROM fish_result ) AS fish, \n" +
            "( SELECT COUNT(*) FROM zg_result) AS ZG, \n" +
            "( SELECT COUNT(*) FROM ( " + sql + " ) AS m) AS query, \n" +
            "( SELECT COUNT(*) FROM ( SELECT p.pat_id FROM patient p, sample s, main_result m WHERE p.pat_id=s.pat_id AND s.lab_id=m.lab_id AND result_id in ( "+ ids + " ) ) AS c ) AS \"result IDs\", \n" +
            "( SELECT COUNT(*) FROM ( SELECT distinct p.pat_id FROM patient p, sample s, main_result m WHERE p.pat_id=s.pat_id AND s.lab_id=m.lab_id AND result_id in ( "+ ids + " ) ) AS c ) AS \"pat affected\"";

            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();
            table_statistics.setModel(DbUtils.resultSetToTableModel(rs));
            jScrollPane7.setViewportView(table_statistics);
            if (table_statistics.getColumnModel().getColumnCount() > 0) {
                table_statistics.getColumnModel().getColumn(0).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(0).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(1).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(1).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(2).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(2).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(3).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(3).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(4).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(4).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(5).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(5).setMaxWidth(100);
            }
            //txtArea_test.setText(ids);           
                    
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
    
    private void initial_table_queryIDs(){
    
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT distinct s.lab_id, result_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id";

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
    
    private void get_queryLabIDs(String sql, PreparedStatement pst, ResultSet rs, Connection conn){

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
    
    private void get_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn) {
        
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
    
    private void deliver_ids(String caller, String ids, String sql) {
        // to extend sql in Array search
        if (ids.length() > 1){
            ids = ids.substring(0, (ids.length() - 1));
            if (caller.equals("A_btn_searchActionPerformed")){
                this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
            } else if (caller.equals("F_btn_searchActionPerformed")){
                this.mod_sql = sql + " AND result_id in(" + ids + ")";
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Why would you do that? ... there's nothing in there!");
        }
    }
    
    private void deliver_SB_ids(String caller, String sql) {  // ids from SampleBrose
        // to extend sql in Array search
        try {
            String ids = SB_resultIDs;
            //JOptionPane.showMessageDialog(null,"SB_IDs:   "+ ids);    //TEST

            if (ids == null) {
                JOptionPane.showMessageDialog(null, "Why would you do that? ... there's nothing in there!");
            } else if (ids.length() > 1) {
                ids = ids.substring(0, (ids.length() - 1));
                if (caller.equals("A_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                } else if (caller.equals("F_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND result_id in(" + ids + ")";
                } else if (caller.equals("Z_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                }
            }      
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    private void deliver_ST_ids(String caller, String sql) {    // ids from SubtypeBrowse
        // to extend sql in Array search
        try {
            String ids = ST_resultIDs;

            if (ids == null) {
                JOptionPane.showMessageDialog(null, "Why would you do that? ... there's nothing in there!");
            } else if (ids.length() > 1) {
                ids = ids.substring(0, (ids.length() - 1));
                if (caller.equals("A_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                } else if (caller.equals("F_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND result_id in(" + ids + ")";
                } else if (caller.equals("Z_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                }
            }

        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    private void deliver_PB_ids(String caller, String sql) {    // ids from SubtypeBrowse
        // to extend sql in Array search
        try {
            String ids = PB_resultIDs;

            if (ids == null) {
                JOptionPane.showMessageDialog(null, "Why would you do that? ... there's nothing in there!");
            } else if (ids.length() > 1) {
                ids = ids.substring(0, (ids.length() - 1));
                if (caller.equals("A_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                } else if (caller.equals("F_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND result_id in(" + ids + ")";
                } else if (caller.equals("Z_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                }
            }
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }
    
    private void deliver_Proj_ids(String sql, String set){ 
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        String proj_id = "";
        //String projPat = (String) ComboBox_projPat.getSelectedItem();
        if(ComboBox_projPat.getSelectedItem().toString().equals("MS_ALL_Array_Diagnostics")){
            proj_id = "1";
        }else if(ComboBox_projPat.getSelectedItem().toString().equals("TEST")){
            proj_id = "2";
        }else{
            proj_id = "0";
        }
        try {
            String sql2 = "SELECT m.*, s.pat_id, p.pat_id FROM main_result m, sample s, pat_inproject p"
                    + " where m.lab_id=s.lab_id"
                    + " and s.pat_id=p.pat_id"
                    + " and p.proj_id='" + proj_id + "'";

            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();

            get_ids(sql2, pst, rs, conn);
            if (ids.length() > 1){
                ids = ids.substring(0, (ids.length() - 1));
                if (set.equals("A")){
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                } else if (set.equals("F")){
                    this.mod_sql = sql + " AND result_id in(" + ids + ")";
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "Something is wrong with project patient's id list!");
            }
            display_ids();
            
        } catch (Exception e) {
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
    
     private void deliver_Stdy_ids(String sql, String set){ 
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        String stdy_id = "";
        //String projPat = (String) ComboBox_projPat.getSelectedItem();
        if(ComboBox_stdyPat.getSelectedItem().toString().equals("ALL BFM 2009")){
            stdy_id = "1";
        }else if(ComboBox_stdyPat.getSelectedItem().toString().equals("TEST")){
            stdy_id = "2";
        }else{ // no study assigned
            stdy_id = "0";
        }
        
        try {
            String sql2 = "SELECT m.*, s.pat_id, y.pat_id FROM main_result m, sample s, pat_instudy y"
                    + " where m.lab_id=s.lab_id"
                    + " and s.pat_id=y.pat_id"
                    + " and y.stdy_id='" + stdy_id + "'";

            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();

            get_ids(sql2, pst, rs, conn);
            if (ids.length() > 1){
                ids = ids.substring(0, (ids.length() - 1));
                if (set.equals("A")){
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                } else if (set.equals("F")){
                    this.mod_sql = sql + " AND result_id in(" + ids + ")";
                }
            }
            else{
                JOptionPane.showMessageDialog(null, "Something is wrong with project patient's id list!");
            }
            display_ids();
            
        } catch (Exception e) {
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
     
private void deliver_AQ_ids(String caller, String sql) {  // ids from ArrayQuery
        // to extend sql in Array search
        try {
            String ids = AQ_resultIDs;
            //JOptionPane.showMessageDialog(null,"AQ_IDs:   "+ ids);    //TEST

            if (ids == null) {
                JOptionPane.showMessageDialog(null, "Why would you do that? ... there's nothing in there!");
            } else if (ids.length() > 1) {
                ids = ids.substring(0, (ids.length() - 1));
                if (caller.equals("A_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                } else if (caller.equals("F_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND result_id in(" + ids + ")";
                } else if (caller.equals("Z_btn_searchActionPerformed")) {
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";
                }
            }      
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }     
     
    private void highlight_gene(String gene, Integer no){
        try{
            String text  = txtArea_genes.getText().toLowerCase();
            String findWord = gene.toLowerCase();
            int index = text.indexOf(findWord);
            
            Highlighter highlighter = txtArea_genes.getHighlighter();
            HighlightPainter painter = null;
            if (no==1){
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.yellow);
            } else if (no==2){
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.cyan);
            } else if (no==3){
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.orange);
            } else if (no==4){ 
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.pink);
            } else if (no==5){ 
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.green);
            }
            while (index >= 0) {  // indexOf returns -1 if no match found
                //JOptionPane.showMessageDialog(null,"find: "+findWord+" index: "+ index);
                int p0 = index;
                int p1 = p0 + findWord.length();
                highlighter.addHighlight(p0, p1, painter);
                index = text.indexOf(findWord, index + 1);
            }   
        }catch(Exception e){
        }     
    }
    
    private void highlight_creg(String gene, Integer no){
        try{
            String text  = txtArea_Creg.getText().toLowerCase();
            String findWord = gene.toLowerCase();
            int index = text.indexOf(findWord);
            
            Highlighter highlighter = txtArea_Creg.getHighlighter();
            HighlightPainter painter = null;
            if (no==1){
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.yellow);
            } else if (no==2){
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.cyan);
            }
            while (index >= 0) {  // indexOf returns -1 if no match found
                //JOptionPane.showMessageDialog(null,"find: "+findWord+" index: "+ index);
                int p0 = index;
                int p1 = p0 + findWord.length();
                highlighter.addHighlight(p0, p1, painter);
                index = text.indexOf(findWord, index + 1);
            }
            
        }catch(Exception e){
        }    
    }
    
    // For Testing (toggle .setText()-lines)
    private void display_ids(){
        if (this.ids.length() >0){
            String display_ids=this.ids.substring(0, (ids.length() - 1));  
            //txtArea_test.setText(display_ids);
            //txtArea_genes.setText(display_ids);
        }
    }
    
    private static boolean isRightClick(MouseEvent e) {
    return (e.getButton()==MouseEvent.BUTTON3 ||
            (System.getProperty("os.name").contains("Mac OS X") &&
                    (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 &&
                    (e.getModifiers() & InputEvent.CTRL_MASK) != 0));
    }
    
public void toExcel(JTable table, File file){
    //https://sites.google.com/site/teachmemrxymon/java/export-records-from-jtable-to-ms-excel
    
    try{
        TableModel model = table.getModel();
        FileWriter excel = new FileWriter(file);
        
        for(int i = 0; i < model.getColumnCount(); i++){
            excel.write(model.getColumnName(i) + "\t");
        }
        excel.write("\n");
               
        for(int i=0; i< model.getRowCount(); i++) {
            for(int j=0; j < model.getColumnCount(); j++) {
                //excel.write(model.getValueAt(i,j).toString()+"\t");

                Object value = model.getValueAt(i,j);

                if(value == null || value.toString().isEmpty()){ 
                    //JOptionPane.showMessageDialog(null, "NULL "+value);
                    //value = "";
                    excel.write("\t");
                }else{
                    excel.write(value+"\t");                   
                }
            }
            excel.write("\n");
        }
        excel.close();

    }catch(IOException e){ 
        System.out.println(e); 
    }catch(Exception e){
        //JOptionPane.showMessageDialog(null, "toExcel() error");
        //JOptionPane.showMessageDialog(null, e.getStackTrace());
    }
}  

    public void getIniData() {
        Ini ini;
        try {
            //ini = new Ini(new File("config.ini")); // TEST
            ini = new Ini(new File(personalConfig));  // toggle 1/1
            //ini = new Ini(new File("C:\\Users\\gerda.modarres\\Desktop\\pat_DB\\config.ini"));
            String dp = ini.get("defaultpath", "path");
            this.defaultPath = dp;
        } catch (IOException ex) {
            Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

//private void saveOnRC(java.awt.event.MouseEvent evt, JTable outTable){
    private void saveOnRC(java.awt.event.ActionEvent evt) {
        try {
            //JOptionPane.showMessageDialog(null, "right click");
            JTable OT = this.outTable;
            String dp = this.defaultPath;
            //JOptionPane.showMessageDialog(null, "dp:  "+dp); // TEST

            JFileChooser fileChooser = new JFileChooser(dp);
            if (fileChooser.showSaveDialog(jPanel1) == JFileChooser.APPROVE_OPTION) {
                File out_file = fileChooser.getSelectedFile();
                // save to file                   
                toExcel(OT, out_file);
            } else {
                //JOptionPane.showMessageDialog(null, "cancel");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "SSomething went wrong saving your stuff ... " + e.getMessage());
            my_log.logger.warning("ERROR:  Something went wrong saving your stuff ...  " + e);
        }
    }

    private void clearBtnColors() {
        lbl_array_signal.setBackground(Color.getColor("6699FF"));
        lbl_fish_signal.setBackground(Color.getColor("6699FF"));
        lbl_zg_signal.setBackground(Color.getColor("6699FF"));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        popUpSave = new javax.swing.JPopupMenu();
        popUpMenu_save = new javax.swing.JMenuItem();
        popUpMenu_selectAll = new javax.swing.JMenuItem();
        popUpMenu_moveTbl = new javax.swing.JMenuItem();
        popUpMenu_intrprWin = new javax.swing.JMenuItem();
        tab_main = new javax.swing.JTabbedPane();
        tab_array = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_array = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        A_lab_lab_id = new javax.swing.JLabel();
        A_lab_result_id = new javax.swing.JLabel();
        A_lab_array_sub_id = new javax.swing.JLabel();
        A_txt_lab_id = new javax.swing.JTextField();
        A_txt_result_id = new javax.swing.JTextField();
        A_txt_array_sub_id = new javax.swing.JTextField();
        jPanel10 = new javax.swing.JPanel();
        txt_fullLoc = new javax.swing.JTextField();
        btn_openLoc = new javax.swing.JButton();
        A_txt_sort = new javax.swing.JTextField();
        A_rbtn_sort = new javax.swing.JRadioButton();
        A_ComboBox_sort = new javax.swing.JComboBox<>();
        A_btn_clear = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        A_txt_type = new javax.swing.JTextField();
        A_txt_cnst = new javax.swing.JTextField();
        A_txt_call = new javax.swing.JTextField();
        A_txt_locStart = new javax.swing.JTextField();
        A_txt_size = new javax.swing.JTextField();
        A_txt_nom = new javax.swing.JTextField();
        A_txt_locEnd = new javax.swing.JTextField();
        A_txt_type_1 = new javax.swing.JTextField();
        A_txt_cnst_1 = new javax.swing.JTextField();
        A_txt_call_1 = new javax.swing.JTextField();
        A_txt_nom_1 = new javax.swing.JTextField();
        A_txt_size_1 = new javax.swing.JTextField();
        A_txt_locStart_1 = new javax.swing.JTextField();
        A_txt_locEnd_1 = new javax.swing.JTextField();
        A_txt_chr = new javax.swing.JTextField();
        A_txt_chr_1 = new javax.swing.JTextField();
        A_lbl_genes1 = new javax.swing.JLabel();
        A_lbl_genes2 = new javax.swing.JLabel();
        A_txt_resID = new javax.swing.JTextField();
        A_txt_resID_1 = new javax.swing.JTextField();
        A_txt_Creg = new javax.swing.JTextField();
        A_txt_Creg_1 = new javax.swing.JTextField();
        jPanel13 = new javax.swing.JPanel();
        A_txt_ANDOR = new javax.swing.JTextField();
        A_rbtn_NOT = new javax.swing.JRadioButton();
        jPanel7 = new javax.swing.JPanel();
        A_txt1_genes1 = new javax.swing.JTextField();
        A_lbl_genes1_1 = new javax.swing.JLabel();
        A_lbl_genes2_1 = new javax.swing.JLabel();
        A_txt3_genes1 = new javax.swing.JTextField();
        A_txt1_genes2 = new javax.swing.JTextField();
        A_txt2_genes1 = new javax.swing.JTextField();
        A_txt2_genes2 = new javax.swing.JTextField();
        A_txt3_genes2 = new javax.swing.JTextField();
        A_txt4_genes1 = new javax.swing.JTextField();
        A_txt4_genes2 = new javax.swing.JTextField();
        A_txt5_genes1 = new javax.swing.JTextField();
        A_txt5_genes2 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        rbtn_useFresult = new javax.swing.JRadioButton();
        A_btn_search = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        txt_genOnc = new javax.swing.JTextField();
        btn_openGenOnc = new javax.swing.JButton();
        tab_fish = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_fish = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        F_lab_lab_id = new javax.swing.JLabel();
        F_lab_result_id = new javax.swing.JLabel();
        F_lab_fish_sub_id = new javax.swing.JLabel();
        F_txt_lab_id = new javax.swing.JTextField();
        F_txt_result_id = new javax.swing.JTextField();
        F_txt_fish_sub_id = new javax.swing.JTextField();
        F_ComboBox_sort = new javax.swing.JComboBox<>();
        F_txt_sort = new javax.swing.JTextField();
        F_rbtn_sort = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        F_txt_probe_no = new javax.swing.JTextField();
        jScrollPane11 = new javax.swing.JScrollPane();
        F_txtArea_loc = new javax.swing.JTextArea();
        F_btn_clear = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        F_txt_percent = new javax.swing.JTextField();
        F_txt_reg1 = new javax.swing.JTextField();
        F_txt_sig1 = new javax.swing.JTextField();
        F_txt_fsn = new javax.swing.JTextField();
        F_txt_sig2 = new javax.swing.JTextField();
        F_txt_reg2 = new javax.swing.JTextField();
        F_txt_fchng = new javax.swing.JTextField();
        F_txt_result = new javax.swing.JTextField();
        F_txt_material = new javax.swing.JTextField();
        F_txt_percent_1 = new javax.swing.JTextField();
        F_txt_reg1_1 = new javax.swing.JTextField();
        F_txt_sig1_1 = new javax.swing.JTextField();
        F_txt_reg2_1 = new javax.swing.JTextField();
        F_txt_sig2_1 = new javax.swing.JTextField();
        F_txt_fsn_1 = new javax.swing.JTextField();
        F_txt_fchng_1 = new javax.swing.JTextField();
        F_txt_result_1 = new javax.swing.JTextField();
        F_txt_material_1 = new javax.swing.JTextField();
        F_txt_mitos = new javax.swing.JTextField();
        F_txt_mitos_1 = new javax.swing.JTextField();
        F_txt_kerne = new javax.swing.JTextField();
        F_txt_kerne_1 = new javax.swing.JTextField();
        F_txt_resID = new javax.swing.JTextField();
        F_txt_resID_1 = new javax.swing.JTextField();
        jPanel14 = new javax.swing.JPanel();
        F_txt_ANDOR = new javax.swing.JTextField();
        F_rbtn_NOT = new javax.swing.JRadioButton();
        jPanel11 = new javax.swing.JPanel();
        F_btn_search = new javax.swing.JButton();
        rbtn_useAresult = new javax.swing.JRadioButton();
        tab_ZG = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_zg_iscn = new javax.swing.JTable();
        jScrollPane4 = new javax.swing.JScrollPane();
        table_zg_result = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        Z_lab_lab_id = new javax.swing.JLabel();
        Z_lab_result_id = new javax.swing.JLabel();
        Z_lab_klon_id = new javax.swing.JLabel();
        Z_txt_lab_id = new javax.swing.JTextField();
        Z_txt_result_id = new javax.swing.JTextField();
        Z_txt_klon_id = new javax.swing.JTextField();
        ZG_rbtn_sort = new javax.swing.JRadioButton();
        ZG_ComboBox_sort = new javax.swing.JComboBox<>();
        ZG_txt_sort = new javax.swing.JTextField();
        Z_btn_clear = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        ZI_txt_mitos = new javax.swing.JTextField();
        ZI_txt_iscn = new javax.swing.JTextField();
        ZR_txt_chr = new javax.swing.JTextField();
        ZR_txt_region = new javax.swing.JTextField();
        ZR_txt_chng = new javax.swing.JTextField();
        ZI_txt_mitos_1 = new javax.swing.JTextField();
        ZI_txt_iscn_1 = new javax.swing.JTextField();
        ZR_txt_region_1 = new javax.swing.JTextField();
        ZR_txt_chr_1 = new javax.swing.JTextField();
        ZR_txt_chng_1 = new javax.swing.JTextField();
        ZI_txt_chr = new javax.swing.JTextField();
        ZI_txt_chr_1 = new javax.swing.JTextField();
        ZR_txt_klonID = new javax.swing.JTextField();
        ZR_txt_klonID_1 = new javax.swing.JTextField();
        ZI_txt_resId = new javax.swing.JTextField();
        ZI_txt_resId_1 = new javax.swing.JTextField();
        ZI_txt_cp = new javax.swing.JTextField();
        ZI_txt_cp_1 = new javax.swing.JTextField();
        jPanel15 = new javax.swing.JPanel();
        ZG_txt_ANDOR = new javax.swing.JTextField();
        ZG_rbtn_NOT = new javax.swing.JRadioButton();
        ZI_txt_mat = new javax.swing.JTextField();
        ZI_txt_mat_1 = new javax.swing.JTextField();
        ZI_txt_stim = new javax.swing.JTextField();
        ZI_txt_stim_1 = new javax.swing.JTextField();
        rbtn_ZGdetailResult = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        Z_btn_search = new javax.swing.JButton();
        rbtn_nyd = new javax.swing.JRadioButton();
        Info_top = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        table_statistics = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtArea_Creg = new javax.swing.JTextArea();
        lab_Genes = new javax.swing.JLabel();
        lab_Creg = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_queryIDs = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtArea_genes = new javax.swing.JTextArea();
        jPanel17 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        lbl_array_signal = new javax.swing.JLabel();
        lbl_fish_signal = new javax.swing.JLabel();
        lbl_zg_signal = new javax.swing.JLabel();
        btn_TTT = new javax.swing.JRadioButton();
        btn_selCol = new javax.swing.JRadioButton();
        btn_selPat = new javax.swing.JRadioButton();
        btn_loadQuery = new javax.swing.JButton();
        btn_saveQuery = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        ComboBox_projPat = new javax.swing.JComboBox<>();
        rbtn_onlyPat = new javax.swing.JRadioButton();
        rbtn_onlyPat1 = new javax.swing.JRadioButton();
        ComboBox_stdyPat = new javax.swing.JComboBox<>();
        rbtn_SB = new javax.swing.JRadioButton();
        rbtn_ST = new javax.swing.JRadioButton();
        rbtn_ArrQuery = new javax.swing.JRadioButton();
        rbtn_PB = new javax.swing.JRadioButton();
        jToolBar1 = new javax.swing.JToolBar();
        bnt_test = new javax.swing.JButton();
        btn_Emergency = new javax.swing.JButton();
        lbl_rowsReturned = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1_openModel = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1_HowTo = new javax.swing.JMenuItem();
        jMenuItem2_Info = new javax.swing.JMenuItem();

        popUpMenu_save.setText("save ...");
        popUpMenu_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popUpMenu_saveActionPerformed(evt);
            }
        });
        popUpSave.add(popUpMenu_save);

        popUpMenu_selectAll.setText("select all ...");
        popUpMenu_selectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popUpMenu_selectAllActionPerformed(evt);
            }
        });
        popUpSave.add(popUpMenu_selectAll);

        popUpMenu_moveTbl.setText("move table ...");
        popUpMenu_moveTbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popUpMenu_moveTblActionPerformed(evt);
            }
        });
        popUpSave.add(popUpMenu_moveTbl);

        popUpMenu_intrprWin.setText("show interpretation ...");
        popUpMenu_intrprWin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popUpMenu_intrprWinActionPerformed(evt);
            }
        });
        popUpSave.add(popUpMenu_intrprWin);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Linked Results Analysis Tool - result search");
        setLocation(new java.awt.Point(100, 0));

        tab_main.setBackground(new java.awt.Color(102, 153, 255));
        tab_main.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        table_array.setAutoCreateRowSorter(true);
        table_array.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9", "Title 10", "Title 11", "Title 12"
            }
        ));
        table_array.setToolTipText("array result");
        table_array.setCellSelectionEnabled(true);
        table_array.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_arrayMouseClicked(evt);
            }
        });
        table_array.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table_arrayKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(table_array);
        table_array.getAccessibleContext().setAccessibleName("table_array");

        jPanel5.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        A_lab_lab_id.setText("lab_id");

        A_lab_result_id.setText("result_id");

        A_lab_array_sub_id.setText("A.sub_id");

        A_txt_lab_id.setToolTipText("fill in lab_id and press enter to get a corresponding result");
        A_txt_lab_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                A_txt_lab_idActionPerformed(evt);
            }
        });

        txt_fullLoc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N

        btn_openLoc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/open_web2.png"))); // NOI18N
        btn_openLoc.setText("open Loc");
        btn_openLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_openLocActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn_openLoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txt_fullLoc)
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btn_openLoc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_fullLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        A_txt_sort.setText("asc");
        A_txt_sort.setToolTipText("asc, desc");

        A_rbtn_sort.setText("sort");

        A_ComboBox_sort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "array_sub_id", "ma_nom", "result_id", "chr", "arr_type", "cnst", "arr_call", "size", "loc_start", "loc_end", "cyto_regions", "genes" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(A_ComboBox_sort, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(A_txt_sort)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(A_lab_result_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(A_lab_lab_id, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(A_lab_array_sub_id, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(A_txt_array_sub_id, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(A_txt_lab_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(A_txt_result_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(A_rbtn_sort))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_txt_lab_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A_lab_lab_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_txt_array_sub_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A_lab_array_sub_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_txt_result_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A_lab_result_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(A_rbtn_sort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(A_ComboBox_sort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(A_txt_sort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        A_btn_clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Actions-edit-clear-list-icon.png"))); // NOI18N
        A_btn_clear.setText("clear all");
        A_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                A_btn_clearActionPerformed(evt);
            }
        });

        A_txt_type.setToolTipText("search text, % for any");

        A_txt_cnst.setToolTipText(">,<,= number");

        A_txt_call.setToolTipText("search text, % for any, null for empty");

        A_txt_locStart.setToolTipText(">,<,= number");

        A_txt_size.setToolTipText("<,>,=  number");

        A_txt_nom.setToolTipText("search text, % for any");

        A_txt_locEnd.setToolTipText(">,<,= number");

        A_txt_cnst_1.setToolTipText(">,<,= number");

        A_txt_call_1.setToolTipText("search text, % for any");

        A_txt_nom_1.setToolTipText("search text, % for any");

        A_txt_size_1.setToolTipText("<,>,=  number");

        A_txt_locStart_1.setToolTipText("search text, % for any");

        A_txt_locEnd_1.setToolTipText("search text, % for any");

        A_txt_chr.setToolTipText("1,2,3,...,\"X\",\"Y\"");

        A_txt_chr_1.setToolTipText("1,2,3,...,\"X\",\"Y\"");

        A_lbl_genes1.setText("select genes 1 ->");

        A_lbl_genes2.setText("select genes 2 ->");

        A_txt_resID.setToolTipText("1,2,3,...");

        A_txt_resID_1.setToolTipText("1,2,3,...");

        A_txt_Creg.setToolTipText("search text, % for any");

        A_txt_Creg_1.setToolTipText("search text, % for any");

        jPanel13.setBackground(new java.awt.Color(153, 153, 153));

        A_txt_ANDOR.setToolTipText("and, or  to proceed to second query");

        A_rbtn_NOT.setText("   NOT");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(A_txt_ANDOR, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(A_rbtn_NOT, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(A_txt_ANDOR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(A_rbtn_NOT)
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_nom, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                    .addComponent(A_txt_nom_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_resID, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                    .addComponent(A_txt_resID_1))
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_chr_1, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(A_txt_chr))
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_type, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(A_txt_type_1))
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_cnst_1, javax.swing.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE)
                    .addComponent(A_txt_cnst))
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_call, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                    .addComponent(A_txt_call_1))
                .addGap(3, 3, 3)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_size_1, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addComponent(A_txt_size))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_locStart, javax.swing.GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                    .addComponent(A_txt_locStart_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_locEnd_1, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                    .addComponent(A_txt_locEnd))
                .addGap(7, 7, 7)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_txt_Creg, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(A_txt_Creg_1))
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(A_lbl_genes1, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(A_lbl_genes2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(A_txt_type, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_cnst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_call, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_locStart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_locEnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_nom, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_size, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_chr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_lbl_genes1)
                            .addComponent(A_txt_resID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_Creg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(A_txt_type_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_cnst_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_call_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_locStart_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_locEnd_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_nom_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_size_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_chr_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_lbl_genes2)
                            .addComponent(A_txt_resID_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(A_txt_Creg_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        A_lbl_genes1_1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        A_lbl_genes1_1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        A_lbl_genes1_1.setText("Genes 1");

        A_lbl_genes2_1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        A_lbl_genes2_1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        A_lbl_genes2_1.setText("Genes 2");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(A_txt2_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(A_txt2_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(A_txt1_genes1)
                            .addComponent(A_lbl_genes1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(A_txt1_genes2)
                            .addComponent(A_lbl_genes2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(A_txt3_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(A_txt3_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(A_txt4_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(A_txt4_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(A_txt5_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(A_txt5_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_lbl_genes1_1)
                    .addComponent(A_lbl_genes2_1))
                .addGap(15, 15, 15)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_txt1_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A_txt1_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_txt2_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A_txt2_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_txt3_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A_txt3_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_txt4_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A_txt4_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(A_txt5_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(A_txt5_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(42, Short.MAX_VALUE))
        );

        rbtn_useFresult.setText("use FISH result");
        rbtn_useFresult.setToolTipText("select to get IDs from tab FISH");

        A_btn_search.setBackground(new java.awt.Color(0, 102, 102));
        A_btn_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Search.png"))); // NOI18N
        A_btn_search.setText("search");
        A_btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                A_btn_searchActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(A_btn_search, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(rbtn_useFresult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(A_btn_search)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rbtn_useFresult)
                .addGap(0, 0, 0))
        );

        txt_genOnc.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        txt_genOnc.setToolTipText("copy & paste gene name here");

        btn_openGenOnc.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/open_web2.png"))); // NOI18N
        btn_openGenOnc.setText("Genetics Oncology");
        btn_openGenOnc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_openGenOncActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btn_openGenOnc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(txt_genOnc, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(btn_openGenOnc)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txt_genOnc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout tab_arrayLayout = new javax.swing.GroupLayout(tab_array);
        tab_array.setLayout(tab_arrayLayout);
        tab_arrayLayout.setHorizontalGroup(
            tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_arrayLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(A_btn_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1299, Short.MAX_VALUE))
                .addGap(24, 24, 24)
                .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );
        tab_arrayLayout.setVerticalGroup(
            tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_arrayLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_arrayLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(tab_arrayLayout.createSequentialGroup()
                        .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(5, 5, 5)
                        .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(tab_arrayLayout.createSequentialGroup()
                                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(A_btn_clear))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        tab_main.addTab("Array", tab_array);
        tab_array.getAccessibleContext().setAccessibleName("Array");

        table_fish.setAutoCreateRowSorter(true);
        table_fish.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table_fish.setToolTipText("fish result");
        table_fish.setCellSelectionEnabled(true);
        table_fish.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_fishMouseClicked(evt);
            }
        });
        table_fish.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table_fishKeyReleased(evt);
            }
        });
        jScrollPane2.setViewportView(table_fish);
        table_fish.getAccessibleContext().setAccessibleName("table_fish");

        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        F_lab_lab_id.setText("lab_id");

        F_lab_result_id.setText("result_id");

        F_lab_fish_sub_id.setText("F.sub_id");

        F_txt_lab_id.setToolTipText("fill in lab_id and press enter to get a corresponding result");
        F_txt_lab_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                F_txt_lab_idActionPerformed(evt);
            }
        });

        F_txt_result_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                F_txt_result_idActionPerformed(evt);
            }
        });

        F_ComboBox_sort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "fish_sub_id", "result_id", "i_count", "m_count", "percent", "region1", "sig1", "region2", "sig2", "fsn_sig", "fish_chng", "result", "material", "p.probe_no" }));

        F_txt_sort.setText("asc");
        F_txt_sort.setToolTipText("asc, desc");

        F_rbtn_sort.setText("sort");

        jLabel1.setText("probe");

        jScrollPane11.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        jScrollPane11.setHorizontalScrollBar(null);

        F_txtArea_loc.setColumns(20);
        F_txtArea_loc.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        F_txtArea_loc.setLineWrap(true);
        F_txtArea_loc.setRows(3);
        jScrollPane11.setViewportView(F_txtArea_loc);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(F_ComboBox_sort, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(F_txt_sort)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(F_lab_result_id, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                            .addComponent(F_lab_fish_sub_id, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)
                            .addComponent(F_lab_lab_id, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(F_txt_fish_sub_id, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(F_txt_lab_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(F_txt_result_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(F_rbtn_sort)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(F_txt_probe_no, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(F_txt_lab_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F_lab_lab_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(F_txt_fish_sub_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F_lab_fish_sub_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(F_txt_result_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F_lab_result_id))
                .addGap(19, 19, 19)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(F_txt_probe_no, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(F_rbtn_sort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(F_ComboBox_sort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(F_txt_sort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        F_btn_clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Actions-edit-clear-list-icon.png"))); // NOI18N
        F_btn_clear.setText("clear all");
        F_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                F_btn_clearActionPerformed(evt);
            }
        });

        F_txt_percent.setToolTipText("<,>,=  number");

        F_txt_reg1.setToolTipText("search text, % for any");

        F_txt_sig1.setToolTipText("<,>,=  number");

        F_txt_fsn.setToolTipText("search text, % for any, null for empty");

        F_txt_sig2.setToolTipText("<,>,=  number");

        F_txt_reg2.setToolTipText("search text, % for any");

        F_txt_fchng.setToolTipText("search text, % for any, null for empty");

        F_txt_result.setToolTipText("search text, % for any");

        F_txt_material.setToolTipText("search text, % for any");

        F_txt_reg1_1.setToolTipText("search text, % for any");

        F_txt_sig1_1.setToolTipText("<,>,=  number");

        F_txt_reg2_1.setToolTipText("search text, % for any");

        F_txt_sig2_1.setToolTipText("<,>,=  number");

        F_txt_fsn_1.setToolTipText("search text, % for any");

        F_txt_fchng_1.setToolTipText("search text, % for any");

        F_txt_result_1.setToolTipText("search text, % for any");

        F_txt_material_1.setToolTipText("search text, % for any");

        F_txt_mitos.setToolTipText("<,>,=  number");

        F_txt_kerne.setToolTipText("<,>,=  number");

        F_txt_kerne_1.setToolTipText("<,>,=  number");

        F_txt_resID.setToolTipText("1,2,3,...");

        F_txt_resID_1.setToolTipText("1,2,3,...");

        jPanel14.setBackground(new java.awt.Color(153, 153, 153));

        F_txt_ANDOR.setToolTipText("and, or  to proceed to second query");

        F_rbtn_NOT.setText("   NOT");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(F_txt_ANDOR, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(F_rbtn_NOT, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(F_txt_ANDOR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(F_rbtn_NOT)
                .addGap(3, 3, 3))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(F_txt_resID_1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(F_txt_resID))
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(F_txt_kerne_1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                    .addComponent(F_txt_kerne))
                .addGap(3, 3, 3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(F_txt_mitos_1, javax.swing.GroupLayout.DEFAULT_SIZE, 50, Short.MAX_VALUE)
                    .addComponent(F_txt_mitos))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(F_txt_percent_1, javax.swing.GroupLayout.DEFAULT_SIZE, 52, Short.MAX_VALUE)
                    .addComponent(F_txt_percent))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(F_txt_reg1, javax.swing.GroupLayout.DEFAULT_SIZE, 98, Short.MAX_VALUE)
                    .addComponent(F_txt_reg1_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(F_txt_sig1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(F_txt_reg2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(F_txt_sig2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(F_txt_fsn, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(F_txt_fchng, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(F_txt_sig1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(F_txt_reg2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(F_txt_sig2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(F_txt_fsn_1, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(F_txt_fchng_1, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(F_txt_result_1, javax.swing.GroupLayout.DEFAULT_SIZE, 446, Short.MAX_VALUE)
                    .addComponent(F_txt_result))
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(F_txt_material_1, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(F_txt_material, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(165, 165, 165))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(F_txt_percent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_reg1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_sig1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_fsn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_fchng, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_reg2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_sig2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_result, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_material, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_mitos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_kerne, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_resID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(F_txt_percent_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_reg1_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_sig1_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_fsn_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_fchng_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_reg2_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_sig2_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_result_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_material_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_mitos_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_kerne_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(F_txt_resID_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0))
        );

        F_btn_search.setBackground(new java.awt.Color(0, 102, 102));
        F_btn_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Search.png"))); // NOI18N
        F_btn_search.setText("search");
        F_btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                F_btn_searchActionPerformed(evt);
            }
        });

        rbtn_useAresult.setText("use array result");
        rbtn_useAresult.setToolTipText("select to get IDs from tab Array");
        rbtn_useAresult.setPreferredSize(new java.awt.Dimension(115, 25));
        rbtn_useAresult.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(F_btn_search, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(rbtn_useAresult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(F_btn_search)
                .addGap(3, 3, 3)
                .addComponent(rbtn_useAresult, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout tab_fishLayout = new javax.swing.GroupLayout(tab_fish);
        tab_fish.setLayout(tab_fishLayout);
        tab_fishLayout.setHorizontalGroup(
            tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_fishLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(F_btn_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(tab_fishLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1323, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 181, Short.MAX_VALUE))))
        );
        tab_fishLayout.setVerticalGroup(
            tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tab_fishLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(tab_fishLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(F_btn_clear))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 454, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34))
        );

        tab_main.addTab("FISH", tab_fish);
        tab_fish.getAccessibleContext().setAccessibleName("FISH");

        table_zg_iscn.setAutoCreateRowSorter(true);
        table_zg_iscn.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table_zg_iscn.setToolTipText("cytogenetics result 1");
        table_zg_iscn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_zg_iscnMouseClicked(evt);
            }
        });
        table_zg_iscn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table_zg_iscnKeyReleased(evt);
            }
        });
        jScrollPane3.setViewportView(table_zg_iscn);
        table_zg_iscn.getAccessibleContext().setAccessibleName("table_zg_iscn");
        table_zg_iscn.getAccessibleContext().setAccessibleDescription("cytogenetics result");

        table_zg_result.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "result_id", "zg_sub_id", "klon_id", "region", "chr", "chng"
            }
        ));
        table_zg_result.setToolTipText("cytogenetics result 2");
        jScrollPane4.setViewportView(table_zg_result);
        table_zg_result.getAccessibleContext().setAccessibleName("table_zg_result");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        Z_lab_lab_id.setText("lab_id");

        Z_lab_result_id.setText("result_id");

        Z_lab_klon_id.setText("klon_id");

        Z_txt_lab_id.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Z_txt_lab_idActionPerformed(evt);
            }
        });

        ZG_rbtn_sort.setText("sort");

        ZG_ComboBox_sort.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "i.klon_id", "result_id", "chr_cnt", "mitos_cnt", "cp", "iscn", "material", "stim" }));

        ZG_txt_sort.setText("asc");
        ZG_txt_sort.setToolTipText("asc, desc");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Z_lab_lab_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Z_lab_klon_id, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Z_lab_result_id, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(Z_txt_klon_id, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(Z_txt_lab_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(Z_txt_result_id, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(ZG_ComboBox_sort, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(ZG_txt_sort)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(ZG_rbtn_sort)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Z_txt_lab_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Z_lab_lab_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Z_txt_klon_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Z_lab_klon_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Z_txt_result_id, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Z_lab_result_id))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ZG_rbtn_sort)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ZG_ComboBox_sort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ZG_txt_sort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Z_btn_clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Actions-edit-clear-list-icon.png"))); // NOI18N
        Z_btn_clear.setText("clear all");
        Z_btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Z_btn_clearActionPerformed(evt);
            }
        });

        ZI_txt_mitos.setToolTipText("<,>,=  number");

        ZI_txt_iscn.setToolTipText("search text, % for any");

        ZR_txt_chr.setToolTipText(">,<,= number");

        ZR_txt_region.setToolTipText("<,>,=  number");

        ZR_txt_chng.setToolTipText(">,<,= number");

        ZI_txt_mitos_1.setToolTipText("<,>,=  number");

        ZI_txt_iscn_1.setToolTipText("search text, % for any");

        ZR_txt_region_1.setToolTipText("<,>,=  number");

        ZR_txt_chr_1.setToolTipText("search text, % for any");

        ZR_txt_chng_1.setToolTipText("search text, % for any");

        ZI_txt_chr.setToolTipText("1,2,3,...,\"X\",\"Y\"");

        ZI_txt_chr_1.setToolTipText("1,2,3,...,\"X\",\"Y\"");

        ZR_txt_klonID.setEditable(false);
        ZR_txt_klonID.setBackground(new java.awt.Color(204, 204, 204));
        ZR_txt_klonID.setToolTipText("<,>,=  number");

        ZR_txt_klonID_1.setEditable(false);
        ZR_txt_klonID_1.setBackground(new java.awt.Color(204, 204, 204));
        ZR_txt_klonID_1.setToolTipText("<,>,=  number");

        ZI_txt_resId.setToolTipText("1,2,3,...");

        ZI_txt_resId_1.setToolTipText("1,2,3,...");

        ZI_txt_cp.setToolTipText("search text, % for any, null for empty");

        ZI_txt_cp_1.setToolTipText("search text, % for any, null for empty");

        jPanel15.setBackground(new java.awt.Color(153, 153, 153));

        ZG_txt_ANDOR.setToolTipText("and, or  to proceed to second query");

        ZG_rbtn_NOT.setText("   NOT");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(ZG_txt_ANDOR, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ZG_rbtn_NOT, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(ZG_txt_ANDOR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ZG_rbtn_NOT)
                .addGap(3, 3, 3))
        );

        ZI_txt_mat.setToolTipText("search text, % for any");

        ZI_txt_mat_1.setToolTipText("search text, % for any");

        ZI_txt_stim.setToolTipText("search text, % for any");

        ZI_txt_stim_1.setToolTipText("search text, % for any");

        rbtn_ZGdetailResult.setText("use detail result");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ZI_txt_resId, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(ZI_txt_resId_1))
                .addGap(3, 3, 3)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ZI_txt_chr, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(ZI_txt_chr_1))
                .addGap(3, 3, 3)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(ZI_txt_mitos)
                    .addComponent(ZI_txt_mitos_1, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE))
                .addGap(3, 3, 3)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ZI_txt_cp_1, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(ZI_txt_cp))
                .addGap(5, 5, 5)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ZI_txt_iscn)
                    .addComponent(ZI_txt_iscn_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ZI_txt_mat, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                    .addComponent(ZI_txt_mat_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ZI_txt_stim_1, javax.swing.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                    .addComponent(ZI_txt_stim))
                .addGap(38, 38, 38)
                .addComponent(rbtn_ZGdetailResult)
                .addGap(18, 18, 18)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ZR_txt_klonID, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(ZR_txt_klonID_1))
                .addGap(3, 3, 3)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ZR_txt_region, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ZR_txt_region_1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ZR_txt_chr, javax.swing.GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
                    .addComponent(ZR_txt_chr_1))
                .addGap(3, 3, 3)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(ZR_txt_chng, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ZR_txt_chng_1, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ZI_txt_mitos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_iscn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZR_txt_chr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZR_txt_chng, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZR_txt_region, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_chr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZR_txt_klonID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_resId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_cp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_mat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_stim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rbtn_ZGdetailResult))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ZI_txt_mitos_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_iscn_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZR_txt_chr_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZR_txt_chng_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZR_txt_region_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_chr_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZR_txt_klonID_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_resId_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_cp_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_mat_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ZI_txt_stim_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 0, 0))
        );

        Z_btn_search.setBackground(new java.awt.Color(0, 102, 102));
        Z_btn_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Search.png"))); // NOI18N
        Z_btn_search.setText("search");
        Z_btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Z_btn_searchActionPerformed(evt);
            }
        });

        rbtn_nyd.setText("not yet defined");
        rbtn_nyd.setPreferredSize(new java.awt.Dimension(115, 25));
        rbtn_nyd.setRequestFocusEnabled(false);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Z_btn_search, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(rbtn_nyd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(Z_btn_search)
                .addGap(3, 3, 3)
                .addComponent(rbtn_nyd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout tab_ZGLayout = new javax.swing.GroupLayout(tab_ZG);
        tab_ZG.setLayout(tab_ZGLayout);
        tab_ZGLayout.setHorizontalGroup(
            tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tab_ZGLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(Z_btn_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15)
                .addGroup(tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_ZGLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1083, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 378, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(25, 25, 25))
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        tab_ZGLayout.setVerticalGroup(
            tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tab_ZGLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tab_ZGLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(Z_btn_clear))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE)
                    .addComponent(jScrollPane4))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tab_main.addTab("Cytogenetics", tab_ZG);
        tab_ZG.getAccessibleContext().setAccessibleName("Cytogenetics");

        Info_top.setBackground(new java.awt.Color(102, 153, 255));
        Info_top.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Info_top.setRequestFocusEnabled(false);

        jScrollPane7.setBackground(new java.awt.Color(0, 204, 204));
        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        table_statistics.setBackground(new java.awt.Color(161, 211, 238));
        table_statistics.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        table_statistics.setToolTipText("statistics");
        table_statistics.setRowHeight(20);
        jScrollPane7.setViewportView(table_statistics);

        txtArea_Creg.setColumns(20);
        txtArea_Creg.setLineWrap(true);
        txtArea_Creg.setRows(4);
        txtArea_Creg.setWrapStyleWord(true);
        jScrollPane8.setViewportView(txtArea_Creg);

        lab_Genes.setText("Genes");

        lab_Creg.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lab_Creg.setText("cyto regions");

        table_queryIDs.setAutoCreateRowSorter(true);
        table_queryIDs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "lab_id", "result_ID", "name", "surname", "sex", "b_date"
            }
        ));
        table_queryIDs.setToolTipText("patients");
        table_queryIDs.setName(""); // NOI18N
        table_queryIDs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_queryIDsMouseClicked(evt);
            }
        });
        table_queryIDs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table_queryIDsKeyReleased(evt);
            }
        });
        jScrollPane10.setViewportView(table_queryIDs);
        table_queryIDs.getAccessibleContext().setAccessibleName("table_queryIDs");

        txtArea_genes.setColumns(20);
        txtArea_genes.setLineWrap(true);
        txtArea_genes.setRows(5);
        txtArea_genes.setWrapStyleWord(true);
        txtArea_genes.setName(""); // NOI18N
        jScrollPane5.setViewportView(txtArea_genes);

        jPanel17.setBackground(new java.awt.Color(102, 153, 255));
        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel16.setBackground(new java.awt.Color(102, 152, 255));
        jPanel16.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel16.setToolTipText("if result/patient is selected, orange indicates, that a result is present in corresponding result tab");

        lbl_array_signal.setBackground(new java.awt.Color(102, 153, 255));
        lbl_array_signal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_array_signal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/array_label_small.png"))); // NOI18N
        lbl_array_signal.setOpaque(true);

        lbl_fish_signal.setBackground(new java.awt.Color(102, 153, 255));
        lbl_fish_signal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_fish_signal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/fish_label_small.png"))); // NOI18N
        lbl_fish_signal.setOpaque(true);

        lbl_zg_signal.setBackground(new java.awt.Color(102, 153, 255));
        lbl_zg_signal.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_zg_signal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/zg_label_small.png"))); // NOI18N
        lbl_zg_signal.setOpaque(true);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lbl_array_signal, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lbl_fish_signal, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(lbl_zg_signal, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(lbl_array_signal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lbl_fish_signal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lbl_zg_signal, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1))
        );

        btn_TTT.setText("ToolTip Help on");
        btn_TTT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TTTActionPerformed(evt);
            }
        });

        btn_selCol.setText("column select");
        btn_selCol.setToolTipText("enable to select a whole column in table patients (press Shift or Cntrl to select more columns)");
        btn_selCol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_selColActionPerformed(evt);
            }
        });

        btn_selPat.setText("result/ patient");
        btn_selPat.setToolTipText("select to show results of selected patient only (shows in each tab - Array, FISH and Cytogenetics)");

        btn_loadQuery.setBackground(new java.awt.Color(51, 153, 255));
        btn_loadQuery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/open-file-icon.png"))); // NOI18N
        btn_loadQuery.setText("load query");
        btn_loadQuery.setToolTipText("load a saved query from a file");
        btn_loadQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_loadQueryActionPerformed(evt);
            }
        });

        btn_saveQuery.setBackground(new java.awt.Color(255, 153, 0));
        btn_saveQuery.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Floppy-Small-icon.png"))); // NOI18N
        btn_saveQuery.setText("save query");
        btn_saveQuery.setToolTipText("save current query to a file");
        btn_saveQuery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_saveQueryActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel17Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_saveQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_loadQuery, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel17Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btn_selCol, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btn_selPat, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(btn_TTT))))
                .addGap(10, 10, 10))
        );

        jPanel17Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_loadQuery, btn_saveQuery, btn_selCol, btn_selPat});

        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_loadQuery)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_saveQuery)
                .addGap(10, 10, 10)
                .addComponent(btn_selCol)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_selPat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                .addComponent(btn_TTT)
                .addContainerGap())
        );

        jPanel19.setBackground(new java.awt.Color(102, 153, 255));
        jPanel19.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        ComboBox_projPat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MS_ALL_Array_Diagnostics", "TEST" }));

        rbtn_onlyPat.setText("only patients from project ...");
        rbtn_onlyPat.setToolTipText("select to get results from patients in a certain study (select from below)");

        rbtn_onlyPat1.setText("only patients from study ...");
        rbtn_onlyPat1.setToolTipText("select to get results from patients in a certain project (select from below)");

        ComboBox_stdyPat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL BFM 2009", "TEST" }));

        rbtn_SB.setText("use SampleBrowse ");
        rbtn_SB.setToolTipText("select to get IDs from window SampleBrowse");

        rbtn_ST.setText("use SubtypeBrowse");
        rbtn_ST.setToolTipText("get IDs from window SubtypeBrowse");

        rbtn_ArrQuery.setText("use ArrayQuery");
        rbtn_ArrQuery.setToolTipText("select to get IDs from window ArrayQuery");

        rbtn_PB.setText("use PatientBrowse ");
        rbtn_PB.setToolTipText("select to get IDs from window SampleBrowse");

        javax.swing.GroupLayout jPanel19Layout = new javax.swing.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtn_PB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel19Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rbtn_onlyPat, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbtn_onlyPat1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ComboBox_projPat, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ComboBox_stdyPat, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(rbtn_SB, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtn_ST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtn_ArrQuery, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        jPanel19Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rbtn_onlyPat, rbtn_onlyPat1});

        jPanel19Layout.setVerticalGroup(
            jPanel19Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel19Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtn_onlyPat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_projPat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtn_onlyPat1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_stdyPat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(rbtn_PB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtn_SB)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtn_ST)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtn_ArrQuery)
                .addContainerGap())
        );

        javax.swing.GroupLayout Info_topLayout = new javax.swing.GroupLayout(Info_top);
        Info_top.setLayout(Info_topLayout);
        Info_topLayout.setHorizontalGroup(
            Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_topLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                    .addComponent(jScrollPane7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lab_Genes, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lab_Creg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                    .addComponent(jScrollPane8))
                .addContainerGap())
        );
        Info_topLayout.setVerticalGroup(
            Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_topLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Info_topLayout.createSequentialGroup()
                        .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab_Genes)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(Info_topLayout.createSequentialGroup()
                                .addComponent(lab_Creg)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane8)))
                    .addGroup(Info_topLayout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        jToolBar1.setRollover(true);

        bnt_test.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Actions-window-close-icon.png"))); // NOI18N
        bnt_test.setToolTipText("TEST");
        bnt_test.setFocusable(false);
        bnt_test.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bnt_test.setIconTextGap(2);
        bnt_test.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        bnt_test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bnt_testActionPerformed(evt);
            }
        });
        jToolBar1.add(bnt_test);

        btn_Emergency.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Reload-icon.png"))); // NOI18N
        btn_Emergency.setText("reload");
        btn_Emergency.setToolTipText("Emergency reload frame \nin case text fields degrade");
        btn_Emergency.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_EmergencyActionPerformed(evt);
            }
        });

        lbl_rowsReturned.setText(" ");

        jMenu4.setBorder(null);
        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_Font_small07_web.png"))); // NOI18N
        jMenu4.setMargin(new java.awt.Insets(0, 0, 0, 5));
        jMenuBar1.add(jMenu4);

        jMenu1.setText("File");

        jMenuItem1_openModel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/open-file-icon.png"))); // NOI18N
        jMenuItem1_openModel.setText("open DB Model");
        jMenuItem1_openModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1_openModelActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1_openModel);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        jMenuItem1_HowTo.setText("how to use");
        jMenuItem1_HowTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1_HowToActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1_HowTo);

        jMenuItem2_Info.setText("Info");
        jMenuItem2_Info.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2_InfoActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2_Info);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbl_rowsReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1300, 1300, 1300)
                        .addComponent(btn_Emergency))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(Info_top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(tab_main)))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab_main, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_Emergency)
                    .addComponent(lbl_rowsReturned))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        getAccessibleContext().setAccessibleName("SearchResult");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void table_zg_iscnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_zg_iscnMouseClicked
        // right klick ==> save to file
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_queryIDs);

            popUpSave.show(table_zg_iscn, evt.getX(), evt.getY());
            this.outTable = table_zg_iscn;
            
        } else {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            try {
                int row = table_zg_iscn.getSelectedRow();
                //String Table_click = (table_zg_iscn.getModel().getValueAt(row, 0).toString());    // values not correct anymore, if auto table rowsorter is used -->
                String Table_click = (table_zg_iscn.getValueAt(row, 0).toString());
                click_result = (table_zg_iscn.getValueAt(row, 1).toString());
                click_lID = Table_click;
                
                String sql = "SELECT * FROM zg_iscn i, main_result m Where i.result_id=m.result_id AND i.klon_id='" + Table_click + "' ";

                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String add1 = rs.getString("klon_id");
                    Z_txt_klon_id.setText(add1);
                    String add2 = rs.getString("result_id");
                    Z_txt_result_id.setText(add2);
                    String add3 = rs.getString("lab_id");
                    Z_txt_lab_id.setText(add3);
                    
                    // IF interpretation window is open
                    if(IntrprWindowIsOpen  == true){
                        updateIntrpr(add2);     // update text in Window "ResultWindow"
                    }

                    String sql2 = "SELECT r.result_id, r.zg_sub_id, r.klon_id, region, chr, chng FROM zg_result r, zg_list l, main_result m "
                            + "Where r.zyto_id=l.zyto_id AND r.result_id=m.result_id "
                            + "AND r.klon_id=?";

                    pst = conn.prepareStatement(sql2);
                    pst.setString(1, Z_txt_klon_id.getText());

                    rs = pst.executeQuery();
                    table_zg_result.setModel(DbUtils.resultSetToTableModel(rs));

                    jScrollPane4.setViewportView(table_zg_result);
                    if (table_zg_result.getColumnModel().getColumnCount() > 0) {
                        table_zg_result.getColumnModel().getColumn(0).setPreferredWidth(60);
                        table_zg_result.getColumnModel().getColumn(0).setMaxWidth(60);
                        table_zg_result.getColumnModel().getColumn(1).setPreferredWidth(70);
                        table_zg_result.getColumnModel().getColumn(1).setMaxWidth(70);
                        table_zg_result.getColumnModel().getColumn(2).setPreferredWidth(60);
                        table_zg_result.getColumnModel().getColumn(2).setMaxWidth(60);
                        table_zg_result.getColumnModel().getColumn(3).setPreferredWidth(70);
                        table_zg_result.getColumnModel().getColumn(3).setMaxWidth(70);
                        table_zg_result.getColumnModel().getColumn(4).setPreferredWidth(50);
                        table_zg_result.getColumnModel().getColumn(4).setMaxWidth(70);
                    }
                }
            } catch (Exception e) {
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
    }//GEN-LAST:event_table_zg_iscnMouseClicked

    private void table_fishMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_fishMouseClicked
        // right click  ==> save to file
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_fish);           
            popUpSave.show(table_fish,evt.getX(),evt.getY());
            this.outTable = table_fish;
            
        } else {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            try {
                int row = table_fish.getSelectedRow();
                //String Table_click = (table_fish.getModel().getValueAt(row, 0).toString());       // values not correct anymore, if auto table rowsorter is used -->
                String Table_click = (table_fish.getValueAt(row, 0).toString());
                click_result = (table_fish.getValueAt(row, 1).toString());
                click_lID = Table_click;
                
                String sql = "SELECT * FROM fish_result r, fish_probe p, main_result m WHERE r.probe_no=p.probe_no AND m.result_id=r.result_id AND fish_sub_id='" + Table_click + "' ";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String add1 = rs.getString("fish_sub_id");
                    F_txt_fish_sub_id.setText(add1);
                    String add2 = rs.getString("result_id");
                    F_txt_result_id.setText(add2);
                    String add3 = rs.getString("lab_id");
                    F_txt_lab_id.setText(add3);

                    String add4 = rs.getString("p.probe_no");
                    F_txt_probe_no.setText(add4);
                    String add5 = rs.getString("p.loc");
                    F_txtArea_loc.setText(add5);
                    
                    // IF interpretation window is open
                    if(IntrprWindowIsOpen  == true){
                        updateIntrpr(add2);     // update text in Window "ResultWindow"
                    }
                    
                    //txtArea_sql.setText(sql);
                }
            } catch (Exception e) {
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
    }//GEN-LAST:event_table_fishMouseClicked

    private void F_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_F_btn_clearActionPerformed
        //initial_table_array();
        initial_table_fish();
        //initial_table_zg_iscn();
        //initial_table_zg_result();
        initial_table_statistics();
        initial_table_queryIDs(); 

        clearBtnColors();
        
         // unselect rbtn
        F_rbtn_NOT.setSelected(false);
        F_rbtn_sort.setSelected(false);
        rbtn_useAresult.setSelected(false);
        
        F_txt_lab_id.setText("");
        F_txt_fish_sub_id.setText("");
        F_txt_result_id.setText("");
        //txtArea_sql.setText("");
        
        // Query fields
        F_txt_resID.setText("");
        F_txt_kerne.setText("");
        F_txt_mitos.setText("");
        F_txt_percent.setText("");
        F_txt_reg1.setText("");
        F_txt_sig1.setText("");
        F_txt_reg2.setText("");
        F_txt_sig2.setText("");
        F_txt_fsn.setText("");
        F_txt_fchng.setText("");
        F_txt_result.setText("");
        F_txt_material.setText("");
        
        F_txt_resID_1.setText("");
        F_txt_kerne_1.setText("");
        F_txt_mitos_1.setText("");
        F_txt_percent_1.setText("");
        F_txt_reg1_1.setText("");
        F_txt_sig1_1.setText("");
        F_txt_reg2_1.setText("");
        F_txt_sig2_1.setText("");
        F_txt_fsn_1.setText("");
        F_txt_fchng_1.setText("");
        F_txt_result_1.setText("");
        F_txt_material_1.setText("");
        
        // reset background colors of "NOT" usage
        F_txt_resID_1.setBackground(java.awt.Color.white);
        F_txt_reg1_1.setBackground(java.awt.Color.white);
        F_txt_reg2_1.setBackground(java.awt.Color.white);
        F_txt_fsn_1.setBackground(java.awt.Color.white);
        F_txt_fchng_1.setBackground(java.awt.Color.white);
        F_txt_result_1.setBackground(java.awt.Color.white);

    }//GEN-LAST:event_F_btn_clearActionPerformed

    private void Z_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Z_btn_clearActionPerformed
        //initial_table_array();
        //initial_table_fish();
        initial_table_zg_iscn();
        initial_table_zg_result();
        initial_table_statistics();
        initial_table_queryIDs(); 

        clearBtnColors();
        
        // unselect rbtn
        ZG_rbtn_NOT.setSelected(false);
        ZG_rbtn_sort.setSelected(false);
        rbtn_ZGdetailResult.setSelected(false);
        
        Z_txt_lab_id.setText("");
        Z_txt_klon_id.setText("");
        Z_txt_result_id.setText("");
        
        // Query fields
        ZI_txt_resId.setText("");
        ZI_txt_chr.setText("");
        ZI_txt_mitos.setText("");
        ZI_txt_cp.setText("");
        ZI_txt_iscn.setText("");
        ZI_txt_mat.setText("");
        ZI_txt_stim.setText("");
        ZR_txt_chng.setText("");
        ZR_txt_chr.setText("");
        ZR_txt_klonID.setText("");
        ZR_txt_region.setText("");
        ZI_txt_resId_1.setText("");
        ZI_txt_chr_1.setText("");
        ZI_txt_mitos_1.setText("");
        ZI_txt_cp_1.setText("");
        ZI_txt_iscn_1.setText("");
        ZI_txt_mat_1.setText("");
        ZI_txt_stim_1.setText("");
        ZR_txt_chng_1.setText("");
        ZR_txt_chr_1.setText("");
        ZR_txt_klonID_1.setText("");     
        ZR_txt_region_1.setText("");
        
        // reset background colors of "NOT" usage
        ZI_txt_resId_1.setBackground(java.awt.Color.white);
        ZI_txt_iscn_1.setBackground(java.awt.Color.white);
        ZI_txt_mat_1.setBackground(java.awt.Color.white);
        ZI_txt_stim_1.setBackground(java.awt.Color.white);        

    }//GEN-LAST:event_Z_btn_clearActionPerformed

    private void F_btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_F_btn_searchActionPerformed
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        // Query 1
        String resID = F_txt_resID.getText();
        String kerne = F_txt_kerne.getText();
        String mitos = F_txt_mitos.getText();
        String percent = F_txt_percent.getText();
        String reg1 = F_txt_reg1.getText();
        String sig1 = F_txt_sig1.getText();
        String reg2 = F_txt_reg2.getText();
        String sig2 = F_txt_sig2.getText();
        String fsn = F_txt_fsn.getText();
        String fchng = F_txt_fchng.getText();
        String result = F_txt_result.getText();
        String material = F_txt_material.getText();
        
        // Query 2
        String resID_1 = F_txt_resID_1.getText();
        String kerne_1 = F_txt_kerne_1.getText();
        String mitos_1 = F_txt_mitos_1.getText();
        String percent_1 = F_txt_percent_1.getText();
        String reg1_1 = F_txt_reg1_1.getText();
        String sig1_1 = F_txt_sig1_1.getText();
        String reg2_1 = F_txt_reg2_1.getText();
        String sig2_1 = F_txt_sig2_1.getText();
        String fsn_1 = F_txt_fsn_1.getText();
        String fchng_1 = F_txt_fchng_1.getText();
        String result_1 = F_txt_result_1.getText();
        String material_1 = F_txt_material_1.getText();
        
        try {
            String sql = "SELECT fish_sub_id as ID, result_id, i_count, m_count, percent, region1, reg1_sig as sig1, region2, reg2_sig as sig2, fsn_sig, fish_chng, result, material FROM fish_result r"
                    + ", fish_probe p WHERE r.probe_no=p.probe_no AND ( 1=1";

            if (F_txt_resID !=null && !F_txt_resID.getText().isEmpty()) { sql = sql + " AND result_id IN (" + resID + ")"; }
            if (F_txt_kerne !=null && !F_txt_kerne.getText().isEmpty()){ sql = sql + " AND i_count " + kerne; }
            if (F_txt_mitos !=null && !F_txt_mitos.getText().isEmpty()){ sql = sql + " AND m_count " + mitos; }
            if (F_txt_percent != null && !F_txt_percent.getText().isEmpty()) { sql = sql + " AND percent " + percent; }
            if (F_txt_reg1 != null && !F_txt_reg1.getText().isEmpty()) { sql = sql + " AND p.region1 LIKE '%" + reg1 + "%'"; }
            if (F_txt_sig1 != null && !F_txt_sig1.getText().isEmpty()) { sql = sql + " AND reg1_sig " + sig1; }
            if (F_txt_reg2 != null && !F_txt_reg2.getText().isEmpty()) { sql = sql + " AND p.region2 LIKE '%" + reg2 + "%'"; }
            if (F_txt_sig2 != null && !F_txt_sig2.getText().isEmpty()) { sql = sql + " AND reg2_sig " + sig2; }
            //if (F_txt_fsn != null && !F_txt_fsn.getText().isEmpty()) { sql = sql + " AND fsn_sig LIKE '%" + fsn + "%'"; }
            if (F_txt_fsn != null && !F_txt_fsn.getText().isEmpty()) { 
                String tmp = F_txt_fsn.getText();
                if (tmp.equals("null") || tmp.equals("NULL")){
                    sql = sql + " AND fsn_sig IS NULL";
                }
                else{
                    sql = sql + " AND fsn_sig LIKE '%" + fsn + "%'";
                }
            }               
            //if (F_txt_fchng != null && !F_txt_fchng.getText().isEmpty()) { sql = sql + " AND fish_chng LIKE '%" + fchng + "%'"; }
            if (F_txt_fchng != null && !F_txt_fchng.getText().isEmpty()) { 
                String tmp = F_txt_fchng.getText();
                if (tmp.equals("null") || tmp.equals("NULL")){
                    sql = sql + " AND fish_chng IS NULL";
                }
                else{
                    sql = sql + " AND fish_chng LIKE '%" + fchng + "%'";
                }
            }          
            if (F_txt_result !=null && !F_txt_result.getText().isEmpty()){ sql = sql + " AND result LIKE '%" + result + "%'"; }
            if (F_txt_material !=null && !F_txt_material.getText().isEmpty()){ sql = sql + " AND material LIKE '%" + material + "%'"; }
            
            // look in textfield for AND/OR for second query
            if (F_txt_ANDOR != null && !F_txt_ANDOR.getText().isEmpty()) {

                String andor = F_txt_ANDOR.getText();
                
                //if (andor.equals("or")) { // OR
                if (andor.equalsIgnoreCase("or")) { // OR
                    sql = sql + " OR ( 1=1 ";
                    //sql = sql + " OR ( fish_sub_id LIKE '%%%'";

                    if (F_rbtn_NOT.isSelected()) {

                        // color fields where NOT can be used on
                        F_txt_resID_1.setBackground(java.awt.Color.yellow);
                        F_txt_reg1_1.setBackground(java.awt.Color.yellow);
                        F_txt_reg2_1.setBackground(java.awt.Color.yellow);
                        F_txt_fsn_1.setBackground(java.awt.Color.yellow);
                        F_txt_fchng_1.setBackground(java.awt.Color.yellow);
                        F_txt_result_1.setBackground(java.awt.Color.yellow);
                        
                        if (F_txt_resID_1 !=null && !F_txt_resID_1.getText().isEmpty()) { sql = sql + " AND result_id NOT IN (" + resID_1 + ")"; }
                        if (F_txt_kerne_1 !=null && !F_txt_kerne_1.getText().isEmpty()){ sql = sql + " AND i_count " + kerne_1; }
                        if (F_txt_mitos_1 !=null && !F_txt_mitos_1.getText().isEmpty()){ sql = sql + " AND m_count " + mitos_1; }
                        if (F_txt_percent_1 != null && !F_txt_percent_1.getText().isEmpty()) { sql = sql + " AND percent " + percent_1; }
                        if (F_txt_reg1_1 != null && !F_txt_reg1_1.getText().isEmpty()) { sql = sql + " AND p.region1 NOT LIKE '%" + reg1_1 + "%'"; }
                        if (F_txt_sig1_1 != null && !F_txt_sig1_1.getText().isEmpty()) { sql = sql + " AND reg1_sig " + sig1_1; }
                        if (F_txt_reg2_1 != null && !F_txt_reg2_1.getText().isEmpty()) { sql = sql + " AND p.region2 NOT LIKE '%" + reg2_1 + "%'"; }
                        if (F_txt_sig2_1 != null && !F_txt_sig2_1.getText().isEmpty()) { sql = sql + " AND reg2_sig " + sig2_1; }
                        //if (F_txt_fsn_1 != null && !F_txt_fsn_1.getText().isEmpty()) { sql = sql + " AND fsn_sig NOT LIKE '%" + fsn_1 + "%'"; }
                        if (F_txt_fsn_1 != null && !F_txt_fsn_1.getText().isEmpty()) {
                            String tmp = F_txt_fsn_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND fsn_sig IS NOT NULL";
                            } else {
                                sql = sql + " AND fsn_sig NOT LIKE '%" + fsn_1 + "%'";
                            }
                        }
                        
                        if (F_txt_fchng_1 != null && !F_txt_fchng_1.getText().isEmpty()) { sql = sql + " AND fish_chng NOT LIKE '%" + fchng_1 + "%'"; }
                        if (F_txt_result_1 !=null && !F_txt_result_1.getText().isEmpty()){ sql = sql + " AND result NOT LIKE '%" + result_1 + "%'"; }
                        if (F_txt_material_1 !=null && !F_txt_material_1.getText().isEmpty()){ sql = sql + " AND material NOT LIKE '%" + material_1 + "%'"; }

                        sql = sql + ")";

                    } else {
                        F_txt_resID_1.setBackground(java.awt.Color.white);
                        F_txt_reg1_1.setBackground(java.awt.Color.white);
                        F_txt_reg2_1.setBackground(java.awt.Color.white);
                        F_txt_fsn_1.setBackground(java.awt.Color.white);
                        F_txt_fchng_1.setBackground(java.awt.Color.white);
                        F_txt_result_1.setBackground(java.awt.Color.white);
                        
                        if (F_txt_resID_1 !=null && !F_txt_resID_1.getText().isEmpty()) { sql = sql + " AND result_id IN (" + resID_1 + ")"; }
                        if (F_txt_kerne_1 !=null && !F_txt_kerne_1.getText().isEmpty()){ sql = sql + " AND i_count " + kerne_1; }
                        if (F_txt_mitos_1 !=null && !F_txt_mitos_1.getText().isEmpty()){ sql = sql + " AND m_count " + mitos_1; }
                        if (F_txt_percent_1 != null && !F_txt_percent_1.getText().isEmpty()) { sql = sql + " AND percent " + percent_1; }
                        if (F_txt_reg1_1 != null && !F_txt_reg1_1.getText().isEmpty()) { sql = sql + " AND p.region1 LIKE '%" + reg1_1 + "%'"; }
                        if (F_txt_sig1_1 != null && !F_txt_sig1_1.getText().isEmpty()) { sql = sql + " AND reg1_sig " + sig1_1; }
                        if (F_txt_reg2_1 != null && !F_txt_reg2_1.getText().isEmpty()) { sql = sql + " AND p.region2 LIKE '%" + reg2_1 + "%'"; }
                        if (F_txt_sig2_1 != null && !F_txt_sig2_1.getText().isEmpty()) { sql = sql + " AND reg2_sig " + sig2_1; }
                        //if (F_txt_fsn_1 != null && !F_txt_fsn_1.getText().isEmpty()) { sql = sql + " AND fsn_sig LIKE '%" + fsn_1 + "%'"; }
                        if (F_txt_fsn_1 != null && !F_txt_fsn_1.getText().isEmpty()) {
                            String tmp = F_txt_fsn_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND fsn_sig IS NULL";
                            } else {
                                sql = sql + " AND fsn_sig LIKE '%" + fsn_1 + "%'";
                            }
                        }
                        if (F_txt_fchng_1 != null && !F_txt_fchng_1.getText().isEmpty()) { sql = sql + " AND fish_chng LIKE '%" + fchng_1 + "%'"; }
                        if (F_txt_result_1 !=null && !F_txt_result_1.getText().isEmpty()){ sql = sql + " AND result LIKE '%" + result_1 + "%'"; }
                        if (F_txt_material_1 !=null && !F_txt_material_1.getText().isEmpty()){ sql = sql + " AND material LIKE '%" + material_1 + "%'"; }

                        sql = sql + ")";
                    }

                } else if (andor.equalsIgnoreCase("and")) { // AND 
                    sql = sql + " AND ( 1=1 ";
                   
                    if(F_rbtn_NOT.isSelected()){
                        F_txt_resID_1.setBackground(java.awt.Color.yellow);
                        F_txt_reg1_1.setBackground(java.awt.Color.yellow);
                        F_txt_reg2_1.setBackground(java.awt.Color.yellow);
                        F_txt_fsn_1.setBackground(java.awt.Color.yellow);
                        F_txt_fchng_1.setBackground(java.awt.Color.yellow);
                        F_txt_result_1.setBackground(java.awt.Color.yellow);
                        
                        if (F_txt_resID_1 !=null && !F_txt_resID_1.getText().isEmpty()) { sql = sql + " AND result_id NOT IN (" + resID_1 + ")"; }
                        if (F_txt_kerne_1 !=null && !F_txt_kerne_1.getText().isEmpty()){ sql = sql + " AND i_count " + kerne_1; }
                        if (F_txt_mitos_1 !=null && !F_txt_mitos_1.getText().isEmpty()){ sql = sql + " AND m_count " + mitos_1; }
                        if (F_txt_percent_1 != null && !F_txt_percent_1.getText().isEmpty()) { sql = sql + " AND percent " + percent_1; }
                        if (F_txt_reg1_1 != null && !F_txt_reg1_1.getText().isEmpty()) { sql = sql + " AND p.region1 NOT LIKE '%" + reg1_1 + "%'"; }
                        if (F_txt_sig1_1 != null && !F_txt_sig1_1.getText().isEmpty()) { sql = sql + " AND reg1_sig " + sig1_1; }
                        if (F_txt_reg2_1 != null && !F_txt_reg2_1.getText().isEmpty()) { sql = sql + " AND p.region2 NOT LIKE '%" + reg2_1 + "%'"; }
                        if (F_txt_sig2_1 != null && !F_txt_sig2_1.getText().isEmpty()) { sql = sql + " AND reg2_sig " + sig2_1; }
                        //if (F_txt_fsn_1 != null && !F_txt_fsn_1.getText().isEmpty()) { sql = sql + " AND fsn_sig NOT LIKE '%" + fsn_1 + "%'"; }
                        if (F_txt_fsn_1 != null && !F_txt_fsn_1.getText().isEmpty()) {
                            String tmp = F_txt_fsn_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND fsn_sig IS NOT NULL";
                            } else {
                                sql = sql + " AND fsn_sig NOT LIKE '%" + fsn_1 + "%'";
                            }
                        }
                        if (F_txt_fchng_1 != null && !F_txt_fchng_1.getText().isEmpty()) { sql = sql + " AND fish_chng NOT LIKE '%" + fchng_1 + "%'"; }
                        if (F_txt_result_1 !=null && !F_txt_result_1.getText().isEmpty()){ sql = sql + " AND result NOT LIKE '%" + result_1 + "%'"; }
                        if (F_txt_material_1 !=null && !F_txt_material_1.getText().isEmpty()){ sql = sql + " AND material NOT LIKE '%" + material_1 + "%'"; }
                           
                        sql = sql + ")";
                            
                    } else {
                        F_txt_resID_1.setBackground(java.awt.Color.white);
                        F_txt_reg1_1.setBackground(java.awt.Color.white);
                        F_txt_reg2_1.setBackground(java.awt.Color.white);
                        F_txt_fsn_1.setBackground(java.awt.Color.white);
                        F_txt_fchng_1.setBackground(java.awt.Color.white);
                        F_txt_result_1.setBackground(java.awt.Color.white);
                    
                        if (F_txt_resID_1 !=null && !F_txt_resID_1.getText().isEmpty()) { sql = sql + " AND result_id IN (" + resID_1 + ")"; }
                        if (F_txt_kerne_1 !=null && !F_txt_kerne_1.getText().isEmpty()){ sql = sql + " AND i_count " + kerne_1; }
                        if (F_txt_mitos_1 !=null && !F_txt_mitos_1.getText().isEmpty()){ sql = sql + " AND m_count " + mitos_1; }
                        if (F_txt_percent_1 != null && !F_txt_percent_1.getText().isEmpty()) { sql = sql + " AND percent " + percent_1; }
                        if (F_txt_reg1_1 != null && !F_txt_reg1_1.getText().isEmpty()) { sql = sql + " AND p.region1 LIKE '%" + reg1_1 + "%'"; }
                        if (F_txt_sig1_1 != null && !F_txt_sig1_1.getText().isEmpty()) { sql = sql + " AND reg1_sig " + sig1_1; }
                        if (F_txt_reg2_1 != null && !F_txt_reg2_1.getText().isEmpty()) { sql = sql + " AND p.region2 LIKE '%" + reg2_1 + "%'"; }
                        if (F_txt_sig2_1 != null && !F_txt_sig2_1.getText().isEmpty()) { sql = sql + " AND reg2_sig " + sig2_1; }
                        //if (F_txt_fsn_1 != null && !F_txt_fsn_1.getText().isEmpty()) { sql = sql + " AND fsn_sig LIKE '%" + fsn_1 + "%'"; }
                        if (F_txt_fsn_1 != null && !F_txt_fsn_1.getText().isEmpty()) {
                            String tmp = F_txt_fsn_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND fsn_sig IS NULL";
                            } else {
                                sql = sql + " AND fsn_sig LIKE '%" + fsn_1 + "%'";
                            }
                        }
                        if (F_txt_fchng_1 != null && !F_txt_fchng_1.getText().isEmpty()) { sql = sql + " AND fish_chng LIKE '%" + fchng_1 + "%'"; }
                        if (F_txt_result_1 !=null && !F_txt_result_1.getText().isEmpty()){ sql = sql + " AND result LIKE '%" + result_1 + "%'"; }
                        if (F_txt_material_1 !=null && !F_txt_material_1.getText().isEmpty()){ sql = sql + " AND material LIKE '%" + material_1 + "%'"; }
                            
                        sql = sql + ")";
                    }
               }
            }// END if (F_txt_ANDOR != null ...
            
            sql = sql +")";
            
            //String result_id = F_txt_result_id.getText();
            //if (F_txt_result_id != null && !F_txt_result_id.getText().isEmpty()) { sql = sql + " AND result_id in (" + result_id + ")"; }
            
            String method_name = Thread.currentThread().getStackTrace()[1].getMethodName();

            // only for the set of array-result 
            if (rbtn_useAresult.isSelected()) {
                //deliver_A_ids(this.ids, sql);
                deliver_ids(method_name,this.ids, sql);
                sql=this.mod_sql;
            }
            
            // only for a selected patient group
            if (rbtn_onlyPat.isSelected()){
                deliver_Proj_ids(sql, "F");
                sql = this.mod_sql;
            }
            if (rbtn_onlyPat1.isSelected()){
                deliver_Stdy_ids(sql, "F");
                sql = this.mod_sql;
            }    
            
            // only for the set of patient browse result
            if (rbtn_PB.isSelected()) {     
                deliver_PB_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            // only for the set of sample browse result
            if (rbtn_SB.isSelected()) {
                //String method_name = Thread.currentThread().getStackTrace()[1].getMethodName();
                //JOptionPane.showMessageDialog(null, method_name);
                deliver_SB_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            // only for the set of subtypes result
            if (rbtn_ST.isSelected()) {     
                deliver_ST_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            // only for the set of ArrayQuery result
            if (rbtn_ArrQuery.isSelected()) {     
                deliver_AQ_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            if (F_rbtn_sort.isSelected()) {
                String order = (String) F_ComboBox_sort.getSelectedItem();
                String asdes = F_txt_sort.getText();
                if(F_ComboBox_sort.getSelectedItem().toString().equals("fish_sub_id")
                        || F_ComboBox_sort.getSelectedItem().toString().equals("i_count")  
                        || F_ComboBox_sort.getSelectedItem().toString().equals("m_count") 
                        || F_ComboBox_sort.getSelectedItem().toString().equals("percent")      // percent necessary?
                        ){   
                    sql = sql + " order by (" + order + " * 1) " + asdes;
                }else {
                    sql = sql + " order by (" + order + ") " + asdes;
                }
            }
            
            get_ids(sql, pst, rs, conn);
            //txtArea_sql.setText(sql);
            my_log.logger.info("SQL:  " + sql);
                      
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_fish.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_fish);

            // resize column width
            jScrollPane2.setViewportView(table_fish);
            if (table_fish.getColumnModel().getColumnCount() > 0) {
                table_fish.getColumnModel().getColumn(0).setPreferredWidth(65); //55
                table_fish.getColumnModel().getColumn(0).setMaxWidth(65);       //55

                table_fish.getColumnModel().getColumn(1).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(1).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(2).setPreferredWidth(55);
                table_fish.getColumnModel().getColumn(2).setMaxWidth(55);

                table_fish.getColumnModel().getColumn(3).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(3).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(4).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(4).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(5).setPreferredWidth(100);
                table_fish.getColumnModel().getColumn(5).setMaxWidth(300);
                table_fish.getColumnModel().getColumn(6).setPreferredWidth(45);
                table_fish.getColumnModel().getColumn(6).setMaxWidth(45);
                table_fish.getColumnModel().getColumn(7).setPreferredWidth(100);
                table_fish.getColumnModel().getColumn(7).setMaxWidth(300);
                table_fish.getColumnModel().getColumn(8).setPreferredWidth(45);
                table_fish.getColumnModel().getColumn(8).setMaxWidth(45);

                table_fish.getColumnModel().getColumn(9).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(9).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(10).setPreferredWidth(100);  // fish_chng
                table_fish.getColumnModel().getColumn(10).setMaxWidth(500);

                table_fish.getColumnModel().getColumn(12).setPreferredWidth(110); // material
                table_fish.getColumnModel().getColumn(12).setMaxWidth(200);
            }

            // if resultset is empty
            if(!rs.first()){
                //JOptionPane.showMessageDialog(null, "No result for that query");
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                //JOptionPane.showMessageDialog(null, getRows+" row(s) returned");
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
                DefaultTableModel model = (DefaultTableModel) table_queryIDs.getModel();
                model.setRowCount(0);
            }
            
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                //JOptionPane.showMessageDialog(null, getRows+" row(s) returned");
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
            }

            get_queryLabIDs(sql, pst, rs, conn);
            get_statistics(sql);
            
            display_ids();
            
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "Something is wrong ...");
            my_log.logger.warning("ERROR: " + e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }

    }//GEN-LAST:event_F_btn_searchActionPerformed

    private void F_txt_lab_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_F_txt_lab_idActionPerformed
        
        update_table_fish();
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {

            String lab_id = F_txt_lab_id.getText();
            //F_txt_result_id.setText(result_id);
            F_txt_fish_sub_id.setText("");
            String sql = "SELECT f.result_id FROM fish_result f, main_result m WHERE f.result_id = m.result_id AND lab_id='" + lab_id + "'";

            //txtArea_sql.setText(sql);
            get_ids(sql, pst, rs, conn); // needed for get_statistics() to count patients affected
            get_statistics(sql);
            get_queryLabIDs(sql, pst, rs, conn);


        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        } 
    }//GEN-LAST:event_F_txt_lab_idActionPerformed

    private void Z_btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Z_btn_searchActionPerformed

        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        // Query 1
        String resID = ZI_txt_resId.getText();
        String chr = ZI_txt_chr.getText();
        String mitos= ZI_txt_mitos.getText();
        String cp = ZI_txt_cp.getText();
        String iscn = ZI_txt_iscn.getText();
        String mat = ZI_txt_mat.getText();
        String stim = ZI_txt_stim.getText();
        
        String chng = ZR_txt_chng.getText();
        String ZR_chr = ZR_txt_chr.getText();
        String klonID = ZR_txt_klonID.getText();
        String region = ZR_txt_region.getText();
        
         // Query 2
        String resID_1 = ZI_txt_resId_1.getText();
        String chr_1 = ZI_txt_chr_1.getText();
        String mitos_1 = ZI_txt_mitos_1.getText();
        String cp_1 = ZI_txt_cp_1.getText();
        String iscn_1 = ZI_txt_iscn_1.getText();
        String mat_1 = ZI_txt_mat_1.getText();
        String stim_1 = ZI_txt_stim_1.getText();
        
        String chng_1 = ZR_txt_chng_1.getText();
        String ZR_chr_1= ZR_txt_chr_1.getText();
        String klonID_1= ZR_txt_klonID_1.getText();     
        String region_1 = ZR_txt_region_1.getText();
        
        try {
            String sql = null;
            if (rbtn_ZGdetailResult.isSelected()){

                // Error: https://confluence.atlassian.com/jirakb/com-mysql-jdbc-exceptions-jdbc4-mysqlsyntaxerrorexception-references-command-denied-to-user-872266070.html
                // TEST!! might not be correct
                sql = "SELECT distinct i.klon_id as Klon,i.result_id, chr_cnt as Chr, mitos_cnt as Mitosen, cp, iscn as ISCN, material, stim "
                        + "FROM zg_iscn i, main_result m, zg_result r, zg_list l "
                        + "Where i.result_id=m.result_id "
                        + "AND r.klon_id=i.klon_id "
                        + "AND r.zyto_id=l.zyto_id AND (1=1";
            } else {
                sql = "SELECT i.klon_id as Klon,i.result_id, chr_cnt as Chr, mitos_cnt as Mitosen, cp, iscn as ISCN, material, stim FROM zg_iscn i, main_result m Where i.result_id=m.result_id AND (1=1";
            }
            
            if (ZI_txt_resId !=null && !ZI_txt_resId.getText().isEmpty()) { sql = sql + " AND i.result_id IN (" + resID + ")"; }
            if (ZI_txt_chr !=null && !ZI_txt_chr.getText().isEmpty()) { sql = sql + " AND chr_cnt " + chr ; }
            if (ZI_txt_mitos != null && !ZI_txt_mitos.getText().isEmpty()) { sql = sql + " AND mitos_cnt " + mitos ; }
            if (ZI_txt_cp != null && !ZI_txt_cp.getText().isEmpty()) { 
                String tmp = ZI_txt_cp.getText();
                if (tmp.equals("null") || tmp.equals("NULL")) {
                    sql = sql + " AND cp IS NULL";
                } else {
                    sql = sql + " AND cp LIKE '%" + cp + "%'"; 
                }
            }
            if (ZI_txt_iscn != null && !ZI_txt_iscn.getText().isEmpty()) { sql = sql + " AND iscn LIKE '%" + iscn + "%'"; }
            if (ZI_txt_mat != null && !ZI_txt_mat.getText().isEmpty()) { 
                String tmp = ZI_txt_mat.getText();
                if (tmp.equals("null") || tmp.equals("NULL")) {
                    sql = sql + " AND material IS NULL";
                } else {
                    sql = sql + " AND material LIKE '%" + mat + "%'"; 
                }
            }    
            if (ZI_txt_stim != null && !ZI_txt_stim.getText().isEmpty()) { 
                String tmp = ZI_txt_stim.getText();
                if (tmp.equals("null") || tmp.equals("NULL")) {
                    sql = sql + " AND stim IS NULL";
                } else {
                    sql = sql + " AND stim LIKE '%" + stim + "%'"; 
                }
            }    
////////////
            if (rbtn_ZGdetailResult.isSelected()){
                if (ZR_txt_klonID !=null && !ZR_txt_klonID.getText().isEmpty()) { sql = sql + " AND r.klon_id IN (" + klonID + ")"; }
                if (ZR_txt_region != null && !ZR_txt_region.getText().isEmpty()) { sql = sql + " AND region LIKE '%" + region + "%'"; }
                if (ZR_txt_chr !=null && !ZR_txt_chr.getText().isEmpty()) { sql = sql + " AND chr IN (" + ZR_chr + ")"; }
                if (ZR_txt_chng != null && !ZR_txt_chng.getText().isEmpty()) { sql = sql + " AND chng LIKE '%" + chng + "%'"; }            
            }
///////////                     
            if (ZG_txt_ANDOR != null && !ZG_txt_ANDOR.getText().isEmpty()) {

                String andor = ZG_txt_ANDOR.getText();

                if (andor.equalsIgnoreCase("or")) { // OR
                    sql = sql + " OR ( 1=1 ";

                    if (ZG_rbtn_NOT.isSelected()) {  // OR NOT

                        // color fields where NOT can be used on
                        ZI_txt_resId_1.setBackground(java.awt.Color.yellow);
                        ZI_txt_iscn_1.setBackground(java.awt.Color.yellow);
                        ZI_txt_mat_1.setBackground(java.awt.Color.yellow);
                        ZI_txt_stim_1.setBackground(java.awt.Color.yellow);
                        
                        if (ZI_txt_resId_1 != null && !ZI_txt_resId_1.getText().isEmpty()) {    sql = sql + " AND result_id NOT IN (" + resID_1 + ")"; }
                        if (ZI_txt_chr_1 != null && !ZI_txt_chr_1.getText().isEmpty()) { sql = sql + " AND chr_cnt " + chr_1; }
                        if (ZI_txt_mitos_1 != null && !ZI_txt_mitos_1.getText().isEmpty()) { sql = sql + " AND mitos_cnt " + mitos_1; }
                        if (ZI_txt_cp_1 != null && !ZI_txt_cp_1.getText().isEmpty()) { sql = sql + " AND cp LIKE '%" + cp_1 + "%'"; }
                        if (ZI_txt_iscn_1 != null && !ZI_txt_iscn_1.getText().isEmpty()) { sql = sql + " AND iscn NOT LIKE '%" + iscn_1 + "%'"; }
                        if (ZI_txt_mat_1 != null && !ZI_txt_mat_1.getText().isEmpty()) { 
                            String tmp = ZI_txt_mat_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND material IS NOT NULL";
                            } else {
                                sql = sql + " AND material NOT LIKE '%" + mat_1 + "%'"; 
                            }
                        }    
                        if (ZI_txt_stim_1 != null && !ZI_txt_stim_1.getText().isEmpty()) { 
                            String tmp = ZI_txt_stim_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND stim IS NOT NULL";
                            } else {
                                sql = sql + " AND stim NOT LIKE '%" + stim_1 + "%'"; 
                            }
                        }

                        sql = sql + ")";

                    } else {	// OR
                        ZI_txt_resId_1.setBackground(java.awt.Color.white);
                        ZI_txt_iscn_1.setBackground(java.awt.Color.white);
                        ZI_txt_mat_1.setBackground(java.awt.Color.white);
                        ZI_txt_stim_1.setBackground(java.awt.Color.white);

                        if (ZI_txt_resId_1 != null && !ZI_txt_resId_1.getText().isEmpty()) { sql = sql + " AND result_id " + resID_1; }
                        if (ZI_txt_chr_1 != null && !ZI_txt_chr_1.getText().isEmpty()) { sql = sql + " AND chr_cnt " + chr_1; }
                        if (ZI_txt_mitos_1 != null && !ZI_txt_mitos_1.getText().isEmpty()) { sql = sql + " AND mitos_cnt " + mitos_1; }
                        if (ZI_txt_cp_1 != null && !ZI_txt_cp_1.getText().isEmpty()) { sql = sql + " AND cp LIKE '%" + cp_1 + "%'"; }
                        if (ZI_txt_iscn_1 != null && !ZI_txt_iscn_1.getText().isEmpty()) { sql = sql + " AND iscn LIKE '%" + iscn_1 + "%'"; }
                                                if (ZI_txt_mat_1 != null && !ZI_txt_mat_1.getText().isEmpty()) { 
                            String tmp = ZI_txt_mat_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND material IS NULL";
                            } else {
                                sql = sql + " AND material LIKE '%" + mat_1 + "%'"; 
                            }
                        }                          
                        if (ZI_txt_stim_1 != null && !ZI_txt_stim_1.getText().isEmpty()) { 
                            String tmp = ZI_txt_stim_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND stim IS NULL";
                            } else {
                                sql = sql + " AND stim LIKE '%" + stim_1 + "%'"; 
                            }
                        }
////////////
                        if (rbtn_ZGdetailResult.isSelected()){
                            if (ZR_txt_klonID_1 !=null && !ZR_txt_klonID_1.getText().isEmpty()) { sql = sql + " AND r.klon_id IN (" + klonID_1 + ")"; }
                            if (ZR_txt_region_1 != null && !ZR_txt_region_1.getText().isEmpty()) { sql = sql + " AND region LIKE '%" + region_1 + "%'"; }
                            if (ZR_txt_chr_1 !=null && !ZR_txt_chr_1.getText().isEmpty()) { sql = sql + " AND chr IN (" + ZR_chr_1 + ")"; }
                            if (ZR_txt_chng_1 != null && !ZR_txt_chng_1.getText().isEmpty()) { sql = sql + " AND chng LIKE '%" + chng_1 + "%'"; }            
                        }
///////////        
                        sql = sql + ")";
                    }

                } else if (andor.equalsIgnoreCase("and")){ // AND
                    sql = sql + " AND ( 1=1 ";

                    if (ZG_rbtn_NOT.isSelected()) {	// AND NOT

                        // color fields where NOT can be used on
                        ZI_txt_resId_1.setBackground(java.awt.Color.yellow);
                        ZI_txt_iscn_1.setBackground(java.awt.Color.yellow);
                        ZI_txt_mat_1.setBackground(java.awt.Color.yellow);
                        ZI_txt_stim_1.setBackground(java.awt.Color.yellow);

                        if (ZI_txt_resId_1 != null && !ZI_txt_resId_1.getText().isEmpty()) {    sql = sql + " AND result_id NOT IN (" + resID_1 + ")"; }
                        if (ZI_txt_chr_1 != null && !ZI_txt_chr_1.getText().isEmpty()) { sql = sql + " AND chr_cnt " + chr_1; }
                        if (ZI_txt_mitos_1 != null && !ZI_txt_mitos_1.getText().isEmpty()) { sql = sql + " AND mitos_cnt " + mitos_1; }
                        if (ZI_txt_cp_1 != null && !ZI_txt_cp_1.getText().isEmpty()) { sql = sql + " AND cp LIKE '%" + cp_1 + "%'"; }
                        if (ZI_txt_iscn_1 != null && !ZI_txt_iscn_1.getText().isEmpty()) { sql = sql + " AND iscn NOT LIKE '%" + iscn_1 + "%'"; }
                        if (ZI_txt_mat_1 != null && !ZI_txt_mat_1.getText().isEmpty()) { 
                            String tmp = ZI_txt_mat_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND material IS NOT NULL";
                            } else {
                                sql = sql + " AND material NOT LIKE '%" + mat_1 + "%'"; 
                            }
                        }    
                        if (ZI_txt_stim_1 != null && !ZI_txt_stim_1.getText().isEmpty()) { 
                            String tmp = ZI_txt_stim_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND stim IS NOT NULL";
                            } else {
                                sql = sql + " AND stim NOT LIKE '%" + stim_1 + "%'"; 
                            }
                        }

                        sql = sql + ")";

                    } else {	// AND
                        ZI_txt_resId_1.setBackground(java.awt.Color.white);
                        ZI_txt_cp_1.setBackground(java.awt.Color.white);
                        ZI_txt_iscn_1.setBackground(java.awt.Color.white);
                        ZI_txt_mat_1.setBackground(java.awt.Color.white);
                        ZI_txt_stim_1.setBackground(java.awt.Color.white);

                        if (ZI_txt_resId_1 != null && !ZI_txt_resId_1.getText().isEmpty()) { sql = sql + " AND result_id " + resID_1; }
                        if (ZI_txt_chr_1 != null && !ZI_txt_chr_1.getText().isEmpty()) { sql = sql + " AND chr_cnt " + chr_1; }
                        if (ZI_txt_mitos_1 != null && !ZI_txt_mitos_1.getText().isEmpty()) { sql = sql + " AND mitos_cnt " + mitos_1; }
                        if (ZI_txt_cp_1 != null && !ZI_txt_cp_1.getText().isEmpty()) { sql = sql + " AND cp LIKE '%" + cp_1 + "%'"; }
                        if (ZI_txt_iscn_1 != null && !ZI_txt_iscn_1.getText().isEmpty()) { sql = sql + " AND iscn LIKE '%" + iscn_1 + "%'"; }
                        if (ZI_txt_mat_1 != null && !ZI_txt_mat_1.getText().isEmpty()) { 
                            String tmp = ZI_txt_mat_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND material IS NULL";
                            } else {
                                sql = sql + " AND material LIKE '%" + mat_1 + "%'"; 
                            }
                        }                          
                        if (ZI_txt_stim_1 != null && !ZI_txt_stim_1.getText().isEmpty()) { 
                            String tmp = ZI_txt_stim_1.getText();
                            if (tmp.equals("null") || tmp.equals("NULL")) {
                                sql = sql + " AND stim IS NULL";
                            } else {
                                sql = sql + " AND stim LIKE '%" + stim_1 + "%'"; 
                            }
                        }
////////////
                        if (rbtn_ZGdetailResult.isSelected()){
                            if (ZR_txt_klonID_1 !=null && !ZR_txt_klonID_1.getText().isEmpty()) { sql = sql + " AND r.klon_id IN (" + klonID_1 + ")"; }
                            if (ZR_txt_region_1 != null && !ZR_txt_region_1.getText().isEmpty()) { sql = sql + " AND region LIKE '%" + region_1 + "%'"; }
                            if (ZR_txt_chr_1 !=null && !ZR_txt_chr_1.getText().isEmpty()) { sql = sql + " AND chr IN (" + ZR_chr_1 + ")"; }
                            if (ZR_txt_chng_1 != null && !ZR_txt_chng_1.getText().isEmpty()) { sql = sql + " AND chng LIKE '%" + chng_1 + "%'"; }            
                        }
///////////        
                        sql = sql + ")";
                    }
                }
            } // END if (ZG_txt_ANDOR != null ...
  
            sql = sql + ")";
            
            String method_name = Thread.currentThread().getStackTrace()[1].getMethodName();
            
            // TODO
            if (rbtn_nyd.isSelected()) {
                //deliver_F_ids(this.ids, sql);
                //sql = this.mod_sql;
            }
            
            // only for a selected patient group
            if (rbtn_onlyPat.isSelected()){
                deliver_Proj_ids(sql, "A");
                sql = this.mod_sql;
            }
            if (rbtn_onlyPat1.isSelected()){
                deliver_Stdy_ids(sql, "A");
                sql = this.mod_sql;
            }    
            
            // only for the set of patient browse result
            if (rbtn_PB.isSelected()) {     
                deliver_PB_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            // only for the set of patient browse result
            if (rbtn_PB.isSelected()) {     
                deliver_PB_ids(method_name,sql);
                sql = this.mod_sql;
            }

            // only for the set of sample browse result
            if (rbtn_SB.isSelected()) {     
                deliver_SB_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            // only for the set of subtypes result
            if (rbtn_ST.isSelected()) {     
                deliver_ST_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            // only for the set of ArrayQuery result
            if (rbtn_ArrQuery.isSelected()) {     
                deliver_AQ_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            if (ZG_rbtn_sort.isSelected()) {
                String order = (String) ZG_ComboBox_sort.getSelectedItem();
                String asdes = ZG_txt_sort.getText();
                if (ZG_ComboBox_sort.getSelectedItem().toString().equals("i.klon_id") 
                        //|| ZG_ComboBox_sort.getSelectedItem().toString().equals("")  
                        //|| ZG_ComboBox_sort.getSelectedItem().toString().equals("") 
                        ) {
                    sql = sql + " order by (" + order + " * 1)" + asdes;
                } else {
                    sql = sql + " order by (" + order + ") " + asdes;
                }
            }
          
            get_ids(sql, pst, rs, conn);
            display_ids();
            //txtArea_genes.setText(sql);  // only for Testing
            my_log.logger.info("SQL:  " + sql);
            
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_zg_iscn.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_zg_iscn);

            // resize column width
            jScrollPane3.setViewportView(table_zg_iscn);
            if (table_zg_iscn.getColumnModel().getColumnCount() > 0) {
                table_zg_iscn.getColumnModel().getColumn(0).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(0).setMaxWidth(60);
                table_zg_iscn.getColumnModel().getColumn(1).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(1).setMaxWidth(60);
                table_zg_iscn.getColumnModel().getColumn(2).setPreferredWidth(45);
                table_zg_iscn.getColumnModel().getColumn(2).setMaxWidth(45);
                table_zg_iscn.getColumnModel().getColumn(3).setPreferredWidth(55);   
                table_zg_iscn.getColumnModel().getColumn(3).setMaxWidth(55);   
                table_zg_iscn.getColumnModel().getColumn(4).setPreferredWidth(50);   
                table_zg_iscn.getColumnModel().getColumn(4).setMaxWidth(50); 
                table_zg_iscn.getColumnModel().getColumn(5).setPreferredWidth(500);   // iscn
                table_zg_iscn.getColumnModel().getColumn(5).setMaxWidth(800);         // iscn
                table_zg_iscn.getColumnModel().getColumn(6).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(6).setMaxWidth(150);
                table_zg_iscn.getColumnModel().getColumn(7).setPreferredWidth(60);
                table_zg_iscn.getColumnModel().getColumn(7).setMaxWidth(200);
            }
            
            // if resultset is empty
            if(!rs.first()){
                //JOptionPane.showMessageDialog(null, "No result for that query");
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                //JOptionPane.showMessageDialog(null, getRows+" row(s) returned");
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
                DefaultTableModel model = (DefaultTableModel) table_queryIDs.getModel();
                model.setRowCount(0);
            }
            
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                //JOptionPane.showMessageDialog(null, getRows+" row(s) returned");
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
            }
                        
            get_statistics(sql);
            get_queryLabIDs(sql, pst, rs, conn);
            //txtArea_genes.setText(sql);   //TEST
            
        }catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "Something is wrong ...");
            my_log.logger.warning("ERROR: " + e);
            
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_Z_btn_searchActionPerformed

    private void A_btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_A_btn_searchActionPerformed
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        // Query 1
        String resID = A_txt_resID.getText();
        String chr = A_txt_chr.getText();
        String type = A_txt_type.getText();
        String cnst = A_txt_cnst.getText();
        String call = A_txt_call.getText();
        String nom = A_txt_nom.getText();
        String size = A_txt_size.getText();
        String locStart = A_txt_locStart.getText();
        String locEnd = A_txt_locEnd.getText();
        String cytoReg = A_txt_Creg.getText();
        // genes 1
        String genes1_1 = A_txt1_genes1.getText();
        String genes1_2 = A_txt2_genes1.getText();
        String genes1_3 = A_txt3_genes1.getText();
        String genes1_4 = A_txt4_genes1.getText();
        String genes1_5 = A_txt5_genes1.getText();

        // Query 2
        String resID_1 = A_txt_resID_1.getText();
        String chr_1 = A_txt_chr_1.getText();
        String type_1 = A_txt_type_1.getText();
        String cnst_1 = A_txt_cnst_1.getText();
        String call_1 = A_txt_call_1.getText();
        String nom_1 = A_txt_nom_1.getText();
        String size_1 = A_txt_size_1.getText();
        String locStart_1 = A_txt_locStart_1.getText();
        String locEnd_1 = A_txt_locEnd_1.getText();
        String cytoReg_1 = A_txt_Creg_1.getText();
        // genes 2
        String genes2_1 = A_txt1_genes2.getText();
        String genes2_2 = A_txt2_genes2.getText();
        String genes2_3 = A_txt3_genes2.getText();
        String genes2_4 = A_txt4_genes2.getText();
        String genes2_5 = A_txt5_genes2.getText();

        try {
            String sql = "SELECT array_sub_id as ID, ma_nom, a.result_id, chr, arr_type as type, cnst, arr_call, size, loc_start, loc_end, cyto_regions AS cyto_regions, genes FROM arr_result a, main_result m " +
            "WHERE a.result_id=m.result_id AND (1=1";

            if (A_txt_resID !=null && !A_txt_resID.getText().isEmpty()) { sql = sql + " AND a.result_id IN (" + resID + ")"; }
            if (A_txt_chr !=null && !A_txt_chr.getText().isEmpty()){ sql = sql + " AND chr IN (" + chr + ")"; }
            if (A_txt_type !=null && !A_txt_type.getText().isEmpty()) { sql = sql + " AND arr_type LIKE '%" + type + "%'"; }
            if (A_txt_cnst !=null && !A_txt_cnst.getText().isEmpty()) { sql = sql + " AND cnst " + cnst; }
            //if (A_txt_call !=null && !A_txt_call.getText().isEmpty()) { sql = sql + " AND arr_call LIKE '%" + call + "%'"; }
            if (A_txt_call !=null && !A_txt_call.getText().isEmpty()) {
                String tmp = A_txt_call.getText();
                if (tmp.equals("null") || tmp.equals("NULL")){
                    sql = sql + " AND arr_call IS NULL";
                }
                else{
                    sql = sql + " AND arr_call LIKE '%" + call + "%'";
                }              
            }
            if (A_txt_nom !=null && !A_txt_nom.getText().isEmpty()) { sql = sql + " AND ma_nom LIKE '%" + nom + "%'"; }
            if (A_txt_size !=null && !A_txt_size.getText().isEmpty()) { sql = sql + " AND size " + size; }
            if (A_txt_locStart !=null && !A_txt_locStart.getText().isEmpty()) { sql = sql + " AND loc_start " + locStart; }
            if (A_txt_locEnd !=null && !A_txt_locEnd.getText().isEmpty()) { sql = sql + " AND loc_End " + locEnd; }
            if (A_txt_Creg != null && !A_txt_Creg.getText().isEmpty()) { sql = sql + " AND cyto_regions LIKE '%" + cytoReg + "%'";}

            if (A_txt1_genes1 !=null && !A_txt1_genes1.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes1_1 + "%'";}
            if (A_txt2_genes1 !=null && !A_txt2_genes1.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes1_2 + "%'";}
            if (A_txt3_genes1 !=null && !A_txt3_genes1.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes1_3 + "%'";}
            if (A_txt4_genes1 !=null && !A_txt4_genes1.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes1_4 + "%'";}
            if (A_txt5_genes1 !=null && !A_txt5_genes1.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes1_5 + "%'";}

            if (A_txt_ANDOR != null && !A_txt_ANDOR.getText().isEmpty()) {

                String andor = A_txt_ANDOR.getText();

                //if (andor.equals("or")) { // OR
                if (andor.equalsIgnoreCase("or")) { // OR
                    sql = sql + " OR ( 1=1 ";

                    if (A_rbtn_NOT.isSelected()) {

                        // color fields where NOT can be used on
                        A_txt_resID_1.setBackground(java.awt.Color.yellow);
                        A_txt_chr_1.setBackground(java.awt.Color.yellow);
                        A_txt_type_1.setBackground(java.awt.Color.yellow);
                        A_txt_call_1.setBackground(java.awt.Color.yellow);
                        A_txt_nom_1.setBackground(java.awt.Color.yellow);
                        A_txt_Creg_1.setBackground(java.awt.Color.yellow);

                        A_txt3_genes2.setBackground(java.awt.Color.yellow);
                        A_txt4_genes2.setBackground(java.awt.Color.yellow);
                        A_txt5_genes2.setBackground(java.awt.Color.yellow);

                        if (A_txt_resID_1 !=null && !A_txt_resID_1.getText().isEmpty()) { sql = sql + " AND a.result_id NOT IN (" + resID_1 + ")"; }
                        if (A_txt_chr_1 !=null && !A_txt_chr_1.getText().isEmpty()){ sql = sql + " AND chr NOT IN (" + chr_1 + ")"; }
                        if (A_txt_type_1 !=null && !A_txt_type_1.getText().isEmpty()) { sql = sql + " AND arr_type NOT LIKE '%" + type_1 + "%'"; }
                        if (A_txt_cnst_1 !=null && !A_txt_cnst_1.getText().isEmpty()) { sql = sql + " AND cnst " + cnst_1; }
                        if (A_txt_call_1 !=null && !A_txt_call_1.getText().isEmpty()) { sql = sql + " AND arr_call NOT LIKE '%" + call_1 + "%'"; }
                        if (A_txt_nom_1 !=null && !A_txt_nom_1.getText().isEmpty()) { sql = sql + " AND ma_nom NOT LIKE '%" + nom_1 + "%'"; }
                        if (A_txt_size_1 !=null && !A_txt_size_1.getText().isEmpty()) { sql = sql + " AND size " + size_1; }
                        if (A_txt_locStart_1 !=null && !A_txt_locStart_1.getText().isEmpty()) { sql = sql + " AND loc_start " + locStart_1; }
                        if (A_txt_locEnd_1 !=null && !A_txt_locEnd_1.getText().isEmpty()) { sql = sql + " AND loc_End " + locEnd_1; }
                        if (A_txt_Creg_1 != null && !A_txt_Creg_1.getText().isEmpty()) { sql = sql + " AND cyto_regions NOT LIKE '%" + cytoReg_1 + "%'";}

                        if (A_txt1_genes2 !=null && !A_txt1_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_1 + "%'";}
                        if (A_txt2_genes2 !=null && !A_txt2_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_2 + "%'";}
                        if (A_txt3_genes2 !=null && !A_txt3_genes2.getText().isEmpty()) { sql = sql + " AND genes NOT LIKE '%" + genes2_3 + "%'";}
                        if (A_txt4_genes2 !=null && !A_txt4_genes2.getText().isEmpty()) { sql = sql + " AND genes NOT LIKE '%" + genes2_4 + "%'";}
                        if (A_txt5_genes2 !=null && !A_txt5_genes2.getText().isEmpty()) { sql = sql + " AND genes NOT LIKE '%" + genes2_5 + "%'";}

                        sql = sql + ")";

                    } else {
                        A_txt_resID_1.setBackground(java.awt.Color.white);
                        A_txt_chr_1.setBackground(java.awt.Color.white);
                        A_txt_type_1.setBackground(java.awt.Color.white);
                        A_txt_call_1.setBackground(java.awt.Color.white);
                        A_txt_nom_1.setBackground(java.awt.Color.white);
                        A_txt_Creg_1.setBackground(java.awt.Color.white);

                        A_txt3_genes2.setBackground(java.awt.Color.white);
                        A_txt4_genes2.setBackground(java.awt.Color.white);
                        A_txt5_genes2.setBackground(java.awt.Color.white);

                        if (A_txt_resID_1 !=null && !A_txt_resID_1.getText().isEmpty()) { sql = sql + " AND a.result_id IN (" + resID_1 + ")"; }
                        if (A_txt_chr_1 !=null && !A_txt_chr_1.getText().isEmpty()){ sql = sql + " AND chr IN (" + chr_1 + ")"; }
                        if (A_txt_type_1 !=null && !A_txt_type_1.getText().isEmpty()) { sql = sql + " AND arr_type LIKE '%" + type_1 + "%'"; }
                        if (A_txt_cnst_1 !=null && !A_txt_cnst_1.getText().isEmpty()) { sql = sql + " AND cnst " + cnst_1; }
                        if (A_txt_call_1 !=null && !A_txt_call_1.getText().isEmpty()) { sql = sql + " AND arr_call LIKE '%" + call_1 + "%'"; }
                        if (A_txt_nom_1 !=null && !A_txt_nom_1.getText().isEmpty()) { sql = sql + " AND ma_nom LIKE '%" + nom_1 + "%'"; }
                        if (A_txt_size_1 !=null && !A_txt_size_1.getText().isEmpty()) { sql = sql + " AND size " + size_1; }
                        if (A_txt_locStart_1 !=null && !A_txt_locStart_1.getText().isEmpty()) { sql = sql + " AND loc_start " + locStart_1; }
                        if (A_txt_locEnd_1 !=null && !A_txt_locEnd_1.getText().isEmpty()) { sql = sql + " AND loc_End " + locEnd_1; }
                        if (A_txt_Creg_1 != null && !A_txt_Creg_1.getText().isEmpty()) { sql = sql + " AND cyto_regions LIKE '%" + cytoReg_1 + "%'";}

                        if (A_txt1_genes2 !=null && !A_txt1_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_1 + "%'";}
                        if (A_txt2_genes2 !=null && !A_txt2_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_2 + "%'";}
                        if (A_txt3_genes2 !=null && !A_txt3_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_3 + "%'";}
                        if (A_txt4_genes2 !=null && !A_txt4_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_4 + "%'";}
                        if (A_txt5_genes2 !=null && !A_txt5_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_5 + "%'";}

                        sql = sql + ")";
                    }

                } else if (andor.equalsIgnoreCase("and"))  { // AND
                    sql = sql + " AND ( 1=1 ";

                    if(A_rbtn_NOT.isSelected()){
                        // color fields where NOT can be used on
                        A_txt_resID_1.setBackground(java.awt.Color.yellow);
                        A_txt_chr_1.setBackground(java.awt.Color.yellow);
                        A_txt_type_1.setBackground(java.awt.Color.yellow);
                        A_txt_call_1.setBackground(java.awt.Color.yellow);
                        A_txt_nom_1.setBackground(java.awt.Color.yellow);
                        A_txt_Creg_1.setBackground(java.awt.Color.yellow);

                        A_txt3_genes2.setBackground(java.awt.Color.yellow);
                        A_txt4_genes2.setBackground(java.awt.Color.yellow);
                        A_txt5_genes2.setBackground(java.awt.Color.yellow);

                        if (A_txt_resID_1 !=null && !A_txt_resID_1.getText().isEmpty()) { sql = sql + " AND a.result_id NOT IN (" + resID_1 + ")"; }
                        if (A_txt_chr_1 !=null && !A_txt_chr_1.getText().isEmpty()){ sql = sql + " AND chr NOT IN (" + chr_1 + ")"; }
                        if (A_txt_type_1 !=null && !A_txt_type_1.getText().isEmpty()) { sql = sql + " AND arr_type NOT LIKE '%" + type_1 + "%'"; }
                        if (A_txt_cnst_1 !=null && !A_txt_cnst_1.getText().isEmpty()) { sql = sql + " AND cnst " + cnst_1; }
                        if (A_txt_call_1 !=null && !A_txt_call_1.getText().isEmpty()) { sql = sql + " AND arr_call NOT LIKE '%" + call_1 + "%'"; }
                        if (A_txt_nom_1 !=null && !A_txt_nom_1.getText().isEmpty()) { sql = sql + " AND ma_nom NOT LIKE '%" + nom_1 + "%'"; }
                        if (A_txt_size_1 !=null && !A_txt_size_1.getText().isEmpty()) { sql = sql + " AND size " + size_1; }
                        if (A_txt_locStart_1 !=null && !A_txt_locStart_1.getText().isEmpty()) { sql = sql + " AND loc_start " + locStart_1; }
                        if (A_txt_locEnd_1 !=null && !A_txt_locEnd_1.getText().isEmpty()) { sql = sql + " AND loc_End " + locEnd_1; }
                        if (A_txt_Creg_1 != null && !A_txt_Creg_1.getText().isEmpty()) { sql = sql + " AND cyto_regions NOT LIKE '%" + cytoReg_1 + "%'";}

                        if (A_txt1_genes2 !=null && !A_txt1_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_1 + "%'";}
                        if (A_txt2_genes2 !=null && !A_txt2_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_2 + "%'";}
                        if (A_txt3_genes2 !=null && !A_txt3_genes2.getText().isEmpty()) { sql = sql + " AND genes NOT LIKE '%" + genes2_3 + "%'";}
                        if (A_txt4_genes2 !=null && !A_txt4_genes2.getText().isEmpty()) { sql = sql + " AND genes NOT LIKE '%" + genes2_4 + "%'";}
                        if (A_txt5_genes2 !=null && !A_txt5_genes2.getText().isEmpty()) { sql = sql + " AND genes NOT LIKE '%" + genes2_5 + "%'";}

                        sql = sql + ")";

                    } else {
                        A_txt_resID_1.setBackground(java.awt.Color.white);
                        A_txt_chr_1.setBackground(java.awt.Color.white);
                        A_txt_type_1.setBackground(java.awt.Color.white);
                        A_txt_call_1.setBackground(java.awt.Color.white);
                        A_txt_nom_1.setBackground(java.awt.Color.white);
                        A_txt_Creg_1.setBackground(java.awt.Color.white);

                        A_txt3_genes2.setBackground(java.awt.Color.white);
                        A_txt4_genes2.setBackground(java.awt.Color.white);
                        A_txt5_genes2.setBackground(java.awt.Color.white);

                        if (A_txt_resID_1 !=null && !A_txt_resID_1.getText().isEmpty()) { sql = sql + " AND a.result_id IN (" + resID_1 + ")"; }
                        if (A_txt_chr_1 !=null && !A_txt_chr_1.getText().isEmpty()){ sql = sql + " AND chr IN (" + chr_1 + ")"; }
                        if (A_txt_type_1 !=null && !A_txt_type_1.getText().isEmpty()) { sql = sql + " AND arr_type LIKE '%" + type_1 + "%'"; }
                        if (A_txt_cnst_1 !=null && !A_txt_cnst_1.getText().isEmpty()) { sql = sql + " AND cnst " + cnst_1; }
                        if (A_txt_call_1 !=null && !A_txt_call_1.getText().isEmpty()) { sql = sql + " AND arr_call LIKE '%" + call_1 + "%'"; }
                        if (A_txt_nom_1 !=null && !A_txt_nom_1.getText().isEmpty()) { sql = sql + " AND ma_nom LIKE '%" + nom_1 + "%'"; }
                        if (A_txt_size_1 !=null && !A_txt_size_1.getText().isEmpty()) { sql = sql + " AND size " + size_1; }
                        if (A_txt_locStart_1 !=null && !A_txt_locStart_1.getText().isEmpty()) { sql = sql + " AND loc_start " + locStart_1; }
                        if (A_txt_locEnd_1 !=null && !A_txt_locEnd_1.getText().isEmpty()) { sql = sql + " AND loc_End " + locEnd_1; }
                        if (A_txt_Creg_1 != null && !A_txt_Creg_1.getText().isEmpty()) { sql = sql + " AND cyto_regions LIKE '%" + cytoReg_1 + "%'";}

                        if (A_txt1_genes2 !=null && !A_txt1_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_1 + "%'";}
                        if (A_txt2_genes2 !=null && !A_txt2_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_2 + "%'";}
                        if (A_txt3_genes2 !=null && !A_txt3_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_3 + "%'";}
                        if (A_txt4_genes2 !=null && !A_txt4_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_4 + "%'";}
                        if (A_txt5_genes2 !=null && !A_txt5_genes2.getText().isEmpty()) { sql = sql + " AND genes LIKE '%" + genes2_5 + "%'";}

                        sql = sql + ")";

                    }
                }
            } // END if (A_txt_ANDOR != null ...

            sql = sql + ")";

            String method_name = Thread.currentThread().getStackTrace()[1].getMethodName();

            // only for the set of fish-result    
            if (rbtn_useFresult.isSelected()) {
                //deliver_F_ids(this.ids, sql);
                deliver_ids(method_name,this.ids, sql);
                sql = this.mod_sql;
            }
            
            // only for a selected patient group
            if (rbtn_onlyPat.isSelected()){
                deliver_Proj_ids(sql, "A");
                sql = this.mod_sql;
            }
            if (rbtn_onlyPat1.isSelected()){
                deliver_Stdy_ids(sql, "A");
                sql = this.mod_sql;
            }          

            // only for the set of patient browse result
            if (rbtn_PB.isSelected()) {     
                deliver_PB_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            // only for the set of sample browse result
            if (rbtn_SB.isSelected()) {     
                deliver_SB_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            // only for the set of subtypes result
            if (rbtn_ST.isSelected()) {     
                deliver_ST_ids(method_name,sql);
                sql = this.mod_sql;
            }

            // only for the set of ArrayQuery result
            if (rbtn_ArrQuery.isSelected()) {     
                deliver_AQ_ids(method_name,sql);
                sql = this.mod_sql;
            }
            
            if (A_rbtn_sort.isSelected()) {
                String order = (String) A_ComboBox_sort.getSelectedItem();
                String asdes = A_txt_sort.getText();
                if (A_ComboBox_sort.getSelectedItem().toString().equals("array_sub_id")
                        || A_ComboBox_sort.getSelectedItem().toString().equals("chr") //|| A_ComboBox_sort.getSelectedItem().toString().equals("") 
                        //|| A_ComboBox_sort.getSelectedItem().toString().equals("")      // percent necessary?
                        ) {
                    sql = sql + " order by (" + order + " * 1)" + asdes;
                } else {
                    sql = sql + " order by (" + order + ") " + asdes;
                }
            }

            get_ids(sql, pst, rs, conn);
            display_ids();
            //txtArea_genes.setText(sql); // test
            my_log.logger.info("SQL:  " + sql);
                        
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_array.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_array); 

            // resize column width
            jScrollPane1.setViewportView(table_array);
            if (table_array.getColumnModel().getColumnCount() > 0) {
                table_array.getColumnModel().getColumn(0).setPreferredWidth(65);    // 50
                table_array.getColumnModel().getColumn(0).setMaxWidth(65);          // 50
                table_array.getColumnModel().getColumn(1).setPreferredWidth(300);   // ma_nom
                table_array.getColumnModel().getColumn(1).setMaxWidth(600);         // ma_nom

                table_array.getColumnModel().getColumn(2).setPreferredWidth(60);
                table_array.getColumnModel().getColumn(2).setMaxWidth(60);
                table_array.getColumnModel().getColumn(3).setPreferredWidth(55); // chr
                table_array.getColumnModel().getColumn(3).setMaxWidth(55);
                table_array.getColumnModel().getColumn(4).setPreferredWidth(55); // type
                table_array.getColumnModel().getColumn(4).setMaxWidth(100);
                table_array.getColumnModel().getColumn(5).setPreferredWidth(60);
                table_array.getColumnModel().getColumn(5).setMaxWidth(60);
                table_array.getColumnModel().getColumn(6).setPreferredWidth(110);
                table_array.getColumnModel().getColumn(6).setMaxWidth(200);
                table_array.getColumnModel().getColumn(7).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(7).setMaxWidth(100);
                table_array.getColumnModel().getColumn(8).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(8).setMaxWidth(100);
                table_array.getColumnModel().getColumn(9).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(9).setMaxWidth(100);
                table_array.getColumnModel().getColumn(10).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(10).setMaxWidth(200);
            }

            // if resultset is empty
            if(!rs.first()){
                //JOptionPane.showMessageDialog(null, "No result for that query");
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                //JOptionPane.showMessageDialog(null, getRows+" row(s) returned");
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
                DefaultTableModel model = (DefaultTableModel) table_queryIDs.getModel();
                model.setRowCount(0);
            }
                        
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                //JOptionPane.showMessageDialog(null, getRows+" row(s) returned");
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
            }
            
            get_statistics(sql);
            get_queryLabIDs(sql, pst, rs, conn);

        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "Something is wrong ...");
            my_log.logger.warning("ERROR: " + e);
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_A_btn_searchActionPerformed

    private void A_btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_A_btn_clearActionPerformed
        initial_table_array();
        //initial_table_fish();
        //initial_table_zg_iscn();
        //initial_table_zg_result();
        initial_table_statistics();
        initial_table_queryIDs(); 
       
        clearBtnColors();

        // unselect rbtn
        A_rbtn_NOT.setSelected(false);
        A_rbtn_sort.setSelected(false);
        rbtn_useFresult.setSelected(false);
        rbtn_onlyPat.setSelected(false);
        rbtn_onlyPat1.setSelected(false);
        
        A_txt_ANDOR.setText("");
        A_txt_lab_id.setText("");
        A_txt_array_sub_id.setText("");
        A_txt_result_id.setText("");
        txt_fullLoc.setText("");
        txtArea_genes.setText("");
        txtArea_Creg.setText("");
        //txtArea_sql.setText("");

        A_txt_resID.setText("");
        A_txt_chr.setText("");
        A_txt_type.setText("");
        A_txt_cnst.setText("");
        A_txt_call.setText("");
        A_txt_nom.setText("");
        A_txt_size.setText("");
        A_txt_locStart.setText("");
        A_txt_locEnd.setText("");
        A_txt_Creg.setText("");
        // genes 1
        A_txt1_genes1.setText("");
        A_txt2_genes1.setText("");
        A_txt3_genes1.setText("");
        A_txt4_genes1.setText("");
        A_txt5_genes1.setText("");

        // Query 2
        A_txt_resID_1.setText("");
        A_txt_chr_1.setText("");
        A_txt_type_1.setText("");
        A_txt_cnst_1.setText("");
        A_txt_call_1.setText("");
        A_txt_nom_1.setText("");
        A_txt_size_1.setText("");
        A_txt_locStart_1.setText("");
        A_txt_locEnd_1.setText("");
        A_txt_Creg_1.setText("");
        // genes 2
        A_txt1_genes2.setText("");
        A_txt2_genes2.setText("");
        A_txt3_genes2.setText("");
        A_txt4_genes2.setText("");
        A_txt5_genes2.setText("");
        
        txt_genOnc.setText("");

        A_txt_resID_1.setBackground(java.awt.Color.white);
        A_txt_chr_1.setBackground(java.awt.Color.white);
        A_txt_type_1.setBackground(java.awt.Color.white);
        A_txt_call_1.setBackground(java.awt.Color.white);
        A_txt_nom_1.setBackground(java.awt.Color.white);
        A_txt_Creg_1.setBackground(java.awt.Color.white);

        A_txt3_genes2.setBackground(java.awt.Color.white);
        A_txt4_genes2.setBackground(java.awt.Color.white);
        A_txt5_genes2.setBackground(java.awt.Color.white);
    }//GEN-LAST:event_A_btn_clearActionPerformed

    private void btn_openLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_openLocActionPerformed
        try {
            String loc = txt_fullLoc.getText();
            String URL = "http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&position=" + loc;
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }//GEN-LAST:event_btn_openLocActionPerformed

    private void A_txt_lab_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_A_txt_lab_idActionPerformed

        update_table_array(); 
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String lab_id = A_txt_lab_id.getText();        
            A_txt_array_sub_id.setText("");
            String tmpSql = "SELECT a.result_id, lab_id FROM arr_result a, main_result m WHERE a.result_id=m.result_id AND lab_id = '" + lab_id + "'";

            //txtArea_sql.setText(tmpSql);  // TEST
            get_ids(tmpSql, pst, rs, conn); // needed for get_statistics() to count patients affected
            get_statistics(tmpSql);
            get_queryLabIDs(tmpSql, pst, rs, conn);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_A_txt_lab_idActionPerformed

    private void table_arrayKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_arrayKeyReleased
        // same code as in table_arrayMouseClicked (without right-klick detection)
        Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            try {
                int row = table_array.getSelectedRow();
                //String Table_click = (table_array.getModel().getValueAt(row, 0).toString());        // values not correct anymore, if auto table rowsorter is used -->
                String Table_click = (table_array.getValueAt(row, 0).toString());
                click_result = (table_array.getValueAt(row, 1).toString());
                click_lID = Table_click;

                //String sql = "SELECT * FROM fish_result r, fish_probe p, main_result m WHERE r.probe_no=p.probe_no AND m.result_id=r.result_id AND fish_sub_id='" + Table_click + "' ";
                String sql = "SELECT * FROM arr_result a, main_result m WHERE a.result_id=m.result_id AND array_sub_id='" + Table_click + "' ";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String add1 = rs.getString("array_sub_id");
                    A_txt_array_sub_id.setText(add1);
                    String add2 = rs.getString("result_id");
                    A_txt_result_id.setText(add2);
                    String add3 = rs.getString("lab_id");
                    A_txt_lab_id.setText(add3);
                    String add4 = rs.getString("genes");
                    txtArea_genes.setText(add4);
                                        
                    // IF interpretation window is open
                    if(IntrprWindowIsOpen  == true){
                        updateIntrpr(add2);     // update text in Window "ResultWindow"
                    }
                    
                    // highlight searched genes in text area
                    if(A_txt1_genes1 != null && !A_txt1_genes1.getText().isEmpty()){
                        String gen1 = A_txt1_genes1.getText();
                        highlight_gene(gen1,1);
                    }
                    if(A_txt2_genes1 != null && !A_txt2_genes1.getText().isEmpty()){
                        String gen2 = A_txt2_genes1.getText();
                        highlight_gene(gen2,2);
                    }
                    if(A_txt3_genes1 != null && !A_txt3_genes1.getText().isEmpty()){
                        String gen3 = A_txt3_genes1.getText();
                        highlight_gene(gen3,3);
                    }
                    if(A_txt4_genes1 != null && !A_txt4_genes1.getText().isEmpty()){
                        String gen4 = A_txt4_genes1.getText();
                        highlight_gene(gen4,4);
                    }
                    if(A_txt5_genes1 != null && !A_txt5_genes1.getText().isEmpty()){
                        String gen5 = A_txt5_genes1.getText();
                        highlight_gene(gen5,5);
                    }
                    /////////////////
                    if(A_txt1_genes2 != null && !A_txt1_genes2.getText().isEmpty()){
                        String gen1_1 = A_txt1_genes2.getText();
                        highlight_gene(gen1_1,1);
                    }
                    if(A_txt2_genes2 != null && !A_txt2_genes2.getText().isEmpty()){
                        String gen2_1 = A_txt2_genes2.getText();
                        highlight_gene(gen2_1,2);
                    }
                    if(A_txt3_genes2 != null && !A_txt3_genes2.getText().isEmpty()){
                        String gen3_1 = A_txt3_genes2.getText();
                        highlight_gene(gen3_1,3);
                    }
                    if(A_txt4_genes2 != null && !A_txt4_genes2.getText().isEmpty()){
                        String gen4_1 = A_txt4_genes2.getText();
                        highlight_gene(gen4_1,4);
                    }
                    if(A_txt5_genes2 != null && !A_txt5_genes2.getText().isEmpty()){
                        String gen5_1 = A_txt5_genes2.getText();
                        highlight_gene(gen5_1,5);
                    }

                    String add5 = rs.getString("cyto_regions");
                    txtArea_Creg.setText(add5);
                    
                    // highlight searched genes in text area
                    if (A_txt_Creg != null && !A_txt_Creg.getText().isEmpty()){
                        String creg1 = A_txt_Creg.getText();
                        highlight_creg(creg1,1);
                    }
                    if (A_txt_Creg_1 != null && !A_txt_Creg_1.getText().isEmpty()){
                        String creg1_1 = A_txt_Creg_1.getText();
                        highlight_creg(creg1_1,2);
                    }

                    String add6 = rs.getString("full_loc");
                    txt_fullLoc.setText(add6);
                    //txtArea_sql.setText(sql);
                    //get_queryLabIDs(sql, pst, rs, conn);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            } finally {
                try {
                    if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }
    }//GEN-LAST:event_table_arrayKeyReleased

    private void table_arrayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_arrayMouseClicked

        // right click  ==> save to file
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_array);
            popUpSave.show(table_array,evt.getX(),evt.getY());
            this.outTable = table_array;

        } else {

            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            try {
                int row = table_array.getSelectedRow();
                //String Table_click = (table_array.getModel().getValueAt(row, 0).toString());        // values not correct anymore, if auto table rowsorter is used -->
                String Table_click = (table_array.getValueAt(row, 0).toString());
                click_result = (table_array.getValueAt(row, 1).toString());
                click_lID = Table_click;

                String sql = "SELECT * FROM arr_result a, main_result m WHERE a.result_id=m.result_id AND array_sub_id='" + Table_click + "' ";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String add1 = rs.getString("array_sub_id");
                    A_txt_array_sub_id.setText(add1);
                    String add2 = rs.getString("result_id");
                    A_txt_result_id.setText(add2);
                    String add3 = rs.getString("lab_id");
                    A_txt_lab_id.setText(add3);
                    String add4 = rs.getString("genes");
                    txtArea_genes.setText(add4);                 
                    
                    // IF interpretation window is open
                    if(IntrprWindowIsOpen  == true){
                        updateIntrpr(add2);     // update text in Window "ResultWindow"
                    }
                    
                    // highlight searched genes in text area
                    if(A_txt1_genes1 != null && !A_txt1_genes1.getText().isEmpty()){
                        String gen1 = A_txt1_genes1.getText();
                        highlight_gene(gen1,1);
                    }
                    if(A_txt2_genes1 != null && !A_txt2_genes1.getText().isEmpty()){
                        String gen2 = A_txt2_genes1.getText();
                        highlight_gene(gen2,2);
                    }
                    if(A_txt3_genes1 != null && !A_txt3_genes1.getText().isEmpty()){
                        String gen3 = A_txt3_genes1.getText();
                        highlight_gene(gen3,3);
                    }
                    if(A_txt4_genes1 != null && !A_txt4_genes1.getText().isEmpty()){
                        String gen4 = A_txt4_genes1.getText();
                        highlight_gene(gen4,4);
                    }
                    if(A_txt5_genes1 != null && !A_txt5_genes1.getText().isEmpty()){
                        String gen5 = A_txt5_genes1.getText();
                        highlight_gene(gen5,5);
                    }
                    ////////////////////////////
                    if(A_txt1_genes2 != null && !A_txt1_genes2.getText().isEmpty()){
                        String gen1_1 = A_txt1_genes2.getText();
                        highlight_gene(gen1_1,1);
                    }
                    if(A_txt2_genes2 != null && !A_txt2_genes2.getText().isEmpty()){
                        String gen2_1 = A_txt2_genes2.getText();
                        highlight_gene(gen2_1,2);
                    }
                    if(A_txt3_genes2 != null && !A_txt3_genes2.getText().isEmpty()){
                        String gen3_1 = A_txt3_genes2.getText();
                        highlight_gene(gen3_1,3);
                    }
                    if(A_txt4_genes2 != null && !A_txt4_genes2.getText().isEmpty()){
                        String gen4_1 = A_txt4_genes2.getText();
                        highlight_gene(gen4_1,4);
                    }
                    if(A_txt5_genes2 != null && !A_txt5_genes2.getText().isEmpty()){
                        String gen5_1 = A_txt5_genes2.getText();
                        highlight_gene(gen5_1,5);
                    }

                    String add5 = rs.getString("cyto_regions");
                    txtArea_Creg.setText(add5);
                    // highlight searched genes in text area
                    if (A_txt_Creg != null && !A_txt_Creg.getText().isEmpty()){
                        String creg1 = A_txt_Creg.getText();
                        highlight_creg(creg1,1);
                    }
                    if (A_txt_Creg_1 != null && !A_txt_Creg_1.getText().isEmpty()){
                        String creg1_1 = A_txt_Creg_1.getText();
                        highlight_creg(creg1_1,2);
                    }

                    String add6 = rs.getString("full_loc");
                    txt_fullLoc.setText(add6);
                    //txtArea_sql.setText(sql);
                    //get_queryLabIDs(sql, pst, rs, conn);
                }
            } catch (Exception e) {
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
    }//GEN-LAST:event_table_arrayMouseClicked

    private void btn_openGenOncActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_openGenOncActionPerformed
        try {
            String gene = txt_genOnc.getText();

            String URL = "http://atlasgeneticsoncology.org/Genes/GC_"+ gene + ".html";
            java.awt.Desktop.getDesktop().browse(java.net.URI.create(URL));

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    
    }//GEN-LAST:event_btn_openGenOncActionPerformed

    private void F_txt_result_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_F_txt_result_idActionPerformed
        //update_table_fish();
        
        /*********  Do not use ... umständlich das Feld, bei neuer Abfrage immer löschen zu müssen
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {

            String result_id = F_txt_result_id.getText();
            F_txt_fish_sub_id.setText("");
            String sql = "SELECT fish_sub_id as ID, result_id, i_count, m_count, percent, region1, reg1_sig as sig1, region2, reg2_sig as sig2, fsn_sig, fish_chng, result, material FROM fish_result r\n"
                       + ", fish_probe p WHERE r.probe_no=p.probe_no AND r.result_id in (" + result_id + ")";
            
            //txtArea_sql.setText(sql);
                      
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            get_ids(sql, pst, rs, conn); // needed for get_statistics() to count patients affected
            
            table_fish.setModel(DbUtils.resultSetToTableModel(rs));
            // resize column width
            jScrollPane2.setViewportView(table_fish);
            if (table_fish.getColumnModel().getColumnCount() > 0) {
                table_fish.getColumnModel().getColumn(0).setPreferredWidth(65); //55
                table_fish.getColumnModel().getColumn(0).setMaxWidth(65);       //55

                table_fish.getColumnModel().getColumn(1).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(1).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(2).setPreferredWidth(55);
                table_fish.getColumnModel().getColumn(2).setMaxWidth(55);

                table_fish.getColumnModel().getColumn(3).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(3).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(4).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(4).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(5).setPreferredWidth(100);
                table_fish.getColumnModel().getColumn(5).setMaxWidth(300);
                table_fish.getColumnModel().getColumn(6).setPreferredWidth(45);
                table_fish.getColumnModel().getColumn(6).setMaxWidth(45);
                table_fish.getColumnModel().getColumn(7).setPreferredWidth(100);
                table_fish.getColumnModel().getColumn(7).setMaxWidth(300);
                table_fish.getColumnModel().getColumn(8).setPreferredWidth(45);
                table_fish.getColumnModel().getColumn(8).setMaxWidth(45);

                table_fish.getColumnModel().getColumn(9).setPreferredWidth(60);
                table_fish.getColumnModel().getColumn(9).setMaxWidth(60);

                table_fish.getColumnModel().getColumn(10).setPreferredWidth(100);  // fish_chng
                table_fish.getColumnModel().getColumn(10).setMaxWidth(500);

                table_fish.getColumnModel().getColumn(12).setPreferredWidth(110); // material
                table_fish.getColumnModel().getColumn(12).setMaxWidth(200);
            }

            get_queryLabIDs(sql, pst, rs, conn);
            get_statistics(sql);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }       
  */
    }//GEN-LAST:event_F_txt_result_idActionPerformed
    
    private void table_queryIDsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_queryIDsMouseClicked

        // right klick ==> save to file
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_queryIDs);
            popUpSave.show(table_queryIDs,evt.getX(),evt.getY());
            this.outTable = table_queryIDs;

        } else{
            if (btn_selPat.isSelected()){
                Connection conn = DBconnect.ConnecrDb();
                ResultSet rs = null;
                PreparedStatement pst = null;

                try {
                    int row = table_queryIDs.getSelectedRow();
                    //String Table_click = (table_queryIDs.getModel().getValueAt(row, 0).toString()); // values not correct anymore, if auto table rowsorter is used -->
                    String Table_click = (table_queryIDs.getValueAt(row, 0).toString());
                    click_result = (table_queryIDs.getValueAt(row, 1).toString());
                    click_lID = Table_click;
                    
                    // sql query array
                    String sql = "SELECT array_sub_id as ID, ma_nom, a.result_id, chr, arr_type as type, cnst, arr_call, size, loc_start, loc_end, cyto_regions AS cyto_regions, genes "
                            + "FROM arr_result a, main_result m "
                            + "WHERE a.result_id=m.result_id AND lab_id='" + Table_click + "' ";
                    if (A_rbtn_sort.isSelected()){
                        String order = (String) A_ComboBox_sort.getSelectedItem();
                        String asdes = F_txt_sort.getText();
                        if(A_ComboBox_sort.getSelectedItem().toString().equals("array_sub_id")
                            //|| A_ComboBox_sort.getSelectedItem().toString().equals("chr")  
                            //|| A_ComboBox_sort.getSelectedItem().toString().equals("")      // percent necessary?
                        ){   
                            sql = sql + " order by length(" + order +  "), " +order+ " " + asdes;
                        } else if (A_ComboBox_sort.getSelectedItem().toString().equals("chr")){
                            sql = sql + " order by (" + order + " *1) " + asdes;
                        } else {
                            sql = sql + " order by (" + order + ") " + asdes;
                        }
                    } 
                    
                    //txtArea_genes.setText(sql); // TEST
                    
                    // sql query fish
                    String sql2 = "SELECT fish_sub_id as ID, r.result_id, i_count, m_count, percent, region1, reg1_sig as sig1, region2, reg2_sig as sig2, fsn_sig, fish_chng, result, material "
                            + "FROM fish_result r, fish_probe p, main_result m "
                            + "WHERE r.probe_no=p.probe_no AND r.result_id=m.result_id AND lab_id='" + Table_click + "' ";
                    if (F_rbtn_sort.isSelected()) {
                        String order = (String) F_ComboBox_sort.getSelectedItem();
                        String asdes = F_txt_sort.getText();
                        if (F_ComboBox_sort.getSelectedItem().toString().equals("fish_sub_id")
                                || F_ComboBox_sort.getSelectedItem().toString().equals("i_count")
                                || F_ComboBox_sort.getSelectedItem().toString().equals("m_count")
                                || F_ComboBox_sort.getSelectedItem().toString().equals("percent") // percent necessary?
                                ) {
                            //sql2 = sql2 + " order by (" + order + " * 1) " + asdes;
                            sql2 = sql2 + " order by length(" + order +  "), " +order+ " " + asdes;
                        } else {
                            sql2 = sql2 + " order by (" + order + ") " + asdes;
                        }
                    }
                    //txtArea_genes.setText(sql2); // TEST
                    
                    // sql query cytogenetics
                    String sql3 = "SELECT i.klon_id as Klon,i.result_id, chr_cnt as Chr, mitos_cnt as Mitosen, cp, iscn as ISCN, material, stim "
                            + "FROM zg_iscn i, main_result m Where i.result_id=m.result_id AND lab_id='" + Table_click + "' ";
                    if (ZG_rbtn_sort.isSelected()) {
                        String order = (String) ZG_ComboBox_sort.getSelectedItem();
                        String asdes = ZG_txt_sort.getText();
                        if (ZG_ComboBox_sort.getSelectedItem().toString().equals("i.klon_id") 
                                //|| ZG_ComboBox_sort.getSelectedItem().toString().equals("")  
                                //|| ZG_ComboBox_sort.getSelectedItem().toString().equals("") 
                                ) {
                            //sql3 = sql3 + " order by (" + order + " * 1)" + asdes;
                            sql3 = sql3 + " order by length(" + order +  "), " +order+ " " + asdes;
                        } else {
                            sql3 = sql3 + " order by (" + order + ") " + asdes;
                        }
                    }
                    
                    // update table_array
                    pst = conn.prepareStatement(sql);
                    rs = pst.executeQuery();
                    if (!rs.isBeforeFirst()){
                        lbl_array_signal.setBackground(Color.getColor("6699FF"));
                    }else{
                        lbl_array_signal.setBackground(Color.ORANGE);
                    }
                    //if (rs.next()) {      // this skips first row!  
                    table_array.setModel(DbUtils.resultSetToTableModel(rs));
                    CustomSorter.table_customRowSort(table_array); 
                    // resize column width
                    jScrollPane1.setViewportView(table_array);
                    if (table_array.getColumnModel().getColumnCount() > 0) {
                        table_array.getColumnModel().getColumn(0).setPreferredWidth(65);    // 50
                        table_array.getColumnModel().getColumn(0).setMaxWidth(65);          // 50
                        table_array.getColumnModel().getColumn(1).setPreferredWidth(300);   // ma_nom
                        table_array.getColumnModel().getColumn(1).setMaxWidth(600);         // ma_nom

                        table_array.getColumnModel().getColumn(2).setPreferredWidth(60);
                        table_array.getColumnModel().getColumn(2).setMaxWidth(60);
                        table_array.getColumnModel().getColumn(3).setPreferredWidth(55); // chr
                        table_array.getColumnModel().getColumn(3).setMaxWidth(55);
                        table_array.getColumnModel().getColumn(4).setPreferredWidth(55); // type
                        table_array.getColumnModel().getColumn(4).setMaxWidth(100);
                        table_array.getColumnModel().getColumn(5).setPreferredWidth(60);
                        table_array.getColumnModel().getColumn(5).setMaxWidth(60);
                        table_array.getColumnModel().getColumn(6).setPreferredWidth(110);
                        table_array.getColumnModel().getColumn(6).setMaxWidth(200);
                        table_array.getColumnModel().getColumn(7).setPreferredWidth(100);
                        table_array.getColumnModel().getColumn(7).setMaxWidth(100);
                        table_array.getColumnModel().getColumn(8).setPreferredWidth(100);
                        table_array.getColumnModel().getColumn(8).setMaxWidth(100);
                        table_array.getColumnModel().getColumn(9).setPreferredWidth(100);
                        table_array.getColumnModel().getColumn(9).setMaxWidth(100);
                        table_array.getColumnModel().getColumn(10).setPreferredWidth(100);
                        table_array.getColumnModel().getColumn(10).setMaxWidth(200);
                    }
                    //} //END if(rs.next)
                    
                    // update table_fish
                    pst = conn.prepareStatement(sql2);
                    rs = pst.executeQuery();
                    if (!rs.isBeforeFirst()){
                        lbl_fish_signal.setBackground(Color.getColor("6699FF"));
                    }else{
                        lbl_fish_signal.setBackground(Color.ORANGE);
                    }
                    //if (rs.next()) {      // this skips first row!   
                    table_fish.setModel(DbUtils.resultSetToTableModel(rs));
                    CustomSorter.table_customRowSort(table_fish);
                    // resize column width
                    jScrollPane2.setViewportView(table_fish);
                    if (table_fish.getColumnModel().getColumnCount() > 0) {
                        table_fish.getColumnModel().getColumn(0).setPreferredWidth(65); //55
                        table_fish.getColumnModel().getColumn(0).setMaxWidth(65);       //55

                        table_fish.getColumnModel().getColumn(1).setPreferredWidth(60);
                        table_fish.getColumnModel().getColumn(1).setMaxWidth(60);

                        table_fish.getColumnModel().getColumn(2).setPreferredWidth(55);
                        table_fish.getColumnModel().getColumn(2).setMaxWidth(55);

                        table_fish.getColumnModel().getColumn(3).setPreferredWidth(60);
                        table_fish.getColumnModel().getColumn(3).setMaxWidth(60);

                        table_fish.getColumnModel().getColumn(4).setPreferredWidth(60);
                        table_fish.getColumnModel().getColumn(4).setMaxWidth(60);

                        table_fish.getColumnModel().getColumn(5).setPreferredWidth(100);
                        table_fish.getColumnModel().getColumn(5).setMaxWidth(300);
                        table_fish.getColumnModel().getColumn(6).setPreferredWidth(45);
                        table_fish.getColumnModel().getColumn(6).setMaxWidth(45);
                        table_fish.getColumnModel().getColumn(7).setPreferredWidth(100);
                        table_fish.getColumnModel().getColumn(7).setMaxWidth(300);
                        table_fish.getColumnModel().getColumn(8).setPreferredWidth(45);
                        table_fish.getColumnModel().getColumn(8).setMaxWidth(45);

                        table_fish.getColumnModel().getColumn(9).setPreferredWidth(60);
                        table_fish.getColumnModel().getColumn(9).setMaxWidth(60);

                        table_fish.getColumnModel().getColumn(10).setPreferredWidth(100);  // fish_chng
                        table_fish.getColumnModel().getColumn(10).setMaxWidth(500);

                        table_fish.getColumnModel().getColumn(12).setPreferredWidth(110); // material
                        table_fish.getColumnModel().getColumn(12).setMaxWidth(200);
                    }
                    
                    // update table_iscn
                    pst = conn.prepareStatement(sql3);
                    rs = pst.executeQuery();
                    if (!rs.isBeforeFirst()){
                        lbl_zg_signal.setBackground(Color.getColor("6699FF"));
                    }else{
                        lbl_zg_signal.setBackground(Color.ORANGE);
                    }
                    //if (rs.next()) {      // this skips first row!   
                    table_zg_iscn.setModel(DbUtils.resultSetToTableModel(rs));
                    CustomSorter.table_customRowSort(table_zg_iscn);

                    // resize column width
                    jScrollPane3.setViewportView(table_zg_iscn);
                    if (table_zg_iscn.getColumnModel().getColumnCount() > 0) {
                        table_zg_iscn.getColumnModel().getColumn(0).setPreferredWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(0).setMaxWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(1).setPreferredWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(1).setMaxWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(2).setPreferredWidth(45);
                        table_zg_iscn.getColumnModel().getColumn(2).setMaxWidth(45);
                        table_zg_iscn.getColumnModel().getColumn(3).setPreferredWidth(55);
                        table_zg_iscn.getColumnModel().getColumn(3).setMaxWidth(55);
                        table_zg_iscn.getColumnModel().getColumn(4).setPreferredWidth(50);
                        table_zg_iscn.getColumnModel().getColumn(4).setMaxWidth(50);
                        table_zg_iscn.getColumnModel().getColumn(5).setPreferredWidth(500);   // iscn
                        table_zg_iscn.getColumnModel().getColumn(5).setMaxWidth(800);         // iscn
                        table_zg_iscn.getColumnModel().getColumn(6).setPreferredWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(6).setMaxWidth(150);
                        table_zg_iscn.getColumnModel().getColumn(7).setPreferredWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(7).setMaxWidth(200);
                    }
                    
                    A_txt_array_sub_id.setText("");
                    A_txt_result_id.setText("");
                    A_txt_lab_id.setText("");
                    txt_fullLoc.setText("");
                    F_txt_fish_sub_id.setText("");
                    F_txt_result_id.setText("");
                    F_txt_lab_id.setText("");
                    Z_txt_klon_id.setText("");
                    Z_txt_result_id.setText("");
                    Z_txt_lab_id.setText("");

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                } finally {
                    try {
                       if (rs != null) { rs.close();}
                      if (pst != null) { pst.close();}
                      if (conn != null) { conn.close();}
                    } catch (Exception e) {
                    }
                }
            } else {
                clearBtnColors();
            }        
        }
    }//GEN-LAST:event_table_queryIDsMouseClicked

    private void popUpMenu_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_saveActionPerformed
        saveOnRC(evt);
        //this.dispose();
    }//GEN-LAST:event_popUpMenu_saveActionPerformed

    private void popUpMenu_selectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_selectAllActionPerformed
        JTable OT = this.outTable;
        OT.selectAll();
    }//GEN-LAST:event_popUpMenu_selectAllActionPerformed

    private void btn_selColActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_selColActionPerformed
        // column selection in table
        if (btn_selCol.isSelected()){
            table_queryIDs.setColumnSelectionAllowed(true);
            table_queryIDs.setRowSelectionAllowed(false);
            //table_queryIDs.setCellSelectionEnabled(false);
        } else {
            table_queryIDs.setColumnSelectionAllowed(false);
            table_queryIDs.setRowSelectionAllowed(true);
            //table_queryIDs.setCellSelectionEnabled(true);
        }
    }//GEN-LAST:event_btn_selColActionPerformed

    private void btn_TTTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TTTActionPerformed
        // TODO add your handling code here:
        if (btn_TTT.isSelected()){
            ToolTipManager.sharedInstance().setEnabled(true);
            // Get current delay ...
            //int initialDelay = ToolTipManager.sharedInstance().getInitialDelay();
            // Show tool tips immediately ...
            ToolTipManager.sharedInstance().setInitialDelay(0);
        } else {
            ToolTipManager.sharedInstance().setEnabled(false);
        }
    }//GEN-LAST:event_btn_TTTActionPerformed

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void table_queryIDsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_queryIDsKeyReleased
        // code from  table_queryIDsMouseClicked() wo. isRightClick(evt)
        
        if (btn_selPat.isSelected()){
                Connection conn = DBconnect.ConnecrDb();
                ResultSet rs = null;
                PreparedStatement pst = null;

                try {
                    int row = table_queryIDs.getSelectedRow();
                    //String Table_click = (table_queryIDs.getModel().getValueAt(row, 0).toString());     // values not correct anymore, if auto table rowsorter is used -->
                    String Table_click = (table_queryIDs.getValueAt(row, 0).toString());
                    click_result = (table_queryIDs.getValueAt(row, 1).toString());
                    click_lID = Table_click;

                    // sql query array
                    String sql = "SELECT array_sub_id as ID, ma_nom, a.result_id, chr, arr_type as type, cnst, arr_call, size, loc_start, loc_end, cyto_regions AS cyto_regions, genes "
                            + "FROM arr_result a, main_result m "
                            + "WHERE a.result_id=m.result_id AND lab_id='" + Table_click + "' ";
                    if (A_rbtn_sort.isSelected()){
                        String order = (String) A_ComboBox_sort.getSelectedItem();
                        String asdes = F_txt_sort.getText();
                        if(A_ComboBox_sort.getSelectedItem().toString().equals("array_sub_id")
                            //|| A_ComboBox_sort.getSelectedItem().toString().equals("chr")  
                            //|| A_ComboBox_sort.getSelectedItem().toString().equals("")      // percent necessary?
                        ){   
                            sql = sql + " order by length(" + order +  "), " +order+ " " + asdes;
                        } else if (A_ComboBox_sort.getSelectedItem().toString().equals("chr")){
                            sql = sql + " order by (" + order + " *1) " + asdes;
                        } else {
                            sql = sql + " order by (" + order + ") " + asdes;
                        }
                    } 
                    //txtArea_genes.setText(sql); // TEST
                    
                    // sql query fish
                    String sql2 = "SELECT fish_sub_id as ID, r.result_id, i_count, m_count, percent, region1, reg1_sig as sig1, region2, reg2_sig as sig2, fsn_sig, fish_chng, result, material "
                            + "FROM fish_result r, fish_probe p, main_result m "
                            + "WHERE r.probe_no=p.probe_no AND r.result_id=m.result_id AND lab_id='" + Table_click + "' ";
                    if (F_rbtn_sort.isSelected()) {
                        String order = (String) F_ComboBox_sort.getSelectedItem();
                        String asdes = F_txt_sort.getText();
                        if (F_ComboBox_sort.getSelectedItem().toString().equals("fish_sub_id")
                                || F_ComboBox_sort.getSelectedItem().toString().equals("i_count")
                                || F_ComboBox_sort.getSelectedItem().toString().equals("m_count")
                                || F_ComboBox_sort.getSelectedItem().toString().equals("percent") // percent necessary?
                                ) {
                            //sql2 = sql2 + " order by (" + order + " * 1) " + asdes;
                            sql2 = sql2 + " order by length(" + order +  "), " +order+ " " + asdes;
                        } else {
                            sql2 = sql2 + " order by (" + order + ") " + asdes;
                        }
                    }
                    //txtArea_genes.setText(sql2); // TEST
                     
                    // sql query cytogenetics
                    String sql3 = "SELECT i.klon_id as Klon,i.result_id, chr_cnt as Chr, mitos_cnt as Mitosen, cp, iscn as ISCN, material, stim "
                            + "FROM zg_iscn i, main_result m Where i.result_id=m.result_id AND lab_id='" + Table_click + "' ";
                    if (ZG_rbtn_sort.isSelected()) {
                        String order = (String) ZG_ComboBox_sort.getSelectedItem();
                        String asdes = ZG_txt_sort.getText();
                        if (ZG_ComboBox_sort.getSelectedItem().toString().equals("i.klon_id") 
                                //|| ZG_ComboBox_sort.getSelectedItem().toString().equals("")  
                                //|| ZG_ComboBox_sort.getSelectedItem().toString().equals("") 
                                ) {
                            //sql3 = sql3 + " order by (" + order + " * 1)" + asdes;
                            sql3 = sql3 + " order by length(" + order +  "), " +order+ " " + asdes;
                        } else {
                            sql3 = sql3 + " order by (" + order + ") " + asdes;
                        }
                    }
                    
                    // update table_array
                    pst = conn.prepareStatement(sql);
                    rs = pst.executeQuery();
                    if (!rs.isBeforeFirst()){
                        lbl_array_signal.setBackground(Color.getColor("6699FF"));
                    }else{
                        lbl_array_signal.setBackground(Color.ORANGE);
                    }
                    //if (rs.next()) {      // this skips first row!  
                    table_array.setModel(DbUtils.resultSetToTableModel(rs));
                    CustomSorter.table_customRowSort(table_array); 
                    // resize column width
                    jScrollPane1.setViewportView(table_array);
                    if (table_array.getColumnModel().getColumnCount() > 0) {
                        table_array.getColumnModel().getColumn(0).setPreferredWidth(65);    // 50
                        table_array.getColumnModel().getColumn(0).setMaxWidth(65);          // 50
                        table_array.getColumnModel().getColumn(1).setPreferredWidth(300);   // ma_nom
                        table_array.getColumnModel().getColumn(1).setMaxWidth(600);         // ma_nom

                        table_array.getColumnModel().getColumn(2).setPreferredWidth(60);
                        table_array.getColumnModel().getColumn(2).setMaxWidth(60);
                        table_array.getColumnModel().getColumn(3).setPreferredWidth(55); // chr
                        table_array.getColumnModel().getColumn(3).setMaxWidth(55);
                        table_array.getColumnModel().getColumn(4).setPreferredWidth(55); // type
                        table_array.getColumnModel().getColumn(4).setMaxWidth(100);
                        table_array.getColumnModel().getColumn(5).setPreferredWidth(60);
                        table_array.getColumnModel().getColumn(5).setMaxWidth(60);
                        table_array.getColumnModel().getColumn(6).setPreferredWidth(110);
                        table_array.getColumnModel().getColumn(6).setMaxWidth(200);
                        table_array.getColumnModel().getColumn(7).setPreferredWidth(100);
                        table_array.getColumnModel().getColumn(7).setMaxWidth(100);
                        table_array.getColumnModel().getColumn(8).setPreferredWidth(100);
                        table_array.getColumnModel().getColumn(8).setMaxWidth(100);
                        table_array.getColumnModel().getColumn(9).setPreferredWidth(100);
                        table_array.getColumnModel().getColumn(9).setMaxWidth(100);
                        table_array.getColumnModel().getColumn(10).setPreferredWidth(100);
                        table_array.getColumnModel().getColumn(10).setMaxWidth(200);
                    }
                    //} //END if(rs.next)
                                       
                    // update table_fish
                    pst = conn.prepareStatement(sql2);
                    rs = pst.executeQuery();
                    if (!rs.isBeforeFirst()){
                        lbl_fish_signal.setBackground(Color.getColor("6699FF"));
                    }else{
                        lbl_fish_signal.setBackground(Color.ORANGE);
                    }
                    //if (rs.next()) {      // this skips first row!   
                    table_fish.setModel(DbUtils.resultSetToTableModel(rs));
                    CustomSorter.table_customRowSort(table_fish);                    
                    // resize column width
                    jScrollPane2.setViewportView(table_fish);
                    if (table_fish.getColumnModel().getColumnCount() > 0) {
                        table_fish.getColumnModel().getColumn(0).setPreferredWidth(65); //55
                        table_fish.getColumnModel().getColumn(0).setMaxWidth(65);       //55

                        table_fish.getColumnModel().getColumn(1).setPreferredWidth(60);
                        table_fish.getColumnModel().getColumn(1).setMaxWidth(60);

                        table_fish.getColumnModel().getColumn(2).setPreferredWidth(55);
                        table_fish.getColumnModel().getColumn(2).setMaxWidth(55);

                        table_fish.getColumnModel().getColumn(3).setPreferredWidth(60);
                        table_fish.getColumnModel().getColumn(3).setMaxWidth(60);

                        table_fish.getColumnModel().getColumn(4).setPreferredWidth(60);
                        table_fish.getColumnModel().getColumn(4).setMaxWidth(60);

                        table_fish.getColumnModel().getColumn(5).setPreferredWidth(100);
                        table_fish.getColumnModel().getColumn(5).setMaxWidth(300);
                        table_fish.getColumnModel().getColumn(6).setPreferredWidth(45);
                        table_fish.getColumnModel().getColumn(6).setMaxWidth(45);
                        table_fish.getColumnModel().getColumn(7).setPreferredWidth(100);
                        table_fish.getColumnModel().getColumn(7).setMaxWidth(300);
                        table_fish.getColumnModel().getColumn(8).setPreferredWidth(45);
                        table_fish.getColumnModel().getColumn(8).setMaxWidth(45);

                        table_fish.getColumnModel().getColumn(9).setPreferredWidth(60);
                        table_fish.getColumnModel().getColumn(9).setMaxWidth(60);

                        table_fish.getColumnModel().getColumn(10).setPreferredWidth(100);  // fish_chng
                        table_fish.getColumnModel().getColumn(10).setMaxWidth(500);

                        table_fish.getColumnModel().getColumn(12).setPreferredWidth(110); // material
                        table_fish.getColumnModel().getColumn(12).setMaxWidth(200);
                    }
                    
                    // update table_iscn
                    pst = conn.prepareStatement(sql3);
                    rs = pst.executeQuery();
                    if (!rs.isBeforeFirst()){
                        lbl_zg_signal.setBackground(Color.getColor("6699FF"));
                    }else{
                        lbl_zg_signal.setBackground(Color.ORANGE);
                    }
                    //if (rs.next()) {      // this skips first row!   
                    table_zg_iscn.setModel(DbUtils.resultSetToTableModel(rs));
                    CustomSorter.table_customRowSort(table_zg_iscn);
                    // resize column width
                    jScrollPane3.setViewportView(table_zg_iscn);
                    if (table_zg_iscn.getColumnModel().getColumnCount() > 0) {
                        table_zg_iscn.getColumnModel().getColumn(0).setPreferredWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(0).setMaxWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(1).setPreferredWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(1).setMaxWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(2).setPreferredWidth(45);
                        table_zg_iscn.getColumnModel().getColumn(2).setMaxWidth(45);
                        table_zg_iscn.getColumnModel().getColumn(3).setPreferredWidth(55);
                        table_zg_iscn.getColumnModel().getColumn(3).setMaxWidth(55);
                        table_zg_iscn.getColumnModel().getColumn(4).setPreferredWidth(50);
                        table_zg_iscn.getColumnModel().getColumn(4).setMaxWidth(50);
                        table_zg_iscn.getColumnModel().getColumn(5).setPreferredWidth(500);   // iscn
                        table_zg_iscn.getColumnModel().getColumn(5).setMaxWidth(800);         // iscn
                        table_zg_iscn.getColumnModel().getColumn(6).setPreferredWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(6).setMaxWidth(150);
                        table_zg_iscn.getColumnModel().getColumn(7).setPreferredWidth(60);
                        table_zg_iscn.getColumnModel().getColumn(7).setMaxWidth(200);
                    }
                    
                    A_txt_array_sub_id.setText("");
                    A_txt_result_id.setText("");
                    A_txt_lab_id.setText("");
                    txt_fullLoc.setText("");
                    F_txt_fish_sub_id.setText("");
                    F_txt_result_id.setText("");
                    F_txt_lab_id.setText("");
                    Z_txt_klon_id.setText("");
                    Z_txt_result_id.setText("");
                    Z_txt_lab_id.setText("");

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                } finally {
                    try {
                       if (rs != null) { rs.close();}
                      if (pst != null) { pst.close();}
                      if (conn != null) { conn.close();}
                    } catch (Exception e) {
                    }
                }
            } else {
                clearBtnColors();
            }
    }//GEN-LAST:event_table_queryIDsKeyReleased

    private void table_zg_iscnKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_zg_iscnKeyReleased
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            int row = table_zg_iscn.getSelectedRow();
            //String Table_click = (table_zg_iscn.getModel().getValueAt(row, 0).toString());      // values not correct anymore, if auto table rowsorter is used -->
            String Table_click = (table_zg_iscn.getValueAt(row, 0).toString());
            click_result = (table_zg_iscn.getValueAt(row, 1).toString());
            click_lID = Table_click;

            String sql = "SELECT * FROM zg_iscn i, main_result m Where i.result_id=m.result_id AND i.klon_id='" + Table_click + "' ";

            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                String add1 = rs.getString("klon_id");
                Z_txt_klon_id.setText(add1);
                String add2 = rs.getString("result_id");
                Z_txt_result_id.setText(add2);
                String add3 = rs.getString("lab_id");
                Z_txt_lab_id.setText(add3);
   
                // IF interpretation window is open
                if (IntrprWindowIsOpen == true) {
                    updateIntrpr(add2);     // update text in Window "ResultWindow"
                }

                String sql2 = "SELECT r.result_id, r.zg_sub_id, r.klon_id, region, chr, chng FROM zg_result r, zg_list l, main_result m "
                        + "Where r.zyto_id=l.zyto_id AND r.result_id=m.result_id "
                        + "AND r.klon_id=?";

                pst = conn.prepareStatement(sql2);
                pst.setString(1, Z_txt_klon_id.getText());

                rs = pst.executeQuery();
                table_zg_result.setModel(DbUtils.resultSetToTableModel(rs));

                jScrollPane4.setViewportView(table_zg_result);
                if (table_zg_result.getColumnModel().getColumnCount() > 0) {
                    table_zg_result.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_zg_result.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_zg_result.getColumnModel().getColumn(1).setPreferredWidth(70);
                    table_zg_result.getColumnModel().getColumn(1).setMaxWidth(70);
                    table_zg_result.getColumnModel().getColumn(2).setPreferredWidth(60);
                    table_zg_result.getColumnModel().getColumn(2).setMaxWidth(60);
                    table_zg_result.getColumnModel().getColumn(3).setPreferredWidth(70);
                    table_zg_result.getColumnModel().getColumn(3).setMaxWidth(70);
                    table_zg_result.getColumnModel().getColumn(4).setPreferredWidth(50);
                    table_zg_result.getColumnModel().getColumn(4).setMaxWidth(70);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }       
    }//GEN-LAST:event_table_zg_iscnKeyReleased

    private void btn_EmergencyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_EmergencyActionPerformed
        new SearchResult().setVisible(true);
        this.dispose();
        //F_txt_resID.resize(50,28);
    }//GEN-LAST:event_btn_EmergencyActionPerformed

    private void popUpMenu_moveTblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_moveTblActionPerformed
        JTable OT = this.outTable;
        source = OT.getAccessibleContext().getAccessibleDescription();
        tableMoving = OT.getAccessibleContext().getAccessibleName();
        moveTableModel = (DefaultTableModel) OT.getModel();
        new FreeTable().setVisible(true);        
    }//GEN-LAST:event_popUpMenu_moveTblActionPerformed

    private void jMenuItem2_InfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2_InfoActionPerformed
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_med.png"));
        JOptionPane.showMessageDialog(rootPane, "LInkedResultsAnalysis \nDB-request Tool\nVersion:   1.0.0", "Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem2_InfoActionPerformed

    private void jMenuItem1_HowToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1_HowToActionPerformed
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/Monsters-Snail-icon.png"));
        JOptionPane.showMessageDialog(rootPane, "... ummmmmm \n... errrrrr \n... pls ask again later", "apparently no useful Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem1_HowToActionPerformed

    private void jMenuItem1_openModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1_openModelActionPerformed
        File file = new File("model.pdf");
        //File file = new File("C:\\Users\\gerda.modarres\\Desktop\\pat_DB\\stdpat_db_model.pdf");
        try {
            if (OSDetector.isWindows()){
                //JOptionPane.showMessageDialog(null, OSDetector.isWindows());
                Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + "model.pdf");
                //Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + "C:\\Users\\gerda.modarres\\Desktop\\pat_DB\\stdpat_db_model.pdf");
            }else{
                Desktop desktop = Desktop.getDesktop();
                desktop.open(file);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }   
    }//GEN-LAST:event_jMenuItem1_openModelActionPerformed

    private void Z_txt_lab_idActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Z_txt_lab_idActionPerformed
        // TODO
        update_table_zg_iscn();
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {

            String lab_id = Z_txt_lab_id.getText();
            Z_txt_klon_id.setText("");
            String sql = "SELECT i.klon_id as Klon,i.result_id, chr_cnt as Chr, mitos_cnt as Mitosen, cp, iscn as ISCN, material, stim FROM zg_iscn i, main_result m Where i.result_id=m.result_id AND lab_id='" + lab_id + "'";

            //txtArea_sql.setText(sql);
            get_ids(sql, pst, rs, conn); // needed for get_statistics() to count patients affected
            get_statistics(sql);
            get_queryLabIDs(sql, pst, rs, conn);
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_Z_txt_lab_idActionPerformed

    private void popUpMenu_intrprWinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_intrprWinActionPerformed
        // open window and show corresponding result-text       
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        
        JTable OT = this.outTable;
        source = OT.getAccessibleContext().getAccessibleDescription();
        try {
            String result_id = "";
            switch (source) {
                case "array result":
                    result_id = A_txt_result_id.getText();
                    break;
                case "fish result":
                    result_id = F_txt_result_id.getText();
                    break;
                case "cytogenetics result":
                    result_id = Z_txt_result_id.getText();
                    break;
                default:
                    break;
            }

            String sql = "select * from main_result where result_id="+ result_id;
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            
            String arr="";
            String fish="";
            String zg="";
            
            if (rs.next()) {
                arr = rs.getString("ar_intrpr");
                fish = rs.getString("fish_intrpr");
                zg = rs.getString("zg_intrpr");
            }

            String text = "";
            switch (source) {
                case "array result":
                    text = arr;
                    break;
                case "fish result":
                    text = fish;
                    break;
                case "cytogenetics result":
                    text = zg;
                    break;
               default:
                    break;
            }
            
            resultMoving = text;
        
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

        new ResultWindow().setVisible(true);
        IntrprWindowIsOpen = true;               
    }//GEN-LAST:event_popUpMenu_intrprWinActionPerformed

    private void table_fishKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_fishKeyReleased
        // same code as in table_fishMouseClicked (without right-klick detection)
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            int row = table_fish.getSelectedRow();
            //String Table_click = (table_fish.getModel().getValueAt(row, 0).toString());       // values not correct anymore, if auto table rowsorter is used -->
            String Table_click = (table_fish.getValueAt(row, 0).toString());
            click_result = (table_fish.getValueAt(row, 1).toString());
            click_lID = Table_click;

            String sql = "SELECT * FROM fish_result r, fish_probe p, main_result m WHERE r.probe_no=p.probe_no AND m.result_id=r.result_id AND fish_sub_id='" + Table_click + "' ";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                String add1 = rs.getString("fish_sub_id");
                F_txt_fish_sub_id.setText(add1);
                String add2 = rs.getString("result_id");
                F_txt_result_id.setText(add2);
                String add3 = rs.getString("lab_id");
                F_txt_lab_id.setText(add3);

                String add4 = rs.getString("p.probe_no");
                F_txt_probe_no.setText(add4);
                String add5 = rs.getString("p.loc");
                F_txtArea_loc.setText(add5);

                // IF interpretation window is open
                if (IntrprWindowIsOpen == true) {
                    updateIntrpr(add2);     // update text in Window "ResultWindow"
                }

                //txtArea_sql.setText(sql);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_table_fishKeyReleased

    private void btn_loadQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_loadQueryActionPerformed
        String dp = this.defaultPath;
        String in_fileString = null;
        Ini ini;
        try {
            ini = new Ini(new File(personalConfig));        //toggle

            JFileChooser fileChooser = new JFileChooser(dp);
            if (fileChooser.showOpenDialog(jPanel1) == JFileChooser.APPROVE_OPTION) {
                File in_file = fileChooser.getSelectedFile();
                in_fileString = in_file.toString();
            }

            ini = new Ini(new File(in_fileString));

            String method = ini.get("frame", "frame");
            if (method.equals("SearchResult")) {

                // info_top
                boolean btn_onlyPat = Boolean.parseBoolean(ini.get("btn", "btn1"));
                rbtn_onlyPat.setSelected(btn_onlyPat);
                Integer combo_projPat = Integer.parseInt(ini.get("combo", "combo1"));
                ComboBox_projPat.setSelectedIndex(combo_projPat);
                boolean btn_onlyPat1 = Boolean.parseBoolean(ini.get("btn", "btn2"));
                rbtn_onlyPat1.setSelected(btn_onlyPat1);
                Integer combo_stdyPat = Integer.parseInt(ini.get("combo", "combo2"));
                ComboBox_stdyPat.setSelectedIndex(combo_stdyPat);
                boolean btn_SB = Boolean.parseBoolean(ini.get("btn", "btn3"));
                rbtn_SB.setSelected(btn_SB);
                if (btn_SB == true){
                    SB_resultIDs = ini.get("IDs","SB_resultIDs"); //TEST
                }
                boolean btn_ST = Boolean.parseBoolean(ini.get("btn", "btn4"));
                rbtn_ST.setSelected(btn_ST);
                if (btn_ST == true){
                    ST_resultIDs = ini.get("IDs","ST_resultIDs"); //TEST
                }
                boolean btn_AQ = Boolean.parseBoolean(ini.get("btn", "btn5"));
                rbtn_ArrQuery.setSelected(btn_AQ);
                if (btn_AQ == true){
                    AQ_resultIDs = ini.get("IDs","AQ_resultIDs"); //TEST
                }

                //tab_array
                boolean A_btn_NOT = Boolean.parseBoolean(ini.get("btn", "btn6"));
                A_rbtn_NOT.setSelected(A_btn_NOT);
                String A_ANDOR = ini.get("tabArray", "txt1");
                A_txt_ANDOR.setText(A_ANDOR);
                String A_tx1_genes1 = ini.get("tabArray", "txt2");
                A_txt1_genes1.setText(A_tx1_genes1);
                String A_tx1_genes2 = ini.get("tabArray", "txt3");
                A_txt1_genes2.setText(A_tx1_genes2);
                String A_tx2_genes1 = ini.get("tabArray", "txt4");
                A_txt2_genes1.setText(A_tx2_genes1);
                String A_tx2_genes2 = ini.get("tabArray", "txt5");
                A_txt2_genes2.setText(A_tx2_genes2);
                String A_tx3_genes1 = ini.get("tabArray", "txt6");
                A_txt3_genes1.setText(A_tx3_genes1);
                String A_tx3_genes2 = ini.get("tabArray", "txt7");
                A_txt3_genes2.setText(A_tx3_genes2);
                String A_tx4_genes1 = ini.get("tabArray", "txt8");
                A_txt4_genes1.setText(A_tx4_genes1);
                String A_tx4_genes2 = ini.get("tabArray", "txt9");
                A_txt4_genes2.setText(A_tx4_genes2);
                String A_tx5_genes1 = ini.get("tabArray", "txt10");
                A_txt5_genes1.setText(A_tx5_genes1);
                String A_tx5_genes2 = ini.get("tabArray", "txt11");
                A_txt5_genes2.setText(A_tx5_genes2);
                String A_tx_Creg = ini.get("tabArray", "txt12");
                A_txt_Creg.setText(A_tx_Creg);
                String A_tx_Creg_1 = ini.get("tabArray", "txt13");
                A_txt_Creg_1.setText(A_tx_Creg_1);
                String A_tx_call = ini.get("tabArray", "txt15");
                A_txt_call.setText(A_tx_call);
                String A_tx_call_1 = ini.get("tabArray", "txt16");
                A_txt_call_1.setText(A_tx_call_1);
                String A_tx_chr = ini.get("tabArray", "txt17");
                A_txt_chr.setText(A_tx_chr);
                String A_tx_chr_1 = ini.get("tabArray", "txt18");
                A_txt_chr_1.setText(A_tx_chr_1);
                String A_tx_cnst = ini.get("tabArray", "txt19");
                A_txt_cnst.setText(A_tx_cnst);
                String A_tx_cnst_1 = ini.get("tabArray", "txt20");
                A_txt_cnst_1.setText(A_tx_cnst_1);
                String A_tx_locEnd = ini.get("tabArray", "txt22");
                A_txt_locEnd.setText(A_tx_locEnd);
                String A_tx_locEnd_1 = ini.get("tabArray", "txt23");
                A_txt_locEnd_1.setText(A_tx_locEnd_1);
                String A_tx_locStart = ini.get("tabArray", "txt24");
                A_txt_locStart.setText(A_tx_locStart);
                String A_tx_locStart_1 = ini.get("tabArray", "txt25");
                A_txt_locStart_1.setText(A_tx_locStart_1);
                String A_tx_nom = ini.get("tabArray", "txt26");
                A_txt_nom.setText(A_tx_nom);
                String A_tx_nom_1 = ini.get("tabArray", "txt27");
                A_txt_nom_1.setText(A_tx_nom_1);
                String A_tx_resID = ini.get("tabArray", "txt28");
                A_txt_resID.setText(A_tx_resID);
                String A_tx_resID_1 = ini.get("tabArray", "txt29");
                A_txt_resID_1.setText(A_tx_resID_1);
                String A_tx_size = ini.get("tabArray", "txt31");
                A_txt_size.setText(A_tx_size);
                String A_tx_size_1 = ini.get("tabArray", "txt32");
                A_txt_size_1.setText(A_tx_size_1);
                String A_tx_type = ini.get("tabArray", "txt34");
                A_txt_type.setText(A_tx_type);
                String A_tx_type_1 = ini.get("tabArray", "txt35");
                A_txt_type_1.setText(A_tx_type_1);
                
                // tab FISH
                boolean F_btn_NOT = Boolean.parseBoolean(ini.get("btn", "btn7"));
                F_rbtn_NOT.setSelected(F_btn_NOT);
                String F_ANDOR = ini.get("tabFISH", "txt1");
                F_txt_ANDOR.setText(F_ANDOR);

                String F_tx_fchng = ini.get("tabFISH", "txt2");
                F_txt_fchng.setText(F_tx_fchng);
                String F_tx_fchng_1 = ini.get("tabFISH", "txt3");
                F_txt_fchng_1.setText(F_tx_fchng_1);
                String F_tx_fish_sub_id = ini.get("tabFISH", "txt4");
                F_txt_fish_sub_id.setText(F_tx_fish_sub_id);
                String F_tx_fsn = ini.get("tabFISH", "txt5");
                F_txt_fsn.setText(F_tx_fsn);
                String F_tx_fsn_1 = ini.get("tabFISH", "txt6");
                F_txt_fsn_1.setText(F_tx_fsn_1);
                String F_tx_kerne = ini.get("tabFISH", "txt7");
                F_txt_kerne.setText(F_tx_kerne);
                String F_tx_kerne_1 = ini.get("tabFISH", "txt8");
                F_txt_kerne_1.setText(F_tx_kerne_1);
                String F_tx_lab_id = ini.get("tabFISH", "txt9");
                F_txt_lab_id.setText(F_tx_lab_id);
                String F_tx_material = ini.get("tabFISH", "txt10");
                F_txt_material.setText(F_tx_material);
                String F_tx_material_1 = ini.get("tabFISH", "txt11");
                F_txt_material_1.setText(F_tx_material_1);
                String F_tx_mitos = ini.get("tabFISH", "tx12");
                F_txt_mitos.setText(F_tx_mitos);
                String F_tx_mitos_1 = ini.get("tabFISH", "txt13");
                F_txt_mitos_1.setText(F_tx_mitos_1);
                String F_tx_percent = ini.get("tabFISH", "txt14");
                F_txt_percent.setText(F_tx_percent);
                String F_tx_percent_1 = ini.get("tabFISH", "txt15");
                F_txt_percent_1.setText(F_tx_percent_1);
                String F_tx_reg1 = ini.get("tabFISH", "txt16");
                F_txt_reg1.setText(F_tx_reg1);
                String F_tx_reg1_1 = ini.get("tabFISH", "txt17");
                F_txt_reg1_1.setText(F_tx_reg1_1);
                String F_tx_reg2 = ini.get("tabFISH", "txt18");
                F_txt_reg2.setText(F_tx_reg2);
                String F_tx_reg2_1 = ini.get("tabFISH", "txt19");
                F_txt_reg2_1.setText(F_tx_reg2_1);
                String F_tx_resID = ini.get("tabFISH", "txt20");
                F_txt_resID.setText(F_tx_resID);
                String F_tx_resID_1 = ini.get("tabFISH", "txt21");
                F_txt_resID_1.setText(F_tx_resID_1);
                String F_tx_result = ini.get("tabFISH", "txt22");
                F_txt_result.setText(F_tx_result);
                String F_tx_result_1 = ini.get("tabFISH", "txt23");
                F_txt_result_1.setText(F_tx_result_1);
                String F_tx_sig1 = ini.get("tabFISH", "txt24");
                F_txt_sig1.setText(F_tx_sig1);
                String F_tx_sig1_1 = ini.get("tabFISH", "txt25");
                F_txt_sig1_1.setText(F_tx_sig1_1);
                String F_tx_sig2 = ini.get("tabFISH", "txt26");
                F_txt_sig2.setText(F_tx_sig2);
                String F_tx_sig2_1 = ini.get("tabFISH", "txt27");
                F_txt_sig2_1.setText(F_tx_sig2_1);

                // tab cytogenetics
                boolean ZG_btn_NOT = Boolean.parseBoolean(ini.get("btn", "btn8"));
                ZG_rbtn_NOT.setSelected(ZG_btn_NOT);
                boolean btn_ZGdetailResult = Boolean.parseBoolean(ini.get("btn", "btn9"));
                rbtn_ZGdetailResult.setSelected(btn_ZGdetailResult);

                String ZG_ANDOR = ini.get("tabZG", "txt1");
                ZG_txt_ANDOR.setText(ZG_ANDOR);
                String ZI_tx_chr = ini.get("tabZG", "txt2");
                ZI_txt_chr.setText(ZI_tx_chr);
                String ZI_tx_chr_1 = ini.get("tabZG", "txt3");
                ZI_txt_chr_1.setText(ZI_tx_chr_1);
                String ZI_tx_cp = ini.get("tabZG", "txt4");
                ZI_txt_cp.setText(ZI_tx_cp);
                String ZI_tx_cp_1 = ini.get("tabZG", "txt5");
                ZI_txt_cp_1.setText(ZI_tx_cp_1);

                String ZI_tx_iscn = ini.get("tabZG", "txt6");
                ZI_txt_iscn.setText(ZI_tx_iscn);
                String ZI_tx_iscn_1 = ini.get("tabZG", "txt7");
                ZI_txt_iscn_1.setText(ZI_tx_iscn_1);
                String ZI_tx_mat = ini.get("tabZG", "txt8");
                ZI_txt_mat.setText(ZI_tx_mat);
                String ZI_tx_mat_1 = ini.get("tabZG", "txt9");
                ZI_txt_mat_1.setText(ZI_tx_mat_1);
                String ZI_tx_mitos = ini.get("tabZG", "txt10");
                ZI_txt_mitos.setText(ZI_tx_mitos);
                String ZI_tx_mitos_1 = ini.get("tabZG", "txt11");
                ZI_txt_mitos_1.setText(ZI_tx_mitos_1);
                String ZI_tx_resId = ini.get("tabZG", "txt12");
                ZI_txt_resId.setText(ZI_tx_resId);
                String ZI_tx_resId_1 = ini.get("tabZG", "txt13");
                ZI_txt_resId_1.setText(ZI_tx_resId_1);
                String ZI_tx_stim = ini.get("tabZG", "txt14");
                ZI_txt_stim.setText(ZI_tx_stim);
                String ZI_tx_stim_1 = ini.get("tabZG", "txt15");
                ZI_txt_stim_1.setText(ZI_tx_stim_1);
                String ZR_tx_chng = ini.get("tabZG", "txt16");
                ZR_txt_chng.setText(ZR_tx_chng);
                String ZR_tx_chng_1 = ini.get("tabZG", "txt17");
                ZR_txt_chng_1.setText(ZR_tx_chng_1);
                String ZR_tx_chr = ini.get("tabZG", "txt18");
                ZR_txt_chr.setText(ZR_tx_chr);
                String ZR_tx_chr_1 = ini.get("tabZG", "txt19");
                ZR_txt_chr_1.setText(ZR_tx_chr_1);
                String ZR_tx_klonID = ini.get("tabZG", "txt20");
                ZR_txt_klonID.setText(ZR_tx_klonID);
                String ZR_tx_klonID_1 = ini.get("tabZG", "txt21");
                ZR_txt_klonID_1.setText(ZR_tx_klonID_1);
                String ZR_tx_region = ini.get("tabZG", "txt22");
                ZR_txt_region.setText(ZR_tx_region);
                String ZR_tx_region_1 = ini.get("tabZG", "txt23");
                ZR_txt_region_1.setText(ZR_tx_region_1);

            } else {
                JOptionPane.showMessageDialog(null, "You are trying to load a wrong frame format!");
            }
        } catch (IOException ex) {
            Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_loadQueryActionPerformed

    private void btn_saveQueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_saveQueryActionPerformed
        String dp = this.defaultPath;    //JOptionPane.showMessageDialog(null, dp);  //TEST
        String out_fileString = null;
        Ini ini;

        try {
            JFileChooser fileChooser = new JFileChooser(dp);
            if (fileChooser.showSaveDialog(jPanel1) == JFileChooser.APPROVE_OPTION) {
                File out_file = fileChooser.getSelectedFile();
                out_fileString = out_file.toString();
                out_file.createNewFile();
            }
            //JOptionPane.showMessageDialog(null, out_fileString);  //TEST
            ini = new Ini(new File(out_fileString));

            // get info from which frame te data comes from
            String method = this.getAccessibleContext().getAccessibleName();
            ini.put("frame", "frame", method);

            // get data from fields, comboboxes & buttons            
            // info_top
            boolean btn_onlyPat = rbtn_onlyPat.isSelected();
            ini.put("btn", "btn1", btn_onlyPat);
            Integer combo_projPat = ComboBox_projPat.getSelectedIndex();
            ini.put("combo", "combo1", combo_projPat);           
            boolean btn_onlyPat1 = rbtn_onlyPat1.isSelected();
            ini.put("btn", "btn2", btn_onlyPat1);
            Integer combo_stdyPat = ComboBox_stdyPat.getSelectedIndex();
            ini.put("combo", "combo2", combo_stdyPat);
            
            boolean btn_SB = rbtn_SB.isSelected();
            ini.put("btn", "btn3", btn_SB);
            if (rbtn_SB.isSelected()){
                String SB_IDs = SB_resultIDs;
                ini.put("IDs","SB_resultIDs",SB_IDs);
            }
            boolean btn_ST = rbtn_ST.isSelected();
            ini.put("btn", "btn4", btn_ST);
            if (rbtn_ST.isSelected()){
                String ST_IDs = ST_resultIDs;
                ini.put("IDs","SB_resultIDs",ST_IDs);
            }
            boolean btn_AQ = rbtn_ArrQuery.isSelected();
            ini.put("btn", "btn5", btn_AQ); 
            if (rbtn_ArrQuery.isSelected()){
                String AQ_IDs = AQ_resultIDs;
                ini.put("IDs","SB_resultIDs",AQ_IDs);
            }

            //tab_array
            boolean A_btn_NOT = A_rbtn_NOT.isSelected();
            ini.put("btn", "btn_6", A_btn_NOT); 
            String A_ANDOR = A_txt_ANDOR.getText();
            ini.put("tabArray", "txt1", A_ANDOR);
            String A_tx1_genes1 = A_txt1_genes1.getText();
            ini.put("tabArray", "txt2", A_tx1_genes1);
            String A_tx1_genes2 = A_txt1_genes2.getText();
            ini.put("tabArray", "txt3", A_tx1_genes2);
            String A_tx2_genes1 = A_txt2_genes1.getText();
            ini.put("tabArray", "txt4", A_tx2_genes1);
            String A_tx2_genes2 = A_txt2_genes2.getText();
            ini.put("tabArray", "txt5", A_tx2_genes2);
            String A_tx3_genes1 = A_txt3_genes1.getText();
            ini.put("tabArray", "txt6", A_tx3_genes1);
            String A_tx3_genes2 = A_txt3_genes2.getText();
            ini.put("tabArray", "txt7", A_tx3_genes2);
            String A_tx4_genes1 = A_txt4_genes1.getText();
            ini.put("tabArray", "txt8", A_tx4_genes1);
            String A_tx4_genes2 = A_txt4_genes2.getText();
            ini.put("tabArray", "txt9", A_tx4_genes2);
            String A_tx5_genes1 = A_txt5_genes1.getText();
            ini.put("tabArray", "txt10", A_tx5_genes1);
            String A_tx5_genes2 = A_txt5_genes2.getText();
            ini.put("tabArray", "txt11", A_tx5_genes2);
            String A_tx_Creg = A_txt_Creg.getText();
            ini.put("tabArray", "txt12", A_tx_Creg);
            String A_tx_Creg_1 = A_txt_Creg_1.getText();
            ini.put("tabArray", "txt13", A_tx_Creg_1);
            String A_tx_call = A_txt_call.getText();
            ini.put("tabArray", "txt15", A_tx_call);
            String A_tx_call_1 = A_txt_call_1.getText();
            ini.put("tabArray", "txt16", A_tx_call_1);
            String A_tx_chr = A_txt_chr.getText();
            ini.put("tabArray", "txt17", A_tx_chr);
            String A_tx_chr_1 = A_txt_chr_1.getText();
            ini.put("tabArray", "txt18", A_tx_chr_1);
            String A_tx_cnst = A_txt_cnst.getText();
            ini.put("tabArray", "txt19", A_tx_cnst);
            String A_tx_cnst_1 = A_txt_cnst_1.getText();
            ini.put("tabArray", "txt20", A_tx_cnst_1);
            String A_tx_locEnd = A_txt_locEnd.getText();
            ini.put("tabArray", "txt22", A_tx_locEnd);
            String A_tx_locEnd_1 = A_txt_locEnd_1.getText();
            ini.put("tabArray", "txt23", A_tx_locEnd_1);
            String A_tx_locStart = A_txt_locStart.getText();
            ini.put("tabArray", "txt24", A_tx_locStart);
            String A_tx_locStart_1 = A_txt_locStart_1.getText();
            ini.put("tabArray", "txt25", A_tx_locStart_1);
            String A_tx_nom = A_txt_nom.getText();
            ini.put("tabArray", "txt26", A_tx_nom);
            String A_tx_nom_1 = A_txt_nom_1.getText();
            ini.put("tabArray", "txt27", A_tx_nom_1);
            String A_tx_resID = A_txt_resID.getText();
            ini.put("tabArray", "txt28", A_tx_resID);
            String A_tx_resID_1 = A_txt_resID_1.getText();
            ini.put("tabArray", "txt29", A_tx_resID_1);
            String A_tx_size = A_txt_size.getText();
            ini.put("tabArray", "txt31", A_tx_size);
            String A_tx_size_1 = A_txt_size_1.getText();
            ini.put("tabArray", "txt32", A_tx_size_1);
            String A_tx_type = A_txt_type.getText();
            ini.put("tabArray", "txt34", A_tx_type);
            String A_tx_type_1 = A_txt_type_1.getText();
            ini.put("tabArray", "txt35", A_tx_type_1);
            
            // tab FISH
            boolean F_btn_NOT = F_rbtn_NOT.isSelected();
            ini.put("btn", "btn_7", F_btn_NOT);
            String F_ANDOR = F_txt_ANDOR.getText();
            ini.put("tabFISH", "txt1", F_ANDOR);
            String F_tx_fchng = F_txt_fchng.getText();
            ini.put("tabFISH", "txt2", F_tx_fchng);
            String F_tx_fchng_1 = F_txt_fchng_1.getText();
            ini.put("tabFISH", "txt3", F_tx_fchng_1);
            String F_tx_fish_sub_id = F_txt_fish_sub_id.getText();
            ini.put("tabFISH", "txt4", F_tx_fish_sub_id);
            String F_tx_fsn = F_txt_fsn.getText();
            ini.put("tabFISH", "txt5", F_tx_fsn);
            String F_tx_fsn_1 = F_txt_fsn_1.getText();
            ini.put("tabFISH", "txt6", F_tx_fsn_1);
            String F_tx_kerne = F_txt_kerne.getText();
            ini.put("tabFISH", "txt7", F_tx_kerne);
            String F_tx_kerne_1 = F_txt_kerne_1.getText();
            ini.put("tabFISH", "txt8", F_tx_kerne_1);
            String F_tx_lab_id = F_txt_lab_id.getText();
            ini.put("tabFISH", "txt9", F_tx_lab_id);
            String F_tx_material = F_txt_material.getText();
            ini.put("tabFISH", "txt10", F_tx_material);
            String F_tx_material_1 = F_txt_material_1.getText();
            ini.put("tabFISH", "txt11", F_tx_material_1);
            String F_tx_mitos = F_txt_mitos.getText();
            ini.put("tabFISH", "tx12", F_tx_mitos);
            String F_tx_mitos_1 = F_txt_mitos_1.getText();
            ini.put("tabFISH", "txt13", F_tx_mitos_1);
            String F_tx_percent = F_txt_percent.getText();
            ini.put("tabFISH", "txt14", F_tx_percent);
            String F_tx_percent_1 = F_txt_percent_1.getText();
            ini.put("tabFISH", "txt15", F_tx_percent_1);
            String F_tx_reg1 = F_txt_reg1.getText();
            ini.put("tabFISH", "txt16", F_tx_reg1);
            String F_tx_reg1_1 = F_txt_reg1_1.getText();
            ini.put("tabFISH", "txt17", F_tx_reg1_1);
            String F_tx_reg2 = F_txt_reg2.getText();
            ini.put("tabFISH", "txt18", F_tx_reg2);
            String F_tx_reg2_1 = F_txt_reg2_1.getText();
            ini.put("tabFISH", "txt19", F_tx_reg2_1);
            String F_tx_resID = F_txt_resID.getText();
            ini.put("tabFISH", "txt20", F_tx_resID);
            String F_tx_resID_1 = F_txt_resID_1.getText();
            ini.put("tabFISH", "txt21", F_tx_resID_1);
            String F_tx_result = F_txt_result.getText();
            ini.put("tabFISH", "txt22", F_tx_result);
            String F_tx_result_1 = F_txt_result_1.getText();
            ini.put("tabFISH", "txt23", F_tx_result_1);
            String F_tx_sig1 = F_txt_sig1.getText();
            ini.put("tabFISH", "txt24", F_tx_sig1);
            String F_tx_sig1_1 = F_txt_sig1_1.getText();
            ini.put("tabFISH", "txt25", F_tx_sig1_1);
            String F_tx_sig2 = F_txt_sig2.getText();
            ini.put("tabFISH", "txt26", F_tx_sig2);
            String F_tx_sig2_1 = F_txt_sig2_1.getText();
            ini.put("tabFISH", "txt27", F_tx_sig2_1);

            // tab cytogenetics
            boolean ZG_btn_NOT = ZG_rbtn_NOT.isSelected();
            ini.put("btn", "btn_8", ZG_btn_NOT);
            boolean btn_ZGdetailResult = rbtn_ZGdetailResult.isSelected();
            ini.put("btn", "btn_9", btn_ZGdetailResult);

            String ZG_ANDOR = ZG_txt_ANDOR.getText();
            ini.put("tabZG", "txt1", ZG_ANDOR);
            String ZI_tx_chr = ZI_txt_chr.getText();
            ini.put("tabZG", "txt2",ZI_tx_chr);
            String ZI_tx_chr_1 = ZI_txt_chr_1.getText();
            ini.put("tabZG", "txt3",ZI_tx_chr_1);
            String ZI_tx_cp = ZI_txt_cp.getText();
            ini.put("tabZG", "txt4",ZI_tx_cp);
            String ZI_tx_cp_1 = ZI_txt_cp_1.getText();
            ini.put("tabZG", "txt5",ZI_tx_cp_1);
            String ZI_tx_iscn = ZI_txt_iscn.getText();
            ini.put("tabZG", "txt6",ZI_tx_iscn);
            String ZI_tx_iscn_1 = ZI_txt_iscn_1.getText();
            ini.put("tabZG", "txt7",ZI_tx_iscn_1);
            String ZI_tx_mat = ZI_txt_mat.getText();
            ini.put("tabZG", "txt8",ZI_tx_mat);
            String ZI_tx_mat_1 = ZI_txt_mat_1.getText();
            ini.put("tabZG", "txt9",ZI_tx_mat_1);
            String ZI_tx_mitos = ZI_txt_mitos.getText();
            ini.put("tabZG", "txt10",ZI_tx_mitos);
            String ZI_tx_mitos_1 = ZI_txt_mitos_1.getText();
            ini.put("tabZG", "txt11",ZI_tx_mitos_1);
            String ZI_tx_resId = ZI_txt_resId.getText();
            ini.put("tabZG", "txt12",ZI_tx_resId);
            String ZI_tx_resId_1 = ZI_txt_resId_1.getText();
            ini.put("tabZG", "txt13",ZI_tx_resId_1);
            String ZI_tx_stim = ZI_txt_stim.getText();
            ini.put("tabZG", "txt14",ZI_tx_stim);
            String ZI_tx_stim_1 = ZI_txt_stim_1.getText();
            ini.put("tabZG", "txt15",ZI_tx_stim_1);
            String ZR_tx_chng = ZR_txt_chng.getText();
            ini.put("tabZG", "txt16",ZR_tx_chng);
            String ZR_tx_chng_1 = ZR_txt_chng_1.getText();
            ini.put("tabZG", "txt17",ZR_tx_chng_1);
            String ZR_tx_chr = ZR_txt_chr.getText();
            ini.put("tabZG", "txt18",ZR_tx_chr);
            String ZR_tx_chr_1 = ZR_txt_chr_1.getText();
            ini.put("tabZG", "txt19",ZR_tx_chr_1);
            String ZR_tx_klonID = ZR_txt_klonID.getText();
            ini.put("tabZG", "txt20",ZR_tx_klonID);
            String ZR_tx_klonID_1 = ZR_txt_klonID_1.getText();
            ini.put("tabZG", "txt21",ZR_tx_klonID_1);
            String ZR_tx_region = ZR_txt_region.getText();
            ini.put("tabZG", "txt22",ZR_tx_region);
            String ZR_tx_region_1 = ZR_txt_region_1.getText();
            ini.put("tabZG", "txt23",ZR_tx_region_1);

            ini.store();
        } catch (IOException ex) {
            Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btn_saveQueryActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */      
                        
        try {
            //TEST:
            //javax.swing.UIManager.setLookAndFeel("com.sun.java.plaf.motif.MotifLookAndFeel");
            //javax.swing.UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClass‌​Name());
            
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                //TEST:
                //JOptionPane.showMessageDialog(null, info);
                
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SearchResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SearchResult().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> A_ComboBox_sort;
    private javax.swing.JButton A_btn_clear;
    private javax.swing.JButton A_btn_search;
    private javax.swing.JLabel A_lab_array_sub_id;
    private javax.swing.JLabel A_lab_lab_id;
    private javax.swing.JLabel A_lab_result_id;
    private javax.swing.JLabel A_lbl_genes1;
    private javax.swing.JLabel A_lbl_genes1_1;
    private javax.swing.JLabel A_lbl_genes2;
    private javax.swing.JLabel A_lbl_genes2_1;
    private javax.swing.JRadioButton A_rbtn_NOT;
    private javax.swing.JRadioButton A_rbtn_sort;
    private javax.swing.JTextField A_txt1_genes1;
    private javax.swing.JTextField A_txt1_genes2;
    private javax.swing.JTextField A_txt2_genes1;
    private javax.swing.JTextField A_txt2_genes2;
    private javax.swing.JTextField A_txt3_genes1;
    private javax.swing.JTextField A_txt3_genes2;
    private javax.swing.JTextField A_txt4_genes1;
    private javax.swing.JTextField A_txt4_genes2;
    private javax.swing.JTextField A_txt5_genes1;
    private javax.swing.JTextField A_txt5_genes2;
    private javax.swing.JTextField A_txt_ANDOR;
    private javax.swing.JTextField A_txt_Creg;
    private javax.swing.JTextField A_txt_Creg_1;
    private javax.swing.JTextField A_txt_array_sub_id;
    private javax.swing.JTextField A_txt_call;
    private javax.swing.JTextField A_txt_call_1;
    private javax.swing.JTextField A_txt_chr;
    private javax.swing.JTextField A_txt_chr_1;
    private javax.swing.JTextField A_txt_cnst;
    private javax.swing.JTextField A_txt_cnst_1;
    private javax.swing.JTextField A_txt_lab_id;
    private javax.swing.JTextField A_txt_locEnd;
    private javax.swing.JTextField A_txt_locEnd_1;
    private javax.swing.JTextField A_txt_locStart;
    private javax.swing.JTextField A_txt_locStart_1;
    private javax.swing.JTextField A_txt_nom;
    private javax.swing.JTextField A_txt_nom_1;
    private javax.swing.JTextField A_txt_resID;
    private javax.swing.JTextField A_txt_resID_1;
    private javax.swing.JTextField A_txt_result_id;
    private javax.swing.JTextField A_txt_size;
    private javax.swing.JTextField A_txt_size_1;
    private javax.swing.JTextField A_txt_sort;
    private javax.swing.JTextField A_txt_type;
    private javax.swing.JTextField A_txt_type_1;
    private javax.swing.JComboBox<String> ComboBox_projPat;
    private javax.swing.JComboBox<String> ComboBox_stdyPat;
    private javax.swing.JComboBox<String> F_ComboBox_sort;
    private javax.swing.JButton F_btn_clear;
    private javax.swing.JButton F_btn_search;
    private javax.swing.JLabel F_lab_fish_sub_id;
    private javax.swing.JLabel F_lab_lab_id;
    private javax.swing.JLabel F_lab_result_id;
    private javax.swing.JRadioButton F_rbtn_NOT;
    private javax.swing.JRadioButton F_rbtn_sort;
    private javax.swing.JTextArea F_txtArea_loc;
    private javax.swing.JTextField F_txt_ANDOR;
    private javax.swing.JTextField F_txt_fchng;
    private javax.swing.JTextField F_txt_fchng_1;
    private javax.swing.JTextField F_txt_fish_sub_id;
    private javax.swing.JTextField F_txt_fsn;
    private javax.swing.JTextField F_txt_fsn_1;
    private javax.swing.JTextField F_txt_kerne;
    private javax.swing.JTextField F_txt_kerne_1;
    private javax.swing.JTextField F_txt_lab_id;
    private javax.swing.JTextField F_txt_material;
    private javax.swing.JTextField F_txt_material_1;
    private javax.swing.JTextField F_txt_mitos;
    private javax.swing.JTextField F_txt_mitos_1;
    private javax.swing.JTextField F_txt_percent;
    private javax.swing.JTextField F_txt_percent_1;
    private javax.swing.JTextField F_txt_probe_no;
    private javax.swing.JTextField F_txt_reg1;
    private javax.swing.JTextField F_txt_reg1_1;
    private javax.swing.JTextField F_txt_reg2;
    private javax.swing.JTextField F_txt_reg2_1;
    private javax.swing.JTextField F_txt_resID;
    private javax.swing.JTextField F_txt_resID_1;
    private javax.swing.JTextField F_txt_result;
    private javax.swing.JTextField F_txt_result_1;
    private javax.swing.JTextField F_txt_result_id;
    private javax.swing.JTextField F_txt_sig1;
    private javax.swing.JTextField F_txt_sig1_1;
    private javax.swing.JTextField F_txt_sig2;
    private javax.swing.JTextField F_txt_sig2_1;
    private javax.swing.JTextField F_txt_sort;
    private javax.swing.JPanel Info_top;
    private javax.swing.JComboBox<String> ZG_ComboBox_sort;
    private javax.swing.JRadioButton ZG_rbtn_NOT;
    private javax.swing.JRadioButton ZG_rbtn_sort;
    private javax.swing.JTextField ZG_txt_ANDOR;
    private javax.swing.JTextField ZG_txt_sort;
    private javax.swing.JTextField ZI_txt_chr;
    private javax.swing.JTextField ZI_txt_chr_1;
    private javax.swing.JTextField ZI_txt_cp;
    private javax.swing.JTextField ZI_txt_cp_1;
    private javax.swing.JTextField ZI_txt_iscn;
    private javax.swing.JTextField ZI_txt_iscn_1;
    private javax.swing.JTextField ZI_txt_mat;
    private javax.swing.JTextField ZI_txt_mat_1;
    private javax.swing.JTextField ZI_txt_mitos;
    private javax.swing.JTextField ZI_txt_mitos_1;
    private javax.swing.JTextField ZI_txt_resId;
    private javax.swing.JTextField ZI_txt_resId_1;
    private javax.swing.JTextField ZI_txt_stim;
    private javax.swing.JTextField ZI_txt_stim_1;
    private javax.swing.JTextField ZR_txt_chng;
    private javax.swing.JTextField ZR_txt_chng_1;
    private javax.swing.JTextField ZR_txt_chr;
    private javax.swing.JTextField ZR_txt_chr_1;
    private javax.swing.JTextField ZR_txt_klonID;
    private javax.swing.JTextField ZR_txt_klonID_1;
    private javax.swing.JTextField ZR_txt_region;
    private javax.swing.JTextField ZR_txt_region_1;
    private javax.swing.JButton Z_btn_clear;
    private javax.swing.JButton Z_btn_search;
    private javax.swing.JLabel Z_lab_klon_id;
    private javax.swing.JLabel Z_lab_lab_id;
    private javax.swing.JLabel Z_lab_result_id;
    private javax.swing.JTextField Z_txt_klon_id;
    private javax.swing.JTextField Z_txt_lab_id;
    private javax.swing.JTextField Z_txt_result_id;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_Emergency;
    private javax.swing.JRadioButton btn_TTT;
    private javax.swing.JButton btn_loadQuery;
    private javax.swing.JButton btn_openGenOnc;
    private javax.swing.JButton btn_openLoc;
    private javax.swing.JButton btn_saveQuery;
    private javax.swing.JRadioButton btn_selCol;
    private javax.swing.JRadioButton btn_selPat;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1_HowTo;
    private javax.swing.JMenuItem jMenuItem1_openModel;
    private javax.swing.JMenuItem jMenuItem2_Info;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lab_Creg;
    private javax.swing.JLabel lab_Genes;
    private javax.swing.JLabel lbl_array_signal;
    private javax.swing.JLabel lbl_fish_signal;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JLabel lbl_zg_signal;
    private javax.swing.JMenuItem popUpMenu_intrprWin;
    private javax.swing.JMenuItem popUpMenu_moveTbl;
    private javax.swing.JMenuItem popUpMenu_save;
    private javax.swing.JMenuItem popUpMenu_selectAll;
    private javax.swing.JPopupMenu popUpSave;
    private javax.swing.JRadioButton rbtn_ArrQuery;
    private javax.swing.JRadioButton rbtn_PB;
    private javax.swing.JRadioButton rbtn_SB;
    private javax.swing.JRadioButton rbtn_ST;
    private javax.swing.JRadioButton rbtn_ZGdetailResult;
    private javax.swing.JRadioButton rbtn_nyd;
    private javax.swing.JRadioButton rbtn_onlyPat;
    private javax.swing.JRadioButton rbtn_onlyPat1;
    private javax.swing.JRadioButton rbtn_useAresult;
    private javax.swing.JRadioButton rbtn_useFresult;
    private javax.swing.JPanel tab_ZG;
    private javax.swing.JPanel tab_array;
    private javax.swing.JPanel tab_fish;
    private javax.swing.JTabbedPane tab_main;
    private javax.swing.JTable table_array;
    private javax.swing.JTable table_fish;
    private javax.swing.JTable table_queryIDs;
    private javax.swing.JTable table_statistics;
    private javax.swing.JTable table_zg_iscn;
    private javax.swing.JTable table_zg_result;
    private javax.swing.JTextArea txtArea_Creg;
    private javax.swing.JTextArea txtArea_genes;
    private javax.swing.JTextField txt_fullLoc;
    private javax.swing.JTextField txt_genOnc;
    // End of variables declaration//GEN-END:variables
}
