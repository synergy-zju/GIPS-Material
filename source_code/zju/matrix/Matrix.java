/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.zju.matrix;

import java.util.HashMap;

/**
 *
 * @author zzx
 */
public class Matrix {
       private HashMap<String, Integer > matrix;
       private Codon2AA codon2AA ;

       /**
        * BLOSUM100,BLOSUM100.50,BLOSUM30,BLOSUM30.50,BLOSUM35,BLOSUM35.50,BLOSUM40,BLOSUM40.50,BLOSUM45,BLOSUM45.50,BLOSUM50,BLOSUM50.50,
        * BLOSUM55,BLOSUM55.50,BLOSUM60,BLOSUM60.50,BLOSUM62,BLOSUM62.50,BLOSUM65,BLOSUM65.50,BLOSUM70,BLOSUM70.50,BLOSUM75,BLOSUM75.50,
        * BLOSUM80,BLOSUM80.50,BLOSUM85,BLOSUM85.50,BLOSUM90,BLOSUM90.50,BLOSUMN,BLOSUMN.50,DAYHOFF,GONNET,PAM10,PAM100,PAM110,PAM120,
        * PAM130,PAM140,PAM150,PAM160,PAM170,PAM180,PAM190,PAM20,PAM200,PAM210,PAM220,PAM230,PAM240,PAM250,PAM260,PAM270,PAM280,PAM290,
        * PAM30,PAM300,PAM310,PAM320,PAM330,PAM340,PAM350,PAM360,PAM370,PAM380,PAM390,PAM40,PAM400,PAM410,PAM420,PAM430,PAM440,PAM450,
        * PAM460,PAM470,PAM480,PAM490,PAM50,PAM500,PAM60,PAM70,PAM80,PAM90,BUILD 
        * @param matrixName 
        */
       public Matrix(String matrixName) {
              this.matrix= edu.zju.file.Config.getMatrix(matrixName);
              //codon2AA = new Codon2AA("Standard");
       }
       private void putScore(String orginAAAndMutatedAA,int score){
              this.matrix.put(orginAAAndMutatedAA, score);
       }
       /**
        * You can input the three letters codon or one letter amino acid abbreviation
        * @param originAA
        * @param mutatedAA
        * @return 
        */
       public int getScore(String originAA,String mutatedAA){
              String temp=originAA+mutatedAA;
              if(!this.matrix.keySet().contains(temp)){
                      return 2;
                     //origin * to mutation ? (eg. codon lost) 
                     //common.CExecutor.println("Don't find the score of "+originAA+" mutated to "+mutatedAA);
              }
              return this.matrix.get(temp);
       }
       public static boolean isMatrixName(String matrixName){
               if(edu.zju.file.Config.getMatrix(matrixName)==null){
                       edu.zju.common.CExecutor.stopProgram("Don't find "+matrixName+" in config file");
                       return false;
               }else return true;
       }
       
}
