package frames;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author gerda.modarres
 */

//import static frames.SetConnection.currentUser;
import myClass.DBconnect;
import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.awt.event.*;
import myClass.Log;
import static myClass.Log.startLog;
//import java.util.logging.Level;
//import java.util.logging.Logger;
//import javax.naming.NamingException;
//import javax.naming.ldap.LdapContext;
//import static myClass.ActiveDirectory.*;

//import java.awt.*;

public class LoginDB extends javax.swing.JFrame {

    Connection conn = null;
    //ResultSet rs = null;
    //PreparedStatement pst = null;
    public static javax.swing.JFrame LoginFrameRef;


    /**
     * Creates new form Login_DB_frame
     */
    
    public LoginDB() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/EsALiR_suite_BG_ico2-3_small.png"));
        this.setIconImage(img.getImage());
        getUser();
        //conn = DBconnect.ConnecrDb();
        setLocationRelativeTo(null);  // center window on screen
        LoginFrameRef = this;
        this.disable();   // TOGGLE for Testing - enable in app for LDAP
    }
    //Connection conn = null;
    ResultSet rs = null;
    PreparedStatement pst = null;
    
    public void close(){
        WindowEvent winClosingEvent = new WindowEvent(this,WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);  
    }
   
    public static void close_Login(){
        LoginFrameRef.setVisible(false);
    } 
    
    public static void getUser(){
        String tmp = SetConnection.txt_passUser.getText();
        txt_username.setText(tmp);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txt_username = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txt_password = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        cmd_login = new javax.swing.JButton();
        LOGO = new javax.swing.JLabel();
        btn_Test = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Easy Analysis of Linked Results");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Login", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 18), new java.awt.Color(0, 102, 153))); // NOI18N

        jLabel2.setText("Password");

        txt_password.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txt_passwordKeyPressed(evt);
            }
        });

        jLabel1.setText("Username");

        cmd_login.setText("Login");
        cmd_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmd_loginActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(txt_username)
                    .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmd_login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(119, 119, 119))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txt_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txt_password, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(cmd_login)
                .addGap(185, 185, 185))
        );

        LOGO.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LOGO.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/NeedsAName_small.png"))); // NOI18N

        btn_Test.setText("TEST");
        btn_Test.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TestActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(55, 16, 168));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("ccri_pat_db");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(55, 16, 168));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Database Query");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btn_Test)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(LOGO, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(35, 35, 35))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LOGO)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_Test, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cmd_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmd_loginActionPerformed

        try {
            SetConnection.get_connData();    
            //JOptionPane.showMessageDialog(null, "LOGIN_DB_frame :"+ DBconnect.USER + " _ "+ DBconnect.PWD );
                       
            conn = DBconnect.ConnecrDb();
            //JOptionPane.showMessageDialog(null,"CONN: " + conn);
            if (conn != null){
                dispose();
                SetConnection.close_setConn();
                new StartFrame().setVisible(true);
                // ... version2:
                //close();
                //Start_frame f = new StartFrame();
                //f.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(null, "Username and Password are not correct!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } 
    }//GEN-LAST:event_cmd_loginActionPerformed

    private void txt_passwordKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txt_passwordKeyPressed

        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {

            try {
                SetConnection.get_connData();
                //JOptionPane.showMessageDialog(null, "LOGIN_DB_frame :"+ DBconnect.USER + " _ "+ DBconnect.PWD );

                conn = DBconnect.ConnecrDb();
                //JOptionPane.showMessageDialog(null, "CONN: " + conn);
                if (conn != null) {
                    dispose();
                    SetConnection.close_setConn();
                    new StartFrame().setVisible(true);

                } else {
                    JOptionPane.showMessageDialog(null, "Username and Password are not correct!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }
    }//GEN-LAST:event_txt_passwordKeyPressed

    private void btn_TestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TestActionPerformed
        StartFrame f = new StartFrame();
        f.setVisible(true);       
    }//GEN-LAST:event_btn_TestActionPerformed

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
            java.util.logging.Logger.getLogger(LoginDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginDB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SetConnection().setVisible(true);
                new LoginDB().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LOGO;
    private javax.swing.JButton btn_Test;
    private javax.swing.JButton cmd_login;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    public static javax.swing.JPasswordField txt_password;
    public static javax.swing.JTextField txt_username;
    // End of variables declaration//GEN-END:variables
}
