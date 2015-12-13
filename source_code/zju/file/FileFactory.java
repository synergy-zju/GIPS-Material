/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.file;

import edu.zju.common.CExecutor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 ** @author Zhongxu Zhu
 */
public class FileFactory {
     public static CommonInputFile getInputFile(String path,String type) {
                try {
                        File file= new File(path);
                        if(!file.isDirectory()&&!file.isFile()){
                                CExecutor.stopProgram(path+" does not exist!");
                        }
                        if(file.isDirectory()) edu.zju.common.CExecutor.println("Wrong in "+path+" becasue it is a directory, not a file");
                        CommonInputFile commonFile = null;
                     try {
                                try {
                                        commonFile=(CommonInputFile)Class.forName("edu.zju.file."+type).getConstructor(String.class).newInstance(path);
                                } catch (ClassNotFoundException ex) {
                                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                                }
                     } catch (InstantiationException ex) {
                             Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (IllegalAccessException ex) {
                             Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (IllegalArgumentException ex) {
                             Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                     } catch (InvocationTargetException ex) {
                             Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     return commonFile;
                } catch (NoSuchMethodException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
     } 
     // in fact, file exsit in resource of jar package, we can't get the file path
     // So we creat a fake inputfile through bufferedReader
     // Buf VCF class will check the file path. If path="/path/to/resource", then do not output error information
     public static CommonInputFile getInputFile(BufferedReader br,String type) {
                try {
                        CommonInputFile commonFile;
                        String fakePath=Config.getItem("FAKE_PAHT");
                        commonFile=(CommonInputFile)Class.forName("edu.zju.file."+type).getConstructor(String.class,BufferedReader.class).newInstance(fakePath,br);
                        return commonFile;
                } catch (InstantiationException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalAccessException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IllegalArgumentException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvocationTargetException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (NoSuchMethodException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SecurityException ex) {
                        Logger.getLogger(FileFactory.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
     }       
     public static CommonOutputFile getOutputFile(String path){
             CommonOutputFile commonOutputFile=new CommonOutputFile(path);
             return commonOutputFile;
     }
     
     public static AbstractFile getBasicFileObject(String path){
             File file= new File(path);
             if(!file.isDirectory()&&!file.isFile()){
                     CExecutor.stopProgram(path+" does not exist!");
             }
             if(file.isDirectory()) return new FileFolder(path);
             else if (file.isFile()) return new CommonInputFile(path);
             return null;
     }       
     
     public static void creatFile(String path) throws IOException{
             File file= new File(path);
             file.createNewFile();
     }
     public static void deleteFile(String path){
             File file= new File(path);
             file.delete();
     }
}
