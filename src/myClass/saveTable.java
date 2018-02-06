/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClass;

import java.awt.Component;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

/**
 *
 * @author gerda.modarres
 */
public class saveTable {
    
    static Log my_log;
    
    public static void toExcel(JTable table, File file) {
        //https://sites.google.com/site/teachmemrxymon/java/export-records-from-jtable-to-ms-excel

        try {
            TableModel model = table.getModel();
            FileWriter excel = new FileWriter(file);

            for (int i = 0; i < model.getColumnCount(); i++) {
                excel.write(model.getColumnName(i) + "\t");
            }
            excel.write("\n");

            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    //excel.write(model.getValueAt(i,j).toString()+"\t");

                    Object value = model.getValueAt(i, j);

                    if (value == null || value.toString().isEmpty()) {
                        //JOptionPane.showMessageDialog(null, "NULL "+value);
                        //value = "";
                        excel.write("\t");
                    } else {
                        excel.write(value + "\t");
                    }
                }
                excel.write("\n");
            }
            excel.close();

        } catch (IOException e) {
            System.out.println(e);
        } catch (Exception e) {
            //JOptionPane.showMessageDialog(null, "saveTable() error");
            //JOptionPane.showMessageDialog(null, e.getStackTrace());
        }
    }
    
    public static void saveOnRC(JTable OT, String dp, Component parent){
        try {
            //JOptionPane.showMessageDialog(null, "right click");
            //JTable OT = this.outTable;
            //String dp = this.defaultPath;
            //JOptionPane.showMessageDialog(null, "dp:  "+dp); // TEST

            JFileChooser fileChooser = new JFileChooser(dp);    
            fileChooser.setFileFilter(new FileNameExtensionFilter(".tsv","tsv"));
            
            if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
                File out_file = fileChooser.getSelectedFile();
                String filename = fileChooser.getSelectedFile().toString();
                if (!filename.endsWith(".tsv")){
                    out_file = new File(out_file + ".tsv");
                }
                // save to file    
                //toExcel(OT, out_file);
                toExcel(OT, out_file);

            } else {
                //JOptionPane.showMessageDialog(null, "cancel");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "SSomething went wrong saving your stuff ... " + e.getMessage());
            my_log.logger.warning("ERROR:  Something went wrong saving your stuff ...  " + e);
        }
    
    
    }
    
}
