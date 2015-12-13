/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.file;

import edu.zju.common.CExecutor;
import java.io.File;
import java.util.HashMap;

/**
 *
 ** @author Zhongxu Zhu
 */
public class Config {
        public static final String path=new CExecutor().getGIPSDirectoy()+System.getProperty("file.separator")+"config";
        private static final CommonInputFile inputFile=new CommonInputFile(path);
        
        public static String getItem(String item){
                if(!new File(path).isFile()){
                        CExecutor.stopProgram("Error: Do not find config file in /path/to/GIPS_folder/");
                }
                if(!inputFile.isFile()){
                        edu.zju.common.CExecutor.stopProgram("GIPS config file can't find. Please check whether GIPS.jar and config are in the same directory");
                }
                String line;
                while((line=inputFile.readLine())!=null){
                        if(line.startsWith("#")) continue;
                        if(line.trim().startsWith(item)) {
                                inputFile.closeInput();                        
                                return line.split(":")[1].toString().trim();
                        }
                        else continue;
                }
                inputFile.closeInput(); 
                edu.zju.common.CExecutor.println("Do not find "+item +" in config");
                return null;
        }
        public static HashMap<String,Integer> getMatrix(String matrixName){
              HashMap<String,Integer> matrix = new HashMap<>();
              String line;
              while((line=inputFile.readLine())!=null){
                     if(line.trim().equals("@ "+matrixName)){
                              boolean flag=true;
                              String []aminoAcids = null;
                              while((line=inputFile.readLine()).trim().length()!=0&&line.toCharArray()[0]!='@'){
                                      if(line.toCharArray()[0]=='#')
                                          continue;
                                      line=line.replace("       ", "\t");
                                      line=line.replace("      ", "\t");
                                      line=line.replace("     ", "\t");
                                      line=line.replace("    ", "\t");
                                      line=line.replace("   ", "\t");
                                      line=line.replace("  ", "\t");
                                      line=line.replace(" ", "\t");
                                      if(flag){
                                           aminoAcids=line.split("\t");
                                           flag=false;
                                           continue;
                                      }
                                      String lineSplit[]=line.split("\t");
                                      for(int i=1;i<lineSplit.length;i++){
                                             String tempAA=aminoAcids[i]+lineSplit[0];
                                             int score=Integer.parseInt(lineSplit[i].trim().toString());
                                             matrix.put(tempAA, score);       
                                      }     
                                }  
                                break;
                     }
                     else continue;
              }
              if(matrix.size()==0){
                        matrix=null;
                        edu.zju.common.CExecutor.stopProgram("Don't find "+matrixName);
              }
              inputFile.closeInput();
              return matrix;
        }
}
