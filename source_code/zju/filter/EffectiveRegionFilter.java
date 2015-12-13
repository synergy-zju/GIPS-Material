package edu.zju.filter;

import edu.zju.common.CExecutor;
import edu.zju.genome.effectiveRegion.GeneEffectiveRegion;
import edu.zju.genome.effectiveRegion.GenomeEffectiveRegion;
import edu.zju.parameter.SampleParameter;
import edu.zju.variant.SNP;
import edu.zju.variant.SampleVariant;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author zzx
 */
public class EffectiveRegionFilter extends FilterSuper{
    private GenomeEffectiveRegion genomeEffectiveRegion;//parameter will set this Variables when using filterFactory to create a filter

       public EffectiveRegionFilter(GenomeEffectiveRegion genomeEffectiveRegion,String strategy) {
               super(strategy);
              this.setGenomeEffectiveRegion(genomeEffectiveRegion);
       }
    
    private void setGenomeEffectiveRegion(GenomeEffectiveRegion genomeEffectiveRegion1){
           this.genomeEffectiveRegion=genomeEffectiveRegion1;
    }
    
    
        @Override
    SampleVariant filtrateSampleVariant(SampleVariant sampleVariant){
           
           LinkedList<SNP> sampleSNPs=sampleVariant.getSnps();
           LinkedList<SNP> filtratedSNPs=new LinkedList<>();
           SNP snp;
           //If effective region is set CDS, then the annotation about Exon will be removed
           //If CDS and Exon are both not set, then annotation about CDS,Exon will removed
           if(edu.zju.parameter.EffectiveRegionParameter.getEffectiveRegionParamString().contains("EXON(false)")){
                boolean isRemoveCDS=false;
                if(edu.zju.parameter.EffectiveRegionParameter.getEffectiveRegionParamString().contains("CDS(false)")){
                        isRemoveCDS=true;
                }
                for(Iterator<SNP> it=sampleSNPs.iterator();it.hasNext();){
                        SNP snptemp=it.next();
                        for(Iterator<edu.zju.variant.SNPAnnotation> annotationIterator=snptemp.getSNPAnnotations().iterator();annotationIterator.hasNext();){
                                edu.zju.variant.SNPAnnotation annotation=annotationIterator.next();
                                if(annotation.isExon()){
                                        annotationIterator.remove();
                                        continue;
                                }
                                if(isRemoveCDS&&annotation.isCoding()){
                                        annotationIterator.remove();
                                }
                        }
                }
           }
           // to filter snp        
           for(Iterator<SNP> it=sampleSNPs.iterator();it.hasNext();){
                  snp=it.next();
                  String chr=snp.getChr();
                  int pos=snp.getPosition();
                  boolean isRetained=false;
                  HashSet<String> geneNameSet=new HashSet<>();
                  if(snp.getSNPAnnotations().size()==0){
                        if(this.genomeEffectiveRegion.isInEffectiveRegion(chr, pos)){
                                          isRetained=true;
                        }
                  }
                  //find which gene the snp in
                  for(edu.zju.variant.SNPAnnotation snpa:snp.getSNPAnnotations()){
                          String geneName=snpa.getItsGeneName();
                          if(geneName==null||geneName.trim().length()==0){
                                  if(this.genomeEffectiveRegion.isInEffectiveRegion(chr,pos)){
                                          isRetained=true;
                                  }
                                  continue;
                          }
                          geneName=geneName.trim();
                          if(geneName.length()!=0)geneNameSet.add(geneName);
                  }
                  // find whether snp in gene effective region
                  for(String name:geneNameSet){
                          GeneEffectiveRegion geneEffectiveRegion = null;
                          try {
                                 geneEffectiveRegion=this.genomeEffectiveRegion.getChromosomeEffectiveRegion(chr).get1GeneEffectiveRegionByGeneName(name);
                          } catch (Exception e) {
                                  CExecutor.stopProgram("\nDo not find chromosome "+chr+" in GFF3 file");
                          }
                          
                          if(geneEffectiveRegion==null&&this.genomeEffectiveRegion.isInEffectiveRegion(chr, pos)){
                                  isRetained=true;
                                  break;
                          }else if(geneEffectiveRegion!=null){
                                  if(geneEffectiveRegion.isInGeneEffectiveRegion(pos)){
                                          isRetained=true;
                                          break;
                                  }
                          }
                  }
                  //remove non-coding annotation
                  Iterator<edu.zju.variant.SNPAnnotation> iterator=snp.getSNPAnnotations().iterator();
                  while(iterator.hasNext()){
                          edu.zju.variant.SNPAnnotation snpa=iterator.next();
                          if(snpa.isPassBigDfferenceFilter())iterator.remove();
                  }
                  //is snp has no annotations, it will be removed
                  if(isRetained){
                          filtratedSNPs.add(snp);
                  }
           }
           sampleVariant.renewSNPsInVariant(filtratedSNPs);
           return sampleVariant;
    }
        @Override
        SampleParameter filtrateSampleParameter(SampleParameter sampleParameter) {
           SampleVariant sampleVariant=sampleParameter.getSampleVariant();
           sampleVariant=this.filtrateSampleVariant(sampleVariant);
           sampleParameter.setSampleVariant(sampleVariant);
           return sampleParameter;
        }

    
}
