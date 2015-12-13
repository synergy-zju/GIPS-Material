package edu.zju.genome.effectiveRegion;

import edu.zju.genome.gffGenome.Gene;
import edu.zju.genome.gffGenome.Module;
import edu.zju.genome.gffGenome.Transcript;
import edu.zju.parameter.EffectiveRegionParameter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author zzx
 */
public class GeneEffectiveRegion extends edu.zju.genome.abstractGenome.Gene{
    //Effective regions are stored according to module.See set gene effective region     
    private HashMap<String,LinkedList<int[]>> effectiveRegionsInGene;
    private int effectiveRegionLength;
    private EffectiveRegionParameter regionParameter;
    
    public GeneEffectiveRegion(Gene gene) {
           super(gene);
           this.setRegionParameter(GenomeEffectiveRegion.getEffectiveRegionParameter());
           this.effectiveRegionsInGene = new HashMap<>();
           this.setGeneEffectiveRegion(gene); 
    }//
    
    private void setEffectiveRegionsInGene(LinkedList<int[]> effectiveRegions,String type){
           this.effectiveRegionsInGene.put(type, effectiveRegions);

    }
    /**
     * count length when ending setEffectiveRegionsInGene
     */
    private void countLength(){
            PositionContainer positionContainer= new PositionContainer();
            positionContainer.add2Container(this.getEffectiveRegionsInGene());
            this.effectiveRegionLength=positionContainer.getNoRepeatPositionLength();
            positionContainer.cleanContainer();
    }
    public int getEffectiveRegionLength(){
           return this.effectiveRegionLength;
    }
    
    //region contains: CDS,Exon,5UTR,3UTR,Promoter,SpliceSite correspond to EffectiveRgionParameter
    private void setGeneEffectiveRegion(Gene gene){
           PositionContainer positionContainer=new PositionContainer();
           if(this.regionParameter.isConsider5UTR()) {
                   positionContainer=new PositionContainer();
                   positionContainer.add2Container(gene.get1ModuleRegions("five_prime_UTR"));
                   this.setEffectiveRegionsInGene(positionContainer.converterPointPosition2Region(), "5UTR");
           }             
           if(this.regionParameter.isConsider3UTR()) {
                   positionContainer=new PositionContainer();
                   positionContainer.add2Container(gene.get1ModuleRegions("three_prime_UTR"));
                   this.setEffectiveRegionsInGene(positionContainer.converterPointPosition2Region(), "3UTR");
           }
           if(this.regionParameter.isConsiderCDS()) {
                   positionContainer=new PositionContainer();
                   positionContainer.add2Container(gene.get1ModuleRegions("CDS"));
                   this.setEffectiveRegionsInGene(positionContainer.converterPointPosition2Region(), "CDS");
           }
           if(this.regionParameter.isConsiderExon()) {
                   positionContainer=new PositionContainer();
                   positionContainer.add2Container(gene.get1ModuleRegions("exon"));
                   this.setEffectiveRegionsInGene(positionContainer.converterPointPosition2Region(), "Exon");
           }
           if(this.regionParameter.getPromoterLength()!=0) {
                   positionContainer=new PositionContainer();
                   positionContainer.add2Container(this.getPromoterRegions(this.regionParameter.getPromoterLength(), gene));
                   this.setEffectiveRegionsInGene(positionContainer.converterPointPosition2Region(), "Promoter");           
           }
           if (this.regionParameter.getSpliceSite()!=0) {
                   positionContainer=new PositionContainer();
                   positionContainer.add2Container(this.getSpliceSite(regionParameter.getSpliceSite(), gene));
                   this.setEffectiveRegionsInGene(positionContainer.converterPointPosition2Region(), "SpliceSite");
           }
           positionContainer.cleanContainer();
           this.countLength();
    }
       /**
        * region[0] is the small position.
        * @param moduleType
        * @param gene
        * @return 
        */    
    private LinkedList<int[]> getPromoterRegions(int length,Gene gene){
           LinkedList<int[]> regions= new LinkedList<>(); 
           LinkedList<int[]> five_prime_utr_region;
           five_prime_utr_region=gene.get1ModuleRegions("five_prime_UTR");
           if(five_prime_utr_region.size()==0){
                   five_prime_utr_region=gene.get1ModuleRegions("transcript");
           }
           for (Iterator<int[]> it = five_prime_utr_region.iterator(); it.hasNext();) {
                  int[] region = it.next();
                  if(gene.getStrand().equals("-")){
                         int start=region[1];
                         region[0]=start+1;
                         region[1]=start+length;
                  }
                  else{
                         int start=region[0];
                         region[0]=start-length;                         
                         region[1]=start-1;
                  }
                  regions.add(region);
           }
           return regions;
    } 
    /**
     * 
     * @param length 1<length<=3
     * @param gene information about gene annotation
     * @return return all splice site
     */
    private LinkedList<int[]>getSpliceSite(int length,Gene gene){
           LinkedList<int[]> regions = new LinkedList<>();
           for(Transcript transcript: gene.getTranscripts()){
                  if(transcript.get1Module("exon").size()>1){
                          int number=1;
                          int former[] = new int[2],latter[]=new int[2];
                          for(Module exon: transcript.get1Module("exon")){
                                 int []spliceSite=  new int[2];                                
                                 if(number==1){
                                        former=exon.getSite();
                                        number=number+1;
                                 }
                                 else{
                                      spliceSite[0]=former[1]+1;
                                      spliceSite[1]=former[1]+length;
                                      regions.add(spliceSite);
                                      spliceSite=new int[2];
                                      latter=exon.getSite();
                                      spliceSite[0]=latter[0]-length;
                                      spliceSite[1]=latter[0]-1;
                                      regions.add(spliceSite);
                                      former=latter;
                                 }
                          }      
                  }              
           }
           return regions;
    }
    /**
     * 
     * @return all the effective regions in this gene
     */
    public LinkedList<int[]> getEffectiveRegionsInGene(){
           LinkedList<int[]> list= new LinkedList<>();
           for(String temp: this.effectiveRegionsInGene.keySet()){
                   list.addAll(this.effectiveRegionsInGene.get(temp));
           }
           return list;
    }
    /**
     * 
     * @param regionParameter region parameter is used to detect which region to be selected
     */
    private void setRegionParameter(EffectiveRegionParameter regionParameter){
           this.regionParameter=regionParameter;
    }
    public boolean isInGeneEffectiveRegion(int pos){
           for(Iterator<int[]> iterator=this.getEffectiveRegionsInGene().iterator();iterator.hasNext();){
                  int [] region=iterator.next();
                  if(pos>=region[0]&&pos<=region[1]){
                         return true;
                  }
           }
           return false;
    }
    public LinkedList<int[]> get1ModelRegion(String type){
            return this.effectiveRegionsInGene.get(type);
    }
    
}
