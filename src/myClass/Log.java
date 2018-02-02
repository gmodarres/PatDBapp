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

import frames.SetConnection;
import static frames.SetConnection.personalConfig;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
//import java.util.logging.SimpleFormatter;
import javax.swing.JOptionPane;
import org.ini4j.Ini;

/**
 * source: 
 * https://stackoverflow.com/questions/15758685/how-to-write-logs-in-text-file-when-using-java-util-logging-logger
 * modified @author gerda.modarres
 */
public class Log {
    //public Logger logger;
    public static Logger logger;
    FileHandler fh;
    
    public Log() throws SecurityException, IOException{
        SimpleDateFormat format = new SimpleDateFormat("yyyy_M_d");  // "M-d_HHmmss"
        String currentUser = SetConnection.currentUser;
        
        try {
            if (OSDetector.isWindows()) {  
                File f = new File("LOG\\MyLogFile_" + format.format(Calendar.getInstance().getTime()) + currentUser + ".log");
                if(!f.exists()){
                    f.createNewFile();
                }
            } else {
                File f = new File("LOG/MyLogFile_" + format.format(Calendar.getInstance().getTime()) + currentUser + ".log");
                if(!f.exists()){
                    f.createNewFile();
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        } finally{
            //fh.close();
        }
        
         try {
            if (OSDetector.isWindows()) { 
                //fh = new FileHandler(("LOG\\MyLogFile_" + format.format(Calendar.getInstance().getTime()) + currentUser + ".log"),true);
                fh = new FileHandler(("LOG\\MyLogFile_" + format.format(Calendar.getInstance().getTime()) + currentUser + ".log"),1048576,1,true);
            } else {
                fh = new FileHandler(("LOG/MyLogFile_" + format.format(Calendar.getInstance().getTime()) + currentUser + ".log"),1048576,1,true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
        
        logger = Logger.getLogger("logTest");
        logger.addHandler(fh);
        //SimpleFormatter formatter = new SimpleFormatter();
        //fh.setFormatter(formatter);
        // option: 
        ///*
        fh.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                SimpleDateFormat logTime = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                Calendar cal = new GregorianCalendar();
                cal.setTimeInMillis(record.getMillis());
                return record.getLevel()
                        + "\t||\t"
                        + logTime.format(cal.getTime())
                        + "\t||\t"
                        + record.getSourceClassName().substring(
                                record.getSourceClassName().lastIndexOf(".") + 1,
                                record.getSourceClassName().length())
                        + "."
                        + record.getSourceMethodName()
                        + "()\t||\t"
                        + record.getMessage() + "\n";
            }
        });
        //*/
        
        logger.setUseParentHandlers(false);     // no console output
    }
    
    public static void startLog(Log my_log, String info){
        Ini ini;
        try{
            //toggle for Testing
            //ini = new Ini(new File("config.ini"));        //TEST
            ini = new Ini(new File(personalConfig));        //toggle 1/1
            String OnOff = ini.get("logger","level");
            //JOptionPane.showMessageDialog(null, OnOff);
            my_log = new Log();
             
            my_log.logger.setLevel(Level.parse(OnOff));
            //my_log.logger.setLevel(Level.OFF);
            
            my_log.logger.info(info);
            //my_log.logger.warning("Warning msg");
            //my_log.logger.severe("Severe msg");
            
        //}catch(Exception e){
        }catch(IOException | IllegalArgumentException | SecurityException e){
            JOptionPane.showMessageDialog(null, e);
        } 
        //catch (IOException ex) {
        //    Logger.getLogger(SetConnection.class.getName()).log(Level.SEVERE, null, ex);
        //}
    }
    
}



       