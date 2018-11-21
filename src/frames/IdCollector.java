/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frames;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 *
 * @author gerda.modarres
 */
public class IdCollector extends javax.swing.JFrame {

    static String COL_resultIDs = null;
    
    //String mod_sql = null;
    
    /**
     * Creates new form IdCollector
     */
    public IdCollector() {
        initComponents();

        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());

    }

    public static String deliver_collected_ids(String sql, String prefixRID, String prefixLID, String prefixPID) {    // ids from IdCollector to extend sql in Classification Search
        String caller = null;
        String ids = COL_resultIDs;
        String mod_sql = null;
        try {

            if (ids == null) {
                JOptionPane.showMessageDialog(null, "Type some IDs into that white area and press 'Apply'!");
            } else if (ids.length() > 1) {
                //ids = ids.substring(0, (ids.length() - 1));
                ids = ids.replaceAll(",","','");
                ids = "'" + ids + "'";
                if (btn_resultID.isSelected()){
                    caller = prefixRID+"result_id";
                } else if (btn_labID.isSelected()){
                    caller = prefixLID+"lab_id";
                } else if (btn_patID.isSelected()){
                    caller = prefixPID+"pat_id";
                }

                mod_sql = sql + " AND " + caller + " in (" + ids + ")";
                //JOptionPane.showMessageDialog(null,mod_sql); //TEST
            }

        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return mod_sql;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea_ids = new javax.swing.JTextArea();
        btn_applyIds = new javax.swing.JButton();
        rbtn_alwaysOnTop = new javax.swing.JRadioButton();
        btn_clear = new javax.swing.JButton();
        btn_resultID = new javax.swing.JRadioButton();
        btn_labID = new javax.swing.JRadioButton();
        btn_patID = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("collect IDs");

        txtArea_ids.setColumns(20);
        txtArea_ids.setRows(5);
        jScrollPane1.setViewportView(txtArea_ids);

        btn_applyIds.setBackground(new java.awt.Color(51, 153, 255));
        btn_applyIds.setText("Apply IDs");
        btn_applyIds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_applyIdsActionPerformed(evt);
            }
        });

        rbtn_alwaysOnTop.setText("always on top");
        rbtn_alwaysOnTop.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        rbtn_alwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_alwaysOnTopActionPerformed(evt);
            }
        });

        btn_clear.setText("clear");
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });

        buttonGroup1.add(btn_resultID);
        btn_resultID.setText("result_id");

        buttonGroup1.add(btn_labID);
        btn_labID.setText("lab_id");

        buttonGroup1.add(btn_patID);
        btn_patID.setSelected(true);
        btn_patID.setText("pat_id");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(rbtn_alwaysOnTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_resultID, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(7, 7, 7)
                        .addComponent(btn_labID, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(btn_patID, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btn_applyIds, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btn_clear, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_labID, btn_patID, btn_resultID, rbtn_alwaysOnTop});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {btn_applyIds, btn_clear});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_applyIds, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtn_alwaysOnTop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(4, 4, 4)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_clear)
                    .addComponent(btn_resultID)
                    .addComponent(btn_labID)
                    .addComponent(btn_patID))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_labID, btn_patID, btn_resultID, rbtn_alwaysOnTop});

        layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {btn_applyIds, btn_clear});

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rbtn_alwaysOnTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_alwaysOnTopActionPerformed
        if(rbtn_alwaysOnTop.isSelected()){
            this.setAlwaysOnTop(true);
        } else {
            this.setAlwaysOnTop(false);
        }
    }//GEN-LAST:event_rbtn_alwaysOnTopActionPerformed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        txtArea_ids.setText("");
        COL_resultIDs = null;
        
    }//GEN-LAST:event_btn_clearActionPerformed

    private void btn_applyIdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_applyIdsActionPerformed
        COL_resultIDs = txtArea_ids.getText();
        
    }//GEN-LAST:event_btn_applyIdsActionPerformed

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
            java.util.logging.Logger.getLogger(IdCollector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IdCollector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IdCollector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IdCollector.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IdCollector().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_applyIds;
    private javax.swing.JButton btn_clear;
    private static javax.swing.JRadioButton btn_labID;
    private static javax.swing.JRadioButton btn_patID;
    private static javax.swing.JRadioButton btn_resultID;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton rbtn_alwaysOnTop;
    private javax.swing.JTextArea txtArea_ids;
    // End of variables declaration//GEN-END:variables
}