/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myClass;

import frames.SearchResult;
import java.awt.Color;
import java.awt.Component;
//import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


/**
 *
 * @author gerda.modarres
 */
public class ColoredTableCellRenderer extends DefaultTableCellRenderer {
  private static final int VALIDATION_COLUMN = 5;   // column ISCN
  String customTextColor = SearchResult.customColor;
  
  public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col){
      Component comp = super.getTableCellRendererComponent(table,  value, isSelected, hasFocus, row, col);
      //String s =  table.getModel().getValueAt(row, VALIDATION_COLUMN ).toString();  // NONO: hier bleibt colored cell an selber stelle, wenn zeilen neu sortiert werden
      String s =  table.getValueAt(row, VALIDATION_COLUMN ).toString();

      if (customTextColor.length() > 0) {
          if (s.contains("COR:")) {
              comp.setForeground(Color.red);
          } else if (s.contains(customTextColor)) {
              comp.setForeground(Color.BLUE);
              if (isSelected) {
                  comp.setForeground(Color.cyan);
              }
          } else {
              comp.setForeground(null);
              if (isSelected) {
                  comp.setForeground(Color.white);
              }
          }
      } else {
          if (s.contains("COR:")) {
              comp.setForeground(Color.red);
          } else {
              comp.setForeground(null);
              if (isSelected) {
                  comp.setForeground(Color.white);
              }
          }
      }

       return( comp );
   } 
  
}
