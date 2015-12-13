package edu.zju.genome.abstractGenome;

import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public abstract class Genome {
        private int chrNumber;
        private LinkedList<String> chrIDs;
        private String organism;
        
        public Genome(Genome g)
        {       
                this.chrIDs=g.getChrIDs();
                this.setChrNumber(g.getChrNumber());
                this.setOrganismName(g.getOrganismName());
        }
        
        public Genome(String organism){
              this.organism=organism; 
              this.chrIDs=new LinkedList<>();
        }
        protected void setChrNumber(int i){
                this.chrNumber=i;
        }
        protected void chrNumPlusOne(){
                this.chrNumber=this.chrNumber+1;
        }
        public int getChrNumber(){
                return this.chrNumber;
        }
        protected void addChrID(String chrName){
                this.chrIDs.add(chrName);
        }
        public LinkedList<String> getChrIDs(){
                return this.chrIDs;
        }
        private void setOrganismName(String organism){
                this.organism=organism;
        }
        public String getOrganismName(){
                return this.organism;
        }
        
        
}
