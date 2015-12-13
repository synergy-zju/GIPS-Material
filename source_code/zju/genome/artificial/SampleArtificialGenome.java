package edu.zju.genome.artificial;

import edu.zju.genome.abstractGenome.Genome;
import java.util.HashMap;

/**
 *
 * @author zzx
 */
public class SampleArtificialGenome extends edu.zju.genome.abstractGenome.Genome{
    private HashMap<String,SampleArtificialChromosome> sampleArtificialChromosomes; 
    private int artificialSNPNumber;
    private String SampleName;
        public SampleArtificialGenome(Genome g,String sampleName) {
                super(g);
                this.setSampleName(sampleName);
                this.artificialSNPNumber=0;
                this.sampleArtificialChromosomes= new HashMap<>();
                for(String chrName: this.getChrIDs()){
                        SampleArtificialChromosome artificialChromosome = new SampleArtificialChromosome(chrName);
                        this.addSampleArtificialChromosme(artificialChromosome);
                }
        
        }
      
    public void addArtificialSNP(ArtificialSNP artificialSNP){
           SampleArtificialChromosome sampleArtificialChromosome = this.getSampleArtificialChromosome(artificialSNP.getChr().trim().toString()) ;
           sampleArtificialChromosome.addArtificialSNP(artificialSNP);
           this.artificialSNPNumber=this.artificialSNPNumber+1;
           this.updateSampleArtificialChromosome(sampleArtificialChromosome);
    }  
    public int getArtificialSNPNumber(){
           return this.artificialSNPNumber;
    }
    private void addSampleArtificialChromosme(SampleArtificialChromosome sampleArtificialChromosome1){
           this.sampleArtificialChromosomes.put(sampleArtificialChromosome1.getID(), sampleArtificialChromosome1);
    }
    private void updateSampleArtificialChromosome(SampleArtificialChromosome sampleArtificialChromosome1){
           this.sampleArtificialChromosomes.put(sampleArtificialChromosome1.getID(), sampleArtificialChromosome1);
    }
    public SampleArtificialChromosome getSampleArtificialChromosome(String chrID){
           return this.sampleArtificialChromosomes.get(chrID);
    }
    private void setSampleName(String name){
            this.SampleName=name;
    }
    String getSampleName(){
            return this.SampleName;
    }
    public boolean isArtificialSNP(String chr,int pos,String type){
            if(type==null)return false;
            boolean is=false;            
            SampleArtificialChromosome sampleArtificialChromosome=this.getSampleArtificialChromosome(chr);
            is=sampleArtificialChromosome.isArtificialSNP(chr, pos, type);
            return is;
    }

}
