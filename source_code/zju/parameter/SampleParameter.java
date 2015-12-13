package edu.zju.parameter;

import edu.zju.file.CommonInputFile;
import edu.zju.file.FileFactory;
import edu.zju.genome.effectiveRegion.GenomeEffectiveRegion;
import edu.zju.options.Init;
import edu.zju.variant.SampleVariant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author zzx
 */
public class SampleParameter {

        private String sampleName;//sample sampleName 
        private double specifiedBackgroundMutationRate;//background mutation rate in this sample
        private double specifyHomoVDS;
        private double specifyHeteroVDS;
        private double estimatedBackgroundMuationFreqency;
        private double estimatedHomoVCS=0;
        private double estimatedHeteroVCS=0;
        private double estimatedHomoVDS=0;
        private double estimatedHeterVDS=0;
        
        private CommonInputFile vcfFile;
        private CommonInputFile sequenceReadsFile;
        private int remaindSNPNumber;
        private String sampleIntermediateDateFilePath;
        private String callerScript;
        private String matrix;
        private int artificialSNPNumber;
        private int snpDensity=0;
        private int maxAAsimilarityScore;
        
        
        //not in parameter file. created while in GIPS running
        private SampleVariant sampleVariant;
        private String controlFilePath;
        private double functionalFIR=0;
        private double studyFIR=0;
        private HashMap<String,HashSet<Integer>> studyPosition2Filter;
        private boolean isSpecifyBVF=false;
        private boolean isSpecifyHomoVariantDetectionSensitivity=false;
        private boolean isSpecifyHeteroVariantDetectionSensitivity=false;
        private String script_md5;
        private String vcf_md5;
        private String sam_md5;
        
        
        
        public SampleParameter(GlobalParameter globalParameter,HashMap<String,String> map){
                this.matrix=globalParameter.getMatrix();
                this.callerScript=globalParameter.getCallerScriptPath();
                if(this.callerScript!=null) this.script_md5=new CommonInputFile(this.callerScript).getFileMD5();
                this.artificialSNPNumber=globalParameter.getArtificialSNPNumber();
                this.controlFilePath=globalParameter.getControlFilePath();
                this.snpDensity=globalParameter.getSNPDensity();
                this.maxAAsimilarityScore=globalParameter.getMaxAASimilarityScore();
                //overwrite global parameter by samplespcific parameter
                ParameterList pl=new ParameterList();
                for(Map.Entry<String,String> entry:map.entrySet()){
                        String item=entry.getKey().trim();
                        String info=entry.getValue().trim();
                        pl.isInSampleSpecificItemList(item);
                        switch (item) {
                                case "SAMPLE_NAME" :{
                                        this.sampleName=info;
                                        break;
                                }
                                case "SAMPLE.VCF" :{
                                        if(!info.contains(edu.zju.common.CExecutor.getFileSeparator())){
                                                info=Init.getDataDirectory()+edu.zju.common.CExecutor.getFileSeparator()+info;
                                        }
                                        this.setVCFFile(FileFactory.getInputFile(info, "VCF"));
                                        break;
                                }
                                case "READS_ALIGNMENT.SAM" :{
                                        if(!info.contains(edu.zju.common.CExecutor.getFileSeparator())){
                                                info=Init.getDataDirectory()+edu.zju.common.CExecutor.getFileSeparator()+info;
                                        }
                                        this.sequenceReadsFile=FileFactory.getInputFile(info, "SAM");
                                        break;
                                }
                                case "SPECIFY_HOMO_VDS" :{
                                        this.specifyHomoVDS=Double.parseDouble(info);
                                        this.isSpecifyHomoVariantDetectionSensitivity=true;
                                        break;
                                }        
                                case "SPECIFY_HETERO_VDS" :{
                                        this.specifyHeteroVDS=Double.parseDouble(info);
                                        this.isSpecifyHeteroVariantDetectionSensitivity=true;
                                        break;
                                } 
                                case "VAR_CALL_SCRIPT" :{
                                        if(!info.contains(edu.zju.common.CExecutor.getFileSeparator())){
                                                info=Init.getScriptDirectory()+edu.zju.common.CExecutor.getFileSeparator()+info;
                                        }
                                        this.callerScript=info;
                                        this.script_md5=new CommonInputFile(this.callerScript).getFileMD5();
                                        break;
                                }           
                                case "SCORE_MATRIX" :{
                                        this.matrix=info;
                                        break;
                                }  
                                case "CONTROL" :{
                                        this.controlFilePath=info;
                                        break;
                                }   
                                case "NUM_SIM_SNPS":{
                                        if(info!=null&&!info.isEmpty())artificialSNPNumber=Integer.parseInt(entry.getValue());
                                        break;
                                }
                                case "MAX_VAR_DENSITY":{
                                        if(info!=null&&!info.isEmpty())snpDensity=Integer.parseInt(entry.getValue());
                                        break;     
                                } 
                                case "SPECIFY_BVF" :{
                                        this.specifiedBackgroundMutationRate=Double.parseDouble(info);
                                        this.isSpecifyBVF=true;break;
                                }
                                case "ESTIMATED_HOMO_VDS":{
                                        this.estimatedHomoVDS=Double.parseDouble(info);break;
                                }       
                                case "ESTIMATED_HETERO_VDS":{
                                        this.estimatedHeterVDS=Double.parseDouble(info);break;
                                }           
                                case "ESTIMATED_HOMO_VCS":{
                                        this.estimatedHomoVCS=Double.parseDouble(info);break;
                                }            
                                case "ESTIMATED_HETERO_VCS":{
                                        this.estimatedHeteroVCS=Double.parseDouble(info);break;
                                }            
                                case "ESTIMATED_BVF":{
                                        this.estimatedBackgroundMuationFreqency= Double.parseDouble(info);break;
                                }  
                                case "VCF_MD5": {
                                        this.vcf_md5=info;break;
                                }       
                                case "SAM_MD5": {
                                        this.sam_md5=info;
                                        break;
                                }            
                                case "SCRIPT_MD5": {
                                        this.script_md5=info;
                                        break;
                                }             
                                case  "MAX_AA_SCORE": {
                                        if(info!=null&&!info.isEmpty())this.maxAAsimilarityScore=Integer.parseInt(entry.getValue());
                                        break;                                
                                }         
                        }
                }
                this.sampleIntermediateDateFilePath = SampleParameterBag.getIntermediateFilePath()
                        + System.getProperty("file.separator") + this.getName();
                new java.io.File(this.sampleIntermediateDateFilePath).mkdir();
        }
        
        
        
 

        private void setSampleName(String name) {
                this.sampleName = name;
        }

        private void setSequenceReadsFile(String path) {
                this.sequenceReadsFile = new edu.zju.file.SAM(path);
                //this.sequenceReadsFile=FileFactory.getInputFile(path, "SAM");
        }

        public void setEstimatedBackgroundMutationRate(double rate) {
                this.estimatedBackgroundMuationFreqency = rate;
                edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+this.getName() + " background variant frequency (BVF) " + edu.zju.common.NumeralHandler.setScale(rate));
        }

        public void setEstimatedHomoVCS(double sensitivity) {
                edu.zju.common.CExecutor.println(this.getName() + " homozygous   variant calling sensitivity (VCS): " + sensitivity);
                this.estimatedHomoVCS = sensitivity;
        }

        public void setEstimatedHeteroVCS(double sensitivity) {
                edu.zju.common.CExecutor.println(this.getName() + " heterozygous variant calling sensitivity (VCS): " + sensitivity + '\n');
                this.estimatedHeteroVCS = sensitivity;
        }

        public double getHomoVCSEstimated() {
                return edu.zju.common.NumeralHandler.setScale(this.estimatedHomoVCS);
        }

        public double getHeteroVCSEstimated() {
                return edu.zju.common.NumeralHandler.setScale(this.estimatedHeteroVCS);
        }
        public double getFalseIgnoracneRate(){
                return edu.zju.common.NumeralHandler.setScale((1-(1-this.studyFIR)*(1-this.functionalFIR)));
        }
        public double getHomoVariantDetectionSensitivity(){
                if(this.specifyHomoVDS!=0){
                       return edu.zju.common.NumeralHandler.setScale(this.specifyHomoVDS);
                }
                return edu.zju.common.NumeralHandler.setScale(this.getHomoVCSEstimated()*(1-this.studyFIR)*(1-this.functionalFIR));
        }
        public double getHeteroVariantDetectionSensitivity(){
                if(this.specifyHeteroVDS!=0){
                       return edu.zju.common.NumeralHandler.setScale(this.specifyHeteroVDS);
                }
                return edu.zju.common.NumeralHandler.setScale(this.getHeteroVCSEstimated()*(1-this.studyFIR)*(1-this.functionalFIR));
        }    
        public double getHomoVariantDetectionSensitivityEstimated(){
                return edu.zju.common.NumeralHandler.setScale(this.getHomoVCSEstimated()*(1-this.studyFIR)*(1-this.functionalFIR));
        }
        public double getHeteroVariantDetectionSensitivityEstimated(){
                return edu.zju.common.NumeralHandler.setScale(this.getHeteroVCSEstimated()*(1-this.studyFIR)*(1-this.functionalFIR));
        }      
        public double getBackgroundMutationFrequencyNoConsiderSpeficyOrEstimate() {
                if(this.specifiedBackgroundMutationRate!=0){
                        return edu.zju.common.NumeralHandler.setScale(this.specifiedBackgroundMutationRate);
                }
                if(this.estimatedBackgroundMuationFreqency!=0){
                        return edu.zju.common.NumeralHandler.setScale(this.estimatedBackgroundMuationFreqency);
                }
                return 0;
        }
        public String getName() {
                return this.sampleName;
        }

        public CommonInputFile getSequenceReadsFile() {
                return this.sequenceReadsFile;
        }

        int getRemainedSNPNumber() {
                return this.remaindSNPNumber;
        }



        private void setVCFFile(CommonInputFile file) {
                this.vcfFile = file;
        }

        public CommonInputFile getVCFFile() {
                return this.vcfFile;
        }

        public String getSampleIntermediateFilePath() {
                return this.sampleIntermediateDateFilePath;
        }

        public int getArtificialSNPNumber(){
                return this.artificialSNPNumber;
        }
        //usded in runGIPSFull
        public void setSampleVariant(SampleVariant sampleVariant1){
                this.sampleVariant=sampleVariant1;
                this.remaindSNPNumber=sampleVariant1.getSNPCounts();
        }
        public SampleVariant getSampleVariant(){
                if(this.sampleVariant==null){
                        this.sampleVariant=new SampleVariant(this.getName());
                }
                return this.sampleVariant;
        }
        public String getMatrix(){
                return this.matrix;
        }
        
        public CommonInputFile getControlFile(){
                if(this.controlFilePath==null||this.controlFilePath.trim().isEmpty())return null;
                return FileFactory.getInputFile(this.controlFilePath, "VariantControl");
        }
        public void setFunctionalFIR(double fir){
                this.functionalFIR=fir;
        }
        public void setStudyFIR( double fir ){
                this.studyFIR=fir;
        }
        public void addStudyPositionToFilter(String chr, int pos){
                if(this.studyPosition2Filter==null){
                        this.studyPosition2Filter= new HashMap<>();
                }
                HashSet<Integer> chrPosSet=new HashSet<>();
                String chrInMap;
                if(!this.studyPosition2Filter.containsKey(chr)){
                        this.studyPosition2Filter.put(chr, chrPosSet);
                }
                chrPosSet=this.studyPosition2Filter.get(chr);
                chrPosSet.add(pos);
                this.studyPosition2Filter.put(chr, chrPosSet);
        }
        public void addStudyPositionToFilter(HashMap<String,HashSet<Integer>> map){
                if(this.studyPosition2Filter==null){
                        this.studyPosition2Filter= new HashMap<>();
                }
                HashSet<Integer> chrPosSet=new HashSet<>();
                if(map==null||map.isEmpty()){
                        return;
                }
                for(String chr:map.keySet()){
                       if(!this.studyPosition2Filter.containsKey(chr)){
                                this.studyPosition2Filter.put(chr, chrPosSet);
                       } 
                       chrPosSet=this.studyPosition2Filter.get(chr);
                       chrPosSet.addAll(map.get(chr));
                       this.studyPosition2Filter.put(chr, chrPosSet);
                }
        }   
        void estimateStudyFir(GenomeEffectiveRegion genomeEffectiveRegion){
                int positionSum=0;
                try {//when variant detection sensitivity and vcf are specified, this set may report java.lang.NullPointerExcetion
                        //But it doesn't matter
                        for(String chr:this.studyPosition2Filter.keySet()){
                                HashSet<Integer> posSet=this.studyPosition2Filter.get(chr);
                                for(int pos:posSet){
                                        if(genomeEffectiveRegion.isInEffectiveRegion(chr, pos)){
                                                positionSum=positionSum+1;
                                        }
                                }
                        }                
                } catch (Exception e) {
                }
                this.studyPosition2Filter=null;
                this.studyFIR=(double) positionSum/genomeEffectiveRegion.getLengt();
        }
        public String getCallerScript(){
                return this.callerScript;
        }
        public String getSampleFilterStrategy2String(){
                StringBuffer string=new StringBuffer();
                String filters=GlobalParameter.getFilersString();
                string.append(" Filters: "+filters);
                if(filters.contains("E")){
                        string.append("\n# Effective region: "+GlobalParameter.getEffectiveRegionString());
                }
                if(filters.contains("B")){
                        string.append("\n# Score matrix: "+this.matrix);
                }
                if(this.controlFilePath!=null){
                        string.append("\n# Control:"+this.controlFilePath);
                }
                return string.toString();
                
        }        
        
        public double getFalseIgnoranceRateOfFunction(){
                return edu.zju.common.NumeralHandler.setScale(this.functionalFIR);
        }
        public double getFalseIgnoranceRateOfStudy(){
                return edu.zju.common.NumeralHandler.setScale(this.studyFIR);
        }

        public int getSNPDensity(){
                return this.snpDensity;
        }
        public double getBackgroundMutationRateEstimated(){
                return edu.zju.common.NumeralHandler.setScale(this.estimatedBackgroundMuationFreqency);
        }
        //please consider the relationship between sample specify and filtering
        public boolean isPassSpecify(){
                return (this.isSpecifyBVF&&this.isSpecifyHeteroVariantDetectionSensitivity&&this.isSpecifyHomoVariantDetectionSensitivity);
        }
        public boolean isSpecified(){
                return (this.isSpecifyBVF||this.isSpecifyHeteroVariantDetectionSensitivity||this.isSpecifyHomoVariantDetectionSensitivity);
        } 
        String getVCFMd5(){
                if(this.vcf_md5==null){
                        if(this.vcfFile==null){
                                return "no md5";
                        }else{
                                this.vcf_md5=this.vcfFile.getFileMD5();
                        }
                }
                return this.vcf_md5;
        }
        String getSAMMd5(){
                if(this.sam_md5==null) {
                        if(this.sequenceReadsFile==null){
                                return "no md5";
                        }else{
                                this.sam_md5=this.sequenceReadsFile.getFileMD5();
                        }
                }
                return this.sam_md5;
        }
        String getScriptMd5(){
                if(this.script_md5==null) {
                        if(this.callerScript==null){
                                return "no md5";
                        }else{
                                this.script_md5= new CommonInputFile(this.callerScript).getFileMD5();
                        }
                }
                return this.script_md5;
        }
        public int getMaxAASimilarityScore(){
                return this.maxAAsimilarityScore;
        }
        
}
