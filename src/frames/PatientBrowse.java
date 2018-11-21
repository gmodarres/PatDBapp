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

import static frames.SetConnection.PresentMode;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import myClass.ColoredTableCellRenderer2;
import myClass.CustomSorter;
import myClass.DBconnect;
import myClass.IdManagement;
import myClass.Log;
import myClass.MenuDriver;
import myClass.OSDetector;
import static myClass.ShowSqlSelector.showSqlInWindow;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author gerda.modarres
 */
public class PatientBrowse extends javax.swing.JFrame {
    
    String ids = null;
    static String PB_resultIDs = null;
    
    JTable outTable = null;  
    
    boolean Present = PresentMode;
        
    Log my_log;

    /**
     * Creates new form PatientBrowse
     */
    public PatientBrowse() {
        MenuDriver menu = new MenuDriver();     // create instance of JMenuBar menuBarGlobal 
        this.setJMenuBar( menu.getMenuBar() );
        
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        initial_tables();
        
        Info_top4.getRootPane().setDefaultButton(btn_Search);
        
        my_log.logger.info("open PatientBrowse()");
    }

    private void showRows(ResultSet rs, String table) {
        try {
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                if (table.equals("patient")){
                    lbl_patient_rowsReturned.setText(getRows + " row(s) returned");
                } else if (table.equals("study")){
                    lbl_study_rowsReturned.setText(getRows + " row(s) returned");
                } else if (table.equals("project")){
                    lbl_project_rowsReturned.setText(getRows + " row(s) returned");
                } 
                my_log.logger.info(getRows+" row(s) returned");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SampleBrowse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         
    private void initial_tables() {

        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT p.pat_id, fm_pat_no, fname, surname, surname_old, sex, b_date, dg_date, mb_down as MDown FROM patient p WHERE 1=1";
        if (Present == true) { // PRESENT
            sql = "SELECT p.pat_id, fm_pat_no, present as fname, present as surname, present as surname_old, sex, b_date, dg_date, mb_down as MDown FROM patient p WHERE 1=1";
        }
        
        // only for the set collected IDs
        if (rbtn_idCollected.isSelected()) {
            sql = IdCollector.deliver_collected_ids(sql,"","","p.");
        }
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_patient.setModel(DbUtils.resultSetToTableModel(rs));

            CustomSorter.table_customRowSort(table_patient);   
            setLookTable_patient(table_patient);

            showRows(rs, "patient");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } 
        
        String sql2 = "SELECT p.pat_id, stdy_name, pat_study_id as stdy_ID, stdy_group, mon_pat as monitor"
                    + " FROM patient p, pat_instudy ps, study s"
                    + " WHERE p.pat_id=ps.pat_id"
                    + " AND s.stdy_id=ps.stdy_id";
        
        // only for the set collected IDs
        if (rbtn_idCollected.isSelected()) {
            sql2 = IdCollector.deliver_collected_ids(sql2,"","","p.");
        }
        
        try {
            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();
            table_patinstudy.setModel(DbUtils.resultSetToTableModel(rs));
            DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();  
            table_patinstudy.setDefaultRenderer(Object.class , ren); 
            CustomSorter.table_customRowSort(table_patinstudy);  
            setLookTable_study(table_patinstudy);
            
            showRows(rs, "study");
                  
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } 
        
        String sql3 = "SELECT p.pat_id, proj_name"
                    + " FROM patient p, pat_inproject pj, project j"
                    + " WHERE p.pat_id=pj.pat_id "
                    + " AND j.proj_id=pj.proj_id ";
        // only for the set collected IDs
        if (rbtn_idCollected.isSelected()) {
            sql3 = IdCollector.deliver_collected_ids(sql3,"","","p.");
        }
        
        try {
            pst = conn.prepareStatement(sql3);
            rs = pst.executeQuery();
            table_patinproject.setModel(DbUtils.resultSetToTableModel(rs));
            DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();  
            table_patinproject.setDefaultRenderer(Object.class , ren);
            CustomSorter.table_customRowSort(table_patinproject);   
            setLookTable_project(table_patinproject);
            
            showRows(rs, "project");
                     
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
        
    private void get_resultIDs() {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String all_ids = this.ids;
                                       
        if (all_ids.length() > 1) {
            all_ids = all_ids.substring(0, (all_ids.length() - 1));

            String sql = "SELECT p.pat_id, m.result_id, m.lab_id FROM main_result m, sample s, patient p"
                    + " Where m.lab_id=s.lab_id and s.pat_id=p.pat_id"      
                    + " AND p.pat_id in (" + all_ids + ")";   
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
               
                //get_r_ids(sql,pst,rs,conn);
                //this.PB_resultIDs = IdManagement.get_r_ids(sql, pst, rs, conn);
                this.PB_resultIDs = IdManagement.get_ids(sql, pst, rs, conn, "result_id");
                //txtArea_test.setText("PB_result:   "+PB_resultIDs);  //TEST
                //showRows(rs);

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
    }
    
    private void fillTable_patient(String sql, String moreIds) {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String pat_ids = "";
              
        if (rbtn_searchCrit2.isSelected()){
            if (moreIds.length() > 1){
                sql = sql + " AND p.pat_id in (" + moreIds + ")";
            } else {
                sql = sql + " AND p.pat_id in (0)";
            }       
        }
        //txtArea_test.append("\nfillTPat:  "+sql);
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            my_log.logger.info("SQL:  " + sql);
            table_patient.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patient);
            setLookTable_patient(table_patient);
            showSqlInWindow(sql, "PatientBrowse/filltable_patient patient");

            //get_ids(sql, pst, rs, conn);
            this.ids = IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
            get_resultIDs();
            //get_r_ids(sql,pst,rs,conn); 
            showRows(rs, "patient");

            // get pat_ids from sql
            pat_ids = this.ids;
            //JOptionPane.showMessageDialog(null, "pat_ids:  "+pat_ids);  //TEST
            if (pat_ids.length() > 1) {
                pat_ids = pat_ids.substring(0, (pat_ids.length() - 1));
            } else {
                //JOptionPane.showMessageDialog(null, "nix drin:  "+pat_ids);  // TEST
                pat_ids = "0";
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }

/////// table study
            String sql2 = "SELECT p.pat_id, stdy_name, pat_study_id as stdy_ID, stdy_group, mon_pat as monitor"
                + " FROM patient p, pat_instudy ps, study s"
                + " WHERE p.pat_id=ps.pat_id"
                + " AND s.stdy_id=ps.stdy_id"
                + " AND p.pat_id in (" + pat_ids + ")";
        
        //txtArea_test.append("\nTPat...sql2:  "+sql2);  // TEST
        
        try {
            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();

            my_log.logger.info("SQL2:  " + sql2);
            table_patinstudy.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patinstudy);
            setLookTable_study(table_patinstudy);
            showSqlInWindow(sql2, "PatientBrowse/filltable_patient pat_instudy");
            
            showRows(rs, "study");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }

/////// table project
        String sql3 = "SELECT p.pat_id, proj_name"
                + " FROM patient p, pat_inproject pj, project j"
                + " WHERE p.pat_id=pj.pat_id "
                + " AND j.proj_id=pj.proj_id "
                + " AND p.pat_id in (" + pat_ids + ")";       
        try {
            pst = conn.prepareStatement(sql3);
            rs = pst.executeQuery();

            my_log.logger.info("SQL3:  " + sql3);
            table_patinproject.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patinproject);
            setLookTable_project(table_patinproject);
            showSqlInWindow(sql3, "PatientBrowse/filltable_patient pat_inproject");
            
            showRows(rs, "project");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }finally {
                try {
                    if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }
    }
    
    private void fillTable_study(String sql2, String moreIds){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String pat_ids = "";
        if (rbtn_searchCrit2.isSelected()){
            if (moreIds.length() > 1){
                sql2 = sql2 + " AND p.pat_id in (" + moreIds + ")";
            } else {
                sql2 = sql2 + " AND p.pat_id in (0)";
            }       
        }
        //txtArea_test.append("\nfillTStu:  "+sql2);  //TEST
        
        
        try {
            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();

            my_log.logger.info("SQL2:  " + sql2);
            table_patinstudy.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patinstudy);
            setLookTable_study(table_patinstudy);
            showSqlInWindow(sql2, "PatientBrowse/filltable_study pat_instudy");
            
            showRows(rs, "study");
            
            //get_ids(sql, pst, rs, conn);
            this.ids = IdManagement.get_ids(sql2, pst, rs, conn, "pat_id");
            get_resultIDs();
            // get pat_ids from sql
            pat_ids = this.ids;
            if (pat_ids.length() > 1) {
                pat_ids = pat_ids.substring(0, (pat_ids.length() - 1));
            }else{
                pat_ids = "0";
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }
/////// table patient
        String sql = "SELECT p.pat_id, fm_pat_no, fname, surname, surname_old, sex, b_date, dg_date, mb_down as MDown FROM patient p"
                + " WHERE p.pat_id in (" + pat_ids + ")";
        if (Present == true) { // PRESENT
            sql = "SELECT p.pat_id, fm_pat_no, present as fname, present as surname, present as surname_old, sex, b_date, dg_date, mb_down as MDown FROM patient p"
                + " WHERE p.pat_id in (" + pat_ids + ")";
        }
        
        //txtArea_test.append("\nTStu...sql:  "+sql);  // TEST
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            my_log.logger.info("SQL:  " + sql);
            table_patient.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patient);
            setLookTable_patient(table_patient);
            showSqlInWindow(sql, "PatientBrowse/filltable_study patient");
            
            showRows(rs, "patient");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }

/////// table project
        String sql3 = "SELECT p.pat_id, proj_name"
                + " FROM patient p, pat_inproject pj, project j"
                + " WHERE p.pat_id=pj.pat_id "
                + " AND j.proj_id=pj.proj_id "
                + " AND p.pat_id in (" + pat_ids + ")";
        try {
            pst = conn.prepareStatement(sql3);
            rs = pst.executeQuery();

            my_log.logger.info("SQL3:  " + sql3);
            table_patinproject.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patinproject);
            setLookTable_project(table_patinproject);
            showSqlInWindow(sql3, "PatientBrowse/filltable_study pat_inproject");
            
            showRows(rs, "project");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }finally {
                try {
                    if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }
    
    }
    
    private void fillTable_project(String sql3, String moreIds) {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String pat_ids = "";
        if (rbtn_searchCrit2.isSelected()){
            if (moreIds.length() > 1){
                sql3 = sql3 + " AND p.pat_id in (" + moreIds + ")";
            } else {
                sql3 = sql3 + " AND p.pat_id in (0)";
            }       
        }
        //txtArea_test.append("\n2-fillTProj:  "+sql3);  //TEST
        try {
            pst = conn.prepareStatement(sql3);
            rs = pst.executeQuery();

            my_log.logger.info("SQL3:  " + sql3);
            table_patinproject.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patinproject);
            setLookTable_project(table_patinproject);
            showSqlInWindow(sql3, "PatientBrowse/filltable_project pat_inproject");
            
            showRows(rs, "project");
            
            //get_ids(sql, pst, rs, conn);
            this.ids = IdManagement.get_ids(sql3, pst, rs, conn, "pat_id");
            get_resultIDs();
            // get pat_ids from sql
            pat_ids = this.ids;
            if (pat_ids.length() > 1) {
                pat_ids = pat_ids.substring(0, (pat_ids.length() - 1));
            }else {
                //JOptionPane.showMessageDialog(null, "nix drin:  "+pat_ids);  // TEST
                pat_ids = "0";
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }

        /////// table patient
        String sql = "SELECT p.pat_id, fm_pat_no, fname, surname, surname_old, sex, b_date, dg_date, mb_down as MDown FROM patient p"
                + " WHERE p.pat_id in (" + pat_ids + ")";
        if (Present == true) { // PRESENT
            sql = "SELECT p.pat_id, fm_pat_no, present as fname, present as surname, present as surname_old, sex, b_date, dg_date, mb_down as MDown FROM patient p"
                + " WHERE p.pat_id in (" + pat_ids + ")";
        }
        //txtArea_test.append("\nTProj...sql:  "+sql);  // TEST
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            my_log.logger.info("SQL:  " + sql);
            table_patient.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patient);
            setLookTable_patient(table_patient);
            showSqlInWindow(sql, "PatientBrowse/filltable_project patient"); 
            
            showRows(rs, "patient");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }

/////// table study
        String sql2 = "SELECT p.pat_id, stdy_name, pat_study_id as stdy_ID, stdy_group, mon_pat as monitor"
                + " FROM patient p, pat_instudy ps, study s"
                + " WHERE p.pat_id=ps.pat_id"
                + " AND s.stdy_id=ps.stdy_id"
                + " AND p.pat_id in (" + pat_ids + ")";
        try {
            pst = conn.prepareStatement(sql2);
            rs = pst.executeQuery();

            my_log.logger.info("SQL2:  " + sql2);
            table_patinstudy.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patinstudy);
            setLookTable_study(table_patinstudy);
            showSqlInWindow(sql2, "PatientBrowse/filltable_project pat_instudy"); 
            
            showRows(rs, "study");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }

    }

    // TODO: source not necessary?
    private String getIdsIn(String sqlAdd, Integer source){     // integer corresponding to sql part requested (1=patient, 2=study, 3=project) 
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String all_ids ="";
        
        //JOptionPane.showMessageDialog(null,"sqlAdd:   "+ sqlAdd); //TSET
        // get ID list from SQL
        this.ids = IdManagement.get_ids(sqlAdd, pst, rs, conn, "pat_id");
        all_ids = this.ids;
        if (all_ids.length() > 1) {
            all_ids = all_ids.substring(0, (all_ids.length() - 1));
        }
        //txtArea_test.setText("START\ngetIdsIn():   "+sqlAdd+"\nIds:   "+all_ids);   //TEST
        return all_ids;
    }
    
    private static boolean isRightClick(MouseEvent e) {
        return (e.getButton() == MouseEvent.BUTTON3
                || (System.getProperty("os.name").contains("Mac OS X")
                && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0
                && (e.getModifiers() & InputEvent.CTRL_MASK) != 0));
    }

    private void setLookTable_patient(javax.swing.JTable table) {
        if (table_patient.getColumnModel().getColumnCount() > 0) {
            table_patient.getColumnModel().getColumn(0).setPreferredWidth(60);
            table_patient.getColumnModel().getColumn(0).setMaxWidth(100);
            table_patient.getColumnModel().getColumn(1).setPreferredWidth(80);
            table_patient.getColumnModel().getColumn(1).setMaxWidth(100);
            table_patient.getColumnModel().getColumn(2).setPreferredWidth(140);
            table_patient.getColumnModel().getColumn(2).setMaxWidth(170);
            table_patient.getColumnModel().getColumn(3).setPreferredWidth(140); // surname
            table_patient.getColumnModel().getColumn(3).setMaxWidth(300);
            table_patient.getColumnModel().getColumn(4).setPreferredWidth(120);
            table_patient.getColumnModel().getColumn(4).setMaxWidth(170);
            table_patient.getColumnModel().getColumn(5).setPreferredWidth(60); // Sex
            table_patient.getColumnModel().getColumn(5).setMaxWidth(60);
            table_patient.getColumnModel().getColumn(6).setPreferredWidth(100);
            table_patient.getColumnModel().getColumn(6).setMaxWidth(120);
            table_patient.getColumnModel().getColumn(7).setPreferredWidth(100);
            table_patient.getColumnModel().getColumn(7).setMaxWidth(120);
            table_patient.getColumnModel().getColumn(8).setPreferredWidth(60); // MDown
            table_patient.getColumnModel().getColumn(8).setMaxWidth(60);
        }
    }
    
    private void setLookTable_study(javax.swing.JTable table) {
        if (table_patinstudy.getColumnModel().getColumnCount() > 0) {
            table_patinstudy.getColumnModel().getColumn(0).setPreferredWidth(60);
            table_patinstudy.getColumnModel().getColumn(0).setMaxWidth(60);
            table_patinstudy.getColumnModel().getColumn(1).setPreferredWidth(200);
            table_patinstudy.getColumnModel().getColumn(1).setMaxWidth(500);
            table_patinstudy.getColumnModel().getColumn(2).setPreferredWidth(100); // stdy_ID
            table_patinstudy.getColumnModel().getColumn(2).setMaxWidth(100);
            table_patinstudy.getColumnModel().getColumn(3).setPreferredWidth(120); // stdy_group
            table_patinstudy.getColumnModel().getColumn(3).setMaxWidth(300);
            table_patinstudy.getColumnModel().getColumn(4).setPreferredWidth(60); // monitoring
            table_patinstudy.getColumnModel().getColumn(4).setMaxWidth(100);
        }
    }
    
    private void setLookTable_project(javax.swing.JTable table) { 
        if (table_patinproject.getColumnModel().getColumnCount() > 0) {
            table_patinproject.getColumnModel().getColumn(0).setPreferredWidth(60);
            table_patinproject.getColumnModel().getColumn(0).setMaxWidth(100);
            table_patinproject.getColumnModel().getColumn(1).setPreferredWidth(200);
            table_patinproject.getColumnModel().getColumn(1).setMaxWidth(500);
        }
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
        popUpResult = new javax.swing.JPopupMenu();
        cpPatIds = new javax.swing.JMenuItem();
        Info_top4 = new javax.swing.JPanel();
        btn_Search = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        rbtn_searchCritMain = new javax.swing.JRadioButton();
        rbtn_NOT1 = new javax.swing.JRadioButton();
        txt_searchCrit1 = new javax.swing.JTextField();
        rbtn_searchCrit1 = new javax.swing.JRadioButton();
        CB_searchCrit1 = new javax.swing.JComboBox<>();
        CB_andor = new javax.swing.JComboBox<>();
        rbtn_searchCrit2 = new javax.swing.JRadioButton();
        CB_searchCrit2 = new javax.swing.JComboBox<>();
        rbtn_NOT2 = new javax.swing.JRadioButton();
        txt_searchCrit2 = new javax.swing.JTextField();
        rbtn_all = new javax.swing.JRadioButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtArea_test = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        rbtn_idCollected = new javax.swing.JRadioButton();
        jToolBar1 = new javax.swing.JToolBar();
        bnt_test = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_patient = new javax.swing.JTable();
        lbl_patient_rowsReturned = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_patinstudy = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_patinproject = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        lbl_study_rowsReturned = new javax.swing.JLabel();
        lbl_project_rowsReturned = new javax.swing.JLabel();

        cpPatIds.setText("copy pat_ids ...");
        cpPatIds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpPatIdsActionPerformed(evt);
            }
        });
        popUpResult.add(cpPatIds);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Linked Results Analysis Tool - browse patients");

        Info_top4.setBackground(new java.awt.Color(102, 153, 255));
        Info_top4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Info_top4.setRequestFocusEnabled(false);

        btn_Search.setBackground(new java.awt.Color(0, 140, 140));
        btn_Search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Search.png"))); // NOI18N
        btn_Search.setText("Search");
        btn_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SearchActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(102, 153, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(rbtn_searchCritMain);
        rbtn_searchCritMain.setText("patients with ...");
        rbtn_searchCritMain.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_searchCritMain.setBorderPainted(true);
        rbtn_searchCritMain.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_searchCritMainActionPerformed(evt);
            }
        });

        rbtn_NOT1.setText("NOT");
        rbtn_NOT1.setEnabled(false);

        txt_searchCrit1.setBackground(new java.awt.Color(204, 204, 204));
        txt_searchCrit1.setToolTipText("searching for Date:  >=,date  or  between,date1,date2");
        txt_searchCrit1.setEnabled(false);

        rbtn_searchCrit1.setEnabled(false);

        CB_searchCrit1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "pat_id", "fm_pat_no", "fname", "surname", "surname old", "sex", "birth date", "diagnosis date", "mb. down", "study", "study_id", "stdy_group", "monitoring", "project", " ", " " }));
        CB_searchCrit1.setEnabled(false);
        CB_searchCrit1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_searchCrit1ActionPerformed(evt);
            }
        });

        CB_andor.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor.setToolTipText("");
        CB_andor.setEnabled(false);

        rbtn_searchCrit2.setEnabled(false);

        CB_searchCrit2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "pat_id", "fm_pat_no", "fname", "surname", "surname old", "sex", "birth date", "diagnosis date", "mb. down", "study", "study_id", "stdy_group", "monitoring", "project" }));
        CB_searchCrit2.setToolTipText("");
        CB_searchCrit2.setEnabled(false);
        CB_searchCrit2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_searchCrit2ActionPerformed(evt);
            }
        });

        rbtn_NOT2.setText("NOT");
        rbtn_NOT2.setEnabled(false);

        txt_searchCrit2.setBackground(new java.awt.Color(204, 204, 204));
        txt_searchCrit2.setEnabled(false);

        buttonGroup1.add(rbtn_all);
        rbtn_all.setSelected(true);
        rbtn_all.setText("all patients");
        rbtn_all.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all.setBorderPainted(true);
        rbtn_all.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_allActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(rbtn_NOT2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_searchCrit2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbtn_all, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(rbtn_searchCrit1, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(rbtn_searchCrit2, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(CB_searchCrit1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addGap(3, 3, 3)
                                            .addComponent(CB_searchCrit2, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(rbtn_searchCritMain, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5)
                        .addComponent(rbtn_NOT1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_searchCrit1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(rbtn_all)
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_searchCritMain)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT1)
                            .addComponent(rbtn_searchCrit1)
                            .addComponent(txt_searchCrit1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_searchCrit1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT2)
                            .addComponent(rbtn_searchCrit2)
                            .addComponent(txt_searchCrit2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_searchCrit2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CB_andor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        txtArea_test.setColumns(20);
        txtArea_test.setRows(5);
        jScrollPane4.setViewportView(txtArea_test);

        jPanel2.setBackground(new java.awt.Color(102, 153, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        rbtn_idCollected.setText("use IDs from collector");
        rbtn_idCollected.setToolTipText("select to get results from patients in a certain project (select from below)");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtn_idCollected, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtn_idCollected)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout Info_top4Layout = new javax.swing.GroupLayout(Info_top4);
        Info_top4.setLayout(Info_top4Layout);
        Info_top4Layout.setHorizontalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Search)
                .addGap(21, 21, 21))
        );
        Info_top4Layout.setVerticalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_Search)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        table_patient.setAutoCreateRowSorter(true);
        table_patient.setModel(new javax.swing.table.DefaultTableModel(
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
        table_patient.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_patientMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(table_patient);
        table_patient.getAccessibleContext().setAccessibleName("table_patient");

        lbl_patient_rowsReturned.setText(" ");

        table_patinstudy.setAutoCreateRowSorter(true);
        table_patinstudy.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(table_patinstudy);
        table_patinstudy.getAccessibleContext().setAccessibleName("table_patInStdy");

        table_patinproject.setAutoCreateRowSorter(true);
        table_patinproject.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(table_patinproject);
        table_patinproject.getAccessibleContext().setAccessibleName("table_patInProj");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 153, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("patient");
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(102, 153, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("study");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(102, 153, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("project");
        jLabel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(102, 102, 102)));

        lbl_study_rowsReturned.setText(" ");

        lbl_project_rowsReturned.setText(" ");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Info_top4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lbl_patient_rowsReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(530, 530, 530)))
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane2)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lbl_study_rowsReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(347, 347, 347)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lbl_project_rowsReturned, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))))
                .addGap(2, 2, 2)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_patient_rowsReturned)
                    .addComponent(lbl_study_rowsReturned)
                    .addComponent(lbl_project_rowsReturned))
                .addContainerGap())
        );

        getAccessibleContext().setAccessibleName("PatientBrowse");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void rbtn_searchCritMainActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_searchCritMainActionPerformed
        if (rbtn_searchCritMain.isSelected()){
            rbtn_searchCrit1.setEnabled(true);
            rbtn_NOT1.setEnabled(true);
            txt_searchCrit1.setEnabled(true);
            txt_searchCrit1.setBackground(new java.awt.Color(255, 255, 255));
            CB_searchCrit1.setEnabled(true);

            rbtn_searchCrit2.setEnabled(true);
            rbtn_NOT2.setEnabled(true);
            txt_searchCrit2.setEnabled(true);
            txt_searchCrit2.setBackground(new java.awt.Color(255, 255, 255));
            CB_searchCrit2.setEnabled(true);
            CB_andor.setEnabled(true);
        }
    }//GEN-LAST:event_rbtn_searchCritMainActionPerformed

    private void rbtn_allActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_allActionPerformed
        if (rbtn_all.isSelected()){
            rbtn_searchCrit1.setEnabled(false);
            rbtn_NOT1.setEnabled(false);
            txt_searchCrit1.setEnabled(false);
            txt_searchCrit1.setBackground(new java.awt.Color(204, 204, 204));
            CB_searchCrit1.setEnabled(false);

            rbtn_searchCrit2.setEnabled(false);
            rbtn_NOT2.setEnabled(false);
            txt_searchCrit2.setEnabled(false);
            txt_searchCrit2.setBackground(new java.awt.Color(204, 204, 204));
            CB_searchCrit2.setEnabled(false);
            CB_andor.setEnabled(false);
        }
    }//GEN-LAST:event_rbtn_allActionPerformed

    private void btn_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SearchActionPerformed
        if(rbtn_all.isSelected()){
            initial_tables();
            
        } else if (rbtn_searchCritMain.isSelected()){
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;          
            /*String sql = "SELECT p.pat_id, fm_pat_no, fname, surname, surname_old, sex, b_date, dg_date, mb_down as MDown, stdy_name, pat_study_id as stdy_ID, stdy_group, mon_pat as monitor, proj_name"
                   + " FROM patient p, pat_instudy ps, pat_inproject pj, study s, project j"
                   + " WHERE p.pat_id=ps.pat_id and p.pat_id=pj.pat_id "
                   + " AND s.stdy_id=ps.stdy_id and j.proj_id=pj.proj_id ";*/
            
            // table patient
            String sql = "SELECT p.pat_id, fm_pat_no, fname, surname, surname_old, sex, b_date, dg_date, mb_down as MDown FROM patient p WHERE 1=1";
            String sqlAdd = "";
            if (Present == true) { // PRESENT
                sql = "SELECT p.pat_id, fm_pat_no, present as fname, present as surname, present as surname_old, sex, b_date, dg_date, mb_down as MDown FROM patient p WHERE 1=1";
            }
            
            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                sql = IdCollector.deliver_collected_ids(sql,"","","p.");
            }
            
            // table study
            String sql2 = "SELECT p.pat_id, stdy_name, pat_study_id as stdy_ID, stdy_group, mon_pat as monitor"
                    + " FROM patient p, pat_instudy ps, study s"
                    + " WHERE p.pat_id=ps.pat_id"
                    + " AND s.stdy_id=ps.stdy_id";
            String sql2Add = "";
            
            // table project
            String sql3 = "SELECT p.pat_id, proj_name"
                    + " FROM patient p, pat_inproject pj, project j"
                    + " WHERE p.pat_id=pj.pat_id "
                    + " AND j.proj_id=pj.proj_id ";
            String sql3Add = "";
            
            Integer sqlStart1 = 0;
            Integer sqlFollow = 0;
            String IdsIn = "NOTSET";
            
            if (rbtn_searchCrit1.isSelected()) {
                String searchCrit1_select = CB_searchCrit1.getSelectedItem().toString();
                String searchCrit1 = "";
                String sCrit1_txt = txt_searchCrit1.getText();
                String select1 = "";  
                switch (searchCrit1_select) {
                    case "pat_id":
                        searchCrit1 = "p.pat_id";
                        select1 = "ID";
                        sqlStart1 = 1;
                        break;
                    case "fm_pat_no":
                        searchCrit1 = "fm_pat_no";
                        select1 = "ID";
                        sqlStart1 = 1;
                        break;
                    case "fname":
                        searchCrit1 = "fname";
                        select1 = "TXT";
                        sqlStart1 = 1;
                        break;
                    case "surname":
                        searchCrit1 = "surname";
                        select1 = "TXT";
                        sqlStart1 = 1;
                        break;
                    case "surname old":
                        searchCrit1 = "surname_old";
                        select1 = "TXT";
                        sqlStart1 = 1;
                        break;
                    case "sex":
                        searchCrit1 = "sex";
                        select1 = "TXT";
                        sqlStart1 = 1;
                        break;
                    case "birth date":
                        searchCrit1 = "b_date";
                        select1 ="DATE";
                        sqlStart1 = 1;
                        break;
                    case "diagnosis date":
                        searchCrit1 = "dg_date";
                        select1 = "DATE";
                        sqlStart1 = 1;
                        break;
                    case "mb. down":
                        searchCrit1 = "mb_down";
                        select1 = "TXT";
                        sqlStart1 = 1;
                        break;
                    case "study":
                        searchCrit1 = "stdy_name";
                        select1 = "TXT";
                        sqlStart1 = 2;
                        break;
                    case "study_id":
                        searchCrit1 = "pat_study_id";
                        select1 = "TXT";
                        sqlStart1 = 2;
                        break;
                    case "stdy_group":
                        searchCrit1 = "stdy_group";
                        select1 = "NULLable";
                        sqlStart1 = 2;
                        break; 
                    case "monitoring":
                        searchCrit1 = "mon_pat";
                        select1 = "NULLable";
                        sqlStart1 = 2;
                        break;
                    case "project":
                        searchCrit1 = "proj_name";
                        select1 = "TXT";
                        sqlStart1 = 3;
                        break;
                    default:
                        break;
                }

                if (sqlStart1 == 1) {       // patient
                    if (select1.equals("ID")) {
                        if (rbtn_NOT1.isSelected()) {
                            sql = sql + " and (" + searchCrit1 + " not in ( " + sCrit1_txt + " )";
                        } else {
                            sql = sql + " and (" + searchCrit1 + " in ( " + sCrit1_txt + " )";
                        }
                    } else if (select1.equals("TXT")) {
                        if (rbtn_NOT1.isSelected()) {
                            sql = sql + " and (" + searchCrit1 + " NOT like '%" + sCrit1_txt + "%'";
                        } else {
                            sql = sql + " and (" + searchCrit1 + " like '%" + sCrit1_txt + "%'";
                        }
                    } else if (select1.equals("DATE")) {
                        String splitDate[] = sCrit1_txt.split(",");          //  >=,2009-01-01   between,2009-01-01;2012-01-01

                        if (splitDate[0].equals("between")) {
                            //JOptionPane.showMessageDialog(null, "1;2;3 " +splitDate[2]);
                            sql = sql + " and (" + searchCrit1 + " " + splitDate[0] + " '" + splitDate[1] + "' and '" + splitDate[2] + "'";
                        } else if (splitDate[0].equals(">") || splitDate[0].equals("<") || splitDate[0].equals("=")) {
                            //JOptionPane.showMessageDialog(null, "1;2 " +splitDate[1]);
                            sql = sql + " and (" + searchCrit1 + " " + splitDate[0] + " '" + splitDate[1] + "'";
                        }
                        //sql = sql + " and " + searchCrit1 + " " + sCrit1_txt;
                    } else if (select1.equals("NULLable")) {
                        if (rbtn_NOT1.isSelected()) {
                            if (sCrit1_txt.equals("null") || sCrit1_txt.equals("NULL")) {
                                sql = sql + " and (" + searchCrit1 + " IS NOT NULL";
                            } else {
                                sql = sql + " and (" + searchCrit1 + " NOT like '%" + sCrit1_txt + "%'";
                            }
                        } else {
                            if (sCrit1_txt.equals("null") || sCrit1_txt.equals("NULL")) {
                                sql = sql + " and (" + searchCrit1 + " IS NULL";
                            } else {
                                sql = sql + " and (" + searchCrit1 + " like '%" + sCrit1_txt + "%'";
                            }
                        }
                    }
                    sql = sql +")";
                } else if (sqlStart1 == 2) {         // study
                    if (select1.equals("TXT")) {
                        if (rbtn_NOT1.isSelected()) {
                            sql2 = sql2 + " and (" + searchCrit1 + " NOT like '%" + sCrit1_txt + "%'";
                        } else {
                            sql2 = sql2 + " and (" + searchCrit1 + " like '%" + sCrit1_txt + "%'";
                        }

                    } else if (select1.equals("NULLable")) {
                        if (rbtn_NOT1.isSelected()) {
                            if (sCrit1_txt.equals("null") || sCrit1_txt.equals("NULL")) {
                                sql2 = sql2 + " and (" + searchCrit1 + " IS NOT NULL";
                            } else {
                                sql2 = sql2 + " and (" + searchCrit1 + " NOT like '%" + sCrit1_txt + "%'";
                            }
                        } else {
                            if (sCrit1_txt.equals("null") || sCrit1_txt.equals("NULL")) {
                                sql2 = sql2 + " and (" + searchCrit1 + " IS NULL";
                            } else {
                                sql2 = sql2 + " and (" + searchCrit1 + " like '%" + sCrit1_txt + "%'";
                            }
                        }
                    }
                    sql2 = sql2 +")";
                } else if (sqlStart1 == 3) {        // project
                    if (select1.equals("TXT")) {
                        if (rbtn_NOT1.isSelected()) {
                            sql3 = sql3 + " and (" + searchCrit1 + " NOT like '%" + sCrit1_txt + "%'";
                        } else {
                            sql3 = sql3 + " and (" + searchCrit1 + " like '%" + sCrit1_txt + "%'";
                        }
                    }
                    sql3 = sql3 +")";
                }
                //sql = sql +")";
                //sql2 = sql2 +")";
                //sql3 = sql3 +")";
            } // END if(rbtn_searchCrit1.isSelected())
            
            if (rbtn_searchCrit2.isSelected()) {
                String andor1 = CB_andor.getSelectedItem().toString();
                String searchCrit2_select = CB_searchCrit2.getSelectedItem().toString();
                String searchCrit2 = "";
                String sCrit2_txt = txt_searchCrit2.getText();
                String select2 = "";                
                switch (searchCrit2_select) {
                    case "pat_id":
                        searchCrit2 = "p.pat_id";
                        select2 = "ID";
                        sqlFollow = 1;
                        break;
                    case "fm_pat_no":
                        searchCrit2 = "fm_pat_no";
                        select2 = "ID";
                        sqlFollow = 1;
                        break;
                    case "fname":
                        searchCrit2 = "fname";
                        select2 = "TXT";
                        sqlFollow = 1;
                        break;
                    case "surname":
                        searchCrit2 = "surname";
                        select2 = "TXT";
                        sqlFollow = 1;
                        break;
                    case "surname old":
                        searchCrit2 = "surname_old";
                        select2 = "TXT";
                        sqlFollow = 1;
                        break;
                    case "sex":
                        searchCrit2 = "sex";
                        select2 = "TXT";
                        sqlFollow = 1;
                        break;
                    case "birth date":
                        searchCrit2 = "b_date";
                        select2 ="DATE";
                        sqlFollow = 1;
                        break;
                    case "diagnosis date":
                        searchCrit2 = "dg_date";
                        select2 = "DATE";
                        sqlFollow = 1;
                        break;
                    case "mb. down":
                        searchCrit2 = "mb_down";
                        select2 = "TXT";
                        sqlFollow = 1;
                        break;
                    case "study":
                        searchCrit2 = "stdy_name";
                        select2 = "TXT";
                        sqlFollow = 2;
                        break;
                    case "study_id":
                        searchCrit2 = "pat_study_id";
                        select2 = "TXT";
                        sqlFollow = 2;
                        break;
                    case "stdy_group":
                        searchCrit2 = "stdy_group";
                        select2 = "NULLable";
                        sqlFollow = 2;
                        break; 
                    case "monitoring":
                        searchCrit2 = "mon_pat";
                        select2 = "NULLable";
                        sqlFollow = 2;
                        break;
                    case "project":
                        searchCrit2 = "proj_name";
                        select2 = "TXT";
                        sqlFollow = 3;
                        break;
                    default:
                        break;
                }
                
                if (sqlFollow == 1) {       // patient
                    if (select2.equals("ID")) {
                        if (rbtn_NOT2.isSelected()) {
                            sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " not in ( " + sCrit2_txt + " ))";
                        } else {
                            sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " in ( " + sCrit2_txt + " ))";
                        }
                    } else if (select2.equals("TXT")) {
                        if (rbtn_NOT2.isSelected()) {
                            sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " NOT like '%" + sCrit2_txt + "%')";
                        } else {
                            sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " like '%" + sCrit2_txt + "%')";
                        }
                    } else if (select2.equals("DATE")) {
                        String splitDate[] = sCrit2_txt.split(",");          //  >=,2009-01-01   between,2009-01-01;2012-01-01

                        if (splitDate[0].equals("between")) {
                            //JOptionPane.showMessageDialog(null, "1;2;3 " +splitDate[2]);
                            sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " " + splitDate[0] + " '" + splitDate[1] + "' and '" + splitDate[2] + "')";
                        } else if (splitDate[0].equals(">") || splitDate[0].equals("<") || splitDate[0].equals("=")) {
                            //JOptionPane.showMessageDialog(null, "1;2 " +splitDate[1]);
                            sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " " + splitDate[0] + " '" + splitDate[1] + "')";
                        }
                    } else if (select2.equals("NULLable")) {
                        if (rbtn_NOT2.isSelected()) {
                            if (sCrit2_txt.equals("null") || sCrit2_txt.equals("NULL")) {
                                sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " IS NOT NULL)";
                            } else {
                                sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " NOT like '%" + sCrit2_txt + "%')";
                            }

                        } else {
                            if (sCrit2_txt.equals("null") || sCrit2_txt.equals("NULL")) {
                                sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " IS NULL)";
                            } else {
                                sqlAdd = sql + " " + andor1 + " (" + searchCrit2 + " like '%" + sCrit2_txt + "%')";
                            }
                        }
                    }
                } else if (sqlFollow == 2) {
                    if (select2.equals("TXT")) {
                        if (rbtn_NOT2.isSelected()) {
                            sql2Add = sql2 + " " + andor1 + " (" + searchCrit2 + " NOT like '%" + sCrit2_txt + "%')";
                        } else {
                            sql2Add = sql2 + " " + andor1 + " (" + searchCrit2 + " like '%" + sCrit2_txt + "%')";
                        }
                    } else if (select2.equals("NULLable")) {
                        if (rbtn_NOT2.isSelected()) {
                            if (sCrit2_txt.equals("null") || sCrit2_txt.equals("NULL")) {
                                sql2Add = sql2 + " " + andor1 + " (" + searchCrit2 + " IS NOT NULL)";
                            } else {
                                sql2Add = sql2 + " " + andor1 + " (" + searchCrit2 + " NOT like '%" + sCrit2_txt + "%')";
                            }
                        } else {
                            if (sCrit2_txt.equals("null") || sCrit2_txt.equals("NULL")) {
                                sql2Add = sql2 + " " + andor1 + " (" + searchCrit2 + " IS NULL)";
                            } else {
                                sql2Add = sql2 + " " + andor1 + " (" + searchCrit2 + " like '%" + sCrit2_txt + "%')";
                            }
                        }
                    }

                } else if (sqlFollow == 3) {
                    if (select2.equals("TXT")) {
                        if (rbtn_NOT2.isSelected()) {
                            sql3Add = sql3 + " " + andor1 + " (" + searchCrit2 + " NOT like '%" + sCrit2_txt + "%')";
                        } else {
                            sql3Add = sql3 + " " + andor1 + " (" + searchCrit2 + " like '%" + sCrit2_txt + "%')";
                        }
                    }
                }

            } /*else {        // no second criteria chosen
                sql = sql +")";
                sql2 = sql2 +")";
                sql3 = sql3 +")";
            }*/
           
            // SECOND QUERY  ... get Ids to pass to first query
            if (sqlFollow == 1) {
                IdsIn = getIdsIn(sqlAdd, 1); 
                //JOptionPane.showMessageDialog(null, "follow1-IdsIn:  "+IdsIn);
                //txtArea_test.append("\nfollow=1-IdsIn:  "+IdsIn);   //TEST
            } else if (sqlFollow == 2){
                IdsIn = getIdsIn(sql2Add, 2);
                //txtArea_test.append("\nfollow=2-IdsIn:  "+IdsIn);   //TEST
            } else if (sqlFollow == 3){
                IdsIn = getIdsIn(sql3Add, 3);
                //txtArea_test.setText("SQL3:  "+IdsIn);   //TEST
            } //else {txtArea_test.setText("__");}
            
            // FIRST QUERY
            if (sqlStart1==1){
                //txtArea_test.append("\nsqlStart=1-sql:  "+sql);   //TEST
                fillTable_patient(sql, IdsIn);
                
            } else if (sqlStart1==2){
                //txtArea_test.append("\nsqlStart=2-sql:  "+sql2);   //TEST
                fillTable_study (sql2, IdsIn);
                
            } else if (sqlStart1 ==3){
                //txtArea_test.append("\nsqlStart=3-sql:  "+sql3);   //TEST
                fillTable_project(sql3, IdsIn);  
            }
 
        }         
    }//GEN-LAST:event_btn_SearchActionPerformed

    private void CB_searchCrit1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_searchCrit1ActionPerformed
        String searchCrit_select = CB_searchCrit1.getSelectedItem().toString();
        if (searchCrit_select.equals("birth date") || searchCrit_select.equals("diagnosis date")){
            rbtn_NOT1.setEnabled(false);
        } else{
            rbtn_NOT1.setEnabled(true);
        }        
    }//GEN-LAST:event_CB_searchCrit1ActionPerformed

    private void CB_searchCrit2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_searchCrit2ActionPerformed
        String searchCrit_select = CB_searchCrit2.getSelectedItem().toString();
        if (searchCrit_select.equals("birth date") || searchCrit_select.equals("diagnosis date")){
            rbtn_NOT2.setEnabled(false);
        } else{
            rbtn_NOT2.setEnabled(true);
        }
    }//GEN-LAST:event_CB_searchCrit2ActionPerformed

    private void table_patientMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_patientMouseClicked
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_queryIDs);
            popUpResult.show(table_patient,evt.getX(),evt.getY());
            this.outTable = table_patient;
        }        
    }//GEN-LAST:event_table_patientMouseClicked

    private void cpPatIdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpPatIdsActionPerformed
        // ... copy pat_ids to clipboard
        JTable OT = this.outTable;
        String IDs = "";
        int resultL = OT.getRowCount();
        for(int i = 0; i < resultL; i++) {
            String tmp = OT.getValueAt(i, 0).toString();
            IDs = IDs + tmp + ", "; 
        }
        IDs = IDs.substring(0, (IDs.length() - 2));
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( 
                new StringSelection(IDs), null); 
    }//GEN-LAST:event_cpPatIdsActionPerformed

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
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(PatientBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PatientBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PatientBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PatientBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PatientBrowse().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CB_andor;
    private javax.swing.JComboBox<String> CB_searchCrit1;
    private javax.swing.JComboBox<String> CB_searchCrit2;
    private javax.swing.JPanel Info_top4;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_Search;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem cpPatIds;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_patient_rowsReturned;
    private javax.swing.JLabel lbl_project_rowsReturned;
    private javax.swing.JLabel lbl_study_rowsReturned;
    private javax.swing.JPopupMenu popUpResult;
    private javax.swing.JRadioButton rbtn_NOT1;
    private javax.swing.JRadioButton rbtn_NOT2;
    private javax.swing.JRadioButton rbtn_all;
    private javax.swing.JRadioButton rbtn_idCollected;
    private javax.swing.JRadioButton rbtn_searchCrit1;
    private javax.swing.JRadioButton rbtn_searchCrit2;
    private javax.swing.JRadioButton rbtn_searchCritMain;
    private javax.swing.JTable table_patient;
    private javax.swing.JTable table_patinproject;
    private javax.swing.JTable table_patinstudy;
    private javax.swing.JTextArea txtArea_test;
    private javax.swing.JTextField txt_searchCrit1;
    private javax.swing.JTextField txt_searchCrit2;
    // End of variables declaration//GEN-END:variables
}
