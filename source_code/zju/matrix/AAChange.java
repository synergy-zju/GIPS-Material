package edu.zju.matrix;

/**
 *
 ** @author Zhongxu Zhu
 */
public class AAChange {

       private String originAA;
       private String mutationAA;
       
       public void setOriginCodon(String AA){
              this.originAA=AA;
       }
       public void setMutationCodon(String AA){
              this.mutationAA=AA;
       }

       public String getOriginAA(){
              return this.originAA;
       }
       public String getMutationAA(){
              return this.mutationAA;
       }
    
}
