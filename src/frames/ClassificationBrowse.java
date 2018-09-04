/*
 * stdpat_DB - Project study patient database 
 * For efficient data evaluation and interpretation
 *
 * Copyright (C) CCRI - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Gerda modarres <gerda.modarres@ccri.at>, August 2017
 *
 */
package frames;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import myClass.CustomSorter;
import myClass.DBconnect;
import myClass.Log;
import net.proteanit.sql.DbUtils;
import myClass.IdManagement;
import static myClass.ShowSqlSelector.showSqlInWindow;

/**
 *
 * @author gerda.modarres
 */
public class ClassificationBrowse extends javax.swing.JFrame {

    String ids = null;
    String mod_sql = null;
        
    JTable outTable = null;  
    static String CB_resultIDs = null;
    static String CB_patIDs = null;
    
    static String sqlShowWindow_CB = null;
        
    Log my_log;
        
    /**
     * Creates new form PatientBrowse
     */
    public ClassificationBrowse() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        initial_table_subtypes();
        initial_table_resultID();
        Info_top4.getRootPane().setDefaultButton(btn_Search);
        
        my_log.logger.info("open SubtypeBrowse()");
        
        // TODO enable function for study patients (probably not necessary?)
        rbtn_onlyPat1.setEnabled(false);
        ComboBox_stdyPat.setEnabled(false);
    }

     private void showRows(ResultSet rs){
        try {
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SampleBrowse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    private void initial_table_subtypes(){    
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        //String sql = "SELECT * from sample";
        String sql = "SELECT * FROM subtypes";
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_RgClassLab);
            
            if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
            }
            
            //get_ids(sql,pst,rs,conn);
            this.ids=IdManagement.get_ids(sql, pst, rs, conn,"pat_id");
            
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
    
     private void initial_table_resultID(){    
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT p.pat_id, m.result_id, m.lab_id FROM main_result m, sample s, patient p, subtypes t"
                + " Where m.lab_id=s.lab_id and s.pat_id=p.pat_id and t.pat_id=p.pat_id;";
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_resultID.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_resultID);
           
            if (table_resultID.getColumnModel().getColumnCount() > 0) {
                table_resultID.getColumnModel().getColumn(0).setPreferredWidth(80);
                table_resultID.getColumnModel().getColumn(0).setMaxWidth(100);
                table_resultID.getColumnModel().getColumn(1).setPreferredWidth(80);
                table_resultID.getColumnModel().getColumn(1).setMaxWidth(100);
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
     
    private void update_table_resultID() {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String all_ids = this.ids;
                                       
        if (all_ids.length() > 1) {
            all_ids = all_ids.substring(0, (all_ids.length() - 1));
        } else {
            //JOptionPane.showMessageDialog(null, "no IDs");
            all_ids = "0";
        }

        String sql = "SELECT p.pat_id, m.result_id, m.lab_id FROM main_result m, sample s, patient p"
                + " Where m.lab_id=s.lab_id and s.pat_id=p.pat_id"
                //+ " AND m.lab_id in (" + all_ids + ")";         
                + " AND p.pat_id in (" + all_ids + ")";

        // only for a selected patient group
        if (rbtn_onlyPat.isSelected()) {
            deliver_Proj_ids(sql, "F");
            sql = this.mod_sql;
        }
        //TODO
        //if (rbtn_onlyPat1.isSelected()){
        //    deliver_Stdy_ids(sql, "F");
        //    sql = this.mod_sql;
        //} 
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            table_resultID.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_resultID);

            if (table_resultID.getColumnModel().getColumnCount() > 0) {
                table_resultID.getColumnModel().getColumn(0).setPreferredWidth(80);
                table_resultID.getColumnModel().getColumn(0).setMaxWidth(100);
                table_resultID.getColumnModel().getColumn(1).setPreferredWidth(80);
                table_resultID.getColumnModel().getColumn(1).setMaxWidth(100);
            }

            //get_r_ids(sql,pst,rs,conn);   // old --> substitute:
            //this.CB_resultIDs = IdManagement.get_r_ids(sql,pst,rs,conn); // TEST classIdManagement
            this.CB_resultIDs = IdManagement.get_ids(sql, pst, rs, conn, "result_id");
            this.CB_patIDs = IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
            showRows(rs);

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
        //}
    }
        
    private static boolean isRightClick(MouseEvent e) {
        return (e.getButton() == MouseEvent.BUTTON3
                || (System.getProperty("os.name").contains("Mac OS X")
                && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0
                && (e.getModifiers() & InputEvent.CTRL_MASK) != 0));
    }
    
    private void deliver_Proj_ids(String sql, String set){ 
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        String proj_id = "";
        //String projPat = (String) ComboBox_projPat.getSelectedItem();
        if(ComboBox_projPat.getSelectedItem().toString().equals("MS_ALL_Array_Diagnostics")){
            proj_id = "1";
        }else if(ComboBox_projPat.getSelectedItem().toString().equals("Paper Dworzak Pickl")){
            proj_id = "2";
        }else if(ComboBox_projPat.getSelectedItem().toString().equals("TEST")){
            proj_id = "3";
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

            //get_ids(sql2, pst, rs, conn);
            this.ids = IdManagement.get_ids(sql2, pst, rs, conn, "result_id");   
            
            if (ids.length() > 1){
                ids = ids.substring(0, (ids.length() - 1));
                if (set.equals("A")){
                    this.mod_sql = sql + " AND m.result_id in(" + ids + ")";   
                } else if (set.equals("F")){
                    this.mod_sql = sql + " AND result_id in(" + ids + ")";     
                }
            } else{
                JOptionPane.showMessageDialog(null, "Something is wrong with project patient's id list!");
            }
            //display_ids();
            
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
        
    private void update_table_RgClassLab(String sqlDropped, String prefix) {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = sqlDropped;
        
        String SelectedPat_ids = this.CB_patIDs;
        if (SelectedPat_ids.length() < 1) {
            SelectedPat_ids = "0,";
        }

        boolean bPrefix = false;
        switch (prefix) {
            case "true":
                bPrefix = true;
                break;
            case "false":
                bPrefix = false;
                break;
            default:
                break;
        }

        if (bPrefix==true){
            SelectedPat_ids = SelectedPat_ids.substring(0, (SelectedPat_ids.length() - 1));
            sql = sql + " and p.pat_id in ( " + SelectedPat_ids + " );";
        }else if (bPrefix==false){
            SelectedPat_ids = SelectedPat_ids.substring(0, (SelectedPat_ids.length() - 1));
            sql = sql + " and pat_id in ( " + SelectedPat_ids + " );";
        }
        //txtArea_test.setText(sql);        //TEST
        showSqlInWindow(sql, "Classification_ProjPat");
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_RgClassLab);

            if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
            }
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
        cpResultIds = new javax.swing.JMenuItem();
        cpLabIds = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        bnt_test = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_RgClassLab = new javax.swing.JTable();
        Info_top4 = new javax.swing.JPanel();
        btn_Search = new javax.swing.JButton();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        rbtn_cytology = new javax.swing.JRadioButton();
        rbtn_NOT3_1 = new javax.swing.JRadioButton();
        txt_cytology = new javax.swing.JTextField();
        rbtn_CYT = new javax.swing.JRadioButton();
        CB_cytology = new javax.swing.JComboBox<>();
        CB_andor3 = new javax.swing.JComboBox<>();
        rbtn_CYT1 = new javax.swing.JRadioButton();
        CB_cytology1 = new javax.swing.JComboBox<>();
        rbtn_NOT3_2 = new javax.swing.JRadioButton();
        txt_cytology1 = new javax.swing.JTextField();
        rbtn_all_cytology = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        rbtn_TODO = new javax.swing.JRadioButton();
        rbtn_NOT4_1 = new javax.swing.JRadioButton();
        txt_RG4 = new javax.swing.JTextField();
        rbtn_specST6 = new javax.swing.JRadioButton();
        CB_RG4 = new javax.swing.JComboBox<>();
        CB_andor4 = new javax.swing.JComboBox<>();
        rbtn_specST7 = new javax.swing.JRadioButton();
        CB_RG5 = new javax.swing.JComboBox<>();
        rbtn_NOT4_2 = new javax.swing.JRadioButton();
        txt_RG5 = new javax.swing.JTextField();
        rbtn_all_TODO = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        rbtn_subtype = new javax.swing.JRadioButton();
        rbtn_NOT1_1 = new javax.swing.JRadioButton();
        txt_specST = new javax.swing.JTextField();
        rbtn_specST = new javax.swing.JRadioButton();
        CB_specST = new javax.swing.JComboBox<>();
        CB_andor1 = new javax.swing.JComboBox<>();
        rbtn_specST1 = new javax.swing.JRadioButton();
        CB_specST1 = new javax.swing.JComboBox<>();
        rbtn_NOT1_2 = new javax.swing.JRadioButton();
        txt_specST1 = new javax.swing.JTextField();
        rbtn_all_subtypes = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        rbtn_class = new javax.swing.JRadioButton();
        rbtn_NOT2_1 = new javax.swing.JRadioButton();
        txt_class = new javax.swing.JTextField();
        rbtn_RG = new javax.swing.JRadioButton();
        CB_class = new javax.swing.JComboBox<>();
        CB_andor2 = new javax.swing.JComboBox<>();
        rbtn_RG1 = new javax.swing.JRadioButton();
        CB_class1 = new javax.swing.JComboBox<>();
        rbtn_NOT2_2 = new javax.swing.JRadioButton();
        txt_class1 = new javax.swing.JTextField();
        rbtn_all_class = new javax.swing.JRadioButton();
        jLabel7 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        rbtn_onlyPat = new javax.swing.JRadioButton();
        ComboBox_projPat = new javax.swing.JComboBox<>();
        rbtn_onlyPat1 = new javax.swing.JRadioButton();
        ComboBox_stdyPat = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_resultID = new javax.swing.JTable();
        lbl_rowsReturned = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        cpResultIds.setText("copy result_ids ...");
        cpResultIds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpResultIdsActionPerformed(evt);
            }
        });
        popUpResult.add(cpResultIds);

        cpLabIds.setText("copy lab_ids ...\n");
        cpLabIds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpLabIdsActionPerformed(evt);
            }
        });
        popUpResult.add(cpLabIds);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Linked Results Analysis Tool - browse classification");

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

        table_RgClassLab.setAutoCreateRowSorter(true);
        table_RgClassLab.setModel(new javax.swing.table.DefaultTableModel(
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
        table_RgClassLab.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(table_RgClassLab);
        table_RgClassLab.getAccessibleContext().setAccessibleName("table_subtypes");
        table_RgClassLab.getAccessibleContext().setAccessibleDescription("");

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

        jTabbedPane2.setBackground(new java.awt.Color(102, 153, 255));
        jTabbedPane2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel3.setBackground(new java.awt.Color(102, 153, 255));

        buttonGroup1.add(rbtn_cytology);
        rbtn_cytology.setText("s. with ...");
        rbtn_cytology.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_cytology.setBorderPainted(true);
        rbtn_cytology.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_cytologyActionPerformed(evt);
            }
        });

        rbtn_NOT3_1.setText("NOT");
        rbtn_NOT3_1.setEnabled(false);

        txt_cytology.setBackground(new java.awt.Color(204, 204, 204));
        txt_cytology.setEnabled(false);

        rbtn_CYT.setEnabled(false);

        CB_cytology.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "fab_class", "% blast PB", "% blast BM", "eval", "summary" }));
        CB_cytology.setEnabled(false);
        CB_cytology.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_cytologyActionPerformed(evt);
            }
        });

        CB_andor3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor3.setEnabled(false);

        rbtn_CYT1.setEnabled(false);

        CB_cytology1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "fab_class", "% blast PB", "% blast BM", "eval", "summary" }));
        CB_cytology1.setEnabled(false);
        CB_cytology1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_cytology1ActionPerformed(evt);
            }
        });

        rbtn_NOT3_2.setText("NOT");
        rbtn_NOT3_2.setEnabled(false);

        txt_cytology1.setBackground(new java.awt.Color(204, 204, 204));
        txt_cytology1.setEnabled(false);

        buttonGroup1.add(rbtn_all_cytology);
        rbtn_all_cytology.setText("all samples");
        rbtn_all_cytology.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all_cytology.setBorderPainted(true);
        rbtn_all_cytology.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_all_cytologyActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("cytology");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(rbtn_NOT3_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_cytology1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbtn_all_cytology, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(rbtn_CYT, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(rbtn_CYT1, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(CB_cytology, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                            .addGap(3, 3, 3)
                                            .addComponent(CB_cytology1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(rbtn_cytology, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(rbtn_NOT3_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_cytology, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(142, 142, 142)
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(5, 5, 5))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_all_cytology)
                            .addComponent(jLabel3))
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_cytology)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT3_1)
                            .addComponent(rbtn_CYT)
                            .addComponent(txt_cytology, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_cytology, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT3_2)
                            .addComponent(rbtn_CYT1)
                            .addComponent(txt_cytology1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_cytology1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CB_andor3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor3, CB_cytology, CB_cytology1, rbtn_CYT, rbtn_CYT1, rbtn_NOT3_1, rbtn_NOT3_2});

        jTabbedPane2.addTab("cytology", jPanel3);

        jPanel4.setBackground(new java.awt.Color(102, 153, 255));

        buttonGroup1.add(rbtn_TODO);
        rbtn_TODO.setText("s. with ...");
        rbtn_TODO.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_TODO.setBorderPainted(true);
        rbtn_TODO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_TODOActionPerformed(evt);
            }
        });

        rbtn_NOT4_1.setText("NOT");
        rbtn_NOT4_1.setEnabled(false);

        txt_RG4.setBackground(new java.awt.Color(204, 204, 204));
        txt_RG4.setEnabled(false);

        rbtn_specST6.setEnabled(false);

        CB_RG4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "parameter 1", "parameter 2", "parameter 3" }));
        CB_RG4.setEnabled(false);

        CB_andor4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor4.setEnabled(false);

        rbtn_specST7.setEnabled(false);

        CB_RG5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "parameter 1", "parameter 2", "parameter 3" }));
        CB_RG5.setEnabled(false);

        rbtn_NOT4_2.setText("NOT");
        rbtn_NOT4_2.setEnabled(false);

        txt_RG5.setBackground(new java.awt.Color(204, 204, 204));
        txt_RG5.setEnabled(false);

        buttonGroup1.add(rbtn_all_TODO);
        rbtn_all_TODO.setText("all samples");
        rbtn_all_TODO.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all_TODO.setBorderPainted(true);
        rbtn_all_TODO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_all_TODOActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("someLab");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(rbtn_NOT4_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_RG5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbtn_all_TODO, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(rbtn_specST6, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(rbtn_specST7, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(CB_RG4, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                            .addGap(3, 3, 3)
                                            .addComponent(CB_RG5, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(rbtn_TODO, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(rbtn_NOT4_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_RG4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(131, 131, 131)
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(5, 5, 5))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_all_TODO)
                            .addComponent(jLabel4))
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_TODO)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT4_1)
                            .addComponent(rbtn_specST6)
                            .addComponent(txt_RG4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_RG4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT4_2)
                            .addComponent(rbtn_specST7)
                            .addComponent(txt_RG5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_RG5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CB_andor4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_RG4, CB_RG5, CB_andor4, rbtn_NOT4_1, rbtn_NOT4_2, rbtn_specST6, rbtn_specST7});

        jTabbedPane2.addTab("someLab", jPanel4);

        jTabbedPane1.setBackground(new java.awt.Color(102, 153, 255));
        jTabbedPane1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jPanel1.setBackground(new java.awt.Color(102, 153, 255));

        buttonGroup1.add(rbtn_subtype);
        rbtn_subtype.setText("s. with subtype");
        rbtn_subtype.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_subtype.setBorderPainted(true);
        rbtn_subtype.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_subtypeActionPerformed(evt);
            }
        });

        rbtn_NOT1_1.setText("NOT");
        rbtn_NOT1_1.setEnabled(false);

        txt_specST.setBackground(new java.awt.Color(204, 204, 204));
        txt_specST.setEnabled(false);

        rbtn_specST.setEnabled(false);

        CB_specST.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "maj. subtype", "b-oth. subtype", "spec. subt. 1", "spec. subt. 2", "spec. subt. 3" }));
        CB_specST.setEnabled(false);

        CB_andor1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor1.setEnabled(false);

        rbtn_specST1.setEnabled(false);

        CB_specST1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "maj. subtype", "b-oth. subtype", "spec. subt. 1", "spec. subt. 2", "spec. subt. 3" }));
        CB_specST1.setEnabled(false);

        rbtn_NOT1_2.setText("NOT");
        rbtn_NOT1_2.setEnabled(false);

        txt_specST1.setBackground(new java.awt.Color(204, 204, 204));
        txt_specST1.setEnabled(false);

        buttonGroup1.add(rbtn_all_subtypes);
        rbtn_all_subtypes.setSelected(true);
        rbtn_all_subtypes.setText("all samples");
        rbtn_all_subtypes.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all_subtypes.setBorderPainted(true);
        rbtn_all_subtypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_all_subtypesActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel1.setText("subtypes");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(rbtn_specST, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(rbtn_specST1, javax.swing.GroupLayout.Alignment.TRAILING))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(CB_specST, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                                .addGap(3, 3, 3)
                                                .addComponent(CB_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                    .addComponent(rbtn_subtype, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(5, 5, 5)
                                .addComponent(rbtn_NOT1_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_specST, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(rbtn_NOT1_2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(5, 5, 5))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(rbtn_all_subtypes, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10))))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rbtn_specST, rbtn_specST1});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rbtn_all_subtypes, rbtn_subtype});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_specST, txt_specST1});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CB_specST, CB_specST1});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_all_subtypes)
                            .addComponent(jLabel1))
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_subtype)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT1_1)
                            .addComponent(rbtn_specST)
                            .addComponent(txt_specST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_specST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT1_2)
                            .addComponent(rbtn_specST1)
                            .addComponent(txt_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CB_andor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_specST, rbtn_specST});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor1, CB_specST1, rbtn_specST1});

        rbtn_subtype.getAccessibleContext().setAccessibleName("subtypes");

        jTabbedPane1.addTab("subtypes", jPanel1);

        jPanel7.setBackground(new java.awt.Color(102, 153, 255));

        buttonGroup1.add(rbtn_class);
        rbtn_class.setText("s. with ...");
        rbtn_class.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_class.setBorderPainted(true);
        rbtn_class.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_classActionPerformed(evt);
            }
        });

        rbtn_NOT2_1.setText("NOT");
        rbtn_NOT2_1.setEnabled(false);

        txt_class.setBackground(new java.awt.Color(204, 204, 204));
        txt_class.setEnabled(false);

        rbtn_RG.setEnabled(false);

        CB_class.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "riskgroup", "mrd riskgroup", "prd", "fcm mrd", "immuno pickl", "immuno dworzak", "BAL dworzak", "MPAL dworzak", "MPAL pickl", "FAB" }));
        CB_class.setEnabled(false);
        CB_class.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_classActionPerformed(evt);
            }
        });

        CB_andor2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor2.setEnabled(false);

        rbtn_RG1.setEnabled(false);

        CB_class1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "riskgroup", "mrd riskgroup", "prd", "fcm mrd", "immuno pickl", "immuno dworzak", "BAL dworzak", "MPAL dworzak", "MPAL pickl", "FAB" }));
        CB_class1.setEnabled(false);
        CB_class1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_class1ActionPerformed(evt);
            }
        });

        rbtn_NOT2_2.setText("NOT");
        rbtn_NOT2_2.setEnabled(false);

        txt_class1.setBackground(new java.awt.Color(204, 204, 204));
        txt_class1.setEnabled(false);

        buttonGroup1.add(rbtn_all_class);
        rbtn_all_class.setText("all samples");
        rbtn_all_class.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all_class.setBorderPainted(true);
        rbtn_all_class.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_all_classActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(0, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("classification");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addComponent(rbtn_NOT2_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_class1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbtn_all_class, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel7Layout.createSequentialGroup()
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(rbtn_RG, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(rbtn_RG1, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel7Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(CB_class, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                            .addGap(3, 3, 3)
                                            .addComponent(CB_class1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(rbtn_class, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addComponent(rbtn_NOT2_1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_class, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGap(107, 107, 107)
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(5, 5, 5))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_all_class)
                            .addComponent(jLabel7))
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_class)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT2_1)
                            .addComponent(rbtn_RG)
                            .addComponent(txt_class, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_class, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT2_2)
                            .addComponent(rbtn_RG1)
                            .addComponent(txt_class1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_class1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CB_andor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor2, CB_class, CB_class1, rbtn_NOT2_1, rbtn_NOT2_2, rbtn_RG, rbtn_RG1});

        jTabbedPane1.addTab("classification", jPanel7);

        jPanel2.setBackground(new java.awt.Color(102, 153, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        rbtn_onlyPat.setText("only patients from project ...");
        rbtn_onlyPat.setToolTipText("select to get results from patients in a certain study (select from below)");

        ComboBox_projPat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MS_ALL_Array_Diagnostics", "Paper Dworzak Pickl", "TEST", "no project assigned" }));

        rbtn_onlyPat1.setText("only patients from study ...");
        rbtn_onlyPat1.setToolTipText("select to get results from patients in a certain project (select from below)");

        ComboBox_stdyPat.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ALL BFM 2009", "Register paedMyLeu BFM-A 2014", "ALL BFM 2000", "ALL Rezidiv", "no study assigned" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtn_onlyPat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ComboBox_projPat, 0, 0, Short.MAX_VALUE)
                            .addComponent(ComboBox_stdyPat, 0, 0, Short.MAX_VALUE)))
                    .addComponent(rbtn_onlyPat1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(rbtn_onlyPat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_projPat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtn_onlyPat1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ComboBox_stdyPat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout Info_top4Layout = new javax.swing.GroupLayout(Info_top4);
        Info_top4.setLayout(Info_top4Layout);
        Info_top4Layout.setHorizontalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 77, Short.MAX_VALUE)
                .addComponent(btn_Search)
                .addGap(21, 21, 21))
        );
        Info_top4Layout.setVerticalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Info_top4Layout.createSequentialGroup()
                        .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(btn_Search)
                                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        table_resultID.setAutoCreateRowSorter(true);
        table_resultID.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "pat_id", "result_id", "lab_id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Long.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table_resultID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_resultIDMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(table_resultID);
        table_resultID.getAccessibleContext().setAccessibleName("table_resultID");

        lbl_rowsReturned.setText(" ");

        jMenu4.setBorder(null);
        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_Font_small07_web.png"))); // NOI18N
        jMenu4.setMargin(new java.awt.Insets(0, 0, 0, 5));
        jMenuBar1.add(jMenu4);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        jMenuItem1.setText("how to use");
        jMenu3.add(jMenuItem1);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

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
                        .addComponent(jScrollPane1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(lbl_rowsReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(lbl_rowsReturned)
                .addGap(11, 11, 11))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void btn_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SearchActionPerformed
        if(rbtn_all_subtypes.isSelected()){
            //rbtn_specST.setEnabled(false);
            initial_table_subtypes();
            update_table_resultID();
            if (rbtn_onlyPat.isSelected()){
                String sql = "SELECT distinct t.auto_id, t.pat_id, major_subtype, bother_subtype, spec_sub1, spec_sub2, spec_sub3, ngs_sub1, ngs_sub2 FROM sample s, patient p, subtypes t"
                    + " where s.pat_id=p.pat_id"
                    + " and t.pat_id=p.pat_id";
                update_table_RgClassLab(sql,"true");          // Test second table - only project
            } 
        } else if (rbtn_subtype.isSelected()){
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            
            String sql = "SELECT distinct t.auto_id, t.pat_id, major_subtype, bother_subtype, spec_sub1, spec_sub2, spec_sub3, ngs_sub1, ngs_sub2 FROM sample s, patient p, subtypes t"
                + " where s.pat_id=p.pat_id"
                + " and t.pat_id=p.pat_id";

            if (rbtn_specST.isSelected()) {
                String specST_select = CB_specST.getSelectedItem().toString();
                String specST = "";
                switch (specST_select) { 
                    case "maj. subtype":
                        specST = "major_subtype";
                        break;
                    case "b-oth. subtype":
                        specST = "bother_subtype";
                        break;
                    case "spec. subt. 1":
                        specST = "spec_sub1";
                        break;
                    case "spec. subt. 2":
                        specST = "spec_sub2";
                        break;
                    case "spec. subt. 3":
                        specST = "spec_sub3";
                        break;
                    default:
                        break;
                }
                String spec_txt = txt_specST.getText();
                
                if (rbtn_NOT1_1.isSelected()) {
                    sql = sql + " and (" + specST + " NOT like '%" + spec_txt + "%'";
                } else {               
                    sql = sql + " and (" + specST + " like '%" + spec_txt + "%'";
                }
            }
           
            if (rbtn_specST1.isSelected()) {
                String andor1 = CB_andor1.getSelectedItem().toString();
                String specST_select1 = CB_specST1.getSelectedItem().toString();
                String specST1 = "";
                switch (specST_select1) { 
                    case "maj. subtype":
                        specST1 = "major_subtype";
                        break;
                    case "b-oth. subtype":
                        specST1 = "bother_subtype";
                        break;
                    case "spec. subt. 1":
                        specST1 = "spec_sub1";
                        break;
                    case "spec. subt. 2":
                        specST1 = "spec_sub2";
                        break;
                    case "spec. subt. 3":
                        specST1 = "spec_sub3";
                        break;
                    default:
                        break;
                }
                String spec_txt1 = txt_specST1.getText();
                
                if (rbtn_NOT1_2.isSelected()) {
                    sql = sql + " " + andor1 + " " + specST1 + " NOT like '%" + spec_txt1 + "%')";
                } else {               
                    sql = sql + " " + andor1 + " " + specST1 + " like '%" + spec_txt1 + "%')";
                }
            }else {
                sql = sql + ")"; 
            }    
           
            //txtArea_test.setText(sql);    //TEST
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                my_log.logger.info("SQL:  " + sql);
                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_RgClassLab);

                if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                    table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                }
//XXX
                //get_ids(sql, pst, rs, conn);
                this.ids=IdManagement.get_ids(sql, pst, rs, conn,"pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_subtypes");
                
                if (rbtn_onlyPat.isSelected()){
                    update_table_RgClassLab(sql,"true");          // Test second table - only project
                }

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
         } else if (rbtn_all_class.isSelected()) {
             Connection conn = DBconnect.ConnecrDb();
             ResultSet rs = null;
             PreparedStatement pst = null;
             String sql = "SELECT auto_id, pat_id, rg, mrd_rg, prd, fcm_mrd, immuno_pickl, immuno_dworzak, BAL_dworzak as BAL, MPAL_dworzak as MPAL, MPAL_pickl, FAB FROM pat_instudy where 1=1";

             try {
                 pst = conn.prepareStatement(sql);
                 rs = pst.executeQuery();

                 table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                 CustomSorter.table_customRowSort(table_RgClassLab);

                 if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                     table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                     table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                     table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                     table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                 }
            
                //get_ids(sql,pst,rs,conn);
                this.ids=IdManagement.get_ids(sql, pst, rs, conn,"pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_classification");
                
                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql,"false");          // Test second table - only project
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
        } else if (rbtn_class.isSelected()) {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            String sql = "SELECT auto_id, pat_id, rg, mrd_rg, prd, fcm_mrd, immuno_pickl, immuno_dworzak ,BAL_dworzak as BAL, MPAL_dworzak as MPAL, MPAL_pickl, FAB FROM pat_instudy where 1=1";

            if (rbtn_RG.isSelected()) {
                String class_select = CB_class.getSelectedItem().toString();
                String classRG = "";
                
                switch (class_select) { 
                    case "riskgroup":
                        classRG = "rg";
                        break;
                    case "mrd riskgroup":
                        classRG = "mrd_rg";
                        break;
                    case "prd":
                        classRG = "prd";
                        break;
                    case "fcm mrd":
                        classRG = "fcm_mrd";
                        break;
                    case "immuno pickl":
                        classRG = "immuno_pickl";
                        break;
                    case "immuno dworzak":
                        classRG = "immuno_dworzak";
                        break;
                    case "BAL dworzak":
                        classRG = "BAL_dworzak";
                        break;    
                    case "MPAL dworzak":
                        classRG = "MPAL_dworzak";
                        break;     
                    case "MPAL pickl":
                        classRG = "MPAL_pickl";
                        break; 
                    case "FAB":
                        classRG = "FAB";
                        break;
                    default:
                        break;
                }
                String class_txt = txt_class.getText();
                if (classRG.equals("fcm_mrd")) {
                    sql = sql + " and (" + classRG + " " + class_txt;
                } else {
                    if (rbtn_NOT2_1.isSelected()) {
                        if (class_txt.equals("null") || class_txt.equals("NULL")) {
                            sql = sql + " and (" + classRG + " IS NOT NULL";
                        } else {
                            sql = sql + " and (" + classRG + " NOT like '%" + class_txt + "%'";
                        }
                    } else {
                        if (class_txt.equals("null") || class_txt.equals("NULL")) {
                            sql = sql + " and (" + classRG + " IS NULL";
                        } else {
                            sql = sql + " and (" + classRG + " like '%" + class_txt + "%'";
                        }
                    }
                }
            }

            if (rbtn_RG1.isSelected()) {
                String andor2 = CB_andor2.getSelectedItem().toString();
                String class_select1 = CB_class1.getSelectedItem().toString();
                String classRG1 = "";
                
                switch (class_select1) { 
                    case "riskgroup":
                        classRG1 = "rg";
                        break;
                    case "mrd riskgroup":
                        classRG1 = "mrd_rg";
                        break;
                    case "prd":
                        classRG1 = "prd";
                        break;
                    case "fcm mrd":
                        classRG1 = "fcm_mrd";
                        break;
                    case "immuno pickl":
                        classRG1 = "immuno_pickl";
                        break;
                    case "immuno dworzak":
                        classRG1 = "immuno_dworzak";
                        break;
                        case "BAL dworzak":
                        classRG1 = "BAL_dworzak";
                        break;    
                    case "MPAL dworzak":
                        classRG1 = "MPAL_dworzak";
                        break;     
                    case "MPAL pickl":
                        classRG1 = "MPAL_pickl";
                        break; 
                    case "FAB":
                        classRG1 = "FAB";
                        break;
                    default:
                        break;
                }
                String class_txt1 = txt_class1.getText();            
                
                if (classRG1.equals("fcm_mrd")) {
                    sql = sql + " "+ andor2 + " " + classRG1 + " " + class_txt1 +")";
                } else {
                    if (rbtn_NOT2_2.isSelected()) {
                        if (class_txt1.equals("null") || class_txt1.equals("NULL")) {
                            sql = sql + " " + andor2 + " " + classRG1 + " IS NOT NULL)";
                        } else {
                            sql = sql + " " + andor2 + " " + classRG1 + " NOT like '%" + class_txt1 + "%')";
                        }
                    } else {
                        if (class_txt1.equals("null") || class_txt1.equals("NULL")) {
                            sql = sql + " " + andor2 + " " + classRG1 + " IS NULL)";
                        } else {
                            sql = sql + " " + andor2 + " " + classRG1 + " like '%" + class_txt1 + "%')";
                        }
                    }
                }
    
            } else {
                sql = sql + ")"; 
            }           
            //txtArea_test.setText(sql);    //TEST

            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                my_log.logger.info("SQL:  " + sql);
                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_RgClassLab);

                if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                    table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                }
                //get_ids(sql, pst, rs, conn);
                this.ids=IdManagement.get_ids(sql, pst, rs, conn,"pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_classification");

                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql,"false");          // Test second table - only project
                }

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
        } else if (rbtn_all_cytology.isSelected()) {
             Connection conn = DBconnect.ConnecrDb();
             ResultSet rs = null;
             PreparedStatement pst = null;
             //String sql = "SELECT * from cytology_result";
             String sql = "SELECT pat_id, cyto_auto_ID as cytoID, fab_class, prcnt_blast_km as `% blast BM`, prcnt_blast_pb as `% blast PB`, eval, summ FROM cytology_result c, main_result m, sample s"
                     + " WHERE c.result_id=m.result_id "
                     + " AND s.lab_id=m.lab_id";

             try {
                 pst = conn.prepareStatement(sql);
                 rs = pst.executeQuery();

                 table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                 CustomSorter.table_customRowSort(table_RgClassLab);

                 if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                     table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                     table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                     table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                     table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                     table_RgClassLab.getColumnModel().getColumn(2).setPreferredWidth(80);
                     table_RgClassLab.getColumnModel().getColumn(2).setMaxWidth(100);
                     table_RgClassLab.getColumnModel().getColumn(3).setPreferredWidth(80);
                     table_RgClassLab.getColumnModel().getColumn(3).setMaxWidth(100);
                     table_RgClassLab.getColumnModel().getColumn(4).setPreferredWidth(80);
                     table_RgClassLab.getColumnModel().getColumn(4).setMaxWidth(100);
                 }
            
                //get_ids(sql,pst,rs,conn);
                this.ids=IdManagement.get_ids(sql, pst, rs, conn,"pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_cytology");

                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql,"false");          // Test second table - only project
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
            //update_table_resultID();
        }else if (rbtn_cytology.isSelected()) {
             Connection conn = DBconnect.ConnecrDb();
             ResultSet rs = null;
             PreparedStatement pst = null;

             String sql = "SELECT pat_id, cyto_auto_ID as cytoID, fab_class, prcnt_blast_km as `% blast BM`, prcnt_blast_pb as `% blast PB`, eval, summ FROM cytology_result c, main_result m, sample s"
                     + " WHERE c.result_id=m.result_id "
                     + " AND s.lab_id=m.lab_id";

             if (rbtn_CYT.isSelected()) {
                 String cyt_select = CB_cytology.getSelectedItem().toString();
                 String cytology = "";
                 switch (cyt_select) {
                     case "fab_class":
                         cytology = "fab_class";
                         break;
                     case "% blast PB":
                         cytology = "prcnt_blast_pb";
                         break;
                     case "% blast BM":
                         cytology = "prcnt_blast_km";
                         break;
                     case "eval":
                         cytology = "eval";
                         break;
                     case "summary":
                         cytology = "summ";
                         break;
                     default:
                         break;
                 }
                 String CYT_txt = txt_cytology.getText();
                 if (cytology.equals("prcnt_blast_pb") || cytology.equals("prcnt_blast_km")) {
                     sql = sql + " and (" + cytology + " " + CYT_txt;
                 } else {
                     if (rbtn_NOT3_1.isSelected()) {
                         if (CYT_txt.equals("null") || CYT_txt.equals("NULL")) {
                             sql = sql + " and (" + cytology + " IS NOT NULL";
                         } else {
                             sql = sql + " and (" + cytology + " NOT like '%" + CYT_txt + "%'";
                         }
                     } else {
                         if (CYT_txt.equals("null") || CYT_txt.equals("NULL")) {
                             sql = sql + " and (" + cytology + " IS NULL";
                         } else {
                             sql = sql + " and (" + cytology + " like '%" + CYT_txt + "%'";
                         }
                     }
                 }
             }
                          
            if (rbtn_CYT1.isSelected()) {
                String andor3 = CB_andor3.getSelectedItem().toString();
                String cyt_select1 = CB_cytology1.getSelectedItem().toString();
                String cytology1 = "";
                switch (cyt_select1) { 
                    case "fab_class":
                        cytology1 = "fab_class";
                        break;
                    case "% blast PB":
                        cytology1 = "prcnt_blast_pb";
                        break;
                    case "% blast BM":
                        cytology1 = "prcnt_blast_km";
                        break;
                    case "eval":
                        cytology1 = "eval";
                        break;
                    case "summary":
                        cytology1 = "summ";
                        break;
                    default:
                        break;
                }
                String CYT_txt1 = txt_cytology1.getText();
                
                if (cytology1.equals("prcnt_blast_pb")|| cytology1.equals("prcnt_blast_km") ){
                    sql = sql + " " + andor3 + " " + cytology1 + " " + CYT_txt1 +")" ;
                } else {
                    
                    if (rbtn_NOT3_2.isSelected()) {
                         if (CYT_txt1.equals("null") || CYT_txt1.equals("NULL")) {
                             sql = sql + " " + andor3 + " " + cytology1 + " IS NOT NULL)";
                         } else {
                             sql = sql + " " + andor3 + " " + cytology1 + " NOT like '%" + CYT_txt1 + "%')";
                         }
                     } else {
                         if (CYT_txt1.equals("null") || CYT_txt1.equals("NULL")) {
                             sql = sql + " " + andor3 + " " + cytology1 + " IS NULL)";
                         } else {
                             sql = sql + " " + andor3 + " " + cytology1 + " like '%" + CYT_txt1 + "%')";
                         }
                     }
                    
                }
            } else {
                sql = sql + ")";
            }
            //txtArea_test.setText(sql);    //TEST

            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                my_log.logger.info("SQL:  " + sql);
                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_RgClassLab);

                if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                    table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_RgClassLab.getColumnModel().getColumn(2).setPreferredWidth(80);
                    table_RgClassLab.getColumnModel().getColumn(2).setMaxWidth(100);
                    table_RgClassLab.getColumnModel().getColumn(3).setPreferredWidth(80);
                    table_RgClassLab.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_RgClassLab.getColumnModel().getColumn(4).setPreferredWidth(80);
                    table_RgClassLab.getColumnModel().getColumn(4).setMaxWidth(100);
                }
                //get_ids(sql, pst, rs, conn);
                this.ids = IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_cytology");

                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql,"false");          // Test second table - only project
                }

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
  
    }//GEN-LAST:event_btn_SearchActionPerformed

    private void table_resultIDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_resultIDMouseClicked
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_queryIDs);
            popUpResult.show(table_resultID,evt.getX(),evt.getY());
            this.outTable = table_resultID;
        }        
    }//GEN-LAST:event_table_resultIDMouseClicked

    private void cpResultIdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpResultIdsActionPerformed
        // TEST ... copy result_ids to clipboard
        JTable OT = this.outTable;
        String IDs = "";
        int resultL = OT.getRowCount();
        for(int i = 0; i < resultL; i++) {
            String tmp = OT.getValueAt(i, 1).toString();
            IDs = IDs + tmp + ", "; 
        }
        IDs = IDs.substring(0, (IDs.length() - 2));
        //txtArea_test.setText(IDs);    //TEST
        
        //Toolkit.getDefaultToolkit().getSystemClipboard().setContents( 
        //        new StringSelection(IDs), null);
        //StringSelection selection = new StringSelection(IDs);
        //Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //clipboard.setContents(selection, selection);
        StringSelection somestring = new StringSelection(IDs);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(somestring, null);
    }//GEN-LAST:event_cpResultIdsActionPerformed

    private void cpLabIdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpLabIdsActionPerformed
        // TEST ... copy lab_ids to clipboard
        JTable OT = this.outTable;
        String IDs = "";
        int resultL = OT.getRowCount();
        for(int i = 0; i < resultL; i++) {
            String tmp = OT.getValueAt(i, 2).toString();
            IDs = IDs + tmp + ", "; 
        }
        IDs = IDs.substring(0, (IDs.length() - 2));
        //txtArea_test.setText(IDs);    //TEST
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( 
                new StringSelection(IDs), null);       
    }//GEN-LAST:event_cpLabIdsActionPerformed

    private void rbtn_subtypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_subtypeActionPerformed
        if (rbtn_subtype.isSelected()){            
            rbtn_specST.setEnabled(true);
            rbtn_NOT1_1.setEnabled(true);
            txt_specST.setEnabled(true);
            txt_specST.setBackground(new java.awt.Color(255, 255, 255));
            CB_specST.setEnabled(true);
            
            rbtn_specST1.setEnabled(true);
            rbtn_NOT1_2.setEnabled(true);
            txt_specST1.setEnabled(true);
            txt_specST1.setBackground(new java.awt.Color(255, 255, 255));
            CB_specST1.setEnabled(true);
            CB_andor1.setEnabled(true);
        }
    }//GEN-LAST:event_rbtn_subtypeActionPerformed

    private void rbtn_all_subtypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_all_subtypesActionPerformed
        if (rbtn_all_subtypes.isSelected()){            
            rbtn_specST.setEnabled(false);
            rbtn_NOT1_1.setEnabled(false);
            txt_specST.setEnabled(false);
            txt_specST.setBackground(new java.awt.Color(204, 204, 204));
            CB_specST.setEnabled(false);
            
            rbtn_specST1.setEnabled(false);
            rbtn_NOT1_2.setEnabled(false);
            txt_specST1.setEnabled(false);
            txt_specST1.setBackground(new java.awt.Color(204, 204, 204));
            CB_specST1.setEnabled(false);
            CB_andor1.setEnabled(false);
        }
    }//GEN-LAST:event_rbtn_all_subtypesActionPerformed

    private void rbtn_all_TODOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_all_TODOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbtn_all_TODOActionPerformed

    private void rbtn_TODOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_TODOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rbtn_TODOActionPerformed

    private void rbtn_all_cytologyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_all_cytologyActionPerformed
        if (rbtn_all_cytology.isSelected()){            
            rbtn_CYT.setEnabled(false);
            rbtn_NOT3_1.setEnabled(false);
            txt_cytology.setEnabled(false);
            txt_cytology.setBackground(new java.awt.Color(204, 204, 204));
            CB_cytology.setEnabled(false);
            
            rbtn_CYT1.setEnabled(false);
            rbtn_NOT3_2.setEnabled(false);
            txt_cytology1.setEnabled(false);
            txt_cytology1.setBackground(new java.awt.Color(204, 204, 204));
            CB_cytology1.setEnabled(false);
            CB_andor3.setEnabled(false);
        }
    }//GEN-LAST:event_rbtn_all_cytologyActionPerformed

    private void rbtn_cytologyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_cytologyActionPerformed
        if (rbtn_cytology.isSelected()){            
            rbtn_CYT.setEnabled(true);
            rbtn_NOT3_1.setEnabled(true);
            txt_cytology.setEnabled(true);
            txt_cytology.setBackground(new java.awt.Color(255, 255, 255));
            CB_cytology.setEnabled(true);
            
            rbtn_CYT1.setEnabled(true);
            rbtn_NOT3_2.setEnabled(true);
            txt_cytology1.setEnabled(true);
            txt_cytology1.setBackground(new java.awt.Color(255, 255, 255));
            CB_cytology1.setEnabled(true);
            CB_andor3.setEnabled(true);
        }
    }//GEN-LAST:event_rbtn_cytologyActionPerformed

    private void rbtn_all_classActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_all_classActionPerformed
        if (rbtn_all_class.isSelected()){            
            rbtn_RG.setEnabled(false);
            rbtn_NOT2_1.setEnabled(false);
            txt_class.setEnabled(false);
            txt_class.setBackground(new java.awt.Color(204, 204, 204));
            CB_class.setEnabled(false);
            
            rbtn_RG1.setEnabled(false);
            rbtn_NOT2_2.setEnabled(false);
            txt_class1.setEnabled(false);
            txt_class1.setBackground(new java.awt.Color(204, 204, 204));
            CB_class1.setEnabled(false);
            CB_andor2.setEnabled(false);
        }
    }//GEN-LAST:event_rbtn_all_classActionPerformed

    private void rbtn_classActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_classActionPerformed
        if (rbtn_class.isSelected()){            
            rbtn_RG.setEnabled(true);
            rbtn_NOT2_1.setEnabled(true);
            txt_class.setEnabled(true);
            txt_class.setBackground(new java.awt.Color(255, 255, 255));
            CB_class.setEnabled(true);
            
            rbtn_RG1.setEnabled(true);
            rbtn_NOT2_2.setEnabled(true);
            txt_class1.setEnabled(true);
            txt_class1.setBackground(new java.awt.Color(255, 255, 255));
            CB_class1.setEnabled(true);
            CB_andor2.setEnabled(true);
        }
    }//GEN-LAST:event_rbtn_classActionPerformed

    private void CB_classActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_classActionPerformed
        String class_select = CB_class.getSelectedItem().toString();
        String classRG = "";
        switch (class_select) {
            case "riskgroup":
                classRG = "rg";
                break;
            case "mrd riskgroup":
                classRG = "mrd_rg";
                break;
            case "prd":
                classRG = "prd";
                break;
            case "fcm mrd":
                classRG = "fcm_mrd";
                break;
            case "immuno pickl":
                classRG = "immuno_pickl";
                break;
            case "immuno dworzak":
                classRG = "immuno_dworzak";
                break;
            case "FAB":
                classRG = "FAB";
                break;
            default:
                break;
        }
        if (classRG.equals("fcm_mrd")) {
            rbtn_NOT2_1.setEnabled(false);
        } else {
            rbtn_NOT2_1.setEnabled(true);
        }
    }//GEN-LAST:event_CB_classActionPerformed

    private void CB_class1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_class1ActionPerformed
        String class_select1 = CB_class1.getSelectedItem().toString();
        String classRG1 = "";
        switch (class_select1) {
            case "riskgroup":
                classRG1 = "rg";
                break;
            case "mrd riskgroup":
                classRG1 = "mrd_rg";
                break;
            case "prd":
                classRG1 = "prd";
                break;
            case "fcm mrd":
                classRG1 = "fcm_mrd";
                break;
            case "immuno pickl":
                classRG1 = "immuno_pickl";
                break;
            case "immuno dworzak":
                classRG1 = "immuno_dworzak";
                break;
            case "FAB":
                classRG1 = "FAB";
                break;
            default:
                break;
        }
        if (classRG1.equals("fcm_mrd")) {
            rbtn_NOT2_2.setEnabled(false);
        } else {
            rbtn_NOT2_2.setEnabled(true);
        }
    }//GEN-LAST:event_CB_class1ActionPerformed

    private void CB_cytologyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_cytologyActionPerformed
        String cyt_select = CB_cytology.getSelectedItem().toString();
        String cytology = "";
        switch (cyt_select) {
            case "fab_class":
                cytology = "fab_class";
                break;
            case "% blast PB":
                cytology = "prcnt_blast_pb";
                break;
            case "% blast BM":
                cytology = "prcnt_blast_km";
                break;
            case "eval":
                cytology = "eval";
                break;
            case "summary":
                cytology = "summ";
                break;
            default:
                break;
        }
        if (cytology.equals("prcnt_blast_pb")|| cytology.equals("prcnt_blast_km") ){
            rbtn_NOT3_1.setEnabled(false);
        } else {
            rbtn_NOT3_1.setEnabled(true);
        }
    }//GEN-LAST:event_CB_cytologyActionPerformed

    private void CB_cytology1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CB_cytology1ActionPerformed
        String cyt_select1 = CB_cytology1.getSelectedItem().toString();
        String cytology1 = "";
        switch (cyt_select1) {
            case "fab_class":
                cytology1 = "fab_class";
                break;
            case "% blast PB":
                cytology1 = "prcnt_blast_pb";
                break;
            case "% blast BM":
                cytology1 = "prcnt_blast_km";
                break;
            case "eval":
                cytology1 = "eval";
                break;
            case "summary":
                cytology1 = "summ";
                break;
            default:
                break;
        }
        if (cytology1.equals("prcnt_blast_pb")|| cytology1.equals("prcnt_blast_km") ){
            rbtn_NOT3_2.setEnabled(false);
        } else {
            rbtn_NOT3_2.setEnabled(true);
        }
    }//GEN-LAST:event_CB_cytology1ActionPerformed

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
            java.util.logging.Logger.getLogger(ClassificationBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClassificationBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClassificationBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClassificationBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ClassificationBrowse().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CB_RG4;
    private javax.swing.JComboBox<String> CB_RG5;
    private javax.swing.JComboBox<String> CB_andor1;
    private javax.swing.JComboBox<String> CB_andor2;
    private javax.swing.JComboBox<String> CB_andor3;
    private javax.swing.JComboBox<String> CB_andor4;
    private javax.swing.JComboBox<String> CB_class;
    private javax.swing.JComboBox<String> CB_class1;
    private javax.swing.JComboBox<String> CB_cytology;
    private javax.swing.JComboBox<String> CB_cytology1;
    private javax.swing.JComboBox<String> CB_specST;
    private javax.swing.JComboBox<String> CB_specST1;
    private javax.swing.JComboBox<String> ComboBox_projPat;
    private javax.swing.JComboBox<String> ComboBox_stdyPat;
    private javax.swing.JPanel Info_top4;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_Search;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem cpLabIds;
    private javax.swing.JMenuItem cpResultIds;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JPopupMenu popUpResult;
    private javax.swing.JRadioButton rbtn_CYT;
    private javax.swing.JRadioButton rbtn_CYT1;
    private javax.swing.JRadioButton rbtn_NOT1_1;
    private javax.swing.JRadioButton rbtn_NOT1_2;
    private javax.swing.JRadioButton rbtn_NOT2_1;
    private javax.swing.JRadioButton rbtn_NOT2_2;
    private javax.swing.JRadioButton rbtn_NOT3_1;
    private javax.swing.JRadioButton rbtn_NOT3_2;
    private javax.swing.JRadioButton rbtn_NOT4_1;
    private javax.swing.JRadioButton rbtn_NOT4_2;
    private javax.swing.JRadioButton rbtn_RG;
    private javax.swing.JRadioButton rbtn_RG1;
    private javax.swing.JRadioButton rbtn_TODO;
    private javax.swing.JRadioButton rbtn_all_TODO;
    private javax.swing.JRadioButton rbtn_all_class;
    private javax.swing.JRadioButton rbtn_all_cytology;
    private javax.swing.JRadioButton rbtn_all_subtypes;
    private javax.swing.JRadioButton rbtn_class;
    private javax.swing.JRadioButton rbtn_cytology;
    private javax.swing.JRadioButton rbtn_onlyPat;
    private javax.swing.JRadioButton rbtn_onlyPat1;
    private javax.swing.JRadioButton rbtn_specST;
    private javax.swing.JRadioButton rbtn_specST1;
    private javax.swing.JRadioButton rbtn_specST6;
    private javax.swing.JRadioButton rbtn_specST7;
    private javax.swing.JRadioButton rbtn_subtype;
    private javax.swing.JTable table_RgClassLab;
    private javax.swing.JTable table_resultID;
    private javax.swing.JTextField txt_RG4;
    private javax.swing.JTextField txt_RG5;
    private javax.swing.JTextField txt_class;
    private javax.swing.JTextField txt_class1;
    private javax.swing.JTextField txt_cytology;
    private javax.swing.JTextField txt_cytology1;
    private javax.swing.JTextField txt_specST;
    private javax.swing.JTextField txt_specST1;
    // End of variables declaration//GEN-END:variables
}
