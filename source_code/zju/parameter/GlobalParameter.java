package edu.zju.parameter;

import edu.zju.common.CExecutor;
import edu.zju.common.ZipUtil;
import edu.zju.file.CommonInputFile;
import edu.zju.file.Config;
import edu.zju.file.FileFactory;
import edu.zju.genome.effectiveRegion.GenomeEffectiveRegion;
import edu.zju.genome.gffGenome.Genome;
import edu.zju.options.Init;
import edu.zju.variant.SampleVariant;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 ** @author Zhongxu Zhu
 */
public class GlobalParameter {
        private static String projectName;
        private static String genomeVersion;
        private String callerScriptPath;
        private String matrix="DEFAULT";
        private static String snpEffPath;
        private static edu.zju.file.CommonInputFile genomeGffFile;
        private static String effectiveRegion="CDS|SpliceSite=2";
        private static String filters="EBA";
        private static int anticipation=0;
        private int artificialSNPNumber=5000;
        private static String clinicalVariantLibrary;
        private static edu.zju.file.CommonInputFile libGffFile;
        private static String libVarGenomeVersion;
        //not in .ini file, but needed here
        private String controlFilePath;
        private int snpDensity=3;
        private static String toolType="gips";
        private static String cmdStr;
        private static int sampleNumber=0;
        private int maxAAsimilarityScore=0;
        
        public GlobalParameter(HashMap<String,String> map) {
                ParameterList pl=new ParameterList();
                for(Map.Entry<String,String> entry:map.entrySet()){
                        String item=entry.getKey().trim();
                        String info=entry.getValue().trim();
                        pl.isInGlobParaList(item);
                        switch (item) {
                                case "PROJECT" :{
                                        projectName=info;
                                        Init.setProjectName(info);
                                        break;
                                }
                                case "SNPEFF_GENOME_VERSION": {
                                        if(info==null)edu.zju.common.CExecutor.stopProgram("Please set SNPEFF_GENOME_VERSION");
                                        genomeVersion=info;
                                        break;
                                }
                                case "REF_GENOME_ANNOTATION.GFF":{
                                        if(info==null)edu.zju.common.CExecutor.stopProgram("Please set REF_GENOME_ANNOTATION.GFF");
                                        if(!info.contains(edu.zju.common.CExecutor.getFileSeparator())){
                                                info=Init.getRefDirectory()+edu.zju.common.CExecutor.getFileSeparator()+info;
                                        }
                                        genomeGffFile=FileFactory.getInputFile(info,"GFF");
                                        break;
                                }
                                case "VAR_CALL_SCRIPT":{
                                        if(!info.contains(edu.zju.common.CExecutor.getFileSeparator())){
                                                info=Init.getScriptDirectory()+edu.zju.common.CExecutor.getFileSeparator()+info;
                                        }
                                        new CommonInputFile(info).readLine();
                                        callerScriptPath=info;
                                        break;
                                }
                                case "SCORE_MATRIX":{
                                        matrix=info;
                                        break;
                                }                                        
                                case "EFF_REGION":{
                                        if(info!=null&&!info.isEmpty())effectiveRegion=info;
                                        break;
                                }
                                case "VAR_FILTERS":{
                                        if(info!=null&&!info.isEmpty())filters=info;
                                        break;
                                }
                                case "CANDIDATE_CRITERIA":{
                                        try {
                                                if(info!=null&&!info.isEmpty())anticipation=Integer.parseInt(info);
                                                break;                                        
                                        } catch (Exception e) {
                                                
                                        }
                                }
                                case "NUM_SIM_SNPS":{
                                        if(info!=null&&!info.isEmpty())artificialSNPNumber=Integer.parseInt(info);
                                        break;
                                }
                                case "SNPEFF":{
                                        if(info==null)edu.zju.common.CExecutor.stopProgram("Please set SnpEff folder path ");                                        
                                        if(info.trim().endsWith(".jar"))edu.zju.common.CExecutor.stopProgram("Please set SnpEff folder path,not a jar file ");                                        
                                        snpEffPath=info;
                                        break;
                                }
                                case "LIB_GENOME_ANNOTATION.GFF":{
                                        if(info==null)edu.zju.common.CExecutor.stopProgram("Please set Genome.gff");
                                        libGffFile=FileFactory.getInputFile(entry.getValue(),"GFF");
                                        break;
                                }
                                case "LIB_PHENOTYPE_VAR":{
                                        clinicalVariantLibrary=info;
                                        break;
                                }
                                case "CONTROL" :{
                                        controlFilePath=info;
                                        break;
                                }        
                                case  "MAX_VAR_DENSITY": {
                                        if(info!=null&&!info.isEmpty())snpDensity=Integer.parseInt(entry.getValue());
                                        break;                                
                                } 
                                case  "MAX_AA_SCORE": {
                                        if(info!=null&&!info.isEmpty())this.maxAAsimilarityScore=Integer.parseInt(entry.getValue());
                                        break;                                
                                }         
                                case "LIB_VAR_SNPEFF_GENOME_VERSION":{
                                        libVarGenomeVersion=info;
                                        break;
                                }        
                                        
                                        
                        }
                        
                }
                if((libGffFile!=null&&libVarGenomeVersion!=null&&clinicalVariantLibrary!=null)||(libGffFile==null&&libVarGenomeVersion==null&&clinicalVariantLibrary==null)){
                        
                }else {
                        edu.zju.common.CExecutor.stopProgram("Please set    LIB_VAR_SNPEFF_GENOME_VERSION    LIB_PHENOTYPE_VAR    and    LIB_GENOME_ANNOTATION.GFF");
                }
        }
        
        public GlobalParameter() {
        }
        
        
        
        
        
        public static String getGenomeVersion(){
                if(genomeVersion==null){
                        edu.zju.common.CExecutor.stopProgram("Please set SNPFF_GENOME_VERSION");
                }
                return genomeVersion.trim();
        }
        public static String getProjectName(){
                return projectName;
        }
        
        String getCallerScriptPath(){
                return this.callerScriptPath;
        }
        String getMatrix(){
                return this.matrix;
        }
        int getArtificialSNPNumber(){
                return this.artificialSNPNumber;
        }
        public static int getAnticipation(){
                if(anticipation==0){
                        anticipation=sampleNumber;
                }else if(anticipation>sampleNumber||anticipation<=0){
                     anticipation=sampleNumber;
                     CExecutor.println(CExecutor.getRunningTime()+"CANDIDATE_CRITERIA is reset to sample numbers");   
                }
                return anticipation;
        }
        public static CommonInputFile getSampleGffFile(){
                if(genomeGffFile==null)edu.zju.common.CExecutor.stopProgram("Please set \"REF_GENOME_ANNOTATION.GFF\"");
                return genomeGffFile;
        }
        public static String getEffectiveRegionString(){
                return effectiveRegion;
        }
        public static EffectiveRegionParameter getEffectiveRegionParameter() {
                return new EffectiveRegionParameter(effectiveRegion);
        }

        public static GenomeEffectiveRegion getGenomeEffectiveRegion(CommonInputFile file) {
                GenomeEffectiveRegion genomeEffectiveRegion;
                LinkedList<String> genomeInformation = new LinkedList<>();
                String line = null;
                edu.zju.genome.gffGenome.Genome genome = null;
                try {
                        while ((line = file.readLine()) != null) {//add all the genome information to LinkedList
                                if (line.trim().isEmpty()||line.startsWith("#")) {
                                        continue;
                                }
                                //contig or mitochondiron do not consider
                                if(line.startsWith("NT_"))continue;
                                if(line.startsWith("NW_"))continue;
                                if(line.startsWith("NC_012920"))continue;
                                if(line.contains("#FASTA")) break;
                                genomeInformation.add(line);
                        }
                        genome = new Genome(GlobalParameter.getGenomeVersion(), genomeInformation);
                        genomeInformation = null;//release 
                        file.closeInput();
                } catch (Exception ex) {
                        ex.printStackTrace();
                        edu.zju.common.CExecutor.stopProgram("Genome could not be established! Please check gff file.");
                }
                genomeEffectiveRegion=genomeEffectiveRegion = new GenomeEffectiveRegion(GlobalParameter.getEffectiveRegionParameter(), genome);
                return genomeEffectiveRegion;
        }    
        public static FilterParameter getFilterParameter() {
                FilterParameter filterParameter=new FilterParameter();
                filterParameter.setFilterStrategy(filters);
                return filterParameter;
        }
        /**
         * The first serval line will check lib
         * This function need to be run after global initiated 
         * @return 
         */
        public  CommonInputFile getLibraryGenomeGffFile() {
                if(libGffFile==null){
                        String fileSeparator=edu.zju.common.CExecutor.getFileSeparator();
                        String cliVarGenomeGffResource=Config.getItem("CLIN_VAR_GENOME.GFF3");
                        if(!cliVarGenomeGffResource.contains(fileSeparator)){
                                cliVarGenomeGffResource=cliVarGenomeGffResource.replace("\\",fileSeparator );
                        }
                        String fileName=cliVarGenomeGffResource.split(fileSeparator)[cliVarGenomeGffResource.split(fileSeparator).length-1].trim().replace(".zip", "");
                        String filePath=edu.zju.options.Init.getRefDirectory()+fileSeparator+fileName;
                        if(new File(filePath).isFile()){
                                
                        }else{
                                OutputStream out = null;
                                try {
                                        String zipPath;
                                        InputStream in=getClass().getResourceAsStream(Config.getItem("CLIN_VAR_GENOME.GFF3"));
                                        zipPath=filePath+".zip";
                                        out = new FileOutputStream(zipPath);
                                        byte[] buffer =new byte[1024];
                                        int len;
                                        while((len=in.read(buffer))>0){
                                             out.write(buffer, 0, len);
                                        }
                                        in.close();
                                        out.flush();
                                        out.close();
                                        new ZipUtil().unzipFiles(new File(zipPath),edu.zju.options.Init.getRefDirectory() );
                                        new File(zipPath).delete();
                                        edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"Release file: "+filePath);
                                } catch (IOException ex) {
                                        Logger.getLogger(GlobalParameter.class.getName()).log(Level.SEVERE, null, ex);
                                } 
                        }
                        return libGffFile=FileFactory.getInputFile(filePath, "GFF");
                }
                return libGffFile;
        }
        public static String getLibVarGenomeVersion(){
                if(libVarGenomeVersion==null){
                        libVarGenomeVersion=Config.getItem("CLIN_VAR_GENOME_VERSION");
                }
                return libVarGenomeVersion.trim();
        }
        public  SampleVariant getClinicalVariant(){
                CommonInputFile file=null;
                if(clinicalVariantLibrary==null){
                        InputStream is=this.getClass().getResourceAsStream(Config.getItem("CLIN_VAR_LIB"));
                        BufferedReader br=new BufferedReader( new InputStreamReader(is));
                        file=FileFactory.getInputFile(br, "VCF");
                }else{
                        file=FileFactory.getInputFile(clinicalVariantLibrary, "VCF");
                }
                String clinicalVarSampleName=Config.getItem("CLIN_VAR_NAME");
                SampleVariant sampleVariant=new SampleVariant(clinicalVarSampleName);
                sampleVariant.setVCFFile(file);
                edu.zju.snpAnnotationTools.SNPAnnotationTool snpAnnotationTool = new edu.zju.snpAnnotationTools.SNPAnnotationToolFactory().createSNPAnnotationTool("snpEff");
                sampleVariant=snpAnnotationTool.getSampleAnnotatedVariant(clinicalVarSampleName,file);
                return sampleVariant;
        }
        String getControlFilePath(){
                return this.controlFilePath;
        }
        int getSNPDensity(){
                return this.snpDensity;
        }
        public static void setToolType(String type){
                HashSet<String> tools=new HashSet<String>(){
                    {
                            add("gips");
                            add("filter");
                            add("vcs");
                    };
                                
                };
                if(!tools.contains(type.trim())){
                        edu.zju.common.CExecutor.stopProgram("Do not find tool ["+type+"]");
                }
                toolType=type;
        }
        public static String getToolType(){
                return toolType;
        }
        public static String getSNPEffPath(){
                return snpEffPath;
        }
        public static int getThreadsNumber(){
                return Integer.parseInt(Config.getItem("THREADS"));
        }
        public static String getFilersString(){
                return filters;
        }
        public static String dumpDataForTracing(){
                StringBuffer sb=new StringBuffer();
                sb.append("[GLOBAL]");
                sb.append("\nPROJECT:"+projectName);
                sb.append("\nSNPEFF_GENOME_VERSION:"+genomeVersion);
                if(genomeGffFile!=null){
                        sb.append("\nREF_GENOME_ANNOTATION.GFF:"+genomeGffFile.getFilePath());
                }
                sb.append("\nEFF_REGION:"+effectiveRegion);
                sb.append("\nVAR_FILTERS:"+filters);
                sb.append("\nCANDIDATE_CRITERIA:"+getAnticipation());
                sb.append("\nSNPEFF:"+snpEffPath);
                if(libGffFile!=null) sb.append("\nLIB_GENOME_ANNOTATION.GFF:"+libGffFile.getFilePath());
                sb.append("\nLIB_PHENOTYPE_VAR:"+clinicalVariantLibrary);
                return sb.toString();
        }
        public static void setSampleNumber(int number){
                sampleNumber=number;
        }
        public int getMaxAASimilarityScore(){
                return this.maxAAsimilarityScore;
        }

}
