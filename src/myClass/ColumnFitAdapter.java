/*
 * source:
 *  http://www.jroller.com/santhosh/entry/fit_tablecolumns_on_demand
 */
package myClass;

import java.awt.event.*;
import javax.swing.table.*;
import java.awt.*;
import javax.swing.*;

/**
 *
 * doubleclick collumn to fit content
 */
public class ColumnFitAdapter extends MouseAdapter{ 

    public void mouseClicked(MouseEvent e){ 
        if(e.getClickCount()==2){ 
            JTableHeader header = (JTableHeader)e.getSource(); 
            TableColumn tableColumn = getResizingColumn(header, e.getPoint()); 
            if(tableColumn==null) 
                return; 
            int col = header.getColumnModel().getColumnIndex(tableColumn.getIdentifier()); 
            JTable table = header.getTable(); 
            int rowCount = table.getRowCount(); 
            int width = (int)header.getDefaultRenderer() 
                    .getTableCellRendererComponent(table, tableColumn.getIdentifier() 
                            , false, false, -1, col).getPreferredSize().getWidth(); 
            for(int row = 0; row<rowCount; row++){ 
                int preferedWidth = (int)table.getCellRenderer(row, col).getTableCellRendererComponent(table, 
                        table.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth(); 
                width = Math.max(width, preferedWidth); 
            } 
            header.setResizingColumn(tableColumn); // this line is very important 
            tableColumn.setWidth(width+table.getIntercellSpacing().width); 
        } 
    } 
 
    // copied from BasicTableHeader.MouseInputHandler.getResizingColumn 
    private TableColumn getResizingColumn(JTableHeader header, Point p){ 
        return getResizingColumn(header, p, header.columnAtPoint(p)); 
    } 
 
    // copied from BasicTableHeader.MouseInputHandler.getResizingColumn 
    private TableColumn getResizingColumn(JTableHeader header, Point p, int column){ 
        if(column==-1){ 
            return null; 
        } 
        Rectangle r = header.getHeaderRect(column); 
        r.grow(-3, 0); 
        if(r.contains(p)) 
            return null; 
        int midPoint = r.x+r.width/2; 
        int columnIndex; 
        if(header.getComponentOrientation().isLeftToRight()) 
            columnIndex = (p.x<midPoint) ? column-1 : column; 
        else 
            columnIndex = (p.x<midPoint) ? column : column-1; 
        if(columnIndex==-1) 
            return null; 
        return header.getColumnModel().getColumn(columnIndex); 
    } 
} 