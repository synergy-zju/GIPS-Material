package edu.zju.parameter;

import edu.zju.common.NumeralHandler;
import edu.zju.file.CommonInputFile;
import edu.zju.file.Config;
import edu.zju.genome.effectiveRegion.GenomeEffectiveRegion;
import edu.zju.gips.CandidateGeneBag;
import edu.zju.gips.SampleGeneBag;
import edu.zju.options.Init;
import edu.zju.variant.SampleVariant;
import edu.zju.variant.SampleVariantBag;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 *
 * @author zzx
 */
public class SampleParameterBag {

        private String organism;
        private LinkedList<Map.Entry<String, SampleParameter>> bag;
        private static int sampleNum;
        private double homozygousGeneDetectingRate;
        private double heterozygousGeneDetectingRate;
        private SampleGeneBag sampleGeneBag;
        private CandidateGeneBag candidateGeneBag;
        private double validation1Phenotype1GeneHeterozygousGene;
        private double validation1Phenotype1GeneHomozygousGene;
        private int[] maxNumberOfStudySubjectsHarboringMuationInSameGene = new int[2];//0 homo 1hetero
        private static String intermediateFilePath;        
        private LinkedList<String> sampleNamesList;
        private boolean isContainSampleSpecified=false;
        
        public SampleParameterBag() {
                bag = new LinkedList<>();
                this.intermediateFilePath = Init.getWorkingDirectroy() + System.getProperty("file.separator") + Config.getItem("IntermediateFile");
                File file = new java.io.File(intermediateFilePath);
                if (!file.exists()) {
                        file.mkdir();
                        edu.zju.common.CExecutor.println("temporary folder is created");
                }
        }
        private void setOrganism(String name) {
                this.organism = name;
        }

        private String getOrganism() {
                return this.organism;
        }

        public static void setSampleNumber(int sampleNumber) {
                sampleNum = sampleNumber;
        }
        public static int getSampleNumber(){
                return sampleNum;
        }
        public void updateSampleParameter(SampleParameter sampleParameter) {
                for (Map.Entry<String, SampleParameter> entry : bag) {
                        if (entry.getKey().equals(sampleParameter.getName())) {
                                entry.setValue(sampleParameter);
                                break;
                        } else {
                                continue;
                        }

                }
        }
        public SampleParameter getSample(String sampleName) {
                for (Map.Entry<String, SampleParameter> entry : bag) {
                        if (sampleName.equals(entry.getKey())) {
                                return entry.getValue();
                        }
                }
                edu.zju.common.CExecutor.stopProgram("Do not find sample: ["+sampleName+"]. If your are using update mode, please make sure your have not changed \"[SAMPLE_LIST]\" section.");
                return null;
        }

        public void setSampleGeneBag(SampleGeneBag sampleGeneBag1) {
                this.sampleGeneBag = sampleGeneBag1;
        }

        public void setCandidateGeneBag(CandidateGeneBag candidateGeneBag1) {
                this.candidateGeneBag = candidateGeneBag1;
//           this.caculateFalseDiscoveryRate(candidateGeneBag1.getCandidateGeneNumber());
        }
        public int getAnticipation() {
                int anticipation=GlobalParameter.getAnticipation();
                return anticipation;
        }
        public HashMap<String, Double> getSampleHomozygousVariantDetectionSensitivity() {
                HashMap<String, Double> sensitivity = new HashMap<>();
                for (Iterator<String> iterator = this.getSamplesNamesList().iterator(); iterator.hasNext();) {
                        String sampleName = iterator.next();
                        sensitivity.put(sampleName, this.getSample(sampleName).getHomoVariantDetectionSensitivity());
                }
                return sensitivity;
        }

        public HashMap<String, Double> getSampleHeterozygousVariantDetectionSensitivity() {
                HashMap<String, Double> sensitivity = new HashMap<>();
                for (Iterator<String> iterator = this.getSamplesNamesList().iterator(); iterator.hasNext();) {
                        String sampleName = iterator.next();
                        sensitivity.put(sampleName, this.getSample(sampleName).getHeteroVariantDetectionSensitivity());
                }
                return sensitivity;
        }

        public LinkedList<Map.Entry<String, CommonInputFile>> getSamplesVariantFile() {
                LinkedList<Map.Entry<String, CommonInputFile>> fileList = new LinkedList<>();
                HashMap<String, CommonInputFile> fileTemp = new HashMap<>();
                for (String sampleName : this.getSamplesNamesList()) {
                        //This place is commenting
                        //because even if sample pass specify checking,
                        // sample also need variant file to run.
                        // if this place continue, sample will not get variant from VCF file
                        // bug time: 2015.01.01
                        //if (this.getSample(sampleName).isPassSpecify()){
                        //        continue;
                        //}
                        fileTemp = new HashMap<>();
                        fileTemp.put(sampleName, this.getSample(sampleName).getVCFFile());
                        if (fileTemp.entrySet().iterator().hasNext()) {
                                fileList.add(fileTemp.entrySet().iterator().next());
                        }
                }
                return fileList;
        }

        public HashMap<String, CommonInputFile> getSamplesSequenceReadsFiles() {
                HashMap<String, CommonInputFile> files = new HashMap<>();
                for (String sampleName : this.getSamplesNamesList()) {
                        files.put(sampleName, this.getSample(sampleName).getSequenceReadsFile());
                }
                return files;
        }
        public void setHomozygousGeneDetectingRate(double rate) {
                rate = this.homozygousGeneDetectingRate = NumeralHandler.setScale(rate);
                edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"If phenotype is recessive:\n"
                        + "Chance to report true phenotype-associated genes: " + rate);
        }
        public double getHomozygousGeneDetectingRate(){
                return this.homozygousGeneDetectingRate;
        }
        public double getHetrozygousGeneDetectingRate(){
                return this.heterozygousGeneDetectingRate;
        }
        public void setHeterozygousGeneDetectingRate(double rate) {
                rate = this.heterozygousGeneDetectingRate = NumeralHandler.setScale(rate);
                edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"If phenotype is  dominant:\n"
                        + "Chance to report true phenotype-associated genes: " + rate);
        }

        public void set1Phenoty1GeneValidationHeterozygousGene(double validation) {
                validation = this.validation1Phenotype1GeneHeterozygousGene = NumeralHandler.setScale(validation);
                edu.zju.common.CExecutor.println("Significance of voilating Mendelian phenotype assumption: " + validation);
        }
        public double get1Phenotype1GeneValidationHomozygous(){
                return this.validation1Phenotype1GeneHomozygousGene;
        }
        public double get1Phenotype1GeneValidationHeterozygous(){
                return this.validation1Phenotype1GeneHeterozygousGene;
        }
        public void set1Phenoty1GeneValidationHomozygousGene(double validation) {
                validation = this.validation1Phenotype1GeneHomozygousGene = NumeralHandler.setScale(validation);
                edu.zju.common.CExecutor.println("Significance of voilating Mendelian phenotype assumption: " + validation);
        }

        private void setMaxSampleNumberHarboringMutationInSameGene(int[] number) {
                this.maxNumberOfStudySubjectsHarboringMuationInSameGene = number;
        }

        /**
         * Check whether bvf, homo_vds and hetero_vds are specified
         * @param sampleParameter sample contains various statistic data .sample
         * just has its name at the beginning
         */
        public void addSampleParameter(SampleParameter sampleParameter) {
                if(sampleParameter.isSpecified()&&!sampleParameter.isPassSpecify()){
                        edu.zju.common.CExecutor.stopProgram("["+sampleParameter.getName() + "] SPECIFY_BVF, SPECIFY_HOMO_VDS and SPECIFY_HETERO_VDS parameters should be specified together");
                }
                if(sampleParameter.isSpecified()&&sampleParameter.isPassSpecify()){
                        this.setContainSpecifiedSample();
                }
                HashMap<String, SampleParameter> map = new HashMap<>();
                map.put(sampleParameter.getName(), sampleParameter);
                for (Map.Entry<String, SampleParameter> entry : map.entrySet()) {
                        this.bag.add(entry);
                }
        }
        public static String getIntermediateFilePath() {
                return intermediateFilePath;
        }
        public void printSNPNumberInSample(){
                for (Iterator<String> sampleNameIterator = this.getSamplesNamesList().iterator(); sampleNameIterator.hasNext();) {
                        SampleVariant sampleVariant = this.getSample(sampleNameIterator.next()).getSampleVariant();
                        edu.zju.common.CExecutor.println("\t\t"+sampleVariant.getName() + "\t"+String.valueOf(sampleVariant.getSNPCounts()));
                }
        }
        public HashSet<SampleParameter> getBag(){
                HashSet<SampleParameter> set= new HashSet<>();
                for (Map.Entry<String, SampleParameter> entry : bag) {
                        set.add(entry.getValue());
                }
                return set;
        }
        
         public HashMap<String, Integer> getSNPNumberInSample() {
                HashMap<String, Integer> snpNumber = new HashMap<>();
                for (Iterator<String> iterator = this.getSamplesNamesList().iterator(); iterator.hasNext();) {
                        String sampleName = iterator.next();
                        int number = this.getSample(sampleName).getSampleVariant().getSNPCounts();
                        snpNumber.put(sampleName, number);
                }
                return snpNumber;
        }      
        
        public void removeSNPInSamePosition(String chr, int position) {
                if (chr.equals("ChrSy") || chr.equals("ChrUn")) {
                        return;
                }
                for (Iterator<String> sampleNameIterator = this.getSamplesNamesList().iterator(); sampleNameIterator.hasNext();) {
                        SampleParameter sampleParameter=this.getSample(sampleNameIterator.next());
                        SampleVariant sampleVariant=sampleParameter.getSampleVariant();
                        sampleVariant.removeSNP(chr, position);
                        sampleParameter.setSampleVariant(sampleVariant);
                        this.updateSampleParameter(sampleParameter);
                }
        }        

        public void estimateStudyFir(GenomeEffectiveRegion genomeEffectiveRegion){
                for(SampleParameter sampleParameter:this.getBag()){
                        sampleParameter.estimateStudyFir(genomeEffectiveRegion);
                        this.updateSampleParameter(sampleParameter);
                }
        }
        public CandidateGeneBag getCandidateGeneBag(){
                return this.candidateGeneBag;
        }
        public void setSampleVariant(SampleVariantBag sampleVariantBag){
                for(String sampleName:this.getSamplesNamesList()){
                        SampleParameter sampleParameter=this.getSample(sampleName);
                        sampleParameter.setSampleVariant(sampleVariantBag.getSampleVariant(sampleName));
                        this.updateSampleParameter(sampleParameter);
                }
        }
        
        public void setSampleNamesList(LinkedList<String> list){
                this.sampleNamesList=list;
        }
        public LinkedList<String> getSamplesNamesList(){
                return this.sampleNamesList;
        }
        public SampleGeneBag getSampleGeneBag(){
                return this.sampleGeneBag;
        }
        public String dumpDataForTracing(){
                StringBuffer sb=new StringBuffer();
                sb.append("[SAMPLE_LIST]\n");
                for(String sampleName:this.getSamplesNamesList()){
                        sb.append(sampleName+"\n");
                }
                for(String sampleName:this.getSamplesNamesList()){
                        SampleParameter sampleParameter=this.getSample(sampleName);
                        sb.append("\n[SAMPLE]");
                        sb.append("\nSAMPLE_NAME:"+sampleParameter.getName());
                        try {
                                sb.append("\nSAMPLE.VCF:"+sampleParameter.getVCFFile().getFilePath());
                        } catch (Exception e) {
                        }
                        try {
                                sb.append("\nREADS_ALIGNMENT.SAM:"+sampleParameter.getSequenceReadsFile().getFilePath());
                        } catch (Exception e) {
                        }
                        sb.append("\nVAR_CALL_SCRIPT:"+sampleParameter.getCallerScript());
                        sb.append("\nSCORE_MATRIX:"+sampleParameter.getMatrix());
                        try {
                                sb.append("\nCONTROL:"+sampleParameter.getControlFile().getFilePath());
                        } catch (Exception e) {
                        }
                        sb.append("\nSCRIPT_MD5:"+sampleParameter.getScriptMd5());
                        sb.append("\nVCF_MD5:"+sampleParameter.getVCFMd5());
                        sb.append("\nSAM_MD5:"+sampleParameter.getSAMMd5());
                        sb.append("\nNUM_SIM_SNPS:"+sampleParameter.getArtificialSNPNumber());
                        sb.append("\nMAX_VAR_DENSITY:"+sampleParameter.getSNPDensity());
                        sb.append("\nESTIMATED_BVF:"+sampleParameter.getBackgroundMutationRateEstimated());
                        sb.append("\nESTIMATED_HOMO_VDS:"+sampleParameter.getHomoVariantDetectionSensitivityEstimated());
                        sb.append("\nESTIMATED_HETERO_VDS:"+sampleParameter.getHeteroVariantDetectionSensitivityEstimated());
                        sb.append("\nESTIMATED_HOMO_VCS:"+sampleParameter.getHomoVCSEstimated());
                        sb.append("\nESTIMATED_HETERO_VCS:"+sampleParameter.getHeteroVCSEstimated());
                }
                return sb.toString();
        }
        // related to whether to filter variant whethr to select candidate gene
        public boolean isContainSpecifiedSample(){
                return this.isContainSampleSpecified;
        }
        private void setContainSpecifiedSample(){
                this.isContainSampleSpecified=true;
        }
}
