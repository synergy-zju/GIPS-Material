package edu.zju.common;

import edu.zju.file.LogFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 ** @author Zhongxu Zhu
 *  Because JAVA has a class name CExecutor, so this class renamed CExecutor
 */

public class CExecutor {

        private String resultInf;
        private String erroInf;
        private String scriptPath;
        private static String currentDirectory;
        private static long processStartTime=0;
                
        public CExecutor() {
                // Get current address and initiate a script for execute
                this.scriptPath = getTerminalDirectoy()+getFileSeparator() + ".script.sh";
        }

        /**
         * Linux command use line break '\n' for more command This command is
         * writed into a .sh file ,then run this shell and delete after running.
         * So this file should be synchronized, only one command can be cunning
         * at the same time.
         *
         * @param command Linux 
         */
        public void execute(String command) {
                this.writeCommandIntoScript(command);
                this.executeCommand();
                new FileHandler().deleteFile(this.scriptPath);
        }
        
        
        private void executeCommand() {
                try {
                        Process ps = null;
                        //file has a function named set executable . see 
                        Runtime.getRuntime().exec("chmod 711 " + this.scriptPath);
                        ps = Runtime.getRuntime().exec(this.scriptPath);
                        StreamGobbler errorGobbler=new StreamGobbler(ps.getErrorStream(),"ERROR");
                        StreamGobbler outGobbler=new StreamGobbler(ps.getInputStream(), "STDOUT");
                        errorGobbler.start();
                        outGobbler.start();
                        //int waitFor() the exit value of the process. By convention, 0 indicates normal termination
                        int status=ps.waitFor();
                        if(status!=0){
                                
                        }
                } catch (InterruptedException ex) {
                        Logger.getLogger(CExecutor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                        //Logger.getLogger(CExecutor.class.getName()).log(Level.SEVERE, null, ex);
                        this.executeCommand();
                }
        }

        private void writeCommandIntoScript(String command) {
                try {
                        FileHandler fileHandler = new FileHandler();
                        fileHandler.writeFile(this.scriptPath);
                        fileHandler.bw.write(command);
                        fileHandler.bw.flush();
                        fileHandler.bw.close();
                } catch (IOException ex) {
                        Logger.getLogger(CExecutor.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        /**
         * check whether the script is running successfully
         * @return true: success; false: unsuccessful
         */
        private boolean isSucceed() {
                if (this.getErroInformation().isEmpty()) {
                        return true;
                } else {
                        return false;
                }
        }

        public String getErroInformation() {
                return this.erroInf;
        }

        public static String getFileSeparator(){
                return System.getProperty("file.separator");
        }
        public static String getTerminalDirectoy() {
                if (currentDirectory == null) {
                        File directory= new File("");
                        currentDirectory = directory.getAbsolutePath();
                }
                return currentDirectory;

        }
        /**
         * only used to find config file path and log file path
        */
        public String getGIPSDirectoy() {
                String path=new ZipUtil().getClass().getProtectionDomain().getCodeSource().getLocation().toString();
                //if in IDE show /path/to/NetBeansProjects/GIPS/build/classes/
                path=path.replace("file:", "").replace("/build/classes/", "");
                //if in jar package, show /path/to/GIPS.jar
                path=path.replace("/GIPS.jar", "");
                return path;

        }        
        public static String getRunningTime() {
                if(processStartTime==0){
                        processStartTime=System.currentTimeMillis();
                        println("\nJob start time: "+getCurrentTime());
                }
                return new String((new SimpleDateFormat("HH:mm:ss.SS")).format(System.currentTimeMillis()-processStartTime-28800000)+"\t");
        }
        public static String getCurrentTime(){
                return new String((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(System.currentTimeMillis()));
        }
        public static void stopProgram(String info){
                println(getRunningTime()+info+"\n");
                System.exit(1);
        }
        public static void println(String content) {
                System.out.println(content);
                LogFile log = new LogFile(new CExecutor().getGIPSDirectoy() + System.getProperty("file.separator") + "log.gips");
                log.write(content + "\n");
        }
        public static void print(String content){
                System.out.print(content);
                LogFile log = new LogFile(new CExecutor().getGIPSDirectoy() + System.getProperty("file.separator") + "log.gips");
                log.write(content);
        }
        
        

        /**
         * 用于处理Runtime.getRuntime().exec产生的错误流及输出流
         * @author shaojing
         *
         */
        private class StreamGobbler extends Thread {
                InputStream is;
                String type;


            StreamGobbler(InputStream is, String type) {
                this.is = is;
                this.type = type;
            }

            public void run() {
                StringBuffer sb=new StringBuffer();
                InputStreamReader isr = null;
                BufferedReader br = null;
                try {
                    isr = new InputStreamReader(is);
                    br = new BufferedReader(isr);
                    String line=null;
                    while ( (line = br.readLine()) != null) {
                        //Following line determine wether to print in terminal
                        //System.out.println(type + ">" + line);
                        sb.append(line+"\n");
                    }
                    if(this.type.equals("ERROR")){
                            erroInf=sb.toString().trim();
                    }
                    if(this.type.equals("STDOUT")){
                            resultInf=sb.toString().trim();
                    }
                } catch (IOException ioe) {
                    ioe.printStackTrace();  
                } finally{
                            try {
                                    br.close();
                                    isr.close();
                            } catch (IOException ex) {
                                    Logger.getLogger(CExecutor.class.getName()).log(Level.SEVERE, null, ex);
                            }
                }
            }
        } 

        
        
        
        
        
        
        
        
}
