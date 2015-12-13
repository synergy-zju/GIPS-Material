package edu.zju.parameter;

import edu.zju.common.StringSecurity;
import edu.zju.file.CommonInputFile;
import edu.zju.file.Config;
import edu.zju.options.Init;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

/**
 *
 ** @author Zhongxu Zhu
 */
public class GIPSJob {
        private String jobType;
        private GlobalParameter globalParameter;
        private SampleParameterBag sampleParameterBag;
        private String md5;
        private boolean update=false;
        
        
        
        public void setJobType(String type){
                this.jobType=type;
        }
        
        
        public void setGlobalParameter(GlobalParameter globalParameter){
               this.globalParameter=globalParameter; 
        }
        public void setSampleParameterBag(SampleParameterBag sampleParameterBag) throws IOException{
                this.sampleParameterBag=sampleParameterBag;
                if(this.update){
                        this.updateSampleForIterating(Init.getWorkingDirectroy()+edu.zju.common.CExecutor.getFileSeparator()+Config.getItem("RESULT_GIPS_FILE"));
                        this.update=false;
                }
        }
        public GlobalParameter getGlobalParameter(){
                return this.globalParameter;
        }
        public SampleParameterBag getSampleParameterBag(){
                return this.sampleParameterBag;
        }
        public String getJobType(){
                if(jobType==null){
                        return jobType=GlobalParameter.getToolType();
                }
                return this.jobType;
        }
        
        public String getMD5(){
                if(this.md5==null){
                        StringSecurity stringSecurity=new StringSecurity();
                        this.md5=stringSecurity.string2MD5(String.valueOf(this.sampleParameterBag)+String.valueOf(globalParameter)+edu.zju.common.CExecutor.getCurrentTime());
                }
                return this.md5;
        }
        
        private void updateSampleForIterating(String filePath) throws IOException{
                CommonInputFile gipsResultFile=new CommonInputFile(filePath);
                String line;
                StringBuffer sb=new StringBuffer();
                while((line=gipsResultFile.readLine())!=null){
                        if(line.contains("[GIPS TRACEBACK]")){
                                while((line=gipsResultFile.readLine())!=null){
                                        if(line.startsWith("#"))continue;
                                        if(line.contains("[END GIPS TRACEBACK]")) break;
                                        sb.append(line+"\n");
                                }
                        }
                }
                StringSecurity ss=new StringSecurity();
                String data=sb.toString();
                data=data.substring(0,data.length()-1);            
                data=ss.gunzip(data);
                //
                StringReader sr=new StringReader(data);
                BufferedReader br=new BufferedReader(sr);
                SampleParameterBag sampleParameterBag=new SampleParameterBag();
                HashMap<String,String> itemMapInfo=new HashMap<>();
                while((line=br.readLine())!=null){
                        line=line.trim();
                        if(line.startsWith("#")||line.trim().isEmpty())continue;
                        if(line.trim().contains("[SAMPLE]"))
                                break;
                }
                String item = null,info=null;
                while((line=br.readLine())!=null){
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
                        }
                }
                SampleParameter sampleParameter=new SampleParameter(globalParameter, itemMapInfo);
                sampleParameterBag.addSampleParameter(sampleParameter);
                itemMapInfo=new HashMap<>();
                this.sampleParameterBag=this.updateParameterCheck(this.sampleParameterBag, sampleParameterBag);
        }   
        
        private SampleParameterBag updateParameterCheck(SampleParameterBag current,SampleParameterBag traceback){
                for(String sample:current.getSamplesNamesList()){
                        //whether sam vcf script be maed changes
                        boolean b1=true,b2=true,b3=true;
                        SampleParameter s1=current.getSample(sample);
                        SampleParameter s2=traceback.getSample(sample);
                        try {
                                b1=s1.getVCFMd5().equals(s2.getVCFMd5());
//                                System.out.println(s1.getVCFMd5());
//                                System.out.println(s2.getVCFMd5());
                        } catch (Exception e) {
                        }
                        try {
                                b2=s1.getSAMMd5().equals(s2.getSAMMd5());
                        } catch (Exception e) {
                        }
                        try {
                                b3=s1.getScriptMd5().equals(s2.getScriptMd5());
                        } catch (Exception e) {
                        }
                        //vcf is sure to change
                        if(!b1){
                               if(!b2||!b3){
                                       
                               }else{
                                      edu.zju.common.CExecutor.stopProgram("\nError information: Variant call file (VCF) has been changed, VCF file should be coresponding to caller script or/and SAM format file."
                                        + "\n                        caller_script"
                                        + "\n        SAM format file ---------------> VCF format file"
                                        + "\nPlease provide a new caller script or/and SAM format file that different from before."
                                        + "\nThis information just for ***["+sample+"]***"); 
                               }
                        //vcf is not changed, but other file are changed. not allow        
                        } else {
                               if(!b2||!b3){
                                     edu.zju.common.CExecutor.stopProgram("\nError information: Variant call file (VCF) isn't changed, VCF file should be coresponding to caller script or/and SAM format file. But SAM format file or/and caller script changed."
                                        + "\n                        caller_script"
                                        + "\n        SAM format file ---------------> VCF format file"
                                        + "\nPlease provide a new caller script or/and SAM format file that different from before."
                                        + "\nThis information just for ***["+sample+"]***");   
                               }else{
                                      
                               } 
                                
                        }
                        //when vcs is null and sam vcf script do not change, take use of vcs information in [traceback] section
                        if(b1&&b2&&b3){
                                double currentvcs=0,tracebackvcs=0;
                                currentvcs=s1.getHomoVCSEstimated();
                                tracebackvcs=s2.getHomoVCSEstimated();
                                if(currentvcs==0&&tracebackvcs!=0){
                                        s1.setEstimatedHomoVCS(tracebackvcs);
                                }
                                currentvcs=0;tracebackvcs=0;
                                currentvcs=s1.getHeteroVCSEstimated();
                                tracebackvcs=s2.getHeteroVCSEstimated();
                                if(currentvcs==0&&tracebackvcs!=0){
                                        s1.setEstimatedHeteroVCS(tracebackvcs);
                                }
                                current.updateSampleParameter(s1);
                        }
                }
                return current;                
        }
        
        
        public void jobCheck() throws Exception{
                //check whether snpeff is set in ini file
                if(this.getJobType().equals("init")||this.getJobType().endsWith("Test"))return;
                if(GlobalParameter.getSNPEffPath()==null){
                          edu.zju.common.CExecutor.stopProgram("\nPlease set snpEff folder (not snpEff.jar) path, like: "
                                  + "\nSNPEFF:/path/to/snpeff_folder"
                                  + "\nSnpEff is available in snpeff.sourceforge.net. Download and unzip file, then set variable in "+Init.getParameterFilePath()+"\n");
//                //download function is not switched on
//                        HttpFileDownloader hfd=new HttpFileDownloader();
//                        String s=common.CExecutor.getFileSeparator();
//                        String localFolder=Init.getProjectDirectory()+s+Config.getItem("SOFTWARE_FOLDER_NAME");
//                        new File(localFolder).mkdirs();
//                        String localSnpEffZipPath=localFolder+s+"snpeff.zip";
//                        common.CExecutor.println("Downloading SnpEff....."
//                                + "\n"+localSnpEffZipPath);
//                        hfd.download("http://"+Config.getItem("SNPEFF_LINK"), localSnpEffZipPath);
//                        new ZipUtil().unzipFiles(new File(localSnpEffZipPath), localFolder);
//                        FileFactory.deleteFile(localFolder+s+"snpEff"+s+"SnpSift.jar");
//                        new IniFile(Init.getParameterFilePath()).setItemInfo("SNPEFF", localFolder+ s + "snpEff" +s +"snpEff.jar");
//                        common.CExecutor.println("Set SNPEFF variable in "+Init.getParameterFilePath()+" :Done"
//                                + "\n(!important) User should set SNPEFF_GENOME_VERSION. "
//                                + "\nGenome version listed in: snpeff.sourceforge.net/download.html#dblist\n");
                }
                if(GlobalParameter.getGenomeVersion()==null){
                        edu.zju.common.CExecutor.stopProgram("Please set a SnpEff supportive  genome version  in "+Init.getParameterFilePath()
                                + "\nGenomes are listed in snpeff.sourceforge.net/download.html#dbs\n");
                }
                //check homo library gff file
                //download function is not switched on
                if(this.jobType.equals("gips")&&(new GlobalParameter().getLibraryGenomeGffFile()==null)){
                        edu.zju.common.CExecutor.stopProgram("Please set LIB_GENOME_ANNOTATION.GFF"
                                + "\nFile is available in "+Config.getItem("H_SAPIENS.GFF")
                                +"\nDownload and gunzip this file"
                                + "\nIt is recommended for user to put this file into "+Init.getRefDirectory()+"\n");
//                        HttpFileDownloader hfd=new HttpFileDownloader();
//                        String s = common.CExecutor.getFileSeparator();
//                        String localFilePath=Init.getRefDirectory()+s+"H_Sapiens.gff3.gz";
//                        common.CExecutor.println("Downloading H_Sapiens.gff3 file"
//                                + "\n"+ localFilePath);
//                        hfd.download("http://"+Config.getItem("H_SAPIENS.GFF"), localFilePath);
//                        new GZipUtil().doUncompressFile(localFilePath);
//                        common.CExecutor.println("Set LIB_GENOME_ANNOTATION.GFF variable in "+Init.getParameterFilePath()+" :Done"
//                                + "\n");
//                        new IniFile(Init.getParameterFilePath()).setItemInfo("LIB_GENOME_ANNOTATION.GFF", localFilePath.replace(".gz", ""));
//                        common.CExecutor.println("Set LIB_GENOME_ANNOTATION.GFF variable in "+Init.getParameterFilePath()+" :Done");
                }
                if((this.jobType.equals("vcs")||this.jobType.equals("gips"))&&GlobalParameter.getSampleGffFile()==null){
                        edu.zju.common.CExecutor.stopProgram("Please set GFF file path for "+GlobalParameter.getGenomeVersion()+'\n');
                }
                if(this.sampleParameterBag.getSampleNumber()==0){
                        edu.zju.common.CExecutor.stopProgram("No sample in "+Init.getParameterFilePath());
                }
                for(SampleParameter sampleParameter:this.sampleParameterBag.getBag()){
                        if(sampleParameter.getVCFFile()==null){
                                edu.zju.common.CExecutor.stopProgram("Please set SAMPLE.VCF variable in "+sampleParameter.getName()+" specific [SAMPLE] setion");
                        }
                        if(this.jobType.equals("filter")){
                                
                        }else{
                                double s1=sampleParameter.getHeteroVCSEstimated();
                                double s2=sampleParameter.getHomoVCSEstimated();
                                if(s1!=0||s2!=0){
                                       if(sampleParameter.getSequenceReadsFile()==null){
                                              edu.zju.common.CExecutor.stopProgram("Please set READS_ALIGNMENT.SAM variable in "+sampleParameter.getName()+" specific [SAMPLE] setion");
                                       }
                                       if(sampleParameter.getCallerScript()==null){
                                              edu.zju.common.CExecutor.stopProgram("No caller script."
                                                      + "\nPlease set VAR_CALL_SCRIPT variable in "+sampleParameter.getName()+" specific [SAMPLE] setion or in [GLOBAL]");
                                       }
                                }
                        }
                }
        }
      public void setJobNeedUpdate(){
              this.update=true;
      }
}
