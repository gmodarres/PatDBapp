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

import static frames.SetConnection.PresentMode;
import myClass.DBconnect;
import java.sql.*;
import javax.swing.*;
import java.awt.Color;
import net.proteanit.sql.DbUtils;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;
import myClass.CustomSorter;
import myClass.IdManagement;
import myClass.Log;
import myClass.MenuDriver;

public class SearchMainResult extends javax.swing.JFrame {

    String ids = null;
    static byte[] imagedata = null;
    private ImageIcon format = null;
    String click_result = null;
    static String click_lID = null;
    
    boolean Present = PresentMode;
    
    Log my_log;
       
    /**
     * Creates new form patDB_search_result
     */
    public SearchMainResult() {
        MenuDriver menu = new MenuDriver();     // create instance of JMenuBar menuBarGlobal 
        this.setJMenuBar( menu.getMenuBar() );
        
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        this.setIconImage(img.getImage());
        setIcons();
        initial_table_queryIDs();
    }

    private void setIcons(){        
        JLabel lbl_array = new JLabel("Array");
        ImageIcon img1 = new javax.swing.ImageIcon(getClass().getResource("/ico/array_label_small.png"));
        lbl_array.setIcon(img1);
        lbl_array.setIconTextGap(10);
        tab_main.setTabComponentAt(0, lbl_array);
        
        JLabel lbl_fish = new JLabel("FISH");
        ImageIcon img2 = new javax.swing.ImageIcon(getClass().getResource("/ico/fish_label_small.png"));
        lbl_fish.setIcon(img2);
        lbl_fish.setIconTextGap(10);
        tab_main.setTabComponentAt(1, lbl_fish);
    
        JLabel lbl_zg = new JLabel("Cytogenetics");
        ImageIcon img3 = new javax.swing.ImageIcon(getClass().getResource("/ico/zg_label_small.png"));
        lbl_zg.setIcon(img3);
        lbl_zg.setIconTextGap(10);
        tab_main.setTabComponentAt(2, lbl_zg);
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
                String sql2 = "SELECT distinct s.lab_id, result_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND result_id IN ( " + all_ids + " )";
                if (Present == true) { // PRESENT
                    sql2 = "SELECT distinct s.lab_id, result_id, present as fname, present as surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND result_id IN ( " + all_ids + " )";
                }
                
                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();

                table_queryIDs.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_queryIDs);
                if (table_queryIDs.getColumnModel().getColumnCount() > 0) {
                    table_queryIDs.getColumnModel().getColumn(0).setPreferredWidth(85);      // 75
                    table_queryIDs.getColumnModel().getColumn(0).setMaxWidth(120);           // 80
                    table_queryIDs.getColumnModel().getColumn(1).setPreferredWidth(60);
                    table_queryIDs.getColumnModel().getColumn(1).setMaxWidth(60);
                    table_queryIDs.getColumnModel().getColumn(2).setPreferredWidth(80);
                    table_queryIDs.getColumnModel().getColumn(2).setMaxWidth(130);
                    //table_queryIDs.getColumnModel().getColumn(3).setPreferredWidth(80);
                    //table_queryIDs.getColumnModel().getColumn(3).setMaxWidth(130);
                    table_queryIDs.getColumnModel().getColumn(4).setPreferredWidth(30);
                    table_queryIDs.getColumnModel().getColumn(4).setMaxWidth(30);
                    table_queryIDs.getColumnModel().getColumn(5).setPreferredWidth(90);      // 80
                    table_queryIDs.getColumnModel().getColumn(5).setMaxWidth(120);           // 80
                    //table_queryIDs.getColumnModel().getColumn(6).setPreferredWidth(30);
                    //table_queryIDs.getColumnModel().getColumn(6).setMaxWidth(30);
                }
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
    
    private void get_queryLabIDs(String sql, PreparedStatement pst, ResultSet rs, Connection conn){        
        try{
            my_log.logger.info("SQL:  " + sql);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            
            //txtArea_sql.setText(sql);
            while(rs.next()){
                String id = rs.getString("result_id");
                all_ids = all_ids + "'" + id + "',";
                //txtArea_test.append("'"+id+"',");  // test
            }
            if (all_ids.length() > 1) {

                all_ids = all_ids.substring(0, (all_ids.length() - 1));
                String sql2 = "SELECT distinct s.lab_id, result_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND result_id IN ( " + all_ids + " )";
                if (Present == true) { // PRESENT
                    sql2 = "SELECT distinct s.lab_id, result_id, present as fname, present as surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND result_id IN ( " + all_ids + " )";
                }
                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();

                table_queryIDs.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_queryIDs);                
                if (table_queryIDs.getColumnModel().getColumnCount() > 0) {
                    table_queryIDs.getColumnModel().getColumn(0).setPreferredWidth(85);      // 75
                    table_queryIDs.getColumnModel().getColumn(0).setMaxWidth(120);           // 80
                    table_queryIDs.getColumnModel().getColumn(1).setPreferredWidth(60);
                    table_queryIDs.getColumnModel().getColumn(1).setMaxWidth(60);
                    table_queryIDs.getColumnModel().getColumn(2).setPreferredWidth(80);
                    table_queryIDs.getColumnModel().getColumn(2).setMaxWidth(130);
                    //table_queryIDs.getColumnModel().getColumn(3).setPreferredWidth(80);
                    //table_queryIDs.getColumnModel().getColumn(3).setMaxWidth(130);
                    table_queryIDs.getColumnModel().getColumn(4).setPreferredWidth(30);
                    table_queryIDs.getColumnModel().getColumn(4).setMaxWidth(30);
                    table_queryIDs.getColumnModel().getColumn(5).setPreferredWidth(90);      // 80
                    table_queryIDs.getColumnModel().getColumn(5).setMaxWidth(120);           // 80
                    //table_queryIDs.getColumnModel().getColumn(6).setPreferredWidth(30);
                    //table_queryIDs.getColumnModel().getColumn(6).setMaxWidth(30);
                }
                if (rs.last()) {
                    int rows = rs.getRow();
                    String getRows = String.valueOf(rows);
                    //JOptionPane.showMessageDialog(null, getRows+" row(s) returned");
                    lbl_rowsReturned.setText(getRows + " row(s) returned");
                    my_log.logger.info(getRows + " row(s) returned");
                }
            } else{
                    JOptionPane.showMessageDialog(null, "NO result for that!");
                    table_queryIDs.setModel(DbUtils.resultSetToTableModel(rs));
                    CustomSorter.table_customRowSort(table_queryIDs);
                }
            //get ids of sql query to count patients affected in get_statistics()
            //get_ids(sql, pst, rs, conn);
            this.ids = IdManagement.get_ids(sql, pst, rs, conn, "result_id");

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
          
    private void highlight_txt_array(String word){
        try{
            String text  = A_txtArea_intrpr.getText().toLowerCase();
            String findWord = word.toLowerCase();
            int index = text.indexOf(findWord);
            
            Highlighter highlighter = A_txtArea_intrpr.getHighlighter();
            HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.yellow);
            
            while (index >= 0) {  // indexOf returns -1 if no match found
                //JOptionPane.showMessageDialog(null, "index: "+ index);
                int p0 = index;
                int p1 = p0 + findWord.length();
                highlighter.addHighlight(p0, p1, painter);
                index = text.indexOf(findWord, index + 1);
            }
        }catch(Exception e){
        }    
    }
    
    private void highlight_txt_fish(String word){ 
        try{
            String text  = F_txtArea_intrpr.getText().toLowerCase();
            String findWord = word.toLowerCase();
            int index = text.indexOf(findWord);
            
            Highlighter highlighter = F_txtArea_intrpr.getHighlighter();
            HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.yellow);
            
            while (index >= 0) {  // indexOf returns -1 if no match found
                //JOptionPane.showMessageDialog(null, "index: "+ index);
                int p0 = index;
                int p1 = p0 + findWord.length();
                highlighter.addHighlight(p0, p1, painter);
                index = text.indexOf(findWord, index + 1);
            }

        }catch(Exception e){
        }    
    }    
    
    private void highlight_txt_zg(String word){ 
        try{
            String text  = ZG_txtArea_intrpr.getText().toLowerCase();
            String findWord = word.toLowerCase();
            int index = text.indexOf(findWord);
            
            Highlighter highlighter = ZG_txtArea_intrpr.getHighlighter();
            HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.yellow);
            
            while (index >= 0) {  // indexOf returns -1 if no match found
                //JOptionPane.showMessageDialog(null, "index: "+ index);
                int p0 = index;
                int p1 = p0 + findWord.length();
                highlighter.addHighlight(p0, p1, painter);
                index = text.indexOf(findWord, index + 1);
            }

        }catch(Exception e){
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
        Info_top = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_queryIDs = new javax.swing.JTable();
        btn_clear = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        btn_getResults = new javax.swing.JButton();
        btn_TTT = new javax.swing.JRadioButton();
        btn_array = new javax.swing.JToggleButton();
        btn_fish = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        btn_zg = new javax.swing.JToggleButton();
        lbl_rowsReturned = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        lbl_array = new javax.swing.JLabel();
        lbl_FISH = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtArea_search_array = new javax.swing.JTextArea();
        jScrollPane9 = new javax.swing.JScrollPane();
        txtArea_search_fish = new javax.swing.JTextArea();
        jScrollPane11 = new javax.swing.JScrollPane();
        txtArea_search_zg = new javax.swing.JTextArea();
        btn_search = new javax.swing.JButton();
        lbl_ZG = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        lbl_searchLabId = new javax.swing.JLabel();
        txt_searchResultId = new javax.swing.JTextField();
        txt_searchLabId = new javax.swing.JTextField();
        lbl_searchResultId = new javax.swing.JLabel();
        tab_main = new javax.swing.JTabbedPane();
        tab_array = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        A_txtArea_intrpr = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        A_txtArea_comm = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        A_txtArea_sumGenRes = new javax.swing.JTextArea();
        A_lab_sumGenRes = new javax.swing.JLabel();
        A_lab_intrpr = new javax.swing.JLabel();
        A_lbl_comment = new javax.swing.JLabel();
        A_lab_result = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        A_txtArea_result = new javax.swing.JTextArea();
        btn_Karyoview = new javax.swing.JButton();
        btn_WGV = new javax.swing.JButton();
        jPanel_INFO = new javax.swing.JPanel();
        lbl_showResultId1 = new javax.swing.JLabel();
        lbl_showLabId1 = new javax.swing.JLabel();
        tab_fish = new javax.swing.JPanel();
        F_lab_result = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        F_txtArea_intrpr = new javax.swing.JTextArea();
        jPanel_INFO2 = new javax.swing.JPanel();
        lbl_showResultId2 = new javax.swing.JLabel();
        lbl_showLabId2 = new javax.swing.JLabel();
        tab_ZG = new javax.swing.JPanel();
        ZG_lab_intrpr = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        ZG_txtArea_intrpr = new javax.swing.JTextArea();
        jPanel_INFO1 = new javax.swing.JPanel();
        lbl_showResultId3 = new javax.swing.JLabel();
        lbl_showLabId3 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        bnt_test = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Linked Results Analysis Tool - search main_result");
        setLocation(new java.awt.Point(150, 50));

        Info_top.setBackground(new java.awt.Color(102, 153, 255));
        Info_top.setBorder(javax.swing.BorderFactory.createEtchedBorder());

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
        table_queryIDs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_queryIDsMouseClicked(evt);
            }
        });
        table_queryIDs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table_queryIDsKeyReleased(evt);
            }
        });
        jScrollPane10.setViewportView(table_queryIDs);
        table_queryIDs.getAccessibleContext().setAccessibleName("table_queryIDs");
        table_queryIDs.getAccessibleContext().setAccessibleDescription("");

        btn_clear.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Actions-edit-clear-list-icon.png"))); // NOI18N
        btn_clear.setText("Clear all");
        btn_clear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_clearActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(102, 153, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btn_getResults.setBackground(new java.awt.Color(102, 102, 102));
        btn_getResults.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_getResults.setForeground(java.awt.Color.white);
        btn_getResults.setText("GET RESULTS");
        btn_getResults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_getResultsActionPerformed(evt);
            }
        });

        btn_TTT.setText("ToolTip Help on");
        btn_TTT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TTTActionPerformed(evt);
            }
        });

        btn_array.setBackground(java.awt.Color.gray);
        btn_array.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_array.setForeground(new java.awt.Color(255, 255, 255));
        btn_array.setText("ARRAY");

        btn_fish.setBackground(java.awt.Color.gray);
        btn_fish.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_fish.setForeground(new java.awt.Color(255, 255, 255));
        btn_fish.setText("FISH");

        jLabel1.setText("result exists :");

        btn_zg.setBackground(java.awt.Color.gray);
        btn_zg.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_zg.setForeground(new java.awt.Color(255, 255, 255));
        btn_zg.setText("CYTOGEN.");

        lbl_rowsReturned.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl_rowsReturned.setText(" ");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_zg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_array, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_fish, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_getResults, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                    .addComponent(btn_TTT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lbl_rowsReturned, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btn_getResults, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_array)
                .addGap(2, 2, 2)
                .addComponent(btn_fish)
                .addGap(2, 2, 2)
                .addComponent(btn_zg)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                .addComponent(lbl_rowsReturned)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btn_TTT)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(102, 153, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_array.setText("Array:");

        lbl_FISH.setText("FISH:");

        txtArea_search_array.setColumns(20);
        txtArea_search_array.setRows(2);
        jScrollPane8.setViewportView(txtArea_search_array);

        txtArea_search_fish.setColumns(20);
        txtArea_search_fish.setRows(2);
        jScrollPane9.setViewportView(txtArea_search_fish);

        txtArea_search_zg.setColumns(20);
        txtArea_search_zg.setRows(2);
        jScrollPane11.setViewportView(txtArea_search_zg);

        btn_search.setBackground(new java.awt.Color(0, 140, 140));
        btn_search.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/Search.png"))); // NOI18N
        btn_search.setText("search txt");
        btn_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_searchActionPerformed(evt);
            }
        });

        lbl_ZG.setText("ZG:");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btn_search, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_ZG, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_array, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lbl_FISH, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(7, 7, 7)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jScrollPane9)
                            .addComponent(jScrollPane11)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.Alignment.LEADING))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl_array, javax.swing.GroupLayout.Alignment.LEADING))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_FISH)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbl_ZG)
                    .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btn_search)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(102, 153, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl_searchLabId.setText("search lab_id:");

        txt_searchResultId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_searchResultIdActionPerformed(evt);
            }
        });

        txt_searchLabId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txt_searchLabIdActionPerformed(evt);
            }
        });

        lbl_searchResultId.setText("search result_id:");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txt_searchResultId)
                    .addComponent(lbl_searchResultId, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
                    .addComponent(txt_searchLabId)
                    .addComponent(lbl_searchLabId, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbl_searchLabId)
                .addGap(3, 3, 3)
                .addComponent(txt_searchLabId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbl_searchResultId)
                .addGap(3, 3, 3)
                .addComponent(txt_searchResultId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(9, 9, 9))
        );

        javax.swing.GroupLayout Info_topLayout = new javax.swing.GroupLayout(Info_top);
        Info_top.setLayout(Info_topLayout);
        Info_topLayout.setHorizontalGroup(
            Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_topLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btn_clear, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        Info_topLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel1, jPanel3});

        Info_topLayout.setVerticalGroup(
            Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_topLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(Info_topLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btn_clear))
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        tab_main.setBackground(new java.awt.Color(102, 153, 255));
        tab_main.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        A_txtArea_intrpr.setColumns(20);
        A_txtArea_intrpr.setLineWrap(true);
        A_txtArea_intrpr.setRows(5);
        A_txtArea_intrpr.setWrapStyleWord(true);
        jScrollPane1.setViewportView(A_txtArea_intrpr);

        A_txtArea_comm.setColumns(20);
        A_txtArea_comm.setLineWrap(true);
        A_txtArea_comm.setRows(5);
        A_txtArea_comm.setWrapStyleWord(true);
        jScrollPane2.setViewportView(A_txtArea_comm);

        A_txtArea_sumGenRes.setColumns(20);
        A_txtArea_sumGenRes.setLineWrap(true);
        A_txtArea_sumGenRes.setRows(5);
        A_txtArea_sumGenRes.setWrapStyleWord(true);
        jScrollPane3.setViewportView(A_txtArea_sumGenRes);

        A_lab_sumGenRes.setText("SUM. GEN. RESULT:");

        A_lab_intrpr.setText("INTERPRETATION:");

        A_lbl_comment.setText("COMMENT INT.:");

        A_lab_result.setText("RESULT:");

        A_txtArea_result.setColumns(20);
        A_txtArea_result.setLineWrap(true);
        A_txtArea_result.setRows(5);
        A_txtArea_result.setWrapStyleWord(true);
        jScrollPane6.setViewportView(A_txtArea_result);

        btn_Karyoview.setBackground(java.awt.Color.gray);
        btn_Karyoview.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_Karyoview.setForeground(new java.awt.Color(255, 255, 255));
        btn_Karyoview.setText("Karyoview");
        btn_Karyoview.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_KaryoviewActionPerformed(evt);
            }
        });

        btn_WGV.setBackground(java.awt.Color.gray);
        btn_WGV.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        btn_WGV.setForeground(new java.awt.Color(255, 255, 255));
        btn_WGV.setText("WGV");
        btn_WGV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_WGVActionPerformed(evt);
            }
        });

        lbl_showResultId1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_showResultId1.setForeground(new java.awt.Color(102, 153, 255));
        lbl_showResultId1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_showResultId1.setText(" ");

        lbl_showLabId1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_showLabId1.setForeground(new java.awt.Color(102, 153, 255));
        lbl_showLabId1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_showLabId1.setText(" ");

        javax.swing.GroupLayout jPanel_INFOLayout = new javax.swing.GroupLayout(jPanel_INFO);
        jPanel_INFO.setLayout(jPanel_INFOLayout);
        jPanel_INFOLayout.setHorizontalGroup(
            jPanel_INFOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_INFOLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_showResultId1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(lbl_showLabId1, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel_INFOLayout.setVerticalGroup(
            jPanel_INFOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_INFOLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel_INFOLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_showLabId1)
                    .addComponent(lbl_showResultId1))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout tab_arrayLayout = new javax.swing.GroupLayout(tab_array);
        tab_array.setLayout(tab_arrayLayout);
        tab_arrayLayout.setHorizontalGroup(
            tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_arrayLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_arrayLayout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btn_Karyoview, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btn_WGV, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(tab_arrayLayout.createSequentialGroup()
                        .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(A_lbl_comment)
                            .addComponent(A_lab_sumGenRes)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 671, Short.MAX_VALUE)
                            .addComponent(A_lab_result)
                            .addComponent(jScrollPane6))
                        .addGap(18, 18, 18)
                        .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 929, Short.MAX_VALUE)
                            .addGroup(tab_arrayLayout.createSequentialGroup()
                                .addComponent(A_lab_intrpr)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tab_arrayLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel_INFO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        tab_arrayLayout.setVerticalGroup(
            tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_arrayLayout.createSequentialGroup()
                .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_arrayLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(A_lab_intrpr)
                            .addComponent(A_lab_sumGenRes)))
                    .addComponent(jPanel_INFO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_arrayLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 409, Short.MAX_VALUE)
                        .addGap(24, 24, 24))
                    .addGroup(tab_arrayLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(A_lab_result)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(A_lbl_comment)))
                .addGroup(tab_arrayLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_arrayLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(btn_Karyoview, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btn_WGV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tab_arrayLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)))
                .addContainerGap())
        );

        tab_main.addTab("Array", tab_array);

        F_lab_result.setText("INTERPRETATION:");

        F_txtArea_intrpr.setColumns(20);
        F_txtArea_intrpr.setLineWrap(true);
        F_txtArea_intrpr.setRows(5);
        F_txtArea_intrpr.setWrapStyleWord(true);
        jScrollPane4.setViewportView(F_txtArea_intrpr);

        lbl_showResultId2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_showResultId2.setForeground(new java.awt.Color(102, 153, 255));
        lbl_showResultId2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_showResultId2.setText(" ");

        lbl_showLabId2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_showLabId2.setForeground(new java.awt.Color(102, 153, 255));
        lbl_showLabId2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_showLabId2.setText(" ");

        javax.swing.GroupLayout jPanel_INFO2Layout = new javax.swing.GroupLayout(jPanel_INFO2);
        jPanel_INFO2.setLayout(jPanel_INFO2Layout);
        jPanel_INFO2Layout.setHorizontalGroup(
            jPanel_INFO2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_INFO2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_showResultId2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(lbl_showLabId2, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel_INFO2Layout.setVerticalGroup(
            jPanel_INFO2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_INFO2Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel_INFO2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_showLabId2)
                    .addComponent(lbl_showResultId2))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout tab_fishLayout = new javax.swing.GroupLayout(tab_fish);
        tab_fish.setLayout(tab_fishLayout);
        tab_fishLayout.setHorizontalGroup(
            tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_fishLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_fishLayout.createSequentialGroup()
                        .addComponent(F_lab_result)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1618, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, tab_fishLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel_INFO2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        tab_fishLayout.setVerticalGroup(
            tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_fishLayout.createSequentialGroup()
                .addGroup(tab_fishLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_fishLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(F_lab_result))
                    .addComponent(jPanel_INFO2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                .addContainerGap())
        );

        tab_main.addTab("FISH", tab_fish);

        ZG_lab_intrpr.setText("INTERPRETATION:");

        ZG_txtArea_intrpr.setColumns(20);
        ZG_txtArea_intrpr.setLineWrap(true);
        ZG_txtArea_intrpr.setRows(5);
        ZG_txtArea_intrpr.setWrapStyleWord(true);
        jScrollPane7.setViewportView(ZG_txtArea_intrpr);

        lbl_showResultId3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_showResultId3.setForeground(new java.awt.Color(102, 153, 255));
        lbl_showResultId3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_showResultId3.setText(" ");

        lbl_showLabId3.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbl_showLabId3.setForeground(new java.awt.Color(102, 153, 255));
        lbl_showLabId3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_showLabId3.setText(" ");

        javax.swing.GroupLayout jPanel_INFO1Layout = new javax.swing.GroupLayout(jPanel_INFO1);
        jPanel_INFO1.setLayout(jPanel_INFO1Layout);
        jPanel_INFO1Layout.setHorizontalGroup(
            jPanel_INFO1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_INFO1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_showResultId3, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(lbl_showLabId3, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        jPanel_INFO1Layout.setVerticalGroup(
            jPanel_INFO1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_INFO1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(jPanel_INFO1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbl_showLabId3)
                    .addComponent(lbl_showResultId3))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout tab_ZGLayout = new javax.swing.GroupLayout(tab_ZG);
        tab_ZG.setLayout(tab_ZGLayout);
        tab_ZGLayout.setHorizontalGroup(
            tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, tab_ZGLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_ZGLayout.createSequentialGroup()
                        .addComponent(ZG_lab_intrpr)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1618, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(tab_ZGLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel_INFO1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        tab_ZGLayout.setVerticalGroup(
            tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tab_ZGLayout.createSequentialGroup()
                .addGroup(tab_ZGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(tab_ZGLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(ZG_lab_intrpr))
                    .addComponent(jPanel_INFO1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 533, Short.MAX_VALUE)
                .addContainerGap())
        );

        tab_main.addTab("ZG", tab_ZG);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Info_top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(tab_main))
                .addGap(15, 15, 15))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tab_main)
                .addGap(18, 18, 18))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void btn_WGVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_WGVActionPerformed
        // ... open picture in new frame
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        //JOptionPane.showMessageDialog(null, click_result);
        
        String result_idFromField = txt_searchResultId.getText();

        try {
            //String sql = "SELECT ar_wgview FROM main_result WHERE result_id= "+ click_result ;
            String sql = "SELECT ar_wgview FROM main_result WHERE result_id= "+ result_idFromField ;
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                imagedata = rs.getBytes("ar_wgview");
                format = new ImageIcon(imagedata);

                new ViewKaryoWG().setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "no picture available!");
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_btn_WGVActionPerformed

    private void btn_KaryoviewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_KaryoviewActionPerformed
        // ... open picture in new frame
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        //JOptionPane.showMessageDialog(null, click_result);

        String result_idFromField = txt_searchResultId.getText();
        
        try {
            //String sql = "SELECT ar_karyoview FROM main_result WHERE result_id= "+ click_result ;
            String sql = "SELECT ar_karyoview FROM main_result WHERE result_id= "+ result_idFromField;
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            //JOptionPane.showMessageDialog(null, sql);

            if (rs.next()) {
                imagedata = rs.getBytes("ar_karyoview");
                format = new ImageIcon(imagedata);
                //label_image.setIcon(format);

                new ViewKaryoWG().setVisible(true);
                //s.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "no picture available!");
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_btn_KaryoviewActionPerformed

    private void btn_TTTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TTTActionPerformed
        if (btn_TTT.isSelected()){
            ToolTipManager.sharedInstance().setEnabled(true);
            // Get current delay
            //int initialDelay = ToolTipManager.sharedInstance().getInitialDelay();
            // Show tool tips immediately
            ToolTipManager.sharedInstance().setInitialDelay(0);
        } else {
            ToolTipManager.sharedInstance().setEnabled(false);
        }
    }//GEN-LAST:event_btn_TTTActionPerformed

    private void btn_getResultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_getResultsActionPerformed
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String sql = "SELECT * from main_result Where 1=1";

            if (btn_array.isSelected()) { sql = sql + " AND ar_intrpr is not null"; }
            if (btn_fish.isSelected()) { sql = sql + " AND fish_intrpr is not null"; }
            if (btn_zg.isSelected()) { sql = sql + " AND zg_intrpr is not null"; }

            get_queryLabIDs(sql, pst, rs, conn);
            //txtArea_sql.setText(sql);

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
    }//GEN-LAST:event_btn_getResultsActionPerformed

    private void txt_searchResultIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_searchResultIdActionPerformed
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        String sql = "SELECT * from main_result WHERE result_id=?";
        try {
            pst = conn.prepareStatement(sql);
            try{
                int r_id_int = Integer.parseInt(txt_searchResultId.getText());
            }catch (Exception e){
                JOptionPane.showMessageDialog(null, "Not a valid result_id!" );
            }

            pst.setString(1, txt_searchResultId.getText());

            rs = pst.executeQuery();
            if (rs.next()) {
                String ar_sgr = rs.getString("ar_sumGenRes");
                A_txtArea_sumGenRes.setText(ar_sgr);
                String ar_result = rs.getString("ar_result");
                A_txtArea_result.setText(ar_result);
                String ar_intrpr = rs.getString("ar_intrpr");
                A_txtArea_intrpr.setText(ar_intrpr);
                String ar_comm = rs.getString("ar_comm");
                A_txtArea_comm.setText(ar_comm);

                String f_intrpr = rs.getString("fish_intrpr");
                F_txtArea_intrpr.setText(f_intrpr);

                String l_id = rs.getString("lab_id");
                String r_id = rs.getString("result_id");
                txt_searchLabId.setText(l_id);

                lbl_showLabId1.setText(l_id);
                lbl_showResultId1.setText(r_id);
                lbl_showLabId2.setText(l_id);
                lbl_showResultId2.setText(r_id);
                lbl_showLabId3.setText(l_id);
                lbl_showResultId3.setText(r_id);

                rs.close();
                pst.close();

            } else {
                JOptionPane.showMessageDialog(null, "result_id does not exist");
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_txt_searchResultIdActionPerformed

    private void btn_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_searchActionPerformed
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            String sql = "SELECT * from main_result Where 1=1";

            String s_array = txtArea_search_array.getText();
            String s_fish = txtArea_search_fish.getText();
            String s_zg = txtArea_search_zg.getText();

            if (txtArea_search_array !=null && !txtArea_search_array.getText().isEmpty()){ sql = sql + " AND ar_intrpr LIKE '%" + s_array + "%'"; }
            if (txtArea_search_fish !=null && !txtArea_search_fish.getText().isEmpty()){ sql = sql + " AND fish_intrpr LIKE '%" + s_fish + "%'"; }
            if (txtArea_search_zg !=null && !txtArea_search_zg.getText().isEmpty()){ sql = sql + " AND zg_intrpr LIKE '%" + s_zg + "%'"; }

            if (btn_array.isSelected()) { sql = sql + " AND ar_intrpr is not null"; }
            if (btn_fish.isSelected()) { sql = sql + " AND fish_intrpr is not null"; }
            if (btn_zg.isSelected()) { sql = sql + " AND zg_intrpr is not null"; }

            get_queryLabIDs(sql, pst, rs, conn);
            //txtArea_sql.setText(sql);

        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
            my_log.logger.warning("ERROR: " + e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_btn_searchActionPerformed

    private void btn_clearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_clearActionPerformed
        initial_table_queryIDs();
        txt_searchLabId.setText("");
        txt_searchResultId.setText("");
        lbl_rowsReturned.setText("");

        A_txtArea_sumGenRes.setText("");
        A_txtArea_result.setText("");
        A_txtArea_intrpr.setText("");
        A_txtArea_comm.setText("");
        F_txtArea_intrpr.setText("");
        ZG_txtArea_intrpr.setText("");

        btn_array.setBackground(Color.gray); btn_array.setForeground(Color.WHITE);
        btn_fish.setBackground(Color.gray); btn_fish.setForeground(Color.WHITE);
        btn_zg.setBackground(Color.gray); btn_zg.setForeground(Color.WHITE);
        btn_array.setSelected(false);
        btn_fish.setSelected(false);
        btn_zg.setSelected(false);

        txtArea_search_array.setText("");
        txtArea_search_fish.setText("");
        txtArea_search_zg.setText("");

        lbl_showLabId1.setText("");
        lbl_showResultId1.setText("");
        lbl_showLabId2.setText("");
        lbl_showResultId2.setText("");
        lbl_showLabId3.setText("");
        lbl_showResultId3.setText("");
    }//GEN-LAST:event_btn_clearActionPerformed

    private void txt_searchLabIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txt_searchLabIdActionPerformed
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        String sql = "SELECT * from main_result WHERE lab_id=?";
        try {
            pst = conn.prepareStatement(sql);
            pst.setString(1, txt_searchLabId.getText());

            rs = pst.executeQuery();
            if (rs.next()) {
                String ar_sgr = rs.getString("ar_sumGenRes");
                A_txtArea_sumGenRes.setText(ar_sgr);
                String ar_result = rs.getString("ar_result");
                A_txtArea_result.setText(ar_result);
                String ar_intrpr = rs.getString("ar_intrpr");
                A_txtArea_intrpr.setText(ar_intrpr);
                String ar_comm = rs.getString("ar_comm");
                A_txtArea_comm.setText(ar_comm);

                String f_intrpr = rs.getString("fish_intrpr");
                F_txtArea_intrpr.setText(f_intrpr);

                String l_id = rs.getString("lab_id");
                String r_id = rs.getString("result_id");
                txt_searchResultId.setText(r_id);

                lbl_showLabId1.setText(l_id);
                lbl_showResultId1.setText(r_id);
                lbl_showLabId2.setText(l_id);
                lbl_showResultId2.setText(r_id);
                lbl_showLabId3.setText(l_id);
                lbl_showResultId3.setText(r_id);
                
                //TODO: color buttons wgv & karyoview, if picture is available
                String karyoview = rs.getString("ar_karyoview");
                String wgview = rs.getString("ar_wgview");
                
                if (karyoview !=null && !karyoview.isEmpty()){ btn_Karyoview.setBackground(Color.blue);btn_Karyoview.setForeground(Color.WHITE);}
                else{ btn_Karyoview.setBackground(Color.gray); btn_Karyoview.setForeground(Color.WHITE); }
                if (wgview !=null && !wgview.isEmpty()){ btn_WGV.setBackground(Color.blue);btn_WGV.setForeground(Color.WHITE);}
                else{ btn_WGV.setBackground(Color.gray); btn_WGV.setForeground(Color.WHITE); }

                rs.close();
                pst.close();

            } else {
                JOptionPane.showMessageDialog(null, "lab_id does not exist");
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_txt_searchLabIdActionPerformed

    private void table_queryIDsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_queryIDsKeyReleased
        //same code as      table_queryIDsMouseClicked(java.awt.event.MouseEvent evt)
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            int row = table_queryIDs.getSelectedRow();
            //String Table_click = (table_queryIDs.getModel().getValueAt(row, 0).toString());  // values not correct anymore, if auto table rowsorter is used -->
            String Table_click = (table_queryIDs.getValueAt(row, 0).toString());
            click_result = (table_queryIDs.getValueAt(row, 1).toString());
            click_lID = Table_click;

            String sql = "SELECT distinct m.lab_id, result_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
            + "Where p.pat_id=s.pat_id AND m.lab_id='" + Table_click + "' ";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                String l_id = rs.getString("lab_id");
                txt_searchLabId.setText(l_id);
                String r_id = rs.getString("result_id");
                txt_searchResultId.setText(r_id);

                lbl_showLabId1.setText(l_id);
                lbl_showResultId1.setText(r_id);
                lbl_showLabId2.setText(l_id);
                lbl_showResultId2.setText(r_id);
                lbl_showLabId3.setText(l_id);
                lbl_showResultId3.setText(r_id);

                //txtArea_sql.setText(sql);
                String sql2 = "SELECT * from main_result WHERE lab_id='" + l_id + "' ";

                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String ar_sgr = rs.getString("ar_sumGenRes");
                    A_txtArea_sumGenRes.setText(ar_sgr);
                    String ar_result = rs.getString("ar_result");
                    A_txtArea_result.setText(ar_result);
                    String ar_intrpr = rs.getString("ar_intrpr");
                    A_txtArea_intrpr.setText(ar_intrpr);
                    String ar_comm = rs.getString("ar_comm");
                    A_txtArea_comm.setText(ar_comm);

                    String f_intrpr = rs.getString("fish_intrpr");
                    F_txtArea_intrpr.setText(f_intrpr);

                    String zg_intrpr = rs.getString("zg_intrpr");
                    ZG_txtArea_intrpr.setText(zg_intrpr);

                    if (txtArea_search_array != null && !txtArea_search_array.getText().isEmpty()){
                        String array_HL = txtArea_search_array.getText();
                        highlight_txt_array(array_HL);
                    }

                    if (txtArea_search_fish != null && !txtArea_search_fish.getText().isEmpty()){
                        String fish_HL = txtArea_search_fish.getText();
                        highlight_txt_fish(fish_HL);
                    }

                    if (txtArea_search_zg !=null && !txtArea_search_zg.getText().isEmpty()){
                        String zg_HL = txtArea_search_zg.getText();
                        highlight_txt_zg(zg_HL);
                    }

                    String karyoview = rs.getString("ar_karyoview");
                    String wgview = rs.getString("ar_wgview");

                    //if (A_txtArea_intrpr !=null && !A_txtArea_intrpr.getText().isEmpty()){ tab_array.setBackground(Color.red);  }
                    if (A_txtArea_intrpr !=null && !A_txtArea_intrpr.getText().isEmpty()){ /*btn_array.setBackground(Color.blue);*/ btn_array.setForeground(Color.ORANGE); }
                    else{ btn_array.setBackground(Color.gray); btn_array.setForeground(Color.WHITE); }
                    if (F_txtArea_intrpr !=null && !F_txtArea_intrpr.getText().isEmpty()){ /*btn_fish.setBackground(Color.blue);*/ btn_fish.setForeground(Color.ORANGE); }
                    else{ btn_fish.setBackground(Color.gray); btn_fish.setForeground(Color.WHITE); }
                    if (ZG_txtArea_intrpr !=null && !ZG_txtArea_intrpr.getText().isEmpty()){ /*btn_zg.setBackground(Color.blue);*/ btn_zg.setForeground(Color.ORANGE); }
                    else{ btn_zg.setBackground(Color.gray); btn_zg.setForeground(Color.WHITE); }
                    if (karyoview !=null && !karyoview.isEmpty()){ btn_Karyoview.setBackground(Color.blue);btn_Karyoview.setForeground(Color.WHITE);}
                    else{ btn_Karyoview.setBackground(Color.gray); btn_Karyoview.setForeground(Color.WHITE); }
                    if (wgview !=null && !wgview.isEmpty()){ btn_WGV.setBackground(Color.blue);btn_WGV.setForeground(Color.WHITE);}
                    else{ btn_WGV.setBackground(Color.gray); btn_WGV.setForeground(Color.WHITE); }
                }
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_table_queryIDsKeyReleased

    private void table_queryIDsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_queryIDsMouseClicked
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;

        try {
            int row = table_queryIDs.getSelectedRow();
            //String Table_click = (table_queryIDs.getModel().getValueAt(row, 0).toString());  // values not correct anymore, if auto table rowsorter is used -->
            String Table_click = (table_queryIDs.getValueAt(row, 0).toString());
            click_result = (table_queryIDs.getValueAt(row, 1).toString());
            click_lID = Table_click;

            String sql = "SELECT distinct m.lab_id, result_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
            + "Where p.pat_id=s.pat_id AND m.lab_id='" + Table_click + "' ";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {
                String l_id = rs.getString("lab_id");
                txt_searchLabId.setText(l_id);
                String r_id = rs.getString("result_id");
                txt_searchResultId.setText(r_id);

                lbl_showLabId1.setText(l_id);
                lbl_showResultId1.setText(r_id);
                lbl_showLabId2.setText(l_id);
                lbl_showResultId2.setText(r_id);
                lbl_showLabId3.setText(l_id);
                lbl_showResultId3.setText(r_id);

                //txtArea_sql.setText(sql);
                String sql2 = "SELECT * from main_result WHERE lab_id='" + l_id + "' ";

                pst = conn.prepareStatement(sql2);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String ar_sgr = rs.getString("ar_sumGenRes");
                    A_txtArea_sumGenRes.setText(ar_sgr);
                    String ar_result = rs.getString("ar_result");
                    A_txtArea_result.setText(ar_result);
                    String ar_intrpr = rs.getString("ar_intrpr");
                    A_txtArea_intrpr.setText(ar_intrpr);
                    String ar_comm = rs.getString("ar_comm");
                    A_txtArea_comm.setText(ar_comm);

                    String f_intrpr = rs.getString("fish_intrpr");
                    F_txtArea_intrpr.setText(f_intrpr);

                    String zg_intrpr = rs.getString("zg_intrpr");
                    ZG_txtArea_intrpr.setText(zg_intrpr);

                    if (txtArea_search_array != null && !txtArea_search_array.getText().isEmpty()){
                        String array_HL = txtArea_search_array.getText();
                        highlight_txt_array(array_HL);
                    }

                    if (txtArea_search_fish != null && !txtArea_search_fish.getText().isEmpty()){
                        String fish_HL = txtArea_search_fish.getText();
                        highlight_txt_fish(fish_HL);
                    }

                    if (txtArea_search_zg !=null && !txtArea_search_zg.getText().isEmpty()){
                        String zg_HL = txtArea_search_zg.getText();
                        highlight_txt_zg(zg_HL);
                    }

                    String karyoview = rs.getString("ar_karyoview");
                    String wgview = rs.getString("ar_wgview");

                    //if (A_txtArea_intrpr !=null && !A_txtArea_intrpr.getText().isEmpty()){ tab_array.setBackground(Color.red);  }
                    if (A_txtArea_intrpr !=null && !A_txtArea_intrpr.getText().isEmpty()){ /*btn_array.setBackground(Color.blue);*/ btn_array.setForeground(Color.ORANGE); }
                    else{ btn_array.setBackground(Color.gray); btn_array.setForeground(Color.WHITE); }
                    if (F_txtArea_intrpr !=null && !F_txtArea_intrpr.getText().isEmpty()){ /*btn_fish.setBackground(Color.blue);*/ btn_fish.setForeground(Color.ORANGE); }
                    else{ btn_fish.setBackground(Color.gray); btn_fish.setForeground(Color.WHITE); }
                    if (ZG_txtArea_intrpr !=null && !ZG_txtArea_intrpr.getText().isEmpty()){ /*btn_zg.setBackground(Color.blue);*/ btn_zg.setForeground(Color.ORANGE); }
                    else{ btn_zg.setBackground(Color.gray); btn_zg.setForeground(Color.WHITE); }
                    if (karyoview !=null && !karyoview.isEmpty()){ btn_Karyoview.setBackground(Color.blue);btn_Karyoview.setForeground(Color.WHITE);}
                    else{ btn_Karyoview.setBackground(Color.gray); btn_Karyoview.setForeground(Color.WHITE); }
                    if (wgview !=null && !wgview.isEmpty()){ btn_WGV.setBackground(Color.blue);btn_WGV.setForeground(Color.WHITE);}
                    else{ btn_WGV.setBackground(Color.gray); btn_WGV.setForeground(Color.WHITE); }
                }
            }
        }catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }finally {
            try {
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }//GEN-LAST:event_table_queryIDsMouseClicked

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
            java.util.logging.Logger.getLogger(SearchMainResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SearchMainResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SearchMainResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SearchMainResult.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new SearchMainResult().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel A_lab_intrpr;
    private javax.swing.JLabel A_lab_result;
    private javax.swing.JLabel A_lab_sumGenRes;
    private javax.swing.JLabel A_lbl_comment;
    private javax.swing.JTextArea A_txtArea_comm;
    private javax.swing.JTextArea A_txtArea_intrpr;
    private javax.swing.JTextArea A_txtArea_result;
    private javax.swing.JTextArea A_txtArea_sumGenRes;
    private javax.swing.JLabel F_lab_result;
    private javax.swing.JTextArea F_txtArea_intrpr;
    private javax.swing.JPanel Info_top;
    private javax.swing.JLabel ZG_lab_intrpr;
    private javax.swing.JTextArea ZG_txtArea_intrpr;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_Karyoview;
    private javax.swing.JRadioButton btn_TTT;
    private javax.swing.JButton btn_WGV;
    private javax.swing.JToggleButton btn_array;
    private javax.swing.JButton btn_clear;
    private javax.swing.JToggleButton btn_fish;
    private javax.swing.JButton btn_getResults;
    private javax.swing.JButton btn_search;
    private javax.swing.JToggleButton btn_zg;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel_INFO;
    private javax.swing.JPanel jPanel_INFO1;
    private javax.swing.JPanel jPanel_INFO2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_FISH;
    private javax.swing.JLabel lbl_ZG;
    private javax.swing.JLabel lbl_array;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JLabel lbl_searchLabId;
    private javax.swing.JLabel lbl_searchResultId;
    private javax.swing.JLabel lbl_showLabId1;
    private javax.swing.JLabel lbl_showLabId2;
    private javax.swing.JLabel lbl_showLabId3;
    private javax.swing.JLabel lbl_showResultId1;
    private javax.swing.JLabel lbl_showResultId2;
    private javax.swing.JLabel lbl_showResultId3;
    private javax.swing.JPanel tab_ZG;
    private javax.swing.JPanel tab_array;
    private javax.swing.JPanel tab_fish;
    private javax.swing.JTabbedPane tab_main;
    private javax.swing.JTable table_queryIDs;
    private javax.swing.JTextArea txtArea_search_array;
    private javax.swing.JTextArea txtArea_search_fish;
    private javax.swing.JTextArea txtArea_search_zg;
    private javax.swing.JTextField txt_searchLabId;
    private javax.swing.JTextField txt_searchResultId;
    // End of variables declaration//GEN-END:variables
}
