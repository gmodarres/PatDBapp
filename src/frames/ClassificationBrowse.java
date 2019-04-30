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

import static frames.SelectMarker.markerSql;
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
import myClass.Log;
import net.proteanit.sql.DbUtils;
import myClass.IdManagement;
import myClass.MenuDriver;
import myClass.OSDetector;
import myClass.ShowSqlSelector;
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
    
    static public String tableSortAdd = null;
        
    Log my_log;
        
    /**
     * Creates new form PatientBrowse
     */
    public ClassificationBrowse() {
        MenuDriver menu = new MenuDriver();     // create instance of JMenuBar menuBarGlobal 
        this.setJMenuBar( menu.getMenuBar() );
        
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
        String sql = "SELECT * FROM subtypes WHERE 1=1";
        
        // only for the set collected IDs
        if (rbtn_idCollected.isSelected()) {
            sql = IdCollector.deliver_collected_ids(sql,"","","");
        }

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
            DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();  
            table_resultID.setDefaultRenderer(Object.class , ren); 
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
    //TEST
    private String getMarker(){
        String wiMarker = "";
        return wiMarker;     
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
        cpPatIds = new javax.swing.JMenuItem();
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
        rbtn_immuno = new javax.swing.JRadioButton();
        rbtn_NOT4_1 = new javax.swing.JRadioButton();
        txt_immuno = new javax.swing.JTextField();
        rbtn_IMM = new javax.swing.JRadioButton();
        CB_immuno = new javax.swing.JComboBox<>();
        CB_andor4 = new javax.swing.JComboBox<>();
        rbtn_IMM1 = new javax.swing.JRadioButton();
        CB_immuno1 = new javax.swing.JComboBox<>();
        rbtn_NOT4_2 = new javax.swing.JRadioButton();
        txt_immuno1 = new javax.swing.JTextField();
        rbtn_all_immuno = new javax.swing.JRadioButton();
        jLabel4 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        rbtn_immuno2 = new javax.swing.JRadioButton();
        rbtn_NOT5_1 = new javax.swing.JRadioButton();
        txt_immuno2 = new javax.swing.JTextField();
        rbtn_IMM2 = new javax.swing.JRadioButton();
        CB_immuno2 = new javax.swing.JComboBox<>();
        CB_andor5 = new javax.swing.JComboBox<>();
        rbtn_IMM21 = new javax.swing.JRadioButton();
        CB_immuno21 = new javax.swing.JComboBox<>();
        rbtn_NOT5_2 = new javax.swing.JRadioButton();
        txt_immuno21 = new javax.swing.JTextField();
        rbtn_all_immuno2 = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        btn_test = new javax.swing.JButton();
        rbtn_marker = new javax.swing.JRadioButton();
        jPanel9 = new javax.swing.JPanel();
        rbtn_somelab2 = new javax.swing.JRadioButton();
        rbtn_NOT4_7 = new javax.swing.JRadioButton();
        txt_immuno4 = new javax.swing.JTextField();
        rbtn_IMM4 = new javax.swing.JRadioButton();
        CB_immuno4 = new javax.swing.JComboBox<>();
        CB_andor6 = new javax.swing.JComboBox<>();
        rbtn_IMM5 = new javax.swing.JRadioButton();
        CB_immuno5 = new javax.swing.JComboBox<>();
        rbtn_NOT4_8 = new javax.swing.JRadioButton();
        txt_immuno5 = new javax.swing.JTextField();
        rbtn_all_some = new javax.swing.JRadioButton();
        jLabel10 = new javax.swing.JLabel();
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
        jPanel5 = new javax.swing.JPanel();
        rbtn_subtype1 = new javax.swing.JRadioButton();
        rbtn_all_subtypes1 = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        txt_subA = new javax.swing.JTextField();
        jCheckBox_majS = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jCheckBox_othS = new javax.swing.JCheckBox();
        jCheckBox_specS1 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox_specS3 = new javax.swing.JCheckBox();
        jCheckBox_specS2 = new javax.swing.JCheckBox();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox_ngsS1 = new javax.swing.JCheckBox();
        jCheckBox_ngsS2 = new javax.swing.JCheckBox();
        txt_subB = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
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
        rbtn_idCollected = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_resultID = new javax.swing.JTable();
        lbl_rowsReturned = new javax.swing.JLabel();

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

        cpPatIds.setText("copy pat_ids ...");
        cpPatIds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpPatIdsActionPerformed(evt);
            }
        });
        popUpResult.add(cpPatIds);

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
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rbtn_all_cytology, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(rbtn_CYT, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(rbtn_CYT1, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(CB_cytology1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(CB_cytology, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(rbtn_cytology, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 155, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtn_NOT3_2)
                            .addComponent(rbtn_NOT3_1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_cytology)
                            .addComponent(txt_cytology1))))
                .addContainerGap())
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
                        .addGap(80, 80, 80)
                        .addComponent(CB_andor3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel3Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor3, CB_cytology, CB_cytology1, rbtn_CYT, rbtn_CYT1, rbtn_NOT3_1, rbtn_NOT3_2});

        jTabbedPane2.addTab("cytology", jPanel3);

        jPanel4.setBackground(new java.awt.Color(102, 153, 255));

        buttonGroup1.add(rbtn_immuno);
        rbtn_immuno.setText("s. with ...");
        rbtn_immuno.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_immuno.setBorderPainted(true);
        rbtn_immuno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_immunoActionPerformed(evt);
            }
        });

        rbtn_NOT4_1.setText("NOT");
        rbtn_NOT4_1.setEnabled(false);

        txt_immuno.setBackground(new java.awt.Color(204, 204, 204));
        txt_immuno.setEnabled(false);

        rbtn_IMM.setEnabled(false);

        CB_immuno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "immuno dworzak", "BAL dworzak", "MPAL dworzak", "MPAL pickl", "add_info" }));
        CB_immuno.setEnabled(false);

        CB_andor4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor4.setEnabled(false);

        rbtn_IMM1.setEnabled(false);

        CB_immuno1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "immuno dworzak", "BAL dworzak", "MPAL dworzak", "MPAL pickl", "add_info" }));
        CB_immuno1.setEnabled(false);

        rbtn_NOT4_2.setText("NOT");
        rbtn_NOT4_2.setEnabled(false);

        txt_immuno1.setBackground(new java.awt.Color(204, 204, 204));
        txt_immuno1.setEnabled(false);

        buttonGroup1.add(rbtn_all_immuno);
        rbtn_all_immuno.setText("all samples");
        rbtn_all_immuno.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all_immuno.setBorderPainted(true);
        rbtn_all_immuno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_all_immunoActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(0, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("immunology");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtn_IMM, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbtn_IMM1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_immuno1, 0, 138, Short.MAX_VALUE)
                            .addComponent(CB_immuno, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(rbtn_immuno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtn_all_immuno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rbtn_NOT4_2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbtn_NOT4_1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_immuno)
                            .addComponent(txt_immuno1)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_all_immuno)
                            .addComponent(jLabel4))
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_immuno)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT4_1)
                            .addComponent(rbtn_IMM)
                            .addComponent(txt_immuno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_immuno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT4_2)
                            .addComponent(rbtn_IMM1)
                            .addComponent(txt_immuno1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_immuno1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(CB_andor4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel4Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor4, CB_immuno, CB_immuno1, rbtn_IMM, rbtn_IMM1, rbtn_NOT4_1, rbtn_NOT4_2});

        jTabbedPane2.addTab("immunology", jPanel4);

        jPanel8.setBackground(new java.awt.Color(102, 153, 255));

        buttonGroup1.add(rbtn_immuno2);
        rbtn_immuno2.setText("s. with ...");
        rbtn_immuno2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_immuno2.setBorderPainted(true);
        rbtn_immuno2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_immuno2ActionPerformed(evt);
            }
        });

        rbtn_NOT5_1.setText("NOT");
        rbtn_NOT5_1.setEnabled(false);

        txt_immuno2.setBackground(new java.awt.Color(204, 204, 204));
        txt_immuno2.setEnabled(false);

        rbtn_IMM2.setEnabled(false);

        CB_immuno2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "marker", "result", "marker group" }));
        CB_immuno2.setEnabled(false);

        CB_andor5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor5.setEnabled(false);

        rbtn_IMM21.setEnabled(false);

        CB_immuno21.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "marker", "result", "marker group" }));
        CB_immuno21.setEnabled(false);

        rbtn_NOT5_2.setText("NOT");
        rbtn_NOT5_2.setEnabled(false);

        txt_immuno21.setBackground(new java.awt.Color(204, 204, 204));
        txt_immuno21.setEnabled(false);

        buttonGroup1.add(rbtn_all_immuno2);
        rbtn_all_immuno2.setText("all samples");
        rbtn_all_immuno2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all_immuno2.setBorderPainted(true);
        rbtn_all_immuno2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_all_immuno2ActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 255, 255));
        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("immuno marker");

        btn_test.setText("select marker");
        btn_test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_testActionPerformed(evt);
            }
        });

        rbtn_marker.setEnabled(false);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rbtn_all_immuno2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel8Layout.createSequentialGroup()
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(rbtn_IMM2, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(rbtn_IMM21, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(CB_immuno2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(CB_immuno21, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(rbtn_immuno2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(0, 96, Short.MAX_VALUE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(rbtn_NOT5_2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbtn_NOT5_1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbtn_marker, javax.swing.GroupLayout.Alignment.LEADING))
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txt_immuno2)
                                    .addComponent(txt_immuno21)))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(btn_test, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbtn_marker)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(rbtn_all_immuno2)
                                    .addComponent(jLabel9))
                                .addGap(4, 4, 4)
                                .addComponent(rbtn_immuno2))
                            .addComponent(btn_test))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT5_1)
                            .addComponent(rbtn_IMM2)
                            .addComponent(txt_immuno2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_immuno2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT5_2)
                            .addComponent(rbtn_IMM21)
                            .addComponent(txt_immuno21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_immuno21, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(CB_andor5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel8Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor5, CB_immuno2, CB_immuno21, rbtn_IMM2, rbtn_IMM21, rbtn_NOT5_1, rbtn_NOT5_2});

        jTabbedPane2.addTab("immuno marker", jPanel8);

        jPanel9.setBackground(new java.awt.Color(102, 153, 255));

        buttonGroup1.add(rbtn_somelab2);
        rbtn_somelab2.setText("s. with ...");
        rbtn_somelab2.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_somelab2.setBorderPainted(true);

        rbtn_NOT4_7.setText("NOT");
        rbtn_NOT4_7.setEnabled(false);

        txt_immuno4.setBackground(new java.awt.Color(204, 204, 204));
        txt_immuno4.setEnabled(false);

        rbtn_IMM4.setEnabled(false);

        CB_immuno4.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "parameter 1", "parameter 2", "parameter 3" }));
        CB_immuno4.setEnabled(false);

        CB_andor6.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor6.setEnabled(false);

        rbtn_IMM5.setEnabled(false);

        CB_immuno5.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "parameter 1", "parameter 2", "parameter 3" }));
        CB_immuno5.setEnabled(false);

        rbtn_NOT4_8.setText("NOT");
        rbtn_NOT4_8.setEnabled(false);

        txt_immuno5.setBackground(new java.awt.Color(204, 204, 204));
        txt_immuno5.setEnabled(false);

        buttonGroup1.add(rbtn_all_some);
        rbtn_all_some.setText("all samples");
        rbtn_all_some.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all_some.setBorderPainted(true);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 255, 255));
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("someLab");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rbtn_all_some, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel9Layout.createSequentialGroup()
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(rbtn_IMM4, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(rbtn_IMM5, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(CB_immuno4, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(CB_immuno5, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addComponent(rbtn_somelab2, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rbtn_NOT4_8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbtn_NOT4_7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_immuno4))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(txt_immuno5))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 150, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_all_some)
                            .addComponent(jLabel10))
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_somelab2)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT4_7)
                            .addComponent(rbtn_IMM4)
                            .addComponent(txt_immuno4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_immuno4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT4_8)
                            .addComponent(rbtn_IMM5)
                            .addComponent(txt_immuno5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_immuno5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                        .addGap(80, 80, 80)
                        .addComponent(CB_andor6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel9Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor6, CB_immuno4, CB_immuno5, rbtn_IMM4, rbtn_IMM5, rbtn_NOT4_7, rbtn_NOT4_8});

        jTabbedPane2.addTab("someLab", jPanel9);

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

        rbtn_specST.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        rbtn_specST.setEnabled(false);

        CB_specST.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "maj. subtype", "oth. subtype", "spec. subt. 1", "spec. subt. 2", "spec. subt. 3" }));
        CB_specST.setEnabled(false);

        CB_andor1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor1.setEnabled(false);

        rbtn_specST1.setEnabled(false);

        CB_specST1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "maj. subtype", "oth. subtype", "spec. subt. 1", "spec. subt. 2", "spec. subt. 3" }));
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
                .addGap(4, 4, 4)
                .addComponent(CB_andor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtn_subtype, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtn_specST, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbtn_specST1, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_specST, 0, 159, Short.MAX_VALUE)
                            .addComponent(CB_specST1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(rbtn_all_subtypes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtn_NOT1_1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rbtn_NOT1_2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_specST1, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(txt_specST)))
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(CB_andor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_all_subtypes)
                            .addComponent(jLabel1))
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_subtype)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtn_specST)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txt_specST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(CB_specST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(rbtn_NOT1_1)))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtn_specST1, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rbtn_NOT1_2)
                                .addComponent(txt_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(CB_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(5, 5, 5))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor1, CB_specST, CB_specST1, rbtn_NOT1_1, rbtn_NOT1_2, rbtn_specST, rbtn_specST1});

        rbtn_subtype.getAccessibleContext().setAccessibleName("subtypes");

        jTabbedPane1.addTab("subtypes", jPanel1);

        jPanel5.setBackground(new java.awt.Color(102, 153, 255));

        buttonGroup1.add(rbtn_subtype1);
        rbtn_subtype1.setText("s. with subtype");
        rbtn_subtype1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_subtype1.setBorderPainted(true);

        buttonGroup1.add(rbtn_all_subtypes1);
        rbtn_all_subtypes1.setText("all samples");
        rbtn_all_subtypes1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all_subtypes1.setBorderPainted(true);

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("subtypes");

        jCheckBox_majS.setText("major subtype");

        jLabel5.setText("search in:");

        jCheckBox_othS.setText("other subtype");

        jCheckBox_specS1.setText("spec. subtype1");

        jCheckBox4.setText("todo");

        jCheckBox_specS3.setText("spec. subtype3");

        jCheckBox_specS2.setText("spec. subtype2");

        jCheckBox7.setText("todo");

        jCheckBox_ngsS1.setText("ngs subtype1");

        jCheckBox_ngsS2.setText("ngs subtype2");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("OR");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox_othS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox_majS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox_specS1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox_specS2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox_specS3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jCheckBox7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox_ngsS1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCheckBox_ngsS2))
                        .addGap(0, 85, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(rbtn_all_subtypes1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(rbtn_subtype1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(txt_subA, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txt_subB, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jCheckBox4, jCheckBox7, jCheckBox_majS, jCheckBox_ngsS1, jCheckBox_ngsS2, jCheckBox_othS, jCheckBox_specS1, jCheckBox_specS2, jCheckBox_specS3});

        jPanel5Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_subA, txt_subB});

        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtn_all_subtypes1)
                    .addComponent(jLabel2))
                .addGap(4, 4, 4)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtn_subtype1)
                    .addComponent(txt_subA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_subB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(3, 3, 3)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(16, 16, 16)
                        .addComponent(jCheckBox_specS1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jCheckBox7, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCheckBox_ngsS1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCheckBox_ngsS2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCheckBox_specS2, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jCheckBox_majS, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCheckBox_specS3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jCheckBox_othS, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jCheckBox4, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel5Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jCheckBox4, jCheckBox7, jCheckBox_majS, jCheckBox_ngsS1, jCheckBox_ngsS2, jCheckBox_othS, jCheckBox_specS1, jCheckBox_specS2, jCheckBox_specS3, jLabel5});

        jTabbedPane1.addTab("subtypes 2", jPanel5);

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
                .addGap(4, 4, 4)
                .addComponent(CB_andor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtn_all_class, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtn_class, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(rbtn_RG)
                            .addComponent(rbtn_RG1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_class, 0, 159, Short.MAX_VALUE)
                            .addComponent(CB_class1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtn_NOT2_1)
                            .addComponent(rbtn_NOT2_2, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txt_class, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
                            .addComponent(txt_class1)))
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(rbtn_NOT2_2)
                        .addComponent(txt_class1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CB_class1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(CB_andor2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(rbtn_RG1))
                .addGap(0, 5, Short.MAX_VALUE))
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

        rbtn_idCollected.setText("use IDs from collector");
        rbtn_idCollected.setToolTipText("select to get results from patients in a certain project (select from below)");

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
                    .addComponent(rbtn_onlyPat1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtn_idCollected, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rbtn_idCollected)
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
                .addComponent(jTabbedPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14)
                .addComponent(btn_Search)
                .addGap(21, 21, 21))
        );
        Info_top4Layout.setVerticalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_Search)
                    .addComponent(jTabbedPane2)
                    .addComponent(jTabbedPane1)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addComponent(lbl_rowsReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(lbl_rowsReturned)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void btn_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SearchActionPerformed
        this.tableSortAdd = null;
        if(rbtn_all_subtypes.isSelected() || rbtn_all_subtypes1.isSelected()){
            //rbtn_specST.setEnabled(false);
            initial_table_subtypes();
            update_table_resultID();
            if (rbtn_onlyPat.isSelected()){
                String sql = "SELECT distinct t.auto_id, t.pat_id, major_subtype, other_subtype, spec_sub1, spec_sub2, spec_sub3, ngs_sub1, ngs_sub2 FROM sample s, patient p, subtypes t"
                    + " where s.pat_id=p.pat_id"
                    + " and t.pat_id=p.pat_id";
                update_table_RgClassLab(sql,"true");    // Test second table - only project
            } 
        } else if (rbtn_subtype.isSelected()){          // subtypes
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            
            String sql = "SELECT distinct t.auto_id, t.pat_id, major_subtype, other_subtype, spec_sub1, spec_sub2, spec_sub3, ngs_sub1, ngs_sub2 FROM sample s, patient p, subtypes t"
                + " where s.pat_id=p.pat_id"
                + " and t.pat_id=p.pat_id";

            if (rbtn_specST.isSelected()) {
                String specST_select = CB_specST.getSelectedItem().toString();
                String specST = "";
                switch (specST_select) { 
                    case "maj. subtype":
                        specST = "major_subtype";
                        break;
                    case "oth. subtype":
                        specST = "other_subtype";
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
                    if (spec_txt.equals("null") || spec_txt.equals("NULL")){
                        sql = sql + " and (" + specST + " IS NOT NULL";
                    } else {
                        sql = sql + " and (" + specST + " NOT like '%" + spec_txt + "%'";
                    }
                } else {
                   if (spec_txt.equals("null") || spec_txt.equals("NULL")){
                        sql = sql + " and (" + specST + " IS NULL";
                    } else{ 
                        sql = sql + " and (" + specST + " like '%" + spec_txt + "%'";
                   }
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
                    case "oth. subtype":
                        specST1 = "other_subtype";
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
           
            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                //deliver_collected_ids(method_name,sql);
                //sql = this.mod_sql;
                sql = IdCollector.deliver_collected_ids(sql,"","s.","t.");  // result_id,lab_id,pat_id
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
        } else if (rbtn_subtype1.isSelected()) {        // subtypes2
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            String sql = "SELECT distinct t.auto_id, t.pat_id, major_subtype, other_subtype, spec_sub1, spec_sub2, spec_sub3, ngs_sub1, ngs_sub2 FROM sample s, patient p, subtypes t"
                    + " where s.pat_id=p.pat_id"
                    + " and t.pat_id=p.pat_id";

            String subA = txt_subA.getText();
            String subB = txt_subB.getText();

            sql = sql + " and (";

            if (jCheckBox_majS.isSelected()){ 
                //if (txt_subA !=null && !txt_subA.getText().isEmpty()) {sql = sql + " major_subtype like '%" + subA +"%'"; }
                if (txt_subA !=null && !txt_subA.getText().isEmpty()) {
                    if (txt_subA.getText().equals("null") || txt_subA.getText().equals("NULL")) {
                        sql = sql + " major_subtype IS NULL";
                    } else {
                        sql = sql + " major_subtype like '%" + subA +"%'"; 
                    }
                }
                if (txt_subB !=null && !txt_subB.getText().isEmpty()) {sql = sql + " or major_subtype like '%" + subB +"%'"; }
                sql = sql + " or";
            }
            if (jCheckBox_othS.isSelected()){ 
                if (txt_subA !=null && !txt_subA.getText().isEmpty()) {sql = sql + " other_subtype like '%" + subA +"%'"; }
                if (txt_subB !=null && !txt_subB.getText().isEmpty()) {sql = sql + " or other_subtype like '%" + subB +"%'"; }
                sql = sql + " or";
            }
            if (jCheckBox_specS1.isSelected()){ 
                if (txt_subA !=null && !txt_subA.getText().isEmpty()) {sql = sql + " spec_sub1 like '%" + subA +"%'"; }
                if (txt_subB !=null && !txt_subB.getText().isEmpty()) {sql = sql + " or spec_sub1 like '%" + subB +"%'"; }
                sql = sql + " or";
            }
            if (jCheckBox_specS2.isSelected()){ 
                if (txt_subA !=null && !txt_subA.getText().isEmpty()) {sql = sql + " spec_sub2 like '%" + subA +"%'"; }
                if (txt_subB !=null && !txt_subB.getText().isEmpty()) {sql = sql + " or spec_sub2 like '%" + subB +"%'"; }
                sql = sql + " or";
            }
            if (jCheckBox_specS3.isSelected()){ 
                if (txt_subA !=null && !txt_subA.getText().isEmpty()) {sql = sql + " spec_sub3 like '%" + subA +"%'"; }
                if (txt_subB !=null && !txt_subB.getText().isEmpty()) {sql = sql + " or spec_sub3 like '%" + subB +"%'"; }
                sql = sql + " or";
            }      
            sql = sql.substring(0, (sql.length() - 3));
            sql = sql + " )";
            
            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                sql = IdCollector.deliver_collected_ids(sql,"",".s","t.");
            }
            
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
                this.ids = IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_subtypes");

                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql, "true");          // Test second table - only project
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
                my_log.logger.warning("ERROR: " + e);
            } finally {
                try {
                    if (rs != null) { rs.close(); }
                    if (pst != null) { pst.close(); }
                    if (conn != null) { conn.close(); }
                } catch (Exception e) {
                }
            }
        } else if (rbtn_all_class.isSelected()) {       // classification
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            String sql = "SELECT auto_id, pat_id, rg, mrd_rg, prd, fcm_mrd, immuno_pickl, immuno_dworzak, FAB, chng_info as I FROM pat_instudy where 1=1";

            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {
                sql = IdCollector.deliver_collected_ids(sql,"","","");
            }

            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();
                table_RgClassLab.setDefaultRenderer(Object.class, ren);
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
        } else if (rbtn_class.isSelected()) {              // classification
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            String sql = "SELECT auto_id, pat_id, rg, mrd_rg, prd, fcm_mrd, immuno_pickl, immuno_dworzak , FAB, chng_info as I FROM pat_instudy where 1=1";

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
            
            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                sql = IdCollector.deliver_collected_ids(sql,"","","");
            }
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                my_log.logger.info("SQL:  " + sql);
                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();  
                table_RgClassLab.setDefaultRenderer(Object.class , ren);
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
        } else if (rbtn_all_cytology.isSelected()) {               // cytology
             Connection conn = DBconnect.ConnecrDb();
             ResultSet rs = null;
             PreparedStatement pst = null;
             //String sql = "SELECT * from cytology_result";
             String sql = "SELECT pat_id, cyto_auto_ID as cytoID, fab_class, prcnt_blast_km as `% blast BM`, prcnt_blast_pb as `% blast PB`, eval, summ FROM cytology_result c, main_result m, sample s"
                     + " WHERE c.result_id=m.result_id "
                     + " AND s.lab_id=m.lab_id";
 
            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                sql = IdCollector.deliver_collected_ids(sql,"c.","m.","s.");  // result_id,lab_id,pat_id
            }

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
        }else if (rbtn_cytology.isSelected()) {               // cytology
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
            
            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                sql = IdCollector.deliver_collected_ids(sql,"c.","m.","s.");
            }
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                my_log.logger.info("SQL:  " + sql);
                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();  
                table_RgClassLab.setDefaultRenderer(Object.class , ren);
                
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
        } else if (rbtn_all_immuno.isSelected()) {               // immunology 1 
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            //String sql = "SELECT * from cytology_result";
            String sql = "SELECT pat_id, r.result_id, immuno_dworzak, BAL_dworzak, MPAL_dworzak, MPAL_pickl, add_info FROM immuno_result r, main_result m, sample s "
                    + " WHERE r.result_id=m.result_id" 
                    + " AND s.lab_id=m.lab_id";
 
            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                sql = IdCollector.deliver_collected_ids(sql,"r.","m.","s.");  // result_id,lab_id,pat_id
            }
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();
                table_RgClassLab.setDefaultRenderer(Object.class, ren);
                CustomSorter.table_customRowSort(table_RgClassLab);

                if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                     table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                     table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                     table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                     table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                }

                //get_ids(sql,pst,rs,conn);
                this.ids = IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_immuno");

                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql, "false");          // Test second table - only project
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
        } else if (rbtn_immuno.isSelected()) {               // immunology
             Connection conn = DBconnect.ConnecrDb();
             ResultSet rs = null;
             PreparedStatement pst = null;

            String sql = "SELECT pat_id, r.result_id, immuno_dworzak, BAL_dworzak, MPAL_dworzak, MPAL_pickl, add_info FROM immuno_result r, main_result m, sample s "
                    + " WHERE r.result_id=m.result_id" 
                    + " AND s.lab_id=m.lab_id";
					
             if (rbtn_IMM.isSelected()) {
                 String imm_select = CB_immuno.getSelectedItem().toString();
                 String immunology = "";
                 switch (imm_select) {
                     case "immuno dworzak":
                         immunology = "immuno_dworzak";
                         break;
                     case "BAL dworzak":
                         immunology = "BAL_dworzak";
                         break;
                     case "MPAL dworzak":
                         immunology = "MPAL_dworzak";
                         break;
                     case "MPAL pickl":
                         immunology = "MPAL_pickl";
                         break;
                     case "add_info":
                         immunology = "add_info";
                         break;
                     default:
                         break;
                 }
                 String IMM_txt = txt_immuno.getText();
                     if (rbtn_NOT4_1.isSelected()) {
                         if (IMM_txt.equals("null") || IMM_txt.equals("NULL")) {
                             sql = sql + " and (" + immunology + " IS NOT NULL";
                         } else {
                             sql = sql + " and (" + immunology + " NOT like '%" + IMM_txt + "%'";
                         }
                     } else {
                         if (IMM_txt.equals("null") || IMM_txt.equals("NULL")) {
                             sql = sql + " and (" + immunology + " IS NULL";
                         } else {
                             sql = sql + " and (" + immunology + " like '%" + IMM_txt + "%'";
                         }
                     }
                 }

             if (rbtn_IMM1.isSelected()) {
                String andor4 = CB_andor4.getSelectedItem().toString();
                String imm_select1 = CB_immuno1.getSelectedItem().toString();
                String immunology1 = "";
                switch (imm_select1) {
                    case "immuno dworzak":
                        immunology1 = "immuno_dworzak";
                        break;
                    case "BAL dworzak":
                        immunology1 = "BAL_dworzak";
                        break;
                    case "MPAL dworzak":
                        immunology1 = "MPAL_dworzak";
                        break;
                    case "MPAL pickl":
                        immunology1 = "MPAL_pickl";
                        break;
                    case "add_info":
                        immunology1 = "add_info";
                        break;
                    default:
                        break;
                }
                String IMM_txt1 = txt_immuno1.getText();
                if (rbtn_NOT4_2.isSelected()) {
                    if (IMM_txt1.equals("null") || IMM_txt1.equals("NULL")) {
                        sql = sql + " " + andor4 + " " + immunology1 + " IS NOT NULL)";
                    } else {
                        sql = sql + " " + andor4 + " " + immunology1 + " NOT like '%" + IMM_txt1 + "%')";
                    }
                } else {
                    if (IMM_txt1.equals("null") || IMM_txt1.equals("NULL")) {
                        sql = sql + " " + andor4 + " " + immunology1 + " IS NULL)";
                    } else {
                        sql = sql + " " + andor4 + " " + immunology1 + " like '%" + IMM_txt1 + "%')";
                    }
                }
            } else {
                sql = sql + ")";
            }
            //txtArea_test.setText(sql);    //TEST

            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {
                sql = IdCollector.deliver_collected_ids(sql, "r.", "m.", "s.");  // result_id,lab_id,pat_id
            }

            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                my_log.logger.info("SQL:  " + sql);
                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();
                table_RgClassLab.setDefaultRenderer(Object.class, ren);
                CustomSorter.table_customRowSort(table_RgClassLab);
                
                if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                    table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                }
                //get_ids(sql, pst, rs, conn);
                this.ids = IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_immuno");

                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql, "false");          // Test second table - only project
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
        } else if (rbtn_all_immuno2.isSelected()) {               // immunology 2 - marker 
            this.tableSortAdd = "IMM2";
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
			String sql = "SELECT pat_id, o.result_id, im_sub_id, i.marker_name , result, marker_group FROM immuno_mresult o, immuno_marker i, main_result m, sample s"
				+ " WHERE o.result_id=m.result_id"
				+ " AND m.lab_id=s.lab_id"
				+ " AND o.marker_id=i.marker_id";
            if (markerSql != null && !markerSql.isEmpty()){ sql = sql + markerSql; }

            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                sql = IdCollector.deliver_collected_ids(sql,"o.","m.","s.");  // result_id,lab_id,pat_id
            }
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                // DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();  // do not use - it is ALWAYS more lines for one patient
                DefaultTableCellRenderer ren = new DefaultTableCellRenderer();
                table_RgClassLab.setDefaultRenderer(Object.class, ren);
                CustomSorter.table_customRowSort(table_RgClassLab);

                if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                     table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                     table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                     table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                     table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                }

                //get_ids(sql,pst,rs,conn);
                this.ids = IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_immunoMarker");

                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql, "false");          // Test second table - only project
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
        } else if (rbtn_immuno2.isSelected()) {               // immunology 2 - marker
            this.tableSortAdd = "IMM2"; 
            Connection conn = DBconnect.ConnecrDb();
             ResultSet rs = null;
             PreparedStatement pst = null;
             String sql = "SELECT pat_id, o.result_id, im_sub_id, i.marker_name , result, marker_group FROM immuno_mresult o, immuno_marker i, main_result m, sample s"
				+ " WHERE o.result_id=m.result_id"
				+ " AND m.lab_id=s.lab_id"
				+ " AND o.marker_id=i.marker_id";
             //TEST
             //String wiMarker = getMarker();       
             if (markerSql != null && !markerSql.isEmpty()){ sql = sql + markerSql; }
             
             if (rbtn_IMM2.isSelected()) {
                 String imm_select = CB_immuno2.getSelectedItem().toString();
                 String immunology = "";
                 switch (imm_select) {
                     case "marker":
                         immunology = "marker_name";
                         break;
                     case "result":
                         immunology = "result";
                         break;
                     case "marker group":
                         immunology = "marker_group";
                         break;
                     default:
                         break;
                 }
                 String IMM_txt = txt_immuno2.getText();
                     if (rbtn_NOT5_1.isSelected()) {
                         if (IMM_txt.equals("null") || IMM_txt.equals("NULL")) {
                             sql = sql + " and (" + immunology + " IS NOT NULL";
                         } else {
                             sql = sql + " and (" + immunology + " NOT like '%" + IMM_txt + "%'";
                         }
                     } else {
                         if (IMM_txt.equals("null") || IMM_txt.equals("NULL")) {
                             sql = sql + " and (" + immunology + " IS NULL";
                         } else {
                             sql = sql + " and (" + immunology + " like '%" + IMM_txt + "%'";
                         }
                     }
                 }

             if (rbtn_IMM21.isSelected()) {
                String andor5 = CB_andor5.getSelectedItem().toString();
                String imm_select1 = CB_immuno21.getSelectedItem().toString();
                String immunology1 = "";
                switch (imm_select1) {
                     case "marker":
                         immunology1 = "marker_name";
                         break;
                     case "result":
                         immunology1 = "result";
                         break;
                     case "marker group":
                         immunology1 = "marker_group";
                         break;
                     default:
                         break;
                 }
                String IMM_txt1 = txt_immuno21.getText();
                    if (rbtn_NOT5_2.isSelected()) {
                        if (IMM_txt1.equals("null") || IMM_txt1.equals("NULL")) {
                            sql = sql + " " + andor5 + " " + immunology1 + " IS NOT NULL)";
                        } else {                      
                            sql = sql + " " + andor5 + " " + immunology1 + " NOT like '%" + IMM_txt1 + "%')";
                        }
                    } else {
                        if (IMM_txt1.equals("null") || IMM_txt1.equals("NULL")) {
                            sql = sql + " " + andor5 + " " + immunology1 + " IS NULL)";
                        } else {
                            sql = sql + " " + andor5 + " " + immunology1 + " like '%" + IMM_txt1 + "%')";                            
                        }           
                    }
            } else {
                sql = sql + ")";
            }
            // txtArea_test.setText(sql);    //TEST

            // only for the set collected IDs
            if (rbtn_idCollected.isSelected()) {     
                sql = IdCollector.deliver_collected_ids(sql,"o.","m.","s.");  // result_id,lab_id,pat_id
            }

            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                my_log.logger.info("SQL:  " + sql);
                table_RgClassLab.setModel(DbUtils.resultSetToTableModel(rs));
                // DefaultTableCellRenderer ren = new ColoredTableCellRenderer2();  // always more lines for one patient
                // table_RgClassLab.setDefaultRenderer(Object.class, ren);
                CustomSorter.table_customRowSort(table_RgClassLab);

                if (table_RgClassLab.getColumnModel().getColumnCount() > 0) {
                    table_RgClassLab.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_RgClassLab.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_RgClassLab.getColumnModel().getColumn(1).setMaxWidth(100);
                }
                //get_ids(sql, pst, rs, conn);
                this.ids = IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
                update_table_resultID();
                showSqlInWindow(sql, "CB_immunoMarker");

                if (rbtn_onlyPat.isSelected()) {
                    update_table_RgClassLab(sql, "false");          // Test second table - only project
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
        } // end else if ()
        
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

    private void rbtn_all_immuno2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_all_immuno2ActionPerformed
    if (rbtn_all_immuno2.isSelected()){            
            rbtn_IMM2.setEnabled(false);
            rbtn_NOT5_1.setEnabled(false);
            txt_immuno2.setEnabled(false);
            txt_immuno2.setBackground(new java.awt.Color(204, 204, 204));
            CB_immuno2.setEnabled(false);
            
            rbtn_IMM21.setEnabled(false);
            rbtn_NOT5_2.setEnabled(false);
            txt_immuno21.setEnabled(false);
            txt_immuno21.setBackground(new java.awt.Color(204, 204, 204));
            CB_immuno21.setEnabled(false);
            CB_andor5.setEnabled(false);
        }
    }//GEN-LAST:event_rbtn_all_immuno2ActionPerformed

    private void rbtn_immuno2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_immuno2ActionPerformed
    if (rbtn_immuno2.isSelected()){            
            rbtn_IMM2.setEnabled(true);
            rbtn_NOT5_1.setEnabled(true);
            txt_immuno2.setEnabled(true);
            txt_immuno2.setBackground(new java.awt.Color(255, 255, 255));
            CB_immuno2.setEnabled(true);
            
            rbtn_IMM21.setEnabled(true);
            rbtn_NOT5_2.setEnabled(true);
            txt_immuno21.setEnabled(true);
            txt_immuno21.setBackground(new java.awt.Color(255, 255, 255));
            CB_immuno21.setEnabled(true);
            CB_andor5.setEnabled(true);
        }
    }//GEN-LAST:event_rbtn_immuno2ActionPerformed

    private void rbtn_all_immunoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_all_immunoActionPerformed
        if (rbtn_all_immuno.isSelected()) {
            rbtn_IMM.setEnabled(false);
            rbtn_NOT4_1.setEnabled(false);
            txt_immuno.setEnabled(false);
            txt_immuno.setBackground(new java.awt.Color(204, 204, 204));
            CB_immuno.setEnabled(false);

            rbtn_IMM1.setEnabled(false);
            rbtn_NOT4_2.setEnabled(false);
            txt_immuno1.setEnabled(false);
            txt_immuno1.setBackground(new java.awt.Color(204, 204, 204));
            CB_immuno1.setEnabled(false);
            CB_andor4.setEnabled(false);
        }
    }//GEN-LAST:event_rbtn_all_immunoActionPerformed

    private void rbtn_immunoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_immunoActionPerformed
        if (rbtn_immuno.isSelected()){
            rbtn_IMM.setEnabled(true);
            rbtn_NOT4_1.setEnabled(true);
            txt_immuno.setEnabled(true);
            txt_immuno.setBackground(new java.awt.Color(255, 255, 255));
            CB_immuno.setEnabled(true);

            rbtn_IMM1.setEnabled(true);
            rbtn_NOT4_2.setEnabled(true);
            txt_immuno1.setEnabled(true);
            txt_immuno1.setBackground(new java.awt.Color(255, 255, 255));
            CB_immuno1.setEnabled(true);
            CB_andor4.setEnabled(true);
        }
    }//GEN-LAST:event_rbtn_immunoActionPerformed

    private void btn_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_testActionPerformed
        SelectMarker s = new SelectMarker();
        s.setVisible(true);  
    }//GEN-LAST:event_btn_testActionPerformed

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
    private javax.swing.JComboBox<String> CB_andor1;
    private javax.swing.JComboBox<String> CB_andor2;
    private javax.swing.JComboBox<String> CB_andor3;
    private javax.swing.JComboBox<String> CB_andor4;
    private javax.swing.JComboBox<String> CB_andor5;
    private javax.swing.JComboBox<String> CB_andor6;
    private javax.swing.JComboBox<String> CB_class;
    private javax.swing.JComboBox<String> CB_class1;
    private javax.swing.JComboBox<String> CB_cytology;
    private javax.swing.JComboBox<String> CB_cytology1;
    private javax.swing.JComboBox<String> CB_immuno;
    private javax.swing.JComboBox<String> CB_immuno1;
    private javax.swing.JComboBox<String> CB_immuno2;
    private javax.swing.JComboBox<String> CB_immuno21;
    private javax.swing.JComboBox<String> CB_immuno4;
    private javax.swing.JComboBox<String> CB_immuno5;
    private javax.swing.JComboBox<String> CB_specST;
    private javax.swing.JComboBox<String> CB_specST1;
    private javax.swing.JComboBox<String> ComboBox_projPat;
    private javax.swing.JComboBox<String> ComboBox_stdyPat;
    private javax.swing.JPanel Info_top4;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_Search;
    private javax.swing.JButton btn_test;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem cpLabIds;
    private javax.swing.JMenuItem cpPatIds;
    private javax.swing.JMenuItem cpResultIds;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox_majS;
    private javax.swing.JCheckBox jCheckBox_ngsS1;
    private javax.swing.JCheckBox jCheckBox_ngsS2;
    private javax.swing.JCheckBox jCheckBox_othS;
    private javax.swing.JCheckBox jCheckBox_specS1;
    private javax.swing.JCheckBox jCheckBox_specS2;
    private javax.swing.JCheckBox jCheckBox_specS3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JPopupMenu popUpResult;
    private javax.swing.JRadioButton rbtn_CYT;
    private javax.swing.JRadioButton rbtn_CYT1;
    private javax.swing.JRadioButton rbtn_IMM;
    private javax.swing.JRadioButton rbtn_IMM1;
    private javax.swing.JRadioButton rbtn_IMM2;
    private javax.swing.JRadioButton rbtn_IMM21;
    private javax.swing.JRadioButton rbtn_IMM4;
    private javax.swing.JRadioButton rbtn_IMM5;
    private javax.swing.JRadioButton rbtn_NOT1_1;
    private javax.swing.JRadioButton rbtn_NOT1_2;
    private javax.swing.JRadioButton rbtn_NOT2_1;
    private javax.swing.JRadioButton rbtn_NOT2_2;
    private javax.swing.JRadioButton rbtn_NOT3_1;
    private javax.swing.JRadioButton rbtn_NOT3_2;
    private javax.swing.JRadioButton rbtn_NOT4_1;
    private javax.swing.JRadioButton rbtn_NOT4_2;
    private javax.swing.JRadioButton rbtn_NOT4_7;
    private javax.swing.JRadioButton rbtn_NOT4_8;
    private javax.swing.JRadioButton rbtn_NOT5_1;
    private javax.swing.JRadioButton rbtn_NOT5_2;
    private javax.swing.JRadioButton rbtn_RG;
    private javax.swing.JRadioButton rbtn_RG1;
    private javax.swing.JRadioButton rbtn_all_class;
    private javax.swing.JRadioButton rbtn_all_cytology;
    private javax.swing.JRadioButton rbtn_all_immuno;
    private javax.swing.JRadioButton rbtn_all_immuno2;
    private javax.swing.JRadioButton rbtn_all_some;
    private javax.swing.JRadioButton rbtn_all_subtypes;
    private javax.swing.JRadioButton rbtn_all_subtypes1;
    private javax.swing.JRadioButton rbtn_class;
    private javax.swing.JRadioButton rbtn_cytology;
    private javax.swing.JRadioButton rbtn_idCollected;
    private javax.swing.JRadioButton rbtn_immuno;
    private javax.swing.JRadioButton rbtn_immuno2;
    private javax.swing.JRadioButton rbtn_marker;
    private javax.swing.JRadioButton rbtn_onlyPat;
    private javax.swing.JRadioButton rbtn_onlyPat1;
    private javax.swing.JRadioButton rbtn_somelab2;
    private javax.swing.JRadioButton rbtn_specST;
    private javax.swing.JRadioButton rbtn_specST1;
    private javax.swing.JRadioButton rbtn_subtype;
    private javax.swing.JRadioButton rbtn_subtype1;
    private javax.swing.JTable table_RgClassLab;
    private javax.swing.JTable table_resultID;
    private javax.swing.JTextField txt_class;
    private javax.swing.JTextField txt_class1;
    private javax.swing.JTextField txt_cytology;
    private javax.swing.JTextField txt_cytology1;
    private javax.swing.JTextField txt_immuno;
    private javax.swing.JTextField txt_immuno1;
    private javax.swing.JTextField txt_immuno2;
    private javax.swing.JTextField txt_immuno21;
    private javax.swing.JTextField txt_immuno4;
    private javax.swing.JTextField txt_immuno5;
    private javax.swing.JTextField txt_specST;
    private javax.swing.JTextField txt_specST1;
    private javax.swing.JTextField txt_subA;
    private javax.swing.JTextField txt_subB;
    // End of variables declaration//GEN-END:variables
}
