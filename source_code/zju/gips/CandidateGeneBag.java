package edu.zju.gips;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class CandidateGeneBag {

        private LinkedList<SampleGene> candidateGenes;
        private int candidateGeneNumber = 0;
        private int anticipation;

        public CandidateGeneBag(int anticipation) {
                this.candidateGenes = new LinkedList<>();
        }

        public void addCandidateGene(SampleGene candidateGene) {
                this.candidateGenes.add(candidateGene);
                this.candidateGeneNumber = this.candidateGeneNumber + 1;
        }

        public LinkedList<SampleGene> getCandidateGenes() {
                LinkedList<SampleGene> list = new LinkedList<>();
                Collections.sort(this.candidateGenes, new Comparator<SampleGene>() {
                        public int compare(SampleGene s1, SampleGene s2) {
                                int value = 0;
                                if (s1.getSignificanceRegardlessOfAnticipation() < s2.getSignificanceRegardlessOfAnticipation()) {
                                        value = -1;
                                } else if (s1.getSignificanceRegardlessOfAnticipation() == s2.getSignificanceRegardlessOfAnticipation()) {
                                        value = 0;
                                } else if (s1.getSignificanceRegardlessOfAnticipation() > s2.getSignificanceRegardlessOfAnticipation()) {
                                        value = 1;
                                }
                                return value;
                        }
                });
                return this.candidateGenes;
        }

        private void setAnticipation(int anticipation) {
                this.anticipation = anticipation;
        }

        public int getCandidateGeneNumber() {
                return this.candidateGeneNumber;
        }
}
