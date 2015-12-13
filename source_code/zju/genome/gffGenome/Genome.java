package edu.zju.genome.gffGenome;

import edu.zju.common.LineHandler;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 *
 * @author zzx
 */
public class Genome extends edu.zju.genome.abstractGenome.Genome{
    private HashMap<String,Chromosome> chromosomes;
    private boolean ready=false;//
    private int genomeLength=0;//to be used
    
        public Genome(String organism) {
                super(organism);
        }
        public Genome(String organism,LinkedList<String> genomeInformation) {
                super(organism);
           chromosomes=new HashMap<>();           
           this.setGenome(genomeInformation); 
        }
        
        
    /**
     * record whether this genome is already builded
     * @return 
     */
    public boolean isReady(){
           return this.ready;
    }
    
    /**
     * input a LinkedList<String> of gff3 formate file, build a genome according to genomeinformation
     * @param genomeInformation 
     */
    private void setGenome(LinkedList<String> genomeInformation){
           String chrID = null ;//chr id from 1 to maxID, 1 is the first chr id
           LinkedList<String> chromosomeInformation =new LinkedList<>();
           String line = null;
           LineHandler lh =new LineHandler();
           for (Iterator<String> it = genomeInformation.iterator(); it.hasNext();) {
                  line=it.next();
                  lh.splitByTab(line);
                  if(chrID==null) chrID=lh.linesplit[0].trim().toString();
                  //if(lh.linesplit[2].trim().toString().equals("region")) continue;
                  //if(lh.linesplit[2].trim().toString().equals("chromosome")) continue;
                  if (!chrID.equals(lh.linesplit[0].trim().toString())) {
                         Chromosome chromosome=new Chromosome(chrID,chromosomeInformation);
                         this.addChromosome(chromosome);
                         chromosomeInformation=new LinkedList<>();
                         chromosomeInformation.add(line);//add another chr information to the new LinkedList
                         chrID=lh.linesplit[0].trim().toString();//change chr id
                  } else chromosomeInformation.add(line); 
                  it.remove();
           }
           Chromosome chromosome=new Chromosome(chrID,chromosomeInformation);
           this.addChromosome(chromosome);//add the last chr into genome
           edu.zju.common.CExecutor.println("\n"
                   +edu.zju.common.CExecutor.getRunningTime()+ "GFF annotation: "+this.getChrIDs());
           chromosomeInformation.clear();//release
           this.ready=true;//after build this genome ,set ready=true
//           common.CExecutor.println("---------------------------------------------------");
    }    
    
    /**
     * waiting for calling after one chromosome is builded
     * @param chromosome 
     */
    private void addChromosome(Chromosome chromosome){
            //please note chr id 
           if(chromosome.getID().trim().isEmpty()||chromosome.getID().contains("ChrUn")||chromosome.getID().contains("ChrSy")){
                   
           }else{
                   this.chromosomes.put(chromosome.getID(), chromosome);
                this.chrNumPlusOne();
                this.addChrID(chromosome.getID());
//           common.CExecutor.println(chromosome.getID()+" annotation is loaded successfully"+"  "+common.CExecutor.getRunningTime());
           }
    }
    /**
     * 
     * @param chromosomeNum chromosomeNum stands for chr id
     * @return return a chromosome according to id
     */
    public Chromosome getChromosome(String chromosomeID){
           if(!this.chromosomes.containsKey(chromosomeID)){
                  edu.zju.common.CExecutor.println("There is no "+chromosomeID);
                  return null;
           }
           return this.chromosomes.get(chromosomeID);
    }
    /**
     * 
     * @return all the chromosomes in genome ,from 1 to the max 
     */
    public LinkedList<Chromosome> getChromosomes(){
           LinkedList<Chromosome> chromosomes = new LinkedList<>();
           for(String chrName:this.getChrIDs()){
                  chromosomes.add(this.getChromosome(chrName));
           }
           return chromosomes;
    }
    
    
}
