package edu.zju.parameter;

import edu.zju.file.CommonInputFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class ParameterLoader {
        private SampleParameterBag sampleParameterBag;
        private GlobalParameter globalParameter;
        public static LinkedList<String> protocalString;
        
        public GlobalParameter loadGlobalParameter(String path){
                CommonInputFile parameterFile=new CommonInputFile (path);
                String line;
                HashMap<String,String> itemMapInfo=new HashMap<>();
                while((line=parameterFile.readLine())!=null){
                        line=line.trim();
                        if(line.startsWith("#")||line.trim().isEmpty())continue;
                        if(line.trim().contains("[SAMPLE]")) break;
                        String info = null,item=null;
                        if(line.contains(":")){
                                item=line.split(":")[0].trim();
                        }else {
                                continue;
                        }
                        if(line.contains("#")){
                                line=line.split("#")[0].trim();
                        }
                        if(line.split(":").length==2){
                                 info=line.split(":")[1].trim();
                                 itemMapInfo.put(item, info);
                        }
                         
                }
                this.globalParameter=new GlobalParameter(itemMapInfo);
                return this.globalParameter;
        }        
        
        public SampleParameterBag loadSampleSpecificParameter(String path){
                CommonInputFile parameterFile= new CommonInputFile(path);
                SampleParameterBag sampleParameterBag=new SampleParameterBag();
                String line;
                HashMap<String,String> itemMapInfo=new HashMap<>();
                HashSet<String> sampleNames=new HashSet<>();
                LinkedList<String> sampleNamelist=new LinkedList<>();
                while((line=parameterFile.readLine())!=null){
                        line=line.trim();
                        if(line.startsWith("#")||line.trim().isEmpty())continue;
                        if(line.trim().contains("[SAMPLE_LIST]"))
                                break;
                }
                while((line=parameterFile.readLine())!=null){
                        line=line.trim();
                        if(line.startsWith("#")||line.trim().isEmpty())continue;
                        if(line.trim().contains("[SAMPLE]")||line.trim().contains("[GIPS"))
                                break;
                        sampleNamelist.add(line.trim());
                        if(!sampleNames.add(line.trim())){
                            edu.zju.common.CExecutor.stopProgram("Repeat sample name "+line);    
                        }
                }
                if(sampleNamelist.size()==0){
                        return null;
                }else{//please note, setSampleNumber function is just invoked once in GIPS process
                        SampleParameterBag.setSampleNumber(sampleNamelist.size());
                }
                //set sample number in global parameter
                GlobalParameter.setSampleNumber(sampleNames.size());
                String item = null,info=null;
                while((line=parameterFile.readLine())!=null){
                        if(line.startsWith("#")||line.trim().isEmpty()){
                                continue;
                        }
                        if(line.contains(":")){
                                item=line.split(":")[0].trim();
                        }else if(line.trim().contains("[SAMPLE]")){
                                SampleParameter sampleParameter=new SampleParameter(globalParameter, itemMapInfo);
                                sampleParameterBag.addSampleParameter(sampleParameter);
                                itemMapInfo=new HashMap<>();
                                continue;
                        }else  {
                                continue;
                        }
                        if(line.contains("#")){
                                line=line.split("#")[0].trim();
                        }
                        if(line.split(":").length==2){
                                 info=line.split(":")[1].trim();
                                 itemMapInfo.put(item, info);
                                 if(item.equals("SAMPLE_NAME")){
                                        if(!sampleNames.contains(info)){
                                                edu.zju.common.CExecutor.stopProgram("SAMPLE_NAME "+ info+" should listed in [SMAPLE_LIST] section fisrt" );
                                        }else{
                                                sampleNames.remove(info);
                                        }    
                                 }
                        }
                }
                SampleParameter sampleParameter=new SampleParameter(globalParameter, itemMapInfo);
                sampleParameterBag.addSampleParameter(sampleParameter);
                itemMapInfo=new HashMap<>();
                if(sampleNames.size()!=0){
                        edu.zju.common.CExecutor.stopProgram("Do not find "+sampleNames+" in [SAMPLE_LIST]");
                }
                sampleParameterBag.setSampleNamesList(sampleNamelist);
                return this.sampleParameterBag=sampleParameterBag;
        }

}
