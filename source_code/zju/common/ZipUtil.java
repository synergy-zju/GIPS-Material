/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 *
 ** @author Zhongxu Zhu
 */
public class ZipUtil {
        
        
        /**
         * 
         * @param zipFile File format is zip. This file is uncompressed.
         * @param descDir Target directory that the file in zip will be released to.
         * @throws ZipException
         * @throws IOException 
         */
        public void unzipFiles(File zipFile,String descDir) throws ZipException, IOException{
                if(descDir.endsWith(edu.zju.common.CExecutor.getFileSeparator())){
                        descDir=descDir.substring(0,descDir.length()-1);
                }
                File targetFolder=new File(descDir);
                if(!targetFolder.exists()){
                        targetFolder.mkdirs();
                }
                ZipFile zip=new ZipFile(zipFile);
                for(Enumeration entries=zip.entries();entries.hasMoreElements();){
                        ZipEntry entry=(ZipEntry) entries.nextElement();
                        String entryName=entry.getName();
                        String targetPath=descDir+edu.zju.common.CExecutor.getFileSeparator()+entryName;
                        File targetFile = new File(targetPath);
                        if(targetFile.getName().startsWith(".")||targetFile.getPath().contains("__MACOSX"))continue;
                        if(entry.isDirectory()){
                                targetFile.mkdirs();continue;
                        }else{
                                if(!new File(targetFile.getParent()).exists()){
                                        new File(targetFile.getParent()).mkdirs();
                                }
                        }
                        OutputStream out = new FileOutputStream(targetFile);
                        byte[] buffer =new byte[1024];
                        int len;
                        InputStream in =zip.getInputStream(entry);
                        while((len=in.read(buffer))>0){
                                out.write(buffer, 0, len);
                        }
                        in.close();
                        out.flush();
                        out.close();
                }
        }

        
}
