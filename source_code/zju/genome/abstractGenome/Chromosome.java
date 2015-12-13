package edu.zju.genome.abstractGenome;

/**
 *
 ** @author Zhongxu Zhu
 */
public abstract class Chromosome {
        private String ID;

        public Chromosome(Chromosome chromosome) {
                this.setID(chromosome.getID());
        }

        public Chromosome(String chrID) {
                this.setID(chrID);
        }
        
        private void setID(String id){
                this.ID=id;
        }
        public String getID(){
                return this.ID;
        }   
}
