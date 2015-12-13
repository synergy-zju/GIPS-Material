/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 ** @author Zhongxu Zhu
 */
public class LogFile extends CommonOutputFile{
        
        public LogFile(String path) {
                super(path);
        }   
        
        public synchronized void write(String info) {
               File file= new File(this.getFilePath());
               if(!file.isFile()&&!file.isDirectory()){
                        try {
                                file.createNewFile();
                        } catch (IOException ex) {
                                Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, ex);
                        }
               }
               try {
                       this.bw= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
               } catch (FileNotFoundException ex) {
                       Logger.getLogger(LogFile.class.getName()).log(Level.SEVERE, null, ex);
               }
               try {
                        this.bw.write(info);
                        this.bw.flush();
                        this.bw.close();
               } catch (IOException ex) {
                       Logger.getLogger(CommonOutputFile.class.getName()).log(Level.SEVERE, null, ex);
               }
        }        
        
        
        
}
