package edu.zju.genome.gffGenome;

/**
 *
 ** @author Zhongxu Zhu
 */
public abstract class Module {
    private String type;
    private int []site=new int[2];//array length : 2
    private String ID;//module id 
    private String parent;        

    public Module(String ModuleID,String feature,String parent,int startPosition ,int endPositon) {//generate function ,  id is required
           this.setID(ModuleID);
           this.setPosition(startPosition, endPositon);
           this.setParent(parent);
           this.setFeatureType(feature);
    }    
    private void setFeatureType(String type){
           this.type=type;
    }
     /**
     * 
     * @return mRNA,CDS ...and so on 
     */
    public String getFeatureType(){
           return FeatureType.featureNormalize(this.type);
    } 

    private void setID(String moduleID){
           this.ID=moduleID;
    }    
    /**
     * 
     * @return return the ID of this module
     */
    public String getID(){
           return this.ID;
    }


    /**
     * set  start position and end position 
     * @param startPosition 
     * @param endPosistion 
     */
    private void setPosition(int startPosition,int endPosistion){
           if (startPosition<=0||endPosistion<=0) {
                  edu.zju.common.CExecutor.println(this.getID()+" position is not right!");
           }
           this.site[0]=startPosition;
           this.site[1]=endPosistion;
    }
    /**
     * 
     * @return the site of this module
     */
    public int[] getSite(){           
           return this.site;
    }

    private void setParent(String parent){
           this.parent= parent;
    }
    public String getParent(){
           return this.parent;
    }        
        
        
}
