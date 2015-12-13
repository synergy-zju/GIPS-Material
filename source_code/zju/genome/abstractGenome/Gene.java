package edu.zju.genome.abstractGenome;

/**
 *
 ** @author Zhongxu Zhu
 */
public abstract class Gene {
        private String chrID;
        private String ID;
        private String name;
        private String strand;

        public Gene(Gene gene) {
                this.setChrID(gene.getChrID());
                this.setGeneName(gene.getGeneName());
                this.setID(gene.getID());
                this.setStrand(gene.getStrand());
        }
        public Gene(String geneID,String geneName,String chrID,String strand) {
                this.setID(geneID);
                if(geneName==null){
                        edu.zju.common.CExecutor.stopProgram("Can't recognize gene name for "+ geneID);
                }else{
                        this.setGeneName(geneName);
                }
                this.setStrand(strand);
                this.setChrID(chrID);
        }        
        
        
        private void setChrID(String id){
                this.chrID=id;
        }
        private void setID(String id){
                this.ID=id;
        }
        private void setGeneName(String name){
                this.name=name.trim();
        }
        
        public String getChrID(){
                return this.chrID;
        }
        public String getID(){
                return this.ID;
        }
        public String getGeneName(){
                if (this.name==null||this.name.equals("")) this.name="NULL";
                return this.name;
        }
        private void setStrand(String strand){
           this.strand=strand;
        }
        public String getStrand(){
                return this.strand;
        }
}
