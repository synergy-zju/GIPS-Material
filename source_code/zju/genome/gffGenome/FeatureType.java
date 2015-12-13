package edu.zju.genome.gffGenome;

import java.util.HashSet;

/**
 *
 ** @author Zhongxu Zhu
 */
class FeatureType {
        private static HashSet<String> exonFeature= new HashSet<String>(){
                {
                        add("exon");
                        add("C_gene_segment");
                        add("V_gene_segment");
                        add("J_gene_segment");
                        add("D_gene_segment");
                };
        };
        private static HashSet<String> transcriptFeature =new HashSet<String>(){
                {
                        add("transcript");
                        add("mRNA");
                }   
        };
        /**
         * If this feature is exon, then return the feature type "exon".
         * @param feature
         * @return 
         */
        static protected String featureNormalize(String feature){
                if(exonFeature.contains(feature)) return "exon";
                if(transcriptFeature.contains(feature)) return "transcript";
                return feature;
        }

}
