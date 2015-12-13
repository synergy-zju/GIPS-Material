package edu.zju.gips;

import edu.zju.common.NumeralHandler;
import edu.zju.genome.effectiveRegion.GeneEffectiveRegion;
import edu.zju.parameter.GlobalParameter;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.variant.SNP;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class SampleGene extends edu.zju.genome.abstractGenome.Gene {

        private LinkedList<SNP> snps;
        private double significance;
        private int effectiveRegionLength;
        private double significanceAtAnticipation;

        public SampleGene(GeneEffectiveRegion gene) {
                super(gene);
                this.snps = new LinkedList<>();
                this.effectiveRegionLength = gene.getEffectiveRegionLength();
        }

        public SampleGene(String geneID, String geneName, String chrID, String strand) {
                super(geneID, geneName, chrID, strand);
        }

        public void addSNPInThisGene(SNP snp) {
                this.snps.add(snp);
        }

        public void setSignificanceRegardlessOfAnticipation(double significance) {
                this.significance = significance;
        }

        public void setSignificanceAtAnticipation(double significance) {
                this.significanceAtAnticipation = significance;
        }
        public double getSignificanceRegardlessOfAnticipation(){
                return NumeralHandler.setScale(5, this.significance);
        }
        public void calculateSignificanceRegarlessOfAnticipation() {
                SignificanceCaculator significanceCaculator = new SignificanceCaculator(SampleParameterBag.getSampleNumber());
                double significance = significanceCaculator.calculateGenesSignificance(this.getSampleNumberHarboringMutationInThisGene(), GIPS.sampleBackgroundMutationRate, this.effectiveRegionLength);
                this.significance = significance;
        }
        
        
        public double calculateSignificanceAtAnticipation(double[] geneMutationRate){
                SignificanceCaculator significanceCaculator = new SignificanceCaculator(SampleParameterBag.getSampleNumber());
                double significance = significanceCaculator.calculateGenesSignificance(GlobalParameter.getAnticipation(), geneMutationRate, this.effectiveRegionLength);
                this.setSignificanceAtAnticipation(significance);
                return significance;
        }
        
        
        
        
        public int getSNPNumber() {
                return this.snps.size();
        }

        public LinkedList<SNP> getSNPInThisCandidateGene() {
                Collections.sort(this.snps, new Comparator<SNP>() {
                        @Override
                        public int compare(SNP o1, SNP o2) {
                                return o1.getSampleName().compareTo(o2.getSampleName());
                        }
                });
                return this.snps;
        }

        /**
         * regardless of genotype, just return the sample number that harboring
         * mutation in this gene
         *
         * @return
         */
        public int getSampleNumberHarboringMutationInThisGene() {
                HashSet<String> sampleNameSet = new HashSet<>();
                for (SNP snp : this.snps) {
                        sampleNameSet.add(snp.getSampleName().toString());
                }
                int i = sampleNameSet.size();
                return i;
        }

        /**
         * Mutation has 2 types,one is homozygous,and the other is heterozygous.
         * Return the sample number has the same mutation(homo or hetero ) in
         * one gene
         *
         * @param genotype
         * @return
         */
        protected int getSampleNumberHarboringMutationInThisGene(String genotype) {
                HashSet<String> sampleNameSet = new HashSet<>();
                boolean heteroMark = false;
                for (SNP snp : this.snps) {
                        String temp = snp.getGenotype();
                        if (genotype.equals("Homo") && (temp.equals("1/1"))) {
                                sampleNameSet.add(snp.getSampleName().toString());
                                continue;
                        }
                        if (genotype.equals("Hetero")) {
                                sampleNameSet.add(snp.getSampleName().toString());
                                if (temp.equals("0/1") || temp.equals("1/2")) {
                                        heteroMark = true;
                                }
                                continue;
                        }

                }
                if (genotype.equals("Hetero") && !heteroMark) {
                        return 0;
                }
                return sampleNameSet.size();
        }

        void resetSNPsInCandidateGene() {
                this.snps = new LinkedList<>();
        }

        public int getEffectiveRegionLength() {
                return this.effectiveRegionLength;
        }

        public double getRandomGeneDiscoveryNumberAtAnticipation() {
                return NumeralHandler.setScale(5, this.significanceAtAnticipation);
        }
}
