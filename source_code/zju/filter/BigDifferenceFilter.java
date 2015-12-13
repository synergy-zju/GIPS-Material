package edu.zju.filter;

import edu.zju.matrix.AAChange;
import edu.zju.matrix.Matrix;
import edu.zju.parameter.SampleParameter;
import edu.zju.variant.SNP;
import edu.zju.variant.SNPAnnotation;
import edu.zju.variant.SampleVariant;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author zzx
 */
public class BigDifferenceFilter extends FilterSuper{
       private String matrixName;
       private edu.zju.matrix.Matrix matrix;
       private int maxAAsimilarityScore;
       
       
       public BigDifferenceFilter(String strategy) {
              super(strategy);
       }  

        private void setMatrix(String matrixName){
               edu.zju.common.CExecutor.println("\t\tMatrix: "+matrixName);
               this.matrixName=matrixName;
               matrix= new Matrix(this.matrixName);
        }
        private void setMaxAASimilarityScore(int score){
                this.maxAAsimilarityScore=score;
        }
        @Override
        SampleVariant filtrateSampleVariant(SampleVariant sampleVariant){
                       
               LinkedList<SNP> sampleSNPs=sampleVariant.getSnps();
               LinkedList<SNP> filtratedSNPs=new LinkedList<>();
               LinkedList<SNPAnnotation> snpAnnotation ;
               SNP snp;
               int i=0;
               for(Iterator<SNP> it=sampleSNPs.iterator();it.hasNext();){
                      snp=it.next();
                      //retain all indels
                      if(snp.isIndel()) {
                          filtratedSNPs.add(snp);
                          continue;
                      }
                      snpAnnotation = snp.getSNPAnnotations();
                      int originAnnotationNumber=snpAnnotation.size();
                      for(Iterator<SNPAnnotation> iterator=snpAnnotation.iterator();iterator.hasNext();){
                             SNPAnnotation annotation=iterator.next();
                             if(annotation.isPassBigDfferenceFilter()) {
                                 continue;
                             }
                             if(annotation.isHighRisk()) {
                                 continue;
                             }
                             if(annotation.isCoding()){
                                    AAChange codonChange =annotation.getCodonChange();
                                    String originCodon=codonChange.getOriginAA();
                                    String mutationCodon=codonChange.getMutationAA();
                                    int score=this.matrix.getScore(originCodon, mutationCodon);
                                    if(score>this.maxAAsimilarityScore){
                                           iterator.remove();
                                    }
                             }
                      }
                      int finalAnnotationNumber=snpAnnotation.size();
                      if(originAnnotationNumber==0||finalAnnotationNumber>0) {
                          filtratedSNPs.add(snp);
                      }
                              
               }
               sampleVariant.renewSNPsInVariant(filtratedSNPs);
               return sampleVariant;
        }

        @Override
        SampleParameter filtrateSampleParameter(SampleParameter sampleParameter) {
               this.setMatrix(sampleParameter.getMatrix());
               this.setMaxAASimilarityScore(sampleParameter.getMaxAASimilarityScore());
               SampleVariant sampleVariant=sampleParameter.getSampleVariant();
               sampleVariant=this.filtrateSampleVariant(sampleVariant);
               sampleParameter.setSampleVariant(sampleVariant);
               return sampleParameter;
        }


}
