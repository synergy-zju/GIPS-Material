package edu.zju.gips;

import java.util.LinkedList;

/**
 *
 * @author Zhongxu Zhu
 */
public class SampleGeneBag {

        private int anticipation;
        private double expectedGeneNumberInGenome = 0;
        private LinkedList<SampleGene> genes;
        private int sampleGeneNumber = 0;

        public SampleGeneBag(int anticipation) {
                this.genes = new LinkedList<>();
                this.setAnticipation(anticipation);
        }

        public SampleGeneBag() {
                this.genes = new LinkedList<>();
        }

        public void addGene(SampleGene geneToBeSelected) {
                this.genes.add(geneToBeSelected);
                this.sampleGeneNumber = this.sampleGeneNumber + 1;
        }

        public double getSignificance(String geneID) {
                for (SampleGene sampleGeneToBeSelected : this.genes) {
                        if (sampleGeneToBeSelected.getID().equals(geneID)) {
                                return sampleGeneToBeSelected.getSignificanceRegardlessOfAnticipation();
                        }
                }
                return -1;
        }

        public void setAnticipation(int anticipation) {
                this.anticipation = anticipation;
        }

        public int getAnticipation() {
                return this.anticipation;
        }

        /**
         * expect Gene Number In Sample is equal the sum total significance of
         * all genes.
         */
        private void addGeneExpectionNumber(double geneSignificance) {
                this.expectedGeneNumberInGenome = this.expectedGeneNumberInGenome + geneSignificance;
        }

        public double getRandomGeneDiscoveryNumberInGenome() {
                for (SampleGene sampleGene : this.genes) {
                        this.expectedGeneNumberInGenome = this.expectedGeneNumberInGenome + sampleGene.getRandomGeneDiscoveryNumberAtAnticipation();
                }
                return edu.zju.common.NumeralHandler.setScale(this.expectedGeneNumberInGenome);
        }

        LinkedList<SampleGene> getSampleGenes() {
                return this.genes;
        }

        int getSampleGeneNumber() {
                return this.sampleGeneNumber;
        }
}
