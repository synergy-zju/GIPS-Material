/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.matrix;

import edu.zju.common.CExecutor;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author sss
 */
public class AA2Symbol {
        private HashMap<String,String> map=new HashMap<String,String> (){
                        {
                                put("Gly", "G"); //Glycine
                                put("Ala", "A"); //Alanine
                                put("Val", "V"); //Valine
                                put("Leu", "L"); //Leucine
                                put("Ile", "I"); //Isoleucine
                                put("Pro", "P"); //Proline
                                put("Phe", "F"); //Phenylalanine
                                put("Tyr", "Y"); //Tyrosine
                                put("Trp", "W"); //Tryptophan
                                put("Ser", "S"); //Serine
                                put("Thr", "T"); //Threonine
                                put("Cys", "C"); //Cystine
                                put("Met", "M"); //Methionine
                                put("Asn", "N"); //Asparagine
                                put("Gln", "Q"); //Glutarnine
                                put("Asp", "D"); //Asparticacid
                                put("Glu", "E"); //Glutamicacid
                                put("Lys", "K"); //Lysine
                                put("Arg", "R"); //Arginine
                                put("His", "H"); //Histidine  
                                put("Ter", "*"); //terminal codon
                        };

                };


        public String getAA(String symbol){
                String AA=null;
                for(Map.Entry<String,String> entry:this.map.entrySet()){
                        entry.getValue().equals(symbol);
                        AA=entry.getKey();break;
                }
                if(AA==null){
                      CExecutor.stopProgram("Do not find "+symbol+" in the AA2Symbol map");
                }
                return AA;
        }
        public String getSymbol(String AA){
                String symbol=this.map.get(AA);
                if(symbol==null){
                      CExecutor.stopProgram("Do not find "+AA+" in the AA2Symbol map");
                }
                return symbol;
        }
        
        
        
}
