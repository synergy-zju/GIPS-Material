/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.file;

import edu.zju.common.CExecutor;

/**
 *
 ** @author Zhongxu Zhu
 */
public abstract class AbstractFile {
     private String fileName;
     private String filePath;
     private boolean isFolder;
     private String parentPath;
        public AbstractFile(String path) {
                path=path.trim();
                String temp[]=null;
                temp=path.split(CExecutor.getFileSeparator());
                this.setFilePath(path);
                this.setFileName(temp[temp.length-1]);
                this.setParentPath(path.replace(this.fileName, ""));
                this.check();
        }
     private void setParentPath(String path){
             this.parentPath=path;
     }
     protected void setFileName(String name){
             this.fileName=name;
     }  
     protected void setFilePath(String path){
             this.filePath=path;
     }
     protected abstract void check();
     public String getFilePath(){
             return this.filePath;
     }
     public String getFileName(){
             return this.fileName;
     }
     protected void setIsFolder(boolean is){
             this.isFolder=is;
     }
     public boolean isFolder(){
             return isFolder;
     }
     public String getParentPath(){
             return this.parentPath;
     }
     public boolean isFile(){
             return new java.io.File(this.filePath).isFile();
     }
}
