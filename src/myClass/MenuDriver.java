/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClass;

import frames.IdCollector;
import frames.ShowSQL;
import java.awt.Desktop;
import static java.awt.image.ImageObserver.HEIGHT;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 *
 * @author gerda.modarres
 */
public class MenuDriver {
    private JMenuBar menuBarGlobal; // added
    
    public MenuDriver(){
        menuBarGlobal = new javax.swing.JMenuBar();   
    }
       
    public JMenuBar getMenuBar(){
        
        JMenu jMenu1 = new javax.swing.JMenu(); // File
        JMenu jMenu2 = new javax.swing.JMenu(); // Edit
        JMenu jMenu3 = new javax.swing.JMenu(); // Info
        JMenu jMenu4 = new javax.swing.JMenu(); // LIRAnalysis Logo
        
        //File
        JMenuItem jMenuItem1_openModel = new javax.swing.JMenuItem("open DB Model",new javax.swing.ImageIcon(getClass().getResource("/ico/open-file-icon.png")));   
        JMenuItem jMenuItem2_showSql = new javax.swing.JMenuItem();
        JMenuItem jMenuItem3_idCollector = new javax.swing.JMenuItem();
        //Edit
        
        //Info
        JMenuItem jMenuItem1_HowTo = new javax.swing.JMenuItem();
        JMenuItem jMenuItem2_Info = new javax.swing.JMenuItem();

        
        jMenu4.setBorder(null);
        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_Font_small07_web.png"))); // NOI18N
        jMenu4.setMargin(new java.awt.Insets(0, 0, 0, 5));
        menuBarGlobal.add(jMenu4);

        jMenu1.setText("File");
        
        jMenuItem1_openModel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/open-file-icon.png"))); // NOI18N
        jMenuItem1_openModel.setText("open DB Model");            
        jMenuItem1_openModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //jMenuItem1_openModelActionPerformed(evt);
                
                File file = new File("model.pdf");
                //File file = new File("C:\\Users\\gerda.modarres\\Desktop\\pat_DB\\stdpat_db_model.pdf");
                try {
                    if (OSDetector.isWindows()) {
                        //JOptionPane.showMessageDialog(null, OSDetector.isWindows());
                        Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file);
                        //Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + "model.pdf");
                        //Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + "C:\\Users\\gerda.modarres\\Desktop\\pat_DB\\stdpat_db_model.pdf");
                    } else {
                        Desktop desktop = Desktop.getDesktop();
                        desktop.open(file);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e);
                }               
            }
        });      
        jMenu1.add(jMenuItem1_openModel);

        jMenuItem2_showSql.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/if_icon-89-document-file-sql_315887.png"))); // NOI18N
        jMenuItem2_showSql.setText("showSQL");
        jMenuItem2_showSql.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //jMenuItem2_showSqlActionPerformed(evt);
                ShowSQL s = new ShowSQL();
                s.setVisible(true);
                ShowSqlSelector.ShowSqlIsOpen = true;
            }
        });      
        jMenu1.add(jMenuItem2_showSql);

        jMenuItem3_idCollector.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/collect_IDs.png"))); // NOI18N
        jMenuItem3_idCollector.setText("open IDcollector");
        jMenuItem3_idCollector.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //jMenueItem3_idCollectorActionPerformed(evt);
                IdCollector s = new IdCollector();
                s.setVisible(true);                    
            }
        });
        jMenu1.add(jMenuItem3_idCollector);

        menuBarGlobal.add(jMenu1);

        jMenu2.setText("Edit");
        menuBarGlobal.add(jMenu2);

        jMenu3.setText("Help");
        
        jMenuItem1_HowTo.setText("how to use");      
        jMenuItem1_HowTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //jMenuItem1_HowToActionPerformed(evt);
                ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/Monsters-Snail-icon.png"));
                JOptionPane.showMessageDialog(menuBarGlobal, "... ummmmmm \n... errrrrr \n... pls ask again later", "apparently no useful Info", HEIGHT,img);
            }
        });
        jMenu3.add(jMenuItem1_HowTo);

        jMenuItem2_Info.setText("Info");
        jMenuItem2_Info.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                //jMenuItem2_InfoActionPerformed(evt);
                ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_med.png"));
                JOptionPane.showMessageDialog(menuBarGlobal, "LInkedResultsAnalysis \nDB-request Tool\nVersion:   1.0.0", "Info", HEIGHT,img);
            }
        });
        jMenu3.add(jMenuItem2_Info);
        
        menuBarGlobal.add(jMenu3);

        //setJMenuBar(menuBar);
        /*javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 885, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        */
        //pack();

        return menuBarGlobal;
    }
    
    
}


/*
Now in you main class you can reference the menu bar by using:
MenuDriver menu = new MenuDriver();
frame.setJMenuBar( menu.getMenuBar() );

*/