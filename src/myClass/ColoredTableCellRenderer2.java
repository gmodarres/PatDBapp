/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClass;

import frames.PatientBrowse;
import frames.SampleBrowse;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author gerda.modarres
 */
public class ColoredTableCellRenderer2 extends DefaultTableCellRenderer {
    private static final int VALIDATION_COLUMN = 0;   // column pat_id
    
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col){
        Component comp = super.getTableCellRendererComponent(table,  value, isSelected, hasFocus, row, col);
        String s = table.getValueAt(row, VALIDATION_COLUMN).toString();

        int rows = table.getRowCount();
        ArrayList<String> tmpList = new ArrayList();
        ArrayList<String> list = new ArrayList();
        for (int i = 0; i < rows; i++) {
            String ids = table.getValueAt(i, 0).toString();
            if (!tmpList.contains(ids)) {
                tmpList.add(ids);
            } else {
                //JOptionPane.showMessageDialog(null, list);  //TEST
                list.add(ids);
            }
        }

        if (list.contains(s)) {
            //comp.setForeground(Color.red);
            comp.setForeground(java.awt.Color.decode("#f97b04"));
        } else {
            comp.setForeground(null);
            if (isSelected) {
                comp.setForeground(Color.white);
            }
        }
        return( comp );
    }
    
    
}
