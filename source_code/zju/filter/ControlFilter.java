package edu.zju.filter;

import edu.zju.file.CommonInputFile;
import edu.zju.parameter.SampleParameter;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.variant.SampleSNP;
import edu.zju.variant.SampleVariant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class ControlFilter extends FilterSuper{
        private CommonInputFile variantsControlFile;
        private boolean isConsiderGenotypeSameWithControl=false;
        private boolean isRemoveHomo=true;
        private boolean isRemoveHetero=true;
        private HashMap<String,HashSet<Integer>> studyPosition2Filter;
        
        
        public ControlFilter(String stragtegy, int mode) {
                super(stragtegy);
                this.setMode(mode);
        }     
        
        @Override
        SampleVariant filtrateSampleVariant(SampleVariant sampleVariant) {
                String line;
                String sampleName=sampleVariant.getName();
                while((line=this.variantsControlFile.readLine())!=null){
                        if(line.startsWith("#"))continue;
                        if(line.trim().isEmpty()) continue;
                        edu.zju.variant.SNP controlSNP=null;
                        controlSNP=new SampleSNP(line, sampleName);
                        edu.zju.variant.SNP sampleSNP=sampleVariant.get1SNP(controlSNP.getChr(), controlSNP.getPosition());
                        String controlGenotype=controlSNP.getGenotype();
                        if(sampleSNP==null){
                                continue;
                        }
                        if(!sampleSNP.getAlt().equals(controlSNP.getAlt())){
                                continue;
                        }
                        if(this.isConsiderGenotypeSameWithControl){
                                if(!sampleSNP.getGenotype().equals(controlGenotype)){
                                        continue;
                                }
                                if(!((controlGenotype.equals("1/1")&&this.isRemoveHomo)||
                                        (controlGenotype.equals("0/1")&&this.isRemoveHetero))){
                                       continue; 
                                }
                        }else {//genotype equals "-1" means there is no GT information
                                if(!(controlGenotype.equals("-1")||(controlGenotype.equals("1/1")&&this.isRemoveHomo)||
                                       (controlGenotype.equals("0/1")&&this.isRemoveHetero))){
                                       continue; 
                                        
                                }
                        }
                        sampleVariant.removeSNP(controlSNP.getChr(), controlSNP.getPosition());
                        this.addStudyPositionToFilter(controlSNP.getChr(), controlSNP.getPosition());
                }
                this.variantsControlFile.closeInput();
                return sampleVariant;
        }
        /**
         *                                        rmHomo&rmHeter    rmHomo    rmHeter 
         *  considerGenotypeSameWithControl             1             2          3
         *  don't consider                              4             5          6
         * 
         * 
         * @param mode This function is not used in current version
         */
        private void setMode(int mode){
                switch (mode) {
                        case 0 : break;
                        case 1 : this.isConsiderGenotypeSameWithControl=true;
                                 this.isRemoveHomo=true;
                                 this.isRemoveHetero=true;break;
                        case 2 : this.isConsiderGenotypeSameWithControl=true;
                                 this.isRemoveHomo=true;
                                 this.isRemoveHetero=false;break;        
                        case 3 : this.isConsiderGenotypeSameWithControl=true;
                                 this.isRemoveHomo=false;
                                 this.isRemoveHetero=true;break; 
                        case 4 : this.isConsiderGenotypeSameWithControl=false;
                                 this.isRemoveHomo=true;
                                 this.isRemoveHetero=true;break;        
                        case 5 : this.isConsiderGenotypeSameWithControl=false;
                                 this.isRemoveHomo=true;
                                 this.isRemoveHetero=false;break;         
                        case 6 : this.isConsiderGenotypeSameWithControl=false;
                                 this.isRemoveHomo=false;
                                 this.isRemoveHetero=true;break;         
                }
        }

        @Override
        SampleParameter filtrateSampleParameter(SampleParameter sampleParameter) {
                this.variantsControlFile=sampleParameter.getControlFile();
                if(this.variantsControlFile==null) return sampleParameter;
                SampleVariant sampleVariant=sampleParameter.getSampleVariant();
                sampleVariant=this.filtrateSampleVariant(sampleVariant);
                sampleParameter.setSampleVariant(sampleVariant);
                sampleParameter.addStudyPositionToFilter(this.studyPosition2Filter);
                this.studyPosition2Filter=new HashMap<>();
                return sampleParameter;
        }
 
        private void addStudyPositionToFilter(String chr, int pos){
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
         
}
