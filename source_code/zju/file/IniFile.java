/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.file;

import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class IniFile {
        private String path;
        
        public IniFile(String path) {
                this.path=path;
        }
        
        public void setItemInfo(String item,String info){
                CommonInputFile input=new CommonInputFile(path);
                CommonOutputFile output=new CommonOutputFile(path);
                String line;
                LinkedList<String> contendList=new LinkedList<>();
                while((line=input.readLine())!=null){
                        contendList.add(line.trim());
                }
                input.closeInput();
                for(String tempLine:contendList){
                        if(tempLine.startsWith(item.trim())||tempLine.replace(item, tempLine).trim().startsWith(":")){
                                output.write(item.trim()+": "+info.trim()+"\n");
                        }else{
                                output.write(tempLine+"\n");
                        }
                }
                output.closeOutput();
        }
        
        
        
}
