package edu.zju.genome.artificial;

import edu.zju.common.LineHandler;
import java.util.Iterator;
import java.util.Random;

/**
 *
 ** @author Zhongxu Zhu
 */
public class ArtificialReadGenerator {

       private SampleArtificialChromosome sampleArtificialChromosome;
       private String read;

       public ArtificialReadGenerator(SampleArtificialChromosome sampleArtificialChromosome, String read) {
              this.setSampleArtificialChromosome(sampleArtificialChromosome);
              this.setRead(read);
       }

       private void setSampleArtificialChromosome(SampleArtificialChromosome sampleArtificialChromosome1) {
              this.sampleArtificialChromosome = sampleArtificialChromosome1;
       }

       private void setRead(String read) {
              this.read = read;
       }

       /**
        * if the position of artificial snp is in read ,convert this read to a
        * artificial read
        *
        * @param line a sequence read line
        */
       public String getNewRead() {
              LineHandler lh = new LineHandler();
              String line = this.getOldRead();
              lh.splitByTab(line);
              try {
                     String alleleQuality=lh.linesplit[10].trim();
                     if(alleleQuality.equals("*")) return null;
              } catch (java.lang.ArrayIndexOutOfBoundsException e) {
                     return null;
              }
              
              String readName = lh.linesplit[0].substring(5);
              int readStartPosition = Integer.parseInt(lh.linesplit[3]);
              int readLength = lh.linesplit[9].length();
              char[] cigar = lh.linesplit[5].toCharArray();
              if (lh.linesplit[5].contains("X") || lh.linesplit[5].contains("=")) {
                     cigar = this.changeCIGAR(cigar);
                     lh.linesplit[5]=new String(cigar);
              }
              SampleArtificialChromosome chromosomeTemp = this.getSampleArtificialChromosome();
              int changeTimes = 0;
              for(ArtificialSNP artificialSNP:chromosomeTemp.getArtificialSNPs()){
                     int artificialSNPPositionInRead = artificialSNP.getPostion() - readStartPosition;
                     String genotype = artificialSNP.getGenotype();
                     if (artificialSNPPositionInRead < 0) {
                            continue;
                     }
                     if (artificialSNPPositionInRead >= readLength) {
                            continue;
                     }
                     StringBuffer tempStringBuffer = new StringBuffer();
                     char operator;
                     int descriptionTotalLength = 0;
                     for (int i = 0; i < cigar.length; i++) {
                            if (cigar[i] <= 57 && cigar[i] >= 48) {
                                   tempStringBuffer.append(cigar[i]);
                            } else {
                                   int descriptionLength ;
                                   try {
                                          descriptionLength= Integer.parseInt(tempStringBuffer.toString());    
                                   } catch (java.lang.NumberFormatException e) {
                                          return null;//In cigar field ,it might be '*' . Other idea about how to handle this exception can be here
                                   }
                                   tempStringBuffer = new StringBuffer();
                                   switch (operator = cigar[i]) {
                                          case 'M':
                                                 break;
                                          case 'I': {
                                                 if (descriptionTotalLength - 1 < artificialSNPPositionInRead) {
                                                        artificialSNPPositionInRead = artificialSNPPositionInRead + descriptionLength;
                                                 }
                                                 break;
                                          }
                                          case 'D': {
                                                 if (artificialSNPPositionInRead >= descriptionTotalLength - 1 && artificialSNPPositionInRead < descriptionTotalLength + descriptionLength - 1) {
                                                        artificialSNPPositionInRead = 1000;
                                                        break;
                                                 } else if (artificialSNPPositionInRead > descriptionTotalLength + descriptionLength - 1) {
                                                        artificialSNPPositionInRead = artificialSNPPositionInRead - descriptionLength;
                                                        descriptionLength = 0;
                                                 }
                                                 break;
                                          }
                                   }
                                   descriptionTotalLength = descriptionTotalLength + descriptionLength;
                            }
                     }
                     if (artificialSNPPositionInRead > readLength - 1) {
                            continue;
                     } else {
                            if (genotype.equals("0/1")) {
                                   if (!this.isChangeRead()) {
                                          changeTimes = changeTimes + 1;
                                          continue;
                                   }
                            }
                            StringBuffer temp = new StringBuffer();
                            char charTemp[] = lh.linesplit[9].toCharArray();
                            for (int i = 0; i < charTemp.length; i++) {
                                   if (i == artificialSNPPositionInRead) {
                                          temp.append(this.getArtificialMutationalAllele(charTemp[i]));
                                   } else {
                                          temp.append(charTemp[i]);
                                   }
                            }
                            lh.linesplit[9] = temp.toString();
                            changeTimes = changeTimes + 1;
                     }
              }
              if (changeTimes == 0) {
                     return null;   
                     //return line;//every read will be attached to artificial reads if return line
              }
              StringBuffer tempStringBuffer = new StringBuffer();
              for (int i = 0; i < lh.linesplit.length; i++) {
                     tempStringBuffer.append(lh.linesplit[i] + '\t');
              }
              line = tempStringBuffer.toString();
              line = line.substring(0, line.length() - 1);
              return line;
       }

       private String getOldRead() {
              return this.read;
       }

       /**
        * Because samtools is not well support X and =; If cigar has 'X' for
        * mismatch or '=' for match ,change them to 'M'
        * M means alignment match(can be a sequence match or mismatch) ,X means sequence mismatch,= means sequence match
        * @param cigar
        * @return
        */
       private char[] changeCIGAR(char cigar[]) {
              StringBuffer tempStringBuffer = new StringBuffer();
              char operator;
              cigar=new String(cigar).replace('=', 'M').replace('X', 'M').toCharArray();
              StringBuffer newCIGAR = new StringBuffer();
              int descriptionOperatorMLength = 0;
              for (int i = 0; i < cigar.length; i++) {
                     if (cigar[i] <= 57 && cigar[i] >= 48) {
                            tempStringBuffer.append(cigar[i]);
                     } else {
                            if(tempStringBuffer.length()==0)continue; 
                            int descriptionLength = Integer.parseInt(tempStringBuffer.toString());
                            tempStringBuffer = new StringBuffer();
                            switch (operator = cigar[i]) {
                                   case 'M': {
                                          descriptionOperatorMLength = descriptionOperatorMLength + descriptionLength;
                                          break;
                                   }
                                   default: {
                                          if (descriptionOperatorMLength != 0) {
                                                 newCIGAR.append(String.valueOf(descriptionOperatorMLength )+ 'M');
                                                 descriptionOperatorMLength = 0;
                                          }
                                          newCIGAR.append(String.valueOf(descriptionLength) + operator);
                                   }
                            }
                     }
              }
              if(descriptionOperatorMLength!=0) newCIGAR.append(String.valueOf(descriptionOperatorMLength)+"M");              
              return newCIGAR.toString().toCharArray();
       }

       private SampleArtificialChromosome getSampleArtificialChromosome() {
              return this.sampleArtificialChromosome;
       }

       private char getArtificialMutationalAllele(char allele) {
              switch (allele) {
                     case 'A':
                            return 'T';
                     case 'G':
                            return 'C';
                     case 'T':
                            return 'A';
                     case 'C':
                            return 'G';
                     default:
                            return 'A';
              }
       }
       /**
        * If the chromatid id of this read is not exist ,then id is randomly
        * created. if id is 0 ,that to say this read need to change one allele
        * if id is 1 ,read is not to be changed ,but the changeTime should be
        * plus 1(newRead will not null due to changeTimes is not 0).
        *
        * @return
        */
       private boolean isChangeRead() {
               Random r=new Random();
               int s=r.nextInt(2);
               if(s==0) return true;
               if(s==1) return false;
               return false;              
       }
}
