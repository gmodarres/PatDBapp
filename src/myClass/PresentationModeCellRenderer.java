/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClass;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author gerda.modarres
 */
public class PresentationModeCellRenderer extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col){
      Component comp = super.getTableCellRendererComponent(table,  value, isSelected, hasFocus, row, col);
        String s = table.getValueAt(row, col).toString();

        if (col == 3 || col == 4) {
            comp.setForeground(Color.white);
        }else {
            comp.setForeground(null);
        }

        return (comp);
    }

}


  //              DefaultTableCellRenderer ren = new PresentationModeCellRenderer();  //MOD TEST
  //              table_queryIDs.setDefaultRenderer(Object.class , ren);
                

