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

import java.awt.Font;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import myClass.DBconnect;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author gerda.modarres
 */
class TableStructure extends javax.swing.JFrame {
    //public static javax.swing.JFrame SetTableStructureRef;
    
    /**
     * Creates new form tableStructure
     */
    TableStructure() {
        initComponents();
        tbl_structure.getTableHeader().setFont(new Font("Tahoma", Font.ITALIC, 10));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tbl_structure = new javax.swing.JTable();
        rbtn_alwaysOnTop = new javax.swing.JRadioButton();
        rbtn_summaryList = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("CCRI STD patBD");

        tbl_structure.setAutoCreateRowSorter(true);
        tbl_structure.setModel(new javax.swing.table.DefaultTableModel(
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
        tbl_structure.setDoubleBuffered(true);
        tbl_structure.setDragEnabled(true);
        jScrollPane1.setViewportView(tbl_structure);

        rbtn_alwaysOnTop.setText("always on top");
        rbtn_alwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_alwaysOnTopActionPerformed(evt);
            }
        });

        rbtn_summaryList.setText("summary list only");
        rbtn_summaryList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_summaryListActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rbtn_alwaysOnTop)
                        .addGap(47, 47, 47)
                        .addComponent(rbtn_summaryList)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1154, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rbtn_alwaysOnTop)
                    .addComponent(rbtn_summaryList))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbtn_alwaysOnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_alwaysOnTopActionPerformed
        if(rbtn_alwaysOnTop.isSelected()){
            this.setAlwaysOnTop(true);
        } else {
            this.setAlwaysOnTop(false);
        }
    }//GEN-LAST:event_rbtn_alwaysOnTopActionPerformed

    private void rbtn_summaryListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_summaryListActionPerformed
        if (rbtn_summaryList.isSelected()) {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            try {
                String sql = "SELECT table_name, GROUP_CONCAT(column_name ORDER BY ordinal_position) as 'columns'"+
                        "FROM information_schema.columns WHERE table_schema = DATABASE()"+
                        "GROUP BY table_name ORDER BY table_name;";	
                
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                //if (rs.next()) {      // this skips first row!   
                TableStructure.tbl_structure.setModel(DbUtils.resultSetToTableModel(rs));
                if (tbl_structure.getColumnModel().getColumnCount() > 0) {
                    tbl_structure.getColumnModel().getColumn(0).setPreferredWidth(100);
                    tbl_structure.getColumnModel().getColumn(0).setMaxWidth(500);
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
        } else {
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            try {
                String sql = "select TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, "
                        + "CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, COLUMN_TYPE, COLUMN_KEY, EXTRA "
                        + "from information_schema.columns where table_schema = DATABASE() order by table_name,ordinal_position;";

                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();
                //if (rs.next()) {      // this skips first row!   
                TableStructure.tbl_structure.setModel(DbUtils.resultSetToTableModel(rs));
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
    }//GEN-LAST:event_rbtn_summaryListActionPerformed

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
            java.util.logging.Logger.getLogger(TableStructure.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TableStructure.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TableStructure.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TableStructure.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TableStructure().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbtn_alwaysOnTop;
    private javax.swing.JRadioButton rbtn_summaryList;
    public static javax.swing.JTable tbl_structure;
    // End of variables declaration//GEN-END:variables
}
