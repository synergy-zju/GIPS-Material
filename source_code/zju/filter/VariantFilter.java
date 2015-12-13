package edu.zju.filter;

import edu.zju.parameter.FilterParameter;
import edu.zju.parameter.GlobalParameter;
import edu.zju.parameter.SampleParameter;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.variant.SampleVariant;
import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class VariantFilter {
        private FilterParameter filterParameter;

        
        
        public VariantFilter(FilterParameter filterParameter) {
                this.filterParameter=filterParameter;
        }

        public SampleParameterBag filtrateAndEstimateStudyFir(SampleParameterBag sampleParameterBag){
                LinkedList<FilterSuper> filters=this.filterParameter.getFilter();
                edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"Filtering variants. Please wait.");
                for(FilterSuper filter: filters){
                        sampleParameterBag=filter.filterSampleVariantsInParameterBag(sampleParameterBag);
                        filter.printFiltering();                        
                        sampleParameterBag.printSNPNumberInSample();   
                }
                sampleParameterBag.estimateStudyFir(this.filterParameter.getGenomeEffectiveRegion());
                return sampleParameterBag;
        }  

         public SampleParameterBag estimateFunctionalFalseDiscoveryRate(SampleParameterBag sampleParameterBag){
                LinkedList<FilterSuper> filters=this.filterParameter.getFilter();
                for(String sampleName: sampleParameterBag.getSamplesNamesList()){
                        //swap
                        //take out true sample variant
                        SampleParameter sampleParameter=sampleParameterBag.getSample(sampleName);
                        SampleVariant sampleVariantInSample=sampleParameter.getSampleVariant();
                        //put in clinical variant
                        SampleVariant clinicalVariantSample=new GlobalParameter().getClinicalVariant();
                        sampleParameter.setSampleVariant(clinicalVariantSample);
                        int originalClinivalVariantNumber=clinicalVariantSample.getSNPCounts();
                        for(FilterSuper filter: filters){
                                String filterName=filter.getFilterName();
                                if(filterName.equals("Congestion")||filterName.equals("AncestryReference")||filterName.equals("Control")) continue;
                                sampleParameter=filter.filtrateSampleParameter(sampleParameter);
                        }        
                        int afterFilering=sampleParameter.getSampleVariant().getSNPCounts();
                        
                        sampleParameter.setFunctionalFIR((double)(originalClinivalVariantNumber-afterFilering)/originalClinivalVariantNumber);
                        //put back true sample variant
                        sampleParameter.setSampleVariant(sampleVariantInSample);
                }
                return sampleParameterBag;
        }        

        
}
