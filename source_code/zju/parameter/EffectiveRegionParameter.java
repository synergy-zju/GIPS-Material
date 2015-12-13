package edu.zju.parameter;

import edu.zju.common.LineHandler;

/**
 *
 * @author Zhongxu Zhu
 */
public class EffectiveRegionParameter {

        private int promoterLength = 100;
        private int spliceSite = 2;
        private boolean consider5UTR = false;
        private boolean consider3UTR = false;
        private boolean considerCDS = true;
        private boolean considerExon = false;
        private static String effectiveRegionPara = null;

        public EffectiveRegionParameter(String parameter)  {
                LineHandler lh = new LineHandler();
                lh.splitByVerticalLine(parameter);
                this.promoterLength = 0;
                this.spliceSite = 0;
                this.consider3UTR = false;
                this.consider5UTR = false;
                this.considerCDS = false;
                this.considerExon = false;
                try {
                        String temp;
                        for (int i = 0; i < lh.linesplit.length; i++) {
                                temp = lh.linesplit[i].toLowerCase();
                                if (temp.contains("promoter")) {
                                        this.setPromoterLength(Integer.parseInt(temp.split("=")[1]));
                                        continue;
                                } else if (temp.equals("cds")) {
                                        this.setConsiderCDS(true);
                                        continue;
                                } else if (temp.equals("exon")) {
                                        this.setConsiderExon(true);
                                        continue;
                                } else if (temp.contains("splicesite")) {
                                        try {
                                            this.setSpliceSite(Integer.parseInt(temp.split("=")[1]));
                                        } catch (Exception e) {
                                             this.setSpliceSite(2);   
                                        }
                                        continue;
                                } else if (temp.equals("5utr")) {
                                        this.setConsider5UTR(true);
                                        continue;
                                } else if (temp.equals("3utr")) {
                                        this.setConsider3UTR(true);
                                        continue;
                                } else {
                                        edu.zju.common.CExecutor.stopProgram("Don't find region: " + temp);
                                        
                                }
                        }
                } catch (ArrayIndexOutOfBoundsException e) {
                        edu.zju.common.CExecutor.stopProgram("region parameter is not set correctly or empty! ");
                } catch (NumberFormatException e1) {
                        edu.zju.common.CExecutor.stopProgram("Please input an integer, for example Promoter=150,SpliceSite=2");
                }
                this.printRegionParameter();
        }
        /**
         *
         * @param length promoter is in front of 5'utr, set the length you want
         * to choose
         */
        private void setPromoterLength(int length) {
                if (length < 0) {
                        length = 200;
                        edu.zju.common.CExecutor.println("Promoter length is not correct and has been set 200! ");
                }
                promoterLength = length;
        }

        /**
         *
         * @param length required: 0<=length<=3
         */
        private void setSpliceSite(int length) {
                if (length < 0 || length > 3) {
                        length = 2;
                        edu.zju.common.CExecutor.println("splice site is not correct and has been set 2! ");
                }
                spliceSite = length;
        }

        /**
         *
         * @param consider5UTR true or false
         */
        private void setConsider5UTR(boolean consider5UTR) {
                this.consider5UTR = consider5UTR;
        }

        /**
         * @param true or false
         */
        private void setConsider3UTR(boolean consider3UTR) {
                this.consider3UTR = consider3UTR;
        }

        /**
         * @param true or false
         */
        private void setConsiderCDS(boolean considerCDS) {
                this.considerCDS = considerCDS;
        }

        /**
         * @param true or false
         */
        private void setConsiderExon(boolean considerExon) {
                this.considerExon = considerExon;
        }

        /**
         * println parameters in a concrete instance
         */
        public void printRegionParameter() {
                edu.zju.common.CExecutor.println("\t\t   Promoter length: " + this.getPromoterLength());
                edu.zju.common.CExecutor.println("\t\t   Splice site: " + this.getSpliceSite());
                edu.zju.common.CExecutor.println("\t\t   5UTR: " + this.isConsider5UTR());
                edu.zju.common.CExecutor.println("\t\t   3UTR: " + this.isConsider3UTR());
                edu.zju.common.CExecutor.println("\t\t   CDS: " + this.isConsiderCDS());
                edu.zju.common.CExecutor.println("\t\t   Exon: " + this.isConsiderExon());
                effectiveRegionPara = "Promoter length(" + this.getPromoterLength() + "); Splice site(" + this.getSpliceSite() + "); 5'UTR(" + this.isConsider5UTR() + "); 3'UTR(" + this.isConsider3UTR() + "); EXON(" + this.isConsiderExon() + "); CDS(" + this.isConsiderCDS() + ")";
        }

        /**
         *
         * @return promoter length which has been set before
         */
        public int getPromoterLength() {
                return promoterLength;
        }

        /**
         *
         * @return the length of splice site in parameter
         */
        public int getSpliceSite() {
                return spliceSite;
        }

        /**
         *
         * @return whether 5utr has been considered
         */
        public boolean isConsider5UTR() {
                return consider5UTR;
        }

        /**
         *
         * @return whether 3utr has been considered
         */
        public boolean isConsider3UTR() {
                return consider3UTR;
        }

        /**
         *
         * @return whether cds has been considered
         */
        public boolean isConsiderCDS() {
                return considerCDS;
        }

        /**
         *
         * @return whether exon has been considered
         */
        public boolean isConsiderExon() {
                return considerExon;
        }

        public static String getEffectiveRegionParamString() {
                return effectiveRegionPara;
        }
}
