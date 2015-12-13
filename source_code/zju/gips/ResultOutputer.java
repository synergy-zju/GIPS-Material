package edu.zju.gips;

import edu.zju.common.FileHandler;
import edu.zju.common.StringSecurity;
import edu.zju.file.CommonInputFile;
import edu.zju.file.CommonOutputFile;
import edu.zju.file.Config;
import edu.zju.file.FileFactory;
import edu.zju.options.Init;
import edu.zju.parameter.GIPSJob;
import edu.zju.parameter.GlobalParameter;
import edu.zju.parameter.SampleParameter;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.variant.SNP;
import edu.zju.variant.SampleVariant;
import java.io.File;
import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class ResultOutputer {
        private GIPSJob job;
        
        public ResultOutputer(GIPSJob job) {
                this.setGIPSJob(job);
        }
        private void setGIPSJob(GIPSJob job){
                this.job=job;
        }
        
        public void outputResult(){
                String jobType=this.job.getJobType();
                switch (jobType) {
                        case "filter":{
                                this.dumpFilterOutput();
                                break;
                        }
                        case "gips":{
                                this.dumpGIPSResult();
                                break;
                        }
                        case "vcs":{
                                this.dumpVCSResult();
                                break;
                        }
                        case "init":{
                                this.createEmptyResultFile();
                                return;
                        }
                        case "Test" :{
                                this.createEmptyResultFile();
                                return;
                        }
                }
                edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"Result file output: "+Init.getWorkingDirectroy());
                
        }
        private void createEmptyResultFile(){
                //this.dumpFilterOutput();
                SampleParameterBag sampleParameterBag=this.job.getSampleParameterBag();
                String workingDirectory=Init.getWorkingDirectroy();
                String gipsFilePath=workingDirectory+edu.zju.common.CExecutor.getFileSeparator()+Config.getItem("RESULT_GIPS_FILE");
                CommonOutputFile gipsFile=FileFactory.getOutputFile(gipsFilePath);
                if(gipsFile.isFile()){
                        new FileHandler().fileChannelCopy(new File(gipsFilePath),new File(Init.getResultArchiveFolderDirectory()+edu.zju.common.CExecutor.getFileSeparator()+"Archive_"+edu.zju.common.CExecutor.getCurrentTime().replace(":", "-") +"_"+gipsFile.getFileName()));
                }
                gipsFile.write(""
                        + "#================================================================"
                        + "\n# Gene Identification via Phenotype Sequencing - Result File"
                        + "\n#"
                        + "\n# Job id: "+job.getMD5()
                        + "\n# Completion time:"+edu.zju.common.CExecutor.getCurrentTime()
                        + "\n# Feature option \'-"+job.getJobType()+"\'"
                        + "\n#================================================================\n"
                        + "\n[Study Effectiveness]"
                        + "\nChance to report true phenotype-associated genes "
                        + "\nIf phenotype is recessive: "
                        + "\nIf phenotype is  dominant: \n"
                        + "\nSignificance of violating Mendelian phenotype assumption"
                        + "\nIf phenotype is recessive: "
                        + "\nIf phenotype is  dominant: \n"
                        + "\nExpected number of random genes that may be reported: \n"
                        + "\n[Candidate Gene List]"
                        + "\n#Details on the variants they harbor can be found in CANDIDATE_GENES.txt"
                        + "\nGENE_ID\tGENE_NAME\tChr\tEFF_REGION_LENGHT\tSIGNIFICANCE\tNUM_VARIANT\tNUM_SAMPLE\n"
                        + ""
                );   
                gipsFile.write("\n[Sample Specific Sensitivity and Specificity]\n"
                        + "Variant Calling Sensitivity (VCS)\n"
                        + "SAMPLE_NAME\tHOMO_VCS\tHETERO_VCS\n");
                gipsFile.write("\nFalse Ignorance Rate (FIR)\n"
                        + "SAMPLE_NAME\tFIR_FUNCTION\tFIR_STUDY\tFIR\n");
                gipsFile.write("\nSample Variant Detection Sensitivity (VDS) (Measurement of sensitivity)\n"
                        + "SAMPLE_NAME\tHOMO_VDS\tHETERO_VDS\n");
                gipsFile.write("\nSample Background  Variant  Frequency (BVF) (Measurement of specificity)\n"
                        + "SAMPLE_NAME\tBVF\n");
                gipsFile.write("#----------------------------------------------------------------\n#\n#\n");
                gipsFile.write("\n# Caution: Do not modify the below section\n[GIPS TRACEBACK]\n");
                gipsFile.write(""+this.dumpDataForTracing(sampleParameterBag)+"\n");
                gipsFile.write("[END GIPS TRACEBACK]");
                gipsFile.closeOutput();
                
                
                
        }
        private void dumpGIPSResult(){
                //this.dumpFilterOutput();
                SampleParameterBag sampleParameterBag=this.job.getSampleParameterBag();
                String workingDirectory=Init.getWorkingDirectroy();
                String gipsFilePath=workingDirectory+edu.zju.common.CExecutor.getFileSeparator()+Config.getItem("RESULT_GIPS_FILE");
                CommonOutputFile gipsFile=FileFactory.getOutputFile(gipsFilePath);
                if(gipsFile.isFile()){
                        new FileHandler().fileChannelCopy(new File(gipsFilePath),new File(Init.getResultArchiveFolderDirectory()+edu.zju.common.CExecutor.getFileSeparator()+"Archive_"+edu.zju.common.CExecutor.getCurrentTime().replace(":", "-")+"_"+gipsFile.getFileName()));
                }
                gipsFile.write(""
                        + "#================================================================"
                        + "\n# Gene Identification via Phenotype Sequencing - Result File"
                        + "\n#"
                        + "\n# Job id: "+job.getMD5()
                        + "\n# Completion time:"+edu.zju.common.CExecutor.getCurrentTime()
                        + "\n# Feature option \'-T "+job.getJobType()+"\'"
                        + "\n#================================================================\n"
                        + "\n[Study Effectiveness]"
                        + "\nChance to report true phenotype-associated genes "
                        + "\nIf phenotype is recessive: "+sampleParameterBag.getHomozygousGeneDetectingRate()
                        + "\nIf phenotype is  dominant: "+sampleParameterBag.getHetrozygousGeneDetectingRate()+"\n"
                        + "\nSignificance of violating Mendelian phenotype assumption"
                        + "\nIf phenotype is recessive: "+sampleParameterBag.get1Phenotype1GeneValidationHomozygous()
                        + "\nIf phenotype is  dominant: "+sampleParameterBag.get1Phenotype1GeneValidationHeterozygous()+"\n"
                        + "\nExpected number of random genes that may be reported: "+sampleParameterBag.getSampleGeneBag().getRandomGeneDiscoveryNumberInGenome()+"\n"
                        + "\n[Candidate Gene List]"
                        + "\n#Details on the variants they harbor can be found in CANDIDATE_GENES.txt"
                        + "\nGENE_ID\tGENE_NAME\tChr\tEFF_REGION_LENGHT\tSIGNIFICANCE\tNUM_VARIANT\tNUM_SAMPLE\n"
                        + ""
                );   
                for(SampleGene candidateGene:sampleParameterBag.getCandidateGeneBag().getCandidateGenes()){
                        gipsFile.write(candidateGene.getID()+"\t"
                                + candidateGene.getGeneName()+"\t"
                                + candidateGene.getChrID()+"\t"
                                + candidateGene.getEffectiveRegionLength()+"\t"
                                + candidateGene.getSignificanceRegardlessOfAnticipation()+"\t"
                                + candidateGene.getSNPNumber()+"\t"
                                + candidateGene.getSampleNumberHarboringMutationInThisGene()+"\n"
                                );
                }
                gipsFile.write("\n[Sample Specific Sensitivity and Specificity]\n"
                        + "Variant Calling Sensitivity (VCS)\n"
                        + "SAMPLE_NAME\tHOMO_VCS\tHETERO_VCS\n");
                if(sampleParameterBag.isContainSpecifiedSample()){
                
                }else{
                        for(String sampleName:sampleParameterBag.getSamplesNamesList()){
                                SampleParameter sampleParameter=sampleParameterBag.getSample(sampleName);
                                gipsFile.write(sampleName+"\t"+sampleParameter.getHomoVCSEstimated()+"\t"+sampleParameter.getHeteroVCSEstimated()+"\n");
                        }
                }
                gipsFile.write("\nFalse Ignorance Rate (FIR)\n"
                        + "SAMPLE_NAME\tFIR_FUNCTION\tFIR_STUDY\tFIR\n");
                for(String sampleName:sampleParameterBag.getSamplesNamesList()){
                        SampleParameter sampleParameter=sampleParameterBag.getSample(sampleName);
                        gipsFile.write(sampleName+"\t"+sampleParameter.getFalseIgnoranceRateOfFunction()+"\t"+sampleParameter.getFalseIgnoranceRateOfStudy()+'\t'+sampleParameter.getFalseIgnoracneRate()+"\n");
                }
                
                gipsFile.write("\nSample Variant Detection Sensitivity (VDS) (Measurement of sensitivity)\n"
                        + "SAMPLE_NAME\tHOMO_VDS\tHETERO_VDS\n");
                for(String sampleName:sampleParameterBag.getSamplesNamesList()){
                        SampleParameter sampleParameter=sampleParameterBag.getSample(sampleName);
                        gipsFile.write(sampleName+"\t"+sampleParameter.getHomoVariantDetectionSensitivity()+"\t"+sampleParameter.getHeteroVariantDetectionSensitivity()+"\n");
                }
                gipsFile.write("\nSample Background  Variant  Frequency (BVF) (Measurement of specificity)\n"
                        + "SAMPLE_NAME\tBVF\n");
                for(String sampleName:sampleParameterBag.getSamplesNamesList()){
                        SampleParameter sampleParameter=sampleParameterBag.getSample(sampleName);
                        gipsFile.write(sampleName+"\t"+sampleParameter.getBackgroundMutationFrequencyNoConsiderSpeficyOrEstimate()+"\n");
                }
                
                gipsFile.write("\n#----------------------------------------------------------------\n");
                gipsFile.write("# [Protocol]");
                gipsFile.write("\n#----------------------------------------------------------------\n");
                CommonInputFile parameterFile= new CommonInputFile(Init.getParameterFilePath());
                String line;
                while((line=parameterFile.readLine())!=null){
                        gipsFile.write("# "+line+"\n");
                }
                gipsFile.write("#----------------------------------------------------------------\n#\n#\n");
                gipsFile.write("\n# Caution: Do not modify the below section\n[GIPS TRACEBACK]\n");
                gipsFile.write(""+this.dumpDataForTracing(sampleParameterBag)+"\n");
                gipsFile.write("[END GIPS TRACEBACK]");
                gipsFile.closeOutput();
                //output SNP in candidate gene 
                if(sampleParameterBag.isContainSpecifiedSample()){}
                else{
                        this.dumpCandidateGene(sampleParameterBag.getCandidateGeneBag());
                }
        }
        
        private void dumpVCSResult(){
                SampleParameterBag sampleParameterBag=this.job.getSampleParameterBag();
                String workingDirectory=Init.getWorkingDirectroy();
                String gipsFilePath=workingDirectory+edu.zju.common.CExecutor.getFileSeparator()+Config.getItem("RESULT_GIPS_FILE");
                CommonOutputFile gipsFile=FileFactory.getOutputFile(gipsFilePath);
                if(gipsFile.isFile()){
                        new FileHandler().fileChannelCopy(new File(gipsFilePath),new File(Init.getResultArchiveFolderDirectory()+edu.zju.common.CExecutor.getFileSeparator()+"Archive_"+edu.zju.common.CExecutor.getCurrentTime().replace(":", "-")+"_"+gipsFile.getFileName()));
                }
                gipsFile.write(""
                        + "#================================================================"
                        + "\n# Gene Identification via Phenotype Sequencing - Result File"
                        + "\n#"
                        + "\n# Job id: "+job.getMD5()
                        + "\n# Completion time:"+edu.zju.common.CExecutor.getCurrentTime()
                        + "\n# Feature option \'-T "+job.getJobType()+"\'"
                        + "\n#================================================================\n"
                );   
                gipsFile.write("\n[Sample Specific Variant Calling Sensitivity (VCS)]"
                        + "\nSAMPLE_NAME\tHOMO_VCS\tHETERO_VCS\n");
                if(sampleParameterBag.isContainSpecifiedSample()){
                
                }else{
                        for(String sampleName:sampleParameterBag.getSamplesNamesList()){
                                SampleParameter sampleParameter=sampleParameterBag.getSample(sampleName);
                                gipsFile.write(sampleName+"\t"+sampleParameter.getHomoVCSEstimated()+"\t"+sampleParameter.getHeteroVCSEstimated()+"\n");
                        }
                }
                gipsFile.write("\n#----------------------------------------------------------------\n");
                gipsFile.write("# [Protocol]");
                gipsFile.write("\n#----------------------------------------------------------------\n");
                CommonInputFile parameterFile= new CommonInputFile(Init.getParameterFilePath());
                String line;
                while((line=parameterFile.readLine())!=null){
                        gipsFile.write("# "+line+"\n");
                }
                gipsFile.write("#----------------------------------------------------------------\n#\n#\n");
                gipsFile.write("\n# Caution: Do not modify the below section\n[GIPS TRACEBACK]\n");
                gipsFile.write(""+this.dumpDataForTracing(sampleParameterBag)+"\n");
                gipsFile.write("[END GIPS TRACEBACK]");
                gipsFile.closeOutput();
        }
        private void dumpFilterOutput(){
                SampleParameterBag sampleParameterBag=this.job.getSampleParameterBag();
                for(String sampleName:sampleParameterBag.getSamplesNamesList()){
                        SampleParameter sampleParameter=sampleParameterBag.getSample(sampleName);
                        CommonOutputFile outputFile=FileFactory.getOutputFile(Init.getWorkingDirectroy()+edu.zju.common.CExecutor.getFileSeparator()+sampleName+".filter.gips");
                        outputFile.write("#================================================================"
                                + "\n# Gene Identification via Phenotype Sequencing - Filter Result File" 
                                + "\n#"
                                + "\n# Job id : "+job.getMD5()
                                + "\n# Completion time :"+edu.zju.common.CExecutor.getCurrentTime()
                                + "\n# Feature option \'-T filter\'"
                                + "\n# "+sampleParameter.getSampleFilterStrategy2String()
                                + "\n#================================================================\n");
                        SampleVariant sampleVariant=sampleParameter.getSampleVariant();
                        LinkedList<SNP> snpList=sampleVariant.getSnps();
                        for(SNP snp:snpList){
                                outputFile.write(snp.getSNPInfoInVcf()+'\n');
                        }
                        outputFile.closeOutput();
                }
        }
        
        private String dumpDataForTracing(SampleParameterBag sampleParameterBag){

                StringBuffer sb2Zip=new StringBuffer();
                String globalParameterData=GlobalParameter.dumpDataForTracing();
                sb2Zip.append(globalParameterData+"\n");
                if(sampleParameterBag!=null){
                        sb2Zip.append(sampleParameterBag.dumpDataForTracing());
                }
   //          System.out.println("\n\n\n"+sb2Zip);
                StringSecurity ss=new StringSecurity();
                return ss.gzip(sb2Zip.toString());
        }
        
        private void dumpCandidateGene(CandidateGeneBag candidateGeneBag){
                //output SNP in candidate gene 
                String genesFilePath=Init.getWorkingDirectroy()+edu.zju.common.CExecutor.getFileSeparator()+Config.getItem("CAN_GENE_FILE");
                CommonOutputFile geneFile=FileFactory.getOutputFile(genesFilePath);
                if(geneFile.isFile()){
                        new FileHandler().fileChannelCopy(new File(genesFilePath),new File(Init.getResultArchiveFolderDirectory()+edu.zju.common.CExecutor.getFileSeparator()+"Archive_"+edu.zju.common.CExecutor.getCurrentTime().replace(":", "-")+"_"+geneFile.getFileName()));
                }
                geneFile.write("#================================================================"
                        + "\n#"
                        + "\n# Gene Identification via Phenotype Sequencing - Candidate Gene File\n# Job id: "+job.getMD5()+"   Time:"+edu.zju.common.CExecutor.getCurrentTime()
                        + "\n#================================================================\n\n"
                        + "[Candidate Gene List]\n"
                        + "GENE_ID\tGENE_NAME\tChr\tEFF_REGION_LENGHT\tSIGNIFICANCE\tNUM_VARIANT\tNUM_SAMPLE\n"
                        + ""
                        );
                for(SampleGene candidateGene:candidateGeneBag.getCandidateGenes()){
                        geneFile.write(candidateGene.getID()+"\t"
                                + candidateGene.getGeneName()+"\t"
                                + candidateGene.getChrID()+"\t"
                                + candidateGene.getEffectiveRegionLength()+"\t"
                                + candidateGene.getSignificanceRegardlessOfAnticipation()+"\t"
                                + candidateGene.getSNPNumber()+"\t"
                                + candidateGene.getSampleNumberHarboringMutationInThisGene()+"\n"
                                );
                }
                for(SampleGene candidateGene:candidateGeneBag.getCandidateGenes()){
                        geneFile.write("\n----------------------------------------------------------------\n[Candidate gene: "+candidateGene.getGeneName()+"]\n");
                        geneFile.write("#SAMPLE	CHROM	POS	ID	REF	ALT	\tDP\tGT\tEFF\n");
                        for(SNP snp:candidateGene.getSNPInThisCandidateGene()){
                                geneFile.write(snp.getSampleName()+"\t"+snp.getChr()+"\t"+snp.getPosition()+"\t"+snp.getId()+"\t"+snp.getRef()+"\t"+snp.getAlt()+"\t"+snp.getDepth()+"\t"+snp.getGenotype()+"\t"+snp.getAnnotationInfo()+"\n");
                        }
                }
                geneFile.closeOutput(); 
        }
        
        
        
}
