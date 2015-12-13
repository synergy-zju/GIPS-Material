/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 ** @author Zhongxu Zhu
 */
public class CommonOutputFile extends AbstractFile{
        protected BufferedWriter bw ;
        private boolean isWriting=false;

        public CommonOutputFile(String path) {
                super(path);
        }
        public synchronized void write(String info) {
               if(isWriting) try {
                        this.bw.write(info);
                        this.bw.flush();
                } catch (IOException ex) {
                        Logger.getLogger(CommonOutputFile.class.getName()).log(Level.SEVERE, null, ex);
                }
               else {
                        try {
                                this.bw= new BufferedWriter(new FileWriter(new File(this.getFilePath())));
                                this.isWriting=true;
                                this.bw.write(info);
                                this.bw.flush();
                        } catch (IOException ex) {
                                Logger.getLogger(CommonOutputFile.class.getName()).log(Level.SEVERE, null, ex);
                        }
               } 
        }
        public synchronized void closeOutput() {
                try {
                        this.isWriting=false;
                        try {
                             this.bw.flush();      
                             this.bw.close();                             
                        } catch (java.lang.NullPointerException e) {
                        }
                } catch (IOException ex) {
                        Logger.getLogger(CommonOutputFile.class.getName()).log(Level.SEVERE, null, ex);
                }
        }        
        @Override
        protected void check() {
        }
}
