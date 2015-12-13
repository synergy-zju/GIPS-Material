package edu.zju.file;

import edu.zju.common.CExecutor;
import edu.zju.common.StringSecurity;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 ** @author Zhongxu Zhu
 */
public class CommonInputFile extends AbstractFile {

        private BufferedReader br ;
        private boolean isReading=false;
        private boolean isFromBufferedReader=false;
        
        public CommonInputFile(String path) {
                super(path.trim());
        }
        /**
         * For we have got bufferedReader instance, so there is not need to know file path
         * @param fakePath List in GIPS config file
         * @param br 
         */
        public CommonInputFile(String fakePath,BufferedReader br){
                super(fakePath);
                this.isFromBufferedReader=true;
                this.br=br;
        }
        
        @Override
        protected void check() {
                File file= new File(this.getFilePath());
                if(file.isDirectory()||!file.isFile()){
                        if(file.getPath().equals(Config.getItem("FAKE_PAHT"))){
                        }else{
                                CExecutor.stopProgram(this.getFilePath()+" is not a correct path, please check");
                        }//unnormal exit
                }else this.setIsFolder(false);
        }

        public synchronized String readLine() {
                if(isReading) try {
                        return this.br.readLine();
                } catch (IOException ex) {
                        Logger.getLogger(CommonInputFile.class.getName()).log(Level.SEVERE, null, ex);
                }else{
                        try {
                                if(isFromBufferedReader){
                                        this.br.mark(0);
                                }else{
                                        this.br=new BufferedReader(new FileReader(new File(this.getFilePath())));
                                }
                                this.isReading=true;
                                return this.br.readLine();
                        } catch (java.io.FileNotFoundException ex1){
                                edu.zju.common.CExecutor.stopProgram(this.getFilePath()+" (No such file)");
                        } catch (IOException ex) {
                                Logger.getLogger(CommonInputFile.class.getName()).log(Level.SEVERE, null, ex);
                        }
                }
                return null;
        }
        public synchronized void closeInput() {
                try {   if(this.isFromBufferedReader){}
                        else{
                                this.br.close();
                        }
                } catch (IOException ex) {
                        Logger.getLogger(CommonInputFile.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.isReading=false;
        }
        protected boolean isFromBufferedReader(){
                return this.isFromBufferedReader;
        }
        public String getFileMD5(){
                return new StringSecurity().getFileSha1(new File(this.getFilePath()));
        }

}
