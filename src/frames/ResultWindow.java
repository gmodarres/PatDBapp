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

import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Observer;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import myClass.DBconnect;

/**
 *
 * @author gerda.modarres
 */
public class ResultWindow extends javax.swing.JFrame {
    
    //static String getMovingResultID = null;
    static String sourceOpener = null;
    public static javax.swing.JFrame ResultWindowRef;

    /**
     * Creates new form freeTable
     */
    public ResultWindow() {
        initComponents();
        getIntrpr();
        ResultWindowRef = this;
        
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        
        
/*        this.addWindowFocusListener(new WindowFocusListener() {
            @Override
            public void windowGainedFocus(WindowEvent windowevent) {
                //window = "getWIndow";  //TEST
                windowevent.getWindow().getAccessibleContext().getAccessibleName();
                windowevent.getWindow().requestFocus();
                windowevent.getWindow().getAccessibleContext().getAccessibleComponent().equals(txtArea_result);
                //JOptionPane.showMessageDialog(null, "win: "+ window);

            }

            @Override
            public void windowLostFocus(WindowEvent e) {
                //e.getWindow().toFront();
                //e.getWindow().requestFocus();
                e.getWindow().getAccessibleContext().getAccessibleName();
                //JOptionPane.showMessageDialog(null, "new: "+ e.getWindow().getAccessibleContext().getAccessibleName());
                
            }
        });*/
         
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                SearchResult.IntrprWindowIsOpen = false;
                //JOptionPane.showMessageDialog(null, "closing, boolean is " + IntrprWindowIsOpen);
            }         
        }); 
         
    }
    
    public void getIntrpr(){
        this.setTitle(SearchResult.source);
        sourceOpener = SearchResult.source;
        //this.setName(sourceOpener);
        
        String resultMoved = SearchResult.resultMoving;
        txtArea_result.setText(resultMoved);
    }

    public static void updateIntrpr(String resultID){
        
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try{
            String sql = "select * from main_result where result_id="+ resultID;
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            
            String arr="";
            String fish="";
            String zg ="";
            
            if (rs.next()) {
                arr = rs.getString("ar_intrpr");
                fish = rs.getString("fish_intrpr");
                zg = rs.getString("zg_intrpr");
            }

            String text = "";
            switch (sourceOpener) {
                case "array result":
                    text = arr;
                    break;
                case "fish result":
                    text = fish;
                    break;
                case "cytogenetics result":
                    text = zg;
                    break;
                default:
                    break;
            }            
            txtArea_result.setText(text);
            
        }catch (Exception e) {
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
    
    public static void close_RW(){
        ResultWindowRef.setVisible(false);
    } 
  
    /*Window getSelectedWindow(Window[] windows) {
    Window result = null;
    for (int i = 0; i < windows.length; i++) {
        Window window = windows[i];
        if (window.isActive()) {
            result = window;
        } else {
            Window[] ownedWindows = window.getOwnedWindows();
            if (ownedWindows != null) {
                result = getSelectedWindow(ownedWindows);
            }
        }
    }
    return result;
    } */
      
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        txtArea_result = new javax.swing.JTextArea();
        rbtn_alwaysOnTop = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Interpretation");

        txtArea_result.setColumns(20);
        txtArea_result.setLineWrap(true);
        txtArea_result.setRows(5);
        txtArea_result.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txtArea_result);

        rbtn_alwaysOnTop.setText("always on top");
        rbtn_alwaysOnTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_alwaysOnTopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 874, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(rbtn_alwaysOnTop)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addComponent(rbtn_alwaysOnTop)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 265, Short.MAX_VALUE)
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
            java.util.logging.Logger.getLogger(ResultWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ResultWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ResultWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ResultWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new ResultWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JRadioButton rbtn_alwaysOnTop;
    public static javax.swing.JTextArea txtArea_result;
    // End of variables declaration//GEN-END:variables
}
