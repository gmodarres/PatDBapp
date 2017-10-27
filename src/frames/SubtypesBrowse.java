/*
 * stdpat_DB - Project study patient database 
 * For efficient data evaluation and interpretation
 *
 * Copyright (C) CCRI - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Gerda modarres <gerda.modarres@ccri.at>, August 2017
 *
 */
package frames;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import myClass.CustomSorter;
import myClass.DBconnect;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author gerda.modarres
 */
public class SubtypesBrowse extends javax.swing.JFrame {

    String ids = null;
    JTable outTable = null;  
    static String ST_resultIDs = null;
        
    /**
     * Creates new form PatientBrowse
     */
    public SubtypesBrowse() {
        initComponents();
        initial_table_subtypes();
        initial_table_resultID();
        Info_top4.getRootPane().setDefaultButton(btn_newList);

    }

     private void showRows(ResultSet rs){
        try {
            if (rs.last()) {
                int rows = rs.getRow();
                String getRows = String.valueOf(rows);
                lbl_rowsReturned.setText(getRows+" row(s) returned");
                //my_log.logger.info(getRows+" row(s) returned");
            }
        } catch (SQLException ex) {
            Logger.getLogger(SampleBrowse.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
     
    private void initial_table_subtypes(){
    
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        //String sql = "SELECT * from sample";
        String sql = "SELECT * FROM subtypes";
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            table_subtypes.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_subtypes);
            
            if (table_subtypes.getColumnModel().getColumnCount() > 0) {
                table_subtypes.getColumnModel().getColumn(0).setPreferredWidth(60);
                table_subtypes.getColumnModel().getColumn(0).setMaxWidth(60);
                table_subtypes.getColumnModel().getColumn(1).setPreferredWidth(80);
                table_subtypes.getColumnModel().getColumn(1).setMaxWidth(100);
            }
            
            get_ids(sql,pst,rs,conn);
            
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
    
     private void initial_table_resultID(){
    
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT p.pat_id, m.result_id, m.lab_id FROM main_result m, sample s, patient p, subtypes t"
                + " Where m.lab_id=s.lab_id and s.pat_id=p.pat_id and t.lab_id=s.lab_id;";
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            table_resultID.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_resultID);
           
            if (table_resultID.getColumnModel().getColumnCount() > 0) {
                table_resultID.getColumnModel().getColumn(0).setPreferredWidth(80);
                table_resultID.getColumnModel().getColumn(0).setMaxWidth(100);
                table_resultID.getColumnModel().getColumn(1).setPreferredWidth(80);
                table_resultID.getColumnModel().getColumn(1).setMaxWidth(100);
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
    }
     
    private void update_table_resultID() {
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String all_ids = this.ids;
                                       
        if (all_ids.length() > 1) {
            all_ids = all_ids.substring(0, (all_ids.length() - 1));

            String sql = "SELECT p.pat_id, m.result_id, m.lab_id FROM main_result m, sample s, patient p"
                    + " Where m.lab_id=s.lab_id and s.pat_id=p.pat_id"
                    + " AND m.lab_id in (" + all_ids + ")";
            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_resultID.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_resultID);
                
                if (table_resultID.getColumnModel().getColumnCount() > 0) {
                    table_resultID.getColumnModel().getColumn(0).setPreferredWidth(80);
                    table_resultID.getColumnModel().getColumn(0).setMaxWidth(100);
                    table_resultID.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_resultID.getColumnModel().getColumn(1).setMaxWidth(100);
                }
                
                get_r_ids(sql,pst,rs,conn);
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
    }
     
    private void get_r_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn) {
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_r_ids = "";
            String r_id_rem = "";
            while (rs.next()) {
                String r_id = rs.getString("result_id");

                if (!r_id.equals(r_id_rem)) {
                    r_id_rem = r_id;
                    all_r_ids = all_r_ids + "'" + r_id + "',";
                } else {
                    //JOptionPane.showMessageDialog(null, "id already in list: " + id + "  "+ id_rem); // test
                }
                //txtArea_test.append("'"+r_id+"',");  // test
            }
            this.ST_resultIDs = all_r_ids;
            //txtArea_test.setText(all_r_ids);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                //rs.close(); pst.close(); //conn.close();
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                //if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }
    }
        
        
    private void get_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn) {
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            String id_rem = "";
            
            while (rs.next()) {
                //this.rs_sizeList.add(rs.getString("array_sub_id"));
                String id = rs.getString("lab_id");
                
                if (!id.equals(id_rem)){
                    id_rem = id;
                    all_ids = all_ids +"'"+id+"',";
                }else{
                    //JOptionPane.showMessageDialog(null, "id already in list: " + id + "  "+ id_rem); // test
                }
                //Combobox_id.addItem(id);        // test
                //txtArea_test.append("'"+id+"',");  // test
            }
            this.ids = all_ids;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {
                //rs.close(); pst.close(); //conn.close();
                if (rs != null) { rs.close();}
                if (pst != null) { pst.close();}
                //if (conn != null) { conn.close();}
            } catch (Exception e) {
            }
        }

    }
     
    private static boolean isRightClick(MouseEvent e) {
        return (e.getButton() == MouseEvent.BUTTON3
                || (System.getProperty("os.name").contains("Mac OS X")
                && (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0
                && (e.getModifiers() & InputEvent.CTRL_MASK) != 0));
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
        popUpResult = new javax.swing.JPopupMenu();
        cpResultIds = new javax.swing.JMenuItem();
        cpLabIds = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        bnt_test = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_subtypes = new javax.swing.JTable();
        Info_top4 = new javax.swing.JPanel();
        btn_newList = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        rbtn_majSub = new javax.swing.JRadioButton();
        rbtn_NOT = new javax.swing.JRadioButton();
        txt_specST = new javax.swing.JTextField();
        rbtn_specST = new javax.swing.JRadioButton();
        CB_specST = new javax.swing.JComboBox<>();
        CB_andor1 = new javax.swing.JComboBox<>();
        rbtn_specST1 = new javax.swing.JRadioButton();
        CB_specST1 = new javax.swing.JComboBox<>();
        rbtn_NOT1 = new javax.swing.JRadioButton();
        txt_specST1 = new javax.swing.JTextField();
        rbtn_all = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        table_resultID = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtArea_test = new javax.swing.JTextArea();
        lbl_rowsReturned = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        cpResultIds.setText("copy result_ids ...");
        cpResultIds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpResultIdsActionPerformed(evt);
            }
        });
        popUpResult.add(cpResultIds);

        cpLabIds.setText("copy lab_ids ...\n");
        cpLabIds.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cpLabIdsActionPerformed(evt);
            }
        });
        popUpResult.add(cpLabIds);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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

        table_subtypes.setAutoCreateRowSorter(true);
        table_subtypes.setModel(new javax.swing.table.DefaultTableModel(
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
        table_subtypes.setCellSelectionEnabled(true);
        jScrollPane1.setViewportView(table_subtypes);
        table_subtypes.getAccessibleContext().setAccessibleName("table_subtypes");
        table_subtypes.getAccessibleContext().setAccessibleDescription("");

        Info_top4.setBackground(new java.awt.Color(102, 153, 255));
        Info_top4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Info_top4.setRequestFocusEnabled(false);

        btn_newList.setText("new List");
        btn_newList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_newListActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(102, 153, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        buttonGroup1.add(rbtn_majSub);
        rbtn_majSub.setText("s. with subtype");
        rbtn_majSub.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_majSub.setBorderPainted(true);
        rbtn_majSub.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_majSubActionPerformed(evt);
            }
        });

        rbtn_NOT.setText("NOT");
        rbtn_NOT.setEnabled(false);

        txt_specST.setBackground(new java.awt.Color(204, 204, 204));
        txt_specST.setEnabled(false);

        rbtn_specST.setEnabled(false);

        CB_specST.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "maj. subtype", "spec. subt. 1", "spec. subt. 2", "spec. subt. 3" }));
        CB_specST.setEnabled(false);

        CB_andor1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "AND", "OR" }));
        CB_andor1.setEnabled(false);

        rbtn_specST1.setEnabled(false);

        CB_specST1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "maj. subtype", "spec. subt. 1", "spec. subt. 2", "spec. subt. 3" }));
        CB_specST1.setEnabled(false);

        rbtn_NOT1.setText("NOT");
        rbtn_NOT1.setEnabled(false);

        txt_specST1.setBackground(new java.awt.Color(204, 204, 204));
        txt_specST1.setEnabled(false);

        buttonGroup1.add(rbtn_all);
        rbtn_all.setSelected(true);
        rbtn_all.setText("all samples");
        rbtn_all.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        rbtn_all.setBorderPainted(true);
        rbtn_all.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_allActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(CB_andor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtn_all, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(rbtn_specST, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(rbtn_specST1, javax.swing.GroupLayout.Alignment.TRAILING))
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(CB_specST, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                            .addGap(3, 3, 3)
                                            .addComponent(CB_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addComponent(rbtn_majSub, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(5, 5, 5)
                            .addComponent(rbtn_NOT)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_specST, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addComponent(rbtn_NOT1)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(txt_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(5, 5, 5))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rbtn_specST, rbtn_specST1});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {rbtn_all, rbtn_majSub});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_specST, txt_specST1});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {CB_specST, CB_specST1});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(rbtn_all)
                        .addGap(4, 4, 4)
                        .addComponent(rbtn_majSub)
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT)
                            .addComponent(rbtn_specST)
                            .addComponent(txt_specST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_specST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(rbtn_NOT1)
                            .addComponent(rbtn_specST1)
                            .addComponent(txt_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_specST1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(CB_andor1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(5, 5, 5))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_specST, rbtn_specST});

        jPanel1Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {CB_andor1, CB_specST1, rbtn_specST1});

        rbtn_majSub.getAccessibleContext().setAccessibleName("subtypes");

        javax.swing.GroupLayout Info_top4Layout = new javax.swing.GroupLayout(Info_top4);
        Info_top4.setLayout(Info_top4Layout);
        Info_top4Layout.setHorizontalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 401, Short.MAX_VALUE)
                .addComponent(btn_newList, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(290, 290, 290))
        );
        Info_top4Layout.setVerticalGroup(
            Info_top4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_top4Layout.createSequentialGroup()
                .addGap(99, 99, 99)
                .addComponent(btn_newList)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Info_top4Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        table_resultID.setAutoCreateRowSorter(true);
        table_resultID.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "pat_id", "result_id", "lab_id"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Long.class, java.lang.Long.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        table_resultID.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_resultIDMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(table_resultID);
        table_resultID.getAccessibleContext().setAccessibleName("table_resultID");

        txtArea_test.setColumns(20);
        txtArea_test.setRows(5);
        jScrollPane3.setViewportView(txtArea_test);

        lbl_rowsReturned.setText(" ");

        jMenu4.setBorder(null);
        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ico/NeedsAName_Font_small07.png"))); // NOI18N
        jMenu4.setMargin(new java.awt.Insets(0, 0, 0, 5));
        jMenuBar1.add(jMenu4);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jMenu3.setText("Help");

        jMenuItem1.setText("how to use");
        jMenu3.add(jMenuItem1);

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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(Info_top4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jScrollPane1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(12, 12, 12))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addComponent(lbl_rowsReturned)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void btn_newListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_newListActionPerformed
        // TODO add your handling code here:
        //String sql = "";
        
        if(rbtn_all.isSelected()){
            rbtn_specST.setEnabled(false);
            initial_table_subtypes();
            update_table_resultID();
            
        } else if (rbtn_majSub.isSelected()){
            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;
            
            String sql = "SELECT distinct t.auto_id, t.lab_id, major_subtype, spec_sub1, spec_sub2, spec_sub3 FROM sample s, patient p, subtypes t"
                + " where s.pat_id=p.pat_id"
                + " and t.lab_id=s.lab_id";
                //+ " and major_subtype like '%" + majSub +"%'";
            /*
            String sql = "SELECT distinct t.auto_id, t.lab_id, major_subtype, spec_sub1, spec_sub2, spec_sub3 FROM sample s, patient p, subtypes t"
                + " where s.pat_id=p.pat_id"
                + " and t.lab_id=s.lab_id"
                + " and major_subtype like '%" + majSub +"%'";
            if (rbtn_NOT.isSelected()) {
                sql = "SELECT distinct t.auto_id, t.lab_id, major_subtype, spec_sub1, spec_sub2, spec_sub3 FROM sample s, patient p, subtypes t"
                        + " where s.pat_id=p.pat_id"
                        + " and t.lab_id=s.lab_id"
                        + " and major_subtype NOT like '%" + majSub + "%'";
            }*/

            if (rbtn_specST.isSelected()) {
                String specST_select = CB_specST.getSelectedItem().toString();
                String specST = "";
                switch (specST_select) { 
                    case "maj. subtype":
                        specST = "major_subtype";
                        break;
                    case "spec. subt. 1":
                        specST = "spec_sub1";
                        break;
                    case "spec. subt. 2":
                        specST = "spec_sub2";
                        break;
                    case "spec. subt. 3":
                        specST = "spec_sub3";
                        break;
                    default:
                        break;
                }
                String spec_txt = txt_specST.getText();
                
                if (rbtn_NOT.isSelected()) {
                    sql = sql + " and " + specST + " NOT like '%" + spec_txt + "%'";
                } else {               
                    sql = sql + " and " + specST + " like '%" + spec_txt + "%'";
                }
            }
           
            if (rbtn_specST1.isSelected()) {
                String andor1 = CB_andor1.getSelectedItem().toString();
                String specST_select1 = CB_specST1.getSelectedItem().toString();
                String specST1 = "";
                switch (specST_select1) { 
                    case "maj. subtype":
                        specST1 = "major_subtype";
                        break;
                    case "spec. subt. 1":
                        specST1 = "spec_sub1";
                        break;
                    case "spec. subt. 2":
                        specST1 = "spec_sub2";
                        break;
                    case "spec. subt. 3":
                        specST1 = "spec_sub3";
                        break;
                    default:
                        break;
                }
                String spec_txt1 = txt_specST1.getText();
                
                if (rbtn_NOT.isSelected()) {
                    sql = sql + " " + andor1 + " " + specST1 + " NOT like '%" + spec_txt1 + "%'";
                } else {               
                    sql = sql + " " + andor1 + " " + specST1 + " like '%" + spec_txt1 + "%'";
                }
            }

            //txtArea_test.setText(sql);

            try {
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                table_subtypes.setModel(DbUtils.resultSetToTableModel(rs));
                CustomSorter.table_customRowSort(table_subtypes);

                if (table_subtypes.getColumnModel().getColumnCount() > 0) {
                    table_subtypes.getColumnModel().getColumn(0).setPreferredWidth(60);
                    table_subtypes.getColumnModel().getColumn(0).setMaxWidth(60);
                    table_subtypes.getColumnModel().getColumn(1).setPreferredWidth(80);
                    table_subtypes.getColumnModel().getColumn(1).setMaxWidth(100);
                }

                get_ids(sql, pst, rs, conn);

                update_table_resultID();

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

 
      
    }//GEN-LAST:event_btn_newListActionPerformed

    private void table_resultIDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_resultIDMouseClicked
        // TODO add your handling code here:
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_queryIDs);

            popUpResult.show(table_resultID,evt.getX(),evt.getY());
            this.outTable = table_resultID;
        }
        
    }//GEN-LAST:event_table_resultIDMouseClicked

    private void cpResultIdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpResultIdsActionPerformed

        JTable OT = this.outTable;
        String IDs = "";
        int resultL = OT.getRowCount();
        for(int i = 0; i < resultL; i++) {
            String tmp = OT.getValueAt(i, 1).toString();
            IDs = IDs + tmp + ", "; 
        }
        IDs = IDs.substring(0, (IDs.length() - 2));
        //txtArea_test.setText(IDs);  // test
        
        //Toolkit.getDefaultToolkit().getSystemClipboard().setContents( 
        //        new StringSelection(IDs), null);
        
        //StringSelection selection = new StringSelection(IDs);
        //Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //clipboard.setContents(selection, selection);
        
        StringSelection somestring = new StringSelection(IDs);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(somestring, null);
        

    }//GEN-LAST:event_cpResultIdsActionPerformed

    private void cpLabIdsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cpLabIdsActionPerformed

        JTable OT = this.outTable;
        String IDs = "";
        int resultL = OT.getRowCount();
        for(int i = 0; i < resultL; i++) {
            String tmp = OT.getValueAt(i, 2).toString();
            IDs = IDs + tmp + ", "; 
        }
        IDs = IDs.substring(0, (IDs.length() - 2));
        //txtArea_test.setText(IDs);  // test
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents( 
                new StringSelection(IDs), null);
        
    }//GEN-LAST:event_cpLabIdsActionPerformed

    private void rbtn_majSubActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_majSubActionPerformed
        if (rbtn_majSub.isSelected()){
            
            rbtn_specST.setEnabled(true);
            rbtn_NOT.setEnabled(true);
            txt_specST.setEnabled(true);
            txt_specST.setBackground(new java.awt.Color(255, 255, 255));
            CB_specST.setEnabled(true);
            
            rbtn_specST1.setEnabled(true);
            rbtn_NOT1.setEnabled(true);
            txt_specST1.setEnabled(true);
            txt_specST1.setBackground(new java.awt.Color(255, 255, 255));
            CB_specST1.setEnabled(true);
            CB_andor1.setEnabled(true);
        }
    }//GEN-LAST:event_rbtn_majSubActionPerformed

    private void rbtn_allActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_allActionPerformed
        if (rbtn_all.isSelected()){
            
            rbtn_specST.setEnabled(false);
            rbtn_NOT.setEnabled(false);
            txt_specST.setEnabled(false);
            txt_specST.setBackground(new java.awt.Color(204, 204, 204));
            CB_specST.setEnabled(false);
            
            rbtn_specST1.setEnabled(false);
            rbtn_NOT1.setEnabled(false);
            txt_specST1.setEnabled(false);
            txt_specST1.setBackground(new java.awt.Color(204, 204, 204));
            CB_specST1.setEnabled(false);
            CB_andor1.setEnabled(false);

        }
       
    }//GEN-LAST:event_rbtn_allActionPerformed

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
            java.util.logging.Logger.getLogger(SubtypesBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SubtypesBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SubtypesBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SubtypesBrowse.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
                new SubtypesBrowse().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> CB_andor1;
    private javax.swing.JComboBox<String> CB_specST;
    private javax.swing.JComboBox<String> CB_specST1;
    private javax.swing.JPanel Info_top4;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_newList;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JMenuItem cpLabIds;
    private javax.swing.JMenuItem cpResultIds;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lbl_rowsReturned;
    private javax.swing.JPopupMenu popUpResult;
    private javax.swing.JRadioButton rbtn_NOT;
    private javax.swing.JRadioButton rbtn_NOT1;
    private javax.swing.JRadioButton rbtn_all;
    private javax.swing.JRadioButton rbtn_majSub;
    private javax.swing.JRadioButton rbtn_specST;
    private javax.swing.JRadioButton rbtn_specST1;
    private javax.swing.JTable table_resultID;
    private javax.swing.JTable table_subtypes;
    private javax.swing.JTextArea txtArea_test;
    private javax.swing.JTextField txt_specST;
    private javax.swing.JTextField txt_specST1;
    // End of variables declaration//GEN-END:variables
}
