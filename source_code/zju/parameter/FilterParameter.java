package edu.zju.parameter;

import edu.zju.common.LineHandler;
import edu.zju.file.CommonInputFile;
import edu.zju.file.FileFactory;
import edu.zju.filter.AncestryReferenceFilter;
import edu.zju.filter.BigDifferenceFilter;
import edu.zju.filter.CongestionFilter;
import edu.zju.filter.ControlFilter;
import edu.zju.filter.EffectiveRegionFilter;
import edu.zju.genome.effectiveRegion.GenomeEffectiveRegion;
import java.util.LinkedList;

/**
 *
 ** @author Zhongxu Zhu
 */
public class FilterParameter {

        private LinkedList<String> filterStrategy;
        private GenomeEffectiveRegion genomeEffectiveRegion;
        private boolean isNeedEffectiveRegion = false;
        private edu.zju.genome.gffGenome.Genome genome;
        private String genomeName;
        private EffectiveRegionParameter regionParameter;
        private boolean isNeedEffectiveRegionParameter = false;
        private int snpDensity = 10;
        private CommonInputFile variantControl = null;
        private int controlFilterMode=0;

        public FilterParameter() {
                this.filterStrategy = new LinkedList<>();
                this.isNeedEffectiveRegion = true;
                this.isNeedEffectiveRegionParameter = true;
        }


        public LinkedList<edu.zju.filter.FilterSuper> getFilter() {
                LinkedList<edu.zju.filter.FilterSuper> filters = new LinkedList<>();
                //the following determine the order of filter

                if (this.filterStrategy.contains("AncestryReference")) {
                        filters.add(new AncestryReferenceFilter("AncestryReference",this.getGenomeEffectiveRegion().getLengt()));
                }
                if (this.filterStrategy.contains("EffectiveRegion")) {
                        filters.add(new EffectiveRegionFilter(this.getGenomeEffectiveRegion(), "EffectiveRegion"));
                }
                if (this.filterStrategy.contains("BigDifference")) {
                        filters.add(new BigDifferenceFilter("BigDifference"));
                }
                if (this.filterStrategy.contains("Congestion")) {
                        filters.add(new CongestionFilter("Congestion"));
                }
                //Control filter must be added, but maybe not used in real variant filtration
                filters.add(new ControlFilter("Control",this.controlFilterMode));
                return filters;
        }

        public void setFilterStrategy(String info) {
                info=info.replace("|", "");
                info=info+"E";
                StringBuffer stringTemp = new StringBuffer();
                if (info.contains("A")) {
                        stringTemp.append("A");
                }
                if (info.contains("C")) {
                        stringTemp.append("C");
                }
                if (info.contains("E")) {
                        stringTemp.append("E");
                }
                if (info.contains("B")) {
                        stringTemp.append("B");
                }

                info = stringTemp.toString();
                this.isNeedEffectiveRegion = false;
                this.isNeedEffectiveRegionParameter = false;
                for (int i = 0; i < info.toString().length(); i++) {
                        String filtrationStrategy = String.valueOf(info.toCharArray()[i]).toUpperCase().toString();
                        switch (filtrationStrategy) {
                                case "E": {
                                        if (this.filterStrategy.contains("EffectiveRegion")) {
                                                break;
                                        }
                                        this.filterStrategy.add("EffectiveRegion");
                                        this.isNeedEffectiveRegionParameter = true;
                                        this.isNeedEffectiveRegion = true;
                                        break;
                                }
                                case "A": {
                                        if (this.filterStrategy.contains("AncestryReference")) {
                                                break;
                                        }
                                        this.filterStrategy.add("AncestryReference");
                                        break;
                                }
                                case "B": {
                                        if (this.filterStrategy.contains("BigDifference")) {
                                                break;
                                        }
                                        this.filterStrategy.add("BigDifference");
                                        break;
                                }
                                case "C": {
                                        if (this.filterStrategy.contains("Congestion")) {
                                                break;
                                        }
                                        this.filterStrategy.add("Congestion");
                                        break;
                                }
                                default: {
                                        edu.zju.common.CExecutor.stopProgram("Don't find filter strategy: " + filtrationStrategy);
                                }
                        }
                }
                //Effective region filter is requested.
                if (!this.filterStrategy.contains("EffectiveRegion") && GlobalParameter.getToolType().equals("gips")) {
                        this.filterStrategy.add("EffectiveRegion");
                        this.isNeedEffectiveRegionParameter = true;
                        this.isNeedEffectiveRegion = true;
                }
                edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"Filter strategy: " + this.filterStrategy);
        }

        public void setGenomeEffectiveRegion(GenomeEffectiveRegion g) {
                this.genomeEffectiveRegion = g;
        }


        public LinkedList<String> getFilterStrategy() {
                return this.filterStrategy;
        }

        boolean isNeedEffectiveRegion() {
                return isNeedEffectiveRegion;
        }


        private String getGenomeName() {
                if (this.genomeName == null) {
                        this.genomeName = GlobalParameter.getGenomeVersion();
                }
                return this.genomeName;
        }

        /**
         * return effective region in genome due to the strategies choosed before this
         * function will check whether genome has been set before
         */
        public GenomeEffectiveRegion getGenomeEffectiveRegion() {
                return this.genomeEffectiveRegion;
        }

        public boolean isNeedEffectiveRegionParameter() {
                return isNeedEffectiveRegionParameter;
        }

        public void setEffectiveRegionParameter(EffectiveRegionParameter effectiveRegionParameter) {
                this.regionParameter = effectiveRegionParameter;
        }
        public void setWindowDensity(int density) {
                this.snpDensity = density;
        }

        private int getWindowDensity() {
                return this.snpDensity;
        }

        private void setVariantControl(CommonInputFile variantControl) {
                this.variantControl = variantControl;
                this.filterStrategy.addFirst("Control");
                edu.zju.common.CExecutor.println("Control Filter: True");
        }
        public void setControlFilter(String line){
                LineHandler lineHandler=new LineHandler();
                lineHandler.splitByComma(line);
                CommonInputFile control = FileFactory.getInputFile(lineHandler.linesplit[0], "VCF");
                this.setVariantControl(control);
                if(lineHandler.linesplit.length==3){
                       edu.zju.common.CExecutor.stopProgram("-c /path/to/control.vcf");
                }
                if(lineHandler.linesplit.length==2){
                        this.setControlFilterMode(Integer.valueOf(lineHandler.linesplit[1]));
                }
        }
        private void setControlFilterMode(int mode){
                this.controlFilterMode=mode;
        }

}
