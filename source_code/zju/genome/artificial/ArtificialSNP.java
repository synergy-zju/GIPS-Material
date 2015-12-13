package edu.zju.genome.artificial;

/**
 * Artificial SNP is used in simulating.
 * @author zzx
 */
public class ArtificialSNP {
    private String chr;//chromosome id
    private int position;//position in chromosome
//    private String ref;//reference
    private String genotype;//genotype information

    public ArtificialSNP(String chrID, int position,String genotype) {
           this.setChr(chrID);
           this.setPosition(position);
           this.setGenotype(genotype);
    }
    
    private void setChr(String chr){
           this.chr=chr;
    }
    public String getChr(){
           return this.chr;
    }
    private void setPosition(int position){
           this.position=position;
    }
    public int getPostion(){
           return this.position;
    }

    private void setGenotype(String genotype){
           this.genotype=genotype;
    }

    public String getGenotype(){
           return this.genotype;
    }


}
