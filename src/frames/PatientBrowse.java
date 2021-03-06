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
import myClass.OSDetector;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author gerda.modarres
 */
public class PatientBrowse extends javax.swing.JFrame {

    /**
     * Creates new form PatientBrowse
     */
    public PatientBrowse() {
        initComponents();
        initial_table_patient();
    }

    private void showRows(ResultSet rs) {
        try {
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                lbl_rowsReturned.setText(getRows + " row(s) returned");
                //my_log.logger.info(getRows+" row(s) returned");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SampleBrowse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
         
    private void initial_table_patient() {

        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT p.pat_id, fm_pat_no, fname, surname, surname_old, sex, b_date, dg_date, mb_down as MDown, stdy_name, pat_study_id as stdy_ID, proj_name"
                + " FROM patient p, pat_instudy ps, pat_inproject pj, study s, project j"
                + " where p.pat_id=ps.pat_id and p.pat_id=pj.pat_id "
                + " and s.stdy_id=ps.stdy_id and j.proj_id=pj.proj_id ";

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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Info_top4 = new javax.swing.JPanel();
        btn_Search = new javax.swing.JButton();
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

        Info_top4.setBackground(new java.awt.Color(102, 153, 255));
        Info_top4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Info_top4.setRequestFocusEnabled(false);

        btn_Search.setText("Search");

        javax.swing.GroupLayout Info_top4Layout = new javax.swing.GroupLayout(Info_top4);
        Info_top4.setLayout(Info_top4Layout);
        Info_top4Layout.setHorizontalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Info_top4Layout.createSequentialGroup()
                .addContainerGap(1194, Short.MAX_VALUE)
                .addComponent(btn_Search)
                .addGap(21, 21, 21))
        );
        Info_top4Layout.setVerticalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_Search)
                .addContainerGap(75, Short.MAX_VALUE))
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
        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/NeedsAName_Font_small07.png"))); // NOI18N
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
                .addComponent(jScrollPane1)
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
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/EsALiR_suite_BG_ico2-3_small.png"));
        JOptionPane.showMessageDialog(rootPane, "Needs A Name \nDB-request Tool\nVersion:   1.0.0", "Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem2_InfoActionPerformed

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

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
    private javax.swing.JPanel Info_top4;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_Search;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1_HowTo;
    private javax.swing.JMenuItem jMenuItem1_openModel;
    private javax.swing.JMenuItem jMenuItem2_Info;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JTable table_patient;
    // End of variables declaration//GEN-END:variables
}
