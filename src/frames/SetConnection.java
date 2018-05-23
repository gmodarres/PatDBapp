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

import static frames.LoginDB.LoginFrameRef;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static myClass.ActiveDirectory.getConnection;
import myClass.BoundsPopupMenuListener;
import myClass.DBconnect;
import myClass.Log;
import myClass.OSDetector;
import org.ini4j.Ini;
import static myClass.Log.startLog;

/**
 *
 * @author gerda.modarres
 */
public class SetConnection extends javax.swing.JFrame {

    public static javax.swing.JFrame SetConnFrameRef;
    public static boolean OK;
    public static String LDAPuser = null;
    public static File userPath = null;
    public static String personalConfig = null;
    public static String currentUser = null;
    public static String devmode = null;
    
    public static boolean PresentMode = true;       // patien name is not being displayed
    //public static boolean PresentMode = false;
    
    Log my_log;
        
    /**
     * Creates new form setConnection_frame
     */
    public SetConnection() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        BoundsPopupMenuListener listener = new BoundsPopupMenuListener(true, false);
        combo_host.addPopupMenuListener( listener );
        combo_host.setPrototypeDisplayValue("ItemWWW");
        //getIniData();
        
        identifyUser();
        read_hosts_fromFile(); 
        SetConnFrameRef = this;
    }
   
    private void identifyUser(){
        currentUser = System.getProperty("user.name");
        // JOptionPane.showMessageDialog(null, currentUser);
        txt_LDAPuser.setText(currentUser);

        //jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("settings for:   " + currentUser));
        //jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(0, 102, 153))); // NOI18N
    }
    
    public void getUser() throws IOException{
        String user = LDAPuser;
        String currentDir = System.getProperty("user.dir");
        createUserDir("\\users\\" + user);       // toggle 1/2  --> btn_testActionPerformed(java.awt.event.ActionEvent evt)
        //createUserDir("\\users\\" + "testuser");  //TEST local        
    }
    
    private static void copyFile(File sourceFile, File destFile)
            throws IOException {
        if (!sourceFile.exists()) {
            return;
        }
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }
    
    private void createUserDir(String dirName) throws IOException {         // test: without final
        try {
            if (OSDetector.isWindows()) {            
            } else {
                dirName = dirName.replace('\\','/');
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        final File currentDir = new File(System.getProperty("user.dir"));
        final File dir = new File(currentDir, dirName);
        userPath = dir;            
        //JOptionPane.showMessageDialog(null, "dirName: " + dirName);
        //JOptionPane.showMessageDialog(null, "user path: " + userPath);
        
        File config = new File ("config.ini");
        File newConfig  = new File(dir, "config.ini");
        personalConfig = newConfig.toString();
        if (dir.exists()){
            getIniData();
            //JOptionPane.showMessageDialog(null, userPath + " is already there ...");
        } else{
            dir.mkdirs();
            copyFile(config,newConfig);
        } 
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Unable to create " + dir.getAbsolutePath());
        }       
    }
    
    public boolean getLDAP(){
        String username = txt_LDAPuser.getText();
        LDAPuser = txt_LDAPuser.getText();
        String pass = jpass_pass.getText();
        String domain = txt_domain.getText();
        OK = false;
        if (jpass_pass.getText().equals("asd")) { // OVERRIDE LDAP password
            devmode = "ON";
            OK = true;
            return OK;
        } else {
            try {
                getConnection(username, pass, domain);
                //String LDAPconn = getConnection(username, pass, domain).toString();
                //txtArea_testLDAP.setText(LDAPconn);
                OK = true;
                if (jpass_pass.getText().isEmpty() || jpass_pass.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "enter a password!");
                    OK = false;
                }
            } catch (NamingException ex) {
                JOptionPane.showMessageDialog(null, "what?");
                OK = false;
                Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            return OK;
        }
    }
    
    public void comboselect() {
        //int d = combo_host.getSelectedIndex();
        String c_s = combo_host.getSelectedItem().toString();
        txt_passHost.setText(c_s);
    }
    
    private void read_hosts_fromFile(){
        try{
            final List<String> lines = Files.readAllLines(Paths.get("hosts.ini"),
            Charset.defaultCharset());
                       
            EventQueue.invokeLater(new Runnable(){
                @Override
                public void run(){          
                  combo_host.setModel(new DefaultComboBoxModel<String>(
                            lines.toArray(new String[0])));                    
                }
            });  
        } catch (IOException e) {
            //e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    public static void get_connData(){
        DBconnect.USER = LoginDB.txt_username.getText();
        DBconnect.PWD = LoginDB.txt_password.getText();
        DBconnect.CONN_STR = txt_passHost.getText();
    }
   
   public static void close_setConn(){
        SetConnFrameRef.dispose();
    }
   
   public static void getIniData(){
        Ini ini;
        try {            
            // toggle for Testing
            //ini = new Ini(new File("config.ini"));        //TEST
            ini = new Ini(new File(personalConfig));        //toggle
            //JOptionPane.showMessageDialog(null, personalConfig);
            
            String user = ini.get("user","user");
            txt_passUser.setText(user);
            String hosts = ini.get("hosts","host");
            txt_passHost.setText(hosts);    
            String dp = ini.get("defaultpath","path");
            txt_defaultPathSave.setText(dp); 
                    
        } catch (IOException ex) {
            Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
   }
   
   public static void saveIniData(){
        Ini ini;
        try {
            // toggle for Testing
            //ini = new Ini(new File("config.ini"));    //TEST
            ini = new Ini(new File(personalConfig));    //toggle
            
            String newHost = txt_passHost.getText();
            String newUser = txt_passUser.getText();
            String newPath = txt_defaultPathSave.getText();
            
            ini.put("hosts", "host", newHost);
            ini.put("user", "user", newUser);
            ini.put("defaultpath", "path", newPath);
            ini.store();            
        } catch (IOException ex) {
            Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
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

        jPanel1 = new javax.swing.JPanel();
        lbl_user = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        lbl_pass = new javax.swing.JLabel();
        btn_LDAPlogin = new javax.swing.JButton();
        lbl_domain = new javax.swing.JLabel();
        txt_LDAPuser = new javax.swing.JTextField();
        txt_domain = new javax.swing.JTextField();
        jpass_pass = new javax.swing.JPasswordField();
        jPanel2 = new javax.swing.JPanel();
        btn_fileChoose = new javax.swing.JButton();
        lbl_conn2 = new javax.swing.JLabel();
        txt_passUser = new javax.swing.JTextField();
        txt_defaultPathSave = new javax.swing.JTextField();
        btn_update = new javax.swing.JButton();
        lbl_dp = new javax.swing.JLabel();
        txt_passHost = new javax.swing.JTextField();
        combo_host = new javax.swing.JComboBox<>();
        lbl_conn1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Linked Results Analysis Tool");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_user.setText("user:");

        jLabel1.setText("LDAP");

        lbl_pass.setText("password:");

        btn_LDAPlogin.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_LDAPlogin.setForeground(new java.awt.Color(0, 102, 153));
        btn_LDAPlogin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Cute-Ball-Standby-icon.png"))); // NOI18N
        btn_LDAPlogin.setText("enable Login");
        btn_LDAPlogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_LDAPloginActionPerformed(evt);
            }
        });

        lbl_domain.setText("domain:");

        txt_domain.setText("stanna.at");

        jpass_pass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                jpass_passKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_pass, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_user, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_domain, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txt_domain)
                    .addComponent(jpass_pass)
                    .addComponent(txt_LDAPuser)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 67, Short.MAX_VALUE)
                        .addComponent(btn_LDAPlogin)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel1)
                    .addComponent(btn_LDAPlogin))
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_LDAPuser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_user))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jpass_pass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_pass))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txt_domain, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_domain))
                .addContainerGap())
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder(""), "settings", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14), new java.awt.Color(0, 102, 153))); // NOI18N

        btn_fileChoose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/open-file-icon.png"))); // NOI18N
        btn_fileChoose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_fileChooseActionPerformed(evt);
            }
        });

        lbl_conn2.setText("Connection:");

        btn_update.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_update.setForeground(new java.awt.Color(0, 102, 153));
        btn_update.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/table-edit-icon.png"))); // NOI18N
        btn_update.setText("update");
        btn_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_updateActionPerformed(evt);
            }
        });

        lbl_dp.setText("default save-path:");

        combo_host.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "model jdbc:mysql://localhost:3306/pat_db", "jdbc:mysql://synology-ha.stanna.at:3306/ccri_pat_db" }));
        combo_host.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent evt) {
            }
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {
                combo_hostPopupMenuWillBecomeInvisible(evt);
            }
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent evt) {
            }
        });

        lbl_conn1.setText("User:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(lbl_conn1)
                        .addComponent(lbl_conn2)
                        .addComponent(combo_host, 0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                            .addComponent(lbl_dp)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 121, Short.MAX_VALUE)
                            .addComponent(btn_fileChoose)))
                    .addComponent(txt_passHost)
                    .addComponent(txt_passUser)
                    .addComponent(txt_defaultPathSave)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(btn_update, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(combo_host, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(lbl_conn2)
                .addGap(0, 0, 0)
                .addComponent(txt_passHost, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(lbl_conn1)
                .addGap(0, 0, 0)
                .addComponent(txt_passUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbl_dp)
                    .addComponent(btn_fileChoose))
                .addGap(0, 0, 0)
                .addComponent(txt_defaultPathSave, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addComponent(btn_update)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(25, 25, 25))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(26, Short.MAX_VALUE))
        );

        jPanel1.getAccessibleContext().setAccessibleName("LDAP");

        getAccessibleContext().setAccessibleName("Conn_frame");

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btn_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_updateActionPerformed
        get_connData();
        saveIniData();
        LoginDB.close_Login();
        new LoginDB().setVisible(true);
        boolean OK = getLDAP(); // toggle 2/2     --> getUser() // THIS IS A COPY ... theres another 2/2!
        //boolean OK = true;   //test
        
        if (OK == true) {
            LoginFrameRef.enable();
        } else {
            LoginFrameRef.disable();
        }
        
    }//GEN-LAST:event_btn_updateActionPerformed

    private void combo_hostPopupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent evt) {//GEN-FIRST:event_combo_hostPopupMenuWillBecomeInvisible
         comboselect();
    }//GEN-LAST:event_combo_hostPopupMenuWillBecomeInvisible

    private void btn_fileChooseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_fileChooseActionPerformed
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setCurrentDirectory(new java.io.File("."));
    fileChooser.setDialogTitle("save path for output files");
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    fileChooser.setAcceptAllFileFilterUsed(false);
    if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
        String dp = fileChooser.getSelectedFile().toString();
        txt_defaultPathSave.setText(dp);
    }               
    }//GEN-LAST:event_btn_fileChooseActionPerformed

    private void btn_LDAPloginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_LDAPloginActionPerformed
        boolean OK = getLDAP(); // toggle 2/2     --> getUser()
        //boolean OK = true;   //test
        
        if (OK == true) {
            try {
                getUser();
                LoginDB.close_Login();
                new LoginDB().setVisible(true);
            } catch (IOException ex) {
                Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
            LoginFrameRef.enable();

        } else {
            LoginFrameRef.disable();
            //JOptionPane.showMessageDialog(null, "nope ...");
            //this.dispose();
            //LoginFrameRef.dispose();
        }
        startLog(my_log,"open Logfile for user: "+currentUser);
    }//GEN-LAST:event_btn_LDAPloginActionPerformed

    private void jpass_passKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jpass_passKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            boolean OK = getLDAP();
            if (OK == true) {
                try {
                    getUser();
                    LoginDB.close_Login();
                    new LoginDB().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
                }
                LoginFrameRef.enable();

            } else {
                LoginFrameRef.disable();
            }
            startLog(my_log,"open Logfile for user: "+currentUser);
        }
    }//GEN-LAST:event_jpass_passKeyPressed

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
            java.util.logging.Logger.getLogger(SetConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SetConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SetConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SetConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new SetConnection().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btn_LDAPlogin;
    private javax.swing.JButton btn_fileChoose;
    private javax.swing.JButton btn_update;
    private javax.swing.JComboBox<String> combo_host;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPasswordField jpass_pass;
    private javax.swing.JLabel lbl_conn1;
    private javax.swing.JLabel lbl_conn2;
    private javax.swing.JLabel lbl_domain;
    private javax.swing.JLabel lbl_dp;
    private javax.swing.JLabel lbl_pass;
    private javax.swing.JLabel lbl_user;
    private javax.swing.JTextField txt_LDAPuser;
    public static javax.swing.JTextField txt_defaultPathSave;
    private javax.swing.JTextField txt_domain;
    public static javax.swing.JTextField txt_passHost;
    public static javax.swing.JTextField txt_passUser;
    // End of variables declaration//GEN-END:variables
}
