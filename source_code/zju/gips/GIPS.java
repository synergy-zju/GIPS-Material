 package edu.zju.gips;

import edu.zju.file.CommonInputFile;
import edu.zju.filter.VariantFilter;
import edu.zju.genome.effectiveRegion.GeneEffectiveRegion;
import edu.zju.genome.effectiveRegion.GenomeEffectiveRegion;
import edu.zju.parameter.FilterParameter;
import edu.zju.parameter.GIPSJob;
import edu.zju.parameter.GlobalParameter;
import edu.zju.parameter.SampleParameter;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.parameter.VariantCallingSensitivityDectectorParameter;
import edu.zju.snpCaller.VariantCallingSensitivityDetector;
import edu.zju.variant.SampleVariant;
import edu.zju.variant.SampleVariantBag;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author zzx
 */
public class GIPS {
        private GlobalParameter globalParameter;
        private SampleParameterBag sampleParameterBag;
        private String jobType;
        private GenomeEffectiveRegion genomeEffectiveRegion;
        static double[] sampleBackgroundMutationRate;
        private int maxNumberOfStudySubjectsHarboringMuationInSameGene[];
        private GIPSJob job;
        
        
        public GIPS(GIPSJob job) throws IOException {
                this.setGIPSJob(job);
                this.setGlobalParameter(job.getGlobalParameter());
                this.jobType=job.getJobType();
                this.setSampleParameterBag(job.getSampleParameterBag());
        }
        
        public GIPSJob gipsRun() throws IOException {
                if(jobType==null) edu.zju.common.CExecutor.stopProgram("Please choose a GIPS tool: [vcs|gips|filter]");
                switch (jobType) {
                        case "gips"  : this.runGIPSFull(); break;
                        case "vcs"   : this.runVCS();break;
                        case "filter": this.runFilter();break; 
                        case "init"  : break;
                        case "Test"  : break;        
                }
                return this.job;
        }
        private void runGIPSFull() throws IOException {
                //estimate variant calling sensitivity
                LinkedList<Map.Entry<String, CommonInputFile>> samplesVariantFiles=this.sampleParameterBag.getSamplesVariantFile();
                edu.zju.snpAnnotationTools.SNPAnnotationTool snpAnnotationTool = new edu.zju.snpAnnotationTools.SNPAnnotationToolFactory().createSNPAnnotationTool("snpEff");
                snpAnnotationTool.setSamplesVariantFile(samplesVariantFiles);
                SampleVariantBag sampleVariantBag = snpAnnotationTool.getAnnotatedSampleVariantBag();
                GenomeEffectiveRegion genomeEffectiveRegion=this.getSampleGenomeEffectiveRegion();
                for (Iterator<String> iterator = this.getSampleParameterBag().getSamplesNamesList().iterator(); iterator.hasNext();) {
                        SampleParameter sampleParameter = this.getSampleParameterBag().getSample(iterator.next());
                        if(sampleParameter.isPassSpecify()){
                                SampleVariant sampleVariant=sampleVariantBag.getSampleVariant(sampleParameter.getName());
                                sampleParameter.setSampleVariant(sampleVariant);
                                this.getSampleParameterBag().updateSampleParameter(sampleParameter);
                                continue;
                        }
                        SampleVariant sampleVariant=sampleVariantBag.getSampleVariant(sampleParameter.getName());
                        sampleParameter.setSampleVariant(sampleVariant);
                        int mode=0;//if homo sensitivity ==0, mode plus 1
                                   //if hetero sensitivity ==0 mode plus 2, let detector know which type senstivity should be detected
                        if (sampleParameter.getHomoVCSEstimated() == 0.0) {
                                mode=mode+1;
                        }
                        if (sampleParameter.getHeteroVCSEstimated() == 0.0) {
                                mode = mode+2;
                        }
                        if(mode!=0){
                                VariantCallingSensitivityDectectorParameter vcsdp=new VariantCallingSensitivityDectectorParameter(sampleParameter);
                                vcsdp.renewSampleVariant(sampleVariant);
                                edu.zju.snpCaller.VariantCallingSensitivityDetector detector;
                                vcsdp.setGenomeEffectiveRegion(genomeEffectiveRegion);
                                vcsdp.setGenotypeMode(mode);
                                detector = new VariantCallingSensitivityDetector(vcsdp);
                                detector.genotyping(mode);
                                if (sampleParameter.getHomoVCSEstimated() == 0.0) {
                                        sampleParameter.setEstimatedHomoVCS(detector.getHomoVariantCallingSensitivty());
                                }
                                if (sampleParameter.getHeteroVCSEstimated() == 0.0) {
                                        sampleParameter.setEstimatedHeteroVCS(detector.getHeteroVariantCallingSensitivty());
                                }   
                        }
                        this.getSampleParameterBag().updateSampleParameter(sampleParameter);
                }
                //to filter variants
                FilterParameter filterParameter= GlobalParameter.getFilterParameter();
                if(filterParameter.isNeedEffectiveRegionParameter()){
                        filterParameter.setGenomeEffectiveRegion(this.getSampleGenomeEffectiveRegion());
                }
                VariantFilter variantFilter= new VariantFilter(filterParameter);
                this.setSampleParameterBag(variantFilter.filtrateAndEstimateStudyFir(this.getSampleParameterBag()));
                this.setBackgroundMutation(genomeEffectiveRegion.getLengt(), this.getSampleParameterBag());
                SampleGeneBag sampleGeneBag= this.generateSampleGeneBagAndCalculateGeneSignificanceAtAnticipation(genomeEffectiveRegion, sampleBackgroundMutationRate);
                this.sampleParameterBag.setSampleGeneBag(sampleGeneBag);
                this.sampleParameterBag.setCandidateGeneBag(this.selectCandidateGene(sampleGeneBag, genomeEffectiveRegion, this.sampleParameterBag));
                this.estimateMaxSampleNumberHarboringMutationInSameGene(this.sampleParameterBag.getCandidateGeneBag());
                
                
                genomeEffectiveRegion=this.getLibraryVariantGenomeEffectiveRegion();
                if(filterParameter.isNeedEffectiveRegionParameter()){
                        filterParameter.setGenomeEffectiveRegion(genomeEffectiveRegion);
                }
                variantFilter.estimateFunctionalFalseDiscoveryRate(this.getSampleParameterBag());
                this.calculateTrueTargetGeneDetectingRateAnd1Phenotype1GeneValidation(this.maxNumberOfStudySubjectsHarboringMuationInSameGene);
                this.setSampleParameterBag(this.sampleParameterBag);
        }
        
        private void runVCS() throws IOException {
                LinkedList<Map.Entry<String, CommonInputFile>> samplesVariantFiles=this.sampleParameterBag.getSamplesVariantFile();
                edu.zju.snpAnnotationTools.SNPAnnotationTool snpAnnotationTool = new edu.zju.snpAnnotationTools.SNPAnnotationToolFactory().createSNPAnnotationTool("snpEff");
                snpAnnotationTool.setSamplesVariantFile(samplesVariantFiles);
                SampleVariantBag sampleVariantBag = snpAnnotationTool.getAnnotatedSampleVariantBag();
                GenomeEffectiveRegion genomeEffectiveRegion=this.getSampleGenomeEffectiveRegion();
                for (Iterator<String> iterator = this.getSampleParameterBag().getSamplesNamesList().iterator(); iterator.hasNext();) {
                        SampleParameter sampleParameter = this.getSampleParameterBag().getSample(iterator.next());
                        VariantCallingSensitivityDectectorParameter vcsdp=new VariantCallingSensitivityDectectorParameter(sampleParameter);
                        vcsdp.renewSampleVariant(sampleVariantBag.getSampleVariant(sampleParameter.getName()));
                        edu.zju.snpCaller.VariantCallingSensitivityDetector detector;
                        int mode=0;//if homo sensitivity ==0, mode plus 1
                                   //if hetero sensitivity ==0 mode plus 2, let detector know which type senstivity should be detected
                        if (sampleParameter.getHomoVCSEstimated() == 0.0) {
                                mode=mode+1;
                        }
                        if (sampleParameter.getHeteroVCSEstimated() == 0.0) {
                                mode = mode+2;
                        }
                        vcsdp.setGenomeEffectiveRegion(genomeEffectiveRegion);
                        vcsdp.setGenotypeMode(mode);
                        detector = new VariantCallingSensitivityDetector(vcsdp);
                        detector.genotyping(mode);
                        if (sampleParameter.getHomoVCSEstimated() == 0.0) {
                                sampleParameter.setEstimatedHomoVCS(detector.getHomoVariantCallingSensitivty());
                        }
                        if (sampleParameter.getHeteroVCSEstimated() == 0.0) {
                                sampleParameter.setEstimatedHeteroVCS(detector.getHeteroVariantCallingSensitivty());
                        }                        
                        this.getSampleParameterBag().updateSampleParameter(sampleParameter);
                }
                this.setSampleParameterBag(this.sampleParameterBag);

        }
        private void runFilter() throws IOException {
                LinkedList<Map.Entry<String, CommonInputFile>> samplesVariantFiles=this.sampleParameterBag.getSamplesVariantFile();
                edu.zju.snpAnnotationTools.SNPAnnotationTool snpAnnotationTool = new edu.zju.snpAnnotationTools.SNPAnnotationToolFactory().createSNPAnnotationTool("snpEff");
                snpAnnotationTool.setSamplesVariantFile(samplesVariantFiles);
                SampleVariantBag sampleVariantBag = snpAnnotationTool.getAnnotatedSampleVariantBag();
                this.getSampleParameterBag().setSampleVariant(sampleVariantBag);
                GenomeEffectiveRegion genomeEffectiveRegion=this.getSampleGenomeEffectiveRegion();
                FilterParameter filterParameter= GlobalParameter.getFilterParameter();
                if(filterParameter.isNeedEffectiveRegionParameter()){
                        filterParameter.setGenomeEffectiveRegion(this.getSampleGenomeEffectiveRegion());
                }
                VariantFilter variantFilter= new VariantFilter(filterParameter);
                this.setSampleParameterBag(variantFilter.filtrateAndEstimateStudyFir(this.getSampleParameterBag()));
                
        }
        
        
        private void setGlobalParameter(GlobalParameter globalParameter1){
                this.globalParameter=globalParameter1;
        }
        private void setSampleParameterBag(SampleParameterBag sampleParameterBag1) throws IOException{
                this.job.setSampleParameterBag(sampleParameterBag1);
                this.sampleParameterBag=sampleParameterBag1;
        }
        public SampleParameterBag getSampleParameterBag(){
                return this.sampleParameterBag;
        }
        private GlobalParameter getGlobalParameter(){
                return this.globalParameter;
        }
        private GenomeEffectiveRegion getSampleGenomeEffectiveRegion() {
                if(this.genomeEffectiveRegion==null){                
                        this.genomeEffectiveRegion=GlobalParameter.getGenomeEffectiveRegion(GlobalParameter.getSampleGffFile());
                }
                return this.genomeEffectiveRegion;
        }
        private GenomeEffectiveRegion getLibraryVariantGenomeEffectiveRegion() {
                if(GlobalParameter.getSampleGffFile().getFilePath().trim().equals(new GlobalParameter().getLibraryGenomeGffFile().getFilePath().trim())){
                        return this.genomeEffectiveRegion;
                }
                return this.genomeEffectiveRegion=GlobalParameter.getGenomeEffectiveRegion(new GlobalParameter().getLibraryGenomeGffFile());
        }
        /**
         * In this function,background mutation rate will be calculated and put
         * into sample attribute bag.
         */
        private void setBackgroundMutation(int genomeEffectiveRegionLength, SampleParameterBag sampleParameterBag) {
                SampleParameter sampleParameter;
                this.sampleBackgroundMutationRate = new double[sampleParameterBag.getSampleNumber()];
                int temp = 0;
                double backgroundMutationRate;
                for (Iterator<String> it = sampleParameterBag.getSamplesNamesList().iterator(); it.hasNext();) {
                        String sampleName = it.next();
                        sampleParameter = sampleParameterBag.getSample(sampleName);
                        if(sampleParameter.getBackgroundMutationFrequencyNoConsiderSpeficyOrEstimate()!=0){
                                backgroundMutationRate=sampleParameter.getBackgroundMutationFrequencyNoConsiderSpeficyOrEstimate();
                        }else{
                                backgroundMutationRate = (double) sampleParameterBag.getSample(sampleName).getSampleVariant().getSNPCounts() / genomeEffectiveRegionLength;
                                sampleParameter.setEstimatedBackgroundMutationRate(backgroundMutationRate);
                        }
                        this.sampleBackgroundMutationRate[temp] = backgroundMutationRate;
                        temp = temp + 1;
                }
        }     
        /**
         * Calculate significance ,return sample gene bag,then select
         */
        private CandidateGeneBag selectCandidateGene(SampleGeneBag sampleGeneBag, GenomeEffectiveRegion genomeEffectiveRegion, SampleParameterBag sampleParameterBag) {
                if(sampleParameterBag.isContainSpecifiedSample()){
                        return new CandidateGeneBag(GlobalParameter.getAnticipation());
                }
                sampleParameterBag.setSampleGeneBag(sampleGeneBag);
                CandidateGeneSelector candidateGeneSelector = new CandidateGeneSelector(genomeEffectiveRegion, sampleParameterBag, sampleGeneBag);
                CandidateGeneBag candidateGeneBag = candidateGeneSelector.selectCandidateGene();
                return candidateGeneBag;
        } 
        
        private SampleGeneBag generateSampleGeneBagAndCalculateGeneSignificanceAtAnticipation(GenomeEffectiveRegion genomeEffectiveRegion,double[] sampleBackgroundMutationRate) {
                SampleGeneBag sampleGeneBag = new SampleGeneBag();
                for (GeneEffectiveRegion geneEffectiveRegion : genomeEffectiveRegion.getGeneEffectiveRegions()) {
                        SampleGene sampleGene = new SampleGene(geneEffectiveRegion);
                        sampleGene.calculateSignificanceAtAnticipation(sampleBackgroundMutationRate);
                        sampleGeneBag.addGene(sampleGene);
                }
                return sampleGeneBag;
        }    
        
        
        private void estimateMaxSampleNumberHarboringMutationInSameGene(CandidateGeneBag candidateGeneBag) {
                int[] maxTemp = new int[2];
                for (SampleGene sampleGene : candidateGeneBag.getCandidateGenes()) {
                        int temp = sampleGene.getSampleNumberHarboringMutationInThisGene("Homo");
                        if (maxTemp[0] < temp) {//0 homo
                                maxTemp[0] = temp;
                        }
                        temp = sampleGene.getSampleNumberHarboringMutationInThisGene("Hetero");
                        if (maxTemp[1] < temp) {
                                maxTemp[1] = temp;//1 hetero
                        }
                }
                this.setMaxSampleNumberHarboringMutationInSameGene(maxTemp);
        }

        private void setMaxSampleNumberHarboringMutationInSameGene(int[] number) {
                this.maxNumberOfStudySubjectsHarboringMuationInSameGene = number;
        }        

        
        
        
        private void calculateTrueTargetGeneDetectingRateAnd1Phenotype1GeneValidation(int[] maxSampleNumHarboringMuationInSameGene) {
                this.setMaxSampleNumberHarboringMutationInSameGene(maxSampleNumHarboringMuationInSameGene);
                SignificanceCaculator significanceCaculator = new SignificanceCaculator(this.getSampleParameterBag().getSampleNumber());
                double[] snpDetectingRate;
                int temp = 0;
                snpDetectingRate = new double[this.getSampleParameterBag().getSampleNumber()];
                LinkedList<Map.Entry<String, Double>> sensitivityList;
                sensitivityList = new LinkedList<>(this.getSampleParameterBag().getSampleHomozygousVariantDetectionSensitivity().entrySet());
                for (Map.Entry<String, Double> entry : sensitivityList) {
                        snpDetectingRate[temp] = entry.getValue();
                        temp = temp + 1;
                }//calcultate homozygous gene's detecting rate
                this.getSampleParameterBag().setHomozygousGeneDetectingRate(significanceCaculator.calculateTrueTargetGeneDiscoveriedProbability(this.getSampleParameterBag().getAnticipation(), snpDetectingRate));
                this.getSampleParameterBag().set1Phenoty1GeneValidationHomozygousGene(significanceCaculator.calculate1Phenotype1GeneValidation(maxSampleNumHarboringMuationInSameGene[0], snpDetectingRate));
                temp = 0;
                snpDetectingRate = new double[this.getSampleParameterBag().getSampleNumber()];
                sensitivityList = new LinkedList<>(this.getSampleParameterBag().getSampleHeterozygousVariantDetectionSensitivity().entrySet());
                for (Map.Entry<String, Double> entry : sensitivityList) {
                        snpDetectingRate[temp] = entry.getValue();
                        temp=temp+1;
                }     //calculate heterozygous gene's detecting rate       
                this.getSampleParameterBag().setHeterozygousGeneDetectingRate(significanceCaculator.calculateTrueTargetGeneDiscoveriedProbability(this.getSampleParameterBag().getAnticipation(), snpDetectingRate));
                this.getSampleParameterBag().set1Phenoty1GeneValidationHeterozygousGene(significanceCaculator.calculate1Phenotype1GeneValidation(maxSampleNumHarboringMuationInSameGene[1], snpDetectingRate));
        }
        private void setGIPSJob(GIPSJob job){
                this.job=job;
        }
        
}
