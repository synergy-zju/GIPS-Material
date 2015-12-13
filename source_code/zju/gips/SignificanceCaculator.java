package edu.zju.gips;

import java.math.BigDecimal;
import java.util.Iterator;

/**
 * GIPS iterative calculation 
 ** @author Zhongxu Zhu
 */
public class SignificanceCaculator {

        private double tempPool[][];
        private int sampleNumber;
        private int anticipation;
        private Iterator<SampleGene> sampleGeneIterator;

        public SignificanceCaculator(int sampleNumber) {
                this.setSampleNumber(sampleNumber);
        }

        public SignificanceCaculator(int sampleNumber, Iterator<SampleGene> iterator) {
                this.sampleGeneIterator = iterator;
                this.setSampleNumber(sampleNumber);
        }
        //This is used to calculate expected gene discovery number
        public double calculateGenesSignificance(int anticipation, double[] backgroundMutationRate, int geneEffectiveRegionLength) {
                double[][] geneMutationRate = new double[this.sampleNumber][2];
                for (int i = 0; i < this.sampleNumber; i++) {
                        geneMutationRate[i][1] = (double) Math.pow(1 - backgroundMutationRate[i], geneEffectiveRegionLength);//gene has no mutation
                        geneMutationRate[i][0] = 1 - geneMutationRate[i][1];//0: the probability of mutation
                }
                //note whether the following line is move to run() function
                this.tempPool = new double[this.sampleNumber + 1][this.sampleNumber + 1];
                for (int i = 0; i < this.sampleNumber + 1; i++) {
                        for (int j = 0; j < this.sampleNumber + 1; j++) {
                                this.tempPool[i][j] = -1;
                        }
                }
                double probability = 0;
                for (int i = this.sampleNumber; i >= anticipation; i--) {
                        //this.setAnticipation(i);
                        probability = this.caculateAtSpecificAnticipation(i, sampleNumber, geneMutationRate) + probability;
                }
                return probability;
        }

        public double calculateTrueTargetGeneDiscoveriedProbability(int anticipation, double detectingRate[]) {
                double[][] detectingProbability = new double[this.sampleNumber][2];
                for (int i = 0; i < this.sampleNumber; i++) {
                        detectingProbability[i][0] = detectingRate[i];
                        detectingProbability[i][1] = 1 - detectingRate[i];
                }
                this.tempPool = new double[this.sampleNumber + 1][this.sampleNumber + 1];
                for (int i = 0; i < this.sampleNumber + 1; i++) {
                        for (int j = 0; j < this.sampleNumber + 1; j++) {
                                this.tempPool[i][j] = -1;
                        }
                }
                double probability = 0;
                for (int i = this.sampleNumber; i >= anticipation; i--) {
                        this.setAnticipation(i);
                        probability = this.caculateAtSpecificAnticipation(this.anticipation, this.sampleNumber, detectingProbability) + probability;
                }
                return probability;
        }

        public double calculate1Phenotype1GeneValidation(int number, double detectingRate[]) {
                if (number == this.sampleNumber) {
                        return 1;
                }
                double[][] detectingProbability = new double[this.sampleNumber][2];
                for (int i = 0; i < this.sampleNumber; i++) {
                        detectingProbability[i][0] = detectingRate[i];
                        detectingProbability[i][1] = 1 - detectingRate[i];
                }
                this.tempPool = new double[this.sampleNumber + 1][this.sampleNumber + 1];
                for (int i = 0; i < this.sampleNumber + 1; i++) {
                        for (int j = 0; j < this.sampleNumber + 1; j++) {
                                this.tempPool[i][j] = -1;
                        }
                }
                // double probability=0;
                BigDecimal bigDecimal = new BigDecimal(0);
                for (int i = number; i >= 0; i--) {
                        this.setAnticipation(i);
                        //probability=this.caculateAtSpecificAnticipation(this.anticipation, this.sampleNumber, detectingProbability)+probability;
                        bigDecimal = bigDecimal.add(new BigDecimal(this.caculateAtSpecificAnticipation(this.anticipation, this.sampleNumber, detectingProbability)));
                }
                // return probability;        
                return bigDecimal.doubleValue();
        }

        private double caculateAtSpecificAnticipation(int anticipation, int sampleNumber, double[][] geneMutationRate) {
                double calculate_result = 1;
                if (this.tempPool[anticipation][sampleNumber] != -1) {
                        return calculate_result = this.tempPool[anticipation][sampleNumber];
                } else if (anticipation == sampleNumber) {
                        for (int i = this.sampleNumber - sampleNumber; i < this.sampleNumber; i++) {
                                calculate_result = calculate_result * geneMutationRate[i][0];
                        }
                        this.tempPool[anticipation][sampleNumber] = calculate_result;
                        return calculate_result;
                } else if (anticipation == 0 && sampleNumber > 0) {
                        for (int i = this.sampleNumber - sampleNumber; i < this.sampleNumber; i++) {
                                calculate_result = calculate_result * geneMutationRate[i][1];
                        }
                        this.tempPool[anticipation][sampleNumber] = calculate_result;
                        return calculate_result;
                } else {
                        calculate_result = geneMutationRate[this.sampleNumber - sampleNumber][0] * this.caculateAtSpecificAnticipation(anticipation - 1, sampleNumber - 1, geneMutationRate) + geneMutationRate[this.sampleNumber - sampleNumber][1] * this.caculateAtSpecificAnticipation(anticipation, sampleNumber - 1, geneMutationRate);
                        this.tempPool[anticipation][sampleNumber] = calculate_result;
                        return calculate_result;
                }
        }

        void setAnticipation(int i) {
                this.anticipation = i;
        }

        private void setSampleNumber(int number) {
                this.sampleNumber = number;
        }

        private int getSampleNumber() {
                return this.sampleNumber;
        }
}
