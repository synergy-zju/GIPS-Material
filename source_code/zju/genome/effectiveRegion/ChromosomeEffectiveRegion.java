package edu.zju.genome.effectiveRegion;

import edu.zju.genome.gffGenome.Chromosome;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/**
 *
 * @author zzx
 */
public class ChromosomeEffectiveRegion extends edu.zju.genome.abstractGenome.Chromosome{
    private LinkedList<int[]> effectiveRegionsInChromosome;
    private HashMap<String,GeneEffectiveRegion> geneEffectiveRegions;
    private HashMap<String,String> geneName2GeneID;
    private int effectiveRegionLength=0;
    private int maxPosition;
    
       public ChromosomeEffectiveRegion(Chromosome chromosome) {
              super(chromosome);
              this.effectiveRegionsInChromosome= new LinkedList<>();
              this.geneEffectiveRegions = new HashMap<>();
              this.geneName2GeneID = new HashMap<>();
              this.setChromosome(chromosome);

       }
    /**
     * split chromosome into lots of gene and pass gene to select effective region
     */
    private void setChromosome(Chromosome chromosome){
           this.setEffectiveRegionsLegth(0);
           String geneID=null;
           GeneEffectiveRegion geneEffectiveRegion;
           for (Iterator<String> it = chromosome.getGeneIDs().iterator(); it.hasNext();) {
                  geneID = it.next();
                  geneEffectiveRegion = new GeneEffectiveRegion( chromosome.getGene(geneID));
                  this.addGeneEffectiveRegion(geneEffectiveRegion);
           } 
           this.updateEffectiveRegions();
           this.countLength();
    }
    private void addGeneEffectiveRegion(GeneEffectiveRegion geneEffectiveRegion){
           this.geneEffectiveRegions.put(geneEffectiveRegion.getID(), geneEffectiveRegion);
           this.geneName2GeneID.put(geneEffectiveRegion.getGeneName(), geneEffectiveRegion.getID());
    }
    /**
     * when all the genes effective region are added to this chromosome, update the effective region in this chromosome
     * that means this will trim overlap region between two gene
     */
    private void updateEffectiveRegions(){
           PositionContainer positionContainer = new PositionContainer();
           for(Iterator<String> it=geneEffectiveRegions.keySet().iterator();it.hasNext();){
                  positionContainer.add2Container(geneEffectiveRegions.get(it.next()).getEffectiveRegionsInGene());
           }
           this.setRegions(positionContainer.converterPointPosition2Region());
           this.setMaxPosition(this.getRegionsInChromosome().getLast()[1]);
           positionContainer.cleanContainer();
    } 
    private void setMaxPosition(int position){
           this.maxPosition=position;
    }
    public int getMaxPosition(){
           return this.maxPosition;
    }
    private void setRegions(LinkedList<int[]> regions){
           this.effectiveRegionsInChromosome=regions;
    }
    /*
     * count length when ending setEffectiveRegionsInGene  
     */
    private void countLength(){
           int length=0;
           for (Iterator<int[]> it = this.getRegionsInChromosome().iterator(); it.hasNext();) {
                  int[] region = it.next();
                  length=length+region[1]-region[0]+1;
           }
           this.setEffectiveRegionsLegth(length);
    }
    /**
     * 
     * @return effective region length in this chromosome
     */
    public int getLength(){
           return this.effectiveRegionLength;
    }
    /**
     * 
     * @return all effective regions in chromosome (none overlap)
     */
    public LinkedList<int[]> getRegionsInChromosome(){
           return this.effectiveRegionsInChromosome;
    }
    public HashSet<Integer> getEffectiveRegionPositionsInChr(){
        HashSet<Integer> set= new HashSet<>();
        for(int[] region:this.effectiveRegionsInChromosome){
            for(int i=region[0];i<=region[1];i++){
                set.add(i);
            }
        }
        return set;
    }    
    private void setEffectiveRegionsLegth(int length){
           this.effectiveRegionLength=length;
    }


    public boolean isInEffectiveRegion(int position){
           for(Iterator<int[]> iterator=this.getRegionsInChromosome().iterator();iterator.hasNext();){
                  int [] region= new int[2];
                  region=iterator.next();
                  if(position>=region[0]&&position<=region[1]){
                         return true;
                  }
           }
           return false;
    }
    protected HashMap<String,GeneEffectiveRegion> getGeneEffectiveRegions(){
           return this.geneEffectiveRegions;
    }
    protected LinkedList<GeneEffectiveRegion> getChrGeneEffectiveRegions(){
            LinkedList<GeneEffectiveRegion> list=new LinkedList<>(this.geneEffectiveRegions.values());
           Collections.sort(list,new Comparator<GeneEffectiveRegion>(){
                  @Override
                  public int compare(GeneEffectiveRegion o1, GeneEffectiveRegion o2) {
                         return o1.getID().compareTo(o2.getID());
                  }
           });            
            return list;
    }
    protected HashMap<String,Integer> getGeneEffectiveRegionLengthInChromosome(){
           HashMap<String,Integer> lengthes = new HashMap<>();
           for(Iterator<String> it=this.getGenesID().iterator();it.hasNext();){
                  String geneName=it.next();
                  lengthes.put(geneName,this.get1GeneEffectiveRegionByGeneID(geneName).getEffectiveRegionLength());
           }
           return lengthes;
    }
    public Set<String> getGenesID(){
           return this.geneEffectiveRegions.keySet();
    }
    public GeneEffectiveRegion get1GeneEffectiveRegionByGeneID(String geneID){
           if(this.getGenesID().contains(geneID.trim())){
                  return this.geneEffectiveRegions.get(geneID.trim());
           }else {
                   return null;
           }
    }
    public GeneEffectiveRegion get1GeneEffectiveRegionByGeneName(String geneName){
           String geneID=this.geneName2GeneID.get(geneName);
           if(geneID==null) return null;
           else return this.get1GeneEffectiveRegionByGeneID(geneID);
    }    
}
