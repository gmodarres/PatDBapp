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

import java.awt.Desktop;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import myClass.CustomSorter;
import myClass.DBconnect;
import myClass.IdManagement;
import myClass.Log;
import myClass.OSDetector;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author gerda.modarres
 */
public class PatientBrowse extends javax.swing.JFrame {
    
    String ids = null;
    static String PB_resultIDs = null;
        
    Log my_log;

    /**
     * Creates new form PatientBrowse
     */
    public PatientBrowse() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        initial_table_patient();
        
        Info_top4.getRootPane().setDefaultButton(btn_Search);
        
        my_log.logger.info("open PatientBrowse()");
    }

    private void showRows(ResultSet rs) {
        try {
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                lbl_rowsReturned.setText(getRows + " row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SampleBrowse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         
    private void initial_table_patient() {

        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT p.pat_id, fm_pat_no, fname, surname, surname_old, sex, b_date, dg_date, mb_down as MDown, stdy_name, pat_study_id as stdy_ID, stdy_group, mon_pat as monitor, proj_name"
                    + " FROM patient p, pat_instudy ps, pat_inproject pj, study s, project j"
                    + " WHERE p.pat_id=ps.pat_id and p.pat_id=pj.pat_id "
                    + " AND s.stdy_id=ps.stdy_id and j.proj_id=pj.proj_id ";

        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_patient.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_patient);      
            if (table_patient.getColumnModel().getColumnCount() > 0) {
                table_patient.getColumnModel().getColumn(0).setPreferredWidth(80);
                table_patient.getColumnModel().getColumn(0).setMaxWidth(100);
                table_patient.getColumnModel().getColumn(1).setPreferredWidth(80);
                table_patient.getColumnModel().getColumn(1).setMaxWidth(100);
                table_patient.getColumnModel().getColumn(2).setPreferredWidth(140);
                table_patient.getColumnModel().getColumn(2).setMaxWidth(170);
                table_patient.getColumnModel().getColumn(3).setPreferredWidth(140);
                table_patient.getColumnModel().getColumn(3).setMaxWidth(170);
                table_patient.getColumnModel().getColumn(4).setPreferredWidth(120);
                table_patient.getColumnModel().getColumn(4).setMaxWidth(170);
                table_patient.getColumnModel().getColumn(5).setPreferredWidth(60); // Sex
                table_patient.getColumnModel().getColumn(5).setMaxWidth(60);
                table_patient.getColumnModel().getColumn(6).setPreferredWidth(90);
                table_patient.getColumnModel().getColumn(6).setMaxWidth(120);
                table_patient.getColumnModel().getColumn(7).setPreferredWidth(90);
                table_patient.getColumnModel().getColumn(7).setMaxWidth(120);
                table_patient.getColumnModel().getColumn(8).setPreferredWidth(60); // MDown
                table_patient.getColumnModel().getColumn(8).setMaxWidth(60);
                table_patient.getColumnModel().getColumn(10).setPreferredWidth(70); // stdy_ID
                table_patient.getColumnModel().getColumn(10).setMaxWidth(100);
                table_patient.getColumnModel().getColumn(11).setPreferredWidth(80); // stdy_group
                table_patient.getColumnModel().getColumn(11).setMaxWidth(100);
                table_patient.getColumnModel().getColumn(12).setPreferredWidth(60); // monitoring
                table_patient.getColumnModel().getColumn(12).setMaxWidth(100);   
            }
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
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
        jToolBar1 = new javax.swing.JToolBar();
        bnt_test = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_patient = new javax.swing.JTable();
        lbl_rowsReturned = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1_openModel = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1_HowTo = new javax.swing.JMenuItem();
        jMenuItem2_Info = new javax.swing.JMenuItem();

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

        javax.swing.GroupLayout Info_top4Layout = new javax.swing.GroupLayout(Info_top4);
        Info_top4.setLayout(Info_top4Layout);
        Info_top4Layout.setHorizontalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_Search)
                .addGap(21, 21, 21))
        );
        Info_top4Layout.setVerticalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Search))
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
        jScrollPane1.setViewportView(table_patient);
        table_patient.getAccessibleContext().setAccessibleName("table_patient");

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
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(lbl_rowsReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Info_top4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1292, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_rowsReturned)
                .addGap(12, 12, 12))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

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

    private void jMenuItem1_HowToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1_HowToActionPerformed
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/Monsters-Snail-icon.png"));
        JOptionPane.showMessageDialog(rootPane, "... ummmmmm \n... errrrrr \n... pls ask again later", "apparently no useful Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem1_HowToActionPerformed

    private void jMenuItem2_InfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2_InfoActionPerformed
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_med.png"));
        JOptionPane.showMessageDialog(rootPane, "LInkedResultsAnalysis \nDB-request Tool\nVersion:   1.0.0", "Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem2_InfoActionPerformed

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
            initial_table_patient();
            
        } else if (rbtn_searchCritMain.isSelected()){
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;          
            String sql = "SELECT p.pat_id, fm_pat_no, fname, surname, surname_old, sex, b_date, dg_date, mb_down as MDown, stdy_name, pat_study_id as stdy_ID, stdy_group, mon_pat as monitor, proj_name"
                    + " FROM patient p, pat_instudy ps, pat_inproject pj, study s, project j"
                    + " WHERE p.pat_id=ps.pat_id and p.pat_id=pj.pat_id "
                    + " AND s.stdy_id=ps.stdy_id and j.proj_id=pj.proj_id ";
            
            if (rbtn_searchCrit1.isSelected()) {
                String searchCrit1_select = CB_searchCrit1.getSelectedItem().toString();
                String searchCrit1 = "";
                String sCrit1_txt = txt_searchCrit1.getText();
                String select1 = "";                
                switch (searchCrit1_select) {
                    case "pat_id":
                        searchCrit1 = "p.pat_id";
                        select1 = "ID";
                        break;
                    case "fm_pat_no":
                        searchCrit1 = "fm_pat_no";
                        select1 = "ID";
                        break;
                    case "fname":
                        searchCrit1 = "fname";
                        select1 = "TXT";
                        break;
                    case "surname":
                        searchCrit1 = "surname";
                        select1 = "TXT";
                        break;
                    case "surname old":
                        searchCrit1 = "surname_old";
                        select1 = "TXT";
                        break;
                    case "sex":
                        searchCrit1 = "sex";
                        select1 = "TXT";
                        break;
                    case "birth date":
                        searchCrit1 = "b_date";
                        select1 ="DATE";
                        break;
                    case "diagnosis date":
                        searchCrit1 = "dg_date";
                        select1 = "DATE";
                        break;
                    case "mb. down":
                        searchCrit1 = "mb_down";
                        select1 = "TXT";
                        break;
                    case "study":
                        searchCrit1 = "stdy_name";
                        select1 = "TXT";
                        break;
                    case "study_id":
                        searchCrit1 = "pat_study_id";
                        select1 = "TXT";
                        break;
                    case "stdy_group":
                        searchCrit1 = "stdy_group";
                        select1 = "NULLable";
                        break; 
                    case "monitoring":
                        searchCrit1 = "mon_pat";
                        select1 = "NULLable";
                        break;
                    case "project":
                        searchCrit1 = "proj_name";
                        break;
                    default:
                        break;
                }

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
                    
                    if (splitDate[0].equals("between")){
                        //JOptionPane.showMessageDialog(null, "1;2;3 " +splitDate[2]);
                        sql = sql + " and (" + searchCrit1 + " " + splitDate[0] +" '"+splitDate[1]+"' and '" + splitDate[2] +"'" ;
                    }else if (splitDate[0].equals(">") || splitDate[0].equals("<") || splitDate[0].equals("=") ) {
                        //JOptionPane.showMessageDialog(null, "1;2 " +splitDate[1]);
                        sql = sql + " and (" + searchCrit1 +" " +splitDate[0] +" '"+splitDate[1]+"'" ;
                    }
                    //sql = sql + " and " + searchCrit1 + " " + sCrit1_txt;
                } else if (select1.equals("NULLable")){
                    if (rbtn_NOT1.isSelected()) {
                        if (sCrit1_txt.equals("null") || sCrit1_txt.equals("NULL")) {
                            sql = sql + " and (" + searchCrit1 + " IS NOT NULL";
                        } else {
                            sql = sql + " and (" + searchCrit1 + " NOT like '%" + sCrit1_txt + "%'";
                        }
                        
                    }else {
                        if (sCrit1_txt.equals("null") || sCrit1_txt.equals("NULL")) {
                            sql = sql + " and (" + searchCrit1 + " IS NULL";
                        } else {
                            sql = sql + " and (" + searchCrit1 + " like '%" + sCrit1_txt + "%'";
                        }
                    }
                }
            }
            
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
                        break;
                    case "fm_pat_no":
                        searchCrit2 = "fm_pat_no";
                        select2 = "ID";
                        break;
                    case "fname":
                        searchCrit2 = "fname";
                        select2 = "TXT";
                        break;
                    case "surname":
                        searchCrit2 = "surname";
                        select2 = "TXT";
                        break;
                    case "surname old":
                        searchCrit2 = "surname_old";
                        select2 = "TXT";
                        break;
                    case "sex":
                        searchCrit2 = "sex";
                        select2 = "TXT";
                        break;
                    case "birth date":
                        searchCrit2 = "b_date";
                        select2 ="DATE";
                        break;
                    case "diagnosis date":
                        searchCrit2 = "dg_date";
                        select2 = "DATE";
                        break;
                    case "mb. down":
                        searchCrit2 = "mb_down";
                        select2 = "TXT";
                        break;
                    case "study":
                        searchCrit2 = "stdy_name";
                        select2 = "TXT";
                        break;
                    case "study_id":
                        searchCrit2 = "pat_study_id";
                        select2 = "TXT";
                        break;
                    case "stdy_group":
                        searchCrit2 = "stdy_group";
                        select2 = "NULLable";
                        break; 
                    case "monitoring":
                        searchCrit2 = "mon_pat";
                        select2 = "NULLable";
                        break;
                    case "project":
                        searchCrit2 = "proj_name";
                        break;
                    default:
                        break;
                }

                if (select2.equals("ID")) {
                    if (rbtn_NOT2.isSelected()) {
                        sql = sql +  " " + andor1 + " "+ searchCrit2 + " not in ( " + sCrit2_txt + " ))";
                    } else {
                        sql = sql +  " " + andor1 + " " + searchCrit2 + " in ( " + sCrit2_txt + " ))";
                    }
                } else if (select2.equals("TXT")) {
                    if (rbtn_NOT2.isSelected()) {
                        sql = sql + " " + andor1 + " "+ searchCrit2 + " NOT like '%" + sCrit2_txt + "%')";
                    } else {
                        sql = sql +  " " + andor1 + " "+ searchCrit2 + " like '%" + sCrit2_txt + "%')";
                    }
                } else if (select2.equals("DATE")) {
                    String splitDate[] = sCrit2_txt.split(",");          //  >=,2009-01-01   between,2009-01-01;2012-01-01
                    
                    if (splitDate[0].equals("between")){
                        //JOptionPane.showMessageDialog(null, "1;2;3 " +splitDate[2]);
                        sql = sql + " " + andor1 + " "+ searchCrit2 + " " + splitDate[0] +" '"+splitDate[1]+"' and '" + splitDate[2] +"')" ;
                    }else if (splitDate[0].equals(">") || splitDate[0].equals("<") || splitDate[0].equals("=") ) {
                        //JOptionPane.showMessageDialog(null, "1;2 " +splitDate[1]);
                        sql = sql + " " + andor1 + " "+ searchCrit2 +" " +splitDate[0] +" '"+splitDate[1]+"')" ;
                    }
                } else if (select2.equals("NULLable")){
                    if (rbtn_NOT2.isSelected()) {
                        if (sCrit2_txt.equals("null") || sCrit2_txt.equals("NULL")) {
                            sql = sql +" " + andor1 + " "+ searchCrit2 + " IS NOT NULL)";
                        } else {
                            sql = sql + " " + andor1 + " " + searchCrit2 + " NOT like '%" + sCrit2_txt + "%')";
                        }
                        
                    }else {
                        if (sCrit2_txt.equals("null") || sCrit2_txt.equals("NULL")) {
                            sql = sql + " " + andor1 + " " + searchCrit2 + " IS NULL)";
                        } else {
                            sql = sql +" " + andor1 + " " + searchCrit2 + " like '%" + sCrit2_txt + "%')";
                        }
                    }
                }
            } else {
                sql = sql +")";
            }

            //txtArea_test.setText(sql);  //TEST
            
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                my_log.logger.info("SQL:  " + sql);
                table_patient.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_patient);
                if (table_patient.getColumnModel().getColumnCount() > 0) {
                    table_patient.getColumnModel().getColumn(0).setPreferredWidth(80);
                    table_patient.getColumnModel().getColumn(0).setMaxWidth(100);
                    table_patient.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_patient.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_patient.getColumnModel().getColumn(2).setPreferredWidth(140);
                    table_patient.getColumnModel().getColumn(2).setMaxWidth(170);
                    table_patient.getColumnModel().getColumn(3).setPreferredWidth(140);
                    table_patient.getColumnModel().getColumn(3).setMaxWidth(170);
                    table_patient.getColumnModel().getColumn(4).setPreferredWidth(120);
                    table_patient.getColumnModel().getColumn(4).setMaxWidth(170);
                    table_patient.getColumnModel().getColumn(5).setPreferredWidth(60); // Sex
                    table_patient.getColumnModel().getColumn(5).setMaxWidth(60);
                    table_patient.getColumnModel().getColumn(6).setPreferredWidth(90);
                    table_patient.getColumnModel().getColumn(6).setMaxWidth(120);
                    table_patient.getColumnModel().getColumn(7).setPreferredWidth(90);
                    table_patient.getColumnModel().getColumn(7).setMaxWidth(120);
                    table_patient.getColumnModel().getColumn(8).setPreferredWidth(60); // MDown
                    table_patient.getColumnModel().getColumn(8).setMaxWidth(60);
                    table_patient.getColumnModel().getColumn(10).setPreferredWidth(70); // stdy_ID
                    table_patient.getColumnModel().getColumn(10).setMaxWidth(120);
                    table_patient.getColumnModel().getColumn(11).setPreferredWidth(80); // stdy_group
                    table_patient.getColumnModel().getColumn(11).setMaxWidth(120);   
                    table_patient.getColumnModel().getColumn(12).setPreferredWidth(60); // monitoring
                    table_patient.getColumnModel().getColumn(12).setMaxWidth(100);                   
                }
                
                //get_ids(sql, pst, rs, conn);
                this.ids=IdManagement.get_ids(sql, pst, rs, conn, "pat_id");
                get_resultIDs();
                //get_r_ids(sql,pst,rs,conn); 
                showRows(rs);

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
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1_HowTo;
    private javax.swing.JMenuItem jMenuItem1_openModel;
    private javax.swing.JMenuItem jMenuItem2_Info;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JRadioButton rbtn_NOT1;
    private javax.swing.JRadioButton rbtn_NOT2;
    private javax.swing.JRadioButton rbtn_all;
    private javax.swing.JRadioButton rbtn_searchCrit1;
    private javax.swing.JRadioButton rbtn_searchCrit2;
    private javax.swing.JRadioButton rbtn_searchCritMain;
    private javax.swing.JTable table_patient;
    private javax.swing.JTextField txt_searchCrit1;
    private javax.swing.JTextField txt_searchCrit2;
    // End of variables declaration//GEN-END:variables
}
