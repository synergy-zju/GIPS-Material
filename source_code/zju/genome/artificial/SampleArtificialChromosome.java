package edu.zju.genome.artificial;

import java.util.HashSet;

/**
 *
 ** @author Zhongxu Zhu
 */
public class SampleArtificialChromosome extends edu.zju.genome.abstractGenome.Chromosome{

       private HashSet<ArtificialSNP> artificialSNPs;
       private int artificialSNPNumber;
       public SampleArtificialChromosome(String ID) {
              super(ID);
              artificialSNPs = new HashSet<>();
              artificialSNPNumber = 0;
       }
    /**
     * 
     * @return total artificial snp number in this chromosome
     */  
    public int getArtificialSNPNumber(){
           return this.artificialSNPNumber;
    }  
    private void setArtificialSNPNumber(int number){
           this.artificialSNPNumber=number;
    }
    /**
     * 
     * @return all artificial snp in this chromosome
     */
    public HashSet<ArtificialSNP> getArtificialSNPs(){
           return this.artificialSNPs;
    }
    /**
     * the artificial snp number plus 1 while adding
     * @param artificialSNP 
     */
    public void addArtificialSNP(ArtificialSNP artificialSNP){
           this.artificialSNPs.add(artificialSNP);
           this.setArtificialSNPNumber(this.getArtificialSNPNumber()+1);
    } 
    /**
     * set or update this artificialSNP set
     * @param artificialSNPs1 
     */
    private void setArtificialSNPs(HashSet<ArtificialSNP> artificialSNPs1){
           this.artificialSNPs=artificialSNPs1;
    } 
    protected boolean isArtificialSNP(String chr,int pos,String type){
            //System.out.println(chr+","+pos+","+type);
            boolean is=false;
            for(ArtificialSNP artificialSNP:this.getArtificialSNPs()){
                    if(artificialSNP.getPostion()!=pos)continue;
                    if(!artificialSNP.getGenotype().equals(type))continue;
                    is=true;break;
            }
            return is;
    }
    
}
