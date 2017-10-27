package frames;

import java.awt.event.WindowEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import myClass.DBconnect;
import myClass.Log;
import static myClass.Log.startLog;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gerda.modarres
 */
public class StartFrame extends javax.swing.JFrame {

    Log my_log;
    
    /**
     * Creates new form Start_frame
     */
    public StartFrame() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/EsALiR_suite_BG_ico2-3_small.png"));
        this.setIconImage(img.getImage());
        
        lbl_logged1.setText("You're logged in as "+DBconnect.USER+".");
        //JOptionPane.showMessageDialog(null, DBconnect.USER);
        if (DBconnect.USER.equals("root") || DBconnect.USER.equals("ccri_pat")){
            //TODO ... btn_admin.disable();
            //btn_admin.enable();
            btn_admin.setBackground(java.awt.Color.green);
            btn_admin.setFont(new java.awt.Font("Tahoma", 1, 13));
            btn_admin.setForeground(new java.awt.Color(0, 0, 0));
        }
        //setLocationRelativeTo(null);
        //String info = "StartFrame";
        //startLog(my_log, info);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                JOptionPane.showMessageDialog(null, "Bye Bye, see you soon!");
                my_log.logger.info("Closing App \n################################################################################################################################\n");
            }
        });
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbl_logged1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        LOGO = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        btn_admin = new javax.swing.JButton();
        btn_browse_result = new javax.swing.JButton();
        btn_browse_sample = new javax.swing.JButton();
        btn_query_results = new javax.swing.JButton();
        btn_browse_subtypes = new javax.swing.JButton();
        btn_sql = new javax.swing.JButton();
        btn_browse_patient = new javax.swing.JButton();
        btn_test = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Easy Analysis of Linked Results");
        setLocation(new java.awt.Point(15, 15));

        lbl_logged1.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        lbl_logged1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lbl_logged1.setText(" ");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));

        LOGO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LOGO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/NeedsAName_small.png"))); // NOI18N

        btn_admin.setFont(new java.awt.Font("Tahoma", 2, 13)); // NOI18N
        btn_admin.setForeground(new java.awt.Color(153, 153, 153));
        btn_admin.setText("Admin Tools");
        btn_admin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_adminActionPerformed(evt);
            }
        });

        btn_browse_result.setText("browse main result");
        btn_browse_result.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browse_resultActionPerformed(evt);
            }
        });

        btn_browse_sample.setText("browse sample");
        btn_browse_sample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browse_sampleActionPerformed(evt);
            }
        });

        btn_query_results.setBackground(new java.awt.Color(51, 153, 255));
        btn_query_results.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_query_results.setForeground(new java.awt.Color(0, 255, 204));
        btn_query_results.setText("result query");
        btn_query_results.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_query_resultsActionPerformed(evt);
            }
        });

        btn_browse_subtypes.setText("browse subtypes");
        btn_browse_subtypes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browse_subtypesActionPerformed(evt);
            }
        });

        btn_sql.setBackground(new java.awt.Color(204, 0, 51));
        btn_sql.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_sql.setForeground(new java.awt.Color(255, 255, 255));
        btn_sql.setText("ready for SQL?");
        btn_sql.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_sqlActionPerformed(evt);
            }
        });

        btn_browse_patient.setText("browse patient");
        btn_browse_patient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_browse_patientActionPerformed(evt);
            }
        });

        btn_test.setText("TEST");
        btn_test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_testActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_browse_sample, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_browse_result, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_browse_subtypes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_query_results, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_sql, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE)
                    .addComponent(btn_admin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_browse_patient, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_test, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1, 1, 1))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btn_query_results, javax.swing.GroupLayout.DEFAULT_SIZE, 47, Short.MAX_VALUE)
                    .addComponent(btn_sql, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(7, 7, 7)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_browse_sample)
                    .addComponent(btn_browse_patient))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_browse_subtypes)
                    .addComponent(btn_test))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btn_browse_result)
                    .addComponent(btn_admin))
                .addGap(1, 1, 1))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LOGO)
                .addGap(10, 10, 10))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LOGO, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_logged1, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_logged1)
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_browse_resultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browse_resultActionPerformed
        SearchMainResult s = new SearchMainResult();
        s.setVisible(true);
    }//GEN-LAST:event_btn_browse_resultActionPerformed

    private void btn_query_resultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_query_resultsActionPerformed
        SearchResult s = new SearchResult();
        s.setVisible(true);
    }//GEN-LAST:event_btn_query_resultsActionPerformed

    private void btn_adminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_adminActionPerformed
        //TODO
        //AdminTools s = new AdminTools();
        //s.setVisible(true);
        JOptionPane.showMessageDialog(null, "What? ... Nope!");
    }//GEN-LAST:event_btn_adminActionPerformed

    private void btn_sqlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_sqlActionPerformed
        ReadyForSQL s = new ReadyForSQL();
        s.setVisible(true);
    }//GEN-LAST:event_btn_sqlActionPerformed

    private void btn_browse_sampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browse_sampleActionPerformed
        SampleBrowse s = new SampleBrowse();
        s.setVisible(true);  
    }//GEN-LAST:event_btn_browse_sampleActionPerformed

    private void btn_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_testActionPerformed
        // TODO add your handling code here:
        //xx_patDB_main_result s = new xx_patDB_main_result();
        //s.setVisible(true); 
        JOptionPane.showMessageDialog(null, "Nothing in here yet! \nMaybe you've got an idea what we can put here?");
    }//GEN-LAST:event_btn_testActionPerformed

    private void btn_browse_subtypesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browse_subtypesActionPerformed
        SubtypesBrowse s = new SubtypesBrowse();
        s.setVisible(true);
    }//GEN-LAST:event_btn_browse_subtypesActionPerformed

    private void btn_browse_patientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_browse_patientActionPerformed
        // TODO add your handling code here:
        PatientBrowse s = new PatientBrowse();
        s.setVisible(true);
        
    }//GEN-LAST:event_btn_browse_patientActionPerformed

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
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(StartFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new StartFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LOGO;
    private javax.swing.JButton btn_admin;
    private javax.swing.JButton btn_browse_patient;
    private javax.swing.JButton btn_browse_result;
    private javax.swing.JButton btn_browse_sample;
    private javax.swing.JButton btn_browse_subtypes;
    private javax.swing.JButton btn_query_results;
    private javax.swing.JButton btn_sql;
    private javax.swing.JButton btn_test;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JLabel lbl_logged1;
    // End of variables declaration//GEN-END:variables
}