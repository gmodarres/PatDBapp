/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frames;

import static frames.ResultWindow.updateIntrpr;
import static frames.SetConnection.personalConfig;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ToolTipManager;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import myClass.CustomSorter;
import myClass.DBconnect;
import myClass.Log;
import myClass.OSDetector;
import net.proteanit.sql.DbUtils;
import org.ini4j.Ini;

/**
 *
 * @author gerda.modarres
 */
public class ArrayQuery extends javax.swing.JFrame {

    String ids = null;
    String mod_sql = null;
    
    String query_labIDs = null;
    String lab_ids = null;
    
    JTable outTable = null;  
    String defaultPath = null;

    static DefaultTableModel moveTableModel = null;
    static String source = null;        // Name for header in FreeTable
    static String tableMoving = null;   // Name of table that is moved to FreeTable, to set row sorter
    
    static String resultMoving = null;  // TEST 
    //static String resultIDMoving = null;
    static boolean IntrprWindowIsOpen = false;
    
    String click_result = null;
    static String click_lID = null;
    
    Log my_log;
    
    /**
     * Creates new form ArrayQuery
     */
    public ArrayQuery() {
        initComponents();
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/LIRA_small.png"));
        //ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/patIGUS.png"));
        this.setIconImage(img.getImage());
        getIniData();
        initial_table_statistics();
        initial_table_queryIDs();
        jPanel1.getRootPane().setDefaultButton(btn_Search);
        
        ToolTipManager.sharedInstance().setEnabled(false);
    }

    public void getIniData() {
        Ini ini;
        try {
            //ini = new Ini(new File("config.ini")); // TEST
            ini = new Ini(new File(personalConfig));  // toggle 1/1
            //ini = new Ini(new File("C:\\Users\\gerda.modarres\\Desktop\\pat_DB\\config.ini"));
            String dp = ini.get("defaultpath", "path");
            this.defaultPath = dp;
        } catch (IOException ex) {
            Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static boolean isRightClick(MouseEvent e) {
        return (e.getButton()==MouseEvent.BUTTON3 ||
            (System.getProperty("os.name").contains("Mac OS X") &&
                    (e.getModifiers() & InputEvent.BUTTON1_MASK) != 0 &&
                    (e.getModifiers() & InputEvent.CTRL_MASK) != 0));
    }
    
    private void saveOnRC(java.awt.event.ActionEvent evt) {
        try {
            //JOptionPane.showMessageDialog(null, "right click");
            JTable OT = this.outTable;
            String dp = this.defaultPath.toString();
            //JOptionPane.showMessageDialog(null, "dp:  "+dp);

            JFileChooser fileChooser = new JFileChooser(dp);
            if (fileChooser.showSaveDialog(Info_top) == JFileChooser.APPROVE_OPTION) { // ? panel ... check
                File out_file = fileChooser.getSelectedFile();

                // save to file                   
                toExcel(OT, out_file);
            } else {
                //JOptionPane.showMessageDialog(null, "cancel
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "SSomething went wrong saving your stuff ... " + e.getMessage());
            my_log.logger.warning("ERROR:  Something went wrong saving your stuff ...  " + e);
        }
    }
    
    public void toExcel(JTable table, File file){
    //https://sites.google.com/site/teachmemrxymon/java/export-records-from-jtable-to-ms-excel
    
    try{
        TableModel model = table.getModel();
        FileWriter excel = new FileWriter(file);
        
        for(int i = 0; i < model.getColumnCount(); i++){
            excel.write(model.getColumnName(i) + "\t");
        }
        excel.write("\n");
               
        for(int i=0; i< model.getRowCount(); i++) {
            for(int j=0; j < model.getColumnCount(); j++) {
                //excel.write(model.getValueAt(i,j).toString()+"\t");

                Object value = model.getValueAt(i,j);

                if(value == null || value.toString().isEmpty()){ 
                    //JOptionPane.showMessageDialog(null, "NULL "+value);
                    //value = "";
                    excel.write("\t");
                }else{
                    excel.write(value+"\t"); 
                    
                }
            }
            excel.write("\n");

        }
        excel.close();

    }catch(IOException e){ 
        System.out.println(e); 
    }catch(Exception e){
        //JOptionPane.showMessageDialog(null, "toExcel() error");
        //JOptionPane.showMessageDialog(null, e.getStackTrace());
    }
}  
    
    private void highlight_gene(String gene, Integer no){
        try{
            String text  = txtArea_genes.getText().toLowerCase();
            String findWord = gene.toLowerCase();
            int index = text.indexOf(findWord);
            
            Highlighter highlighter = txtArea_genes.getHighlighter();
            Highlighter.HighlightPainter painter = null;
            if (no==1){
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.yellow);
            } else if (no==2){
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.cyan);
            } else if (no==3){
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.orange);
            } else if (no==4){ 
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.pink);
            } else if (no==5){ 
                painter = new DefaultHighlighter.DefaultHighlightPainter(java.awt.Color.green);
            }
            while (index >= 0) {  // indexOf returns -1 if no match found
                //JOptionPane.showMessageDialog(null,"find: "+findWord+" index: "+ index);
                int p0 = index;
                int p1 = p0 + findWord.length();
                highlighter.addHighlight(p0, p1, painter);
                index = text.indexOf(findWord, index + 1);
            }   
        }catch(Exception e){
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
    
    private void get_statistics(String sql){
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try{
            // from get_ids()  ... 1,2,3,4,5,  --> get rid of "," at the end:
            String ids = this.ids;
            if (ids.length() > 1) {
                ids = ids.substring(0, (ids.length() - 1));
            }else{
                //JOptionPane.showMessageDialog(null, "no IDs");
                ids = "0";
            }
            //String sql2 = "SELECT COUNT(*) AS matchingCriteria FROM( "+ sql + " ) AS M";
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
    
    private void initial_table_queryIDs(){
    
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        String sql = "SELECT distinct s.lab_id, result_id, fname, surname, sex, b_date from main_result m, patient p, sample s "
                        + "Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id";
        //String sql = "SELECT distinct s.lab_id, result_id, fname, surname, sex, b_date, proj_id as P"
        //        + " from main_result m, patient p, sample s, pat_inproject ip"
        //        + " Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND ip.pat_id=p.pat_id";
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
                //String sql2 = "SELECT distinct s.lab_id, result_id, fname, surname, sex, b_date, proj_id as P"
                //+ " from main_result m, patient p, sample s, pat_inproject ip"
                //+ " Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND ip.pat_id=p.pat_id AND result_id IN ( " + all_ids + " )";
                
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
                    //table_queryIDs.getColumnModel().getColumn(3).setPreferredWidth(80);
                    //table_queryIDs.getColumnModel().getColumn(3).setMaxWidth(130);
                    table_queryIDs.getColumnModel().getColumn(5).setPreferredWidth(30);
                    table_queryIDs.getColumnModel().getColumn(5).setMaxWidth(30);
                    table_queryIDs.getColumnModel().getColumn(6).setPreferredWidth(90);      // 80
                    table_queryIDs.getColumnModel().getColumn(6).setMaxWidth(120);           // 80
                    //table_queryIDs.getColumnModel().getColumn(7).setPreferredWidth(30);
                    //table_queryIDs.getColumnModel().getColumn(7).setMaxWidth(30);
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
                //String sql2 = "SELECT distinct s.lab_id, result_id, fname, surname, sex, b_date, proj_id as P"
                //+ " from main_result m, patient p, sample s, pat_inproject ip"
                //+ " Where p.pat_id=s.pat_id AND m.lab_id=s.lab_id AND ip.pat_id=p.pat_id AND result_id IN ( " + all_ids + " )";
                
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
                    //table_queryIDs.getColumnModel().getColumn(3).setPreferredWidth(80);
                    //table_queryIDs.getColumnModel().getColumn(3).setMaxWidth(130);
                    table_queryIDs.getColumnModel().getColumn(5).setPreferredWidth(30);
                    table_queryIDs.getColumnModel().getColumn(5).setMaxWidth(30);
                    table_queryIDs.getColumnModel().getColumn(6).setPreferredWidth(90);      // 80
                    table_queryIDs.getColumnModel().getColumn(6).setMaxWidth(120);           // 80
                    //table_queryIDs.getColumnModel().getColumn(7).setPreferredWidth(30);
                    //table_queryIDs.getColumnModel().getColumn(7).setMaxWidth(30);
                }
            }
            //get ids of sql query to count patients affected in get_statistics()
            get_ids(sql, pst, rs, conn);

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
    
    private void get_ids(String sql, PreparedStatement pst, ResultSet rs, Connection conn) {
        
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            String all_ids= "";
            String id_rem = "";
            
            while (rs.next()) {
                //this.rs_sizeList.add(rs.getString("array_sub_id"));
                String id = rs.getString("result_id");
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
            JOptionPane.showMessageDialog(null, e.getMessage());
            my_log.logger.warning(e.toString() + "\n\t\t\t\t\t\tERROR-SOURCE-SQL: "+sql);
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
       
    // For Testing
    private void display_ids(){
        if (this.ids.length() >0){
            String display_ids=this.ids.substring(0, (ids.length() - 1));  // HERE!
            //txtArea_test.setText(display_ids);
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

        popUpSave = new javax.swing.JPopupMenu();
        popUpMenu_save = new javax.swing.JMenuItem();
        popUpMenu_selectAll = new javax.swing.JMenuItem();
        popUpMenu_moveTbl = new javax.swing.JMenuItem();
        jToolBar1 = new javax.swing.JToolBar();
        bnt_test = new javax.swing.JButton();
        Info_top = new javax.swing.JPanel();
        jScrollPane7 = new javax.swing.JScrollPane();
        table_statistics = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        txtArea_Creg = new javax.swing.JTextArea();
        lab_Genes = new javax.swing.JLabel();
        lab_Creg = new javax.swing.JLabel();
        jScrollPane10 = new javax.swing.JScrollPane();
        table_queryIDs = new javax.swing.JTable();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtArea_genes = new javax.swing.JTextArea();
        jPanel17 = new javax.swing.JPanel();
        btn_TTT = new javax.swing.JRadioButton();
        rbtn_exportIDs = new javax.swing.JRadioButton();
        rbtn_showExample = new javax.swing.JRadioButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        table_array = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        btn_Search = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jCombo_gene1const = new javax.swing.JComboBox<>();
        txt_genes1 = new javax.swing.JTextField();
        txt_gene1const = new javax.swing.JTextField();
        txt_gene2const = new javax.swing.JTextField();
        jCombo_gene2const = new javax.swing.JComboBox<>();
        lbl_Genes = new javax.swing.JLabel();
        txt_const1_1 = new javax.swing.JTextField();
        lbl_mainConst = new javax.swing.JLabel();
        rbtn_const1 = new javax.swing.JRadioButton();
        lbl_or1 = new javax.swing.JLabel();
        jCombo_const1 = new javax.swing.JComboBox<>();
        txt_genes2 = new javax.swing.JTextField();
        txt_const1_2 = new javax.swing.JTextField();
        lbl_or3 = new javax.swing.JLabel();
        rbtn_const2 = new javax.swing.JRadioButton();
        jCombo_const2 = new javax.swing.JComboBox<>();
        txt_const2_1 = new javax.swing.JTextField();
        txt_const2_2 = new javax.swing.JTextField();
        txt_genes3 = new javax.swing.JTextField();
        jCombo_gene3const = new javax.swing.JComboBox<>();
        txt_gene3const = new javax.swing.JTextField();
        txt_genes4 = new javax.swing.JTextField();
        jCombo_gene4const = new javax.swing.JComboBox<>();
        txt_gene4const = new javax.swing.JTextField();
        txt_genes5 = new javax.swing.JTextField();
        lbl_Genes2 = new javax.swing.JLabel();
        lbl_or2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1_openModel = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenu3 = new javax.swing.JMenu();
        jMenuItem1_HowTo = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2_Info = new javax.swing.JMenuItem();

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

        popUpMenu_moveTbl.setText("move table ...");
        popUpMenu_moveTbl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popUpMenu_moveTblActionPerformed(evt);
            }
        });
        popUpSave.add(popUpMenu_moveTbl);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        Info_top.setBackground(new java.awt.Color(102, 153, 255));
        Info_top.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        Info_top.setRequestFocusEnabled(false);

        jScrollPane7.setBackground(new java.awt.Color(0, 204, 204));
        jScrollPane7.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane7.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        table_statistics.setBackground(new java.awt.Color(161, 211, 238));
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
        table_statistics.setToolTipText("statistics");
        table_statistics.setRowHeight(20);
        jScrollPane7.setViewportView(table_statistics);

        jScrollPane8.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtArea_Creg.setColumns(20);
        txtArea_Creg.setLineWrap(true);
        txtArea_Creg.setRows(4);
        txtArea_Creg.setWrapStyleWord(true);
        jScrollPane8.setViewportView(txtArea_Creg);

        lab_Genes.setText("Genes");

        lab_Creg.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        lab_Creg.setText("cyto regions");

        table_queryIDs.setAutoCreateRowSorter(true);
        table_queryIDs.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "lab_id", "result_ID", "name", "surname", "sex", "b_date"
            }
        ));
        table_queryIDs.setToolTipText("patients");
        table_queryIDs.setName(""); // NOI18N
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

        jScrollPane5.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        txtArea_genes.setColumns(20);
        txtArea_genes.setLineWrap(true);
        txtArea_genes.setRows(5);
        txtArea_genes.setWrapStyleWord(true);
        txtArea_genes.setName(""); // NOI18N
        jScrollPane5.setViewportView(txtArea_genes);

        jPanel17.setBackground(new java.awt.Color(102, 153, 255));
        jPanel17.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btn_TTT.setText("ToolTip Help on");
        btn_TTT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_TTTActionPerformed(evt);
            }
        });

        rbtn_exportIDs.setText("export result_IDs");

        rbtn_showExample.setText("show example");
        rbtn_showExample.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rbtn_showExampleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel17Layout = new javax.swing.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtn_exportIDs, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btn_TTT, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rbtn_showExample, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel17Layout.setVerticalGroup(
            jPanel17Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel17Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(rbtn_exportIDs, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btn_TTT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(rbtn_showExample)
                .addContainerGap())
        );

        javax.swing.GroupLayout Info_topLayout = new javax.swing.GroupLayout(Info_top);
        Info_top.setLayout(Info_topLayout);
        Info_topLayout.setHorizontalGroup(
            Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_topLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 541, Short.MAX_VALUE)
                    .addComponent(jScrollPane7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lab_Genes)
                    .addComponent(lab_Creg, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addComponent(jScrollPane8))
                .addContainerGap())
        );
        Info_topLayout.setVerticalGroup(
            Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Info_topLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(Info_topLayout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(Info_topLayout.createSequentialGroup()
                        .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab_Genes)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(Info_topLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lab_Creg)
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 82, Short.MAX_VALUE)))
                    .addComponent(jPanel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(10, 10, 10))
        );

        table_array.setAutoCreateRowSorter(true);
        table_array.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7", "Title 8", "Title 9", "Title 10", "Title 11", "Title 12"
            }
        ));
        table_array.setToolTipText("array result");
        table_array.setCellSelectionEnabled(true);
        table_array.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                table_arrayMouseClicked(evt);
            }
        });
        table_array.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                table_arrayKeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(table_array);
        table_array.getAccessibleContext().setAccessibleName("table_array");

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        btn_Search.setText("Search");
        btn_Search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btn_SearchActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(204, 204, 204));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jCombo_gene1const.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "type", "cnst", "size", "call", "chr" }));

        jCombo_gene2const.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "type", "cnst", "size", "call", "chr" }));

        lbl_Genes.setText("Genes:");

        txt_const1_1.setToolTipText("<html><p width=400px>\nenter constraints here - possible values:<br>\n<font color=\"green\">\n= 'text' &#9&#9 ...is text<br>\n= no. &#9&#9 ...is number<br>\n&lt&gt 'text' &#9&#9 ...is not text<br> \n&lt&gt no. &#9&#9 ...is not a number<br>\nlike '%text%' &#9&#9 ...does contain text<br>\nnot like '%text%' &#9 ...does not contain text<br>\nin (no1,no2,...) &#9 ...does contain numbers<br>\nnot in (no1,no2,...) &#9 ...does not contain numbers<br>\n</font></p></html>");

        lbl_mainConst.setText("main constraints:");

        rbtn_const1.setText("1");
        rbtn_const1.setToolTipText("select, if you want to add a constraint to the query");

        lbl_or1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_or1.setText("OR");

        jCombo_const1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "type", "cnst", "size", "call", "chr" }));
        jCombo_const1.setToolTipText("choose feature");

        txt_const1_2.setToolTipText("enter a second constraint value for a feature here");

        lbl_or3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_or3.setText("AND");

        rbtn_const2.setText("2");

        jCombo_const2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "type", "cnst", "size", "call", "chr" }));

        jCombo_gene3const.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "type", "cnst", "size", "call", "chr" }));

        jCombo_gene4const.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "type", "cnst", "size", "call", "chr" }));

        lbl_Genes2.setText("(show gene, without constraints)");

        lbl_or2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl_or2.setText("OR");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_mainConst, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(rbtn_const1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbl_or3, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_or1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCombo_const1, 0, 103, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_const1_2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_const1_1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbtn_const2)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lbl_or2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jCombo_const2, 0, 103, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txt_const2_1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_const2_2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(txt_genes3, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCombo_gene3const, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_gene3const, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txt_genes5, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_genes4, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jCombo_gene4const, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txt_gene4const, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(lbl_Genes2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(lbl_Genes, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txt_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCombo_gene1const, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_gene1const, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addGap(62, 62, 62)
                                .addComponent(txt_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCombo_gene2const, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txt_gene2const, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_const1_1, txt_const1_2, txt_const2_2});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {txt_const2_1, txt_genes2});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(3, 3, 3)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lbl_Genes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txt_genes1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCombo_gene1const, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_gene1const, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCombo_gene2const, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_gene2const, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_genes2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCombo_gene3const, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_gene3const, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_genes3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCombo_gene4const, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_gene4const, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txt_genes4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(3, 3, 3)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txt_genes5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl_Genes2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rbtn_const1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(rbtn_const2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lbl_or3)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(lbl_mainConst)
                                    .addComponent(jCombo_const1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txt_const1_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(3, 3, 3)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txt_const1_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lbl_or1)
                                    .addComponent(lbl_or2)
                                    .addComponent(txt_const2_2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jCombo_const2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txt_const2_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(47, 47, 47))))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {lbl_mainConst, lbl_or1});

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 136, Short.MAX_VALUE)
                .addComponent(btn_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(21, 21, 21))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addComponent(btn_Search, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10))
        );

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

        jMenuItem1_HowTo.setText("how to use");
        jMenuItem1_HowTo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1_HowToActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1_HowTo);

        jMenuItem1.setText("query cheat sheet");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem1);

        jMenuItem2_Info.setText("Info");
        jMenuItem2_Info.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2_InfoActionPerformed(evt);
            }
        });
        jMenu3.add(jMenuItem2_Info);

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
                    .addComponent(jScrollPane1)
                    .addComponent(Info_top, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Info_top, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void bnt_testActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bnt_testActionPerformed
        // TODO add your handling code here:
        // Testbutton in toolbar
    }//GEN-LAST:event_bnt_testActionPerformed

    private void table_queryIDsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_queryIDsMouseClicked
        // right klick ==> save to file

        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_queryIDs);

            popUpSave.show(table_queryIDs, evt.getX(), evt.getY());
            this.outTable = table_queryIDs;

        } else {

        }
    }//GEN-LAST:event_table_queryIDsMouseClicked

    private void table_queryIDsKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_queryIDsKeyReleased
        // code from  table_queryIDsMouseClicked()
        try {

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally {
            try {

            } catch (Exception e) {
            }
        }

    }//GEN-LAST:event_table_queryIDsKeyReleased

    private void jMenuItem2_InfoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2_InfoActionPerformed
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/EsALiR_suite_BG_ico2-3_small.png"));
        JOptionPane.showMessageDialog(rootPane, "Needs A Name \nDB-request Tool\nVersion:   1.0.0", "Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem2_InfoActionPerformed

    private void jMenuItem1_HowToActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1_HowToActionPerformed
        ImageIcon img = new javax.swing.ImageIcon(getClass().getResource("/ico/Monsters-Snail-icon.png"));
        JOptionPane.showMessageDialog(rootPane, "... ummmmmm \n... errrrrr \n... pls ask again later", "apparently no useful Info", HEIGHT,img);
    }//GEN-LAST:event_jMenuItem1_HowToActionPerformed

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

    private void popUpMenu_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_saveActionPerformed

        saveOnRC(evt);
        //this.dispose();
    }//GEN-LAST:event_popUpMenu_saveActionPerformed

    private void popUpMenu_selectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_selectAllActionPerformed

        JTable OT = this.outTable;
        OT.selectAll();
    }//GEN-LAST:event_popUpMenu_selectAllActionPerformed

    private void popUpMenu_moveTblActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_popUpMenu_moveTblActionPerformed

        JTable OT = this.outTable;
        source = OT.getAccessibleContext().getAccessibleDescription();
        tableMoving = OT.getAccessibleContext().getAccessibleName();
        moveTableModel = (DefaultTableModel) OT.getModel();
        new FreeTable().setVisible(true);

    }//GEN-LAST:event_popUpMenu_moveTblActionPerformed

    private void table_arrayMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_table_arrayMouseClicked

        // right click  ==> save to file
        if (isRightClick(evt) == true) {
            //JOptionPane.showMessageDialog(null, "right click");
            //saveOnRC(evt, table_array);
            popUpSave.show(table_array,evt.getX(),evt.getY());
            this.outTable = table_array;

        } else {

            Connection conn = DBconnect.ConnecrDb();
            ResultSet rs = null;
            PreparedStatement pst = null;

            try {
                int row = table_array.getSelectedRow();
                //String Table_click = (table_array.getModel().getValueAt(row, 0).toString());        // values not correct anymore, if auto table rowsorter is used -->
                String Table_click = (table_array.getValueAt(row, 0).toString());
                click_result = (table_array.getValueAt(row, 1).toString());
                click_lID = Table_click;

                //String sql = "SELECT * FROM fish_result r, fish_probe p, main_result m WHERE r.probe_no=p.probe_no AND m.result_id=r.result_id AND fish_sub_id='" + Table_click + "' ";
                String sql = "SELECT * FROM arr_result a, main_result m WHERE a.result_id=m.result_id AND array_sub_id='" + Table_click + "' ";
                pst = conn.prepareStatement(sql);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String add1 = rs.getString("array_sub_id");
                    //A_txt_array_sub_id.setText(add1);
                    String add2 = rs.getString("result_id");
                    //A_txt_result_id.setText(add2);
                    String add3 = rs.getString("lab_id");
                    //A_txt_lab_id.setText(add3);
                    String add4 = rs.getString("genes");
                    txtArea_genes.setText(add4);

                    // IF interpretation window is open
                    if(IntrprWindowIsOpen  == true){
                        updateIntrpr(add2);     // update text in Window "ResultWindow"
                    }

                    // highlight searched genes in text area
                    if(txt_genes1 != null && !txt_genes1.getText().isEmpty()){
                        String gen1 = txt_genes1.getText();
                        highlight_gene(gen1,1);
                    }
                    if(txt_genes2 != null && !txt_genes2.getText().isEmpty()){  
                        String gen2 = txt_genes2.getText();
                        highlight_gene(gen2,2);
                    }
                    if(txt_genes3 != null && !txt_genes3.getText().isEmpty()){
                        String gen3 = txt_genes3.getText();
                        highlight_gene(gen3,3);
                    }
                    if(txt_genes4 != null && !txt_genes4.getText().isEmpty()){
                        String gen4 = txt_genes4.getText();
                        highlight_gene(gen4,4);
                    }
                    if(txt_genes5 != null && !txt_genes5.getText().isEmpty()){
                        String gen5 = txt_genes5.getText();
                        highlight_gene(gen5,5);
                    }

                    String add5 = rs.getString("cyto_regions");
                    txtArea_Creg.setText(add5);

                    //String add6 = rs.getString("full_loc");
                    //txt_fullLoc.setText(add6);

                    //txtArea_sql.setText(sql);
                    //get_queryLabIDs(sql, pst, rs, conn);
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

            // right click
            if (isRightClick(evt) == true) {
                JOptionPane.showMessageDialog(null, "right click");

            }
        }
    }//GEN-LAST:event_table_arrayMouseClicked

    private void table_arrayKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_table_arrayKeyReleased
        // same code as in table_arrayMouseClicked (without right-klick detection)
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        try{
            // TODO enter code from mouseClicked
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
    }//GEN-LAST:event_table_arrayKeyReleased

    private void btn_SearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_SearchActionPerformed
        // TODO add your handling code here:
        Connection conn = DBconnect.ConnecrDb();
        ResultSet rs = null;
        PreparedStatement pst = null;
        
        try {
          
            String sql = "SELECT array_sub_id as ID, ma_nom, result_id, chr, arr_type as type, cnst, arr_call, size, loc_start, loc_end, cyto_regions AS cyto_regions, genes FROM arr_result a where (1=1";
            //array_sub_id as ID, ma_nom, result_id, chr, arr_type as type, cnst, arr_call, size, loc_start, loc_end, cyto_regions AS cyto_regions, genes
            // GENES ... used twice!
            String gen1=""; String gen2=""; String gen3=""; String gen4=""; String gen5="";
         
            if(txt_genes1 != null && !txt_genes1.getText().isEmpty()){
                gen1 = txt_genes1.getText();
                sql = sql + " AND a.genes like '%"+ gen1 +"%'";
            } else{
                JOptionPane.showMessageDialog(null, "enter at least one gene name");
            }
            // gene 5 is only shown (no constraints)
            if(txt_genes5 != null && !txt_genes5.getText().isEmpty()){
                gen5 = txt_genes5.getText();
                sql = sql + " OR a.genes like '%"+ gen5 +"%'";
            }
            ///////////////////////////////////////////////////////////
            if(txt_genes2 != null && !txt_genes2.getText().isEmpty()){
                gen2 = txt_genes2.getText();
                sql = sql + " OR a.genes like '%"+ gen2 +"%'";
            }else{
                sql = sql +")\n";
            }
            if(txt_genes3 != null && !txt_genes3.getText().isEmpty()){
                gen3 = txt_genes3.getText();
                sql = sql + " OR a.genes like '%"+ gen3 +"%'";
            }else{
                if(txt_genes2 != null && !txt_genes2.getText().isEmpty()){
                    sql = sql +")\n";
                }
            }
            if(txt_genes4 != null && !txt_genes4.getText().isEmpty()){
                gen4 = txt_genes4.getText();
                sql = sql + " OR a.genes like '%"+ gen4 +"%')";
            }else{
                if(txt_genes3 != null && !txt_genes3.getText().isEmpty()){
                    sql = sql +")\n";
                }
            }
            /*if(txt_genes5 != null && !txt_genes5.getText().isEmpty()){
                gen5 = txt_genes5.getText();
                sql = sql + " OR a.genes like '%"+ gen5 +"%')\n";
            }else{
                if(txt_genes4 != null && !txt_genes4.getText().isEmpty()){
                    sql = sql +")\n";
                }
            }*/
            
            // constraints 1
            String const1_1=""; String const1_2=""; String const2_1=""; String const2_2="";
            if (rbtn_const1.isSelected()) {
                String C1_select = jCombo_const1.getSelectedItem().toString();
                String C1 = "";
                
                if(txt_const1_1 != null && !txt_const1_1.getText().isEmpty()){

                    const1_1 = txt_const1_1.getText(); 
                    
                    switch (C1_select) {
                    case "type":
                        C1 = "a.arr_type";
                        break;
                    case "cnst":
                        C1 = "a.cnst";
                        break;
                    case "size":
                        C1 = "a.size";
                        break;
                    case "call":
                        C1 = "a.arr_call";
                        break;
                    case "chr":
                        C1 = "a.chr";
                        break;    
                    default:
                        break;
                    }
                    
                    sql = sql + " AND (" + C1 + " " + const1_1  ;
                }
                if(txt_const1_2 != null && !txt_const1_2.getText().isEmpty()){
                    const1_2 = txt_const1_2.getText();
                    sql = sql + " OR "+ C1 + " " + const1_2 + ")\n";

                }else{
                    sql = sql + ")\n";
                }
            }
            
            // constraints 2
            if (rbtn_const2.isSelected()) {
                String C2_select = jCombo_const2.getSelectedItem().toString();
                String C2 = "";
                
                if(txt_const2_1 != null && !txt_const2_1.getText().isEmpty()){
                    const2_1=txt_const2_1.getText();
                    
                    switch (C2_select) {
                    case "type":
                        C2 = "a.arr_type";
                        break;
                    case "cnst":
                        C2 = "a.cnst";
                        break;
                    case "size":
                        C2 = "a.size";
                        break;
                    case "call":
                        C2 = "a.arr_call";
                        break;
                    case "chr":
                        C2 = "a.chr";
                        break;    
                    default:
                        break;
                    }
                    
                    sql = sql + " AND (" + C2 + " " + const2_1  ;
                }
                if(txt_const2_2 != null && !txt_const2_2.getText().isEmpty()){
                    const2_2=txt_const2_2.getText();
                    sql = sql + " OR "+ C2 + " " + const2_2 + ")\n";
                }else{
                    sql = sql + ")\n";
                }
            }
            
            // at lest one gene has to be set --> sql continues like this:
            sql = sql + " AND a.result_id in \n(SELECT result_id from arr_result b WHERE";

            if(txt_gene1const != null && !txt_gene1const.getText().isEmpty()){
                String gen1C = txt_gene1const.getText();
                String gen1C_select = jCombo_gene1const.getSelectedItem().toString();
                String g1C="";
                
                switch (gen1C_select) {
                    case "type":
                        g1C = "b.arr_type";
                        break;
                    case "cnst":
                        g1C = "b.cnst";
                        break;
                    case "size":
                        g1C = "b.size";
                        break;
                    case "call":
                        g1C = "b.arr_call";
                        break;
                    case "chr":
                        g1C = "b.chr";
                        break;    
                    default:
                        break;
                    }

                sql = sql + " " + g1C + " " + gen1C + " AND b.genes like '%" + gen1 + "%')\n";

            }else{
                sql = sql + " b.genes like '%" + gen1 + "%')\n" ;
            }

            // need to check, if a 2nd, 3rd or 4th gene has been entered ...
            if (txt_genes2 != null && !txt_genes2.getText().isEmpty()) {
                sql = sql + " AND a.result_id in \n(SELECT result_id from arr_result b WHERE";

                if (txt_gene2const != null && !txt_gene2const.getText().isEmpty()) {
                    String gen2C = txt_gene2const.getText();
                    String gen2C_select = jCombo_gene2const.getSelectedItem().toString();
                    String g2C = "";

                    switch (gen2C_select) {
                        case "type":
                            g2C = "b.arr_type";
                            break;
                        case "cnst":
                            g2C = "b.cnst";
                            break;
                        case "size":
                            g2C = "b.size";
                            break;
                        case "call":
                            g2C = "b.arr_call";
                            break;
                        case "chr":
                            g2C = "b.chr";
                            break;
                        default:
                            break;
                    }

                    sql = sql + " " + g2C + " " + gen2C + " AND b.genes like '%" + gen2 + "%')\n";
                } else {
                    sql = sql + " b.genes like '%" + gen2 + "%')\n";
                }
            }
            
            if (txt_genes3 != null && !txt_genes3.getText().isEmpty()) {
                sql = sql + " AND a.result_id in \n(SELECT result_id from arr_result b WHERE";

                if (txt_gene3const != null && !txt_gene3const.getText().isEmpty()) {
                    String gen3C = txt_gene3const.getText();
                    String gen3C_select = jCombo_gene3const.getSelectedItem().toString();
                    String g3C = "";

                    switch (gen3C_select) {
                        case "type":
                            g3C = "b.arr_type";
                            break;
                        case "cnst":
                            g3C = "b.cnst";
                            break;
                        case "size":
                            g3C = "b.size";
                            break;
                        case "call":
                            g3C = "b.arr_call";
                            break;
                        case "chr":
                            g3C = "b.chr";
                            break;
                        default:
                            break;
                    }

                    sql = sql + " " + g3C + " " + gen3C + " AND b.genes like '%" + gen3 + "%')\n";
                } else {
                    sql = sql + " b.genes like '%" + gen3 + "%')\n";
                }
            }
            
            if (txt_genes4 != null && !txt_genes4.getText().isEmpty()) {
                sql = sql + " AND a.result_id in \n(SELECT result_id from arr_result b WHERE";

                if (txt_gene4const != null && !txt_gene4const.getText().isEmpty()) {
                    String gen4C = txt_gene4const.getText();
                    String gen4C_select = jCombo_gene4const.getSelectedItem().toString();
                    String g4C = "";

                    switch (gen4C_select) {
                        case "type":
                            g4C = "b.arr_type";
                            break;
                        case "cnst":
                            g4C = "b.cnst";
                            break;
                        case "size":
                            g4C = "b.size";
                            break;
                        case "call":
                            g4C = "b.arr_call";
                            break;
                        case "chr":
                            g4C = "b.chr";
                            break;
                        default:
                            break;
                    }

                    sql = sql + " " + g4C + " " + gen4C + " AND b.genes like '%" + gen4 + "%')\n";
                } else {
                    sql = sql + " b.genes like '%" + gen4 + "%')\n";
                }
            }

            txtArea_genes.setText(sql); // TEST    
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            
            get_ids(sql, pst, rs, conn);
            display_ids();
            
            table_array.setModel(DbUtils.resultSetToTableModel(rs));
            CustomSorter.table_customRowSort(table_array);   // not working ?
            // resize column width
            //jScrollPane1.setViewportView(table_array);

            if (table_array.getColumnModel().getColumnCount() > 0) {
                table_array.getColumnModel().getColumn(0).setPreferredWidth(65);    // 50
                table_array.getColumnModel().getColumn(0).setMaxWidth(65);          // 50
                table_array.getColumnModel().getColumn(1).setPreferredWidth(300);   // ma_nom
                table_array.getColumnModel().getColumn(1).setMaxWidth(600);         // ma_nom

                table_array.getColumnModel().getColumn(2).setPreferredWidth(60);
                table_array.getColumnModel().getColumn(2).setMaxWidth(60);
                table_array.getColumnModel().getColumn(3).setPreferredWidth(55); // chr
                table_array.getColumnModel().getColumn(3).setMaxWidth(55);
                table_array.getColumnModel().getColumn(4).setPreferredWidth(55); // type
                table_array.getColumnModel().getColumn(4).setMaxWidth(100);
                table_array.getColumnModel().getColumn(5).setPreferredWidth(60);
                table_array.getColumnModel().getColumn(5).setMaxWidth(60);
                table_array.getColumnModel().getColumn(6).setPreferredWidth(110);
                table_array.getColumnModel().getColumn(6).setMaxWidth(200);
                table_array.getColumnModel().getColumn(7).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(7).setMaxWidth(100);
                table_array.getColumnModel().getColumn(8).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(8).setMaxWidth(100);
                table_array.getColumnModel().getColumn(9).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(9).setMaxWidth(100);
                table_array.getColumnModel().getColumn(10).setPreferredWidth(100);
                table_array.getColumnModel().getColumn(10).setMaxWidth(200);
            }

            get_statistics(sql);
            get_queryLabIDs(sql, pst, rs, conn);
            
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

    }//GEN-LAST:event_btn_SearchActionPerformed

    private void btn_TTTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btn_TTTActionPerformed
        // TODO add your handling code here:
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

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        ArrayQueryCheatSheet s = new ArrayQueryCheatSheet();
        s.setVisible(true); 
  
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void rbtn_showExampleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rbtn_showExampleActionPerformed
        // TODO add your handling code here:
        if (rbtn_showExample.isSelected()){
            txt_const1_1.setToolTipText("<html><p width=400px>\npossible values:<br>\n<font color=\"green\">\n= 'loss'<br>\n= 7<br>\n&lt&gt 'LOH'<br> \n&lt&gt 22<br>\nlike '%mosaic%'<br>\nnot like '%gain%'<br>\nin (7,9,21)<br>\nnot in (1,2) &#9<br>\n</font></p></html>");
        }else{
            txt_const1_1.setToolTipText("<html><p width=400px>\nenter constraints here - possible values:<br>\n<font color=\"green\">\n= 'text' &#9&#9 ...is text<br>\n= no. &#9&#9 ...is number<br>\n&lt&gt 'text' &#9&#9 ...is not text<br> \n&lt&gt no. &#9&#9 ...is not a number<br>\nlike '%text%' &#9&#9 ...does contain text<br>\nnot like '%text%' &#9 ...does not contain text<br>\nin (no1,no2,...) &#9 ...does contain numbers<br>\nnot in (no1,no2,...) &#9 ...does not contain numbers<br>\n</font></p></html>");
        }
    }//GEN-LAST:event_rbtn_showExampleActionPerformed

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
            java.util.logging.Logger.getLogger(ArrayQuery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ArrayQuery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ArrayQuery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ArrayQuery.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ArrayQuery().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel Info_top;
    private javax.swing.JButton bnt_test;
    private javax.swing.JButton btn_Search;
    private javax.swing.JRadioButton btn_TTT;
    private javax.swing.JComboBox<String> jCombo_const1;
    private javax.swing.JComboBox<String> jCombo_const2;
    private javax.swing.JComboBox<String> jCombo_gene1const;
    private javax.swing.JComboBox<String> jCombo_gene2const;
    private javax.swing.JComboBox<String> jCombo_gene3const;
    private javax.swing.JComboBox<String> jCombo_gene4const;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem1_HowTo;
    private javax.swing.JMenuItem jMenuItem1_openModel;
    private javax.swing.JMenuItem jMenuItem2_Info;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lab_Creg;
    private javax.swing.JLabel lab_Genes;
    private javax.swing.JLabel lbl_Genes;
    private javax.swing.JLabel lbl_Genes2;
    private javax.swing.JLabel lbl_mainConst;
    private javax.swing.JLabel lbl_or1;
    private javax.swing.JLabel lbl_or2;
    private javax.swing.JLabel lbl_or3;
    private javax.swing.JMenuItem popUpMenu_moveTbl;
    private javax.swing.JMenuItem popUpMenu_save;
    private javax.swing.JMenuItem popUpMenu_selectAll;
    private javax.swing.JPopupMenu popUpSave;
    private javax.swing.JRadioButton rbtn_const1;
    private javax.swing.JRadioButton rbtn_const2;
    private javax.swing.JRadioButton rbtn_exportIDs;
    private javax.swing.JRadioButton rbtn_showExample;
    private javax.swing.JTable table_array;
    private javax.swing.JTable table_queryIDs;
    private javax.swing.JTable table_statistics;
    private javax.swing.JTextArea txtArea_Creg;
    private javax.swing.JTextArea txtArea_genes;
    private javax.swing.JTextField txt_const1_1;
    private javax.swing.JTextField txt_const1_2;
    private javax.swing.JTextField txt_const2_1;
    private javax.swing.JTextField txt_const2_2;
    private javax.swing.JTextField txt_gene1const;
    private javax.swing.JTextField txt_gene2const;
    private javax.swing.JTextField txt_gene3const;
    private javax.swing.JTextField txt_gene4const;
    private javax.swing.JTextField txt_genes1;
    private javax.swing.JTextField txt_genes2;
    private javax.swing.JTextField txt_genes3;
    private javax.swing.JTextField txt_genes4;
    private javax.swing.JTextField txt_genes5;
    // End of variables declaration//GEN-END:variables
}
