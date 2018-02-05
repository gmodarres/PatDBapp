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

import static frames.SetConnection.personalConfig;
import myClass.DBconnect;
import myClass.ColumnFitAdapter;
import myClass.OSDetector;
import java.sql.*;
import javax.swing.*;
import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;
import myClass.CustomSorter;
import myClass.IdManagement;
import myClass.Log;
import myClass.saveTable;
import net.proteanit.sql.DbUtils;
import org.ini4j.Ini;

public class ReadyForSQL extends javax.swing.JFrame {

    String ids = null;
        
    JTable outTable = null;  
    String defaultPath = null;
    
    Log my_log;
    
    /**
     * Creates new form patDB_search_result
     */
    public ReadyForSQL() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        getIniData();
        initial_table_queryIDs();
        initial_table_statistics();
        table_SQLresult.getTableHeader().addMouseListener(new ColumnFitAdapter());      
        Info_top.getRootPane().setDefaultButton(btn_SQLquery);

        my_log.logger.info("open ReadyForSQL()");
    }
    
    private void update_table_SQLresult(){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        
        String sql = txtArea_SQL.getText();
        try{
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            //if (rs.next()) {      // this skips first row!   
            table_SQLresult.setModel(DbUtils.resultSetToTableModel(rs));
            // TODO:    resize column width
            //table_SQLresult.getColumnModel().getColumn(columnNumber).setPreferredWidth(columnWidth);

            get_statistics(sql);
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                my_log.logger.info(getRows+" row(s) returned");
            }

        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());    //#2
            my_log.logger.warning("ERROR:  " + e.getMessage()); 

        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }
    
    private void initial_table_queryIDs(){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT distinct s.lab_id, result_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id";
        
        try{         
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            
            while(rs.next()){
                String id = rs.getString("result_id");
                all_ids = all_ids + "'" + id + "',";
                //txtArea_test.append("'"+id+"',");  // test
            }
            if (all_ids.length() > 1) {
                all_ids = all_ids.substring(0, (all_ids.length() - 1));
                String sql2 = "SELECT distinct s.lab_id, result_id, p.pat_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND result_id IN ( " + all_ids + " )";
                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();

                table_queryIDs.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_queryIDs);
                if (table_queryIDs.getColumnModel().getColumnCount() > 0) {
                    table_queryIDs.getColumnModel().getColumn(0).setPreferredWidth(85);      // 75
                    table_queryIDs.getColumnModel().getColumn(0).setMaxWidth(120);           // 80
                    table_queryIDs.getColumnModel().getColumn(1).setPreferredWidth(60);
                    table_queryIDs.getColumnModel().getColumn(1).setMaxWidth(60);
                    table_queryIDs.getColumnModel().getColumn(2).setPreferredWidth(60);
                    table_queryIDs.getColumnModel().getColumn(2).setMaxWidth(60);
                    table_queryIDs.getColumnModel().getColumn(3).setPreferredWidth(80);
                    table_queryIDs.getColumnModel().getColumn(3).setMaxWidth(130);
                    //table_queryIDs.getColumnModel().getColumn(4).setPreferredWidth(80);
                    //table_queryIDs.getColumnModel().getColumn(4).setMaxWidth(130);
                    table_queryIDs.getColumnModel().getColumn(5).setPreferredWidth(30);
                    table_queryIDs.getColumnModel().getColumn(5).setMaxWidth(30);
                    table_queryIDs.getColumnModel().getColumn(6).setPreferredWidth(90);      // 80
                    table_queryIDs.getColumnModel().getColumn(6).setMaxWidth(120);           // 80
                    //table_queryIDs.getColumnModel().getColumn(7).setPreferredWidth(30);
                    //table_queryIDs.getColumnModel().getColumn(7).setMaxWidth(30);
                }
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }
    
    private void get_queryLabIDs(String sql, PreparedStatement pst, ResultSet rs, Connection conn){        
        try{
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            boolean IdsPres = false;
            String RsGet = null;
                    
            while(rs.next()){
                try {
                    RsGet = rs.getString("result_id");
                } catch (Exception e) {
                    //OptionPane.showMessageDialog(null, e);
                }
                if (RsGet != null){
                    IdsPres = true;
                    String id = rs.getString("result_id");
                    all_ids = all_ids + "'" + id + "',";
                    //txtArea_test.append("'"+id+"',");  // test  
                } else {
                    IdsPres = false;
                }
            }

            if (IdsPres == true) {

                if (all_ids.length() > 1) {

                    all_ids = all_ids.substring(0, (all_ids.length() - 1));
                    String sql2 = "SELECT distinct s.lab_id, result_id, p.pat_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
                            + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND result_id IN ( " + all_ids + " )";

                    pst = conn.prepareStatement(sql2);
                    rs = pst.executeQuery();

                    table_queryIDs.setModel(DbUtils.resultSetToTableModel(rs));
                    CustomSorter.table_customRowSort(table_queryIDs);
                    if (table_queryIDs.getColumnModel().getColumnCount() > 0) {
                      table_queryIDs.getColumnModel().getColumn(0).setPreferredWidth(85);      // 75
                    table_queryIDs.getColumnModel().getColumn(0).setMaxWidth(120);           // 80
                    table_queryIDs.getColumnModel().getColumn(1).setPreferredWidth(60);
                    table_queryIDs.getColumnModel().getColumn(1).setMaxWidth(60);
                    table_queryIDs.getColumnModel().getColumn(2).setPreferredWidth(60);
                    table_queryIDs.getColumnModel().getColumn(2).setMaxWidth(60);
                    table_queryIDs.getColumnModel().getColumn(3).setPreferredWidth(80);
                    table_queryIDs.getColumnModel().getColumn(3).setMaxWidth(130);
                    //table_queryIDs.getColumnModel().getColumn(4).setPreferredWidth(80);
                    //table_queryIDs.getColumnModel().getColumn(4).setMaxWidth(130);
                    table_queryIDs.getColumnModel().getColumn(5).setPreferredWidth(30);
                    table_queryIDs.getColumnModel().getColumn(5).setMaxWidth(30);
                    table_queryIDs.getColumnModel().getColumn(6).setPreferredWidth(90);      // 80
                    table_queryIDs.getColumnModel().getColumn(6).setMaxWidth(120);           // 80
                    //table_queryIDs.getColumnModel().getColumn(7).setPreferredWidth(30);
                    //table_queryIDs.getColumnModel().getColumn(7).setMaxWidth(30);
                    }
                }

                //get ids of sql query to count patients affected in get_statistics()
                //get_ids(sql, pst, rs, conn);
                this.ids = IdManagement.get_ids(sql, pst, rs, conn, "result_id");

            }else if (IdsPres == false){
                this.ids = null;
            }

        }catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e.getMessage());        //#1
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }
         
    private void initial_table_statistics(){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String sql = "SELECT  ( SELECT COUNT(*) FROM  patient ) AS patients, " +
            "        ( SELECT COUNT(*) FROM arr_result ) AS array, " +
            "        ( SELECT COUNT(*) FROM fish_result ) AS fish, " +
            "        ( SELECT COUNT(*) FROM zg_result ) AS ZG, "+
            "        (0) AS query, "+
            "        (0) AS \"result IDs\", "+
            "        (0) AS \"pat affected\"";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_statistics.setModel(DbUtils.resultSetToTableModel(rs));
            jScrollPane7.setViewportView(table_statistics);
            if (table_statistics.getColumnModel().getColumnCount() > 0) {
                table_statistics.getColumnModel().getColumn(0).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(0).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(1).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(1).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(2).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(2).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(3).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(3).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(4).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(4).setMaxWidth(100);
                table_statistics.getColumnModel().getColumn(5).setPreferredWidth(70);
                table_statistics.getColumnModel().getColumn(5).setMaxWidth(100);
            }
            
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
    
    private void get_statistics(String sql) {
         if (this.ids != null) {

            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            try {
                // from get_ids()  ... 1,2,3,4,5,  --> get rid of "," at the end:
                String ids = this.ids;
                
                if (ids.length() > 1) {
                    ids = ids.substring(0, (ids.length() - 1));
                } else {
                    //JOptionPane.showMessageDialog(null, "no IDs");
                    //ids = "0";                                    // not needed due to        if (this.ids != null)
                }
                String sql2 = "SELECT  ( SELECT COUNT(*) FROM  patient ) AS patients, \n" +
                "( SELECT COUNT(*) FROM  arr_result ) AS array, \n" +
                "( SELECT COUNT(*) FROM fish_result ) AS fish, \n" +
                "( SELECT COUNT(*) FROM zg_result) AS ZG, \n" +
                "( SELECT COUNT(*) FROM ( " + sql + " ) AS m) AS query, \n" +
                "( SELECT COUNT(*) FROM ( SELECT p.pat_id FROM patient p, sample s, main_result m WHERE p.pat_id=s.pat_id AND s.lab_id=m.lab_id AND result_id in ( "+ ids + " ) ) AS c ) AS \"result IDs\", \n" +
                "( SELECT COUNT(*) FROM ( SELECT distinct p.pat_id FROM patient p, sample s, main_result m WHERE p.pat_id=s.pat_id AND s.lab_id=m.lab_id AND result_id in ( "+ ids + " ) ) AS c ) AS \"pat affected\"";

                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();
                table_statistics.setModel(DbUtils.resultSetToTableModel(rs));
                jScrollPane7.setViewportView(table_statistics);
                if (table_statistics.getColumnModel().getColumnCount() > 0) {
                    table_statistics.getColumnModel().getColumn(0).setPreferredWidth(70);
                    table_statistics.getColumnModel().getColumn(0).setMaxWidth(100);
                    table_statistics.getColumnModel().getColumn(1).setPreferredWidth(70);
                    table_statistics.getColumnModel().getColumn(1).setMaxWidth(100);
                    table_statistics.getColumnModel().getColumn(2).setPreferredWidth(70);
                    table_statistics.getColumnModel().getColumn(2).setMaxWidth(100);
                    table_statistics.getColumnModel().getColumn(3).setPreferredWidth(70);
                    table_statistics.getColumnModel().getColumn(3).setMaxWidth(100);
                    table_statistics.getColumnModel().getColumn(4).setPreferredWidth(70);
                    table_statistics.getColumnModel().getColumn(4).setMaxWidth(100);
                    table_statistics.getColumnModel().getColumn(5).setPreferredWidth(70);
                    table_statistics.getColumnModel().getColumn(5).setMaxWidth(100);
                }
                //txtArea_test.setText(ids);           

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Statistics can't be calculated ... " + e.getMessage());
                my_log.logger.warning("Statistics can't be calculated ... " + e.getMessage());
            } finally {
                try {
                     if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }
         } else {
            JOptionPane.showMessageDialog(null, "Statistics were not calculated ... no result_id's in query.");
            my_log.logger.info("Statistics were not calculated ... no result_id's in query.");
            initial_table_statistics();
         }
    }
    
    private static boolean isRightClick(MouseEvent e) {
    return (e.getButton()==MouseEvent.BUTTON3 ||
            (System.getProperty("os.name").contains("Mac OS X") &&
                    (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 &&
                    (e.getModifiers() & InputEvent.CTRL_MASK) != 0));
    }
      
    public void getIniData(){
        Ini ini;
        try {
            //ini = new Ini(new File("config.ini"));    // TEST
            ini = new Ini(new File(personalConfig));    // toggle 1/1    
            //ini = new Ini(new File("C:\\Users\\gerda.modarres\\Desktop\\pat_DB\\config.ini"));
            String dp = ini.get("defaultpath","path");
            this.defaultPath = dp;
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        popUpSave = new javax.swing.JPopupMenu();
        popUpMenu_save = new javax.swing.JMenuItem();
        popUpMenu_selectAll = new javax.swing.JMenuItem();
        Info_top = new javax.swing.JPanel();
        btn_SQLquery = new javax.swing.JButton();
        btn_clear = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtArea_SQL = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        table_statistics = new javax.swing.JTable();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_queryIDs = new javax.swing.JTable();
        jToolBar1 = new javax.swing.JToolBar();
        bnt_test = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        table_SQLresult = new javax.swing.JTable();
        lbl_rowsReturned = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1_openModel = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1_HowTo = new javax.swing.JMenuItem();
        jMenuItem2_tableStructure = new javax.swing.JMenuItem();
        jMenuItem1_info = new javax.swing.JMenuItem();

        popUpMenu_save.setText("save ...");
        popUpMenu_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popUpMenu_saveActionPerformed(evt);
            }
        });
        popUpSave.add(popUpMenu_save);

        popUpMenu_selectAll.setText("select all ...");
        popUpMenu_selectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popUpMenu_selectAllActionPerformed(evt);
            }
        });
        popUpSave.add(popUpMenu_selectAll);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Linked Results Analysis Tool - free SQL");
        setLocation(new java.awt.Point(100, 50));

        Info_top.setBackground(new java.awt.Color(102, 153, 255));
        Info_top.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btn_SQLquery.setBackground(java.awt.Color.darkGray);
        btn_SQLquery.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_SQLquery.setForeground(java.awt.Color.white);
        btn_SQLquery.setText("SQL query");
        btn_SQLquery.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SQLqueryActionPerformed(evt);
            }
        });

        btn_clear.setText("Clear");
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });

        txtArea_SQL.setColumns(20);
        txtArea_SQL.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        txtArea_SQL.setRows(5);
        txtArea_SQL.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtArea_SQLKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(txtArea_SQL);

        table_statistics.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane7.setViewportView(table_statistics);

        table_queryIDs.setAutoCreateRowSorter(true);
        table_queryIDs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "lab_id", "result_ID", "name", "surname", "sex", "b_date"
            }
        ));
        jScrollPane10.setViewportView(table_queryIDs);
        table_queryIDs.getAccessibleContext().setAccessibleName("table_queryIDs");
        table_queryIDs.getAccessibleContext().setAccessibleDescription("");

        javax.swing.GroupLayout Info_topLayout = new javax.swing.GroupLayout(Info_top);
        Info_top.setLayout(Info_topLayout);
        Info_topLayout.setHorizontalGroup(
            Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_topLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                    .addComponent(jScrollPane10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(Info_topLayout.createSequentialGroup()
                        .addComponent(btn_SQLquery)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 938, Short.MAX_VALUE)))
                .addContainerGap())
        );
        Info_topLayout.setVerticalGroup(
            Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_topLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(Info_topLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 28, Short.MAX_VALUE)
                        .addComponent(btn_clear, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Info_topLayout.createSequentialGroup()
                        .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_SQLquery, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                .addGap(10, 10, 10))
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

        jScrollPane3.setAutoscrolls(true);

        table_SQLresult.setModel(new javax.swing.table.DefaultTableModel(
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
        table_SQLresult.setMaximumSize(new java.awt.Dimension(2147483647, 100));
        table_SQLresult.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_SQLresultMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(table_SQLresult);

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

        jMenuItem1_HowTo.setText("how to");
        jMenuItem1_HowTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1_HowToActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1_HowTo);

        jMenuItem2_tableStructure.setText("table structure");
        jMenuItem2_tableStructure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2_tableStructureActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2_tableStructure);

        jMenuItem1_info.setText("Info");
        jMenuItem1_info.setToolTipText("");
        jMenuItem1_info.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1_infoActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1_info);

        jMenuBar1.add(jMenu3);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addComponent(Info_top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(21, 21, 21))
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lbl_rowsReturned, javax.swing.GroupLayout.PREFERRED_SIZE, 327, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 426, Short.MAX_VALUE)
                .addGap(5, 5, 5)
                .addComponent(lbl_rowsReturned)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void btn_SQLqueryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SQLqueryActionPerformed
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String sql = txtArea_SQL.getText();

            get_queryLabIDs(sql, pst, rs, conn);
            update_table_SQLresult();
            my_log.logger.info("SQL:  " + sql);

        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, e.getMessage());
        } finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }

    }//GEN-LAST:event_btn_SQLqueryActionPerformed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        initial_table_queryIDs();
        initial_table_statistics();
        txtArea_SQL.setText("");
    }//GEN-LAST:event_btn_clearActionPerformed

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

    private void jMenuItem2_tableStructureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2_tableStructureActionPerformed
        TableStructure s = new TableStructure();
        
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            //String sql = "select * from information_schema.columns where table_schema = DATABASE() order by table_name,ordinal_position;";	
            String sql = "select TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME, ORDINAL_POSITION, IS_NULLABLE, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, "+
                "CHARACTER_OCTET_LENGTH, NUMERIC_PRECISION, NUMERIC_SCALE, COLUMN_TYPE, COLUMN_KEY, EXTRA " +
                "from information_schema.columns where table_schema = DATABASE() order by table_name,ordinal_position;";

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
        s.setVisible(true);         
    }//GEN-LAST:event_jMenuItem2_tableStructureActionPerformed

    private void table_SQLresultMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_SQLresultMouseClicked
        // right klick ==> save to file
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_queryIDs);

            popUpSave.show(table_SQLresult, evt.getX(), evt.getY());
            this.outTable = table_SQLresult;
        } else {           
        }
    }//GEN-LAST:event_table_SQLresultMouseClicked

    private void popUpMenu_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_saveActionPerformed
        //saveOnRC(evt);     
        saveTable.saveOnRC(this.outTable, this.defaultPath, this);
    }//GEN-LAST:event_popUpMenu_saveActionPerformed

    private void popUpMenu_selectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_selectAllActionPerformed
        JTable OT = this.outTable;
        OT.selectAll();
    }//GEN-LAST:event_popUpMenu_selectAllActionPerformed

    private void txtArea_SQLKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtArea_SQLKeyPressed
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
            
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            try {
                String sql = txtArea_SQL.getText();
                my_log.logger.info("SQL:  " + sql);

                get_queryLabIDs(sql, pst, rs, conn);
                update_table_SQLresult();

            } catch (Exception e) {
                //JOptionPane.showMessageDialog(null, e.getMessage());
            } finally {
                try {
                    if (rs != null) { rs.close();}
                    if (pst != null) { pst.close();}
                    if (conn != null) { conn.close();}
                } catch (Exception e) {
                }
            }
        }
    }//GEN-LAST:event_txtArea_SQLKeyPressed

    private void jMenuItem1_infoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1_infoActionPerformed
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_med.png"));
        JOptionPane.showMessageDialog(rootPane, "LInkedResultsAnalysis \nDB-request Tool\nVersion:   1.0.0", "Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem1_infoActionPerformed

    private void jMenuItem1_HowToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1_HowToActionPerformed
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/Monsters-Snail-icon.png"));
        JOptionPane.showMessageDialog(rootPane, "... ummmmmm \n... errrrrr \n... pls ask again later", "apparently no useful Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem1_HowToActionPerformed

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
            java.util.logging.Logger.getLogger(ReadyForSQL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ReadyForSQL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ReadyForSQL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ReadyForSQL.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
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
                new ReadyForSQL().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Info_top;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_SQLquery;
    private javax.swing.JButton btn_clear;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1_HowTo;
    private javax.swing.JMenuItem jMenuItem1_info;
    private javax.swing.JMenuItem jMenuItem1_openModel;
    private javax.swing.JMenuItem jMenuItem2_tableStructure;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JMenuItem popUpMenu_save;
    private javax.swing.JMenuItem popUpMenu_selectAll;
    private javax.swing.JPopupMenu popUpSave;
    private javax.swing.JTable table_SQLresult;
    private javax.swing.JTable table_queryIDs;
    private javax.swing.JTable table_statistics;
    private javax.swing.JTextArea txtArea_SQL;
    // End of variables declaration//GEN-END:variables
}
