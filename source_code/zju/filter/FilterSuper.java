package edu.zju.filter;

import edu.zju.parameter.SampleParameter;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.variant.SampleVariant;
import java.util.LinkedList;

/**
 *
 * @author zzx
 */
public abstract class FilterSuper {
    private String filterStrategy;  
    
    
        public FilterSuper(String stragtegy) {
                this.filterStrategy=stragtegy;
        }
       
    
    
    void printFiltering(){
            edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"After "+this.filterStrategy+" filter, retained variants:");
    }
     
    public SampleParameterBag filterSampleVariantsInParameterBag(SampleParameterBag sampleParameterBag){
                SampleParameterBag bag=sampleParameterBag;  
                LinkedList<String> sampleNameList=bag.getSamplesNamesList();
                for(String sampleName:sampleNameList){
                       SampleParameter sampleParameter=bag.getSample(sampleName);
                       sampleParameter=this.filtrateSampleParameter(sampleParameter);
                       bag.updateSampleParameter(sampleParameter);
                }
                return bag;   
    }
    abstract SampleParameter filtrateSampleParameter(SampleParameter sampleParameter);
    
    
    
    
    
    public String getFilterName(){
            return this.filterStrategy;
    }
    abstract SampleVariant filtrateSampleVariant(SampleVariant sampleVariant);


}
