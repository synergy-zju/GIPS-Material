package edu.zju.genome.effectiveRegion;

import edu.zju.genome.gffGenome.Genome;
import edu.zju.parameter.EffectiveRegionParameter;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author zzx
 */
public class GenomeEffectiveRegion extends edu.zju.genome.abstractGenome.Genome{
    private HashMap<String,ChromosomeEffectiveRegion> chromosomeEffectiveRegion;
    private int effectiveRegionLength=0;
    private boolean ready=false;
    private int maxPosition=0;
    private static EffectiveRegionParameter effectiveRegionParameter=null;//static

        public GenomeEffectiveRegion(EffectiveRegionParameter regionParameter,Genome genome) {
                super(genome);
              effectiveRegionParameter = regionParameter;
              chromosomeEffectiveRegion = new HashMap<>();                       
              this.setGenomeEffectivetRegion(genome);
        }
       /**
        * split genome into chromosomes, and pass chromosome information
        * @param regionParameter
        * @param genome 
        */
    private void setGenomeEffectivetRegion(Genome genome){
           edu.zju.common.CExecutor.print(edu.zju.common.CExecutor.getRunningTime()+"Building genome effective region, please wait ");
           int total=0;
           ChromosomeEffectiveRegion chromosomeEffectiveRegion;
           for(String chrID:genome.getChrIDs()){
                  chromosomeEffectiveRegion = new ChromosomeEffectiveRegion(genome.getChromosome(chrID));
                  this.addChromosomeEffectiveRegion(chromosomeEffectiveRegion);
                  total=total+chromosomeEffectiveRegion.getLength();
                  edu.zju.common.CExecutor.print("*");
           }
           edu.zju.common.CExecutor.println("");
           this.setEffectiveRegionLength(total);
           this.ready=true;
           edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"Effective region: "+this.getChrIDs()+"\n");
    }
    /**
     * 
     * @param chromosomeEffectiveRegion an object of chromosomeEffectiveRegion
     */
    private void addChromosomeEffectiveRegion(ChromosomeEffectiveRegion chromosomeEffectiveRegion){
           this.chromosomeEffectiveRegion.put(chromosomeEffectiveRegion.getID(), chromosomeEffectiveRegion);
//           common.CExecutor.println(chromosomeEffectiveRegion.getID()+" effective region is recieved "+(new String((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(new Date()))));
           if(this.getMaxPosition()<chromosomeEffectiveRegion.getMaxPosition()) this.setMaxPosition(chromosomeEffectiveRegion.getMaxPosition());
    }
    /**
     * 
     * @return return the effective region total length in this genome 
     */
    public int getLengt(){
           return this.effectiveRegionLength;
    }
    public int getMaxPosition(){
           return this.maxPosition;
    }
    private void setMaxPosition(int pos){
           this.maxPosition=pos;
    }
    /**
     * 
     * @param chrNum chromosome ID  
     * @return an object of chromosomeEffectiveRegion
     */
    public ChromosomeEffectiveRegion getChromosomeEffectiveRegion(String chrID){
            if(!this.chromosomeEffectiveRegion.keySet().contains(chrID)){
                    throw new NullPointerException();
            }
            return chromosomeEffectiveRegion.get(chrID);
    }
    /**
     * 
     * @return all objects of chromosomeEffectiveRegion
     */
    public HashMap<String, ChromosomeEffectiveRegion> getChromosomeEffectiveRegions(){
           return this.chromosomeEffectiveRegion;
    }
    /**
     * to estimate genome effective region is builded or not
     * @return 
     */
    public boolean isReady(){
           return this.ready;
    }

    /**
     * 
     * @param length total effective region length in genome 
     */
    private void setEffectiveRegionLength(int length){
           this.effectiveRegionLength=length;
    }

    public boolean isInEffectiveRegion(String chrID ,int position){
           if(!this.getChrIDs().contains(chrID)){
                   if(this.getChrIDs().contains("Chr"+chrID))chrID="Chr"+chrID;
                   else {
                           edu.zju.common.CExecutor.println("Do not find chromosome "+chrID+"; please check whether your chromosome id in vcf file is comsistent with chromosome id in gff file");
                           return false;
                   }
           }
           return this.getChromosomeEffectiveRegion(chrID).isInEffectiveRegion(position);    
           //Chr may not in this effective region , for instance "ChrUn" "ChrSy"???
    }
    
    public LinkedList<Entry<String,Integer>> getGeneEffectiveRegionLength(){
           HashMap<String,Integer> geneEffectiveRegionLength = new HashMap<>();
           for(Iterator<String> it=this.getChromosomeEffectiveRegions().keySet().iterator();it.hasNext();){
                  ChromosomeEffectiveRegion chromosomeEffectiveRegion=this.getChromosomeEffectiveRegions().get(it.next());
                  geneEffectiveRegionLength.putAll(chromosomeEffectiveRegion.getGeneEffectiveRegionLengthInChromosome());
           }
           LinkedList<Map.Entry<String,Integer>> sortedList= new LinkedList<>(geneEffectiveRegionLength.entrySet());
           Collections.sort(sortedList,new Comparator<Map.Entry<String,Integer>>(){
                  @Override
                  public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                         return o1.getKey().compareTo(o2.getKey());
                  }
                  
           });
           return sortedList;
    }
    public Set<String> getChromosomesID(){
           return this.chromosomeEffectiveRegion.keySet();
    }
    public Set<String> getGeneNames(){
          TreeSet<String> nameSet= new TreeSet<>();
          for(Iterator<String> iterator=this.getChromosomesID().iterator();iterator.hasNext();){
               nameSet.addAll(this.getChromosomeEffectiveRegion(iterator.next()).getGenesID());  
          } 
          return nameSet;
    }
    public GeneEffectiveRegion getGeneEffectiveRegion(String geneID){
          for(Iterator<String> iterator=this.getChromosomesID().iterator();iterator.hasNext();){
                 String chrID=iterator.next();
                 GeneEffectiveRegion geneEffectiveRegion= this.getChromosomeEffectiveRegion(chrID).get1GeneEffectiveRegionByGeneID(geneID);
                 if(geneEffectiveRegion!=null) return geneEffectiveRegion;
          } 
          return null;           
    }
    static EffectiveRegionParameter getEffectiveRegionParameter(){
            return effectiveRegionParameter;
    }
    
    public LinkedList<GeneEffectiveRegion> getGeneEffectiveRegions(){
            LinkedList<GeneEffectiveRegion> list= new LinkedList<>();
            for(String chr:this.getChrIDs()){
                    list.addAll(this.getChromosomeEffectiveRegion(chr).getChrGeneEffectiveRegions());
            }
            return list;
    }
    
    
}
