package edu.zju.parameter;

import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class ParameterList {
        private LinkedList<String> globalParaList;
        private LinkedList<String> sampleSpecificParaList;

        public ParameterList() {
                initiateParaList();
        }
        public boolean isInSampleSpecificItemList(String item){
                for(String line:sampleSpecificParaList){
                        if(line.trim().startsWith(item))return true;
                }
                edu.zju.common.CExecutor.stopProgram("Faile to load "+item);
                return false;
                
        }
        public boolean isInGlobParaList(String item){
                for(String line: globalParaList){
                        if(line.trim().startsWith(item))return true;
                }
                edu.zju.common.CExecutor.stopProgram("Faile to load "+item);
                return false;
        }
        
        public LinkedList<String> getGlobalParaList(){
                LinkedList<String> temp=new LinkedList<>();
                for(int i=0;i<12;i++){
                        temp.add(this.globalParaList.get(i));
                }
                return temp;
        }
        public LinkedList<String> getSampleBasicParaList(){
                LinkedList<String> temp=new LinkedList<>();
                temp.add("  ");
                for(int i=0;i<4;i++){
                        temp.add(this.sampleSpecificParaList.get(i));
                }
                return temp;
        }
        
        void initiateParaList(){
               this.globalParaList = new LinkedList<String>() {
                        {
                                add("[GLOBAL]");
                                add("PROJECT :                     ");
                                add("REF_GENOME_ANNOTATION.GFF :");                                
                                add("SNPEFF_GENOME_VERSION     :  ");
                                add("SNPEFF  :    ");
                                add("CANDIDATE_CRITERIA :    ");
                                add("VAR_CALL_SCRIPT :   ");
                                add("EFF_REGION  : CDS|SpliceSite=2  ");
                                add("VAR_FILTERS :  BA   ");
                                add("SCORE_MATRIX: DEFAULT");
                                add("MAX_AA_SCORE: 0");
                                add("NUM_SIM_SNPS : 5000");
                                add("MAX_VAR_DENSITY   : 3");
                                add("LIB_PHENOTYPE_VAR :");
                                add("LIB_VAR_SNPEFF_GENOME_VERSION:");
                                add("LIB_GENOME_ANNOTATION.GFF :");
                                add("CONTROL :");
                        };
                };  
                
               this.sampleSpecificParaList=new LinkedList<String>(){
                       {
                               add("[SAMPLE]");
                               add("SAMPLE_NAME :");
                               add("SAMPLE.VCF  :");
                               add("READS_ALIGNMENT.SAM :");
                               add("SPECIFY_HOMO_VDS    :");
                               add("SPECIFY_HETERO_VDS  :");
                               add("SPECIFY_BVF :");
                               add("VAR_CALL_SCRIPT :");
                               add("SCORE_MATRIX:");
                               add("MAX_AA_SCORE: ");
                               add("CONTROL :");
                               add("NUM_SIM_SNPS :");
                               add("MAX_VAR_DENSITY :");
                               add("ESTIMATED_HOMO_VDS :");
                               add("ESTIMATED_HETERO_VDS:");
                               add("ESTIMATED_HOMO_VCS :");
                               add("ESTIMATED_HETERO_VCS:");  
                               add("ESTIMATED_BVF:");
                               add("VCF_MD5:");
                               add("SAM_MD5");
                               add("SCRIPT_MD5");
                       };
               };
                    
        }         
}
