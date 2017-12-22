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
package myClass;

import com.google.common.collect.ComparisonChain;
import com.mysql.jdbc.StringUtils;
import frames.SearchResult;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;



/**
 *
 * @author gerda.modarres
 */

public class CustomSorter {
    
    public static void table_customRowSort(JTable table) {
        // auto row sorter: assign collumns to sort by Integer
        
        String tableS = table.getAccessibleContext().getAccessibleName();
        //JOptionPane.showMessageDialog(null, "table:  " + tableS);  //Test
        
        Comparator intComparator = new Comparator<Integer>() {

                @Override
                public int compare(Integer arg0, Integer arg1) {
                    return arg0.intValue() - arg1.intValue();
                }
            };
        
        Comparator doubleComparator = new Comparator<Number>() {

                //@Override
                public int compare(Number arg0, Number arg1) {
                    //JOptionPane.showMessageDialog(null, "HERE!");

                    double a = arg0.doubleValue();
                    double b = arg1.doubleValue();

                    if(a<b){
                        return 1;
                    } else if (b<a){
                        return -1;
                    }else{
                        return 0;
                    }
                }
            };
        
        Comparator ChrComparator = new Comparator<String>() {

            @Override
            public int compare(String arg0, String arg1) {

                if (arg0.equals("X") || arg1.equals("X") || arg0.equals("Y") || arg1.equals("Y")) {
                    //JOptionPane.showMessageDialog(null, "NOT INT: " + arg0 + " - " + arg1);              
                    if (arg0 == null) {
                        return -1;
                    }
                    if (arg1 == null) {
                        return 1;
                    }
                    if (arg0.equals(arg1)) {
                        return 0;
                    }
                    return arg0.compareTo(arg1);

                } else {
                    int a = Integer.parseInt(arg0);
                    int b = Integer.parseInt(arg1);
                    return a - b;
                }
            }
        };
        
        Comparator IDComparator = new Comparator<String>() {

            //@Override
            public int compare(String arg0, String arg1) {
               
                String[] first = arg0.split("-");
                String[] second = arg1.split("-");
                //JOptionPane.showMessageDialog(null, "first: " + first[1] + " second: " + second[1]);

                int a = Integer.parseInt(first[0]);
                int b = Integer.parseInt(first[1]);
                int c = Integer.parseInt(second[0]);
                int d = Integer.parseInt(second[1]);
                
                //JOptionPane.showMessageDialog(null, "a: " + a + " b: " + b + " c: "+ c + " d: " + d );
                               
                return ComparisonChain.start().compare(a,c).compare(b,d).result();
            }
            

        };
        
        if (tableS.equals("table_resultID")) {
            TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            //Comparator intComparator = new Comparator<Integer>() {
            //    @Override
            //    public int compare(Integer arg0, Integer arg1) {
            //        return arg0.intValue() - arg1.intValue();
            //    }
            //};
            rowSorter.setComparator(0, intComparator);
            rowSorter.setComparator(1, intComparator);
            table.setRowSorter(rowSorter);
            
        } else if (tableS.equals("table_sample")) {
            TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            
            rowSorter.setComparator(0, intComparator);
            rowSorter.setComparator(1, intComparator);
            table.setRowSorter(rowSorter);
            
        } else if (tableS.equals("table_subtypes")) {
            TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            
            rowSorter.setComparator(0, intComparator);
            rowSorter.setComparator(1, intComparator);
            table.setRowSorter(rowSorter);
            
        } else if (tableS.equals("table_queryIDs")) {
            TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            
            rowSorter.setComparator(1, intComparator);
            rowSorter.setComparator(2, intComparator);
            table.setRowSorter(rowSorter);
            
        } else if (tableS.equals("table_array")) { 
            TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            
            rowSorter.setComparator(0, IDComparator);       //ID:  int-int
            rowSorter.setComparator(2, intComparator);
            rowSorter.setComparator(3, ChrComparator);      // chr: 1,2,3,...X,Y
            rowSorter.setComparator(5, doubleComparator);
            rowSorter.setComparator(7, doubleComparator);
            rowSorter.setComparator(8, intComparator);
            rowSorter.setComparator(9, intComparator);
            table.setRowSorter(rowSorter);
            //table.updateUI();
        } else if (tableS.equals("table_zg_iscn")) {
            TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            
            rowSorter.setComparator(0, IDComparator);        //ID:  int-int
            rowSorter.setComparator(1, intComparator);
            rowSorter.setComparator(2, intComparator);  
            rowSorter.setComparator(3, intComparator);  
            table.setRowSorter(rowSorter);
        } else if (tableS.equals("table_fish")) {
            TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            
            rowSorter.setComparator(0, IDComparator);       //ID:  int-int
            rowSorter.setComparator(1, intComparator);
            rowSorter.setComparator(2, intComparator);      // intComparator not working ...?
            rowSorter.setComparator(3, intComparator);      // intComparator not working ...?
            rowSorter.setComparator(4, doubleComparator);   // percent
            rowSorter.setComparator(6, intComparator);
            rowSorter.setComparator(8, intComparator);
            table.setRowSorter(rowSorter);
        } else if (tableS.equals("table_patient")) {
            TableRowSorter<DefaultTableModel> rowSorter = (TableRowSorter<DefaultTableModel>) table.getRowSorter();
            
            rowSorter.setComparator(0, intComparator);       //
            rowSorter.setComparator(1, intComparator);
            table.setRowSorter(rowSorter);
        }               
    }
    
}
