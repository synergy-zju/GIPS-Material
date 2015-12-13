package edu.zju.matrix;

import java.util.HashMap;

/**
 *
 * @author zzx
 */
public class Codon2AA {
       
       HashMap<String, String> codon2AA=new HashMap<>();
       
       public Codon2AA(String codonType) {
               String line=edu.zju.file.Config.getItem("codon."+codonType);
               String split[]=line.split(";");
               for(int i=0;i<split.length;i++){
                      String codon=split[i].split(",")[0].trim().toString();
                      String AA=split[i].split(",")[1].trim().toString();
                      this.putCodon(codon, AA); 
                }
        }
       private void putCodon(String codon,String aminoAcid){
              this.codon2AA.put(codon, aminoAcid);
       }
       public final String getAminoAcid(String codon){
              if(!this.codon2AA.keySet().contains(codon)) {
                       return null;
               }else {
                       return this.codon2AA.get(codon);
               }
       }

    
}
