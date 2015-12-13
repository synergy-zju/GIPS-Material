package edu.zju.gips;

import edu.zju.genome.effectiveRegion.GeneEffectiveRegion;
import edu.zju.genome.effectiveRegion.GenomeEffectiveRegion;
import edu.zju.parameter.GlobalParameter;
import edu.zju.parameter.SampleParameter;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.variant.SNP;
import edu.zju.variant.SampleVariant;
import edu.zju.variant.SampleVariantBag;
import java.util.Iterator;

/**
 *
 ** @author Zhongxu Zhu
 */
public class CandidateGeneSelector {

        private GenomeEffectiveRegion genomeEffectiveRegion;
        private SampleVariantBag sampleVariantBag;
        private SampleGeneBag geneSignificanceBag = new SampleGeneBag(0);//to be selected
        private SampleParameterBag sampleParameterBag;

        public CandidateGeneSelector(GenomeEffectiveRegion genomeEffectiveRegion1, SampleParameterBag sampleParameterBag1, SampleGeneBag significanceBag) {
                this.setGenomeEffectiveRegion(genomeEffectiveRegion1);
                this.setSampleParameterBag(sampleParameterBag1);
                this.setGeneSignificanceBag(significanceBag);
        }
        private void setGenomeEffectiveRegion(GenomeEffectiveRegion genomeEffectiveRegion1) {
                this.genomeEffectiveRegion = genomeEffectiveRegion1;
        }
        private void setSampleParameterBag(SampleParameterBag sampleParameterBag1){
                this.sampleParameterBag=sampleParameterBag1;
        }
        private void setSampleVariantBag(SampleVariantBag sampleVariantBag1) {
                this.sampleVariantBag = sampleVariantBag1;
        }

        private void setGeneSignificanceBag(SampleGeneBag bag) {
                this.geneSignificanceBag = bag;
        }
        /**
         * Select gene that has variants in M of N samples.
         * M is the criteria to report candidate gene. N is total sample number.
         * @return 
         */
        public CandidateGeneBag selectCandidateGene() {
                int anticipation=GlobalParameter.getAnticipation();
                CandidateGeneBag candidateGeneBag = new CandidateGeneBag(anticipation);
                for (SampleGene candidateGeneToBeSelected : this.geneSignificanceBag.getSampleGenes()) {
                        GeneEffectiveRegion geneEffectiveRegion = this.genomeEffectiveRegion.getGeneEffectiveRegion(candidateGeneToBeSelected.getID());
                        for (Iterator<SampleParameter> sampleParameterIterator = this.sampleParameterBag.getBag().iterator(); sampleParameterIterator.hasNext();) {
                                SampleVariant sampleVariant = sampleParameterIterator.next().getSampleVariant();
                                try {
                                        for (Iterator<SNP> snpIterator = sampleVariant.get1ChromosomeSNPs(geneEffectiveRegion.getChrID()).iterator(); snpIterator.hasNext();) {
                                                SNP snp = snpIterator.next();
                                                if (geneEffectiveRegion.isInGeneEffectiveRegion(snp.getPosition())) {
                                                        // will check whether snp is correspond with gene
                                                        String geneName = candidateGeneToBeSelected.getGeneName().trim();
                                                        if (snp.getSNPAnnotations().size() != 0) {
                                                                if (!geneName.equals(snp.getSNPAnnotations().getFirst().getItsGeneName().trim())) {
                                                                        continue;
                                                                }
                                                        }
                                                        candidateGeneToBeSelected.addSNPInThisGene(snp);
                                                }
                                        }
                                } catch (Exception e) {
                                        continue;
                                }
                        }
                        if (candidateGeneToBeSelected.getSampleNumberHarboringMutationInThisGene() >= anticipation) {
                                candidateGeneToBeSelected.calculateSignificanceRegarlessOfAnticipation();
                                candidateGeneBag.addCandidateGene(candidateGeneToBeSelected);
                        }
                }
                return candidateGeneBag;
        }


}
