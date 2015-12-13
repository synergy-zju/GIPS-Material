/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.file;

import edu.zju.common.CExecutor;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 ** @author Zhongxu Zhu
 */
public class FileFolder extends AbstractFile {
  
        
        public FileFolder(String path) {
                super(path);
        }
        
        
        
        @Override
        public void check() {
                File file= new File(this.getFilePath());
                if(!file.isDirectory()){
                        CExecutor.stopProgram(this.getFilePath()+" is not a correct file folder path, please check");//1:unnormal exit;0:normal exit
                }else this.setIsFolder(true);                
        }
        
        
        
        public AbstractFile[] getAllFile(){
                String folderpath=this.getFilePath();
                File f=new File(folderpath);
                String []temp = null;
                temp=f.list();
                int j=0;
                ArrayList list=new ArrayList();//将隐藏文件去掉，先给list，再eterator，然后给files【】
                try{
                      for(int i=0;i<temp.length;i++){
                            if(temp[i].toCharArray()[0]!='.'&&temp[i].toCharArray()[temp[i].length()-1]!='~'){   
                                  list.add(folderpath+System.getProperty("file.separator")+temp[i]);
                                  j++;
                            }
                      }
                }catch(NullPointerException e){
                      //common.CExecutor.println("文件夹中没有文件");
                }

                AbstractFile files[]=new AbstractFile[j];
                Iterator iterator=list.iterator();
                iterator.hasNext();
                for(int i=0 ;i<j;i++){
                      String pathtemp=iterator.next().toString();
                      File file=new File(pathtemp);
                      files[i]=FileFactory.getBasicFileObject(pathtemp);
                }
                return files;
//                common.CExecutor.println("在"+folderpath+"中一共有"+files.length+"个"+"文件或文件夹:");
//                for(int i=0 ;i<j;i++){
//                      common.CExecutor.println(files[i]+"      ");
//                }
      }        
        
        
        
        
}
