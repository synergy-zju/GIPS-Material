package edu.zju.common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * 
 * @author Zhongxu Zhu
 */
public class FileHandler {

        public BufferedReader br;
        public BufferedWriter bw;
        public String files[];

        public void readFile(String filepath) {//建立输入流
                try {
                        br = new BufferedReader(new FileReader(filepath));
                } catch (FileNotFoundException ex) {
                        edu.zju.common.CExecutor.println(filepath + " doesn't exit or path is not correct ");
                }

        }

        public void writeFile(String filepath) { //建立输出流    
                try {
                        bw = new BufferedWriter(new FileWriter(new File(filepath)));
                } catch (IOException ex) {
                        Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        public void writeNewLine() {
                try {
                        this.bw.write('\n');
                } catch (IOException ex) {
                        Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        public void writeLineSplitting() {
                try {
                        this.bw.write("    -----    ------    \n");
                } catch (IOException ex) {
                        Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        public void appendToFile(String filePath) {
                try {
                        bw = new BufferedWriter(new FileWriter(new File(filePath), true));
                } catch (IOException ex) {
                        Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
        }

        private void getAllFile(String folderpath) {//参数是文件夹的路径,读取所有非隐藏文件的文件名
                File f = new File(folderpath);
                String[] temp = null;
                temp = f.list();
                int j = 0;
                ArrayList list = new ArrayList();//将隐藏文件去掉，先给list，再eterator，然后给files【】
                try {
                        for (int i = 0; i < temp.length; i++) {
                                if (temp[i].toCharArray()[0] != '.' && temp[i].toCharArray()[temp[i].length() - 1] != '~') {
                                        list.add(folderpath + '/' + temp[i]);
                                        j++;
                                }
                        }
                } catch (NullPointerException e) {
                        edu.zju.common.CExecutor.println("文件夹中没有文件");
                }

                files = new String[j];
                Iterator iterator = list.iterator();
                iterator.hasNext();
                for (int i = 0; i < j; i++) {
                        files[i] = iterator.next().toString();
                }
        }

        public void deleteFile(String filePath) {
                File file = new File(filePath);
                file.delete();
        }

        public void createFile(String pathName) {
                try {
                        new File(pathName).createNewFile();
                } catch (IOException ex) {
                        Logger.getLogger(FileHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        /**
         * Copy file
         * @param input file to copy
         * @param output 
         */
        public void fileChannelCopy(File input, File output) {
                FileInputStream fi = null;
                FileOutputStream fo = null;
                FileChannel in = null;
                FileChannel out = null;
                try {
                    fi = new FileInputStream(input);
                    fo = new FileOutputStream(output);
                    in = fi.getChannel();//得到对应的文件通道
                    out = fo.getChannel();//得到对应的文件通道
                    in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fi.close();
                        in.close();
                        fo.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }        
        
}
