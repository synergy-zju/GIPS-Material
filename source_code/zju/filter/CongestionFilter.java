package edu.zju.filter;

import edu.zju.file.Config;
import edu.zju.parameter.SampleParameter;
import edu.zju.variant.SNP;
import edu.zju.variant.SampleVariant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class CongestionFilter extends FilterSuper{
        private int windowSize=0;
        private int snpDensity;
        private HashMap<String,HashSet<Integer>> studyPosition2Filter;
        
        public CongestionFilter(String stragtegy) {
                super(stragtegy);
                this.windowSize=Integer.parseInt(Config.getItem("WINDOW"))-1;
        }
        
        @Override
        SampleVariant filtrateSampleVariant(SampleVariant sampleVariant){
                LinkedList<SNP> snps= new LinkedList<>();
                for(String chrName:sampleVariant.getChrNameInSequence()){
                        LinkedList<SNP> tempSnps=sampleVariant.get1ChromosomeSNPs(chrName);
                        if(tempSnps==null||tempSnps.size()==0) continue; 
                        snps.addAll(this.filtrateChromosome(tempSnps));
                }
                sampleVariant.renewSNPsInVariant(snps);
                return sampleVariant;
        }        

        private LinkedList<SNP> filtrateChromosome(LinkedList<SNP> chrSNP){
               HashSet<Integer> effectiveRegionPositions=null;
                LinkedList<SNP> highDensitySnps=new LinkedList<>();
                Iterator<SNP> snpIterator=chrSNP.iterator();
                while(snpIterator.hasNext()){
                        int density=1;
                        SNP previousSNP=snpIterator.next();
                        if(!previousSNP.isIndel())continue;
                        for(SNP innerSNP:chrSNP){
                                int window=Math.abs(previousSNP.getPosition()-innerSNP.getPosition());
                                if(window<=this.windowSize/2&&window!=0){
                                        density++;
                                }
                        }
                        if(density>=this.snpDensity) {
                                highDensitySnps.add(previousSNP);                              
                        }
                }
                snpIterator=highDensitySnps.iterator();
                LinkedList<SNP> rmSnps= new LinkedList<>();
                while(snpIterator.hasNext()){
                        SNP previousSNP=snpIterator.next();
                        if(!rmSnps.contains(previousSNP))rmSnps.add(previousSNP);
                        for(SNP innerSNP:chrSNP){
                                int window=Math.abs(previousSNP.getPosition()-innerSNP.getPosition());
                                if(window<=this.windowSize/2&&window!=0){
                                        if(!rmSnps.contains(innerSNP)) rmSnps.add(innerSNP);
                                }     
                        }
                }
                for(SNP snp:rmSnps){
                        if(chrSNP.contains(snp)) {
                                this.addStudyPositionToFilter(snp.getChr(),snp.getPosition());
                                chrSNP.remove(snp);
                        }
                }
                effectiveRegionPositions=null;
                return chrSNP;
        }

        private void setDensity(int density){
                if(density<2){
                       edu.zju.common.CExecutor.stopProgram("Variant density should be more than or equale 2");
                }else this.snpDensity=density;
        }
        @Override
        SampleParameter filtrateSampleParameter(SampleParameter sampleParameter) {
                this.setDensity(sampleParameter.getSNPDensity());
                SampleVariant sampleVariant=this.filtrateSampleVariant(sampleParameter.getSampleVariant());
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
