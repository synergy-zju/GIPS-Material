package edu.zju.genome.artificial;

import edu.zju.common.FileHandler;
import edu.zju.file.CommonInputFile;
import edu.zju.file.Config;
import edu.zju.file.FileFactory;
import edu.zju.genome.effectiveRegion.GenomeEffectiveRegion;
import edu.zju.parameter.SampleParameterBag;
import edu.zju.variant.SampleVariant;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zzx
 */
public class ArtificialSNPGenerator {
    private int artificialSNPNumber;
    private GenomeEffectiveRegion genomeEffectiveRegion;
    private SampleVariant sampleVariant;
    private int maxChrID;
    private int maxPosition;
    private String genetype;
    private String artificialSNPPositionFilePath = null;
    private HashMap<Integer,String> numberToChrID;
    private boolean positiveSNP;
    private boolean is2GenerateHomo=false;
    private boolean is2GenerateHeter=false;
    
    
       public ArtificialSNPGenerator(GenomeEffectiveRegion genomeEffectiveRegion1,SampleVariant sampleVariant1,int mode,int artificialSNPNumber) {
              this.setSampleVariace(sampleVariant1);
              this.setGenomeEffectiveRegion(genomeEffectiveRegion1);
              this.numberToChrID = new HashMap<>();
              this.mapChrIDToInteger(genomeEffectiveRegion1.getChromosomesID());
              this.setNumber(artificialSNPNumber);//the artificial snp artificialSNPNumber ,waiting for modification
              this.setChrNumber(genomeEffectiveRegion1.getChrNumber());
              this.setMaxPosition(genomeEffectiveRegion1.getMaxPosition());
              this.setGenotype(mode);
              //a fixed artificial snp position that would not generate a new artificial snp position file
              //this.setArtificialSNPPositionFilePath("/home/sss/NetBeansProjects/RiceGIPS/tempPosition");
       }

    private void setGenotype(int mode){
                switch (mode){
                        case 3:{
                                this.is2GenerateHeter=true;
                                this.is2GenerateHomo=true;
                                break;
                        }
                        case 2:this.is2GenerateHeter=true;break;
                        case 1:this.is2GenerateHomo=true;break;
                        case 0:break;
                }
    }
    private String getGenotype(){
           return this.genetype;
    }
    private void setChrNumber(int max){
           this.maxChrID=max;
    }
    public int getChrNumber(){
           return this.maxChrID;
    }
    private void setMaxPosition(int max){
           this.maxPosition=max;
    }
    public int getMaxPosition(){
           return this.maxPosition;
    }
    private void setNumber(int number){
           this.artificialSNPNumber=number;
    }
    private void setGenomeEffectiveRegion(GenomeEffectiveRegion genomeEffectiveRegion){
           this.genomeEffectiveRegion=genomeEffectiveRegion;
    }
    private void setSampleVariace(SampleVariant sampleVariant){
           this.sampleVariant=sampleVariant;
    }
    public void setArtificialSNPPositionFilePath(String path){
           this.artificialSNPPositionFilePath=path;
    }
    public CommonInputFile getArtificialSNPPositionFile(){
           return FileFactory.getInputFile(this.artificialSNPPositionFilePath, "POS");
    }
    private void generateArtificialSNPPositionFile(){
           edu.zju.common.CExecutor.print(edu.zju.common.CExecutor.getRunningTime()+"Start to generate position for simulated SNPs");
           this.setArtificialSNPPositionFilePath(SampleParameterBag.getIntermediateFilePath()+System.getProperty("file.separator")+this.sampleVariant.getName()+System.getProperty("file.separator")+Config.getItem("AR_VAR_POS"));
           int artificialNumber=0;
           String chr;
           int pos;
           int oneThirdNumber=this.artificialSNPNumber/3;
           HashSet<String> pool = new HashSet<>();
           FileHandler fileHandler=new FileHandler();
           fileHandler.writeFile(this.artificialSNPPositionFilePath);
           try {
                fileHandler.bw.write("Chr"+'\t'+"Pos"+'\t'+"Type\n");
                fileHandler.bw.flush();
                StringBuffer region= new StringBuffer();
                if(this.is2GenerateHomo){
                      artificialNumber=0;
                      while(artificialNumber<this.artificialSNPNumber){              
                                chr=this.randomChr();
                                pos=this.randomPos();
                                region.append(chr+"|");
                                region.append(String.valueOf(pos));
                                if(pool.contains(region.toString())) continue;
                                else pool.add(region.toString());
                                region = new StringBuffer();
                                ArtificialSNP artificialSNP;
                                if(this.isSatisfyArtificialSNP(chr, pos)){
                                       artificialNumber=artificialNumber+1;
                                       fileHandler.bw.write(chr+'\t'+pos+'\t'+"1/1"+'\n');
                                       fileHandler.bw.flush();
                                       if(artificialNumber%oneThirdNumber==0){
                                        edu.zju.common.CExecutor.print(".");
                                       }
                                }
                      } 
                      artificialNumber=0;
                }
                if(this.is2GenerateHeter){
                      artificialNumber=0;
                      while(artificialNumber<this.artificialSNPNumber){              
                                chr=this.randomChr();
                                pos=this.randomPos();
                                region.append(chr+"|");
                                region.append(String.valueOf(pos));
                                if(pool.contains(region.toString())) continue;
                                else pool.add(region.toString());
                                region = new StringBuffer();
                                ArtificialSNP artificialSNP;
                                if(this.isSatisfyArtificialSNP(chr, pos)){
                                       artificialNumber=artificialNumber+1;
                                       fileHandler.bw.write(chr+'\t'+pos+'\t'+"0/1"+'\n');
                                       fileHandler.bw.flush();
                                       if(artificialNumber%oneThirdNumber==0){
                                            edu.zju.common.CExecutor.print(".");
                                       }
                                }
                      } 
                      
                      artificialNumber=0;
                }
                fileHandler.bw.close();
                edu.zju.common.CExecutor.println("");
           } catch (IOException ex) {
                  Logger.getLogger(ArtificialSNPGenerator.class.getName()).log(Level.SEVERE, null, ex);
           }
    }
    /**
     * Generate a pos file first, then read it
     * @return 
     */
    public SampleArtificialGenome generate(){
           if(this.artificialSNPPositionFilePath==null) this.generateArtificialSNPPositionFile();
           String chr;
           int pos;
           String snpType;
           CommonInputFile posFile=this.getArtificialSNPPositionFile();
           SampleArtificialGenome sampleArtificialGenome = new SampleArtificialGenome(this.genomeEffectiveRegion,this.sampleVariant.getName());
           String line;        
           while((line=posFile.readLine())!=null){
                          if(line.contains("Pos")||line.contains("pos"))continue;                
                          chr=line.split("\t")[0].toString();
                          pos=Integer.parseInt(line.split("\t")[1]);
                          snpType=line.split("\t")[2];
                          ArtificialSNP artificialSNP;
                          artificialSNP = new ArtificialSNP(chr, pos,snpType);
                          sampleArtificialGenome.addArtificialSNP(artificialSNP);
           }
           posFile.closeInput();

            try {
                    edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+ this.getSampleVariant().getName()+": Simulated SNPs' positions have been generated  " );
            } catch (NullPointerException e) {
                    edu.zju.common.CExecutor.println(edu.zju.common.CExecutor.getRunningTime()+"Simulated SNPs' positions have been generated ");
            }
           return sampleArtificialGenome;
    }

    private String randomChr(){
           int number2ChrID= (int)(Math.random()*this.getChrNumber()+1);
           return this.numberToChrID.get(number2ChrID);
    }
    private int randomPos(){
           return (int)(Math.random()*this.getMaxPosition()+1);
    }
    /**
     * if this position meets the condition of a artificial snp ,the return the ref of this position
     */
    private boolean isSatisfyArtificialSNP(String chr,int pos){
           char nucleicAcid;
           if(!this.getSampleVariant().isSNP(chr, pos)&&this.genomeEffectiveRegion.isInEffectiveRegion(chr, pos)){
                  return true;
           }
           return false;
    }
    
    private SampleVariant getSampleVariant(){
           return this.sampleVariant;
    }
  
    private GenomeEffectiveRegion getGenomeEffectiveRegion(){
           return this.genomeEffectiveRegion;
    }
    
    private void mapChrIDToInteger(Set<String> chrNames){
            TreeSet<String> treeSet= new TreeSet<>(chrNames);
            int temp=1;
            for(String chrName: treeSet){
                    this.numberToChrID.put(temp, chrName);
                    temp=temp+1;
            }
    }
    
}
