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

/**
 *
 * @author gerda.modarres
 */
public class SampleBrowse extends javax.swing.JFrame {

    String ids = null;
    JTable outTable = null;  
    static String SB_resultIDs = null;
    
    Log my_log;
        
    /**
     * Creates new form PatientBrowse
     */
    public SampleBrowse() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        initial_table_sample();
        initial_table_resultID();
        Info_top4.getRootPane().setDefaultButton(btn_Search);
        
        my_log.logger.info("open SampleBrowse()");
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
    
    private void initial_table_sample(){
    
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT pat_id, fm_sample_no as 'FM sample', lab_id, corr_lab_id, comm as comment, material, punct_date, rec_date, ref_diag FROM sample";
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            table_sample.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_sample);
            
            if (table_sample.getColumnModel().getColumnCount() > 0) {
                table_sample.getColumnModel().getColumn(0).setPreferredWidth(60);
                table_sample.getColumnModel().getColumn(0).setMaxWidth(60);
                table_sample.getColumnModel().getColumn(1).setPreferredWidth(80);
                table_sample.getColumnModel().getColumn(1).setMaxWidth(100);
                table_sample.getColumnModel().getColumn(2).setPreferredWidth(90);   // 80
                table_sample.getColumnModel().getColumn(2).setMaxWidth(100);        //100
                table_sample.getColumnModel().getColumn(3).setPreferredWidth(90);   // 80
                table_sample.getColumnModel().getColumn(3).setMaxWidth(100);
                table_sample.getColumnModel().getColumn(4).setPreferredWidth(120);
                table_sample.getColumnModel().getColumn(4).setMaxWidth(300);
                table_sample.getColumnModel().getColumn(5).setPreferredWidth(90);   
                table_sample.getColumnModel().getColumn(5).setMaxWidth(120);
                table_sample.getColumnModel().getColumn(6).setPreferredWidth(90);   // 80
                table_sample.getColumnModel().getColumn(6).setMaxWidth(90);         // 80
                table_sample.getColumnModel().getColumn(7).setPreferredWidth(90);   // 80
                table_sample.getColumnModel().getColumn(7).setMaxWidth(90);         // 80  
            }
            
            get_ids(sql,pst,rs,conn);
            
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
        String sql = "SELECT p.pat_id, m.result_id, m.lab_id FROM main_result m, sample s, patient p"
                + " Where m.lab_id=s.lab_id and s.pat_id=p.pat_id;";
        
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

            String sql = "SELECT p.pat_id, m.result_id, m.lab_id FROM main_result m, sample s, patient p"
                    + " Where m.lab_id=s.lab_id and s.pat_id=p.pat_id"
                    + " AND m.lab_id in (" + all_ids + ")";
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
                
                get_r_ids(sql,pst,rs,conn);
                my_log.logger.info("SQL:  " + sql);
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
        }
    }
     
    private void get_r_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn) {
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
            this.SB_resultIDs = all_r_ids;
            //txtArea_test.setText(all_r_ids);

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
    }
        
        
    private void get_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn) {        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            String id_rem = "";
            
            while (rs.next()) {
                //this.rs_sizeList.add(rs.getString("array_sub_id"));
                String id = rs.getString("lab_id");
                
                if (!id.equals(id_rem)){
                    id_rem = id;
                    all_ids = all_ids +"'"+id+"',";
                }else{
                    //JOptionPane.showMessageDialog(null, "id already in list: " + id + "  "+ id_rem); // test
                }
                //Combobox_id.addItem(id);        // test
                //txtArea_test.append("'"+id+"',");  // test
            }
            this.ids = all_ids;

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
    }
     
    private static boolean isRightClick(MouseEvent e) {
        return (e.getButton() == MouseEvent.BUTTON3
                || (System.getProperty("os.name").contains("Mac OS X")
                && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0
                && (e.getModifiers() & InputEvent.CTRL_MASK) != 0));
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
        table_sample = new javax.swing.JTable();
        Info_top4 = new javax.swing.JPanel();
        rbtn_all = new javax.swing.JRadioButton();
        rbtn_corr = new javax.swing.JRadioButton();
        btn_Search = new javax.swing.JButton();
        rbtn_male = new javax.swing.JRadioButton();
        rbtn_female = new javax.swing.JRadioButton();
        rbtn_bdate = new javax.swing.JRadioButton();
        txt_date1 = new javax.swing.JTextField();
        txt_date2 = new javax.swing.JTextField();
        rbtn_patID = new javax.swing.JRadioButton();
        txt_patID = new javax.swing.JTextField();
        rbtn_refDiag = new javax.swing.JRadioButton();
        txt_refDiag = new javax.swing.JTextField();
        rbtn_NOT = new javax.swing.JRadioButton();
        rbtn_MDown = new javax.swing.JRadioButton();
        CB_MDown = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_resultID = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtArea_test = new javax.swing.JTextArea();
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
        setTitle("Linked Results Analysis Tool - browse samples");

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

        table_sample.setAutoCreateRowSorter(true);
        table_sample.setModel(new javax.swing.table.DefaultTableModel(
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
        table_sample.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(table_sample);
        table_sample.getAccessibleContext().setAccessibleName("table_sample");

        Info_top4.setBackground(new java.awt.Color(102, 153, 255));
        Info_top4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Info_top4.setRequestFocusEnabled(false);

        buttonGroup1.add(rbtn_all);
        rbtn_all.setSelected(true);
        rbtn_all.setText("all samples");
        rbtn_all.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all.setBorderPainted(true);

        buttonGroup1.add(rbtn_corr);
        rbtn_corr.setText("correlating lab_id");
        rbtn_corr.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_corr.setBorderPainted(true);

        btn_Search.setBackground(new java.awt.Color(0, 140, 140));
        btn_Search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Search.png"))); // NOI18N
        btn_Search.setText("Search");
        btn_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SearchActionPerformed(evt);
            }
        });

        buttonGroup1.add(rbtn_male);
        rbtn_male.setText("male samples");
        rbtn_male.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_male.setBorderPainted(true);

        buttonGroup1.add(rbtn_female);
        rbtn_female.setText("female samples");
        rbtn_female.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_female.setBorderPainted(true);

        buttonGroup1.add(rbtn_bdate);
        rbtn_bdate.setText("s. w. pat. b_date between");
        rbtn_bdate.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_bdate.setBorderPainted(true);

        buttonGroup1.add(rbtn_patID);
        rbtn_patID.setText("pat_id in");
        rbtn_patID.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_patID.setBorderPainted(true);

        buttonGroup1.add(rbtn_refDiag);
        rbtn_refDiag.setText("s. with ref. diag.");
        rbtn_refDiag.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_refDiag.setBorderPainted(true);

        rbtn_NOT.setText("NOT");
        rbtn_NOT.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_NOT.setBorderPainted(true);

        buttonGroup1.add(rbtn_MDown);
        rbtn_MDown.setText("Mb.Down");
        rbtn_MDown.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_MDown.setBorderPainted(true);

        CB_MDown.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "N", "Y" }));

        javax.swing.GroupLayout Info_top4Layout = new javax.swing.GroupLayout(Info_top4);
        Info_top4.setLayout(Info_top4Layout);
        Info_top4Layout.setHorizontalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(rbtn_all, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtn_male, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtn_female, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rbtn_corr, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Info_top4Layout.createSequentialGroup()
                        .addComponent(rbtn_MDown, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CB_MDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(rbtn_patID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtn_bdate, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(Info_top4Layout.createSequentialGroup()
                        .addComponent(rbtn_refDiag)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(rbtn_NOT)))
                .addGap(5, 5, 5)
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_patID)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Info_top4Layout.createSequentialGroup()
                        .addComponent(txt_date1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txt_date2, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txt_refDiag))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 448, Short.MAX_VALUE)
                .addComponent(btn_Search)
                .addGap(21, 21, 21))
        );
        Info_top4Layout.setVerticalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtn_all)
                    .addComponent(rbtn_bdate)
                    .addComponent(txt_date1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txt_date2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtn_corr)
                    .addComponent(btn_Search))
                .addGap(4, 4, 4)
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtn_patID)
                    .addComponent(txt_patID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtn_male)
                    .addComponent(rbtn_MDown)
                    .addComponent(CB_MDown, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtn_refDiag)
                    .addComponent(txt_refDiag, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rbtn_female)
                    .addComponent(rbtn_NOT))
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

        txtArea_test.setColumns(20);
        txtArea_test.setRows(5);
        jScrollPane3.setViewportView(txtArea_test);

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_rowsReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Info_top4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_rowsReturned)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        Info_top4.getAccessibleContext().setAccessibleName("samples");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void btn_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SearchActionPerformed
        if(rbtn_all.isSelected()){
            initial_table_sample();
            update_table_resultID();
        } else if (rbtn_corr.isSelected()) {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            String sql = "SELECT a.pat_id, a.fm_sample_no as 'FM sample', a.lab_id, a.corr_lab_id, a.comm as comment, a.material, a.punct_date, a.rec_date, a.ref_diag FROM sample a"
                    + " WHERE pat_id in (SELECT pat_id FROM sample"
                    + " GROUP BY pat_id"
                    + " HAVING COUNT(pat_id) > 1);";
            
            my_log.logger.info("SQL:  " + sql);
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_sample.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_sample);                
                if (table_sample.getColumnModel().getColumnCount() > 0) {
                    table_sample.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_sample.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_sample.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_sample.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(2).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(2).setMaxWidth(100);        //100
                    table_sample.getColumnModel().getColumn(3).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(4).setPreferredWidth(120);
                    table_sample.getColumnModel().getColumn(4).setMaxWidth(300);
                    table_sample.getColumnModel().getColumn(5).setPreferredWidth(90);
                    table_sample.getColumnModel().getColumn(5).setMaxWidth(120);
                    table_sample.getColumnModel().getColumn(6).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(6).setMaxWidth(90);         // 80
                    table_sample.getColumnModel().getColumn(7).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(7).setMaxWidth(90);         // 80  
                }

                get_ids(sql, pst, rs, conn);
                update_table_resultID();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                my_log.logger.warning("ERROR: " + e);
            } finally {
                try {
                    if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }
        } else if (rbtn_male.isSelected()) {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            String sql = "SELECT s.pat_id, s.fm_sample_no as 'FM sample', s.lab_id, s.corr_lab_id, s.comm as comment, s.material, s.punct_date, s.rec_date, s.ref_diag FROM sample s, patient p"
                    + " where s.pat_id=p.pat_id"
                    + " and p.sex='M';";
            my_log.logger.info("SQL:  " + sql);
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_sample.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_sample);
                if (table_sample.getColumnModel().getColumnCount() > 0) {
                    table_sample.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_sample.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_sample.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_sample.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(2).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(2).setMaxWidth(100);        //100
                    table_sample.getColumnModel().getColumn(3).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(4).setPreferredWidth(120);
                    table_sample.getColumnModel().getColumn(4).setMaxWidth(300);
                    table_sample.getColumnModel().getColumn(5).setPreferredWidth(90);
                    table_sample.getColumnModel().getColumn(5).setMaxWidth(120);
                    table_sample.getColumnModel().getColumn(6).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(6).setMaxWidth(90);         // 80
                    table_sample.getColumnModel().getColumn(7).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(7).setMaxWidth(90);         // 80  
                }

                get_ids(sql, pst, rs, conn);
                update_table_resultID();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                my_log.logger.warning("ERROR: " + e);
            } finally {
                try {
                    if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }
        } else if (rbtn_female.isSelected()) {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            String sql = "SELECT s.pat_id, s.fm_sample_no as 'FM sample', s.lab_id, s.corr_lab_id, s.comm as comment, s.material, s.punct_date, s.rec_date, s.ref_diag FROM sample s, patient p"
                    + " where s.pat_id=p.pat_id"
                    + " and p.sex='F';";
            my_log.logger.info("SQL:  " + sql);
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_sample.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_sample);
                if (table_sample.getColumnModel().getColumnCount() > 0) {
                    table_sample.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_sample.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_sample.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_sample.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(2).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(2).setMaxWidth(100);        //100
                    table_sample.getColumnModel().getColumn(3).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(4).setPreferredWidth(120);
                    table_sample.getColumnModel().getColumn(4).setMaxWidth(300);
                    table_sample.getColumnModel().getColumn(5).setPreferredWidth(90);
                    table_sample.getColumnModel().getColumn(5).setMaxWidth(120);
                    table_sample.getColumnModel().getColumn(6).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(6).setMaxWidth(90);         // 80
                    table_sample.getColumnModel().getColumn(7).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(7).setMaxWidth(90);         // 80  
                }

                get_ids(sql, pst, rs, conn);
                update_table_resultID();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                my_log.logger.warning("ERROR: " + e);
            } finally {
                try {
                    if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }
        } else if (rbtn_bdate.isSelected()) {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            String date1 = txt_date1.getText();
            String date2 = txt_date2.getText();
            String sql = "SELECT s.pat_id, s.fm_sample_no as 'FM sample', s.lab_id, s.corr_lab_id, s.comm as comment, s.material, s.punct_date, s.rec_date, s.ref_diag FROM sample s, patient p"
                    + " where s.pat_id=p.pat_id"
                    + " and p.b_date between '" + date1 + "' and '" + date2 +"'" ;
            //txtArea_test.setText(sql);
            my_log.logger.info("SQL:  " + sql);
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_sample.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_sample);
                if (table_sample.getColumnModel().getColumnCount() > 0) {
                    table_sample.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_sample.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_sample.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_sample.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(2).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(2).setMaxWidth(100);        //100
                    table_sample.getColumnModel().getColumn(3).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(4).setPreferredWidth(120);
                    table_sample.getColumnModel().getColumn(4).setMaxWidth(300);
                    table_sample.getColumnModel().getColumn(5).setPreferredWidth(90);
                    table_sample.getColumnModel().getColumn(5).setMaxWidth(120);
                    table_sample.getColumnModel().getColumn(6).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(6).setMaxWidth(90);         // 80
                    table_sample.getColumnModel().getColumn(7).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(7).setMaxWidth(90);         // 80  
                }
                                       
                get_ids(sql, pst, rs, conn);
                update_table_resultID();

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
        } else if (rbtn_patID.isSelected()) {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            String patID = txt_patID.getText();
            String sql = "SELECT s.pat_id, s.fm_sample_no as 'FM sample', s.lab_id, s.corr_lab_id, s.comm as comment, s.material, s.punct_date, s.rec_date, s.ref_diag FROM sample s, patient p"
                    + " where s.pat_id=p.pat_id"
                    + " and s.pat_id in ( " + patID +" )" ;
            //txtArea_test.setText(sql);
            my_log.logger.info("SQL:  " + sql);
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_sample.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_sample);
                if (table_sample.getColumnModel().getColumnCount() > 0) {
                    table_sample.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_sample.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_sample.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_sample.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(2).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(2).setMaxWidth(100);        //100
                    table_sample.getColumnModel().getColumn(3).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(4).setPreferredWidth(120);
                    table_sample.getColumnModel().getColumn(4).setMaxWidth(300);
                    table_sample.getColumnModel().getColumn(5).setPreferredWidth(90);
                    table_sample.getColumnModel().getColumn(5).setMaxWidth(120);
                    table_sample.getColumnModel().getColumn(6).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(6).setMaxWidth(90);         // 80
                    table_sample.getColumnModel().getColumn(7).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(7).setMaxWidth(90);         // 80  
                }

                get_ids(sql, pst, rs, conn);
                update_table_resultID();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    my_log.logger.warning("ERROR: " + e);
                } finally {
                    try {
                        if (rs != null) { rs.close();}
                        if (pst != null) { pst.close();}
                        if (conn != null) { conn.close();}
                    } catch (Exception e) {
                    }
                }
        } else if (rbtn_refDiag.isSelected()){
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            String refDiag = txt_refDiag.getText();            
            String sql = "SELECT s.pat_id, s.fm_sample_no as 'FM sample', s.lab_id, s.corr_lab_id, s.comm as comment, s.material, s.punct_date, s.rec_date, s.ref_diag FROM sample s, patient p"
                    + " where s.pat_id=p.pat_id"
                    + " and ref_diag like  ('%" + refDiag +"%')" ;
            if(rbtn_NOT.isSelected()){
                    sql = "SELECT s.pat_id, s.fm_sample_no as 'FM sample', s.lab_id, s.corr_lab_id, s.comm as comment, s.material, s.punct_date, s.rec_date, s.ref_diag FROM sample s, patient p"
                    + " where s.pat_id=p.pat_id"
                    + " and ref_diag NOT like  ('%" + refDiag +"%')" ;
            } 
            my_log.logger.info("SQL:  " + sql);

            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_sample.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_sample);
                if (table_sample.getColumnModel().getColumnCount() > 0) {
                    table_sample.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_sample.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_sample.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_sample.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(2).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(2).setMaxWidth(100);        //100
                    table_sample.getColumnModel().getColumn(3).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(4).setPreferredWidth(120);
                    table_sample.getColumnModel().getColumn(4).setMaxWidth(300);
                    table_sample.getColumnModel().getColumn(5).setPreferredWidth(90);
                    table_sample.getColumnModel().getColumn(5).setMaxWidth(120);
                    table_sample.getColumnModel().getColumn(6).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(6).setMaxWidth(90);         // 80
                    table_sample.getColumnModel().getColumn(7).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(7).setMaxWidth(90);         // 80  
                }

                get_ids(sql, pst, rs, conn);
                update_table_resultID();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                my_log.logger.warning("ERROR: " + e);
            } finally {
                try {
                    if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }

        }else if (rbtn_MDown.isSelected()) {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            String MDown = CB_MDown.getSelectedItem().toString();
            
            String sql = "SELECT s.pat_id, s.fm_sample_no as 'FM sample', s.lab_id, s.corr_lab_id, s.comm as comment, s.material, s.punct_date, s.rec_date, s.ref_diag FROM sample s, patient p"
                    + " where s.pat_id=p.pat_id"
                    + " and mb_down = '" + MDown + "'";
            my_log.logger.info("SQL:  " + sql);
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_sample.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_sample);
                if (table_sample.getColumnModel().getColumnCount() > 0) {
                    table_sample.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_sample.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_sample.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_sample.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(2).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(2).setMaxWidth(100);        //100
                    table_sample.getColumnModel().getColumn(3).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_sample.getColumnModel().getColumn(4).setPreferredWidth(120);
                    table_sample.getColumnModel().getColumn(4).setMaxWidth(300);
                    table_sample.getColumnModel().getColumn(5).setPreferredWidth(90);
                    table_sample.getColumnModel().getColumn(5).setMaxWidth(120);
                    table_sample.getColumnModel().getColumn(6).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(6).setMaxWidth(90);         // 80
                    table_sample.getColumnModel().getColumn(7).setPreferredWidth(90);   // 80
                    table_sample.getColumnModel().getColumn(7).setMaxWidth(90);         // 80  
                }
            
                get_ids(sql, pst, rs, conn);
                update_table_resultID();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
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
        JTable OT = this.outTable;
        String IDs = "";
        int resultL = OT.getRowCount();
        for(int i = 0; i < resultL; i++) {
            String tmp = OT.getValueAt(i, 1).toString();
            IDs = IDs + tmp + ", "; 
        }
        IDs = IDs.substring(0, (IDs.length() - 2));
        StringSelection somestring = new StringSelection(IDs);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(somestring, null);        
    }//GEN-LAST:event_cpResultIdsActionPerformed

    private void cpLabIdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpLabIdsActionPerformed
        JTable OT = this.outTable;
        String IDs = "";
        int resultL = OT.getRowCount();
        for(int i = 0; i < resultL; i++) {
            String tmp = OT.getValueAt(i, 2).toString();
            IDs = IDs + tmp + ", "; 
        }
        IDs = IDs.substring(0, (IDs.length() - 2));
        //txtArea_test.setText(IDs);  // test
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( 
                new StringSelection(IDs), null);
    }//GEN-LAST:event_cpLabIdsActionPerformed

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
            java.util.logging.Logger.getLogger(SampleBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SampleBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SampleBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SampleBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SampleBrowse().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CB_MDown;
    private javax.swing.JPanel Info_top4;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_Search;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem cpLabIds;
    private javax.swing.JMenuItem cpResultIds;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JPopupMenu popUpResult;
    private javax.swing.JRadioButton rbtn_MDown;
    private javax.swing.JRadioButton rbtn_NOT;
    private javax.swing.JRadioButton rbtn_all;
    private javax.swing.JRadioButton rbtn_bdate;
    private javax.swing.JRadioButton rbtn_corr;
    private javax.swing.JRadioButton rbtn_female;
    private javax.swing.JRadioButton rbtn_male;
    private javax.swing.JRadioButton rbtn_patID;
    private javax.swing.JRadioButton rbtn_refDiag;
    private javax.swing.JTable table_resultID;
    private javax.swing.JTable table_sample;
    private javax.swing.JTextArea txtArea_test;
    private javax.swing.JTextField txt_date1;
    private javax.swing.JTextField txt_date2;
    private javax.swing.JTextField txt_patID;
    private javax.swing.JTextField txt_refDiag;
    // End of variables declaration//GEN-END:variables
}
